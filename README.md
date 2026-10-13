# Spring Boot Admin Template

> 生产级 Spring Boot 后台管理后端模板 —— 认证、权限（RBAC）、通用 CRUD、Docker 一键部署，开箱即用。

> 注意：本文档为中文大纲版，第 6-7 周将翻译为英文 README（面向海外买家）。

## 功能特性（Features）

- [x] 健康检查接口（骨架已完成）
- [ ] 用户注册 / 登录 / JWT 鉴权（第 3 周）
- [ ] 用户 / 角色 / 权限管理（RBAC，第 4 周）
- [ ] 通用 CRUD 示例（第 5 周）
- [ ] Swagger 接口文档（骨架已完成）
- [ ] Docker 一键部署（第 5 周）

## 技术栈（Tech Stack）

- Spring Boot 3.3 + Spring Security + JWT
- MyBatis-Plus
- MySQL 8 + Redis 7
- Swagger / OpenAPI（springdoc）
- Docker / docker-compose
- JDK 17

## 快速开始（Quick Start）

### 环境要求
- JDK 17+
- Maven 3.6+
- Docker（可选，用于起 MySQL / Redis）

### 启动步骤

```bash
# 1. 启动 MySQL + Redis（第 3 周做认证时需要；骨架阶段可跳过）
docker-compose up -d

# 2. 启动应用
mvn spring-boot:run

# 3. 验证
curl http://localhost:8080/api/health
```

- 健康检查：`http://localhost:8080/api/health`
- Swagger 文档：`http://localhost:8080/swagger-ui.html`

## 项目结构（Project Structure）

```
src/main/java/com/dongh/admin/
├── AdminApplication.java      # 启动类
├── controller/                # 控制器
├── service/                   # 业务逻辑（待补充）
├── mapper/                    # MyBatis-Plus Mapper（待补充）
├── entity/                    # 实体类（待补充）
├── config/                    # 配置类（Security / Redis / Swagger）
├── security/                  # JWT 相关（待补充）
└── common/                    # 统一返回 / 异常处理（待补充）
```

## API 文档（API Docs）

- Swagger UI：`http://localhost:8080/swagger-ui.html`

## 开发路线（Roadmap）

- [ ] 第 3 周：认证模块（登录 / JWT）
- [ ] 第 4 周：RBAC 权限模块
- [ ] 第 5 周：通用 CRUD + Docker 完整部署
- [ ] 第 6 周：英文 README + 演示 GIF

## 许可证（License）

MIT（待定）
