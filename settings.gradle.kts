rootProject.name = "vtt-tools"

include(
    "app"
)

val kotlinVersion: String by settings
val shadowVersion: String by settings
val springDependencyManagementVersion: String by settings
val gradleGitPropertiesVersion: String by settings
val gradleVersionsVersion: String by settings

pluginManagement {
    fun PluginManagementSpec.loadProperties(fileName: String, path: String = rootDir.absolutePath) = java.util.Properties().also { properties ->
        File("$path/$fileName").inputStream().use {
            properties.load(it)
        }
    }
    val versions: java.util.Properties = loadProperties("gradle.properties")

    val kotlinVersion: String by versions
    val springDependencyManagementVersion: String by versions
    val shadowVersion: String by versions
    val micronautApplicationVersion: String by versions

    resolutionStrategy {
        eachPlugin {
            when(requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.kapt" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.allopen" -> useVersion(kotlinVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
                "com.github.johnrengelman.shadow" -> useVersion(shadowVersion)
                "io.micronaut.application" -> useVersion(micronautApplicationVersion)
            }
        }
    }
}
