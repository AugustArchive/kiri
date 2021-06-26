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

package dev.floofy.kiri.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val environment: Environment = Environment.Development,
    val metrics: MetricsConfig = MetricsConfig(),
    val redis: RedisConfig = RedisConfig(),
    val users: List<UsernamePasswordConfig> = listOf(),
    val host: String? = null,
    val s3: S3Config
)

@Serializable
data class UsernamePasswordConfig(
    val username: String,
    val password: String
)
