# CampusHub 校园互助平台

SEC-II-2026 大作业项目。CampusHub 面向校园互助场景，当前版本包含用户注册登录、个人资料、需求发布/浏览/详情/接单、我的需求、会话消息和基础实时聊天能力。

## 技术栈

| 模块 | 技术 |
|------|------|
| 前端 | Vue 3、Vite、TypeScript、Pinia、Vue Router、Axios、Element Plus |
| 后端 | Java 17、Spring Boot 3.3、Maven、MyBatis |
| 数据库 | MySQL 8.x |
| CI/CD | GitLab CI |

## 目录结构

```text
backend/        Spring Boot 后端
frontend/       Vue 3 前端
database/       MySQL 初始化脚本和测试数据
docs/           各阶段交付文档
.gitlab/        GitLab CI 子流水线配置
start-dev.bat   Windows 本地一键启动脚本
```

## 环境准备

请先安装：

- JDK 17 或更高版本
- Maven 3.8+
- Node.js 20+
- MySQL 8.x

后端默认端口：`8080`  
前端默认端口：`5173`  
后端 API 地址：`http://localhost:8080/api`

## 配置环境变量

复制示例配置：

```bat
copy .env.example .env
```

然后编辑 `.env`：

```env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=campushub
MYSQL_USER=root
MYSQL_PASSWORD=your_mysql_password
```

`.env` 只用于本地运行，不应提交到 Git。

## 初始化数据库

确保 MySQL 服务已启动，然后在项目根目录执行：

```bat
database\init_database.bat
```

脚本会依次执行：

1. 删除旧表
2. 创建 `campushub` 数据库
3. 创建用户、需求、详情、订单、评价、消息等核心表
4. 插入测试数据

初始化后可用以下 SQL 检查：

```sql
USE campushub;
SHOW TABLES;
SELECT * FROM `user`;
```

更多说明见 [database/README.md](database/README.md)。

## 一键启动

Windows 下可在项目根目录执行：

```bat
start-dev.bat
```

脚本会读取 `.env`，并分别在两个窗口中启动：

- 后端：`http://localhost:8080`
- 前端：`http://localhost:5173`

首次启动前请先完成数据库初始化。

## 手动启动

启动后端：

```bash
cd backend
mvn spring-boot:run
```

启动前端：

```bash
cd frontend
npm install
npm run dev
```

浏览器访问：

```text
http://localhost:5173
```

## 测试与构建

后端单元测试：

```bash
cd backend
mvn test
```

后端打包：

```bash
cd backend
mvn clean package -DskipTests
```

前端依赖安装：

```bash
cd frontend
npm install
```

前端静态检查：

```bash
cd frontend
npx eslint . --ext .vue,.ts
```

前端生产构建：

```bash
cd frontend
npm run build
```

当前本地验证记录：

- 后端 `mvn test` 通过，70 个测试全部成功。
- 后端 `mvn clean package -DskipTests` 通过。
- 前端 `npm run build` 通过。
- 前端 `npx eslint . --ext .vue,.ts` 可执行，但现有文件会输出较多 Prettier 换行符警告。

## CI/CD 说明

项目使用 GitLab CI：

- [.gitlab-ci.yml](.gitlab-ci.yml)：根据变更触发前后端子流水线。
- [.gitlab/backend.yml](.gitlab/backend.yml)：执行后端测试与打包。
- [.gitlab/frontend.yml](.gitlab/frontend.yml)：执行前端 lint 与 build。

流水线设计目标：

| 阶段 | 内容 |
|------|------|
| 后端测试 | `mvn test` |
| 后端构建 | `mvn clean package -DskipTests` |
| 前端检查 | `npx eslint . --ext .vue,.ts` |
| 前端构建 | `npm run build` |

如果 GitLab 上出现 `system failure`，通常不是项目代码错误，而是 Runner 环境问题，例如 Runner 离线、Docker 镜像无法拉取、Docker 服务异常或没有可用 Runner。

## 常见问题

### 1. 后端启动时报数据库连接失败

请检查：

- MySQL 服务是否启动
- `.env` 中 `MYSQL_PASSWORD` 是否正确
- 是否已执行 `database\init_database.bat`

### 2. 端口被占用

后端默认使用 `8080`，前端默认使用 `5173`。可先关闭占用端口的进程，再重新运行 `start-dev.bat`。

### 3. Maven 依赖下载失败

通常是网络或 Maven 镜像源问题。可以尝试：

```bash
cd backend
mvn -U clean package
```

必要时在本机 Maven `settings.xml` 中配置可用镜像源。

### 4. 前端依赖安装失败

可删除 `frontend/node_modules` 后重新安装：

```bash
cd frontend
npm install
```

## 交付文档

P4 编码阶段文档位于 [docs/p4](docs/p4)，包括 Sprint 合并任务看板、Bug 修复日志等内容。
