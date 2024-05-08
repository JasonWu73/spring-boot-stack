plugins {
    // `./gradlew build` - 用于构建 Java 项目
    java
    // 支持 `api` 配置传递依赖
    `java-library`
    // 支持 `dependencyManagement` 配置依赖版本号
    id("io.spring.dependency-management") version "1.1.4"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "net.wuxianjie"
    version = "0.0.1-SNAPSHOT"

    java { sourceCompatibility = JavaVersion.VERSION_21 }

    // 配置项目依赖的下载源为阿里云 Maven 仓库
    // `repositories` 的声明顺序非常重要，会直接影响 Gradle 查找和下载依赖包的顺序
    repositories {
        // 本地 Maven 仓库中的依赖包放在第一位
        mavenLocal()
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/spring") }
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        // 默认情况下，Java 编译器在编译时会丢弃方法参数的名称
        // 这会导致 Spring 无法依赖于方法参数名称信息来自动绑定参数值
        // `javac -parameters MyClass.java`
        // - `-parameters` - 在生成的字节码中包含方法参数的名称
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
            mavenBom(
                "org.springframework.boot:spring-boot-dependencies:${
                    project.property(
                        "springBootVersion"
                    )
                }"
            )
            mavenBom(
                "org.springframework.cloud:spring-cloud-dependencies:${
                    project.property(
                        "springCloudVersion"
                    )
                }"
            )
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
