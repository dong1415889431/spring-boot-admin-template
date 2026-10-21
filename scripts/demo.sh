#!/usr/bin/env bash
#
# Live demo script — walks through the whole flow with readable, paced output.
# Designed to be recorded as a terminal screencast:
#
#   asciinema rec demo.cast -c "bash scripts/demo.sh"
#
# Usage:
#   docker compose up -d --build
#   BASE_URL=http://localhost:8080 bash scripts/demo.sh
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"

GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m'

say()  { printf "${GREEN}%s${NC}\n" "$1"; }
step() { printf "\n${CYAN}━━━ %s ━━━${NC}\n\n" "$1"; }
pause() { sleep 1.5; }

echo ""
echo "  Spring Boot Admin Template — live demo"
echo ""

step "1. Health check"
curl -s "$BASE_URL/api/health" | jq .
pause

step "2. Login as admin (admin / admin123)"
TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}' | jq -r '.data.accessToken')
say "access token: ${TOKEN:0:40}..."
pause

step "3. List seeded articles"
curl -s "$BASE_URL/api/articles?page=1&size=10" \
    -H "Authorization: Bearer $TOKEN" | jq .
pause

step "4. Create an article"
NEW_ID=$(curl -s -X POST "$BASE_URL/api/articles" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"title":"Hello from the demo","content":"Created live during the screencast.","author":"admin","status":1}' \
    | jq -r '.data.id')
say "created article id=$NEW_ID"
pause

step "5. Update the article"
curl -s -X PUT "$BASE_URL/api/articles/$NEW_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"title":"Hello (updated)","content":"Updated live.","author":"admin","status":1}' | jq .
pause

step "6. Delete the article"
curl -s -X DELETE "$BASE_URL/api/articles/$NEW_ID" \
    -H "Authorization: Bearer $TOKEN" | jq .
pause

step "7. No token -> 401"
curl -s -o /dev/null -w "HTTP %{http_code}\n" "$BASE_URL/api/articles"
pause

step "8. Permission control -> 403 (unprivileged user)"
REG_TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"demo_$(date +%s)\",\"password\":\"demo123456\"}" | jq -r '.data.accessToken')
curl -s -o /dev/null -w "HTTP %{http_code}\n" "$BASE_URL/api/articles" \
    -H "Authorization: Bearer $REG_TOKEN"
pause

step "9. Logout"
curl -s -X POST "$BASE_URL/api/auth/logout" \
    -H "Authorization: Bearer $TOKEN" | jq .
echo ""
say "Done. Swagger UI: $BASE_URL/swagger-ui.html"
