package my.kopring.setting.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config(
    @Value("\${aws.access.key}")
    private var accessKey: String,

    @Value("\${aws.secret.key}")
    private val secretKey: String,

    @Value("\${aws.s3.region}")
    private var region: String) {

    @Bean
    fun amazonS3Client(): AmazonS3 {
        val awsCreds = BasicAWSCredentials(accessKey, secretKey)
        return AmazonS3ClientBuilder.standard()
            .withRegion(Regions.fromName(region))
            .withCredentials(AWSStaticCredentialsProvider(awsCreds))
            .build()
    }
}