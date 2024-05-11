plugins {
    id("java-conventions")
}

// 原本 `implementation` 依赖在编译和依赖解析阶段不会被传递给其他模块
// 但打包和运行 Spring Boot 应用时，所有必需的依赖（包括 `implementation` 依赖）都会被包含在最终的可执行 JAR 文件中
// 这就导致无法根据添加到项目中的 Jar 依赖来决定是否自动配置
// 最终解决方案：对需要按条件自动配置的 Jar 采用 `compileOnly`（保证正常编译）和 `testImplementation`（如果涉及测试）依赖配置
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")

    compileOnly("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-security")

    compileOnly("org.springframework.cloud:spring-cloud-starter-gateway")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
