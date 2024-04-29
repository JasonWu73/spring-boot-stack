# Spring Boot 项目

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