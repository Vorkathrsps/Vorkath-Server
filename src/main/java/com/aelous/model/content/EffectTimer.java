package com.aelous.model.content;

import com.aelous.model.entity.player.Player;

public enum EffectTimer {

    VENGEANCE(157),
    FREEZE(158),
    ANTIFIRE(159),
    OVERLOAD(160),
    TELEBLOCK(161),
    FARMING(1039),
    STAMINA(1041),
    VENOM(1040),
    DFS(1038),
    CHARGE(1037),
    IMBUED_HEART(1098),
    DROP_LAMP(1106),
    MONSTER_RESPAWN(1434),
    DIVINE_BASTION_POTION(1437),
    DIVINE_BATTLE_MAGE_POTION(1438),
    DIVINE_MAGIC_POTION(1439),
    DIVINE_RANGING_POTION(1440),
    DIVINE_SUPER_COMBAT_POTION(1441),
    DIVINE_SUPER_DEFENCE_POTION(1442),
    DIVINE_SUPER_ATTACK_POTION(1443),
    DIVINE_SUPER_STRENGTH_POTION(1444),
    DOUBLE_EXP(1771),
    ;

    EffectTimer(int clientSprite) {
        this.clientSprite = clientSprite;
    }

    private final int clientSprite;

    public int getClientSprite() {
        return clientSprite;
    }

    public static void clearTimers(Player player) {
        player.getPacketSender().sendEffectTimer(0, EffectTimer.VENGEANCE).
            sendEffectTimer(0, EffectTimer.FREEZE).
            sendEffectTimer(0, EffectTimer.ANTIFIRE).
            sendEffectTimer(0, EffectTimer.OVERLOAD).
            sendEffectTimer(0, EffectTimer.TELEBLOCK).
            sendEffectTimer(0, EffectTimer.FARMING).
            sendEffectTimer(0, EffectTimer.STAMINA).
            sendEffectTimer(0, EffectTimer.VENOM).
            sendEffectTimer(0, EffectTimer.DFS).
            sendEffectTimer(0, EffectTimer.CHARGE).
            sendEffectTimer(0, EffectTimer.IMBUED_HEART).
            sendEffectTimer(0, DROP_LAMP).
            sendEffectTimer(0, MONSTER_RESPAWN).
            sendEffectTimer(0, DIVINE_BASTION_POTION).
            sendEffectTimer(0, DIVINE_BATTLE_MAGE_POTION).
            sendEffectTimer(0, DIVINE_MAGIC_POTION).
            sendEffectTimer(0, DIVINE_RANGING_POTION).
            sendEffectTimer(0, DIVINE_SUPER_COMBAT_POTION).
            sendEffectTimer(0, DIVINE_SUPER_DEFENCE_POTION).
            sendEffectTimer(0, DIVINE_SUPER_ATTACK_POTION).
            sendEffectTimer(0, DIVINE_SUPER_STRENGTH_POTION);
    }

}
