# CampusHub 数据库初始化说明

本目录用于初始化 CampusHub 第一阶段核心业务数据库。数据库名为 `campushub`，字符集为 `utf8mb4`，排序规则为 `utf8mb4_unicode_ci`，目标版本为 MySQL 8.x。

## 文件说明

- `00_drop_tables.sql`：关闭外键检查，按依赖顺序删除已有核心业务表，最后恢复外键检查。
- `01_create_database.sql`：创建 `campushub` 数据库，若数据库已存在则跳过。
- `02_create_tables.sql`：创建核心业务表，包括用户、需求、订单、评价、消息、信用记录。
- `03_insert_test_data.sql`：插入可重复执行的测试数据，密码字段使用 `mock_hash_123456` 这类占位哈希值。
- `init_database.bat`：Windows 一键初始化脚本。
- `init_database.sh`：macOS/Linux 一键初始化脚本。

## Windows 一键初始化

在项目根目录打开命令提示符或 PowerShell，执行：

```bat
database\init_database.bat
```

脚本默认使用 MySQL 用户 `root`，数据库名 `campushub`。如需修改，请编辑 `init_database.bat` 顶部：

```bat
set MYSQL_USER=root
set DB_NAME=campushub
```

运行时会提示输入 MySQL 密码。脚本会依次执行：

1. `00_drop_tables.sql`
2. `01_create_database.sql`
3. `02_create_tables.sql`
4. `03_insert_test_data.sql`

## macOS/Linux 一键初始化

在项目根目录执行：

```bash
chmod +x database/init_database.sh
./database/init_database.sh
```

脚本默认使用 MySQL 用户 `root`，数据库名 `campushub`。如需修改，请编辑 `init_database.sh` 顶部：

```bash
MYSQL_USER="root"
DB_NAME="campushub"
```

运行时会提示输入 MySQL 密码。

## mysql 命令找不到怎么办

如果执行脚本时提示 `mysql` 不是内部或外部命令，或提示 `command not found: mysql`，说明 MySQL 命令行工具没有加入系统 `PATH`。

Windows 可以将 MySQL 安装目录下的 `bin` 目录加入环境变量，例如：

```text
C:\Program Files\MySQL\MySQL Server 8.0\bin
```

macOS/Linux 可以确认 MySQL 是否已安装，并将 `mysql` 所在目录加入 `PATH`。常见检查命令：

```bash
which mysql
mysql --version
```

## 初始化后验证

登录 MySQL：

```bash
mysql -u root -p
```

依次执行：

```sql
SHOW DATABASES;
USE campushub;
SHOW TABLES;
SELECT * FROM `user`;
```

如果能看到 `campushub` 数据库、核心业务表和测试用户数据，说明初始化成功。

## 核心表关系

- `user` 1:N `demand`：一个用户可以发布多个需求。
- `demand` 1:0..1 `task_order`：一个需求最多对应一个接单记录。
- `user` 1:N `task_order`：用户可以作为 `publisher` 发布订单，也可以作为 `receiver` 接单。
- `task_order` 1:N `review`：一个订单可以产生多条评价记录。
- `user` 1:1 `user_detail`：`user` 保存登录注册需要的账号信息，`user_detail` 保存昵称、头像、联系方式、信用分和个性资料。
- `user` 1:N `message`：用户可以作为 `sender` 发送消息，也可以作为 `receiver` 接收消息；系统消息的 `sender_uuid` 可以为空。
- `user` 1:N `credit_record`：一个用户可以有多条信用变更记录。
- `task_order` 1:N `credit_record`：一个订单可以关联多条信用变更记录。

## 后续开发建议

Java 实体类建议使用驼峰命名字段，对应数据库下划线字段。例如 `user_uuid` 对应 `userUuid`，`password_hash` 对应 `passwordHash`，`create_time` 对应 `createTime`。

MyBatis Mapper XML 放在 `backend/src/main/resources/mapper` 目录中。当前后端已配置下划线到驼峰映射，也可以在复杂查询里使用 `resultMap` 显式配置字段映射。
