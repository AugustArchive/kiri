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
@XmlSerialName("metadata", namespace = "", prefix = "")
data class MavenMetadataPOM(
    @XmlElement(true)
    val groupId: String,

    @XmlElement(true)
    val artifactId: String,

    @XmlElement(true)
    val versioning: Versioning
)

@Serializable
@SerialName("versioning")
data class Versioning(
    @XmlElement(true) val latest: String,
    @XmlElement(true) val release: String,
    @XmlElement(true) val versions: WithVersionObject,
    @XmlElement(true) @SerialName("lastUpdated") val lastUpdatedAt: String
)

@Serializable
@SerialName("versions")
data class WithVersionObject(
    @XmlElement(true) val version: String
)
