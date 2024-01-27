package com.cryptic.utility;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Jonathan on 3/24/2017.
 *
 * lmfao is this from os-scape? this is my code l0l, yeah funny because i have alot of os-scape / runite stuff impl'd in here
 * pathing etc,
 *
 * htis my friend Jonathan Beaudoin, guess he didnt give me credit l0l, sadge af, this is bottlenecking?
 * nah just this is as fast as it gets so nothing else can do about it, strange, is there anythign else on the gamethread tha
 * flags? how do you identify problems on there
 *
 * nothing else but login a player and u can see
 */
public final class Indexer<E> implements Iterable<E> {

    private final Object[] arr;

    public Indexer(int minIndex, int capacity) {
        arr = new Object[capacity];
    }

    public Indexer(int capacity) {
        this(0, capacity);
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) arr[index];
    }

    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        Object previous = arr[index];
        arr[index] = element;
        return (E) previous;
    }

    public boolean contains(E element) {
        if (element == null)
            return false;

        for (E e : this) {
            if (element.equals(e))
                return true;
        }

        return false;
    }

    public void clear() {
        Arrays.fill(arr, null);
    }

    public int size() {
        return arr.length;
    }

    public int nextIndex() {
        for (int i = 0; i < arr.length; i++) {
            if (null == arr[i])
                return i;
        }
        throw new IllegalStateException("Out of indices!");
    }

    @Override
    public Iterator<E> iterator() {
        iterator.pointer = 0;
        return iterator;
    }

    private final IndexerIterator iterator = new IndexerIterator();

    public boolean isEmpty() {
        return arr.length == 0;
    }

    private final class IndexerIterator implements Iterator<E> {

        private int pointer;

        @Override
        public boolean hasNext() {
            return arr.length > 0 && pointer < arr.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            Object o = arr[pointer++];
            if (o == null && hasNext())
                return next();
            return (E) o;
        }

        @Override
        public void remove() {
            set(pointer - 1, null);
        }

    }

    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

}
