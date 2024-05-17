# Java 项目规范

## import 语句的组织和排序

> 在 Java 中，虽然`import *`和`import static`都有其优点，但为了代码的可读性和维护性，通常都不建议使用他们。

将`import`语句按开源依赖分组，并在组之间使用空行分隔，可以提高代码的可读性和维护性。分组方式包括：

1. **标准库导入**：如`java.*`、`javax.*`、`jakarta.*`
2. **第三方库导入**：如`com.*`、`org.*`等（组内排序由 IDEA 自动判别，一般是以字母顺序排序）
3. **项目内部包导入**：自定义包
4. **`import static`**：如果一定要使用，则它一定放在最后

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

## Controller 方法命名

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

## URL 映射

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

## 常用状态码

### 1xx：信息性状态码

这类状态码用于协议握手阶段的临时应答，通常不直接关联到错误处理。

### 2xx：成功状态码

- **200 OK**：请求成功，响应体中包含请求的数据
- **201 Created**：请求成功并且服务器创建了新的资源，常用于 POST 请求
- **202 Accepted**：服务器已接受请求，但尚未处理完成，常用于需要长时间处理的请求
- **204 No Content**：服务器成功处理了请求，但不需要返回任何实体内容，常用于成功更新数据库记录或删除资源

### 3xx：重定向状态码

这类状态码通常不直接用于错误处理，而是指示客户端需要进行额外操作以完成请求。

### 4xx：客户端错误状态码

- **400 Bad Request**：服务器无法理解请求的格式，客户端不应该重复提交这个请求
- **401 Unauthorized**：请求未包含有效的认证信息，例如未提供认证令牌或令牌过期
- **403 Forbidden**：客户端没有权限执行此操作，即使提供了认证令牌
- **404 Not Found**：服务器找不到请求的资源，例如请求了不存在的页面或资源
- **405 Method Not Allowed**：客户端请求中指定的方法不被允许
- **415 Unsupported Media Type**：客户端请求中指定的媒体类型不被支持
- **429 Too Many Requests**：客户端的请求次数超过限制，通常用于控制访问频率

### 5xx：服务器错误状态码

- **500 Internal Server Error**：服务器内部错误，无法完成请求
- **503 Service Unavailable**：服务器暂时不可用，通常是由于过载或维护

## 目录结构

```text
spring-boot-stack
├── buildSrc
│   ├── src
│   │   └── main
│   │       └── kotlin
│   │           ├── app-conventions.gradle.kts
│   │           └── java-conventions.gradle.kts
│   └── build.gradle.kts
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── my-spring-boot-starter
│   │── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── net
│   │   │   │       └── wuxianjie
│   │   │   │           └── myspringbootstarter
│   │   │   └── resources
│   │   │       ├── META-INF
│   │   │       │   └── spring
│   │   │       │       └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│   │   │       └── logback-spring.xml
│   │   └── test
│   │       ├── java
│   │       │   └── net
│   │       │       └── wuxianjie
│   │       │           └── myspringbootstarter
│   │       └── resources
│   │           ├── application-test.yaml
│   │           └── static
│   │               └── index.html
│   └── build.gradle.kts
│── web
│   │── src
│   │   └── main
│   │       ├── java
│   │       │   └── net
│   │       │       └── wuxianjie
│   │       │           └── web
│   │       │               ├── shared
│   │       │               ├── biz
│   │       │               │   ├── XxxController
│   │       │               │   ├── XxxService
│   │       │               │   ├── XxxMapper
│   │       │               │   └── dto
│   │       │               └── XxxApp.java
│   │       └── resources
│   │           ├── mapper
│   │           │   └── XxxMapper
│   │           ├── static
│   │           ├── application.yaml
│   │           └── logback-spring.xml
│   └── build.gradle.kts
├── .gitignore
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

## Gradle

- `./gradlew clean`：清理构建目录
- `./gradlew build`：构建项目
- `./gradlew publishToMavenLocal`：将工件发布到本地 Maven 仓库

## Maven

- `mvn clean`：清理构建目录
- `mvn package`：构建项目
- `mvn install`：将工件发布到本地 Maven 仓库
