plugins {
    id("org.springframework.boot") version "3.2.5"
    id("org.graalvm.buildtools.native") version "0.9.28" // `./gradlew nativeCompile` - 编译本地可执行文件
}

dependencies {
    api(project(":web-kit"))
}

graalvmNative {
    binaries {
        named("main") {
            // 指定生成独立的可执行文件，而不是共享库（Shared Library）
            sharedLibrary.set(false)
        }
    }
}