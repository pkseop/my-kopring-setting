package biz.gripcloud.api.cache.kryo

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import com.esotericsoftware.kryo.util.Pool
import mu.KotlinLogging
import org.objenesis.strategy.StdInstantiatorStrategy
import java.io.ByteArrayOutputStream

private val log = KotlinLogging.logger {}

class KryoHelper// Configure the Kryo instance.
    () {
    var kryoPool: Pool<Kryo>? = null
    var inputPool: Pool<Input>? = null
    var outputPool: Pool<Output>? = null

    init {
        kryoPool = object : Pool<Kryo>(true, true) {
            override fun create(): Kryo {
                val kryo = Kryo()
                // Configure the Kryo instance.
                kryo.isRegistrationRequired = false
                kryo.classLoader = Thread.currentThread().contextClassLoader
                kryo.instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
                return kryo
            }
        }
        inputPool = object : Pool<Input>(true, true) {
            override fun create(): Input {
                return Input()
            }
        }
        outputPool = object : Pool<Output>(true, true) {
            override fun create(): Output {
                return Output(4096)
            }
        }
    }

    fun <T> readObject(bytes: ByteArray, classType: Class<T>?): T? {
        val callback: (Kryo, Input) -> T = { kryo, input ->  kryo.readObject(input, classType) }
        return read(callback, bytes)
    }

    fun <T> readObjectOrNull(bytes: ByteArray?, classType: Class<T>?): T? {
        val callback: (Kryo, Input) -> T = { kryo, input -> kryo.readObjectOrNull(input, classType) }
        return read(callback, bytes!!)
    }

    fun readClassAndObject(bytes: ByteArray?): Any? {
        val callback: (Kryo, Input) -> Any? =  { kryo, input -> kryo.readClassAndObject(input) }
        return read(callback, bytes!!)
    }

    fun writeObject(obj: Any): ByteArray? {
        val callback: (Kryo, Output) -> Unit = { kryo, output -> kryo.writeObject(output, obj) }
        return write(callback, obj)
    }

    fun writeObjectOrNull(obj: Any): ByteArray? {
        val callback: (Kryo, Output) -> Unit = { kryo, output -> kryo.writeObjectOrNull(output, obj, obj.javaClass) }
        return write(callback, obj)
    }

    fun writeClassAndObject(obj: Any?): ByteArray? {
        val callback: (Kryo, Output) -> Unit = { kryo, output -> kryo.writeClassAndObject(output, obj) }
        return write(callback, obj)
    }

    private fun write(callback: (Kryo, Output) -> Unit, obj: Any?): ByteArray? {
        var kryo: Kryo? = null
        var output: Output? = null
        var bytes: ByteArray?
        try {
            kryo = kryoPool!!.obtain()
            output = outputPool!!.obtain()

            val outputStream = ByteArrayOutputStream()
            output.outputStream = outputStream

            callback(kryo, output)
            output.flush()
            bytes = outputStream.toByteArray()
        } finally {
            output?.let {
                outputPool!!.free(output)
            }
            kryo?.let {
                kryoPool!!.free(kryo)
            }
        }
        return bytes
    }

    private fun <T> read(callback: (Kryo, Input) -> T, bytes: ByteArray): T? {
        var kryo: Kryo? = null
        var input: Input? = null
        var result: T?
        try {
            kryo = kryoPool!!.obtain()
            input = inputPool!!.obtain()

            input.buffer = bytes

            result = callback(kryo, input)
        } finally {
            input?.let {
                inputPool!!.free(input)
            }
            kryo?.let {
                kryoPool!!.free(kryo)
            }
        }
        return result
    }
}