plugins {
    id("org.springframework.boot")
}

dependencies {
    api(project(":web-kit"))
    implementation("org.springframework.boot:spring-boot-starter-amqp")
}
