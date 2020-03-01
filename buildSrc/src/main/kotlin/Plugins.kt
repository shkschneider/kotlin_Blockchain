import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object Plugins {

    const val kotlinJvm = "org.jetbrains.kotlin.jvm"
    const val kotlinter = "org.jmailen.kotlinter"

}

val PluginDependenciesSpec.kotlinter: PluginDependencySpec
    get() = id(Plugins.kotlinter)
