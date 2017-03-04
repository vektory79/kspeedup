package ru.vektory79.kspeedup.async

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.AbstractQueuedSynchronizer

/**
 * Created by vektor on 13.01.16.
 */
class ReentrantReadWriteSpinLock {
    private val outerGate = AtomicBoolean(true)
    @PublishedApi
    internal val enterCounter = AtomicInteger(0)
    private val innerGate = AtomicBoolean(true)
    @PublishedApi
    internal val lockContext = ThreadLocal.withInitial { LockContext() }
    @PublishedApi
    internal val readWait = ReadWait()
    @PublishedApi
    internal val writeWait = WriteWait()

    @PublishedApi
    internal fun getOuterGate(): Boolean {
        return outerGate.get()
    }

    @PublishedApi
    internal fun outerGateTryClose(): Boolean {
        return outerGate.compareAndSet(true, false)
    }

    @PublishedApi
    internal fun outerGateOpen() {
        outerGate.set(true)
    }

    @PublishedApi
    internal fun getInnerGate(): Boolean {
        return innerGate.get()
    }

    @PublishedApi
    internal fun innerGateTryClose(): Boolean {
        return innerGate.compareAndSet(true, false)
    }

    @PublishedApi
    internal fun innerGateOpen() {
        innerGate.set(true)
    }

    inline fun <T> readLock(readOperation: () -> T): T {
        val context = lockContext.get()
        while (true) {
            if (context.readLocks == 0 && context.writeLocks == 0) {
                readWait.lock()
            }
            enterCounter.andIncrement
            context.readLocks++
            try {
                if (context.readLocks == 1 && context.writeLocks == 0 && !getInnerGate()) continue
                return readOperation()
            } catch (e: ShortCircuit) {
                if (context.readLocks > 1) {
                    throw shortCircuit
                }
            } finally {
                context.readLocks--
                enterCounter.andDecrement
                writeWait.release()
            }
        }
    }

    inline fun <T> writeLock(writeOperation: () -> T): T {
        val context = lockContext.get()
        while (true) {
            if (context.writeLocks == 0 && !getOuterGate()) {
                if (context.readLocks == 0) {
                    readWait.lock()
                } else {
                    throw shortCircuit
                }
            }
            var innerEntered = false
            var outerEntered = false
            try {
                if (context.writeLocks == 0) {
                    if (!innerGateTryClose()) {
                        if (context.readLocks == 0) {
                            continue
                        } else {
                            throw shortCircuit
                        }
                    }
                    innerEntered = true
                    if (!outerGateTryClose()) {
                        if (context.readLocks == 0) {
                            continue
                        } else {
                            throw shortCircuit
                        }
                    }
                    outerEntered = true
                    context.writeLocks++
                    enterCounter.andIncrement
                    writeWait.lock()
                }

                return writeOperation()
            } catch (e: ShortCircuit) {
                if (context.readLocks > 0) {
                    throw shortCircuit
                }
            } finally {
                if (outerEntered) {
                    context.writeLocks--
                    enterCounter.andDecrement

                    if (context.writeLocks == 0) {
                        outerGateOpen()
                    }
                }
                if (innerEntered && context.writeLocks == 0) {
                    innerGateOpen()
                }
                writeWait.release()
                readWait.release()
            }
        }
    }

    @PublishedApi
    internal inner class ReadWait : AbstractQueuedSynchronizer() {
        override fun tryAcquire(arg: Int): Boolean {
            return getOuterGate()
        }

        override fun tryRelease(arg: Int): Boolean {
            return true
        }

        fun lock() {
            val context = lockContext.get()
            if (enterCounter.get() <= context.readLocks + context.writeLocks) {
                writeWait.release()
            }
            try {
                acquireInterruptibly(0)
            } catch (e: InterruptedException) {
                throw RuntimeException("Lock interrupted:", e)
            } finally {
                release()
            }
        }

        fun release() {
            if (getOuterGate()) {
                release(0)
            }
        }
    }

    @PublishedApi
    internal inner class WriteWait : AbstractQueuedSynchronizer() {
        override fun tryAcquire(arg: Int): Boolean {
            val context = lockContext.get()
            return enterCounter.get() <= context.readLocks + context.writeLocks
        }

        override fun tryRelease(arg: Int): Boolean {
            return true
        }

        fun lock() {
            val context = lockContext.get()
            try {
                while (enterCounter.get() > context.readLocks + context.writeLocks) {
                    acquireInterruptibly(0)
                }
            } catch (e: InterruptedException) {
                throw RuntimeException("Lock interrupted:", e)
            } finally {
                if (enterCounter.get() <= context.readLocks + context.writeLocks) {
                    release()
                }
            }
        }


        fun release() {
            release(0)
        }
    }

    @PublishedApi
    internal class LockContext {
        @PublishedApi
        internal var readLocks = 0
        @PublishedApi
        internal var writeLocks = 0
    }

    companion object {
        @PublishedApi
        internal val shortCircuit = ShortCircuit()
    }
}
