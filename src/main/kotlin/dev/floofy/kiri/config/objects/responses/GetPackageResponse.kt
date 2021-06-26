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

package dev.floofy.kiri.config.objects.responses

import dev.floofy.kiri.config.objects.LibraryModule
import dev.floofy.kiri.config.objects.LibraryPOM
import dev.floofy.kiri.config.objects.MavenMetadataPOM
import kotlinx.serialization.Serializable

@Serializable
data class GetPackageResponse(
    val versions: Map<String, LibraryVersion>,
    val metadata: MavenMetadataPOM
)

@Serializable
data class LibraryVersion(
    val module: LibraryModule,
    val pom: LibraryPOM
)
