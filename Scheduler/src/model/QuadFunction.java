/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Leonard T. Erwine
 */
@FunctionalInterface
public interface QuadFunction<T0, T1, T2, T3, R> {
    R apply(T0 arg0, T1 arg1, T2 arg2, T3 arg3);
    
    default <V> QuadFunction<T0, T1, T2, T3, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T0 arg0, T1 arg1, T2 arg2, T3 arg3) -> after.apply(apply(arg0, arg1, arg2, arg3));
    }
}
