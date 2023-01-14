package my.kopring.setting.model

class PagingResultModel<T> (
    val data: List<T>,
    val page: PageModel
) {
}