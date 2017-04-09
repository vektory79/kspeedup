package ru.vektory79.kspeedup.async

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.AbstractQueuedSynchronizer

class ReadWriteSpinLock {
    companion object {
        val shortCircuit = ShortCircuit()
    }
    private val _outerGate = AtomicBoolean(true)
    private val _enterCounter = AtomicInteger(0)
    private val _innerGate = AtomicBoolean(true)
    @PublishedApi
    internal val readWait = ReadWait()
    @PublishedApi
    internal val writeWait = WriteWait()
    @PublishedApi
    internal val writeLock = WriteLock(this)

    @PublishedApi
    internal val outerGate: Boolean
        get() = _outerGate.get()

    @PublishedApi
    internal fun outerGateTryClose(): Boolean {
        return _outerGate.compareAndSet(true, false)
    }

    @PublishedApi
    internal fun outerGateOpen() {
        _outerGate.set(true)
    }

    @PublishedApi
    internal val innerGate: Boolean
        get() = _innerGate.get()

    @PublishedApi
    internal fun innerGateTryClose(): Boolean {
        return _innerGate.compareAndSet(true, false)
    }

    @PublishedApi
    internal fun innerGateOpen() {
        _innerGate.set(true)
    }

    @PublishedApi
    internal val threadsEntered: Int
        get() = _enterCounter.get()

    @PublishedApi
    internal fun enter() {
        _enterCounter.andIncrement
    }

    @PublishedApi
    internal fun leave() {
        _enterCounter.andDecrement
    }

    fun <T> readLock(readOperation: (WriteLock) -> T): T {
        return invoke(readOperation)
    }

    inline operator fun <T> invoke(readOperation: (WriteLock) -> T): T {
        while (true) {
            readWait()
            enter()
            try {
                if (!innerGate) continue

                return readOperation(writeLock)
            } catch (sc: ShortCircuit) {
                // Short circuit to spin loop
            } finally {
                leave()
                writeWait.release()
            }
        }
    }

    inline fun <T> writeLock(writeOperation: () -> T): T {
        while (true) {
            readWait()
            enter()
            try {
                if (!innerGate) continue

                var innerEnter = false
                var outerEnter = false
                try {
                    if (!innerGateTryClose()) continue
                    innerEnter = true
                    if (!outerGateTryClose()) continue
                    outerEnter = true
                    writeWait()

                    return writeOperation()
                } finally {
                    if (innerEnter) innerGateOpen()
                    if (outerEnter) outerGateOpen()
                    readWait.release()
                }
            } catch (sc: ShortCircuit) {
                // Short circuit to spin loop
            } finally {
                leave()
                writeWait.release()
            }
        }
    }

    class WriteLock(val lock: ReadWriteSpinLock) {

        fun <T> writeLock(writeOperation: () -> T): T {
            return invoke(writeOperation)
        }

        inline operator fun <T> invoke(writeOperation: () -> T): T {
            var innerEnter = false
            var outerEnter = false
            try {
                if (!lock.innerGateTryClose()) throw shortCircuit
                innerEnter = true
                if (!lock.outerGateTryClose()) throw shortCircuit
                outerEnter = true
                lock.writeWait()

                return writeOperation()
            } finally {
                if (innerEnter) lock.innerGateOpen()
                if (outerEnter) lock.outerGateOpen()
                lock.writeWait.release()
                lock.readWait.release()
            }
        }
    }

    @PublishedApi
    internal inner class ReadWait : AbstractQueuedSynchronizer() {

        override fun tryAcquire(arg: Int): Boolean {
            return outerGate
        }

        override fun tryRelease(arg: Int): Boolean {
            return true
        }

        operator fun invoke() {
            if (threadsEntered <= 1) writeWait.release()
            acquireInterruptibly(0)
            release()
        }

        fun release() {
            if (outerGate) release(0)
        }
    }

    @PublishedApi
    internal inner class WriteWait : AbstractQueuedSynchronizer() {

        override fun tryAcquire(arg: Int): Boolean {
            return threadsEntered <= 1
        }

        override fun tryRelease(arg: Int): Boolean {
            return true
        }

        operator fun invoke() {
            while (threadsEntered > 1) acquireInterruptibly(0)
            release()
        }

        fun release() {
            if (threadsEntered <= 1) release(0)
        }
    }
}