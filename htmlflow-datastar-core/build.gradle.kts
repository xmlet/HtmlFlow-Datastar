plugins {
    kotlin("jvm") version "2.3.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // HtmlFlow Kotlin dependency
    implementation("com.github.xmlet:htmlflow-kotlin:5.0.2")
    api("com.github.xmlet:htmlflow-kotlin:5.0.2")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
