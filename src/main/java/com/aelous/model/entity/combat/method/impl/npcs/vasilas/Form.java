package com.aelous.model.entity.combat.method.impl.npcs.vasilas;

import com.aelous.model.entity.combat.CombatType;
import com.aelous.utility.Utils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.EnumSet;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Sharky
 * @Since February 12, 2023
 */
public enum Form {

    MELEE(new int[]{NYLOCAS_ISCHYROS_8342, NYLOCAS_ISCHYROS_8345, NYLOCAS_VASILIAS_8355}, 7988, new int[]{8004}, 8003, 8005, CombatType.MELEE, 2),
    RANGED(new int[]{NYLOCAS_TOXOBOLOS_8343, NYLOCAS_TOXOBOLOS_8346, NYLOCAS_VASILIAS_8357}, 7988, new int[]{7999, 8001}, 7997, 7998, CombatType.RANGED, 3),
    MAGIC(new int[]{NYLOCAS_HAGIOS, NYLOCAS_HAGIOS_8347, NYLOCAS_VASILIAS_8356}, 7993, new int[]{7989, 7990}, 7987, 7991, CombatType.MAGIC, 3);

    /**
     * Caches our enum values.
     */
    public static final ImmutableSet<Form> VALUES = Sets.immutableEnumSet(EnumSet.allOf(Form.class));

    private static final int smallNpcIndex = 0;
    private static final int bigNpcIndex = 1;
    private static final int bossNpcIndex = 2;

    private final int[] npcIds;
    private final int stanceAnim;
    private final int[] attackAnims;
    private final int walkAnim;
    private final int deathAnim;
    private final CombatType combatType;
    private final int attackDistance;

    Form(int[] npcIds, int stance, int[] attack, int walk, int death, CombatType combatType, int attackDistance) {
        this.npcIds = npcIds;
        this.stanceAnim = stance;
        this.attackAnims = attack;
        this.walkAnim = walk;
        this.deathAnim = death;
        this.combatType = combatType;
        this.attackDistance = attackDistance;
    }

    public int getNpcIdBySize(NylocasSize size) {
        return switch (size) {
            case SMALL -> npcIds[smallNpcIndex];
            case BIG -> npcIds[bigNpcIndex];
            case BOSS -> npcIds[bossNpcIndex];
        };
    }

    public int getStanceAnim() {
        return stanceAnim;
    }

    public int[] getAttackAnims() {
        return attackAnims;
    }

    public int getWalkAnim() {
        return walkAnim;
    }

    public int getDeathAnim() {
        return deathAnim;
    }

    public CombatType getCombatType() {
        return combatType;
    }

    public int getAttackDistance() {
        return this.attackDistance;
    }

    public static Form random() {
        return values()[Utils.random(values().length - 1)];
    }

    public static Form fromName(String name) {
        if (name.equalsIgnoreCase("melee")) {
            return Form.MELEE;
        } else if (name.equalsIgnoreCase("mage")) {
            return Form.MAGIC;
        } else if (name.equalsIgnoreCase("range")) {
            return Form.RANGED;
        }

        return Form.MELEE;
    }
}
