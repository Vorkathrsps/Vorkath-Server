package com.cryptic.model.entity.events.star;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public class CrashedStar extends GameObject {
    @Getter @Setter private int dustCount = 0;
    private static final List<Integer> miningLevels = Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90);
    private static final List<Integer> xpRates = Arrays.asList(3360, 6480, 4920, 5920, 8400, 7750, 11180, 15400, 16400);
    private static final List<Integer> additionalDustChance = Arrays.asList(2, 6, 12, 20, 30, 42, 56, 72, 90);
    @Getter @Setter private boolean stopActions = false;
    @Getter @Setter private StarStage starStage;

    public CrashedStar(int id, Tile tile) {
        super(id, tile);
        starStage = StarStage.ONE;
    }

    public void depleteAndSetNextStage(int nextObject) {
        dustCount = 0;
        MapObjects.get(this.getId(), this.tile()).ifPresent(star -> {
            setStopActions(true);
            Chain.noCtx().delay(1, () -> {
                setStopActions(false);
                star.setId(nextObject);
            });
        });
    }

    private int getPlayerSkillIndex(int skillLevel) {
        for (int index = 0; index < miningLevels.size(); index++) {
            int currentLevel = miningLevels.get(index);
            int nextLevel = (index < miningLevels.size() - 1) ? miningLevels.get(index + 1) : Integer.MAX_VALUE;
            if (skillLevel >= currentLevel && skillLevel < nextLevel) {
                return index;
            }
        }
        return -1;
    }

    private double interpolateChance(int level, double lowerChance, double upperChance) {
        double levelFactor = (double) (level - 1) / 98.0;
        return lowerChance + (upperChance - lowerChance) * levelFactor;
    }

    public void calculateSuccess(@NonNull final Player player) {
        int skillLevel = player.skills().level(Skills.MINING);
        int playerSkillIndex = getPlayerSkillIndex(skillLevel);

        if (playerSkillIndex != -1) {
            double lowerChance;
            double upperChance;

            switch (playerSkillIndex) {
                case 1 -> {
                    lowerChance = 0.3;
                    upperChance = 1.0;
                }
                case 2 -> {
                    lowerChance = 0.3;
                    upperChance = 0.57;
                }
                case 3 -> {
                    lowerChance = 0.3;
                    upperChance = 0.49;
                }
                case 4 -> {
                    lowerChance = 0.3;
                    upperChance = 0.44;
                }
                case 5 -> {
                    lowerChance = 0.23;
                    upperChance = 0.3;
                }
                case 6 -> {
                    lowerChance = 0.15;
                    upperChance = 0.2;
                }
                case 7 -> {
                    lowerChance = 0.09;
                    upperChance = 0.13;
                }
                case 8 -> {
                    lowerChance = 0.07;
                    upperChance = 0.1;
                }
                case 9 -> {
                    lowerChance = 0.06;
                    upperChance = 0.07;
                }
                default -> {
                    lowerChance = 0.0;
                    upperChance = 0.0;
                }
            }

            int interpolatedChance = (int) (interpolateChance(skillLevel, lowerChance, upperChance) * 100);

            if (Utils.rollDie(interpolatedChance, 1)) {
                addRewardsToInventory(player);
            }
        }
    }

    public void addRewardsToInventory(@NonNull final Player player) {
        int skillLevel = player.skills().level(Skills.MINING);
        int playerSkillIndex = getPlayerSkillIndex(skillLevel);

        if (playerSkillIndex != -1) {
            int assignedXpRate = xpRates.get(playerSkillIndex);
            double additionalStarDustChance = additionalDustChance.get(playerSkillIndex);

            int dustToAdd = Utils.rollDie((int) additionalStarDustChance) ? 2 : 1;

            if (Utils.rollDie(127, 1)) {
                Item crystalShard = new Item(ItemIdentifiers.CRYSTAL_SHARD, 5);
                String message = "";

                if (!player.getInventory().isFull()) {
                    player.getInventory().add(crystalShard);
                    message = "The crashed star rewards you with crystal shards.";
                } else {
                    player.getInventory().addOrDrop(crystalShard);
                    message = "The crashed star rewards you with crystal shards, they've been placed on the floor.";
                }

                player.message(Color.PURPLE.wrap(message));
            }

            dustCount += dustToAdd;

            if (dustToAdd > 1) {
                player.message(Color.BLUE.wrap("The crashed star rewards you with extra stardust."));
            }

            player.getInventory().addOrDrop(new Item(ItemIdentifiers.STARDUST, dustToAdd));
            player.getInventory().addOrDrop(new Item(ItemIdentifiers.BLOOD_MONEY, 150));
            player.skills().addXp(Skills.MINING, assignedXpRate);
        }
    }
}

