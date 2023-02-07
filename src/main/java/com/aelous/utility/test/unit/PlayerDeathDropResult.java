package com.aelous.utility.test.unit;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jak |Shadowrs
 */
public class PlayerDeathDropResult {
    private static final Logger logger = LogManager.getLogger(PlayerDeathDropResult.class);

    public static boolean DEBUG = false;

    public final Player killer;
    public final List<Item> outDrop;
    public final List<Item> outKeep;
    public final List<Item> outDel;
    /**
     * these are INFORMATIONAL DEBUGGING ONLY - already included in {@code outKeep} and {@code outDrop} so don't re-add them
     */
    public final List<PlayerDeathConvertResult> outCon;

    public PlayerDeathDropResult(Player killer, List<Item> outDrop, List<Item> outKeep, List<Item> outDel, List<PlayerDeathConvertResult> outCon) {
        this.killer = killer;
        this.outDrop = outDrop;
        this.outKeep = outKeep;
        this.outDel = outDel;
        this.outCon = outCon;
        if (DEBUG) {
            System.out.println(toString());
            logger.trace(toString());
        }
    }

    @Override
    public String toString() {
        return "pd_DR{" +
            "killer=" + (killer==null?"?":killer.getUsername()) +
            ", \noutDrop=" + Arrays.toString(outDrop.stream().filter(Objects::nonNull).map(Item::toShortString).toArray()) +
            ", \noutKeep=" + Arrays.toString(outKeep.stream().filter(Objects::nonNull).map(Item::toShortString).toArray()) +
            ", \noutDel=" + Arrays.toString(outDel.stream().filter(Objects::nonNull).map(Item::toShortString).toArray()) +
            ", \noutCon=" + Arrays.toString(outCon.stream().filter(Objects::nonNull).toArray()) +
            '}';
    }

    public ItemContainer maindrop() {
        ItemContainer ic = new ItemContainer(100, ItemContainer.StackPolicy.ALWAYS, new Item[100]);
        outDrop.stream().filter(Objects::nonNull).forEach(ic::add);
        return ic;
    }

    public ItemContainer allDrops() {
        ItemContainer ic = new ItemContainer(100, ItemContainer.StackPolicy.ALWAYS, new Item[100]);
        outDrop.stream().filter(Objects::nonNull).forEach(ic::add);
        if (outCon != null) {
            for (PlayerDeathConvertResult oc : outCon) {
                if (oc == null) continue;
                oc.toDrop.stream().filter(Objects::nonNull).forEach(ic::add);
            }
        }
        return ic;
    }
}
