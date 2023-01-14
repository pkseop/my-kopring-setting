package my.kopring.setting.model

data class ResponseModel<T> (
    val status: Int,
    val message: String,
    var result: T? = null
) {
}