package com.aelous.utility.test.generic;

import java.util.*;
import java.util.concurrent.TimeUnit;

/*
 * This test is useful for checking the performance of O(1) vs O(n) operations.
 * For example using a ArrayList instead of a LinkedHashSet for the recent teleports for the teleports interface.
 * We use nano time (which is not thread-safe, but this test is single-threaded) for benchmarking operations
 * since milliseconds is not precise enough.
 */
public class ArrayListVersusLinkedHashSetTest {
    //This is how many objects are created to warm up the JVM for accurate testing.
    static final int TEMP_ITERATIONS = 10_000_000;

    //This is how many iterations for the data structures.
    static final int ITERATIONS = 10_000_000;

    //This is the element we want to find (and remove) in the data structures.
    static final int ELEMENT = 5_900;

    public static void main(String[] args) {
        List<Integer> temp = new ArrayList<>();
        for (int counter = 0; counter < TEMP_ITERATIONS; counter++) {
            temp.add(counter);
        }

        List<Integer> list = new ArrayList<>();
        for (int counter = 0; counter < ITERATIONS; counter++) {
            list.add(counter);
        }
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        for (int counter = 0; counter < ITERATIONS; counter++) {
            linkedHashSet.add(counter);
        }

        System.out.println("ArrayList before removing " + ELEMENT + ": " + list);
        long beforeList = System.nanoTime();
        list.remove(ELEMENT);
        long elapsedArrayListNano = System.nanoTime() - beforeList;
        long elapsedArrayListMillis = TimeUnit.NANOSECONDS.toMillis(elapsedArrayListNano);
        System.out.println("ArrayList after removing " + ELEMENT + ": " + list);

        System.out.println("LinkedHashSet before removing " + ELEMENT + ": " + Arrays.toString(linkedHashSet.toArray()));
        long beforeLinkedHashSet = System.nanoTime();
        linkedHashSet.remove(ELEMENT);
        long elapsedLinkedHashSetNano = System.nanoTime() - beforeLinkedHashSet;
        long elapsedLinkedHashSetMillis = TimeUnit.NANOSECONDS.toMillis(elapsedLinkedHashSetNano);
        System.out.println("LinkedHashSet after removing " + ELEMENT + ": " + Arrays.toString(linkedHashSet.toArray()));

        System.out.println("It took " + elapsedArrayListNano + " ns (" + elapsedArrayListMillis + " ms) to remove the element " + ELEMENT + " from the size "  + ITERATIONS + " ArrayList.");
        System.out.println("It took " + elapsedLinkedHashSetNano + " ns (" + elapsedLinkedHashSetMillis + " ms) to remove the element " + ELEMENT + " from the size "  + ITERATIONS + " LinkedHashSet.");
    }
}
