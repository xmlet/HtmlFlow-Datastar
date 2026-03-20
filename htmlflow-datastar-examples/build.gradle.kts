plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // Apply the Kotlin serialization plugin
    kotlin("plugin.serialization") version "2.3.0"

    // Apply the ktlint plugin for code linting
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Ktor dependencies
    implementation("io.ktor:ktor-server-core:3.4.0")
    implementation("io.ktor:ktor-server-netty:3.4.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.4.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.0")

    // Http4k dependencies
    implementation(platform("org.http4k:http4k-bom:6.33.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.http4k:http4k-web-datastar")

    implementation(project(":htmlflow-datastar-core"))

    // Datastar Kotlin dependencies
    implementation("dev.data-star.kotlin:kotlin-sdk:1.0.0-RC3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("org.slf4j:slf4j-simple:2.0.16")
    // Jakarta web service annotations API for using annotation @Path
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")

    // Ktor test tools for testing
    testImplementation("io.ktor:ktor-server-test-host:3.4.0")

    // Playwright for end-to-end testing with JavaScript execution
    testImplementation("com.microsoft.playwright:playwright:1.48.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "pt.isel.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

sourceSets {
    main {
        resources {
            srcDir("src/main/kotlin")
        }
    }
}
