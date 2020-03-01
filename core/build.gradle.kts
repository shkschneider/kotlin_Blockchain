import tasks.gitDescribe

plugins {
    kotlin("jvm")
    kotlinter version Versions.kotlinter
}

// org.jmailen.kotlinter
kotlinter {
    ignoreFailures = false
    reporters = arrayOf("checkstyle", "plain")
    disabledRules = arrayOf("no-blank-line-before-rbrace", "import-ordering")
}
tasks.alias("lint", "lintKotlin")

dependencies {
    implementation("org.jetbrains.kotlin", "kotlin-stdlib")
    tests()
}

tasks.add("version") {
    gitDescribe()
}
