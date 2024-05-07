plugins {
    id("org.springframework.boot") version "3.2.5"
}

dependencies {
    api(project(":web-kit"))
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}