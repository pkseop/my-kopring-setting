package biz.gripcloud.admin.encrypt

import org.hashids.Hashids

class PasswordGenerator {

    companion object{
        private val USE_CHARS  = "abcdefghijklmnopqrstuvwxyz1234567890"
        private val SALT = "what is your group"

        fun generate(userSeq: Long,  minLength: Int):String{
            val hashids: Hashids = Hashids(SALT, minLength, USE_CHARS)
            val password = "$2a$" + hashids.encode(userSeq, System.currentTimeMillis() / 1000)

            return if(password.length > minLength){
                password.substring(0, minLength)
            }else{
                password
            }
        }
    }

}