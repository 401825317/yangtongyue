# 成长时光轴 (yangtongyue)

记录宝贝成长的家庭相册：按拍摄时间排列的时光轴（自动显示宝贝当时的年龄）、相册分组、爸妈留言、给亲友的只读分享链接。

- 后端：Spring Boot 3 / Java 17，MySQL，腾讯云 COS（或本地磁盘）
- 前端：Vue 3 + Vite（`frontend/`），打包进同一个 jar

## 本地开发

```bash
# 后端：H2 数据库 + 本地文件存储，默认账号 admin / admin123
mvn spring-boot:run -DskipFrontend=true -Dspring-boot.run.profiles=dev

# 前端（另一个终端），打开 http://localhost:5173
cd frontend && npm install && npm run dev
```

## 构建与部署

```bash
mvn clean package          # 会自动安装 Node 并构建前端
java -jar target/yangtongyue.jar
```

或使用 Docker：`docker build -t yangtongyue . && docker run -p 8082:8082 --env-file .env yangtongyue`

### 环境变量

| 变量 | 说明 | 默认值 |
|---|---|---|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | MySQL 连接 | `jdbc:mysql://localhost:3306/yangtongyue` / `root` / 空 |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | 首次启动（无用户时）创建的管理员；不设密码则随机生成并打印在日志里 | `admin` / 随机 |
| `BABY_NAME` / `BABY_BIRTHDAY` | 宝贝名字、生日（`YYYY-MM-DD`，用于计算年龄） | `Yang Tong Yue` / 空 |
| `STORAGE_TYPE` | `cos` 或 `local` | `cos` |
| `COS_SECRET_ID` / `COS_SECRET_KEY` | COS 密钥；未设置时回退读取旧的 `config` 表 | 空 |
| `COS_REGION` / `COS_BUCKET` | COS 地域、存储桶 | `ap-shanghai` / `yangtongyue-1252240258` |
| `COS_PUBLIC_BASE_URL` | 自定义 CDN 域名（可选） | 存储桶默认域名 |
| `STORAGE_LOCAL_DIR` | `local` 存储目录 | `./data/files` |
| `PORT` | 端口 | `8082` |

旧版 `image` 表会被直接沿用（启动时自动补充新列），已有照片无需迁移。
