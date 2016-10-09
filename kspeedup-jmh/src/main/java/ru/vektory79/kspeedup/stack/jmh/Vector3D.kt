package ru.vektory79.kspeedup.stack.jmh

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
    for (i in 0..size - 1) {
        this[i] += b
    }
}
