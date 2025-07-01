 plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig") version "5.4.0"
}

repositories {
    mavenCentral()
}

val isProVersion = false // true 为打包的版本，false 是本地开发的版本

buildConfig {
    useKotlinOutput { topLevelConstants = true }
    useKotlinOutput { internalVisibility = false }   // adds `internal` modifier to all declarations

    buildConfigField("APP_NAME", project.name)
    buildConfigField("APP_VERSION", "${rootProject.extra["app.version"]}")
    buildConfigField("KOTLIN_VERSION", "${rootProject.extra["kotlin.version"]}")
    buildConfigField("COMPOSE_VERSION", "${rootProject.extra["compose.version"]}")
    buildConfigField("IS_PRO_VERSION", isProVersion)
    buildConfigField("BUILD_TIME", System.currentTimeMillis())
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