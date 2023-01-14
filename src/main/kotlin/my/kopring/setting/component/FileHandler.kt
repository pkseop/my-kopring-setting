package my.kopring.setting.component

import my.kopring.setting.model.FileModel
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.hashids.Hashids
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import javax.annotation.PostConstruct

@Component
class FileHandler(
    @Value("\${aws.s3.bucket.name}")
    private val bucketName: String,

    private val awsS3Handler: AwsS3Handler,

    @Value("\${domain.static}")
    private val domainStatic: String,

    @Value("\${common.local.temp.path}")
    private val localTempPath: String
) {

    companion object {
        private const val TEMP_DIR = "temp"
        private const val ONE_DAY: Long = 24 * 60 * 60 * 1000
        private const val TWO_DAY: Long = 2 * ONE_DAY
    }

    lateinit var localtempDir: File
    private val HASH_IDS: Hashids = Hashids("grip cloud file", 8, "abcdefghijklmnopqrstuvwxyz1234567890")

    @PostConstruct
    fun postConstruct() {
        localtempDir = File(localTempPath)
        if (!localtempDir.exists()) {
            if (!localtempDir.mkdirs()) {
                throw RuntimeException("mkdirs failed: $localTempPath")
            }
        }
    }

    @Scheduled(fixedDelay = ONE_DAY, initialDelay = ONE_DAY)
    fun removeTemporaryFiles() {
        if (localtempDir == null) {
            return
        }
        removeDir(localtempDir)
    }

    fun removeDir(dir: File) {
        val files = dir.listFiles()
        if (files == null || files.size == 0) {
            return
        }
        for (file in files) {
            try {
                val attr = Files.readAttributes(
                    file.toPath(),
                    BasicFileAttributes::class.java
                )
                if (attr.creationTime().toMillis() < System.currentTimeMillis() - TWO_DAY) {
                    if (file.isDirectory) {
                        removeDir(file)
                    }
                    file.delete()
                }
            } catch (ex: java.lang.Exception) {
                // do nothing
            }
        }
    }

    fun toTemp(file: File, fileName: String): FileModel {
        var ext: String = FilenameUtils.getExtension(file.name)
        var key: String = "$TEMP_DIR/$fileName"

        try {
            var input: InputStream = FileInputStream(file)
            upload(key, input, file.length())
        } catch (e: Exception){
            throw e
        }

        val url: String = String.format("%s/%s", domainStatic, key)
        return FileModel(url, fileName, FilenameUtils.getName(file.name), ext)
    }

    fun upload(key: String, input: InputStream, size: Long) {
        try {
            awsS3Handler.upload(bucketName, key, input, size)
        } finally {
            IOUtils.closeQuietly(input)
        }
    }

    fun exists(key: String): Boolean {
        return awsS3Handler.doesObjectExists(bucketName, key)
    }
}