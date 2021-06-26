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

package dev.floofy.kiri.services.s3

import java.io.InputStream
import java.net.URI
import java.util.concurrent.CompletableFuture
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.Bucket

/**
 * Represents the configuration details for building a [S3Service].
 */
class S3Configuration {
    /**
     * The access key to authenticate with AWS. If this is not provided,
     * then it'll use your credentials from the system itself.
     */
    var accessKey: String = ""

    /**
     * The secret key to authenticate with AWS. If this is not provided,
     * then it'll use your credentials from the system itself.
     */
    var secretKey: String = ""

    /**
     * `true` if this [s3 client][S3Service] should connect over Wasabi than AWS, otherwise false.
     */
    var wasabi: Boolean = false

    /**
     * The region to use, [view here](https://github.com/aws/aws-sdk-java-v2/blob/master/codegen-lite/src/test/resources/software/amazon/awssdk/codegen/lite/regions/regions.java#L39-L85) for a list of regions.
     */
    var region: String = "us-east-1"

    /**
     * The bucket to use, if the bucket isn't found,
     * then it'll create it.
     */
    var bucket: String = "kiri"
}

/**
 * Creates a new [S3Service] with a [configuration block][block].
 * @param block The configuration block to use when creating this [s3 client][S3Service].
 */
fun createS3Service(block: S3Configuration.() -> Unit): S3Service {
    val config = S3Configuration().apply(block)
    return S3Service(config)
}

class S3Service(private val config: S3Configuration): IS3Service {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var bucket: Bucket
    private lateinit var client: S3Client

    override fun init() {
        logger.info("Initializing S3 client...")
        val builder = S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsSessionCredentials.create(
                    config.accessKey,
                    config.secretKey,
                    ""
                )
            )).region(if (config.region == "") Region.US_EAST_1 else Region.of(config.region))

        if (config.wasabi)
            builder.endpointOverride(URI.create("https://s3.wasabisys.com"))

        client = builder.build()
        logger.info("S3 client has been built, validating buckets...")

        val buckets = client.listBuckets().buckets()
        val foundBucket = buckets.find {
            it.name() == config.bucket
        }

        if (foundBucket == null) {
            logger.warn("Bucket with name ${config.bucket} was not found, creating!")
            try {
                client.createBucket {
                    it.bucket(config.bucket)
                }

                bucket = client.listBuckets().buckets().find { it.name() == config.bucket }!!
                logger.info("Created bucket ${config.bucket}.")
            } catch (e: Exception) {
                logger.warn("Unable to create bucket with name ${config.bucket}", e)
                throw e
            }
        } else {
            logger.info("Using bucket ${config.bucket}.")
            bucket = foundBucket
        }

        logger.info("✔ Initialized storage bucket with ${if (config.wasabi) "Wasabi" else "AWS S3"}.")
    }

    override fun handle(stream: InputStream, contentType: String): CompletableFuture<Void> {
        return CompletableFuture()
    }
}
