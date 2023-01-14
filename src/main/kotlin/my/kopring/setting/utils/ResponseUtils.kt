package my.kopring.setting.utils

import my.kopring.setting.model.ResponseModel
import my.kopring.setting.model.ResponseWithPageModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ResponseUtils {
    fun<T> success(): ResponseEntity<ResponseModel<T>> {
        return successMessage("OK")
    }

    fun<T> successMessage(message: String): ResponseEntity<ResponseModel<T>> {
        return ResponseEntity<ResponseModel<T>> (
            ResponseModel(
                status = HttpStatus.OK.value(),
                message = message
            ),
            HttpStatus.OK
        )
    }

    fun<T> successResult(result: T?): ResponseEntity<ResponseModel<T>> {
        return successMessageResult("OK", result)
    }

    fun<T> successMessageResult(message: String, result: T?): ResponseEntity<ResponseModel<T>> {
        return ResponseEntity<ResponseModel<T>> (
            ResponseModel(
                status = HttpStatus.OK.value(),
                message = message,
                result = result
            ),
            HttpStatus.OK
        )
    }

    fun<T> successResult(responseModel: ResponseModel<T>?) : ResponseEntity<ResponseModel<T>?> {
        return ResponseEntity<ResponseModel<T>?> (responseModel, HttpStatus.OK)
    }

    fun<T> successResult(responseModel: ResponseWithPageModel<T>?): ResponseEntity<ResponseWithPageModel<T>?> {
        return ResponseEntity<ResponseWithPageModel<T>?> (responseModel, HttpStatus.OK)
    }
}