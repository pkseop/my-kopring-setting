package my.kopring.setting.utils

import com.github.ksuid.Ksuid

object IdGenUtils {
    fun generate(prefix: String): String {
        val rand = Ksuid.newKsuid().toString()
        return "${prefix}_$rand"
    }

    fun generate(): String {
        return Ksuid.newKsuid().toString()
    }

    fun generate(length: Int): String {
        val rand = Ksuid.newKsuid().toString()
        return rand.substring(0, length)
    }
}

//fun main() {
//    println(IdGenUtils.generate("prod"))
//}
