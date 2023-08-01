package com.cryptic.model.entity.player;

import com.cryptic.cache.definitions.VarbitDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;

/**
 * Created by bart on 7/18/15.
 */
public class Varps {

    public static final int[] BIT_SIZES = new int[32];

    static {
        for (int numbits = 0, size = 2; numbits < 32; numbits++) {
            BIT_SIZES[numbits] = size - 1;
            size += size;
        }
    }

    private final Player player;
    private final int[] varps = new int[4000];

    public Varps(Player player) {
        this.player = player;
    }

    public void varp(int id, int v) {
        varps[id] = v;
        sync(id);
    }

    public Player player() {
        return player;
    }

    /**
     * Returns the full varp integer value rather than a specific varbit whom's integer value contains multiple shifts representing different effects.
     * Some varbits take a full 32-bit integer so you can use it.
     *
     * @param id
     * @return
     */
    public int varp(int id) {
        return varps[id];
    }

    /**
     * Sets the value of a varp using the given varbit definition id. Varbit contains shift values.
     *
     * @param id varbit definition id
     * @param v  value
     */
    public void varbit(int id, int v) {
        VarbitDefinition def = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if (def != null) {
            int area = BIT_SIZES[def.endbit - def.startbit] << def.startbit;
            varps[def.varp] = (varps[def.varp] & (~area)) | v << def.startbit & area;


            sync(def.varp);
        } //what is the index in syncnonzero?
    }

    public static int updateValue(int old, int BITSIZE, int newval, int shift) {
        BITSIZE <<= shift;
        return (old & ~BITSIZE) | (BITSIZE & (newval << shift));
        // old (1) & ~15 -> changes 0001 to 0000
        // now OR the other bits and the new value: 8 (maximum) and 1 << 0
        // old | 1 => updated
    }

    public int varbit(int id) {
        VarbitDefinition def = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if (def != null) {
            return varbitValue(varps[def.varp], def);
        }

        return 0;
    }

    /**
     * Returns the boolean value of current varpbit
     *
     * @param id
     * @return false if varp value is 0, true for anything else
     */
    public boolean varbitBool(int id) {
        VarbitDefinition def = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if (def != null) {
            return varbitValue(varps[def.varp], def) != 0;
        }

        return false;
    }

    public static int varbitValue(int varp, VarbitDefinition def) {
        return (varp >> def.startbit) & BIT_SIZES[def.endbit - def.startbit];
    }

    public void sync(int id) {
        player.getPacketSender().sendConfig(id, varps[id]);
    }

    public int[] raw() {
        return varps;
    }

    public void presave() {
        // Turn off prayers
        Prayers.closeAllPrayers(player);
    }

    public void syncNonzero() {
        for (int i = 0; i < 4000; i++) {
            if (varps[i] != 0)
                sync(i);
        }
    }
}
