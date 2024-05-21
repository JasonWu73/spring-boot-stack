import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("app-conventions")
}

// 用于正式发布的版本号，每次发布时更新，并使用 `git tag` 标记
version = "v0.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}

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
            name = "Redis 项目"
        }
    }
}
