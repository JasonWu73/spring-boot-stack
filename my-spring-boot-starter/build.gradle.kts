plugins {
    id("java-conventions")
}

// 原本 `implementation` 依赖在编译和依赖解析阶段不会被传递给其他模块
// 但 Spring Boot 的 Gradle 插件在「运行」和「打包」应用时，会将所有必需的依赖都包含在最终可以运行的 Jar 文件（称为 Fat Jar 或 Uber Jar）中
// 这就导致了无法根据类路径中是否存在指定 Jar 依赖来决定是否使用自动配置
// 最终解决方案：对需要按条件自动配置的 Jar 依赖采用 `compileOnly`（用于编译）和 `testImplementation`（用于测试，如果需要的话）依赖配置
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")

    compileOnly("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-security")

    compileOnly("org.springframework.cloud:spring-cloud-starter-gateway")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
