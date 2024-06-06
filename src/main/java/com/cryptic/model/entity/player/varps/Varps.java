package com.cryptic.model.entity.player.varps;

import com.cryptic.cache.definitions.VarbitDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import dev.openrune.cache.CacheManager;
import dev.openrune.cache.filestore.definition.data.VarBitType;
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

    public void setVarbit2(int id, int value) {
        VarBitType def = CacheManager.INSTANCE.getVarbit(id);
        setBit(def.getVarp(), def.getStartBit(), def.getEndBit(), value);
    }

    public void setVarbit(final int id, int value) {
        if (id == -1) return;
        final VarBitType defs = CacheManager.INSTANCE.getVarbit(id);
        int mask = BIT_SIZES[defs.getEndBit() - defs.getStartBit()];
        if (value < 0 || value > mask) value = 0;
        mask <<= defs.getStartBit();
        final int varpValue = (varps[defs.getVarp()] & (~mask) | value << defs.getStartBit() & mask);
        setState(defs.getVarp(), varpValue);
    }

    public void sendTempVarbit(int id, int value) {
        VarBitType def = CacheManager.INSTANCE.getVarbit(id);
        int packed = BIT_SIZES[def.getEndBit() - def.getStartBit()] << def.getStartBit();
        player.getPacketSender().sendConfig(def.getVarp(), (varps[def.getVarp()] & (~packed)) | value << def.getStartBit() & packed);
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
        VarBitType def = CacheManager.INSTANCE.getVarbit(id);
        return getState(varps[def.getVarp()], def);
    }

    public static int getState(int varp, VarBitType def) {
        return BitManipulation.getBit(varp, def.getStartBit(), def.getEndBit());
    }

    public void updateVarps() {
        requireUpdate.forEach( value -> {
            System.out.println("varp val="+varps[value]);
            player.getPacketSender().sendConfig(value, varps[value]);
        });
        requireUpdate.clear();
    }

}
