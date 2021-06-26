package dev.floofy.kiri.config

import dev.floofy.kiri.services.s3.S3Service
import kotlinx.serialization.Serializable

/**
 * Represents the configuration details for building a [S3Service].
 */
@Serializable
class S3Config {
    /**
     * The access key to authenticate with AWS. If this is not provided,
     * then it'll use your credentials from the system itself.
     */
    val accessKey: String = ""

    /**
     * The secret key to authenticate with AWS. If this is not provided,
     * then it'll use your credentials from the system itself.
     */
    val secretKey: String = ""

    /**
     * `true` if this [s3 client][S3Service] should connect over Wasabi than AWS, otherwise false.
     */
    val wasabi: Boolean = false

    /**
     * The region to use, [view here](https://github.com/aws/aws-sdk-java-v2/blob/master/codegen-lite/src/test/resources/software/amazon/awssdk/codegen/lite/regions/regions.java#L39-L85) for a list of regions.
     */
    val region: String = "us-east-1"

    /**
     * The bucket to use, if the bucket isn't found,
     * then it'll create it.
     */
    val bucket: String = "kiri"
}
