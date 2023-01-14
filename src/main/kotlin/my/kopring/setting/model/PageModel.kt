package my.kopring.setting.model

class PageModel(
    val page: Long,
    val limit: Long,
    val count: Long,
    val totalCount: Long
) {
    var totalPages: Long = 0L
        get() {
            var pages = if (totalCount > 0) 1L else 0L
            if (totalCount > 0 && limit > 0) {
                pages = totalCount / limit
                if (totalCount % limit > 0) pages += 1
            }
            return pages
        }
}