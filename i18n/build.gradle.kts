plugins {
    kotlin("jvm")
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
    testImplementation("junit:junit:4.13.2")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")

    // log config
    implementation("ch.qos.logback:logback-classic:${rootProject.extra["logback"]}")
    implementation("ch.qos.logback:logback-core:${rootProject.extra["logback"]}")
    implementation("ch.qos.logback:logback-access:${rootProject.extra["logback"]}")
}

tasks.test {
    useJUnitPlatform()
}