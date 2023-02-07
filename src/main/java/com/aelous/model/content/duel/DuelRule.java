package com.aelous.model.content.duel;

import com.aelous.model.entity.player.EquipSlot;
import com.aelous.utility.Utils;

/**
 * @author Patrick van Elderen | January, 27, 2021, 21:39
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum DuelRule {

    NO_RANGED(16, 6725, -1, -1),
    NO_MELEE(32, 6726, -1, -1),
    NO_MAGIC(64, 6727, -1, -1),
    NO_SPECIAL_ATTACKS(8192, 7816, -1, -1),
    LOCK_WEAPON(4096, 670, -1, -1),
    NO_FORFEIT(1, 6721, -1, -1),
    NO_POTIONS(128, 6728, -1, -1),
    NO_FOOD(256, 6729, -1, -1),
    NO_PRAYER(512, 6730, -1, -1),
    NO_MOVEMENT(2, 6722, -1, -1),
    OBSTACLES(1024, 6732, -1, -1),

    NO_HELM(16384, 13813, 1, EquipSlot.HEAD),
    NO_CAPE(32768, 13814, 1, EquipSlot.CAPE),
    NO_AMULET(65536, 13815, 1, EquipSlot.AMULET),
    NO_AMMUNITION(134217728, 13816, 1, EquipSlot.AMMO),
    NO_WEAPON(131072, 13817, 1, EquipSlot.WEAPON),
    NO_BODY(262144, 13818, 1, EquipSlot.BODY),
    NO_SHIELD(524288, 13819, 1, EquipSlot.SHIELD),
    NO_LEGS(2097152, 13820, 1, EquipSlot.LEGS),
    NO_RING(67108864, 13821, 1, EquipSlot.RING),
    NO_BOOTS(16777216, 13822, 1, EquipSlot.FEET),
    NO_GLOVES(8388608, 13823, 1, EquipSlot.HANDS);

    DuelRule(int configId, int buttonId, int inventorySpaceReq, int equipmentSlot) {
        this.configId = configId;
        this.buttonId = buttonId;
        this.inventorySpaceReq = inventorySpaceReq;
        this.equipmentSlot = equipmentSlot;
    }

    private final int configId;
    private final int buttonId;
    private final int inventorySpaceReq;
    private final int equipmentSlot;

    public int getConfigId() {
        return configId;
    }

    public int getButtonId() {
        return this.buttonId;
    }

    public int getInventorySpaceReq() {
        return this.inventorySpaceReq;
    }

    public int getEquipmentSlot() {
        return this.equipmentSlot;
    }

    public static DuelRule forId(int i) {
        for (DuelRule r : DuelRule.values()) {
            if (r.ordinal() == i)
                return r;
        }
        return null;
    }

    static DuelRule forButtonId(int buttonId) {
        for (DuelRule r : DuelRule.values()) {
            if (r.getButtonId() == buttonId)
                return r;
        }
        return null;
    }

    @Override
    public String toString() {
        return Utils.formatText(this.name().toLowerCase());
    }
}
