package my.kopring.batch.utils

import com.google.gson.GsonBuilder
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.nio.charset.Charset

object JsonFileUtils {

    @Throws(IOException::class)
    fun toJsonFile(`object`: Any, file: File) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        FileWriter(file).use { fw ->
            gson.toJson(`object`, fw)
            fw.flush()
        }
    }

    @Throws(IOException::class)
    private fun readAll(rd: Reader): String {
        val sb = StringBuilder()
        var cp: Int
        while (rd.read().also { cp = it } != -1) {
            sb.append(cp.toChar())
        }
        return sb.toString()
    }

    @Throws(IOException::class, JSONException::class)
    fun readJsonFromUrl(url: String): JSONObject {
        val `is` = URL(url).openStream()
        return try {
            val rd =
                BufferedReader(InputStreamReader(`is`, Charset.forName("UTF-8")))
            val jsonText = readAll(rd)
            JSONObject(jsonText)
        } finally {
            `is`.close()
        }
    }
}