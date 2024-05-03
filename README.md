# Spring Boot 项目

## RESTful API 设计

**Endpoints**

- 使用名词表示资源
    - 端点应该是名词，表示一种资源，而不是动词。例如使用 `/users` 而不是 `/getUsers`
    - 使用复数形式表示资源集合，如 `/users`，单个资源则为 `/users/{id}`
- 使用层级关系表示资源之间的关联
    - 使用 URL 路径来表示资源之间的层级关系。例如 `/users/{userId}/orders` 表示某个用户的订单列表
    - 层级通常不超过 2 层，否则会导致 URL 过长，不利于使用和维护
- 使用 HTTP 方法表示操作
    - `GET` 用于获取资源
    - `POST` 用于创建资源
    - `PUT` 用于更新整个资源
    - `PATCH` 用于更新资源的部分属性
    - `DELETE` 用于删除资源
- 其他最佳实践
    - 使用版本化的 URL，如 `/v1/users`
    - URL 中只使用小写字母和横线（不使用下划线）
    - URL 中不要包含文件扩展名
    - 使用 query string 参数过滤、排序等操作，如 `/users?role=admin&sortBy=name`

**HTTP 响应状态码***

- `2xx` 成功状态码
    - `200 OK` - 一切正常，请求成功
    - `201 Created` - 新资源已成功创建
    - `204 No Content` - 请求成功，但响应主体为空，如 `DELETE` 请求成功时的响应
- `4xx` 客户端错误状态码
    - `400 Bad Request` - 请求存在语法错误或参数错误，服务器无法理解
    - `401 Unauthorized` - 请求缺少身份验证信息或者身份验证失败
    - `403 Forbidden` - 请求被服务器拒绝，没有访问权限
    - `404 Not Found` - 请求的资源在服务器上不存在
    - `405 Method Not Allowed` - 请求的 HTTP 方法不被允许
    - `409 Conflict` - 请求与服务器当前状态存在冲突，如修改时存在同名用户
- `5xx` 服务器错误状态码
    - `500 Internal Server Error` - 服务器发生未知错误
    - `502 Bad Gateway` - 服务器本身工作正常，但在将请求转发到上游服务器时出现了问题

## 目录结构

```bash
spring-boot-project
├── src
│   ├── main
│   │   ├── java
│   │   │   └── net
│   │   │       └── wuxianjie
│   │   │           ├── shared # 与业务无关的通用代码
│   │   │           ├── biz # 功能模块
│   │   │           │   └── dto # 数据传输对象（Data Transfer Object）
│   │   │           └── Application.java # 项目启动入口类
│   │   └── resources
│   │       ├── mapper # 对应 MyBatis 映射器接口的 XML 映射文件
│   │       ├── static # 静态资源文件如 CSS、JS 等
│   │       ├── application.yml # 应用配置文件
│   │       └── logback-spring.xml # 日志配置文件
│   └── test
│       └── java
│           └── net
│               └── wuxianjie
└── README.md # 项目的描述文件
```