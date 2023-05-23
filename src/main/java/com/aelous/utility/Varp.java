package com.aelous.utility;

import com.aelous.cache.definitions.VarbitDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Bart on 8/16/2015.
 */
public class Varp {

    private static final int[] SHIFTS = new int[32];

    static {
        int offset = 2;
        for(int i_4_ = 0; i_4_ < 32; i_4_++) {
            SHIFTS[i_4_] = offset - 1;
            offset += offset;
        }
    }
    private final int id;
    @Getter
    private int defaultValue;
    private boolean saved;
    private boolean forceSend;

    public static Varp createVarp(int id) {
        return new Varp(id, false);
    }

    public static Varp createVarp(int id, boolean save) {
        return new Varp(id, save);
    }

    public static final ArrayList<Varp> SYNCED_VARPS = new ArrayList<>();

    private void defaultValue(int val) {
        this.defaultValue = val;
        store();
    }

    private static Varp createVarp(int id, int defaultValue) {
        return new Varp(id, defaultValue);
    }

    public void store() {
        // System.out.println("store "+id);
        var stored = SYNCED_VARPS.stream().filter(e -> e.id == id).findFirst().orElse(null);
        if (stored != null) {
            // keeping one in the array, update values
            stored.defaultValue = defaultValue;
            stored.saved = saved;
            stored.forceSend = forceSend;
        } else {
            SYNCED_VARPS.add(this);
        }
    }

    private Varp(int id, boolean saved) {
        this.id = id;
        this.defaultValue = 0;
        this.saved = saved;
        if (saved)
            store();
    }

    private Varp(int id, int defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
        if (defaultValue != 0)
            store();
    }

    public Varp forceSend() {
        this.forceSend = true;
        store();
        return this;
    }

    public static void load(Player player) {
        for (Varp v : SYNCED_VARPS) {
            if (!v.saved) {
                if (v.defaultValue != 0)
                    v.set(player, v.defaultValue);
                else if (v.forceSend)
                    sync(v.id(), player);
                continue;
            }
            sync(v.id, player);
        }
    }

    public void set(Player player, int newVal) { // this is varp setter
        boolean changed = get(player) != newVal;
        if (changed) {
            player.sessionVarps()[id] = newVal;
            sync(player);
        }
    }

    public int toggle(Player player) {
        int returnVal = get(player) == 1 ? 0 : 1;
        set(player, returnVal);
        return returnVal;
    }

    public boolean active(Player player) {
        return get(player) != 0;
    }

    public void add(Player player, int bitmask) {
        set(player, get(player) + bitmask);
    }

    public void sync(Player player) {
        sync(id, player);
    }

    public static void sync(int id, Player player) {

        int varpState = player.sessionVarps()[id];

        if (id > 611 && id < 615) { //Quick curses varps auto set to 1, set to 0 on login
            varpState = 0;
        }

        //System.out.println("sending config id "+id+" with state "+varpState);
        player.getPacketSender().sendConfig(id, varpState);
    }

    public int get(Player player) {
        return player.sessionVarps()[id];
    }

    public int bitValue(Player player, int bitpos) {
        return player.sessionVarps()[id] >> bitpos;
    }

    public int id() {
        return id;
    }

    public void reset(Player player) {
        set(player, defaultValue);
    }

    public static void varbit(Player player, int id, int value) {
        VarbitDefinition bit = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if(bit != null) {
            int varpId = bit.varp;
            int least = bit.startbit;
            int most = bit.endbit;
            int shift = SHIFTS[most - least];
            if(value < 0 || value > shift)
                value = 0;
            int varpValue = player.sessionVarps()[varpId];
            shift <<= least;
            player.sessionVarps()[varpId] = ((varpValue & (~shift)) | value << least & shift);
            sync(varpId, player);
            //System.out.println("syncing varbit "+id+" belongs to varp (aka config) "+varpId+" value "+value);
        } else {
            System.err.println("warn: no varbit def for id "+id);
            player.sessionVarps()[id] = value;
            sync(id, player);
        }
    }

    public static int varbit(Player player, int id) {
        VarbitDefinition bit = World.getWorld().definitions().get(VarbitDefinition.class, id);
        if(bit != null) {
            int varpId = bit.varp;
            int least = bit.startbit;
            int most = bit.endbit;
            int shift = SHIFTS[most - least];
            return player.sessionVarps()[varpId] >> least & shift;
        }
        return player.sessionVarps()[id];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Varp varp = (Varp) o;
        return id == varp.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
