plugins {
    `kotlin-dsl`
}

repositories {
    // 使用阿里云的 Maven 镜像以加速下载依赖
    maven { setUrl("https://maven.aliyun.com/repository/public") }
    maven { setUrl("https://maven.aliyun.com/repository/spring") }

    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.spring.gradle:dependency-management-plugin:1.1.5")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.5")
    implementation("org.graalvm.buildtools:native-gradle-plugin:0.10.1")
}
