rootProject.name = "spring-boot-stack"

// 子模块
include(":common-kit")
include(":web-kit")
include(":gateway-kit")
include(":web")
include(":rabbitmq-producer")
include(":rabbitmq-consumer")

// 配置 Gradle 插件的下载源为阿里云 Maven 仓库
pluginManagement {
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
    }
}
