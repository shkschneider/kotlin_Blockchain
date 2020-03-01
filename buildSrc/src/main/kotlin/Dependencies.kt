import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.core() {
    project(path = ":core")
    add("implementation", project(":core"))
}
fun DependencyHandler.tests() {
    listOf(
        Triple("org.jetbrains.kotlin", "kotlin-test", null),
        Triple("org.jetbrains.kotlin", "kotlin-test-junit", null),
        Triple("io.mockk", "mockk", Versions.mockk)
    ).forEach {
        add("testImplementation", "${it.first}:${it.second}:${it.third ?: "+"}")
    }
}
