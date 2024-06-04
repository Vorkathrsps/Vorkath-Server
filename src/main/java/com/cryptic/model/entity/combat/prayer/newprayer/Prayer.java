package com.cryptic.model.entity.combat.prayer.newprayer;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.utility.Utils;
import com.cryptic.utility.Varbit;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
public enum Prayer {
    THICK_SKIN(ComponentID.THICK_SKIN_COMPONENT, Varbit.THICK_SKIN, 1, -1, 3, 2690, 4107, 4113, 4128, 4129, 5464, 5465),
    BURST_OF_STRENGTH(ComponentID.BURST_OF_STRENGTH_COMPONENT, Varbit.BURST_OF_STRENGTH, 4, -1, 3, 2688, 4108, 4114, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 5464, 5465),
    CLARITY_OF_THOUGHT(ComponentID.CLARITY_OF_THOUGHT_COMPONENT, Varbit.CLARITY_OF_THOUGHT, 7, -1, 3, 2664, 4109, 4115, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 5464, 5465),
    ROCK_SKIN(ComponentID.ROCK_SKIN_COMPONENT, Varbit.ROCK_SKIN, 10, -1, 6, 2684, 4104, 4113, 4128, 4129, 5464, 5465),
    SUPERHUMAN_STRENGTH(ComponentID.SUPER_HUMAN_STRENGTH_COMPONENT, Varbit.SUPERHUMAN_STRENGTH, 13, -1, 6, 2689, 4105, 4114, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 5464, 5465),
    IMPROVED_REFLEXES(ComponentID.IMPROVED_REFLEX_COMPONENT, Varbit.IMPROVED_REFLEXIS, 16, -1, 6, 2662, 4106, 4115, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 5464, 5465),
    RAPID_RESTORE(ComponentID.RAPID_RESTORE_COMPONENT, Varbit.RAPID_RESTORE, 19, -1, 1, 2679),
    RAPID_HEAL(ComponentID.RAPID_HEAL_COMPONENT, Varbit.RAPID_HEAL, 22, -1, 2, 2678),
    PROTECT_ITEM(ComponentID.PROTECT_ITEM_COMPONENT, Varbit.PROTECT_ITEM, 25, -1, 2, 1982),
    STEEL_SKIN(ComponentID.STEEL_SKIN_COMPONENT, Varbit.STEEL_SKIN, 28, -1, 12, 2687, 4104, 4107, 4128, 4129, 5464, 5465),
    ULTIMATE_STRENGTH(ComponentID.ULTIMATE_STRENGTH_COMPONENT, Varbit.ULTIMATE_STRENGTH, 31, -1, 12, 2691, 4108, 4105, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 5464, 5465),
    INCREDIBLE_REFLEXES(ComponentID.INCREDIBLE_REFLEX_COMPONENT, Varbit.INCREDIBLE_REFLEXES, 34, -1, 12, 2667, 4109, 4106, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 5464, 5465),
    PROTECT_FROM_MAGIC(ComponentID.PROTECT_FROM_MAGIC_COMPONENT, Varbit.PROTECT_FROM_MAGIC, 37, 2, 12, 2675, 4117, 4118, 4119, 4120, 4121),
    PROTECT_FROM_MISSILES(ComponentID.PROTECT_FROM_MISSILES_COMPONENT, Varbit.PROTECT_FROM_MISSILES, 40, 1, 12, 2677, 4116, 4118, 4119, 4121, 4120),
    PROTECT_FROM_MELEE(ComponentID.PROTECT_FROM_MELEE_COMPONENT, Varbit.PROTECT_FROM_MELEE, 43, 0, 12, 2676, 4116, 4117, 4119, 4121, 4120),
    RETRIBUTION(ComponentID.RETRIBUTION_COMPONENT, Varbit.RETRIBUTION, 46, 3, 3, 2682, 4116, 4117, 4118, 4121, 4120),
    REDEMPTION(ComponentID.REDEMPTION_COMPONENT, Varbit.REDEMPTION, 49, 5, 6, 2680, 4119, 4121, 4116, 4117, 4118),
    SMITE(ComponentID.SMITE_COMPONENT, Varbit.SMITE, 52, 4, 18, 2686, 4116, 4117, 4118, 4119, 4120),
    SHARP_EYE(ComponentID.SHARP_EYE_COMPONENT, Varbit.SHARP_EYE, 8, -1, 3, 2685, 4125, 4127, 4123, 4124, 4126, 4105, 4106, 4108, 4109, 4114, 4115, 4128, 4129, 5464, 5465),
    MYSTIC_WILL(ComponentID.MYSTIC_WILL_COMPONENT, Varbit.MYSTIC_WILL, 9, -1, 3, 2670, 4125, 4127, 4122, 4124, 4126, 4105, 4106, 4108, 4109, 4114, 4115, 4128, 4129, 5464, 5465),
    HAWK_EYE(ComponentID.HAWK_EYE_COMPONENT, Varbit.HAWK_EYE, 26, -1, 6, 2666, 4125, 4127, 4122, 4123, 4126, 4105, 4106, 4108, 4109, 4114, 4115, 4128, 4129, 5464, 5465),
    MYSTIC_LORE(ComponentID.MYSTIC_LORE_COMPONENT, Varbit.MYSTIC_LORE, 27, -1, 6, 2668, 4124, 4127, 4122, 4123, 4126, 4105, 4106, 4108, 4109, 4114, 4115, 4128, 4129, 5464, 5465),
    EAGLE_EYE(ComponentID.EAGLE_EYE_COMPONENT, Varbit.EAGLE_EYE, 44, -1, 12, 2665, 4125, 4127, 4122, 4123, 4124, 4105, 4106, 4108, 4109, 4114, 4115, 4128, 4129, 5464, 5465),
    MYSTIC_MIGHT(ComponentID.MYSTIC_MIGHT_COMPONENT, Varbit.MYSTIC_MIGHT, 45, -1, 12, 2669, 4125, 4124, 4122, 4123, 4126, 4105, 4106, 4108, 4109, 4114, 4115, 4128, 4129, 5464, 5465),
    RIGOUR(ComponentID.RIGOUR_COMPONENT, Varbit.RIGOUR, 74, -1, 24, 2685, 4125, 4124, 4122, 4123, 4126, 4105, 4106, 4108, 4109, 4114, 4115, 4127, 4128, 4129, 4104, 4107, 4113, 5465),
    CHIVALRY(ComponentID.CHIVALRY_COMPONENT, Varbit.CHIVALRY, 60, -1, 24, 3826, 4129, 4105, 4106, 4108, 4109, 4114, 4115, 4122, 4123, 4124, 4125, 4126, 4127, 4104, 4107, 4113, 5464, 5465),
    PIETY(ComponentID.PIETY_COMPONENT, Varbit.PIETY, 70, -1, 24, 3825, 4128, 4105, 4106, 4108, 4109, 4114, 4115, 4122, 4123, 4124, 4125, 4126, 4127, 4104, 4107, 4113, 5464, 5465),
    AUGURY(ComponentID.AUGURY_COMPONENT, Varbit.AUGURY, 77, -1, 24, 2670, 4125, 4124, 4122, 4123, 4126, 4105, 4106, 4108, 4109, 4114, 4115, 4127, 4128, 4129, 4104, 4107, 4113, 5464),
    PRESERVE(ComponentID.PRESERVE_COMPONENT, Varbit.PRESERVE, 55, -1, 3, 2679);

    public static final Prayer[] VALUES = values();
    private static final Int2ObjectOpenHashMap<Prayer> PRAYERS = new Int2ObjectOpenHashMap<Prayer>(VALUES.length);
    public static final Long2ObjectMap<Prayer> MAPPED_COMPONENTS = new Long2ObjectOpenHashMap<>();

    static {
        for (Prayer p : VALUES) {
            PRAYERS.put(p.varbit, p);
            MAPPED_COMPONENTS.put(p.component, p);
            p.setQuickPrayerCollisions(p.getQPCollisions());
        }
    }

    private final long component;
    private final int varbit;
    private final int level;
    private final int headIcon;
    private final int soundEffect;
    private final float drainRate;
    private final int[] collisions;
    private final String name;
    @Setter
    int[] quickPrayerCollisions;

    Prayer(final long component, final int varbit, final int level, final int headIcon, final float drainRate, final int soundEffect, final int... collisions) {
        this.component = component;
        this.varbit = varbit;
        this.level = level;
        this.headIcon = headIcon;
        this.drainRate = drainRate;
        this.soundEffect = soundEffect;
        this.collisions = collisions;
        this.name = Utils.capitalize(name().toLowerCase().replace("_", " ")).replace("From", "from").replace("Of", "of");
    }

    public static Prayer getPrayer(final int varbitId) {
        return PRAYERS.get(varbitId);
    }

    public static Prayer get(int id) {
        return Prayer.VALUES[id];
    }

    public int[] getQPCollisions() {
        final int[] values = new int[getCollisions().length];
        for (Prayer prayers : Prayer.VALUES) {
            for (int i = 0; i < collisions.length; i++) {
                if (prayers.getVarbit() == collisions[i]) values[i] = prayers.ordinal();
            }
        }
        return values;
    }

    @Override
    public String toString() {
        return "Prayer{" +
            "component=" + component +
            '}';
    }
}
