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
