plugins {
    kotlin("jvm") version "1.3.61"
    application
    id("org.jmailen.kotlinter") version "2.3.1"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("io.mockk:mockk:1.+")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

application {
    mainClassName = "Application"
}

kotlinter {
    ignoreFailures = false
    indentSize = 4
    continuationIndentSize = 4
    reporters = arrayOf("checkstyle", "plain")
    experimentalRules = false
    disabledRules = arrayOf("no-blank-line-before-rbrace", "import-ordering")
}
