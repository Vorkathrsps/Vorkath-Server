package com.cryptic.model.entity.player.varps;

import com.cryptic.cache.definitions.VarbitDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import dev.openrune.cache.CacheManager;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class Varps {


    public static final int[] BIT_SIZES = new int[32];

    static {
        for (int numbits = 0, size = 2; numbits < 32; numbits++) {
            BIT_SIZES[numbits] = size - 1;
            size += size;
        }
    }

    private final Player player;
    public final int[] varps;
    private final IntOpenHashSet requireUpdate;

    public Varps(Player player) {
        this.player = player;
        int size = CacheManager.INSTANCE.varbitSize();

        this.varps = new int[size];
        requireUpdate = new IntOpenHashSet(size);
    }

    public void setVarp(int id, int state) {
        setState(id, state);
    }

    public void setVarbitDirect(int id, int state) {
        setState(id, state);
    }

    public void setBit(int id, int startBit, int endBit, int value) {
        int packed = BIT_SIZES[endBit - startBit] << startBit;
        setState(id, (varps[id] & (~packed)) | value << startBit & packed);
    }

    public int getState(int id) {
        return varps[id];
    }

    public void setState(int id, int state) {
        varps[id] = state;
        requireUpdate.add(id);
    }

    public void toggleVarp(int id) {
        setVarp(id, getVarp(id) ^ 1);
    }

    public int getVarp(int id) {
        return varps[id];
    }

    public void setVarbit(int id, int value) {
        VarbitDefinition def = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if (def != null) {
            setBit(def.varp, def.startbit, def.endbit, value);
        }
    }

    public void sendTempVarbit(int id, int value) {
        VarbitDefinition def = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if (def != null) {
            int packed = BIT_SIZES[def.endbit - def.startbit] << def.startbit;
            player.getPacketSender().sendConfig(def.varp, (varps[def.varp] & (~packed)) | value << def.startbit & packed);
        }
    }

    public void toggleVarbit(int id) {
        setVarbit(id, getVarbit(id) ^ 1);
    }

    public void incrementVarbit(int id, int amount) {
        int inc = getVarbit(id)+amount;
        setVarbit(id, inc);
    }

    public void decrementVarbit(int id, int amount) {
        int dec = getVarbit(id)-amount;
        setVarbit(id, dec);
    }


    public int getVarbit(int id) {
        VarbitDefinition def = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if (def != null) {
            return getState(varps[def.varp], def);
        }
        return 0;
    }

    public static int getState(int varp, VarbitDefinition def) {
        return BitManipulation.getBit(varp, def.startbit, def.endbit);
    }

    public void updateVarps() {
        requireUpdate.forEach( value -> player.getPacketSender().sendConfig(value, varps[value]));
        requireUpdate.clear();
    }

}
