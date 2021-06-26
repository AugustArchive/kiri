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

package dev.floofy.kiri

import dev.floofy.kiri.config.Config
import dev.floofy.kiri.config.Environment
import dev.floofy.kiri.config.dataModule
import dev.floofy.kiri.endpoints.endpointsModule
import dev.floofy.kiri.services.redis.IRedisService
import dev.floofy.kiri.services.s3.IS3Service
import dev.floofy.kiri.services.serviceModule
import dev.floofy.kiri.struct.Endpoint
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.util.concurrent.TimeUnit
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.environmentProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Bootstrap {
    private lateinit var service: NettyApplicationEngine
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        Runtime.getRuntime().addShutdownHook(Thread(::shutdown).apply { name = "Kiri-ShutdownThread" })
    }

    private fun shutdown() {
        logger.warn("Requested to shutdown.")

        val redis = GlobalContext.get().get<IRedisService>()
        redis.close()
        service.stop(0, 0, TimeUnit.MILLISECONDS)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        Thread.currentThread().name = "Kiri-StartupThread"
        logger.info("Launching Kiri!")

        // Start-up Koin
        startKoin {
            environmentProperties()
            modules(
                kiriModule,
                dataModule,
                serviceModule,
                endpointsModule
            )
        }

        // Grab endpoints and such
        val koin = GlobalContext.get()
        val redis = koin.get<IRedisService>()
        val config = koin.get<Config>()
        val s3 = koin.get<IS3Service>()

        logger.info("Connecting to Redis!")
        redis.connect()

        logger.info("Connected to Redis, checking S3 bucket...")
        s3.init()

        // Start up Ktor
        val environment = applicationEngineEnvironment {
            this.developmentMode = config.environment == Environment.Development
            this.log = LoggerFactory.getLogger("dev.floofy.kiri.ktor.Application")

            connector {
                host = config.host ?: "0.0.0.0"
                port = 9921
            }

            module {
                install(ContentNegotiation) {
                    json(koin.get())
                }

                install(DefaultHeaders) {
                    header("X-Powered-By", "Kiri (+https://github.com/auguwu/Kiri; v0.0.0-development.0)")
                }

                val promRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

                if (config.metrics.enabled) {
                    logger.info("Enabling metrics... (defined by config)")
                    install(MicrometerMetrics) {
                        registry = promRegistry
                        meterBinders = if (config.metrics.includeJVMStats)
                            listOf(
                                JvmMemoryMetrics(),
                                JvmGcMetrics(),
                                JvmThreadMetrics(),
                                ProcessorMetrics()
                            )
                        else
                            listOf(
                                ProcessorMetrics()
                            )
                    }
                }

                // Install routes
                val endpoints = koin.getAll<Endpoint>()
                install(Routing) {
                    for (endpoint in endpoints) {
                        route(endpoint.path, endpoint.method) {
                            handle { endpoint.call(call) }
                        }
                    }

                    if (config.metrics.enabled) {
                        get("/metrics") {
                            call.respondText(promRegistry.scrape())
                        }
                    }
                }
            }
        }

        logger.info("Ktor has been booted up.")
        service = embeddedServer(Netty, environment)

        service.start(wait = true)
    }
}
