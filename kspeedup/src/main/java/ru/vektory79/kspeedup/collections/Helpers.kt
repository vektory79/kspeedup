package ru.vektory79.kspeedup.collections

import gnu.trove.map.hash.TIntObjectHashMap
import ru.vektory79.kspeedup.async.ReadWriteSpinLock
import java.util.Deque
import java.util.Map
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal inline fun <E> Deque<E>.poll(factory: () -> E): E {
    val result: E? = this.poll()
    if (result != null) {
        return result;
    }
    return factory()
}

internal inline fun <E> Deque<E>.poll(lock: ReentrantReadWriteLock, factory: () -> E): E {
    val result: E? = lock.read {
        this.poll()
    }
    if (result != null) {
        return result;
    }
    return factory()
}

inline fun <K, V> MutableMap<K, V>.getOrPut(key: K, lock: ReentrantReadWriteLock, defaultValue: () -> V): V {
    lock.read {
        val value = get(key)
        if (value == null && !containsKey(key)) {
            lock.write {
                val newValue = get(key)
                if (newValue != null) {
                    return newValue
                }
                val answer = defaultValue()
                put(key, answer)
                return answer
            }
        } else {
            return value as V
        }
    }
}

inline fun <V> TIntObjectHashMap<V>.getOrPut(key: Int, lock: ReentrantReadWriteLock, defaultValue: () -> V): V {
    lock.read {
        val value = get(key)
        if (value == null && !containsKey(key)) {
            lock.write {
                val answer = defaultValue()
                put(key, answer)
                return answer
            }
        } else {
            return value
        }
    }
}

inline fun <V> TIntObjectHashMap<V>.getOrPut(key: Int, writeLock: ReadWriteSpinLock.WriteLock, crossinline defaultValue: () -> V): V {
    var value = this[key]
    if (value == null) {
        value = writeLock {
            var value = this[key]
            if (value == null) {
                value = defaultValue()
                this.put(key, value as V)
            }
            value
        }
    }
    return value
}

inline fun <V> TIntObjectHashMap<V>.getOrPut(key: Int, defaultValue: () -> V): V {
    val value = get(key)
    if (value == null && !containsKey(key)) {
        val answer = defaultValue()
        put(key, answer)
        return answer
    } else {
        return value
    }
}

inline fun <K, V> MutableMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    val value = this[key]
    if (value == null) {
        val answer = defaultValue()
        this.put(key, answer)
        return answer
    } else {
        return value
    }
}

inline fun <K, V> MutableMap<K, V>.getOrPut(key: K, writeLock: ReadWriteSpinLock.WriteLock, crossinline defaultValue: () -> V): V {
    var value = this[key]
    if (value == null) {
        value = writeLock {
            var value = this[key]
            if (value == null) {
                value = defaultValue()
                this.put(key, value as V)
            }
            value
        }
    }
    return value as V
}
