package my.kopring.setting.utils

import my.kopring.setting.model.PagingResultModel
import my.kopring.setting.model.ResponseModel
import my.kopring.setting.model.ResponseWithListModel
import my.kopring.setting.model.ResponseWithPageModel
import org.springframework.core.ParameterizedTypeReference
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


object PTypeUtils {
    fun<T> response(clazz: Class<T>): ParameterizedTypeReference<ResponseModel<T>> {
        return object : ParameterizedTypeReference<ResponseModel<T>>() {
            override fun getType(): Type {
                return CustomParameterizedTypeImpl(super.getType() as ParameterizedType, arrayOf(clazz))
            }
        }
    }

    fun<T> listResponse(clazz: Class<T>): ParameterizedTypeReference<ResponseWithListModel<T>> {
        return object : ParameterizedTypeReference<ResponseWithListModel<T>>() {
            override fun getType(): Type {
                return CustomParameterizedTypeImpl(super.getType() as ParameterizedType, arrayOf(clazz))
            }
        }
    }

    fun<T> pagingModelResponse(clazz: Class<T>): ParameterizedTypeReference<PagingResultModel<T>> {
        return object : ParameterizedTypeReference<PagingResultModel<T>>() {
            override fun getType(): Type {
                return CustomParameterizedTypeImpl(super.getType() as ParameterizedType, arrayOf(clazz))
            }
        }
    }

    fun<T> list(clazz: Class<T>): ParameterizedTypeReference<List<T>> {
        return object : ParameterizedTypeReference<List<T>>() {
            override fun getType(): Type {
                return CustomParameterizedTypeImpl(super.getType() as ParameterizedType, arrayOf(clazz))
            }
        }
    }

    fun<T> pagingResponse(clazz: Class<T>): ParameterizedTypeReference<ResponseWithPageModel<T>> {
        return object : ParameterizedTypeReference<ResponseWithPageModel<T>>() {
            override fun getType(): Type {
                return CustomParameterizedTypeImpl(super.getType() as ParameterizedType, arrayOf(clazz))
            }
        }
    }
}

internal class CustomParameterizedTypeImpl(
    private val delegate: ParameterizedType,
    private val actualTypeArgs: Array<Type>
) : ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> {
        return actualTypeArgs
    }

    override fun getRawType(): Type {
        return delegate.rawType
    }

    override fun getOwnerType(): Type {
        return delegate.ownerType
    }

}