import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    id("com.github.gmazzo.buildconfig") version "5.4.0"
}

val appVersion = "1.0.0"
val isProVersion = true

group = "cn.netdiscovery.monica"
version = appVersion

val mOutputDir = project.buildDir.resolve("output")

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven( "https://jitpack.io" )
}

val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val osArch = System.getProperty("os.arch")
var targetArch = when (osArch) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val skikoVersion = "0.8.4"
val target = "${targetOs}-${targetArch}"

buildConfig {
    useKotlinOutput { topLevelConstants = true }
    useKotlinOutput { internalVisibility = false }   // adds `internal` modifier to all declarations

    buildConfigField("APP_NAME", project.name)
    buildConfigField("APP_VERSION", appVersion)
    buildConfigField("KOTLIN_VERSION", "${rootProject.extra["kotlin.version"]}")
    buildConfigField("COMPOSE_VERSION", "${rootProject.extra["compose.version"]}")
    buildConfigField("IS_PRO_VERSION", isProVersion)
    buildConfigField("BUILD_TIME", System.currentTimeMillis())
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                // skiko
                implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$skikoVersion")

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

                // log config
                implementation("ch.qos.logback:logback-classic:${rootProject.extra["logback"]}")
                implementation("ch.qos.logback:logback-core:${rootProject.extra["logback"]}")
                implementation("ch.qos.logback:logback-access:${rootProject.extra["logback"]}")

                // precompose
                implementation("moe.tlaster:precompose:${rootProject.extra["precompose.version"]}")
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
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
        nativeDistributions {
            outputBaseDir.set(mOutputDir)   //build/output
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            packageName = "Monica"
            packageVersion = appVersion
            description = "Monica is a cross-platform image editor"
            copyright = "© 2024 Tony Shen. All rights reserved."

            if(isProVersion) {
                jvmArgs += listOf("-Xms4G","-Xmx4G")
            } else {
                jvmArgs += listOf("-Xmx2G")
            }

            includeAllModules = true    //包含所有模块

            macOS {
                bundleID = "cn.netdiscovery.monica"
                dockName = "monica"
            }

            windows {
                console = false    // 为应用程序添加一个控制台启动器
                shortcut = true    // 桌面快捷方式
                dirChooser = true  // 允许在安装过程中自定义安装路径
                perUserInstall = false   //允许在每个用户的基础上安装应用程序
                menuGroup = "start-menu-group"
                upgradeUuid = "b329caf3-6681-49b9-98d0-adb34d32e130"
                iconFile.set(project.file("src/jvmMain/resources/images/launcher.ico"))
            }
        }
    }
}