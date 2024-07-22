import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

val appVersion = Properties().apply {
    val dir =
        project.projectDir.absolutePath + File.separator + "src" + File.separator + "jvmMain" + File.separator + "resources"

    load(FileInputStream(File(dir, "config.properties")))
}.getProperty("app_version") ?: "1.0.0"

group = "cn.netdiscovery.monica"
version = appVersion

val mOutputDir = project.buildDir.resolve("output")

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven( "https://jitpack.io" )
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                // 缓存
                implementation("com.github.fengzhizi715.RxCache:core:${rootProject.extra["rxcache"]}")
                implementation("com.github.fengzhizi715.RxCache:okio:${rootProject.extra["rxcache"]}")
                implementation("com.github.fengzhizi715.RxCache:extension:${rootProject.extra["rxcache"]}")

                // di
                implementation("io.insert-koin:koin-compose:${rootProject.extra["koin.compose"]}")

                // color math
                implementation("com.github.ajalt.colormath:colormath-ext-jetpack-compose:${rootProject.extra["colormath"]}")

                // coroutines utils
                implementation ("com.github.fengzhizi715.Kotlin-Coroutines-Utils:common:${rootProject.extra["coroutines.utils"]}")

                //log config
                implementation("ch.qos.logback:logback-classic:${rootProject.extra["logback"]}")
                implementation("ch.qos.logback:logback-core:${rootProject.extra["logback"]}")
                implementation("ch.qos.logback:logback-access:${rootProject.extra["logback"]}")
            }
        }
        val jvmTest by getting
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            outputBaseDir.set(mOutputDir)   //build/output
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "Monica"
            packageVersion = appVersion

            jvmArgs += listOf("-Xmx2G")
            includeAllModules = true    //包含所有模块

            macOS {
                bundleID = "cn.netdiscovery.monica"

                dockName = "monica"
            }
        }
    }
}
