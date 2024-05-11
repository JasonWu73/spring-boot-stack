rootProject.name = "spring-boot-stack"

include(":my-spring-boot-starter")
include(":web")
include(":rabbitmq-producer")
include(":rabbitmq-consumer")

pluginManagement {
    repositories {
        // 使用阿里云的 Maven 镜像以加速下载插件
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }

        gradlePluginPortal()
    }
}
