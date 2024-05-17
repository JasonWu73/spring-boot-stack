plugins {
    id("app-conventions")
    id("org.graalvm.buildtools.native")
}

dependencies {
}

graalvmNative {
    binaries {
        named("main") {
            // 开启实验
            buildArgs.add("-H:+UnlockExperimentalVMOptions")

            // 指定生成独立的可执行文件，而不是共享库（Shared Library）
            sharedLibrary.set(false)
        }
    }
}
