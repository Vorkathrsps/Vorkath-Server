package com.aelous.utility.test.unit;

import com.aelous.model.items.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jak |Shadowrs
 */
public class PlayerDeathConvertResult {
    public final List<Item> toDrop;
    public final List<Item> keep;

    public PlayerDeathConvertResult(List<Item> toDrop, List<Item> keep) {
        this.toDrop = toDrop;
        this.keep = keep;
    }

    @Override
    public String toString() {
        return "pd_CR{" +
            "\ntoDrop=" + (toDrop==null ? "-" : Arrays.toString(toDrop.stream().filter(Objects::nonNull).map(Item::toShortString).toArray())) +
            ", \nkeep=" + (keep==null ? "-" : Arrays.toString(keep.stream().filter(Objects::nonNull).map(Item::toShortString).toArray())) +
            '}';
    }
}
