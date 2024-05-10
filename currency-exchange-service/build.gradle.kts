plugins {
    id("org.springframework.boot")
}

dependencies {
    val mybatisSpringBootStarterVersion: String by project

    implementation(project(":web-kit"))
    implementation("com.mysql:mysql-connector-j")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:$mybatisSpringBootStarterVersion")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
}
