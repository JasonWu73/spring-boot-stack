import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("org.springframework.boot")
}

version = "v1.0.0"

springBoot {
    buildInfo {
        properties {
            excludes.add("time") // 移除默认时间戳（ISO 8601 格式 `yyyy-MM-dd'T'HH:mm:ssZ`）
            additional.set(
                mapOf(
                    "timestamp" to LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
            )
            name = "Spring Cloud 网关项目"
        }
    }
}

dependencies {
    implementation(project(":gateway-kit"))
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:")
}