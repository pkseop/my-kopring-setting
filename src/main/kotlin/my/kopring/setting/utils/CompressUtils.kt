package my.kopring.batch.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object CompressUtils {
    fun gzipFile(src: File?, dest: File?) {
        val buffer = ByteArray(4096)
        try {
            FileOutputStream(dest).use { fos ->
                GZIPOutputStream(fos).use { zos ->
                    FileInputStream(src).use { fis ->
                        var nread: Int
                        while (fis.read(buffer).also { nread = it } >= 0) {
                            zos.write(buffer, 0, nread)
                        }
                    }
                }
            }
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }


    fun unGunzipFile(src: File, dest: File) {
        val buffer = ByteArray(4096)
        try {
            FileInputStream(src).use { fis ->
                GZIPInputStream(fis).use { zis ->
                    FileOutputStream(dest).use { fos ->
                        var nread: Int
                        while (zis.read(buffer).also { nread = it } >= 0) {
                            fos.write(buffer, 0, nread)
                        }
                    }
                }
            }
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }
}