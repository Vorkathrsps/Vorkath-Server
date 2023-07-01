package com.aelous.model.content.skill.impl.mining;

import java.util.Random;

public class SkillingSuccess {
    private static final Random random = new Random();
    public static boolean success(int miningLevel, int requiredLevel, Ore type, Pickaxe pickaxe) {
        if (miningLevel < requiredLevel) {
            return false;
        }

        if (pickaxe.level > miningLevel) {
            return false;
        }

        double successChance = calculateSuccessChance(miningLevel, type);
        double randomValue = random.nextDouble();

        return successChance > randomValue;
    }

    public static double calculateSuccessChance(int level, Ore type) {
        double low = type.getLow();
        double high = type.getHigh();
        return (1D + (Math.floor((low * (99D - level)) / 98D) + Math.floor((high * (level - 1D)) / 98D))) / 256D;
    }

}
