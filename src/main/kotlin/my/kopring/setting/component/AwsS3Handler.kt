package my.kopring.setting.component

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import mu.KotlinLogging
import org.apache.tika.Tika
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.InputStream

private val log = KotlinLogging.logger {}

@Component
class AwsS3Handler(
    private val s3client: AmazonS3
    ) {

    fun getMimeType(filename: String?): String? {
        var mimeType: String?
        try {
            val tika = Tika()
            mimeType = tika.detect(filename)
            mimeType ?: return "application/octet-stream"
        } catch (e: Exception) {
            log.warn("get mime type failed.", e)
            mimeType = "application/octet-stream"
        }
        return mimeType
    }

    fun upload(bucketName: String, key: String, data: ByteArray): PutObjectResult {
        val omd = ObjectMetadata()
        omd.contentLength = data.size.toLong()
        omd.contentType = getMimeType(key)
        return upload(bucketName, omd, key, data)
    }

    @Async("threadPoolTaskExecutor")
    fun uploadAsync(bucketName: String, key: String, data: ByteArray) {
        val omd = ObjectMetadata()
        omd.contentLength = data.size.toLong()
        omd.contentType = getMimeType(key)
        upload(bucketName, omd, key, data)
    }

    private fun upload(bucketName: String, omd: ObjectMetadata, key: String, data: ByteArray): PutObjectResult {
        val stream = ByteArrayInputStream(data)
        val putObjectRequest = PutObjectRequest(bucketName, key, stream, omd)
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead // file permission
        return s3client.putObject(putObjectRequest) // upload file
    }

    fun upload(bucketName: String, key: String, inputStream: InputStream, size: Long): PutObjectResult {
        val omd = ObjectMetadata()
        omd.contentLength = size
        omd.contentType = getMimeType(key)
        return upload(bucketName, omd, key, inputStream)
    }

    fun uploadAsOctetStream(
        bucketName: String,
        key: String,
        inputStream: InputStream,
        size: Long
    ): PutObjectResult? {
        val omd = ObjectMetadata()
        omd.contentLength = size
        omd.contentType = "application/octet-stream"
        return upload(bucketName, omd, key, inputStream)
    }

    @Async("threadPoolTaskExecutor")
    open fun uploadAsync(bucketName: String, key: String, inputStream: InputStream, size: Long) {
        val omd = ObjectMetadata()
        omd.contentLength = size
        omd.contentType = getMimeType(key)
        upload(bucketName, omd, key, inputStream)
    }

    private fun upload(
        bucketName: String,
        omd: ObjectMetadata,
        key: String,
        inputStream: InputStream
    ): PutObjectResult {
        val putObjectRequest = PutObjectRequest(bucketName, key, inputStream, omd)
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead // file permission
        return s3client.putObject(putObjectRequest) // upload file
    }

    fun copyObject(bucketName: String, sourceKey: String, destKey: String): CopyObjectResult {
        val copyObjectRequest = CopyObjectRequest(bucketName, sourceKey, bucketName, destKey)
        copyObjectRequest.cannedAccessControlList = CannedAccessControlList.PublicRead
        return s3client.copyObject(copyObjectRequest)
    }

    fun copyObject(copyObjectRequest: CopyObjectRequest): CopyObjectResult {
        return s3client.copyObject(copyObjectRequest)
    }

    fun getObject(bucketName: String, key: String): S3Object {
        return s3client.getObject(bucketName, key)
    }

    fun listObjects(bucketName: String, maxKeys: Int): ObjectListing {
        val listObjectsRequest = ListObjectsRequest()
            .withBucketName(bucketName)
            .withMaxKeys(maxKeys)
        return s3client.listObjects(listObjectsRequest)
    }

    fun listObjects(bucketName: String, prefix: String, maxKeys: Int): ObjectListing {
        val listObjectsRequest = ListObjectsRequest()
            .withBucketName(bucketName)
            .withPrefix(prefix)
            .withMaxKeys(maxKeys)
        return s3client.listObjects(listObjectsRequest)
    }

    fun listNextBatchOfObjects(prevObjectListing: ObjectListing): ObjectListing {
        return s3client.listNextBatchOfObjects(prevObjectListing)
    }

    fun deleteObject(bucketName: String, key: String) {
        s3client.deleteObject(bucketName, key)
    }

    fun getObjectMetadata(bucketName: String, key: String): ObjectMetadata {
        return s3client.getObjectMetadata(bucketName, key)
    }

    fun doesObjectExists(bucketName: String, key: String): Boolean {
        return s3client.doesObjectExist(bucketName, key)
    }
}