# rbac-user-service

Spring Boot 后端示例项目：用户管理 + JWT 登录 + RBAC 权限控制。

## 技术栈

| 类别 | 选型 |
|---|---|
| 语言 / 框架 | Java 21、Spring Boot 3.5 |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8 |
| 认证 | JWT（jjwt 0.12），权限基于 RBAC（用户-角色-权限） |
| 密码 | BCrypt（spring-security-crypto） |
| 样板代码 | Lombok（数据类统一 `@Getter` / `@Setter`） |

## 功能

- **登录**：账号密码校验（BCrypt），签发携带权限列表的 JWT；账号或密码错误返回真实的 HTTP `401`
- **用户管理**：新增 / 修改 / 删除 / 分页条件查询
- **接口权限**：Controller 方法上用 `@RequiresPermission("user:add")` 声明所需权限，`JwtInterceptor` 统一校验
  - 未登录 → `401`
  - 权限不足 → `403`
- **环境隔离**：建表、种子数据、SQL 日志都收在 dev profile（`application-dev.properties`），生产环境设 `SPRING_PROFILES_ACTIVE=prod` 即全部关闭
- **安全细节**：修改接口不允许改密码；查询接口不返回密码（密码字段声明为 Jackson 只写属性，入参能收到、响应不输出）；登录失败统一提示"用户名或密码错误"，不区分"账号不存在"与"密码错误"；新增用户时先查重，账号唯一性由应用层查重保证，数据库另有 `uk_user_username` 唯一索引兜底
- **入参契约**：新增 / 修改各用独立 DTO（`UserAddDTO` / `UserUpdateDTO`），`User` 实体只负责持久化与响应，不直接接收入参，避免实体字段变动意外改掉接口
- **仅本机监听**：`server.address=127.0.0.1`，接口默认不暴露给局域网/公网；需要同网段访问时改成 `0.0.0.0`，但注意种子账号密码是公开的

## 快速开始

### 1. 准备数据库

MySQL 8 中创建库（默认 dev profile 下首次启动会自动建表并写入初始数据，脚本幂等可重复执行）：

```sql
CREATE DATABASE demo DEFAULT CHARACTER SET utf8mb4;
```

> **如果本地已有旧版本的表结构**：`schema.sql` 用的是 `CREATE TABLE IF NOT EXISTS`，表已存在时整条建表语句（含唯一索引）不会执行，所以老库不会自动补上 `uk_user_username` 唯一索引。此时 `username` 可能重复，登录用的 `selectOne` 会因匹配到多行而报错。
>
> 两种处理方式，任选一种：
>
> **方式一（推荐，非破坏性）**：给老库补上唯一索引。若已有重复账号，先查出重复项、给多余的行让出账号名（保留数据，不删除），再建索引：
>
> ```sql
> -- 1. 查出重复的账号
> SELECT username, COUNT(*) FROM `user`
> WHERE username IS NOT NULL GROUP BY username HAVING COUNT(*) > 1;
>
> -- 2. 给多余的行改个名，让出原账号（这里保留 id 较小的行，按实际情况调整）
> UPDATE `user` SET username = CONCAT(username, '_legacy') WHERE id = 1 AND username = 'admin';
>
> -- 3. 补上 schema.sql 声明但老表缺失的唯一索引
> ALTER TABLE `user` ADD UNIQUE KEY `uk_user_username` (`username`);
> ```
>
> 建索引后 `username` 唯一，登录才能确定地匹配到一行。注意建表语句里 `name`/`username`/`password` 是 `NOT NULL`，老库这几列仍是可空的，想让结构完全一致就走方式二。
>
> **方式二（彻底重建，会清空数据）**：`DROP DATABASE demo` 后重新创建，让 `schema.sql` 完整跑一遍。

### 2. 配置环境变量

敏感信息不写入代码库。缺失时启动会直接失败，并打印一段可读的说明（`APPLICATION FAILED TO START`），不会带着空配置跑起来：

| 变量 | 必填 | 说明 |
|---|---|---|
| `DB_PASSWORD` | 是 | MySQL 密码（缺失时提示"数据库密码未配置"） |
| `JWT_SECRET` | 是 | JWT 签名密钥，**至少32个字符** |
| `DB_URL` | 否 | 连接串，默认本机 `demo` 库 |
| `DB_USERNAME` | 否 | MySQL 用户，默认 `root` |

三种配置方式，任选一种（优先级：环境变量 > 本地配置文件 > `application.properties`）：

**方式一（推荐，最省事）：写进不进 git 的本地配置文件**

`application.properties` 已经配好 `spring.config.import=optional:file:./application-local.properties`，你只要在项目根目录建一个 `application-local.properties`（该文件已在 `.gitignore` 中）：

```properties
spring.datasource.password=你的MySQL密码
jwt.secret=你的至少32位密钥
```

IDEA 和命令行启动都不用再配环境变量。

**方式二：IDEA 运行配置**

Run → Edit Configurations → 选中启动类 → Environment variables：

```
DB_PASSWORD=你的MySQL密码;JWT_SECRET=你的至少32位密钥
```

多个变量用分号分隔。⚠️ 不要勾 "Store as project file"，那样会写到 `.run/*.run.xml`，而 `.gitignore` 里**没有** `.run` 目录，密码会被提交上去。

**方式三：命令行导出环境变量**

```bash
# Windows PowerShell
$env:DB_PASSWORD="你的密码"
$env:JWT_SECRET="dev-secret-please-change-to-32-chars"
./mvnw spring-boot:run

# Git Bash / Linux / macOS
export DB_PASSWORD=你的密码
export JWT_SECRET="dev-secret-please-change-to-32-chars"
./mvnw spring-boot:run
```

> 设置系统级环境变量（`setx`）时注意：**IDEA 只在启动时读一次环境变量**，设完要重启 IDEA 才生效。

> **监听地址与 profile（默认无需配置）**：`spring.profiles.default=dev` 让本地启动默认走 dev，建表、种子数据、SQL 日志都在这个 profile 里；生产环境设 `SPRING_PROFILES_ACTIVE=prod` 即可全部关闭。`server.address=127.0.0.1` 让接口只监听本机，需要同网段其他设备访问时改成 `0.0.0.0`。

### 3. 初始账号（默认 dev profile 下首次启动自动创建，密码均为 `123456`）

| 账号 | 角色 | 权限 |
|---|---|---|
| `admin` | 管理员 | 查询 / 新增 / 修改 / 删除 |
| `user` | 普通用户 | 仅查询 |

### 4. 验证

```bash
# 1. 登录拿Token
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 2. 带Token分页查询用户
curl "http://localhost:8080/page?page=1&size=10" -H "Authorization: Bearer <token>"

# 3. 换成 user 账号的Token调用删除，会得到 403
curl -X DELETE "http://localhost:8080/delete?id=1" -H "Authorization: Bearer <user的token>"
```

## 接口一览

| 方法 | 路径 | 所需权限 | 说明 |
|---|---|---|---|
| POST | `/login` | 无 | 登录，返回 Token + 用户信息 + 权限列表；失败返回 `401` |
| GET | `/user/info` | 登录即可 | 当前用户信息 |
| GET | `/page` | `user:list` | 分页 + 姓名/年龄条件查询（`size` 上限 100） |
| GET | `/find` | `user:list` | 按ID查询 |
| POST | `/add` | `user:add` | 新增（入参 `UserAddDTO`，密码 BCrypt 加密入库） |
| PUT | `/update` | `user:update` | 修改（入参 `UserUpdateDTO`，不能改密码） |
| DELETE | `/delete` | `user:delete` | 删除 |
| GET | `/actuator/health` | 无 | 健康检查 |

列表查询只有 `/page` 一个入口，并且强制分页上限，不存在返回全表的接口。

统一返回格式：`{"code":200, "message":"操作成功", "data":...}`。**所有错误都使用真实的 HTTP 状态码**（`400` 参数或请求体不合法、`401` 未登录或登录失败、`403` 权限不足、`404` 路径或资源不存在、`409` 数据冲突、`500` 服务端异常），响应体格式不变，其中 `code` 与状态码一致。例如访问不存在的路径返回 `404`，查询不存在的用户返回 `404`，新增已存在的账号返回 `409`。

## 运行测试

```bash
./mvnw test
```

两类测试，都不需要本机 MySQL：

- **单元测试**：Service 层用 Mockito Mock 掉 Mapper；认证流程用 standalone MockMvc 验证 401/403/权限校验
- **集成测试**（`ApplicationIntegrationTest`）：`@SpringBootTest` 启动真实上下文，跑在 H2 内存库上（`test` profile），会真的执行建表与种子数据、真的走一遍登录与鉴权

集成测试的价值在于覆盖单元测试看不到的部分：配置绑定与启动期校验、profile 装配、拦截器注册、`schema-h2.sql` / `data-h2.sql` 能否执行成功、以及种子密文能否匹配 `123456`。其中 `测试使用H2内存库` 会断言数据源确实是 H2，防止测试意外连到你本机的 MySQL。

> H2 脚本是 `src/test/resources/schema-h2.sql` 和 `data-h2.sql`，与生产脚本结构一致但去掉了 MySQL 专有子句。它们**刻意不叫** `schema.sql` / `data.sql`：Spring 默认按 `classpath*:schema.sql` 发现脚本，那样会同时匹配到 `main` 和 `test` 下的两份，建表与种子数据各跑两次，种子数据第二次会主键冲突。

## 安全说明与已知限制

- JWT 无状态、无法服务端吊销，登出靠前端删除 Token；如需强制下线要引入 Redis 黑名单
- 数据类统一用 Lombok 的 `@Getter` / `@Setter`，刻意不用 `@Data`：`User`、`LoginDTO`、`UserAddDTO` 三个类带密码字段，`@Data` 生成的 `toString` 一旦被日志打印就会把明文或密文密码带出去；同时 `@Data` 生成的全字段 `equals` 对实体也没有意义
- Token 中的权限在登录时固化，后台改权限后需重新登录才生效；Token 有效期内即使账号被删或权限被撤，`@RequiresPermission` 的校验仍会通过（只有 `/user/info` 因为会查库才发现账号已不存在）
- 所有错误都使用真实的 HTTP 状态码，`code` 与状态码一致，响应体仍是统一的 `{"code":...,"message":...,"data":...}`：
  - `400` 参数校验失败、请求体不是合法 JSON、业务规则不满足（业务异常的默认值）
  - `401` 登录失败、未登录、Token 无效
  - `403` 权限不足
  - `404` 路径不存在，或按 ID 查询的资源不存在
  - `409` 与已有数据冲突，如新增时登录账号已存在
  - `500` 服务端异常，如库里存在重复账号
  - 语义更明确时业务异常可以自己携带状态码：`throw new BusinessException(HttpStatus.CONFLICT, "登录账号已存在")`。代价是 Service 层引用了 `HttpStatus`，如果将来要支持非 HTTP 入口（消息队列、定时任务），需要把状态码决定上移到 Controller
- 建表、种子数据、SQL 日志都收在 dev profile（`application-dev.properties`）。**生产环境绝不要启用 dev profile**：`data.sql` 会在真实库里插入 `admin/123456`、`user/123456` 这两个已知密码的账号，SQL 日志也会把账号与密文打到控制台
- `application-local.properties` 保存本地开发用的明文密码/密钥，已在 `.gitignore` 中忽略；生产环境应改用密钥管理服务或 CI 注入
- 数据库密码只存在于本地 gitignore 文件或环境变量中，代码与配置模板里都是占位符。但**有一个密码必须视为已公开**：它曾以明文写在 `application.properties` 里并被推送到了公开仓库（该仓库已删除，本仓库历史也已重写，因此当前提交里搜不到它，但内容已经外泄过，不能当作秘密）
  - 该密码目前**无法从远程利用**，因为它所在的 MySQL 账号是 `root@localhost`，不存在 `root@'%'`，从非本机身份连接会在校验密码之前就被拒绝（`ERROR 1130 Host ... is not allowed`）
  - **这道 host 限制是它唯一的防线，请勿破坏**：不要执行 `CREATE USER 'root'@'%'`、也不要把现有账号的 host 改成 `%`。一旦改成 `%`，这个已公开的密码立刻就能从任何能访问到 3306 的机器登录
  - 要彻底消除隐患，需在 MySQL 侧改掉该账号的密码（改完同步更新 `application-local.properties`）；当前选择不改，因此上面两条前提必须一直保持
- MySQL 建议只监听本机：在 `my.ini` 的 `[mysqld]` 段加 `bind-address=127.0.0.1` 和 `mysqlx-bind-address=127.0.0.1`，避免 3306/33060 暴露到局域网或公网（端口暴露本身对 MySQL 的预认证漏洞就是风险，与密码是否泄露无关）。改完需重启 MySQL 服务生效
