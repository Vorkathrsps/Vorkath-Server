package com.aelous.utility.test.generic;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HashSetVersusLinkedHashSetTest {
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
        Set<Integer> hashSet = new HashSet<>();
        for (int counter = 0; counter < ITERATIONS; counter++) {
            hashSet.add(counter);
        }
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        for (int counter = 0; counter < ITERATIONS; counter++) {
            linkedHashSet.add(counter);
        }

        //For some reason, the LinkedHashSet completes faster than the HashSet if the LinkedHashSet is benchmarked second.
        //However, if the HashSet is benchmarked first, the LinkedHashSet and HashSet complete at around the same time.
        //It could be because it is caching something.
        //Both are O(1) complexity so they should always be within margin of error, and 4000 ns may be within margin of error.

        System.out.println("HashSet before removing " + ELEMENT + ": " + hashSet);
        long beforeHashSet = System.nanoTime();
        hashSet.remove(ELEMENT);
        long elapsedHashSetNano = System.nanoTime() - beforeHashSet;
        long elapsedHashSetMillis = TimeUnit.NANOSECONDS.toMillis(elapsedHashSetNano);
        System.out.println("HashSet after removing " + ELEMENT + ": " + hashSet);

        System.out.println("LinkedHashSet before removing " + ELEMENT + ": " + Arrays.toString(linkedHashSet.toArray()));
        long beforeLinkedHashSet = System.nanoTime();
        linkedHashSet.remove(ELEMENT);
        long elapsedLinkedHashSetNano = System.nanoTime() - beforeLinkedHashSet;
        long elapsedLinkedHashSetMillis = TimeUnit.NANOSECONDS.toMillis(elapsedLinkedHashSetNano);
        System.out.println("LinkedHashSet after removing " + ELEMENT + ": " + Arrays.toString(linkedHashSet.toArray()));

        System.out.println("It took " + elapsedHashSetNano + " ns (" + elapsedHashSetMillis + " ms) to remove the element " + ELEMENT + " from the size "  + ITERATIONS + " HashSet.");
        System.out.println("It took " + elapsedLinkedHashSetNano + " ns (" + elapsedLinkedHashSetMillis + " ms) to remove the element " + ELEMENT + " from the size "  + ITERATIONS + " LinkedHashSet.");
    }
}
