package de.singing4peace.videogenerator.config

import org.springframework.beans.factory.annotation.Autowired
import io.minio.MinioClient
import com.jlefebure.spring.boot.minio.MinioConfigurationProperties
import javax.annotation.PostConstruct
import io.minio.SetBucketPolicyArgs
import de.singing4peace.videogenerator.config.MinioConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import java.lang.Exception

/**
 * @author Daniel Nägele
 */
@Configuration
class MinioConfig @Autowired constructor(private val minioClient: MinioClient, private val configurationProperties: MinioConfigurationProperties) {

    private val log = LoggerFactory.getLogger(MinioConfig::class.java)

    // This access policy allows the public retrieval of objects
    private val accessPolicy: String =
        """
                            {
                                "Version": "2012-10-17",
                                "Statement": [
                                    {
                                        "Action": "s3:GetObject",
                                        "Effect": "Allow",
                                        "Principal": "*",
                                        "Resource": "arn:aws:s3:::{bucket}/**"
                                    }
                                ]
                            }                
            """.trimIndent()

    @PostConstruct
    fun makeBucketPublicAccessible() {
        val policyArgs = SetBucketPolicyArgs.builder()
            .bucket(configurationProperties.bucket)
            .config(accessPolicy.replace("\\{bucket}".toRegex(), configurationProperties.bucket))
            .build()
        try {
            minioClient.setBucketPolicy(policyArgs)
        } catch (e: Exception) {
            log.error("Could not set public access bucket policy, reason: {}", e.message)
            e.printStackTrace()
        }
    }
}