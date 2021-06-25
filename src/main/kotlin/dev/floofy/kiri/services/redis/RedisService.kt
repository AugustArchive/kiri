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

package dev.floofy.kiri.services.redis

import dev.floofy.kiri.config.Config
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.reactive.RedisReactiveCommands
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class RedisService(
    val config: Config,
    val json: Json
): IRedisService {
    override lateinit var commands: RedisReactiveCommands<String, String>
    private lateinit var connection: StatefulRedisConnection<String, String>
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val url: RedisURI = RedisURI().let {
        if (config.redis.sentinels?.isNotEmpty() == true) {
            it.sentinels += config.redis.sentinels.map { s ->
                RedisURI.create(s.host, s.port)
            }

            if (config.redis.password != null) {
                it.password = config.redis.password.toCharArray()
            }

            if (config.redis.master != null) {
                it.sentinelMasterId = config.redis.master
            }

            return@let it
        }

        it.host = config.redis.host
        it.port = config.redis.port
        it.database = config.redis.index
        it.clientName = ""

        if (config.redis.password != null) {
            it.password = config.redis.password.toCharArray()
        }

        it
    }

    private val client = RedisClient.create(url)

    override fun connect() {
        connection = client.connect()
        commands = connection.reactive()
    }

    override fun close() {
        connection.close()
    }
}
