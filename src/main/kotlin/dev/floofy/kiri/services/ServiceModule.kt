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

package dev.floofy.kiri.services

import dev.floofy.kiri.config.Config
import dev.floofy.kiri.services.redis.IRedisService
import dev.floofy.kiri.services.redis.RedisService
import dev.floofy.kiri.services.s3.IS3Service
import dev.floofy.kiri.services.s3.createS3Service
import org.koin.dsl.module

val serviceModule = module {
    single<IRedisService> { RedisService(get(), get()) }
    single<IS3Service> {
        val config = get<Config>()
        createS3Service {
            accessKey = config.s3.accessKey
            secretKey = config.s3.secretKey
            bucket = config.s3.bucket
            region = config.s3.region
            wasabi = config.s3.wasabi
        }
    }
}
