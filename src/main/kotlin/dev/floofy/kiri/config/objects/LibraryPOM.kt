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

package dev.floofy.kiri.config.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("project", namespace = "http://maven.apache.org/POM/4.0.0", prefix = "")
data class LibraryPOM(
    @XmlElement(true)
    val modelVersion: String,

    @XmlElement(true)
    val groupId: String,

    @XmlElement(true)
    val artifactId: String,

    @XmlElement(true)
    val version: String,

    @XmlElement(true)
    val name: String,

    @XmlElement(true)
    val description: String,

    @XmlElement(true)
    val url: String,

    @XmlElement(true)
    val licenses: List<License>,

    @XmlElement(true)
    val developers: List<Developer>,

    @XmlElement(true)
    val dependencies: List<Dependency>
) {
    @Serializable
    @SerialName("license")
    data class License(
        val name: String,
        val url: String
    )

    @Serializable
    @SerialName("developer")
    data class Developer(
        val id: String,
        val name: String,
        val email: String
    )

    @Serializable
    @SerialName("dependency")
    data class Dependency(
        val groupId: String,
        val artifactId: String,
        val version: String,
        val scope: DependencyScope
    )
}

@Serializable
enum class DependencyScope {
    @SerialName("runtime")
    @Suppress("UNUSED")
    Runtime
}
