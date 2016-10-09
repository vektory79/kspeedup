package ru.vektory79.kspeedup.async.utils

import java.util.Random

/**
 * Created by vektor on 09.02.16.
 */
class RandomString(private val length: Int) {

    private val random = Random(123)

    fun nextString(): String {
        if (length < 1)
            throw IllegalArgumentException("length < 1: " + length)
        val buf = CharArray(length)
        for (idx in buf.indices)
            buf[idx] = symbols[random.nextInt(symbols.size)]
        return String(buf)
    }

    companion object {

        private val symbols: CharArray

        init {
            val tmp = StringBuilder()
            for (ch in '0'..'9') {
                tmp.append(ch)
            }
            for (ch in 'a'..'z') {
                tmp.append(ch)
            }
            symbols = tmp.toString().toCharArray()
        }
    }
}
