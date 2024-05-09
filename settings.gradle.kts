rootProject.name = "spring-boot-stack"

// 子模块
include(":common-kit")
include(":web-kit")
include(":gateway-kit")
include(":web")
include(":rabbitmq-producer")
include(":rabbitmq-consumer")

pluginManagement {
    // 统一管理 Gradle 插件的版本
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val graalvmBuildtoolsNativeVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion

        // 添加以支持通过 `dependencyManagement` 配置依赖版本号
        id("io.spring.dependency-management") version springDependencyManagementVersion

        // `./gradlew nativeCompile` - 编译本地可执行文件
        id("org.graalvm.buildtools.native") version graalvmBuildtoolsNativeVersion
    }

    // 配置 Gradle 插件的下载源为阿里云 Maven 仓库
    repositories {
        maven {
            setUrl("https://maven.aliyun.com/repository/gradle-plugin")
        }
        gradlePluginPortal()
    }
}
