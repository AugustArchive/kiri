package dev.floofy.kiri.endpoints.v1

import dev.floofy.kiri.struct.Endpoint
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

class GetPackageEndpoint: Endpoint("/api/v1/{package}", HttpMethod.Get) {
    override suspend fun call(call: ApplicationCall) {
        call.respondText("You used ${call.parameters["package"]} to fetch a package I guess.")
    }
}
