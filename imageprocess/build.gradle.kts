plugins {
    kotlin("jvm")
}

group = "cn.netdiscovery.monica"
version = "1.0.5"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}