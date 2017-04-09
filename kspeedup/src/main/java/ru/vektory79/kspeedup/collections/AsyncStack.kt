package ru.vektory79.kspeedup.collections

import ru.vektory79.kspeedup.async.ReadWriteSpinLock
import java.util.concurrent.atomic.AtomicInteger

internal class AsyncStackBuffer<T : Any>(val initialCapacity: Int = 16, val capacityIncrement: Int = initialCapacity) {
    private val spinLock = ReadWriteSpinLock()

    @Suppress("CAST_NEVER_SUCCEEDS")
    private var buffer = arrayOfNulls<Any>(initialCapacity) as Array<T>

    private var head = AtomicInteger(-1)

    val size: Int
        get() = head.get()
    val capacity: Int
        get() = buffer.size

    fun fill(value: T) {
        val h = head.incrementAndGet()

        spinLock { writeLock ->
            if (h >= buffer.size) {
                writeLock {
                    var b = buffer
                    var inc = capacityIncrement
                    while (h >= b.size + inc) {
                        inc += capacityIncrement
                    }
                    @Suppress("CAST_NEVER_SUCCEEDS")
                    val newBuffer = arrayOfNulls<Any>(b.size + inc) as Array<T>
                    System.arraycopy(b, 0, newBuffer, 0, b.size)
                    b = newBuffer
                    buffer = b
                }
            }
            buffer[h] = value
        }
    }

    fun drain(): T? {
        return spinLock {
            val h = head.getAndUpdate { prev ->
                if (prev >= 0) prev - 1 else -1
            }
            var result: T? = null
            if (h >= 0) {
                result = buffer[h] as T?
            }
            result
        }
    }
}

class AsyncStack<T : Any>(val initialCapacity: Int = 16, val capacityIncrement: Int = initialCapacity) {
    private val spinLock = ReadWriteSpinLock()

    private var up = AsyncStackBuffer<T>(initialCapacity, capacityIncrement)
    private var down = AsyncStackBuffer<T>(initialCapacity, capacityIncrement)

    fun clear() {
        up = AsyncStackBuffer<T>(initialCapacity, capacityIncrement)
        down = AsyncStackBuffer<T>(initialCapacity, capacityIncrement)
    }

    fun push(value: T) {
        spinLock {
            down.fill(value)
        }
    }

    fun pop(): T? {
        return spinLock { writeLock ->
            var result: T? = up.drain()
            if (result == null && down.size >= (up.capacity shr 2)) {
                writeLock.invoke {
                    val tmp = up
                    up = down
                    down = tmp
                    null
                }
                result = up.drain()
            }
            result
        }
    }
}
