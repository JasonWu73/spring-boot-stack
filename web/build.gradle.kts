import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("org.graalvm.buildtools.native") version "0.9.28" // `./gradlew nativeCompile` - 编译本地可执行文件
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
            name = "Web 项目"
        }
    }
}

dependencies {
    implementation(project(":web-kit"))
}

graalvmNative {
    binaries {
        named("main") {
            // 指定生成独立的可执行文件，而不是共享库（Shared Library）
            sharedLibrary.set(false)
        }
    }
}