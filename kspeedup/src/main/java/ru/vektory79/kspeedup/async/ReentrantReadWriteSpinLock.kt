package ru.vektory79.kspeedup.async

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.AbstractQueuedSynchronizer

/**
 * Created by vektor on 13.01.16.
 */
class ReentrantReadWriteSpinLock {
    private val outerGate = AtomicBoolean(true)
    private val enterCounter = AtomicInteger(0)
    private val innerGate = AtomicBoolean(true)
    @PublishedApi
    internal val lockContext = ThreadLocal.withInitial { LockContext() }
    @PublishedApi
    internal val readSync = ReadSync()
    @PublishedApi
    internal val writeSync = WriteSync()

    private fun getOuterGate(): Boolean {
        return outerGate.get()
    }

    private fun outerGateTryClose(): Boolean {
        return outerGate.compareAndSet(true, false)
    }

    private fun outerGateOpen() {
        outerGate.set(true)
    }

    private fun getInnerGate(): Boolean {
        return innerGate.get()
    }

    private fun innerGateTryClose(): Boolean {
        return innerGate.compareAndSet(true, false)
    }

    private fun innerGateOpen() {
        innerGate.set(true)
    }

    inline fun <T> readLock(readOperation: () -> T): T {
        val context = lockContext.get()
        while (true) {
            readSync.lockForRead(context)
            try {
                if (readSync.testInnerGate(context)) continue
                return readOperation()
            } catch (e: ShortCircuit) {
                if (context.readLocks > 1) {
                    throw shortCircuit
                }
            } finally {
                readSync.unlockForRead(context)
            }
        }
    }

    inline fun <T> writeLock(writeOperation: () -> T): T {
        val context = lockContext.get()
        while (true) {
            readSync.lockForWrite(context)
            var gateMask = 0
            try {
                gateMask = writeSync.lock(context)
                if (gateMask and CAS_MASK > 0) {
                    continue
                }
                return writeOperation()
            } catch (e: ShortCircuit) {
                if (context.readLocks > 0) {
                    throw shortCircuit
                }
            } finally {
                writeSync.unlock(context, gateMask)
                readSync.unlockForWrite()
            }
        }
    }

    @PublishedApi
    internal inner class ReadSync internal constructor() : AbstractQueuedSynchronizer() {
        override fun tryAcquire(arg: Int): Boolean {
            return getOuterGate()
        }

        override fun tryRelease(arg: Int): Boolean {
            return true
        }

        @PublishedApi
        internal fun lockForRead(context: LockContext) {
            if (context.readLocks == 0 && context.writeLocks == 0) {
                park(context)
            }
            enterCounter.andIncrement
            context.readLocks++
        }

        @PublishedApi
        internal fun lockForWrite(context: LockContext) {
            if (context.writeLocks == 0 && !getOuterGate()) {
                if (context.readLocks == 0) {
                    park(context)
                } else {
                    throw shortCircuit
                }
            }
        }

        @PublishedApi
        internal fun testInnerGate(context: LockContext) = context.readLocks == 1 && context.writeLocks == 0 && !getInnerGate()

        @PublishedApi
        internal fun unlockForRead(context: LockContext) {
            context.readLocks--
            enterCounter.andDecrement
            writeSync.release()
        }

        @PublishedApi
        internal fun unlockForWrite() {
            release()
        }

        private fun park(context: LockContext) {
            if (enterCounter.get() <= context.readLocks + context.writeLocks) {
                writeSync.release()
            }
            try {
                acquireInterruptibly(0)
            } catch (e: InterruptedException) {
                throw RuntimeException("Lock interrupted:", e)
            } finally {
                release()
            }
        }

        private fun release() {
            if (getOuterGate()) {
                release(0)
            }
        }
    }

    @PublishedApi
    internal inner class WriteSync internal constructor() : AbstractQueuedSynchronizer() {
        override fun tryAcquire(arg: Int): Boolean {
            val context = lockContext.get()
            return enterCounter.get() <= context.readLocks + context.writeLocks
        }

        override fun tryRelease(arg: Int): Boolean {
            return true
        }

        @PublishedApi
        internal fun lock(context: LockContext): Int {
            var gateMask = 0
            if (context.writeLocks == 0) {
                if (!innerGateTryClose()) {
                    if (context.readLocks == 0) {
                        return CAS_MASK
                    } else {
                        throw shortCircuit
                    }
                }
                gateMask = INNER_GATE_MASK
                if (!outerGateTryClose()) {
                    if (context.readLocks == 0) {
                        return gateMask or CAS_MASK
                    } else {
                        throw shortCircuit
                    }
                }
                gateMask = gateMask or OUTER_GATE_MASK
                context.writeLocks++
                enterCounter.andIncrement
                park(context)
            }
            return gateMask
        }

        @PublishedApi
        internal fun unlock(context: LockContext, gateMask: Int) {
            if (gateMask and OUTER_GATE_MASK > 0) {
                context.writeLocks--
                enterCounter.andDecrement

                if (context.writeLocks == 0) {
                    outerGateOpen()
                }
            }
            if ((gateMask and INNER_GATE_MASK > 0) && context.writeLocks == 0) {
                innerGateOpen()
            }
            release()
        }

        private fun park(context: LockContext) {
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

        internal fun release() {
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
        private const val INNER_GATE_MASK = 0b010
        private const val OUTER_GATE_MASK = 0b100
        @PublishedApi
        internal const val CAS_MASK = 0b001
    }
}
