package dev.floofy.kiri.endpoints

import dev.floofy.kiri.struct.Endpoint
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

class HealthEndpoint: Endpoint("/", HttpMethod.Get) {
    override suspend fun call(call: ApplicationCall) {
        call.respondText("OK!")
    }
}
