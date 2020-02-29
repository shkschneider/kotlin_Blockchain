plugins {
    // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    // https://github.com/jeremymailen/kotlinter-gradle/releases
    id("org.jmailen.kotlinter") version "2.3.1"
    application
}

repositories {
    jcenter()
    mavenCentral()
}

apply(from = "gradle/gradle.gradle")
apply(from = "gradle/kotlin.gradle")

application {
    applicationName = rootProject.name
    mainClassName = "Application"
}
dependencies {
    implementation("org.jetbrains.kotlin", "kotlin-stdlib")
}
dependencies {
    testImplementation("org.jetbrains.kotlin", "kotlin-test")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit")
    testImplementation("io.mockk", "mockk", "1.+")
}

apply(from = "gradle/kotlinter.gradle")
