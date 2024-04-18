package org.jgrapht.alg.shortestpath;

@FunctionalInterface
public interface Condition<E> {
    boolean test(E e, Double weight);
}
