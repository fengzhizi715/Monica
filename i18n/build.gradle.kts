plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")

    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)

    // log config
    implementation("ch.qos.logback:logback-classic:${rootProject.extra["logback"]}")
    implementation("ch.qos.logback:logback-core:${rootProject.extra["logback"]}")
    implementation("ch.qos.logback:logback-access:${rootProject.extra["logback"]}")
}

tasks.test {
    useJUnitPlatform()
}