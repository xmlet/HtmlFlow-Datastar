plugins {
    kotlin("jvm") version "2.3.0"

    // Apply ktlint plugin for code linting
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

repositories {
    mavenCentral()
}

dependencies {
    // HtmlFlow Kotlin dependency
    api("com.github.xmlet:htmlflow-kotlin:5.0.3")

    // Jakarta web service annotations API for using annotation @Path
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    // Kotlin reflection library
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
