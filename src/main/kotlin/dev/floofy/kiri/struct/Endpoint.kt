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

package dev.floofy.kiri.struct

import io.ktor.application.*
import io.ktor.http.*

/**
 * Represents a endpoint to connect this to the world!
 * @param path The path to use
 * @param method The HTTP method verb to use
 */
abstract class Endpoint(val path: String, val method: HttpMethod) {
    /**
     * Abstract function to control this [Endpoint].
     * @param call The application callee
     */
    abstract suspend fun call(call: ApplicationCall)
}
