package my.kopring.setting.api.client

//import biz.gripcloud.admin.entity.ServiceSecret
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile


private val log = KotlinLogging.logger {}

@Component
class CloudApiTemplate(
    @Value("\${api.server.host}") private val domainApi: String
) : ApiTemplate() {
    companion object {
        const val USER_API_PATH = "v1/users"
    }

    override fun host(): String {
        return domainApi
    }

//    fun <T> exchange(
//        serviceSecret: ServiceSecret,
//        paths: Array<String>,
//        queryParams: Map<String, String>? = null,
//        httpMethod: HttpMethod,
//        requestBody: Any? = null,
//        clazz: Class<T>
//    ): T? {
//        val apiPaths = arrayOf(serviceSecret.accessKey).plus(paths)
//        return this.exchange(
//            secretKey = serviceSecret.secretKey,
//            paths = apiPaths,
//            queryParams = queryParams,
//            httpMethod = httpMethod,
//            requestBody = requestBody,
//            clazz = clazz
//        )
//    }
//
//    fun <T> exchange(
//        serviceSecret: ServiceSecret,
//        paths: Array<String>,
//        queryParams: Map<String, Any>? = null,
//        httpMethod: HttpMethod,
//        requestBody: Any? = null,
//        ptype: ParameterizedTypeReference<T>
//    ): T? {
//        val apiPaths = arrayOf(serviceSecret.accessKey).plus(paths)
//        return this.exchange(
//            secretKey = serviceSecret.secretKey,
//            paths = apiPaths,
//            queryParams = queryParams,
//            httpMethod = httpMethod,
//            requestBody = requestBody,
//            ptype = ptype
//        )
//    }
//
//    fun <T> exchangeMultipartForm(
//        serviceSecret: ServiceSecret,
//        paths: Array<String>,
//        queryParams: Map<String, String>?,
//        files: Array<MultipartFile>,
//        ptype: ParameterizedTypeReference<T>
//    ): T? {
//        val apiPaths = arrayOf(serviceSecret.accessKey).plus(paths)
//        return this.exchangeMultipartForm(
//            secretKey = serviceSecret.secretKey,
//            paths = apiPaths,
//            queryParams = queryParams,
//            files = files,
//            ptype = ptype
//        )
//    }
}