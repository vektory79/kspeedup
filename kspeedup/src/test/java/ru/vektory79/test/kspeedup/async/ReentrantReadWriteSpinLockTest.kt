package ru.vektory79.test.kspeedup.async

import com.googlecode.junittoolbox.MultithreadingTester
import org.junit.Test
import ru.vektory79.kspeedup.async.ReentrantReadWriteSpinLock
import ru.vektory79.kspeedup.async.utils.RandomString

import java.util.HashMap

/**
 * Created by vektor on 09.02.16.
 */
class ReentrantReadWriteSpinLockTest {
    private inner class Ref(val name: String) {
        val ref: Ref

        init {
            lock.writeLock { graph.put(name, this@Ref) }
            ref = lock.readLock {
                val refName = stringGenerator.nextString()
                graph[refName] ?: Ref(refName)
            }
        }
    }

    private val lock = ReentrantReadWriteSpinLock()
    private val stringGenerator = RandomString(2, 1234)
    private val graph = HashMap<String, Ref>()

    @Test
    @Throws(Exception::class)
    fun writeLockSingle() {
        for (i in 0..999) {
            graph.clear()
            Ref(stringGenerator.nextString())
        }
    }

    @Test
    @Throws(Exception::class)
    fun writeLockMulti() {
        for (i in 0..99) {
            graph.clear()
            MultithreadingTester()
                    .add(Runnable { Ref(stringGenerator.nextString()) })
                    .numRoundsPerThread(100)
                    .numThreads(200)
                    .run()
        }
    }
}