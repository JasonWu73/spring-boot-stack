plugins {
    java // `./gradlew build` - 用于构建 Java 项目
    `java-library` // 支持 `api` 配置传递依赖
    id("io.spring.dependency-management") version "1.1.4" // 支持 `dependencyManagement` 配置 Spring 依赖版本号
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "net.wuxianjie"
    version = "0.0.1-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    // 配置项目依赖的下载源为阿里云 Maven 仓库
    // `repositories` 的声明顺序非常重要，会直接影响 Gradle 查找和下载依赖包的顺序
    repositories {
        mavenLocal() // 本地 Maven 仓库中的依赖包放在第一位
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/spring") }
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        // 默认情况下，Java 编译器不会保留方法参数的名称
        // 为了使 Spring 能够通过反射自动解析参数名称，需要在编译项目时加上 `-parameters` 参数
        options.compilerArgs.add("-parameters")

        // 明确指定编译器编译的字符集为 UTF-8，而不是系统默认的字符集
        options.encoding = "UTF-8"
    }
}

subprojects {
    apply(plugin = "io.spring.dependency-management")

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${project.property("springBootVersion")}")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${project.property("springCloudVersion")}")
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}