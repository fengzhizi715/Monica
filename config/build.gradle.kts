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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}