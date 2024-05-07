plugins {
    id("org.springframework.boot") version "3.2.5"
}

dependencies {
    api(project(":web-kit"))
    implementation("com.mysql:mysql-connector-j")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:${project.properties["mybatisSpringBootStarterVersion"]}")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
}