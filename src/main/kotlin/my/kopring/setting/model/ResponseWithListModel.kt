package my.kopring.setting.model

class ResponseWithListModel<T>(
    val status: Int,
    val message: String,
    var result: List<T>? = null
) {

}