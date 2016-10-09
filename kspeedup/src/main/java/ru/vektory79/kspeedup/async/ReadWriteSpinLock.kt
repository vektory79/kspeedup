package ru.vektory79.kspeedup.async

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.AbstractQueuedSynchronizer

class ReadWriteSpinLock() {
    companion object {
        val shortCircuit = ShortCircuit()
    }
    private val _outerGate = AtomicBoolean(true)
    private val _enterCounter = AtomicInteger(0)
    private val _innerGate = AtomicBoolean(true)
    val readWait = ReadWait()
    val writeWait = WriteWait()
    val writeLock = WriteLock(this)

    val outerGate: Boolean
        get() = _outerGate.get()

    fun outerGateTryClose(): Boolean {
        return _outerGate.compareAndSet(true, false)
    }

    fun outerGateOpen() {
        _outerGate.set(true)
    }

    val innerGate: Boolean
        get() = _innerGate.get()

    fun innerGateTryClose(): Boolean {
        return _innerGate.compareAndSet(true, false)
    }

    fun innerGateOpen() {
        _innerGate.set(true)
    }

    val threadsEntered: Int
        get() = _enterCounter.get()

    fun enter() {
        _enterCounter.andIncrement
    }

    fun leave() {
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

    inner class ReadWait : AbstractQueuedSynchronizer() {

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

    inner class WriteWait : AbstractQueuedSynchronizer() {

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