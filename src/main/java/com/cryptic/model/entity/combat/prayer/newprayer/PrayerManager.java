package com.cryptic.model.entity.combat.prayer.newprayer;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;
import com.cryptic.utility.Varbit;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: Origin
 * @Date: 6/4/24
 */
public class PrayerManager {

    public final Player player;
    private final Object2IntOpenHashMap<Prayer> activePrayers = new Object2IntOpenHashMap<>();
    private int drain;
    public int quickPrayerSettings;
    private static final int SHOW_LOWER_TIERS = 6574;
    private static final int SHOW_MULTI = 6575;
    private static final int RAPID_HEALING = 6576;
    private static final int LACK_LEVEL = 6577;
    private static final int LACK_REQ = 6578;

    public PrayerManager(Player player) {
        this.player = player;
    }

    public void process() {
        for (var entry : this.activePrayers.object2IntEntrySet()) {
            var value = entry.getIntValue();
            var key = entry.getKey();
            if (value < World.getWorld().cycleCount()) {
                this.drain += (int) key.getDrainRate();
            }
        }
        final int resistance = 60 + (player.getBonuses().pray * 2);
        while (this.drain > resistance) {
            this.decrementPoints(1);
            this.drain -= resistance;
            if (this.getSkillLevel() <= 0) {
                this.clear();
            }
        }
    }

    public void setQuickPrayer(final int slot) {
        Prayer[] values = Prayer.VALUES;
        final Prayer prayer = values[slot];
        final int selected = prayer.ordinal();
        quickPrayerSettings = Utils.getShiftedValue(quickPrayerSettings, selected);
        for (int collision : prayer.getQPCollisions()) {
            quickPrayerSettings = Utils.getShiftedValue(quickPrayerSettings, collision, false);
        }
        player.varps().setVarbit(Varbit.QUICK_PRAYER, quickPrayerSettings);
    }

    public void checkCollisions(final Prayer prayer) {
        for (final int varbit : prayer.getCollisions()) {
            this.activePrayers.removeInt(Prayer.getPrayer(varbit));
            this.player.varps().setVarbit(varbit, 0);
        }
    }

    public void activate(final Prayer prayer) {
        this.activePrayers.put(prayer, World.getWorld().cycleCount());
        this.toggle(prayer);
        this.checkCollisions(prayer);
    }

    public void toggle(final Prayer prayer) {
        final boolean activating = this.player.varps().getVarbit(prayer.getVarbit()) == 0;
        if (activating) {
            if (prayer.getHeadIcon() != -1) this.player.setHeadHint(prayer.getHeadIcon());
            this.player.varps().toggleVarbit(prayer.getVarbit());
            this.player.sendPrivateSound(prayer.getSoundEffect());
            return;
        }

        this.clearHeadIcons(prayer);
        this.player.varps().toggleVarbit(prayer.getVarbit());
        this.player.sendPrivateSound(2663);
        this.activePrayers.removeInt(prayer);
    }

    public void clear() {
        for (var prayer : this.activePrayers.keySet()) {
            this.clearHeadIcons(prayer);
            this.player.varps().toggleVarbit(prayer.getVarbit());
        }
        this.activePrayers.clear();
    }

    private void clearHeadIcons(final Prayer prayer) {
        if (this.activePrayers.containsKey(prayer) && prayer.getHeadIcon() != -1) {
            this.player.setHeadHint(-1);
        }
    }

    public final void decrementPoints(final int amount) {
        this.player.skills().alterSkill(Skills.PRAYER, -amount);
    }

    public final int getSkillLevel() {
        return player.getSkills().level(Skills.PRAYER);
    }

    public final boolean isPrayerActive(Prayer prayer) {
        return this.activePrayers.containsKey(prayer);
    }

    public @Nullable Prayer getPrayer(long button) {
        return Prayer.MAPPED_COMPONENTS.get(button);
    }

    public static int getFilterConfiguration(final int slot) {
        return switch (slot) {
            case 0 -> SHOW_LOWER_TIERS;
            case 1 -> SHOW_MULTI;
            case 2 -> RAPID_HEALING;
            case 3 -> LACK_LEVEL;
            case 4 -> LACK_REQ;
            default -> 0;
        };
    }
}
