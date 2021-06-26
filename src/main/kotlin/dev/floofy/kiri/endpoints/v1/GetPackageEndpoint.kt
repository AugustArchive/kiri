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

package dev.floofy.kiri.endpoints.v1

import dev.floofy.kiri.config.objects.LibraryModule
import dev.floofy.kiri.config.objects.LibraryPOM
import dev.floofy.kiri.config.objects.MavenMetadataPOM
import dev.floofy.kiri.config.objects.responses.GetPackageResponse
import dev.floofy.kiri.config.objects.responses.LibraryVersion
import dev.floofy.kiri.struct.Endpoint
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import java.io.File
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML

class GetPackageEndpoint(
    private val xml: XML,
    private val json: Json
): Endpoint("/api/v1/{package}", HttpMethod.Get) {
    override suspend fun call(call: ApplicationCall) {
        // Use filesystem for now
        val path = call.parameters["package"]
            ?: return call.respondText("where in the fucking hell is the package root at HUH")

        val actualPath = path
            .replace(".", "/")

        val _file = File("C:\\Users\\cutie\\.m2\\repository\\$actualPath")
        if (!_file.isDirectory)
            return call.respondText("uh, no directory was found at $actualPath...")

        // Parse `maven-metadata.xml` / `maven-metadata-local.xml`
        val mavenMetadataPath = File("${_file.absolutePath}\\maven-metadata.xml")
        val mavenMetadataLocal = File("${_file.absolutePath}\\maven-metadata-local.xml")
        val file: File = when {
            mavenMetadataLocal.exists() -> mavenMetadataLocal
            mavenMetadataPath.exists() -> mavenMetadataPath
            else -> null
        } ?: return call.respondText("uh no maven metadata was not found. >:(")

        val mavenMeta = xml.decodeFromString(MavenMetadataPOM.serializer(), file.readText())
        println(mavenMeta)

        // Parse the .module of all directories
        val files = _file.listFiles() ?: emptyArray()

        if (files.isEmpty())
            return call.respondText("unable to find versions. :(")

        val versions = mutableListOf<File>()
        for (file in files) {
            if (file.isDirectory)
                versions.add(file)
        }

        val sources = mutableMapOf<String, LibraryVersion>()

        // Recurse all files
        for (f in versions) {
            // Get version sources
            val mod = f.listFiles()!!.find { it.extension == "module" } ?: continue
            val source = json.decodeFromString(LibraryModule.serializer(), mod.readText())

            // Get POM metadata
            val fi = f.listFiles()!!.find { it.extension == "pom" } ?: continue
            val pom = xml.decodeFromString(LibraryPOM.serializer(), fi.readText())

            sources[f.name] = LibraryVersion(
                module = source,
                pom
            )
        }

        call.respond(GetPackageResponse(
            versions = sources,
            metadata = mavenMeta
        ))
    }
}
