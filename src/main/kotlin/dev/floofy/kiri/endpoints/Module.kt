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

package dev.floofy.kiri.endpoints

import dev.floofy.kiri.endpoints.v1.GetPackageEndpoint
import dev.floofy.kiri.struct.Endpoint
import org.koin.dsl.bind
import org.koin.dsl.module

val endpointsModule = module {
    single { GetPackageEndpoint(get(), get()) } bind(Endpoint::class)
    single { PutObjectsEndpoint() } bind(Endpoint::class)
    single { HealthEndpoint() } bind(Endpoint::class)
    single { MainEndpoint() } bind(Endpoint::class)
}
