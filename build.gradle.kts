plugins {
    // Apply SonarQube plugin for code quality analysis
    id("org.sonarqube") version "7.2.3.7755"
    // Apply Kover plugin for code coverage analysis
    id("org.jetbrains.kotlinx.kover") version "0.9.7"
}

dependencies {
    // Add Kover plugin as a dependency for code coverage analysis
    kover(project(":htmlflow-datastar-core"))
    kover(project(":htmlflow-datastar-examples"))
}

repositories {
    mavenCentral()
}

sonar {
    properties {
        property("sonar.projectKey", "xmlet_HtmlFlow-Datastar")
        property("sonar.organization", "fmcarvalho-xmlet")
    }
}

subprojects {
    apply {
        plugin("org.sonarqube")
        plugin("org.jetbrains.kotlinx.kover")
    }
    sonar {
        properties {
            property("sonar.coverage.jacoco.xmlReportPaths", "${projectDir.parentFile.path}/build/reports/kover/report.xml")
            property("sonar.junit.reportPaths", "${projectDir.parentFile.path}/build/test-results/test")
            property("sonar.cpd.exclusions", "**/views/fragments/**")
        }
    }
}

kover.reports {
    verify {
        rule {
            bound {
                minValue.set(80)
                maxValue.set(100)
            }
        }
    }
}

tasks.named("sonar") {
    dependsOn(tasks.named("koverXmlReport"))
}
