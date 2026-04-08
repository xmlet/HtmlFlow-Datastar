plugins {
    kotlin("jvm") version "2.3.0"

    // Apply ktlint plugin for code linting
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"

    id("org.jetbrains.dokka") version "2.1.0"
	
    // For publishing the library to a Maven repository
    `maven-publish`
    signing
}

group = "com.github.xmlet"
version = "1.0.0-alpha"

repositories {
    mavenCentral()
}

dependencies {
    // HtmlFlow Kotlin dependency
    api("com.github.xmlet:htmlflow-kotlin:5.0.4")

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

// Create Javadoc JAR from dokkaGenerate output
tasks.register<Jar>("dokkaJavadocJar") {
    archiveClassifier.set("javadoc")
    dependsOn(tasks.dokkaGenerate)
    from(layout.buildDirectory.dir("dokka/html"))
}

// Configure publishing to Maven Central
publishing {
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://central.sonatype.com/api/v1/publisher")
            credentials {
                username = System.getenv("MAVEN_USERNAME") ?: ""
                password = System.getenv("MAVEN_PASSWORD") ?: ""
            }
        }
    }

    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])

            artifactId = project.name
            groupId = project.group.toString()
            version = project.version.toString()
			
            artifact(tasks.kotlinSourcesJar)
            artifact(tasks.named("dokkaJavadocJar"))

            pom {
                name.set("HtmlFlow-Datastar")
                description.set("Type-Safe Hypermedia-First DSL for Reactive Backend-Driven Web Applications")
                url.set("https://github.com/xmlet/HtmlFlow-Datastar")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        name.set("Miguel Gamboa")
                    }
                    developer {
                        name.set("Paulo Carvalho")
                    }
                    developer {
                        name.set("Leonel Correia")
                    }
                    developer {
                        name.set("Ricardo Gomes")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/xmlet/HtmlFlow-Datastar.git")
                    developerConnection.set("scm:git:https://github.com/xmlet/HtmlFlow-Datastar.git")
                    url.set("https://github.com/xmlet/HtmlFlow-Datastar")
                }
            }
        }
    }
}

// Configure signing
signing {
    useInMemoryPgpKeys(
        System.getenv("GPG_PRIVATE_KEY"),
        System.getenv("GPG_PASSPHRASE"),
    )
    sign(publishing.publications["mavenJava"])
}
