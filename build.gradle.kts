plugins {
    // https://github.com/JetBrains/kotlin/blob/master/ChangeLog.md
    kotlin("jvm") version "1.3.61"
    application
    // https://github.com/jeremymailen/kotlinter-gradle/releases
    id("org.jmailen.kotlinter") version "2.3.1"
}

repositories {
    jcenter()
    mavenCentral()
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    wrapper {
        // https://gradle.org/releases/
        gradleVersion = "6.2.1"
        distributionType = Wrapper.DistributionType.BIN
    }
}

application {
    mainClassName = "Application"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("io.mockk:mockk:1.+")
}

kotlinter {
    ignoreFailures = false
    indentSize = 4
    continuationIndentSize = 4
    reporters = arrayOf("checkstyle", "plain")
    experimentalRules = false
    disabledRules = arrayOf("no-blank-line-before-rbrace", "import-ordering")
}
