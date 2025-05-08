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

    // coroutines utils
    implementation ("com.github.fengzhizi715.Kotlin-Coroutines-Utils:common:${rootProject.extra["coroutines.utils"]}")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["kotlinx.coroutines.core.version"]}")

    // twelvemonkeys
    implementation("com.twelvemonkeys.imageio:imageio-core:${rootProject.extra["twelvemonkeys"]}")
    implementation("com.twelvemonkeys.imageio:imageio-jpeg:${rootProject.extra["twelvemonkeys"]}")

    // webp
    implementation("org.sejda.imageio:webp-imageio:0.1.5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}