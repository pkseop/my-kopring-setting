package my.kopring.setting.api.client

import mu.KotlinLogging
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.Resource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import javax.annotation.PostConstruct

private val log = KotlinLogging.logger {}

abstract class ApiTemplate {

    private lateinit var restTemplate: RestTemplate

    protected abstract fun host(): String

    @PostConstruct
    fun postConstruct() {
        val requestConfig: RequestConfig = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
            .build()

        val httpClient: CloseableHttpClient = HttpClientBuilder.create()
            .useSystemProperties()
            .setDefaultRequestConfig(requestConfig)
            .build()

        val factory = HttpComponentsClientHttpRequestFactory(httpClient)

        factory.setReadTimeout(30000)
        factory.setConnectTimeout(2000)

        restTemplate = RestTemplate(factory)
    }

    private fun toUrl(paths: Array<String>, queryParams: Map<String, Any>?): String {
        var url = "${this.host()}/${paths.joinToString("/")}"
        queryParams?.let {
            val list = mutableListOf<String>()
            for((key, value) in queryParams) {
                list.add("$key=$value")
            }
            if(list.isNotEmpty()) {
                url = "$url?${list.joinToString("&")}"
            }
        }
        log.debug { "ApiTemplate send URL: $url" }
        return url
    }

    private fun httpEntity(secretKey: String?, body: Any?): HttpEntity<Any?>? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        secretKey?.let { headers["Authorization"] = "Bearer $secretKey" }
        return HttpEntity(body, headers)
    }

    fun <T> exchange(
        secretKey: String? = null,
        paths: Array<String>,
        queryParams: Map<String, String>? = null,
        httpMethod: HttpMethod,
        requestBody: Any? = null,
        clazz: Class<T>
    ): T? {
        val responseEntity: ResponseEntity<T> =
            restTemplate.exchange(
                toUrl(paths, queryParams),
                httpMethod,
                httpEntity(secretKey, requestBody),
                clazz,
            )
        return responseEntity.body
    }

    fun <T> exchange(
        secretKey: String? = null,
        paths: Array<String>,
        queryParams: Map<String, Any>? = null,
        httpMethod: HttpMethod,
        requestBody: Any? = null,
        ptype: ParameterizedTypeReference<T>
    ): T? {
        val responseEntity = restTemplate.exchange(
            toUrl(paths, queryParams),
            httpMethod,
            httpEntity(secretKey, requestBody),
            ptype
        )
        return responseEntity.body
    }

    private fun httpEntityMultipart(secretKey: String?, multipartFiles: Array<MultipartFile>): HttpEntity<Any?>? {
        val map: MultiValueMap<String, Any> = LinkedMultiValueMap()
        val files: MutableList<Resource> = ArrayList()
        for (file in multipartFiles) {
            files.add(file.resource)
        }
        map.put("files", files as List<Any>?)

        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        secretKey?.let { headers["Authorization"] = "Bearer $secretKey" }
        return HttpEntity(map, headers)
    }

    fun <T> exchangeMultipartForm(
        secretKey: String? = null,
        paths: Array<String>,
        queryParams: Map<String, String>? = null,
        files: Array<MultipartFile>,
        ptype: ParameterizedTypeReference<T>
    ): T? {
        val responseEntity = restTemplate.exchange(
            toUrl(paths, queryParams),
            HttpMethod.POST,
            httpEntityMultipart(
                secretKey,
                files!!
            ),
            ptype
        )
        return responseEntity.body
    }
}