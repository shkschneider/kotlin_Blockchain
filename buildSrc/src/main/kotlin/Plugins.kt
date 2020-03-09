import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object Plugins {

    const val kotlinJvm = "org.jetbrains.kotlin.jvm"
    const val kotlinter = "org.jmailen.kotlinter"
    const val versions = "com.github.ben-manes.versions"

}

val PluginDependenciesSpec.kotlinter: PluginDependencySpec
    get() = id(Plugins.kotlinter)

val PluginDependenciesSpec.versions: PluginDependencySpec
    get() = id(Plugins.versions)
