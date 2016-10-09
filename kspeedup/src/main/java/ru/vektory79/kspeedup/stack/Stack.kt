package ru.vektory79.kspeedup.stack

import gnu.trove.list.array.TIntArrayList
import gnu.trove.map.hash.TIntObjectHashMap
import gnu.trove.stack.array.TIntArrayStack
import ru.vektory79.kspeedup.async.ReadWriteSpinLock
import ru.vektory79.kspeedup.collections.AsyncStack
import ru.vektory79.kspeedup.collections.*
import java.io.Closeable
import java.util.*

interface SingleFactoryAcceptor<T : Any> {
    fun setFactory(factory: StackSingleConstructor<T>)
}

interface CollectionFactoryAcceptor<T : Any> {
    fun setFactory(factory: StackCollectionConstructor<T>)
}

inline fun stack(body: (ctrFactory: StackConstructorFactory) -> Unit) {
    val manager = StackManager.manager
    val ctrFactory = manager.open()
    try {
        body(ctrFactory)
    } finally {
        ctrFactory.close()
    }
}

class StackSegment<E : Any> {
    private var cursor = 0
    val isCollection: Boolean
    val size: Int
    val type: Class<E>
    private val data: Array<E>

    constructor(capacity: Int, type: Class<E>, factory: () -> E) {
        this.type = type
        isCollection = false
        size = 0
        data = Array<Any>(capacity) { i -> factory() } as Array<E>
    }

    constructor(capacity: Int, type: Class<E>, size: Int, factory: (Int) -> E) {
        this.type = type
        isCollection = true
        this.size = size
        data = Array<Any>(capacity) { i -> factory(size) } as Array<E>
    }

    fun alloc(): E? {
        return if (cursor < data.size) data[cursor++] else null
    }

    fun free(count: Int = 1): Int {
        if (cursor > 0) {
            if (cursor >= count) {
                cursor -= count
                return 0
            } else {
                val result = count - cursor;
                cursor = 0
                return result
            }
        }
        return count
    }
}

class StackConstructorFactory internal constructor(private val manager: StackManager) : Closeable {
    private var level = -1

    private val singleConstructors = WeakHashMap<Class<*>, StackSingleConstructor<*>>(StackManager.DEFAULT_CAPACITY)
    private val collectionConstructors = WeakHashMap<Class<*>, TIntObjectHashMap<StackCollectionConstructor<*>>>(StackManager.DEFAULT_CAPACITY)
    private val levelSingleStack = ArrayList<ArrayList<StackSingleConstructor<*>>>(StackManager.DEFAULT_CAPACITY)
    private val levelCollectionStack = ArrayList<ArrayList<StackCollectionConstructor<*>>>(StackManager.DEFAULT_CAPACITY)

    init {
        for (i in 0..StackManager.DEFAULT_CAPACITY - 1) {
            levelSingleStack.add(ArrayList(StackManager.DEFAULT_CAPACITY))
        }
        for (i in 0..StackManager.DEFAULT_CAPACITY - 1) {
            levelCollectionStack.add(ArrayList(StackManager.DEFAULT_CAPACITY))
        }
    }

    internal fun open(): StackConstructorFactory {
        level++
        if (level >= levelSingleStack.size) {
            levelSingleStack.add(ArrayList(StackManager.DEFAULT_CAPACITY))
        }
        if (level >= levelCollectionStack.size) {
            levelCollectionStack.add(ArrayList(StackManager.DEFAULT_CAPACITY))
        }
        return this
    }

    override fun close() {
        for (i in 0..levelSingleStack[level].size - 1) {
            levelSingleStack[level][i].close()
        }
        levelSingleStack[level].clear()
        for (i in 0..levelCollectionStack[level].size - 1) {
            levelCollectionStack[level][i].close()
        }
        levelCollectionStack[level].clear()
        level--
    }

    fun <T : Any> getConstructor(clazz: Class<T>, ctr: () -> T): StackSingleConstructor<T> {
        val result = singleConstructors.getOrPut(clazz) {
            StackSingleConstructor(manager, clazz, ctr)
        }.open(level)
        levelSingleStack[level].add(result)
        return result as StackSingleConstructor<T>
    }

    inline operator fun <reified T : Any> invoke(noinline ctr: () -> T): StackSingleConstructor<T> {
        return getConstructor(T::class.java, ctr)
    }

    fun <T : Any> getConstructor(clazz: Class<T>, size: Int, ctr: (size: Int) -> T): StackCollectionConstructor<T> {
        val result = collectionConstructors.getOrPut(clazz) {
            TIntObjectHashMap(StackManager.DEFAULT_CAPACITY)
        }.getOrPut(size) {
            StackCollectionConstructor(manager, clazz, size, ctr)
        }.open(level)
        levelCollectionStack[level].add(result)
        return result as StackCollectionConstructor<T>
    }

    inline operator fun <reified T : Any> invoke(size: Int, noinline ctr: (size: Int) -> T): StackCollectionConstructor<T> {
        return getConstructor(T::class.java, size, ctr)
    }
}

class StackSingleConstructor<T : Any> internal constructor(
        private val manager: StackManager,
        private val clazz: Class<T>,
        private val ctr: () -> T) {

    private var level = -1
    private val levelStack = TIntArrayStack(StackManager.DEFAULT_CAPACITY)
    private val levelCounters = TIntArrayList(StackManager.DEFAULT_CAPACITY)
    private var lastSegment: StackSegment<T>? = null
    private val segments = ArrayDeque<StackSegment<T>>()

    init {
        segments.push(newSegment())
    }

    fun newSegment(): StackSegment<T> {
        if (lastSegment != null) {
            val result = lastSegment
            lastSegment = null
            return result as StackSegment<T>
        }
        return manager.getSegment(clazz, ctr)
    }

    internal fun open(level: Int): StackSingleConstructor<T> {
        if (level <= this.level) {
            throw RuntimeException("Error open constructor. Level: $level Type: ${clazz.name}")
        }
        levelStack.push(this.level)
        this.level = level
        while (levelCounters.size() <= level) {
            levelCounters.add(0)
        }
        return this
    }

    internal fun close() {
        while (levelCounters[level] != 0) {
            levelCounters[level] = segments.peek().free(levelCounters[level])
            if (levelCounters[level] > 0) {
                if (segments.size > 1) {
                    if (lastSegment != null) {
                        manager.retractSegment(lastSegment as StackSegment<T>)
                    }
                    lastSegment = segments.pop()
                } else {
                    throw RuntimeException("Error close single stack constructor")
                }
            }
        }
        this.level = levelStack.pop()
    }

    operator fun invoke(): T {
        val segment = segments.peek()
        val value = segment.alloc() ?: let {
            val newSegment = newSegment()
            segments.push(newSegment)
            newSegment.alloc()
        } ?: let {
            throw RuntimeException("Error construct object. Level: $level Type: ${clazz.name}")
        }
        levelCounters[level] += 1
        if (value is SingleFactoryAcceptor<*>) {
            val v = value as SingleFactoryAcceptor<T>
            v.setFactory(this)
        }
        return value
    }

    inline operator fun invoke(init: T.() -> Unit): T {
        val value = invoke()
        value.init()
        return value
    }
}

class StackCollectionConstructor<T : Any> internal constructor(
        private val manager: StackManager,
        private val clazz: Class<T>,
        private val size: Int,
        private val ctr: (size: Int) -> T) {
    private var level = -1
    private val levelStack = TIntArrayStack(StackManager.DEFAULT_CAPACITY)
    private val levelCounters = TIntArrayList(StackManager.DEFAULT_CAPACITY)
    private var lastSegment: StackSegment<T>? = null
    private val segments = ArrayDeque<StackSegment<T>>()

    init {
        segments.push(newSegment())
    }

    private fun newSegment(): StackSegment<T> {
        if (lastSegment != null) {
            val result = lastSegment
            lastSegment = null
            return result as StackSegment<T>
        }
        return manager.getSegment(clazz, size, ctr)
    }

    internal fun open(level: Int): StackCollectionConstructor<T> {
        if (level <= this.level) {
            throw RuntimeException("Error open constructor. Level: $level Type: ${clazz.name}")
        }
        levelStack.push(this.level)
        this.level = level
        while (levelCounters.size() <= level) {
            levelCounters.add(0)
        }
        return this
    }

    internal fun close() {
        while (levelCounters[level] != 0) {
            levelCounters[level] = segments.peek().free(levelCounters[level])
            if (levelCounters[level] > 0) {
                if (segments.size > 1) {
                    if (lastSegment != null) {
                        manager.retractSegment(lastSegment as StackSegment<T>)
                    }
                    lastSegment = segments.pop()
                } else {
                    throw RuntimeException("Error close collection stack constructor")
                }
            }
        }
        this.level = levelStack.pop()
    }

    operator fun invoke(): T {
        val segment = segments.peek()
        val value = segment.alloc() ?: let {
            val newSegment = newSegment()
            segments.push(newSegment)
            newSegment.alloc()
        } ?: let {
            throw RuntimeException("Error construct object. Level: $level Type: ${clazz.name}")
        }
        levelCounters[level] += 1
        if (value is CollectionFactoryAcceptor<*>) {
            val v = value as CollectionFactoryAcceptor<T>
            v.setFactory(this)
        }
        return value
    }

    operator fun invoke(init: T.() -> Unit): T {
        val segment = segments.peek()
        val value = segment.alloc() ?: let {
            val newSegment = newSegment()
            segments.push(newSegment)
            newSegment.alloc()
        } ?: let {
            throw RuntimeException("Error construct object. Level: $level Type: ${clazz.name}")
        }
        value.init()
        levelCounters[level] += 1
        if (value is CollectionFactoryAcceptor<*>) {
            val v = value as CollectionFactoryAcceptor<T>
            v.setFactory(this)
        }
        return value
    }
}

class StackManager private constructor() {
    companion object {
        internal const val DEFAULT_CAPACITY = 32
        val manager = StackManager()
    }

    private class Holder<T>(val factory: () -> T) {
        private var _value: T? = null
        var value: T?
            get() {
                if (_value == null) {
                    _value = factory()
                }
                return _value
            }
            set(v) {
                _value = v
            }
    }

    private val valueSegmentLock = ReadWriteSpinLock()
    private val collectionSegmentLock = ReadWriteSpinLock()

    private val valueSegmentsVault = WeakHashMap<Class<*>, AsyncStack<StackSegment<*>>>()
    private val arraySegmentsVault = WeakHashMap<Class<*>, TIntObjectHashMap<AsyncStack<StackSegment<*>>>>()

    private val constructorFactory = ThreadLocal.withInitial {
        Holder {
            StackConstructorFactory(this)
        }
    }

    fun clear() {
        valueSegmentsVault.clear()
        arraySegmentsVault.clear()
        constructorFactory.remove()
    }

    internal fun <E : Any> getSegment(clazz: Class<E>, factory: () -> E): StackSegment<E> {
        return valueSegmentLock { writeLock ->
            val segmentVault = valueSegmentsVault.getOrPut(clazz, writeLock) {
                AsyncStack<StackSegment<*>>()
            }
            segmentVault.pop() as StackSegment<E>? ?: StackSegment(StackManager.DEFAULT_CAPACITY*32, clazz, factory)
        }
    }

    internal fun <E : Any> getSegment(clazz: Class<E>, size: Int, factory: (Int) -> E): StackSegment<E> {
        return collectionSegmentLock { writeLock ->
            val arrayVault = arraySegmentsVault.getOrPut(clazz, writeLock) {
                TIntObjectHashMap<AsyncStack<StackSegment<*>>>()
            }
            val segmentVault = arrayVault.getOrPut(size, writeLock) {
                AsyncStack<StackSegment<*>>()
            }
            val segment = segmentVault.pop() ?: StackSegment(StackManager.DEFAULT_CAPACITY*32, clazz, size, factory)
            segment as StackSegment<E>
        }
    }

    internal fun <E : Any> retractSegment(segment: StackSegment<E>) {
        if (!segment.isCollection) {
            val seg: AsyncStack<StackSegment<E>> = valueSegmentLock {
                valueSegmentsVault[segment.type] as AsyncStack<StackSegment<E>>
            }
            seg.push(segment)
        } else {
            val seg: TIntObjectHashMap<AsyncStack<StackSegment<E>>> = collectionSegmentLock {
                arraySegmentsVault[segment.type] as TIntObjectHashMap<AsyncStack<StackSegment<E>>>
            }
            seg.get(segment.size).push(segment)
        }
    }

    fun open(): StackConstructorFactory = constructorFactory.get().value!!.open()
}

fun main(args: Array<String>) {
    for (i in 1..16) {
        Thread {
            println("Start $i !!!")
            val result = Vector3D()
            while (true) {
//                recursionStack(256)
                sequentialStack(10000, result)
            }
            println("Stop $i !!!\nResult: $result")
        }.start()
    }
}

internal fun sequentialStack(size: Int, result: Vector3D) {
    stack { ctrFactory ->
        val vectorCtr = ctrFactory<Vector3D> { Vector3D() }
        for (i in 0..size) {
            result += vectorCtr { set(size.toDouble(), size.toDouble(), size.toDouble()) }
        }
    }
}

internal data class Vector3D(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {

    fun set(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    operator fun plusAssign(b: Vector3D) {
        x += b.x
        y += b.y
        z += b.z
    }
}

internal operator fun Array<Vector3D>.plusAssign(b: Vector3D) {
    for (i in 0..size - 1) {
        this[i] += b
    }
}

internal fun recursionStack(r: Int) {
    if (r > 0) {
        stack { ctrFactory ->
            val newVector3D = ctrFactory<Vector3D>{ Vector3D() }
            val single = newVector3D()
            single.set(r.toDouble(), r.toDouble(), r.toDouble())


            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32){ Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array.plusAssign(single)
            recursionStack(r - 1)
        }
    }
}
