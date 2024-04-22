# Spring Boot 项目

## 目录结构

```bash
spring-boot-project
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           ├── common # 全局组件，如拦截器、过滤器、监听器、AOP、`@ControllerAdvice` 等
│   │   │           │   ├── advice # 存放 `@ControllerAdvice` 注解的全局异常处理类
│   │   │           │   ├── aspect # 切面类
│   │   │           │   ├── config # 配置相关类
│   │   │           │   ├── constant # 常量类
│   │   │           │   ├── exception # 自定义异常类
│   │   │           │   ├── filter # 过滤器
│   │   │           │   └── util # 工具类集
│   │   │           ├── controller # Web 层控制器
│   │   │           ├── domain # 领域模型实体类
│   │   │           │   ├── dto # 数据传输对象（Data Transfer Object），关注于在不同层次间高效传输数据
│   │   │           │   ├── entity # 持久化实体，通常与数据库表结构一一对应
│   │   │           │   └── vo # 视图对象（View Object），关注于数据在展示层的呈现形式
│   │   │           ├── mapper # MyBatis 的映射器接口
│   │   │           ├── service # 服务层接口
│   │   │           │   └── impl # 服务实现类
│   │   │           └── Application.java # 项目启动入口类
│   │   └── resources
│   │       ├── mapper # 对应 MyBatis 映射器接口的 XML 映射文件
│   │       ├── static # 静态资源文件如 CSS、JS 等
│   │       ├── application.yml # 应用配置文件
│   │       └── logback-spring.xml # 日志配置文件
│   └── test
│       └── java
│           └── com
│               └── example
└── README.md # 项目的描述文件
```
