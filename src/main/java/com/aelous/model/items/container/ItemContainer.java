package com.aelous.model.items.container;


import com.aelous.GameServer;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainerListener;
import com.aelous.model.items.container.bank.Bank;
import com.aelous.utility.Tuple;
import com.google.gson.annotations.Expose;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * An abstraction game representing a group of {@link Item}s.
 *
 * @author lare96 <http://github.com/lare96>
 */
public class ItemContainer implements Iterable<Item> {

    private static final Logger log = LogManager.getLogger(ItemContainer.class);
    private static final Marker marker = MarkerManager.getMarker("ItemContainer");

    public String toStringDetail() {
        return Arrays.toString(Arrays.stream(items).filter(Objects::nonNull).map(i -> {
            if (i.unnote().getValue() == 0)
                return i.toShortString();
            else
                return i.toShortString() + " (value:" + ((long)i.getAmount() * (long)i.unnote().getValue()) + (i.getAmount() > 1 ? " cost:" + i.unnote().getValue() : "") + ")";
        }).toArray());
    }

    /** An {@link Iterator} implementation for this container. */
    private static final class ItemContainerIterator implements Iterator<Item> {

        /** The container instance to iterate over. */
        private final ItemContainer container;

        /** The current index being iterated over. */
        private int index;

        /** The last index that was iterated over. */
        private int lastIndex = -1;

        /** Creates a new {@link ItemContainerIterator}. */
        ItemContainerIterator(ItemContainer container) {
            this.container = container;
        }

        @Override
        public boolean hasNext() {
            return (index + 1) <= container.capacity;
        }

        @Override
        public Item next() {
            checkState(index < container.capacity, "no more elements left to iterate");

            lastIndex = index;
            index++;
            return container.items[lastIndex];
        }

        @Override
        public void remove() {
            checkState(lastIndex != -1, "can only be called once after 'next'");

            Item oldItem = container.items[lastIndex];
            container.items[lastIndex] = null;
            container.fireItemUpdatedEvent(oldItem, null, lastIndex, true);

            index = lastIndex;
            lastIndex = -1;
        }
    }

    /** An enumerated type defining policies for stackable {@link Item}s. */
    public enum StackPolicy {

        /**
         * The {@code STANDARD} policy, items are only stacked if they are defined as
         * stackable in their {@link ItemDefinition} table.
         */
        STANDARD,

        /**
         * The {@code ALWAYS} policy, items are always stacked regardless of their
         * {@link ItemDefinition} table.
         */
        ALWAYS,

        /**
         * The {@code NEVER} policy, items are never stacked regardless of their
         * {@link ItemDefinition} table.
         */
        NEVER
    }

    /**
     * An {@link ArrayList} of {@link ItemContainerListener}s listening for various
     * events.
     */
    private final List<ItemContainerListener> listeners = new ArrayList<>();

    /** The capacity of this container. */
    private final int capacity;

    /** The policy of this container. */
    private final StackPolicy policy;

    /** The {@link Item}s within this container. */
    @Expose
    private Item[] items;

    /** If events are currently being fired. */
    private boolean firingEvents = true;

    /** Creates a new {@link ItemContainer}. */
    public ItemContainer(int capacity, StackPolicy policy, Item[] items) {
        this.capacity = capacity;
        this.policy = policy;
        this.items = items;
    }

    /** Creates a new {@link ItemContainer}. */
    public ItemContainer(int capacity, StackPolicy policy) {
        this(capacity, policy, new Item[capacity]);
    }

    /**
     * Iterates through all of the {@link Item}s within this container and performs
     * {@code action} on them, skipping empty indexes ({@code null} values) as they
     * are encountered.
     */
    @Override
    public final void forEach(Consumer<? super Item> action) {
        Objects.requireNonNull(action);
        for (Item item : items) {
            if (item != null) {
                action.accept(item);
            }
        }
    }

    @Override
    public final Spliterator<Item> spliterator() {
        return Spliterators.spliterator(items, Spliterator.ORDERED);
    }

    @Override
    public final Iterator<Item> iterator() {
        return new ItemContainerIterator(this);
    }

    /**
     * @return A stream associated with the elements in this container, built using
     *         the {@code spliterator()} implementation.
     */
    public final Stream<Item> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Attempts to deposit {@code item} into this container.
     *
     * @param item The {@link Item} to deposit.
     * @return {@code true} the {@code Item} was added, {@code false} if there was
     *         not enough space left.
     */
    public boolean add(Item item) {
        return add(item, -1, true);
    }

    public Result add0(Item item) {
        return add0(item, -1, true);
    }

    /**
     * Attempts to deposit {@code item} into this container.
     * WARNING: do not use the ItemContainer add methods for depositing to the bank, use depositFromNothing.
     * @param item The {@link Item} to deposit.
     * @param refresh        The condition if we will be refreshing our container.
     * @return {@code true} the {@code Item} was added, {@code false} if there was
     *         not enough space left.
     */
    public boolean add(Item item, boolean refresh) {
        return add(item, -1, refresh);
    }

    /**
     * Attempts to deposit {@code item} into this container.
     * WARNING: do not use the ItemContainer add methods for depositing to the bank, use depositFromNothing.
     * @param item The {@link Item} to deposit.
     * @param slot The slot to deposit the item too.
     * @return {@code true} the {@code Item} was added, {@code false} if there was
     *         not enough space left.
     */
    public boolean add(Item item, int slot) {
        return add(item, slot, true);
    }

    /**
     * Attempts to deposit {@code item} into this container.
     *
     * @param id     the id of the item.
     * @return {@code true} the item was added, {@code false} if there was not
     *         enough space left.
     */
    public boolean add(int id) {
        return add(new Item(id, 1));
    }

    /**
     * Attempts to deposit {@code item} into this container.
     *
     * @param id     the id of the item.
     * @param amount the amount of the item.
     * @return {@code true} the item was added, {@code false} if there was not
     *         enough space left.
     */
    public boolean add(int id, int amount) {
        return add(new Item(id, amount));
    }

    public boolean add(Item item, boolean refresh, boolean stack) {
        return add(item, -1, refresh, stack).success();
    }

    public boolean add(Item item, int preferredIndex, boolean refresh) {
        return add(item, preferredIndex, refresh, false).success();
    }

    public Result add0(Item item, int preferredIndex, boolean refresh) {
        //System.out.println("Adding...");
        return add(item, preferredIndex, refresh, false);
    }

    /**
     * Attempts to deposit {@code item} into this container, preferably at
     * {@code preferredIndex}.
     * WARNING: do not use the ItemContainer add methods for depositing to the bank, use depositFromNothing.
     * @param item           The {@link Item} to deposit.
     * @param preferredIndex The preferable index to deposit {@code item} to.
     * @param refresh        The condition if we will be refreshing our container.
     * @return {@code true} if the {@code Item} was added, {@code false} if there
     *         was not enough space left.
     */
    public Result add(final Item item, int preferredIndex, boolean refresh, boolean stack) {
        checkArgument(preferredIndex >= -1, "invalid index identifier");
        if (preferredIndex >= items.length) {
            if (getFreeSlots() == 0) {
                return new Result(item.getAmount(), 0, 0);
            } else {
                preferredIndex = nextFreeSlot();
            }
        }

        //System.out.println("adding to container "+item.toLongString());

        boolean stackable = (stack || (policy.equals(StackPolicy.STANDARD) && item.stackable()) || policy.equals(StackPolicy.ALWAYS));

        // go to an existing stack, or fallback to given preferred index
        int existingSlot = getSlot(item.getId());
        if (stackable && !stack && existingSlot != -1) {
            preferredIndex = existingSlot;
        }
        if (preferredIndex != -1) {
            // else if its a non stackables, double  check you dont overwrite a different item in the given slot
            preferredIndex = items[preferredIndex] != null && !items[preferredIndex].matchesId(item.getId()) ? -1 : preferredIndex;
        }

        preferredIndex = preferredIndex == -1 ? nextFreeSlot() : preferredIndex;

        if (preferredIndex == -1) { // Not enough space in container.
            fireCapacityExceededEvent();
            return new Result(item.getAmount(), 0, 0);
        }

        if (stackable) {
            Item current = items[preferredIndex];
            if (current != null) {
                if (!current.matchesId(item.getId()))
                    throw new RuntimeException("Trying to increase stack of different items! " + current + " vs " + item);

                long newAmount = (long) current.getAmount() + item.getAmount();
                if (newAmount > Integer.MAX_VALUE)
                    return new Result(item.getAmount(), 0, preferredIndex);

                items[preferredIndex] = current.createAndIncrement(item.getAmount());
            } else {
                items[preferredIndex] = item;
            }
            fireItemUpdatedEvent(current, items[preferredIndex], preferredIndex, refresh);
            return new Result(item.getAmount(), item.getAmount(), preferredIndex);
        } else {
            int remaining = remaining();
            int until = Math.min(remaining, item.getAmount());
            List<Integer> slots = new LinkedList<>();

            for (int index = 0; index < until; index++) {
                preferredIndex = (preferredIndex > capacity || preferredIndex < 0 || items[preferredIndex] == null) ? preferredIndex : nextFreeSlot();
                if (preferredIndex == -1) {//Couldn't find an empty spot.
                    fireCapacityExceededEvent();
                    return new Result(item.getAmount(), 0, 0);
                }
                item.setAmount(1);
                items[preferredIndex] = item;
                slots.add(preferredIndex);
                fireItemUpdatedEvent(null, item, preferredIndex++, refresh);
            }
            return new Result(item.getAmount(), item.getAmount(), slots.stream().mapToInt(i -> i).toArray());
        }
    }

    /**
     * Attempts to deposit {@code items} in bulk into this container.
     *
     * @param items The {@link Item}s to deposit.
     * @return {@code true} if at least {@code 1} of the {@code Item}s were added,
     *         {@code false} if none could be added.
     */
    public boolean addAll(Collection<? extends Item> items) {
        if (items.size() == 1) { // Bulk operation on singleton list? No thanks..
            Optional<? extends Item> item = items.stream().filter(Objects::nonNull).findFirst();
            return item.isPresent() && add(item.get());
        }

        firingEvents = false;

        boolean added = false;
        try {
            for (Item item : items) {
                if (item == null) {
                    continue;
                }
                if (add(item, -1, false)) {
                    added = true;
                }
            }
        } finally {
            firingEvents = true;
        }
        fireBulkItemsUpdatedEvent();
        return added;
    }

    /**
     * Attempts to deposit {@code items} in bulk into this container.
     *
     * @param items The {@link Item}s to deposit.
     * @return {@code true} if at least {@code 1} of the {@code Item}s were added,
     *         {@code false} if none could be added.
     */
    public boolean addAll(Item... items) {
        return addAll(Arrays.asList(items));
    }

    /**
     * Attempts to deposit {@code items} in bulk into this container.
     *
     * @param items The {@link Item}s to deposit.
     * @return {@code true} if at least {@code 1} of the {@code Item}s were added,
     *         {@code false} if none could be added.
     */
    public boolean addAll(ItemContainer items) {
        return addAll(items.items);
    }

    /**
     * Attempts to withdraw {@code item} from this container.
     *
     * @param item The {@link Item} to withdraw.
     * @return {@code true} if the {@code Item} was removed, {@code false} if it
     *         isn't present in this container.
     */
    public boolean remove(Item item) {
        return remove(item, -1, true);
    }

    /**
     * Attempts to withdraw {@code item} from this container.
     *
     * @param item The {@link Item} to withdraw.
     * @param refresh The condition if we will be refreshing our container.
     * @return {@code true} if the {@code Item} was removed, {@code false} if it
     *         isn't present in this container.
     */
    public boolean remove(Item item, boolean refresh) {
        return remove(item, -1, refresh);
    }

    /**
     * Attempts to withdraw {@code item} from this container, preferably from
     * {@code preferredIndex}.
     *
     * @param item           The {@link Item} to withdraw.
     * @param preferredIndex The preferable index to withdraw {@code item} from.
     * @return {@code true} if the {@code Item} was removed, {@code false} if it
     *         isn't present in this container.
     */
    public boolean remove(Item item, int preferredIndex) {
        return remove(item, preferredIndex, true);
    }

    public boolean remove(int id) {
        return remove(new Item(id, 1));
    }

    public boolean remove(int id, int amount) {
        return remove(new Item(id, amount));
    }

    public boolean remove(Item item, int preferredIndex, boolean refresh) {
        ///System.out.println("Removing...");
        return remove(item, preferredIndex, refresh, true);
    }

    /**
     * Attempts to withdraw {@code item} from this container, preferably from
     * {@code preferredIndex}.
     *
     * @param item           The {@link Item} to withdraw.
     * @param preferredIndex The preferable index to withdraw {@code item} from.
     * @param refresh        The condition if we will be refreshing our container.
     * @param removeAll      Determines if the items should be removed from all
     *                       slots or only the specified s
     * @return {@code true} if the {@code Item} was removed, {@code false} if it
     *         isn't present in this container.
     */
    public boolean remove(Item item, int preferredIndex, boolean refresh, boolean removeAll) {
        checkArgument(preferredIndex >= -1, "invalid index identifier");

        //System.out.println("Are we even here");
        boolean stackable = ((policy.equals(StackPolicy.STANDARD) && item.stackable())
            || policy.equals(StackPolicy.ALWAYS));

        if (stackable) {
            preferredIndex = getSlot(item.getId());
        } else {
            preferredIndex = preferredIndex == -1 ? getSlot(item.getId()) : preferredIndex;

            if (preferredIndex != -1 && items[preferredIndex] == null) {
                preferredIndex = -1;
            }
        }

        if (preferredIndex == -1) { // Item isn't present within this container.
            return false;
        }

        if (stackable) {
            Item current = items[preferredIndex];
            //System.out.println("current: "+current.toLongString());
            if (current.getAmount() > item.getAmount()) {
                items[preferredIndex] = current.createAndDecrement(item.getAmount());
                //System.out.println("current amount higher as item amount print: "+items[preferredIndex].toLongString());
            } else {
                if (allowZero()) {
                    items[preferredIndex] = current.createWithAmount(0);
                    //System.out.println("Allow 0.");
                } else {
                    items[preferredIndex] = null;
                    //System.out.println("Otherwise item becomes null");
                }
            }
            fireItemUpdatedEvent(current, items[preferredIndex], preferredIndex, refresh);
            //System.out.println("Update event: "+items[preferredIndex].toLongString());
        } else {
            int until = removeAll ? count(item.getId()) : items[preferredIndex].getAmount();
            until = Math.min(item.getAmount(), until);

            //System.out.println("hmmm: "+items[preferredIndex].getAmount());
            for (int index = 0; index < until && index < capacity; index++) {
                if (removeAll) {
                    //System.out.println("REMOVE ALL");
                    if (preferredIndex < 0 || preferredIndex >= capacity) {
                        preferredIndex = getSlot(item.getId());
                    } else if (items[preferredIndex] == null) {
                        preferredIndex = getSlot(item.getId());
                    } else if (items[preferredIndex].getId() != item.getId()) {
                        preferredIndex = getSlot(item.getId());
                    }
                    if (preferredIndex == -1) {
                        break;
                    }
                }
                Item oldItem = items[preferredIndex];
                //System.out.println("oldItem print: "+oldItem.toLongString());
                if (allowZero()) {
                    items[preferredIndex] = oldItem.createWithAmount(0);
                } else {
                    items[preferredIndex] = null;
                }
                fireItemUpdatedEvent(oldItem, null, preferredIndex++, refresh);
            }
        }
        return true;
    }

    /**
     * Attempts to withdraw {@code items} in bulk from this container.
     *
     * @param items The {@link Item}s to withdraw.
     * @return {@code true} if at least {@code 1} of the {@code Item}s were
     *         withdraw, {@code false} if none could be removed.
     */
    public boolean removeAll(Collection<? extends Item> items) {
        if (items.size() == 1) { // Bulk operation on singleton list? No thanks..
            Optional<? extends Item> item = items.stream().filter(Objects::nonNull).findFirst();
            return item.isPresent() && remove(item.get());
        }

        firingEvents = false;
        boolean removed = false;
        try {
            for (Item item : items) {
                if (item == null) {
                    continue;
                }
                if (remove(item, -1, false)) {
                    removed = true;
                }
            }
        } finally {
            firingEvents = true;
        }
        fireBulkItemsUpdatedEvent();
        return removed;
    }

    /**
     * Attempts to withdraw {@code items} in bulk from this container.
     *
     * @param items The {@link Item}s to withdraw.
     * @return {@code true} if at least {@code 1} of the {@code Item}s were
     *         withdraw, {@code false} if none could be removed.
     */
    public boolean removeAll(Item... items) {
        return removeAll(Arrays.asList(items));
    }

    /**
     * Attempts to withdraw {@code items} in bulk from this container.
     *
     * @param items The {@link Item}s to withdraw.
     * @return {@code true} if at least {@code 1} of the {@code Item}s were
     *         withdraw, {@code false} if none could be removed.
     */
    public boolean removeAll(ItemContainer items) {
        return removeAll(items.items);
    }

    /**
     * Gets the total worth of the container using the item's values.
     *
     * @return The total container worth.
     */
    public long containerValue() {
        long value = 0;
        for (final Item item : items) {
            if (item == null) {
                continue;
            }
            final int amount = item.getAmount();
            final long price = Math.max(0, item.unnote().getValue());
            value += price * amount;
            if (value < 0) {
                return Long.MAX_VALUE;
            }
        }
        return value;
    }

    /**
     * Computes the next free ({@code null}) index in this container.
     *
     * @return The free index, {@code -1} if no free indexes could be found.
     */
    public final int nextFreeSlot() {
        for (int index = 0; index < capacity; index++) {
            if (items[index] == null) {
                return index;
            }
        }
        return -1;
    }

    public int getAmountOf(int itemId) {
        for (Item item : items) {
            if (item != null && itemId == item.getId()) {
                return item.getAmount();
            }
        }
        return 0;
    }

    /**
     * Computes the first index found that {@code id} is in.
     *
     * @param id The identifier to compute for.
     * @return The first index found, {@code -1} if no {@link Item} with {@code
     * id}  is in this container.
     */
    public final int getSlot(int id) {
        for (int index = 0; index < items.length; index++) {
            if (items[index] != null && items[index].getId() == id) {
                return index;
            }
        }
        return -1;
    }

    public int count(int item) {
        long count = 0;

        // Stackability check here
        boolean stacks = new Item(item).stackable();

        for (Item i : items) {
            if (i != null && i.getId() == item) {
                count += i.getAmount();

                // Avoid breaking the game by returning a 'fake' count on stackables (indicates game error)
                if (stacks)
                    break;
            }
        }

        return (int) Math.min(Integer.MAX_VALUE, count);
    }

    public ArrayList<Item> collectItems(int... ids) {
        ArrayList<Item> list = new ArrayList<>(ids.length);
        for(Item item : items) {
            if(item == null)
                continue;
            for(int id : ids) {
                if(item.getId() == id)
                    list.add(item);
            }
        }
        return list.isEmpty() ? null : list;
    }

    public int count(Integer... matches) {
        List<Integer> list = Arrays.asList(matches);

        long count = 0;

        for (Item i : items) {
            if (i != null && list.contains(i.getId()))
                count += i.getAmount();
        }

        return (int) Math.min(Integer.MAX_VALUE, count);
    }

    /**
     * Computes the identifier of the {@link Item} on {@code index}.
     *
     * @param index The index to compute the identifier for.
     * @return The identifier wrapped in an optional.
     */
    public final Optional<Integer> computeIdForIndex(int index) {
        return retrieve(index).map(Item::getId);
    }

    /**
     * Replaces the first occurrence of the {@link Item} having the identifier
     * {@code oldId} with {@code newId}.
     *
     * @param oldId   The old identifier to replace.
     * @param newId   The new identifier to replace.
     * @param refresh The condition if the coontainer will be refreshed.
     * @return {@code true} if the replace operation was successful, {@code
     * false otherwise}.
     */
    public final boolean replace(int oldId, int newId, int index, boolean refresh) {
        Item oldItem = items[index];
        Item newItem = oldItem.createWithId(newId);

        return remove(oldItem, index, refresh) && add(newItem, index, refresh);
    }


    /**
     * Replaces the first occurrence of the {@link Item} having the identifier
     * {@code oldId} with {@code newId}.
     *
     * @param oldId   The old identifier to replace.
     * @param newId   The new identifier to replace.
     * @param refresh The condition if the coontainer will be refreshed.
     * @return {@code true} if the replace operation was successful, {@code
     * false otherwise}.
     */
    public final boolean replace(int oldId, int newId, boolean refresh) {
        int index = getSlot(oldId);
        if (index == -1) {
            return false;
        }

        Item oldItem = items[index];
        Item newItem = oldItem.createWithId(newId);

        //System.out.println("old: "+oldItem.toLongString());
        //System.out.println("new: "+newItem.toLongString());
        //System.out.println("index: "+index);

        return remove(oldItem, index, refresh) && add(newItem, index, refresh);
    }

    /**
     * Replaces the first occurrence of the {@link Item} having the identifier
     * {@code oldId} with {@code newId}.
     *
     * @param refresh The condition if the coontainer will be refreshed.
     * @return {@code true} if the replace operation was successful, {@code
     * false otherwise}.
     */
    public final boolean replace(Item first, Item second, boolean refresh) {
        int index = getSlot(first.getId());

        if (index == -1) {
            return false;
        }

        Item oldItem = items[index];
        return remove(oldItem, index, refresh) && add(second, index, refresh);
    }

    /**
     * Replaces all occurrences of {@link Item}s having the identifier {@code
     * oldId} with {@code newId}.
     *
     * @param oldId The old identifier to replace.
     * @param newId The new identifier to replace.
     * @return {@code true} if the replace operation was successful at least once,
     *         {@code false otherwise}.
     */
    public final boolean replaceAll(int oldId, int newId) {
        boolean replaced = false;

        firingEvents = false;
        try {
            while (replace(oldId, newId, false)) {
                replaced = true;
            }
        } finally {
            firingEvents = true;
        }
        fireBulkItemsUpdatedEvent();
        return replaced;
    }

    /**
     * Basically just the space check code of #add method
     */
    public final boolean canFit(Item item) {
        if (item == null)
            return false;
        boolean stackable = ((policy.equals(StackPolicy.STANDARD) && item.stackable())
            || policy.equals(StackPolicy.ALWAYS));

        if (stackable) {
            int index = getSlot(item.getId());
            if (index == -1) { // doesnt exist, all good
                return hasFreeSlots(1);
            }

            Item existing = items[index];
            // check we won't exceed max value
            return ((long) existing.getAmount() + (long) item.getAmount()) <= Integer.MAX_VALUE;
        } else {
            // can fit each of the non-stacking items
            return hasFreeSlots(item.getAmount());
        }
    }

    /**
     * Determines if this container has the capacity for multiple {@code item}s. Example: adding skill cape+hood.
     *
     * @param item The {@link Item} to determine this for.
     * @return {@code true} if {@code item} can be added, {@code false} otherwise.
     */
    public final boolean hasCapacity(Item... item) {
        for (Item item1 : item) {
            if (!canFit(item1))
                return false;
        }
        return true;
    }

    /**
     * Creates a copy of the underlying container and removes the items specified
     * from it and after tries to deposit the specified items to it.
     *
     * @param add    the items to deposit to this container.
     * @param remove the items that should be removed before adding.
     * @return {@code true} if {@code item} can be added, {@code false} otherwise.
     */
    public final boolean hasCapacityAfter(Item[] add, Item... remove) {
        ItemContainer container = new ItemContainer(capacity, policy, toArray());
        container.removeAll(Arrays.copyOf(remove, remove.length));
        return container.hasCapacity(add);
    }

    /**
     * Determines if this container contains {@code id}.
     *
     * @param id The identifier to check this container for.
     * @return {@code true} if this container has {@code id}, {@code false}
     *         otherwise.
     */
    public boolean contains(int id) {
        for (Item item : items) {
            if (item != null && id == item.getId()) {
                return true;
            }
        }
        return false;
    }

    public ItemDefinition getDef(int slot) {
        Item item = get(slot);
        return item == null ? null : item.definition();
    }

    public boolean hasId(int id) {
        return findItem(id) != null;
    }

    /**
     * Determines if this container contains all {@code identifiers}.
     *
     * @param identifiers The identifiers to check this container for.
     * @return {@code true} if this container has all {@code identifiers},
     *         {@code false} otherwise.
     */
    public final boolean containsAll(int... identifiers) {
        for (int id : identifiers) {
            if (!contains(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if this container contains any {@code identifiers}.
     *
     * @param identifiers The identifiers to check this container for.
     * @return {@code true} if this container has any {@code identifiers},
     *         {@code false} otherwise.
     */
    public final boolean containsAny(int... identifiers) {
        for (int id : identifiers) {
            if (contains(id)) {
                return true;
            }
        }
        return false;
    }

    /** @return {@code true} if this container has the {@code item} */
    public final boolean contains(Item item) {
        return item != null && contains(item.getId(), item.getAmount());
    }

    /** @return {@code true} if this container has id with amount */
    public final boolean contains(int id, int amount) {
        for (Item item : items) {
            if (item != null && id == item.getId()) {
                amount -= item.getAmount();
                if (amount <= 0)
                    return true;
            }
        }
        return false;
    }

    /** @return {@code true} if this container has all {@code items} */
    public final boolean containsAll(Item... items) {
        for (Item item : items) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    /** @return {@code true} if this container has all {@code items} */
    public final boolean containsAll(Collection<Item> items) {
        for (Item item : items) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    /** @return {@code true} if this container has all {@code items} */
    public final boolean containsAny(Item... items) {
        for (Item item : items) {
            if (contains(item)) {
                return true;
            }
        }
        return false;
    }

    /** @return {@code true} if this container has all {@code items} */
    public final boolean containsAny(Collection<Item> items) {
        for (Item item : items) {
            if (contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends the items on the itemcontainer.
     *
     * @param widget The widget to send the {@code Item}s on.
     */
    public void refresh(Player player, int widget) {
        player.getPacketSender().sendItemOnInterface(widget, items);
        onRefresh();
    }

    /** Any functionality that should occur when refreshed. */
    public void onRefresh() {
        /* can be overriden */
    }

    /**
     * Swaps the {@link Item}s on {@code oldIndex} and {@code newIndex}.
     *
     * @param oldIndex The old index.
     * @param newIndex The new index.
     */
    public final void swap(int oldIndex, int newIndex) {
        swap(false, oldIndex, newIndex, true);
    }

    /**
     * Swaps the {@link Item}s on {@code oldIndex} and {@code newIndex}.
     *
     * @param insert   If the {@code Item} should be inserted.
     * @param oldIndex The old index.
     * @param newIndex The new index.
     * @param refresh  The condition that determines if we will refresh the
     *                 container.
     */
    public final void swap(boolean insert, int oldIndex, int newIndex, boolean refresh) {
        if (insert) {
            insert(oldIndex, newIndex, refresh);
        } else {
            swap(oldIndex, newIndex, refresh);
        }
    }

    public final void swap(int oldIndex, int newIndex, boolean refresh) {
        checkArgument(oldIndex >= 0 && oldIndex < capacity, "[swap] oldIndex out of range - [old=" + oldIndex + ", new="
            + newIndex + ", refresh=" + refresh + ", size=" + size() + ", capacity=" + capacity + "]");
        checkArgument(newIndex >= 0 && newIndex < capacity, "[swap] newIndex out of range - [old=" + oldIndex + ", new="
            + newIndex + ", refresh=" + refresh + ", size=" + size() + ", capacity=" + capacity + "]");

        Item itemOld = items[oldIndex];
        Item itemNew = items[newIndex];
        if (this instanceof Bank && (itemOld == null || itemNew == null)) {
            log.warn(marker, "swap FAIL "+oldIndex+" "+newIndex+" "+itemOld+" -> "+itemNew);
            refresh(); // sync up the UI with rejected item placement - client might lag behind
            return;
        }

        items[oldIndex] = itemNew;
        items[newIndex] = itemOld;

        fireItemUpdatedEvent(itemOld, items[oldIndex], oldIndex, refresh);
        fireItemUpdatedEvent(itemNew, items[newIndex], newIndex, refresh);
    }

    public final void insert(int oldIndex, int newIndex, boolean refresh) {
        checkArgument(oldIndex >= 0 && oldIndex < capacity, "[insert] oldIndex out of range - [old=" + oldIndex
            + ", new=" + newIndex + ", refresh=" + refresh + ", size=" + size() + ", capacity=" + capacity + "]");
        checkArgument(newIndex >= 0 && newIndex < capacity, "[insert] newIndex out of range - [old=" + oldIndex
            + ", new=" + newIndex + ", refresh=" + refresh + ", size=" + size() + ", capacity=" + capacity + "]");

        if (newIndex > oldIndex) {
            for (int index = oldIndex; index < newIndex; index++) {
                swap(index, index + 1, refresh);
            }
        } else if (oldIndex > newIndex) {
            for (int index = oldIndex; index > newIndex; index--) {
                swap(index, index - 1, refresh);
            }
        }
    }

    /**
     * Percolates the null indices to the end of the stack.
     */
    public void shift() {
        Item[] newItems = new Item[capacity];
        int newIndex = 0;

        for (Item item : items) {
            if (item == null) {
                continue;
            }
            newItems[newIndex++] = item;
        }

        setItems(newItems);
    }

    /**
     * Adds a set of items into the inventory.
     *
     * @param item
     * the set of items to add.
     */
    public void addItemSet(Item[] item, Boolean refresh) {
        for (Item addItem : item) {
            if (addItem == null) {
                continue;
            }
            add(addItem, refresh);
        }
    }

    /**
     * Sets the container of items to {@code items}. The container will not hold any
     * references to the array, nor the item instances in the array.
     *
     * @param items the new array of items, the capacities of this must be equal to
     *              or lesser than the container.
     */
    public final void setItems(Item[] items, boolean copy) {
        checkArgument(items.length <= capacity);
        clear();
        for (int i = 0; i < items.length; i++) {
            this.items[i] = items[i] == null ? null : copy ? items[i].copy() : items[i];
        }
        fireBulkItemsUpdatedEvent();
    }

    public final void setItems(Item[] items) {
        setItems(items, true);
    }

    public final void set(Item[] toSet) {
        items = toSet;
    }

    /**
     * Returns a <strong>shallow copy</strong> of the backing array. Changes made to
     * the returned array will not be reflected on the backing array.
     *
     * @return A shallow copy of the backing array.
     */
    public final Item[] toArray() {
        return Arrays.copyOf(items, items.length);
    }

    public final List<Item> toList() {
        return Arrays.asList(toArray());
    }

    public final Item[] toNonNullArray() {
        if (size() == 0) {
            return new Item[0];
        }

        final List<Item> items = new ArrayList<>(size());

        for (Item item : getItems()) {
            if (item == null) {
                continue;
            }

            items.add(item);
        }

        return items.toArray(new Item[items.size()]);
    }

    /**
     * Sets the {@code index} to {@code item}.
     *
     * @param index   The index to set.
     * @param item    The {@link Item} to set on the index.
     * @param refresh The condition if the container must be refreshed.
     */
    public void set(int index, Item item, boolean refresh) {
        Item oldItem = items[index];
        items[index] = item;
        fireItemUpdatedEvent(oldItem, items[index], index, refresh);
    }

    /**
     * Retrieves the item located on {@code index}.
     *
     * @param index the index to get the item on.
     * @return the item on the index, or {@code null} if no item exists on the
     *         index.
     */
    public final Optional<Item> retrieve(int index) {
        if (index >= 0 && index < items.length)
            return Optional.ofNullable(items[index]);
        return Optional.empty();
    }

    /**
     * Consumes an action if the {@code index} is a valid item index in this
     * container.
     *
     * @param index the index to get the item on.
     */
    public final void ifPresent(int index, Consumer<Item> action) {
        if (index >= 0 && index < items.length)
            action.accept(items[index]);
    }

    /**
     * Gets the {@link Item} located on {@code index}.
     *
     * @param slot The index to get the {@code Item} on.
     * @return The {@code Item} instance, {@code null} if the index is empty.
     */
    public Item get(int slot) {
        if (slot < 0 || slot >= items.length) {
            return null;
        }
        return items[slot];
    }

    /**
     * Gets the item id located on {@code index}.
     *
     * @param index The index to get the {@code Item} on.
     * @return The {@code Item} instance, {@code null} if the index is empty.
     */
    public final int getId(int index) {
        if (items[index] == null) {
            return -1;
        }
        return items[index].getId();
    }

    /**
     * Searches and returns the first item found with {@code id}.
     *
     * @param id the identifier to search this container for.
     * @return the item wrapped within an optional, or an empty optional if no item
     *         was found.
     */
    public Optional<Item> search(int id) {
        return stream().filter(i -> i != null && id == i.getId()).findFirst();
    }

    /**
     * Searches and returns the first item found with {@code id} and {@code
     * amount}.
     *
     * @param item the item to search this container for.
     * @return the item wrapped within an optional, or an empty optional if no item
     *         was found.
     */
    public Optional<Item> search(Item item) {
        return stream().filter(i -> i != null && item.getId() == i.getId() && item.getAmount() == i.getAmount())
            .findFirst();
    }

    /**
     * Returns {@code true} if {@code index} is occupied (non-{@code null}).
     */
    public final boolean indexOccupied(int index) {
        return retrieve(index).isPresent();
    }

    /**
     * Returns {@code true} if {@code index} is not occupied ({@code null}).
     */
    public final boolean indexFree(int index) {
        return !indexOccupied(index);
    }

    /**
     * Creates a copy of the underlying item container.
     *
     * @return a copy of the unterlying item container.
     */
    public ItemContainer copy() {
        ItemContainer container = new ItemContainer(this.capacity, this.policy, this.toArray());
        this.listeners.forEach(container::addListener);
        return container;
    }

    public Item[] getCopiedItems() {
        Item[] it = new Item[items.length];
        for (int i = 0; i < it.length; i++) {
            if (items[i] == null)
                continue;
            it[i] = items[i].copy();
        }
        return it;
    }

    /**
     * Gets the valid items in the container,
     * @return items in a list format.
     */
    public ArrayList<Item> getValidItems() {
        ArrayList<Item> items = new ArrayList<Item>();
        for (Item item : this.items) {
            if (item != null && item.isValid()) {
                items.add(item);
            }
        }
        return items;
    }

    public Item[] copyValidItemsArray() {
        List<Item> items = getValidItems();
        Item[] array = new Item[items.size()];
        for (int i = 0; i < items.size(); i++) {
            array[i] = new Item(items.get(i).getId(), items.get(i).getAmount());
        }
        return array;
    }

    /** Removes all the items from this container. */
    public void clear() {
        clear(true);
    }

    /** Removes all the items from this container. */
    public final void clear(boolean refresh) {
        Arrays.fill(items, null);
        if (refresh)
            fireBulkItemsUpdatedEvent();
    }

    /** @return {@code true} if this container is empty */
    public final boolean isEmpty() {
        return size() == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * Adds an {@link ItemContainerListener} to this container.
     *
     * @param listener The listener to deposit to this container.
     * @return {@code true} if the listener was added, {@code false} otherwise.
     */
    public final boolean addListener(ItemContainerListener listener) {
        return listeners.add(listener);
    }

    /**
     * Removes an {@link ItemContainerListener} from this container.
     *
     * @param listener The listener to withdraw from this container.
     * @return {@code true} if the listener was removed, {@code false} otherwise.
     */
    public final boolean removeListener(ItemContainerListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Removes all itemlisteners
     */
    public final void removeAllListeners() {
        for (ItemContainerListener listener : listeners) {
            System.out.println("Removing Listener " + listener.getClass().getSimpleName());
        }

        listeners.clear();
    }

    /**
     * Fires the {@code ItemContainerListener.itemUpdated(ItemContainer, int)}
     * event.
     */
    public final void fireItemUpdatedEvent(Item oldItem, Item newItem, int index, boolean refresh) {
        if (firingEvents) {
            listeners.forEach(evt -> evt.itemUpdated(this, Optional.ofNullable(oldItem), Optional.ofNullable(newItem),
                index, refresh));
        }
    }

    /**
     * Fires the {@code ItemContainerListener.bulkItemsUpdated(ItemContainer)}
     * event.
     */
    public final void fireBulkItemsUpdatedEvent() {
        if (firingEvents) {
            listeners.forEach(evt -> evt.bulkItemsUpdated(this));
        }
    }

    /**
     * Fires the {@code ItemContainerListener.capacityExceeded(ItemContainer)}
     * event.
     */
    public final void fireCapacityExceededEvent() {
        if (firingEvents) {
            listeners.forEach(evt -> evt.capacityExceeded(this));
        }
    }

    /** @return The item array in this container. */
    public Item[] getItems() {
        return items;
    }

    /**
     * Gets an item by their slot index.
     * @param slot    Slot to check for item.
     * @return        Item in said slot.
     */
    public Item forSlot(int slot) {
        return items[slot];
    }

    /**
     * Checks if the slot contains an item.
     * @param slot    The container slot to check.
     * @return        items[slot] != null.
     */
    public boolean isSlotOccupied(int slot) {
        return items[slot] != null && items[slot].getId() > 0 && items[slot].getAmount() > 0;
    }

    /**
     * Removes a set of items from the inventory.
     *
     * @param optional
     * the set of items to delete.
     */
    public void removeItemSet(Optional<Item[]> optional) {
        if (optional.isPresent()) {
            for (Item deleteItem : optional.get()) {
                if (deleteItem == null) {
                    continue;
                }
                remove(deleteItem);
            }
        }
    }

    public boolean hasAt(int slot, int item) {
        Item at = items[slot];
        return at != null && at.getId() == item;
    }

    public boolean hasAt(int slot) {
        return slot >= 0 & slot < items.length && items[slot] != null;
    }

    private static final Tuple<Integer, Item> NULL_TUPLE = new Tuple<>(-1, null);

    public Tuple<Integer, Item> findFirst(int item) {
        for (int i = 0; i < size(); i++) {
            if (items[i] != null && items[i].getId() == item)
                return new Tuple<>(i, items[i]);
        }
        return NULL_TUPLE;
    }

    public Tuple<Integer, Item> findFirst(Set<Integer> matches) {
        for (int i = 0; i < size(); i++) {
            if (items[i] != null && matches.contains(items[i].getId()))
                return new Tuple<>(i, items[i]);
        }
        return NULL_TUPLE;
    }

    public boolean hasAllArr(Item[] items) {
        for (Item i : items)
            if (i != null && findFirst(i.getId()).first() == -1)
                return false;
        return true;
    }

    public boolean hasAllArr(int[] items) {
        for (int i : items)
            if (findFirst(i).first() == -1)
                return false;
        return true;
    }

    /**
     * Searches for the item id specified in the argument and returns the item object corresponding.
     * @param itemId
     * @return
     */
    public Item byId(int itemId) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            if (items[i].getId() == itemId) {
                return items[i];
            }
        }
        return null;
    }

    public ArrayList<Item> collectOneOfEach(int... ids) {
        ArrayList<Item> list = new ArrayList<>(ids.length);
        i: for(int id : ids) {
            for(Item item : items) {
                if(item != null && item.getId() == id) {
                    list.add(item);
                    continue i;
                }
            }
        }
        return list.size() != ids.length ? null : list;
    }

    /** Sets the value for {@link #firingEvents}. */
    public void setFiringEvents(boolean firingEvents) {
        this.firingEvents = firingEvents;
    }

    /** @return the amount of remaining free indices */
    public final int remaining() {
        return capacity - size();
    }

    public boolean hasFreeSlots(int slots) {
        return getFreeSlots() >= slots;
    }

    /** @return the amount of free slots available in the container */
    public int getFreeSlots() {
        return capacity() - size();
    }

    /** @return the amount of used indices */
    public final int size() {
        return (int) Arrays.stream(items).filter(Objects::nonNull).count();
    }

    /** @return the total amount of used and free indices */
    public final int capacity() {
        return capacity;
    }

    /** @return the policy this container follows */
    public final StackPolicy policy() {
        return policy;
    }

    /** @return this policy checks if the container will allow empty items */
    public boolean allowZero() {
        return false;
    }

    /**
     * Checks if the container is out of available slots.
     * @return    No free slot available.
     */
    public boolean isFull() {
        return getFreeSlots() == 0;
    }

    public boolean dirty;

    public void refresh() {
        dirty = true;
    }

    public void sync() {

    }

    public Item findItem(int id) {
        return findItem(id, false);
    }

    public Item findItem(int id, boolean acceptNoted) {
        for(Item item : items) {
            if(item != null && (item.getId() == id || (acceptNoted && World.getWorld().definitions().get(ItemDefinition.class, id).notelink != -1 && item.getId() == World.getWorld().definitions().get(ItemDefinition.class, id).notelink)))
                return item;
        }
        return null;
    }

    public Item findItem(int id, boolean acceptNoted, int attributeHash) {
        for(Item item : items) {
            if(item != null && (item.getId() == id || (acceptNoted && World.getWorld().definitions().get(ItemDefinition.class, id).notelink != -1 && item.getId() == World.getWorld().definitions().get(ItemDefinition.class, id).notelink)))
                return item;
        }
        return null;
    }

    /**
         * from oss
         */
        public record Result(int requested, int completed, int... effectedSlots) {

        public boolean success() {
                return completed == requested;
            }

            public boolean failed() {
                if (GameServer.properties().debugMode) {
                    System.out.println(requested + "vs " + completed + "");
                }
                return !success();
            }

            @Override
            public String toString() {
                return "Result{" +
                    "requested=" + requested +
                    ", completed=" + completed +
                    ", effectedSlots=" + Arrays.toString(effectedSlots) +
                    ", success()=" + success() +
                    '}';
            }
        }
}
