plugins {
    java // `./gradlew build` - 用于构建 Java 项目
    `java-library` // 添加以支持通过 `api` 配置传递依赖
    id("io.spring.dependency-management")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "net.wuxianjie"

    // 生产版本号，建议每次发布时更新，并使用 `git tag` 标记
    version = "${project.property("productionVersion") ?: "v0.0.1-SNAPSHOT"}"

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        // 配置项目依赖的下载源为阿里云 Maven 仓库
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
            setUrl("https://maven.aliyun.com/repository/spring")
        }

        mavenLocal()
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
        val springBootVersion: String by project
        val springCloudVersion: String by project

        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
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
