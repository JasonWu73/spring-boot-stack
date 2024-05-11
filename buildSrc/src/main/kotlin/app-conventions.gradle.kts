plugins {
    id("java-conventions")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":my-spring-boot-starter"))
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("com.github.ben-manes.caffeine:caffeine")
}
