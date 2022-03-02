tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "7.1.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}

defaultTasks(
        "clean", "build"
)

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}
