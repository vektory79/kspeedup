/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ru.vektory79.kspeedup.stack.jmh

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import ru.vektory79.kspeedup.stack.stack
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
open class MyBenchmark {
    /*************************************************************************/
    @Benchmark
    @OperationsPerInvocation(1)
    fun inclusionStack01(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(1.toDouble(), 1.toDouble(), 1.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
        }
    }

    @Benchmark
    @OperationsPerInvocation(2)
    fun inclusionStack02(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(2.toDouble(), 2.toDouble(), 2.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack01(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(3)
    fun inclusionStack03(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(3.toDouble(), 3.toDouble(), 3.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack02(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(4)
    fun inclusionStack04(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(4.toDouble(), 4.toDouble(), 4.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack03(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(5)
    fun inclusionStack05(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(5.toDouble(), 5.toDouble(), 5.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack04(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(6)
    fun inclusionStack06(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(6.toDouble(), 6.toDouble(), 6.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack05(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(7)
    fun inclusionStack07(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(7.toDouble(), 7.toDouble(), 7.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack06(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(8)
    fun inclusionStack08(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(8.toDouble(), 8.toDouble(), 8.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack07(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(9)
    fun inclusionStack09(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(9.toDouble(), 9.toDouble(), 9.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack08(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(10)
    fun inclusionStack10(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(10.toDouble(), 10.toDouble(), 10.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack09(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(11)
    fun inclusionStack11(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(11.toDouble(), 11.toDouble(), 11.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack10(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(12)
    fun inclusionStack12(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack11(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(13)
    fun inclusionStack13(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack12(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(14)
    fun inclusionStack14(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack13(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(15)
    fun inclusionStack15(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack14(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(16)
    fun inclusionStack16(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack15(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(17)
    fun inclusionStack17(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack16(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(18)
    fun inclusionStack18(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack17(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(19)
    fun inclusionStack19(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack18(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(20)
    fun inclusionStack20(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack19(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(21)
    fun inclusionStack21(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack20(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(22)
    fun inclusionStack22(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack21(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(23)
    fun inclusionStack23(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack22(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(24)
    fun inclusionStack24(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack23(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(25)
    fun inclusionStack25(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack24(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(26)
    fun inclusionStack26(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack25(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(27)
    fun inclusionStack27(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack26(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(28)
    fun inclusionStack28(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack27(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(29)
    fun inclusionStack29(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack28(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(30)
    fun inclusionStack30(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack29(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(31)
    fun inclusionStack31(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack30(bh)
        }
    }

    @Benchmark
    @OperationsPerInvocation(32)
    fun inclusionStack32(bh: Blackhole) {
        stack { ctrFactory ->
            val newVector3D  = ctrFactory<Vector3D> { Vector3D() }
            val single = newVector3D()
            single.set(12.toDouble(), 12.toDouble(), 12.toDouble())

            val newArrayVector3D = ctrFactory<Array<Vector3D>>(32) { Array(it) { Vector3D() } }
            val array = newArrayVector3D()
            for (i in 0..array.size - 1) {
                array[i].set(i.toDouble(), i.toDouble(), i.toDouble())
            }
            array += single
            bh.consume(array)
            bh.consume(single)
            inclusionStack31(bh)
        }
    }

    /*************************************************************************/
    @Benchmark
    @OperationsPerInvocation(1)
    fun inclusionDirect01(bh: Blackhole) {
        val single = Vector3D(1.toDouble(), 1.toDouble(), 1.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
    }
    @Benchmark
    @OperationsPerInvocation(2)
    fun inclusionDirect02(bh: Blackhole) {
        val single = Vector3D(2.toDouble(), 2.toDouble(), 2.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect01(bh)
    }
    @Benchmark
    @OperationsPerInvocation(3)
    fun inclusionDirect03(bh: Blackhole) {
        val single = Vector3D(3.toDouble(), 3.toDouble(), 3.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect02(bh)
    }
    @Benchmark
    @OperationsPerInvocation(4)
    fun inclusionDirect04(bh: Blackhole) {
        val single = Vector3D(4.toDouble(), 4.toDouble(), 4.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect03(bh)
    }
    @Benchmark
    @OperationsPerInvocation(5)
    fun inclusionDirect05(bh: Blackhole) {
        val single = Vector3D(5.toDouble(), 5.toDouble(), 5.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect04(bh)
    }
    @Benchmark
    @OperationsPerInvocation(6)
    fun inclusionDirect06(bh: Blackhole) {
        val single = Vector3D(6.toDouble(), 6.toDouble(), 6.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect05(bh)
    }
    @Benchmark
    @OperationsPerInvocation(7)
    fun inclusionDirect07(bh: Blackhole) {
        val single = Vector3D(7.toDouble(), 7.toDouble(), 7.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect06(bh)
    }
    @Benchmark
    @OperationsPerInvocation(8)
    fun inclusionDirect08(bh: Blackhole) {
        val single = Vector3D(8.toDouble(), 8.toDouble(), 8.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect07(bh)
    }
    @Benchmark
    @OperationsPerInvocation(9)
    fun inclusionDirect09(bh: Blackhole) {
        val single = Vector3D(9.toDouble(), 9.toDouble(), 9.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect08(bh)
    }
    @Benchmark
    @OperationsPerInvocation(10)
    fun inclusionDirect10(bh: Blackhole) {
        val single = Vector3D(10.toDouble(), 10.toDouble(), 10.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect09(bh)
    }
    @Benchmark
    @OperationsPerInvocation(11)
    fun inclusionDirect11(bh: Blackhole) {
        val single = Vector3D(11.toDouble(), 11.toDouble(), 11.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect10(bh)
    }
    @Benchmark
    @OperationsPerInvocation(12)
    fun inclusionDirect12(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect11(bh)
    }
    @Benchmark
    @OperationsPerInvocation(13)
    fun inclusionDirect13(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect12(bh)
    }
    @Benchmark
    @OperationsPerInvocation(14)
    fun inclusionDirect14(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect13(bh)
    }
    @Benchmark
    @OperationsPerInvocation(15)
    fun inclusionDirect15(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect14(bh)
    }
    @Benchmark
    @OperationsPerInvocation(16)
    fun inclusionDirect16(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect15(bh)
    }
    @Benchmark
    @OperationsPerInvocation(17)
    fun inclusionDirect17(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect16(bh)
    }
    @Benchmark
    @OperationsPerInvocation(18)
    fun inclusionDirect18(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect17(bh)
    }
    @Benchmark
    @OperationsPerInvocation(19)
    fun inclusionDirect19(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect18(bh)
    }
    @Benchmark
    @OperationsPerInvocation(20)
    fun inclusionDirect20(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect19(bh)
    }
    @Benchmark
    @OperationsPerInvocation(21)
    fun inclusionDirect21(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect20(bh)
    }
    @Benchmark
    @OperationsPerInvocation(22)
    fun inclusionDirect22(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect21(bh)
    }
    @Benchmark
    @OperationsPerInvocation(23)
    fun inclusionDirect23(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect22(bh)
    }
    @Benchmark
    @OperationsPerInvocation(24)
    fun inclusionDirect24(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect23(bh)
    }
    @Benchmark
    @OperationsPerInvocation(25)
    fun inclusionDirect25(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect24(bh)
    }
    @Benchmark
    @OperationsPerInvocation(26)
    fun inclusionDirect26(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect25(bh)
    }
    @Benchmark
    @OperationsPerInvocation(27)
    fun inclusionDirect27(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect26(bh)
    }
    @Benchmark
    @OperationsPerInvocation(28)
    fun inclusionDirect28(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect27(bh)
    }
    @Benchmark
    @OperationsPerInvocation(29)
    fun inclusionDirect29(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect28(bh)
    }
    @Benchmark
    @OperationsPerInvocation(30)
    fun inclusionDirect30(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect29(bh)
    }
    @Benchmark
    @OperationsPerInvocation(31)
    fun inclusionDirect31(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect30(bh)
    }
    @Benchmark
    @OperationsPerInvocation(32)
    fun inclusionDirect32(bh: Blackhole) {
        val single = Vector3D(12.toDouble(), 12.toDouble(), 12.toDouble())
        val array = Array(32) { Vector3D(it.toDouble(), it.toDouble(), it.toDouble()) }
        array += single
        bh.consume(array)
        bh.consume(single)
        inclusionDirect31(bh)
    }
}
