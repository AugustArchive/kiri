package dev.floofy.kiri.services.s3

import java.io.InputStream
import java.util.concurrent.CompletableFuture

interface IS3Service {
    fun handle(stream: InputStream, contentType: String): CompletableFuture<Void>
}
