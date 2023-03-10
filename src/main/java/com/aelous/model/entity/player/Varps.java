package com.aelous.model.entity.player;

import com.aelous.cache.definitions.VarbitDefinition;
import com.aelous.model.content.skill.impl.farming.patch.Farmbit;
import com.aelous.model.World;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.utility.Varp;

/**
 * Created by bart on 7/18/15.
 * ok it sgot all the code needed straight from os-scape
 * just doesnt have saving
 * but the system is therre will work
 */
public class Varps {

    public static final int[] BIT_SIZES = new int[32];

    static {
        for (int numbits = 0, size = 2; numbits < 32; numbits++) {
            BIT_SIZES[numbits] = size - 1;
            size += size;
        }
    }

    private Player player;
    private int[] varps = new int[4000]; // osrs is 4k 317 was 2k

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
        varp(Varp.SPECIAL_ENABLED, 0);
    }

    public void syncNonzero() {
        for (int i = 0; i < 4000; i++) { // what you mean 2000? magic number, theres 2k varps, or was now theres more like 4k. and its varps count not varbit (5.9k)
            if (varps[i] != 0)
                sync(i);
        }
    }

    public static int varbitGet(int varp, VarbitDefinition def) { // Temp to avoid merge conflicts
        return (varp >> def.startbit) & BIT_SIZES[def.endbit - def.startbit];
    }

    public static int varbitSet(int varp, VarbitDefinition def, int v) { // Temp to avoid merge conflicts
        int area = BIT_SIZES[def.endbit - def.startbit] << def.startbit;
        return (varp & ~area) | (area & (v << def.startbit));
    }

    public boolean varpBool(int varp) {
        return varp(varp) == 1;
    }

    //TODO need Jak
    public int farmbit(Farmbit farmbit) {
        var value = player().<Integer>getAttribOr(farmbit.attrib, 0);
        return 0;/*value.shr(farmbit.bitStart).and(0xFF);*/
    }

     public void farmbit(Farmbit farmbit, int v) {
        /*var value = player().<Integer>getAttribOr(farmbit.attrib, 0);
         var newval = value.and(0xFF.shl(farmbit.bitStart).inv()).or(v.shl(farmbit.bitStart));
        player().putAttrib(farmbit.attrib, newval);

        // Synch that one. Only updates if in the correct region.
        Farming.synch(player(), farmbit,false);*/
    }
}
