package com.cryptic.model.entity.combat.prayer;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;
import com.cryptic.utility.Varbit;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: Origin
 * @Date: 6/4/24
 */
public class PrayerManager {

    public final Player player;
    private final Object2IntOpenHashMap<Prayer> activePrayers = new Object2IntOpenHashMap<>();
    private int drain;
    @Setter
    public int quickPrayerSettings = -1;

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
            final var active = Prayer.getPrayer(varbit);
            if (active != null) {
                this.activePrayers.removeInt(active);
                this.player.varps().setVarbit(varbit, 0);
            }
        }
    }

    public void activate(final Prayer prayer) {
        this.activePrayers.put(prayer, World.getWorld().cycleCount());
        this.toggle(prayer);
        this.checkCollisions(prayer);
    }

    public void deactivate(final Prayer prayer) {
        final boolean activated = this.player.varps().getVarbit(prayer.getVarbit()) == 1;
        if (activated) {
            this.clearHeadIcons(prayer);
            this.player.varps().toggleVarbit(prayer.getVarbit());
            this.player.sendPrivateSound(2663);
            this.activePrayers.removeInt(prayer);
        }
    }

    public void toggle(final Prayer prayer) {
        final boolean activating = this.player.varps().getVarbit(prayer.getVarbit()) == 0;

        if (this.getSkillLevel() <= 0) return;

        if (player.getPrayer().getSkillLevel() < prayer.getLevel() && activating) {
            player.getPacketSender().runClientScriptNew(5224, 112);
            player.message("You do not meet the requirements to activate this Prayer.");
            return;
        }

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
        this.player.sendPrivateSound(2672);
        this.player.varps().setVarbit(Varbit.REGULAR_PRAYERS, 0);
    }

    public void reset() {
        for (var prayer : this.activePrayers.keySet()) {
            this.clearHeadIcons(prayer);
            this.player.varps().toggleVarbit(prayer.getVarbit());
        }
        this.activePrayers.clear();
        this.player.varps().setVarbit(Varbit.REGULAR_PRAYERS, -1);
        this.player.varps().setVarbit(Varbit.QUICK_PRAYERS_ON, -1);
    }

    private void clearHeadIcons(final Prayer prayer) {
        if (this.activePrayers.containsKey(prayer) && prayer.getHeadIcon() != -1) {
            this.player.setHeadHint(-1);
        }
    }

    public final void decrementPoints(final int amount) {
        if (this.getSkillLevel() > 0) this.player.skills().alterSkill(Skills.PRAYER, -amount);
    }

    public final int getSkillLevel() {
        return player.getSkills().level(Skills.PRAYER);
    }

    public final boolean isPrayerActive(Prayer prayer) {
        return this.activePrayers.containsKey(prayer);
    }

    public @Nullable Prayer getPrayer(long button) {
        return Prayer.MAPPED_COMPONENTS.getOrDefault(button, null);
    }

    public static int getFilterConfiguration(final int slot) {
        return switch (slot) {
            case 0 -> Varbit.LOWER_TIERS_OF_TIERED_PRAYERS;
            case 1 -> Varbit.SHOW_TIERED_PRAYERS_EVEN_IF_MULTI;
            case 2 -> Varbit.SHOW_RAPID_HEALING_PRAYERS;
            case 3 -> Varbit.SHOW_PRAYERS_YOU_LACK_THE_LEVEL;
            case 4 -> Varbit.SHOW_PRAYERS_YOU_LACK_THE_REQUIREMENTS;
            default -> 0;
        };
    }
}
