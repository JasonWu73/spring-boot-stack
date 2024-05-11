# Spring Boot 项目

## MyBatis Mapper 方法命名

- 方法名前缀
    - `select*`：查询数据
        - `count*`：查询数据总数
        - `exists*`：查询数据是否存在
            - `*Exists`：变量名后缀
    - `insert*`：插入数据
    - `update*`：更新数据
    - `delete*`：删除数据
- 方法名后缀
    - `By*And*OrderBy*`
    - `By*Or*OrderBy*`

## RESTful API 设计

### Controller 方法命名

- GET 方法用于获取资源：`get*`
    - `getUsers`：获取用户列表
- POST 方法用于新建资源：`add*`
    - `addUser`：创建用户
- PUT 方法用于完整更新资源：`update*`
    - `updateUser`：更新用户的全部属性
- PATCH 方法用于更新资源的部分属性：`patch*`
    - `patchUser`：更新用户的部分属性
- DELETE 方法用于删除资源：`delete*`
    - `deleteUser`：删除用户

### URL 映射

对于 URL 映射，通常采用复数形式的资源名称，例如:

- 获取单个资源：`GET /users/{id}`
- 获取资源列表：`GET /users`
- 新增资源：`POST /users`
- 更新资源
    - 更新资源的全部属性：`PUT /users/{id}`
    - 更新资源的部分属性：`PATCH /users/{id}`
- 删除资源：`DELETE /users/{id}`
- 其他最佳实践
    - 使用版本化的 URL，如 `/v1/users`
    - URL 中只使用小写字母和横线（不使用下划线）
    - URL 中不要包含文件扩展名
    - 使用 Query String 参数过滤、排序等操作，如 `/users?role=admin&sortBy=name`

## 目录结构

```text
spring-boot-project
├── src
│   ├── main
│   │   ├── java
│   │   │   └── net
│   │   │       └── wuxianjie
│   │   │           ├── shared
│   │   │           ├── biz
│   │   │           │   ├── XxxController
│   │   │           │   ├── XxxService
│   │   │           │   ├── XxxMapper
│   │   │           │   └── dto
│   │   │           └── XxxApp.java
│   │   └── resources
│   │       ├── mapper
│   │       │   └── XxxMapper
│   │       ├── static
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test
│       └── java
│           └── net
│               └── wuxianjie
└── README.md
```

## Gradle

- `./gradlew clean`：清理构建目录
- `./gradlew build`：构建项目
- `./gradlew publishToMavenLocal`：将工件发布到本地 Maven 仓库
- `./gradlew nativeCompile`：生成本地可执行文件

## Maven

- `mvn clean`：清理构建目录
- `mvn package`：构建项目
- `mvn install`：将工件发布到本地 Maven 仓库
- 在 Maven 多模块项目中，生成本地可执行文件必须分以下两步执行：
    1. 先在父 POM 目录执行 `mvn install`
    2. 再进入目标模块的 POM 目录执行 `mvn native:compile -Pnative`
