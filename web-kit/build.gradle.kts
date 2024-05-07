plugins {
    `maven-publish` // `./gradlew publishToMavenLocal` - 发布工件到本地 Maven 仓库
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("org.springframework.boot:spring-boot-configuration-processor")
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