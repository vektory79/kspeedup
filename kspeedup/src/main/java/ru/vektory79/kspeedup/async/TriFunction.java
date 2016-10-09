package ru.vektory79.kspeedup.async;

/**
 * Created by vektor on 13.01.16.
 */
public interface TriFunction<P1, P2, P3, R> {
    R apply(P1 param1, P2 param2, P3 param3);
}
