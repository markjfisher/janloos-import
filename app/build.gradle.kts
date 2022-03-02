import java.lang.System.getenv

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
}

val minorVersion = getenv().getOrDefault("CI_PIPELINE_ID", "")!!

group = "tools"
version = if (minorVersion.isEmpty()) "DEV" else "0.$minorVersion.0"

val jacksonVersion: String by project
val jakartaInjectVersion: String by project
val julToSlf4jVersion: String by project
val kotlinxCoroutineVersion: String by project
val micronautVersion: String by project
val zip4jVersion: String by project

val assertJVersion: String by project
val junitJupiterEngineVersion: String by project
val micronautJunit: String by project
val mockkVersion: String by project

val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val logbackEncoderVersion: String by project

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("tools.*")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinxCoroutineVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("jakarta.inject:jakarta.inject-api:$jakartaInjectVersion")

    implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    implementation("info.picocli:picocli")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-http-client")

    implementation("net.lingala.zip4j:zip4j:$zip4jVersion")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("org.slf4j:jul-to-slf4j:$julToSlf4jVersion")
    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("ch.qos.logback:logback-classic")

    kapt(platform("io.micronaut:micronaut-bom:$micronautVersion"))
    kapt("info.picocli:picocli-codegen")
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation("io.micronaut.test:micronaut-test-junit5:$micronautJunit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")

}

application {
    mainClass.set("tools.Application")
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }

    shadowJar {
        mergeServiceFiles()
    }

    named<JavaExec>("run") {
        jvmArgs(listOf("-noverify", "-XX:TieredStopAtLevel=1"))
    }

    test {
        useJUnitPlatform()
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }
}

allOpen {
    annotation("io.micronaut.aop.Around")
}
