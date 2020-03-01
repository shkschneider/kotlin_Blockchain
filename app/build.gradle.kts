plugins {
    kotlin("jvm")
    application
}

// org.gradle.application
application {
    applicationName = rootProject.name
    mainClassName = "Application"
}

dependencies {
    core()
}
