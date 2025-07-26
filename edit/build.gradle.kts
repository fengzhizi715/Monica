plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven( "https://jitpack.io" )
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