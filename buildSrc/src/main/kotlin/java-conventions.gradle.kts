plugins {
    java
    `java-library`
    `maven-publish`
    id("io.spring.dependency-management")
}

group = "net.wuxianjie"
version = "0.0.1-SNAPSHOT"

val springCloudVersion by extra("2023.0.1")
val mybatisSpringBootVersion by extra("3.0.3")

dependencyManagement {
    // 引入 Spring Boot 和 Spring Cloud 的 BOM 文件来管理这两个框架及其相关依赖的版本
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }

    // 统一管理其他依赖的版本
    dependencies {
        dependency("org.mybatis.spring.boot:mybatis-spring-boot-starter:$mybatisSpringBootVersion")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    // 使用阿里云的 Maven 镜像以加速下载依赖
    maven { setUrl("https://maven.aliyun.com/repository/public") }
    maven { setUrl("https://maven.aliyun.com/repository/spring") }

    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    // `javac` 使用 `-parameters` 选项编译 Java 源码，以支持反射获取参数名
    options.compilerArgs.add("-parameters")
    // 设置编译字符集为 UTF-8，而不是默认的系统字符集
    options.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            from(components["java"])
        }
    }

    repositories {
        // 指定将工件发布到本地 Maven 仓库
        mavenLocal()
    }
}
