package com.aelous.utility;

import com.aelous.model.entity.Entity;

import com.aelous.model.entity.combat.CombatFactory;

import javax.annotation.Nullable;

/**
 * These are used for both forcechat and println debugs.
 * <br> toggle via ::dprints tttttt where t=true and f=false, in order of the enum:
 *
 */
public enum Debugs {
    NPC_MOVE(false), //1
    CLIP(false),
    CB_FOLO(false), // 3
    MOB_STEPS(false),
    NPC_RETREAT(false), // 5
    PLAYER_STEPS_ONLY(false),
    WALK(false),//7
    CMB(false),
    PLAYER_DEBUGS_ONLY(false)//9
    ;
    public boolean enabled;

    Debugs() {
    }
    Debugs(boolean enabled) {
        this.enabled = enabled;
    }
    public void toggle() {
        enabled = !enabled;
    }

    public void debug(@Nullable Entity attacker, String s, @Nullable Entity victim, boolean debugMessage) {
        if (attacker != null && !attacker.isPlayer() && PLAYER_DEBUGS_ONLY.enabled)
            return;
        if (this == MOB_STEPS && attacker != null && attacker.isPlayer()) {
            PLAYER_STEPS_ONLY.debug(attacker, s, victim, debugMessage);
            return;
        }
        if (!enabled)
            return;
        CombatFactory.debug(attacker, s, victim, debugMessage);
    }
    public void debug(@Nullable Entity attacker, String s, @Nullable Entity victim) {
        if (this == MOB_STEPS && attacker != null && attacker.isPlayer()) {
            PLAYER_STEPS_ONLY.debug(attacker, s, victim);
            return;
        }
        if (!enabled)
            return;
        debug(attacker, s, victim, true);
    }
    public void debug(Entity attacker, String s) {
        if (this == MOB_STEPS && attacker != null && attacker.isPlayer()) {
            PLAYER_STEPS_ONLY.debug(attacker, s);
            return;
        }
        if (!enabled)
            return;
        debug(attacker, s, attacker==null?null:attacker.getCombat().getTarget(), true);
    }
}
