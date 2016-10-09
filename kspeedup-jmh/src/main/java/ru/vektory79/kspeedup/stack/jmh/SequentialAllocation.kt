package ru.vektory79.kspeedup.stack.jmh

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import ru.vektory79.kspeedup.stack.stack
import java.util.concurrent.TimeUnit

/**
 * Created by vektor on 09.09.16.
 */

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class SequentialAllocation {

    @Param("0", "1", "10", "100", "1000", "10000", "100000", "1000000")
    var allocationSize = 1

    @Benchmark
    fun sequentialDirect(bh: Blackhole) {
        for (i in 0..allocationSize) {
            bh.consume(Vector3D(0.0, 0.0, 0.0))
        }
    }

    @Benchmark
    fun sequentialStack(bh: Blackhole) {
        stack { ctrFactory ->
            val vectorCtr = ctrFactory<Vector3D> { Vector3D() }
            bh.consume(vectorCtr)
            for (i in 0..allocationSize) {
                bh.consume(vectorCtr { set(0.0, 0.0, 0.0) })
            }
        }
    }
}