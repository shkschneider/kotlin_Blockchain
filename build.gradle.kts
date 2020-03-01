plugins {
    kotlin("jvm") version Versions.kotlin
}

println("Gradle ${gradle.gradleVersion}")

// repositories
allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

// gradlew
tasks {
    wrapper {
        gradleVersion = Versions.gradle
        distributionType = Wrapper.DistributionType.BIN
    }
}

// kotlin.jvm
allprojects {
    pluginManager.withPlugin(Plugins.kotlinJvm) {
        dependencies {
            implementation("org.jetbrains.kotlin", "kotlin-stdlib")
        }
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}
