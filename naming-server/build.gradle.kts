plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":web-kit"))
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
}
