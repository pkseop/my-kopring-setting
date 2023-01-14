package my.kopring.setting.model

class ResponseWithPageModel<T>(
    val status: Int,
    val message: String,
    var result: PagingResultModel<T>? = null
) {
}