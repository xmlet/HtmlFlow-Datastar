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
    implementation("com.github.xmlet:htmlflow-kotlin:5.0.3")
    api("com.github.xmlet:htmlflow-kotlin:5.0.3")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
