plugins {
    id("java-conventions")
    id("org.springframework.boot")
    id("org.graalvm.buildtools.native")
}

dependencies {
    implementation(project(":my-spring-boot-starter"))
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("com.github.ben-manes.caffeine:caffeine")
}

graalvmNative {
    binaries {
        named("main") {
            // 指定生成独立的可执行文件，而不是共享库（Shared Library）
            sharedLibrary.set(false)
        }
    }
}
