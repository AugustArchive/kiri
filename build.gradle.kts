/**
 * 🍣 Kiri: Simple Maven repository made in Kotlin with love.
 * Copyright (C) 2021 Noel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.diffplug.spotless") version "5.14.0"
    kotlin("plugin.serialization") version "1.5.10"
    kotlin("jvm") version "1.5.10"
    application
}

group = "dev.floofy"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // Kotlin libraries
    implementation(kotlin("stdlib"))

    // Koin (Dependency Injection)
    implementation("io.insert-koin:koin-ktor:3.1.1")
    implementation("io.insert-koin:koin-core-ext:3.0.2")
    implementation("io.insert-koin:koin-logger-slf4j:3.1.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("org.slf4j:slf4j-simple:1.7.31")
    api("org.slf4j:slf4j-api:1.7.31")

    // Serializing XML
    implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.82.0")

    // Ktor
    implementation("io.ktor:ktor-client-serialization:1.6.1")
    implementation("io.ktor:ktor-metrics-micrometer:1.6.1")
    implementation("io.ktor:ktor-serialization:1.6.1")
    implementation("io.ktor:ktor-server-netty:1.6.0")
    api("io.ktor:ktor-server-core:1.6.0")

    // Config (YML)
    implementation("com.charleskorn.kaml:kaml:0.34.0")

    // Lettuce (Redis)
    implementation("io.lettuce:lettuce-core:6.1.3.RELEASE")

    // Metrics (Micrometer + Prometheus)
    implementation("io.micrometer:micrometer-registry-prometheus:1.7.1")

    // S3 implementation
    implementation("software.amazon.awssdk:s3:2.16.92")
}

tasks.register("generateMetadata") {
    val path = sourceSets["main"].resources.srcDirs.first()
    if (!file(path).exists()) path.mkdirs()

    val date = Date()
    val formatter = SimpleDateFormat("MMM dd, yyyy @ hh:mm:ss")
    val year = SimpleDateFormat("yyyy").format(date)

    file("$path/metadata.properties").writeText("""built.at = ${formatter.format(date)}
app.version = $version
app.commit = ${execShell("git rev-parse HEAD")}
""".trimIndent())
}

spotless {
    kotlin {
        trimTrailingWhitespace()
        licenseHeaderFile("${rootProject.projectDir}/assets/HEADER")
        endWithNewline()

        // We can't use the .editorconfig file, so we'll have to specify it here
        // issue: https://github.com/diffplug/spotless/issues/142
        ktlint()
            .userData(mapOf(
                "no-consecutive-blank-lines" to "true",
                "no-unit-return" to "true",
                "disabled_rules" to "no-wildcard-imports,colon-spacing",
                "indent_size" to "4"
            ))
    }
}

application {
    mainClass.set("dev.floofy.kiri.Bootstrap")
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.javaParameters = true
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    named<ShadowJar>("shadowJar") {
        val branch = execShell("git branch --show-current")

        archiveFileName.set("Kiri-$branch.jar")
        mergeServiceFiles()
        manifest {
            attributes(mapOf(
                "Manifest-Version" to "1.0.0",
                "Main-Class" to "dev.floofy.kiri.Bootstrap"
            ))
        }
    }

    build {
        dependsOn("generateMetadata")
        dependsOn(spotlessApply)
        dependsOn(shadowJar)
    }
}

fun execShell(command: String): String {
    val parts = command.split("\\s".toRegex())
    val process = ProcessBuilder(*parts.toTypedArray())
        .directory(File("."))
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    process.waitFor(1, TimeUnit.MINUTES)
    return process.inputStream.bufferedReader().readText().trim()
}
