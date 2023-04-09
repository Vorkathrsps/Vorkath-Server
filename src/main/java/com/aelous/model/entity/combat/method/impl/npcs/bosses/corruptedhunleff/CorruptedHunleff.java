package com.aelous.model.entity.combat.method.impl.npcs.bosses.corruptedhunleff;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;

import static com.aelous.model.entity.combat.method.impl.npcs.bosses.corruptedhunleff.CorruptedHunleff.Phase.*;

public class CorruptedHunleff extends NPC {

    public CorruptedHunleff.CombatAI getCombatAI() {
        return combatAI;
    }

    private final CorruptedHunleff.CombatAI combatAI;
    private CorruptedHunleff.Phase phase;

    public CorruptedHunleff(int id, Tile tile) {
        super(id, tile);
        this.phase = CorruptedHunleff.Phase.forId(id);
        this.combatAI = new CorruptedHunleff.CombatAI(this);
        this.setCombatMethod(combatAI);
    }

    enum Phase {
        MELEE(9035, CombatType.MELEE, new CorruptedHunleffCombatStrategy()),
        RANGED(9036, CombatType.RANGED, new CorruptedHunleffCombatStrategy()),
        MAGIC(9037, CombatType.MAGIC, new CorruptedHunleffCombatStrategy());

        public final int npcId;
        public final CombatMethod method;
        public final CombatType type;

        Phase(int npcId, CombatType type, CombatMethod method) {
            this.npcId = npcId;
            this.type = type;
            this.method = method;
        }

        public static CorruptedHunleff.Phase forId(int id) {
            for (CorruptedHunleff.Phase phase : values()) {
                if (phase.npcId == id)
                    return phase;
            }
            return null;
        }
    }

    public static class CombatAI extends CommonCombatMethod {

        private final CorruptedHunleff corruptedHunleff;
        private CombatMethod currentMethod;
        private int attacksCounter;

        public CombatAI(CorruptedHunleff corruptedHunleff) {
            this.corruptedHunleff = corruptedHunleff;
            currentMethod = corruptedHunleff.phase.method;
        }

        void updatePhase(CorruptedHunleff.Phase phase) {
            corruptedHunleff.phase = phase;
            currentMethod = phase.method;
            corruptedHunleff.transmog(phase.npcId);
            corruptedHunleff.getCombat().delayAttack(1);
            target.putAttrib(AttributeKey.HUNLESS_PREVIOUS_STYLE, phase);
        }

        public void attacksDone() {
            attacksCounter += 1;
            //System.out.println("attks "+attacksCounter);
            if (attacksCounter >= 4) {
                CorruptedHunleff.Phase old = target.getAttribOr(AttributeKey.HUNLESS_PREVIOUS_STYLE, MELEE);
                //System.out.println("previous phase: "+old);
                if(old == MELEE) {
                    updatePhase(Phase.RANGED);
                } else if(old == RANGED) {
                    updatePhase(MAGIC);
                } else if(old == MAGIC) {
                    updatePhase(MELEE);
                }
                attacksCounter = 0;
            }
        }

        @Override
        public boolean prepareAttack(Entity entity, Entity target) {
            attacksDone();
            return currentMethod.prepareAttack(entity, target);
        }

        @Override
        public int getAttackSpeed(Entity entity) {
            return currentMethod.getAttackSpeed(entity);
        }

        @Override
        public int getAttackDistance(Entity entity) {
            return currentMethod.getAttackDistance(entity);
        }
    }
}
