#!/usr/bin/env bash
#
# End-to-end smoke test for the Spring Boot Admin Template.
#
# Runs the full regression flow against a running instance:
#   health -> register -> login -> unauthorized(401) -> article CRUD -> permission(403) -> logout
#
# Usage:
#   docker compose up -d --build
#   BASE_URL=http://localhost:8080 bash scripts/smoke-test.sh
#
# Requirements: curl, jq
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_USER="admin"
ADMIN_PASS="admin123"
TEST_USER="smoke_$(date +%s)"
TEST_PASS="test123456"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
PASS=0
FAIL=0

ok()   { PASS=$((PASS + 1)); printf "${GREEN}  PASS${NC}  %s\n" "$1"; }
fail() { FAIL=$((FAIL + 1)); printf "${RED}  FAIL${NC}  %s\n" "$1"; }

if ! command -v jq >/dev/null 2>&1; then
    printf "${RED}Error:${NC} jq is required. Install it with: brew install jq\n" >&2
    exit 2
fi

HTTP_CODE=""
HTTP_BODY=""

# req METHOD PATH [JSON_BODY] [TOKEN]
# stores the HTTP status code in HTTP_CODE and the response body in HTTP_BODY
req() {
    local method="$1" path="$2" body="${3:-}" token="${4:-}"
    local args=(-s -X "$method" "$BASE_URL$path" -o /tmp/smoke_body.$$ -w "%{http_code}")
    [ -n "$body" ]  && args+=(-H "Content-Type: application/json" -d "$body")
    [ -n "$token" ] && args+=(-H "Authorization: Bearer $token")
    HTTP_CODE=$(curl "${args[@]}")
    HTTP_BODY=$(cat /tmp/smoke_body.$$ 2>/dev/null)
    rm -f /tmp/smoke_body.$$
}

# jget JQ_EXPR -> extract a field from HTTP_BODY
jget() { jq -r "$1" <<<"$HTTP_BODY"; }

# assert_code EXPECTED HTTP_CODE DESCRIPTION
assert_code() {
    if [ "$2" = "$1" ]; then ok "$3"; else fail "$3 (expected HTTP $1, got $2)"; fi
}

# assert_ok DESCRIPTION -> business code == 0
assert_ok() {
    if [ "$(jget '.code')" = "0" ]; then ok "$1"; else fail "$1 (code=$(jget '.code'), message=$(jget '.message'))"; fi
}

echo ""
echo "=================================================="
echo "  Spring Boot Admin Template — Smoke Test"
echo "  target: $BASE_URL"
echo "=================================================="
echo ""

# 1. Health check
req GET /api/health
assert_code 200 "$HTTP_CODE" "GET /api/health returns 200"
if [ "$(jget '.status')" = "UP" ]; then ok "health status is UP"; else fail "health status is UP (got $(jget '.status'))"; fi

# 2. Register a new user
REG_BODY=$(jq -n --arg u "$TEST_USER" --arg p "$TEST_PASS" \
    '{username: $u, password: $p, nickname: "Smoke Test", email: ($u + "@example.com")}')
req POST /api/auth/register "$REG_BODY"
assert_ok "POST /api/auth/register creates user $TEST_USER"

# 3. Login as admin
LOGIN_BODY=$(jq -n --arg u "$ADMIN_USER" --arg p "$ADMIN_PASS" '{username: $u, password: $p}')
req POST /api/auth/login "$LOGIN_BODY"
assert_ok "POST /api/auth/login as admin"
ADMIN_TOKEN=$(jget '.data.accessToken')
if [ -n "$ADMIN_TOKEN" ] && [ "$ADMIN_TOKEN" != "null" ]; then ok "admin access token issued"; else fail "admin access token issued"; fi

# 4. Login as the freshly registered (unprivileged) user
LOGIN_BODY=$(jq -n --arg u "$TEST_USER" --arg p "$TEST_PASS" '{username: $u, password: $p}')
req POST /api/auth/login "$LOGIN_BODY"
assert_ok "POST /api/auth/login as $TEST_USER"
USER_TOKEN=$(jget '.data.accessToken')
if [ -n "$USER_TOKEN" ] && [ "$USER_TOKEN" != "null" ]; then ok "user access token issued"; else fail "user access token issued"; fi

# 5. No token -> 401
req GET /api/articles
assert_code 401 "$HTTP_CODE" "GET /api/articles without token returns 401"

# 6. List articles (admin)
req GET "/api/articles?page=1&size=10" "" "$ADMIN_TOKEN"
assert_ok "GET /api/articles lists articles"
echo "        (seeded articles: $(jget '.data.total'))"

# 7. Create article (admin)
ARTICLE_BODY=$(jq -n '{title: "Smoke Test Article", content: "created by smoke-test.sh", author: "admin", status: 1}')
req POST /api/articles "$ARTICLE_BODY" "$ADMIN_TOKEN"
assert_ok "POST /api/articles creates article"
ARTICLE_ID=$(jget '.data.id')

# 8. Update article (admin)
ARTICLE_BODY=$(jq -n '{title: "Smoke Test Article (updated)", content: "updated", author: "admin", status: 1}')
req PUT "/api/articles/$ARTICLE_ID" "$ARTICLE_BODY" "$ADMIN_TOKEN"
assert_ok "PUT /api/articles/$ARTICLE_ID updates article"

# 9. Article detail (admin)
req GET "/api/articles/$ARTICLE_ID" "" "$ADMIN_TOKEN"
assert_ok "GET /api/articles/$ARTICLE_ID returns detail"

# 10. Delete article (admin)
req DELETE "/api/articles/$ARTICLE_ID" "" "$ADMIN_TOKEN"
assert_ok "DELETE /api/articles/$ARTICLE_ID deletes article"

# 11. Permission control -> 403 (unprivileged user has no article:* permission)
req GET "/api/articles?page=1&size=10" "" "$USER_TOKEN"
assert_code 403 "$HTTP_CODE" "unprivileged user is denied (403) on /api/articles"

# 12. Logout (admin)
req POST /api/auth/logout "" "$ADMIN_TOKEN"
assert_ok "POST /api/auth/logout clears login state"

echo ""
echo "=================================================="
printf "  Result: ${GREEN}%d passed${NC}, ${RED}%d failed${NC}\n" "$PASS" "$FAIL"
echo "=================================================="
echo ""

[ "$FAIL" -eq 0 ]
