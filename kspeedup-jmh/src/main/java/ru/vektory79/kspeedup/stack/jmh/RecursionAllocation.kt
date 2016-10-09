package ru.vektory79.kspeedup.stack.jmh

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import ru.vektory79.kspeedup.stack.stack
import java.util.concurrent.TimeUnit

/**
 * Created by vektor on 10.09.16.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class RecursionAllocation {

    @Param("1", "10", "100", "1000")
    var allocationSize = 1

    @Benchmark
    fun recursionStack(bh: Blackhole) {
        recursionStack(bh, allocationSize)
    }

    @Benchmark
    fun recursionDirect(bh: Blackhole) {
        recursionDirect(bh, allocationSize)
    }

    private fun recursionStack(bh: Blackhole, r: Int) {
        if (r > 0) {
            stack { ctrFactory ->
                val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
                val single = newVector3D()
                single.set(r.toDouble(), r.toDouble(), r.toDouble())

                val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
                val array = newArrayVector3D()
                for (i in 0..array.size - 1) {
                    array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
                }
                array += single
                bh.consume(array)
                bh.consume(single)
                recursionStack(bh, r - 1)
            }
        }
    }

    private fun recursionDirect(bh: Blackhole, r: Int) {
        if (r > 0) {
            val single = Vector3D(r.toDouble(), r.toDouble(), r.toDouble())
            val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
            array += single
            bh.consume(array)
            bh.consume(single)
            recursionDirect(bh, r - 1)
        }
    }
}