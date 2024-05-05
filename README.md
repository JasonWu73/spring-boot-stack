# Spring Boot 项目

## 关于排版说明

- 行内代码（即 `行内代码`）与非标点之间需要有一个空格

### 中文、英文、数字混合时空格的使用

- 英文与非标点的中文之间需要有一个空格，如「使用 GitHub 保存笔记文件」而不是「使用GitHub保存笔记文件」
- 数字与非标点的中文之间需要有一个空格，如「写了 5 份文档」而不是「写了5份文档」
- 数字与单位之间需要有一个空格，如「5 GB」而不是「5GB」
- 专有名词哪怕是在句首也要使用官方或约定俗成的格式，包括空格、英文大小写等，如 macOS、iOS、iPhone、iCloud、QQ音乐

### 标点相关

> 打印在服务器控制台（即普通用户不可见的内容）的中英文一律使用英文/半角标点，比如日志记录、抛出的异常信息等。

- 只有中文或中英文混排中，一律使用中文/全角标点
- 中英文混排中如果出现整句英文，则在这句英文中使用英文/半角标点
- 中文/全角标点与其他字符间一律不加空格
- 中文中使用直角引号「」和『』，其中「」为外层引号
- 在中文中使用引号引用英文短语、句子时，引号内使用英文半角标点
    - `他的座右铭是「Stay hungry, stay foolish.」。`

## RESTful API 设计

### Controller 方法命名

- GET 方法用于获取资源，方法名通常为 `get*`
    - `getUsers` - 获取用户列表
- POST 方法用于新建资源，方法名通常为 `add*`
    - `addUser` - 创建用户
- PUT 方法用于完整更新资源，方法名通常为 `update*`
    - `updateUser` - 更新用户的全部属性
- PATCH 方法用于更新资源的部分属性，方法名通常为 `patch*`
    - `patchUser` - 更新用户的部分属性
- DELETE 方法用于删除资源，方法名通常为 `delete*`
    - `deleteUser` - 删除用户

### URL 映射

对于 URL 映射，通常采用复数形式的资源名称，例如:

- `@RequestMapping("/users")`
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
    - 使用 query string 参数过滤、排序等操作，如 `/users?role=admin&sortBy=name`

**HTTP 响应状态码***

- `2xx` 成功状态码
    - `200 OK` - 一切正常，请求成功
    - `201 Created` - 新资源已成功创建，如 `POST` 请求创建资源成功
    - `204 No Content` - 请求成功，但响应主体为空，如 `DELETE` 请求删除资源成功，`PUT` 或 `PATCH` 请求更新资源成功
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