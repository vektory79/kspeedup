package ru.vektory79.kspeedup.async

class ShortCircuit : RuntimeException() {
    @Override
    fun fillInStackTrace(): Throwable = this
}