plugins {
    id("org.springframework.boot") version "3.2.5"
}

dependencies {
    implementation(project(":web-kit"))
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
}