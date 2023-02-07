package com.aelous.model.entity;

import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Boundary;
import com.aelous.model.map.position.areas.impl.DuelArenaArea;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A collection that provides functionality for storing and managing mobs.
 * This list does not support the storage of elements with a value of
 * {@code null}, and maintains an extremely strict ordering of the elements.
 * This list for storing mobs will be blazingly faster than typical
 * implementations, mainly due to the fact that it uses a {@link Queue} to cache
 * the slots that mobs are removed from in order to reduce the amount of
 * lookups needed to add a new mob.
 *
 * @param <E> the type of mob being managed with this collection.
 * @author lare96 <http://github.com/lare96>
 */
public final class EntityList<E extends Entity> implements Iterable<E> {

    /**
     * The backing array of {@link Entity}s within this collection.
     */
    public E[] entities;

    /**
     * The queue containing all of the cached slots that can be assigned to
     * {@link Entity}s to prevent expensive lookups.
     */
    private final Queue<Integer> slotQueue = new ArrayDeque<>();

    /**
     * A list of integers that individually represent slots that are occupied. This list determines
     * the order in which a mob is processed without modifying the underlying array.
     */
    private final List<Integer> renderOrder = new ArrayList<>();

    /**
     * The finite capacity of this collection.
     */
    private final int capacity;

    /**
     * The size of this collection.
     */
    private int size;

    /**
     * Creates a new {@link EntityList}.
     *
     * @param capacity the finite capacity of this collection.
     */
    @SuppressWarnings("unchecked")
    public EntityList(int capacity) {
        this.capacity = ++capacity;
        this.entities = (E[]) new Entity[capacity];
        this.size = 0;
        IntStream.rangeClosed(1, capacity - 1).forEach(slotQueue::add);
    }

    /**
     * A means of shuffling the underlying {@link #renderOrder}. This
     * will change the way that a player is rendered or updated.
     */
    public void shuffleRenderOrder() {
        if (size == 0) {
            return;
        }
        Collections.shuffle(renderOrder);
        for (int i = 0; i < renderOrder.size(); i++) {
            final E e = get(renderOrder.get(i));
            if (e != null && e.isPlayer() && e.getController() instanceof DuelArenaArea)
                continue;
            if (e != null)
                e.pidOrderIndex = i;
        }
    }

    /**
     * Adds an element to this collection.
     *
     * @param e the element to add to this collection.
     * @return {@code true} if the element was successfully added, {@code false}
     * otherwise.
     */
    public boolean add(E e) {
        Objects.requireNonNull(e);

        if (isFull()) {
            return false;
        }

        if (!e.isRegistered()) {
            //System.out.println("[add] Slot was: " + slotQueue.size());
            int slot = slotQueue.remove();
            renderOrder.add(slot);
            e.pidOrderIndex = renderOrder.indexOf(slot);
            e.setRegistered(true);
            e.setIndex(slot);
            //System.out.println("[add] Slot is: " + slot);
            entities[slot] = e;
            e.onAdd();
            size++;
            return true;
        }
        return false;
    }

    /**
     * Removes an element from this collection.
     * <br> WARNING calling this during the main game loops when iterating over this list will cause a ConcurrentModificationException .. but it WONT THROW one lmfao probably due to overriding and not including jdk checks from {@link List}
     *
     * @param e the element to remove from this collection.
     * @return {@code true} if the element was successfully removed,
     * {@code false} otherwise.
     */
    public boolean remove(E e) {
        Objects.requireNonNull(e);

        if (e.getIndex() != -1 && entities[e.getIndex()] != null) {
            int renderIndexOf = renderOrder.indexOf(e.getIndex());
            //System.out.println("Removing npc");
            if (renderIndexOf != -1) {
                renderOrder.remove(renderIndexOf);
            }
            e.pidOrderIndex = -1;
            e.setRegistered(false);
            entities[e.getIndex()] = null;
            //System.out.println("[remove] Slot queue was " + slotQueue.size());
            slotQueue.add(e.getIndex());
            //System.out.println("[remove] Slot queue is " + slotQueue.size());
            e.setIndex(-1);
            if (!e.isPlayer())
                e.onRemove();
            size--;
            return true;
        }

        return false;
    }

    /**
     * Determines if this collection contains the specified element.
     *
     * @param e the element to determine if this collection contains.
     * @return {@code true} if this collection contains the element,
     * {@code false} otherwise.
     */
    public boolean contains(E e) {
        Objects.requireNonNull(e);
        return entities[e.getIndex()] != null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation will exclude all elements with a value of
     * {@code null} to avoid {@link NullPointerException}s.
     * <p>
     * <p>
     * <p>
     * UPDATED:
     * Now uses a shuffle_list to battle PID.
     * This means that all mobs/characters are always processed
     * in an random order instead of having higher priority
     * than other mobs/characters because of a higher PID.
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        for (E e : entities) {
            if (e == null) {
                continue;
            }
            action.accept(e);
        }
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final void forEachInArea(Area area, Consumer<? super E>... actions) {
        int size = entities.length;

        for (Consumer<? super E> a : actions) {
            for (int i = 0; i < size; i++) {
                Entity e = entities[i];
                if (e != null && area.contains(e.tile(),false))
                    ((Consumer<? super Entity>) a).accept(e);
            }
        }
    }

    @SafeVarargs
    public final void forEachInRegion(int region, Consumer<? super E>... actions) {
        for (Consumer<? super E> a : actions) {
            for (Entity e : entities) {
                if (e != null && e.tile().region() == region)
                    ((Consumer<? super Entity>) a).accept(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public final void forEachInBoundary(Boundary boundary, Consumer<? super E> action) {
        int size = entities.length;

        for (int i = 0; i < size; i++) {
            Entity e = entities[i];
            if (e != null && boundary.inside(e.tile()))
                ((Consumer<? super Entity>) action).accept(e);
        }
    }

    /**
     * Lazy way so you can type {@code World.getWorld().getPlayers().filter(custom).foreach} without a null check
     * instead of {@code World.getWorld().getPlayers().stream().filter(o -> Objects.nonNull(o) && abc).foreach}
     * @param predicate
     * @return
     */
    public Stream<E> filter(Predicate<? super Entity> predicate) {
        return Arrays.stream(entities).filter(mob -> Objects.nonNull(mob) && predicate.test(mob));
    }

    /**
     * Lazy way so you can type {@code World.getWorld().getPlayers().forEachFiltered(p -> p.inArea(x), p -> {})} without a null check
     * and additional forEach call instead of {@code World.getWorld().getPlayers().stream().filter(o -> Objects.nonNull(o) && abc).foreach}
     * @param predicate
     * @return
     */
    public void forEachFiltered(Predicate<? super Entity> predicate, Consumer<? super Entity> consumer) {
        Arrays.stream(entities).filter(mob -> Objects.nonNull(mob) && predicate.test(mob)).forEach(consumer);
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(entities, Spliterator.ORDERED);
    }

    /**
     * Searches the backing array for the first element encountered that matches
     * {@code filter}. This does not include elements with a value of
     * {@code null}.
     *
     * @param filter the predicate that the search will be based on.
     * @return an optional holding the found element, or an empty optional if no
     * element was found.
     */
    public Optional<E> search(Predicate<? super E> filter) {
        for (E e : entities) {
            if (e == null)
                continue;
            if (filter.test(e))
                return Optional.of(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterator<E> iterator() {
        return new MobListIterator<>(this);
    }

    /**
     * Retrieves the element on the given slot.
     *
     * @param slot the slot to retrieve the element on.
     * @return the element on the given slot or {@code null} if no element is on
     * the spot.
     */
    public E get(int slot) {
        return entities[slot];
    }

    /**
     * Determines the amount of elements stored in this collection.
     *
     * @return the amount of elements stored in this collection.
     */
    public int size() {
        return size;
    }

    /**
     * Gets the finite capacity of this collection.
     *
     * @return the finite capacity of this collection.
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Gets the remaining amount of space in this collection.
     *
     * @return the remaining amount of space in this collection.
     */
    public int spaceLeft() {
        return capacity - size;
    }

    /**
     * Is the collection full?
     *
     * @return true if collection is full, otherwise false
     */
    public boolean isFull() {
        return size + 1 >= capacity;
    }

    /**
     * Returns a sequential stream with this collection as its source.
     *
     * @return a sequential stream over the elements in this collection.
     */
    public Stream<E> stream() {
        return Arrays.stream(entities);
    }

    /**
     * Removes all of the elements in this collection and resets the
     * {@link EntityList#entities} and {@link EntityList#size}.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        forEach(this::remove);
        entities = (E[]) new Entity[capacity];
        size = 0;
    }

    /**
     * Retrieves the underlying list of integers determine the slots that are rendered first.
     *
     * @return this returns a copy of the list of slots that are rendered. Order is important.
     */
    public List<Integer> getRenderOrder() {
        return new ArrayList<>(renderOrder);
    }

    public List<Integer> getRenderOrderInternal() {
        return renderOrder;
    }

    public Stream<E> nonNullStream() {
        return Stream.of(entities).filter(Objects::nonNull);
    }

    /**
     * An {@link Iterator} implementation that will iterate over the elements in
     * a mob list.
     *
     * @param <E> the type of mob being iterated over.
     * @author lare96 <http://github.com/lare96>
     */
    private static final class MobListIterator<E extends Entity> implements Iterator<E> {

        /**
         * The {@link EntityList} that is storing the elements.
         */
        private final EntityList<E> list;

        /**
         * The current index that the iterator is iterating over.
         */
        private int index;

        /**
         * The last index that the iterator iterated over.
         */
        private int lastIndex = -1;

        /**
         * Creates a new {@link MobListIterator}.
         *
         * @param list the list that is storing the elements.
         */
        public MobListIterator(EntityList<E> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return !(index + 1 > list.capacity());
        }

        @Override
        public E next() {
            if (index >= list.capacity()) {
                throw new ArrayIndexOutOfBoundsException("There are no " + "elements left to iterate over!");
            }
            lastIndex = index;
            index++;
            return list.entities[lastIndex];
        }

        @Override
        public void remove() {
            if (lastIndex == -1) {
                throw new IllegalStateException("This method can only be " + "called once after \"next\".");
            }
            list.remove(list.entities[lastIndex]);
            lastIndex = -1;
        }

    }
}
