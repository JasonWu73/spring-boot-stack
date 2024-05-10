plugins {
    // `./gradlew publishToMavenLocal` - 发布工件到本地 Maven 仓库
    `maven-publish`
}

// 作为工具类 Jar，需要通过 `api` 配置传递依赖
dependencies {
    api("org.springframework.boot:spring-boot-starter-json")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("com.github.ben-manes.caffeine:caffeine")
}

// 配置 Maven 发布工件
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

    // 指定将工件发布到本地 Maven 仓库
    repositories {
        mavenLocal()
    }
}
