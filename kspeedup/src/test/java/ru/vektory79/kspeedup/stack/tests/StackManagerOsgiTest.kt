package ru.vektory79.kspeedup.stack.tests

import com.googlecode.junittoolbox.MultithreadingTester
import gnu.trove.stack.array.TIntArrayStack
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.arquillian.osgi.StartLevelAware
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.util.Checks
import ru.vektory79.kspeedup.stack.StackManager
import ru.vektory79.kspeedup.stack.stack
import ru.vektory79.testing.OsgiTestingUtils
import kotlin.test.assertNotNull


data class Vector3D(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {

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

operator fun Array<Vector3D>.plusAssign(b: Vector3D) {
    this.forEach { it += b }
}

@RunWith(Arquillian::class)
class StackManagerOsgiTest {

    @Test
    fun t01simpleTest() {
        StackManager.manager.clear()
        stack { ctrFactory ->
            val newVector3D = ctrFactory { Vector3D() }
            val newArrayVector3D = ctrFactory(32) { Array(it) { Vector3D() } }

            val single = newVector3D {
                set(1.0, 1.0, 1.0)
            }
            val array = newArrayVector3D {
                forEachIndexed { i, vector ->
                    vector.set(i.toDouble(), i.toDouble(), i.toDouble())
                }
            }
            array += single
        }
    }

    @Test
    fun t02recursiveSimpleStackValueTest() {
        StackManager.manager.clear()
        recursiveSingleValueTest(1024)
        recursiveSingleValueTest(1024)
    }

    private fun recursiveSingleValueTest(r: Int) {
        if (r > 0) {
            stack { ctrFactory ->
                val newVector3D = ctrFactory { Vector3D() }
                val single = newVector3D {
                    set(r.toDouble(), r.toDouble(), r.toDouble())
                }
                assertNotNull(single, "Error to create object handler")
                recursiveSingleValueTest(r - 1)
            }
        }
    }

    @Test
    fun t03recursiveSimpleStackManagerArrayTest() {
        StackManager.manager.clear()
        recursiveSingleArrayTest(1024)
        recursiveSingleArrayTest(1024)
    }

    private fun recursiveSingleArrayTest(r: Int) {
        if (r > 0) {
            stack { ctrFactory ->
                val newVector3D = ctrFactory { Vector3D() }
                val single = newVector3D {
                    set(r.toDouble(), r.toDouble(), r.toDouble())
                }

                val newArrayVector3D = ctrFactory(32) { Array(it) { Vector3D() } }
                val array = newArrayVector3D {
                    forEachIndexed { i, vector ->
                        vector.set(i.toDouble(), i.toDouble(), i.toDouble())
                    }
                }
                array += single

                array.forEachIndexed { i, v ->
                    assert(Math.abs(v.x - (r.toDouble() + i.toDouble())) < 0.000000001)
                    assert(Math.abs(v.y - (r.toDouble() + i.toDouble())) < 0.000000001)
                    assert(Math.abs(v.z - (r.toDouble() + i.toDouble())) < 0.000000001)
                }
                recursiveSingleArrayTest(r - 1)
            }
        }
    }

    @Test
    fun t04concurrentStackManagerTest() {
        for (i in 0..100) {
            StackManager.manager.clear()
            MultithreadingTester()
                    .add(Runnable { recursiveSingleValueTest(32) })
                    .numRoundsPerThread(100)
                    .numThreads(16)
                    .run()
        }
    }

    @Test
    fun t05concurrentStackManagerTest() {
        for (i in 0..100) {
            StackManager.manager.clear()
            MultithreadingTester()
                    .add(Runnable { recursiveSingleArrayTest(32) })
                    .numRoundsPerThread(100)
                    .numThreads(16)
                    .run()
        }
    }

    @Test
    fun t06concurrentStackManagerTest() {
        for (i in 0..100) {
            StackManager.manager.clear()
            MultithreadingTester()
                    .add(Runnable { recursiveSingleValueTest(32) })
                    .add(Runnable { recursiveSingleArrayTest(32) })
                    .numRoundsPerThread(100)
                    .numThreads(16)
                    .run()
        }
    }

    @Test
    fun t07sequentialSimpleStackValueTest() {
        StackManager.manager.clear()
        sequentialSingleValueTest(10000)
        sequentialSingleValueTest(10000)
    }

    private fun sequentialSingleValueTest(r: Int) {
        val result = Vector3D()

        var test: Double = 0.0
        for (i in 0..r) {
            test += i.toDouble()
        }

        stack { ctrFactory ->
            val vectorCtr = ctrFactory { Vector3D() }
            for (i in 0..r) {
                result += vectorCtr { set(i.toDouble(), i.toDouble(), i.toDouble()) }
            }
        }

        assert(Math.abs(test - result.x) < 0.000000001)
        assert(Math.abs(test - result.y) < 0.000000001)
        assert(Math.abs(test - result.z) < 0.000000001)
    }

    @Test
    fun t08sequentialSimpleStackManagerArrayTest() {
        StackManager.manager.clear()
        sequentialSingleArrayTest(10000)
        sequentialSingleArrayTest(10000)
    }

    private fun sequentialSingleArrayTest(r: Int) {
        val result = Array(32) { Vector3D(0.0, 0.0, 0.0) }

        var test: Double = 0.0
        for (i in 0..r) {
            test += i.toDouble()
        }

        stack { ctrFactory ->
            val newArrayVector3D = ctrFactory(32) { Array(it) { Vector3D() } }
            for (i in 0..r) {
                val array = newArrayVector3D {
                    forEach { vector ->
                        vector.set(i.toDouble(), i.toDouble(), i.toDouble())
                    }
                }
                result.forEachIndexed { j, vector ->
                    vector += array[j]
                }
            }
        }

        result.forEach { vector ->
            assert(Math.abs(test - vector.x) < 0.000000001)
            assert(Math.abs(test - vector.y) < 0.000000001)
            assert(Math.abs(test - vector.z) < 0.000000001)
        }
    }

    @Test
    fun t09sequentialconcurrentStackManagerTest() {
        for (i in 0..10) {
            StackManager.manager.clear()
            MultithreadingTester()
                    .add(Runnable { sequentialSingleValueTest(10000) })
                    .numRoundsPerThread(10)
                    .numThreads(16)
                    .run()
        }
    }

    @Test
    fun t10sequentialconcurrentStackManagerTest() {
        for (i in 0..10) {
            StackManager.manager.clear()
            MultithreadingTester()
                    .add(Runnable { sequentialSingleArrayTest(10000) })
                    .numRoundsPerThread(10)
                    .numThreads(16)
                    .run()
        }
    }

    @Test
    fun t11sequentialconcurrentStackManagerTest() {
        for (i in 0..10) {
            StackManager.manager.clear()
            MultithreadingTester()
                    .add(Runnable { sequentialSingleValueTest(10000) })
                    .add(Runnable { sequentialSingleArrayTest(10000) })
                    .numRoundsPerThread(10)
                    .numThreads(16)
                    .run()
        }
    }

    companion object {

        /**
         * Creates a new Java Archive.

         * @return the archive
         */
        @JvmStatic
        @Deployment
        @StartLevelAware(autostart = true)
        fun createDeployment(): JavaArchive {
            val archive = OsgiTestingUtils.createOsgiBundle()
            archive
                    .addClass(Vector3D::class.java)
                    .addClass(MultithreadingTester::class.java)
                    .addClass(Checks::class.java)
                    .addClass(TIntArrayStack::class.java)
                    .addClass(loadClass("ru.vektory79.kspeedup.stack.tests.StackManagerOsgiTestKt"))
                    .addPackages(true, "com.googlecode.junittoolbox")
                    .addPackages(true, "kotlin")
                    .addPackages(true, "kotlin.test")
            return archive.`as`(JavaArchive::class.java)
        }

        private fun loadClass(className: String): Class<*> {
            return StackManagerOsgiTest::class.java.classLoader.loadClass(className)
        }
    }
}