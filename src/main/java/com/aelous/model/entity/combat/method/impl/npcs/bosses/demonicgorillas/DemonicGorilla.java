package com.aelous.model.entity.combat.method.impl.npcs.bosses.demonicgorillas;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | March, 13, 2021, 22:08
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DemonicGorilla extends NPC {

    public CombatAI getCombatAI() {
        return combatAI;
    }

    private final CombatAI combatAI;
    private Phase phase;

    public DemonicGorilla(int id, Tile tile) {
        super(id, tile);
        this.phase = Phase.forId(id);
        this.combatAI = new CombatAI(this);
        this.getAsNpc().setHeadHint(phase.headHintID);
        this.setCombatMethod(combatAI);
    }

    @Override
    public void onHit(Hit hit) {
        if (hp() - hit.getDamage() > 0) {
            combatAI.onDamageTaken(hit);
        }
    }

    enum Phase {
        MELEE(7144, CombatType.MELEE, 2, new DemonicGorillaMeleeStrategy()),
        RANGED(7145, CombatType.RANGED, 1, new DemonicGorillaRangedStrategy()),
        MAGIC(7146, CombatType.MAGIC, 0, new DemonicGorillaMagicStrategy());

        public final int npcId, headHintID;
        public final CombatMethod method;
        public final CombatType type;

        Phase(int npcId, CombatType type, int headHintID, CombatMethod method) {
            this.npcId = npcId;
            this.type = type;
            this.headHintID = headHintID;
            this.method = method;
        }

        public static Phase forType(CombatType type) {
            for (Phase phase : values()) {
                if (phase.type == type)
                    return phase;
            }
            return MELEE;
        }

        public static Phase nextPhase(Phase old) {
            return values()[old.ordinal() + 1 % values().length - 1];
        }

        public static Phase forId(int id) {
            for (Phase phase : values()) {
                if (phase.npcId == id)
                    return phase;
            }
            return null;
        }
    }

    public static Phase forId(int id) {
        for (Phase phase : Phase.values()) {
            if (phase.headHintID == id)
                return phase;
        }
        return null;
    }

    public static class CombatAI extends CommonCombatMethod {

        private final DemonicGorilla demonic;
        private CombatMethod currentMethod;
        private int missCounter;
        private int damageCounter;

        public CombatAI(DemonicGorilla demonic) {
            this.demonic = demonic;
            currentMethod = demonic.phase.method;
        }

        void updatePhase(Phase phase) {
            demonic.phase = phase;
            currentMethod = phase.method;
            demonic.transmog(phase.npcId);
            demonic.setHeadHint(phase.headHintID);
            demonic.getCombat().delayAttack(1);
        }

        public void onDamageTaken(Hit hit) {
            damageCounter += hit.getDamage();
            if (damageCounter >= 50) {
                updatePhase(Phase.forType(hit.getCombatType()));
                damageCounter = 0;
            }
        }

        @Override
        public boolean prepareAttack(Entity entity, Entity target) {
            if (demonic.phase == Phase.MAGIC || demonic.phase == Phase.RANGED) {
                if (Utils.random(4) == 1) {
                    boulderToss(entity, target);
                    entity.getCombat().delayAttack(4);
                    return false;
                }
            }
            if (withinDistance(1))
                return currentMethod.prepareAttack(entity, target);
            return false;
        }

            @Override
            public int getAttackSpeed (Entity entity){
                return currentMethod.getAttackSpeed(entity);
            }

            @Override
            public int getAttackDistance (Entity entity){
                return currentMethod.getAttackDistance(entity);
            }

            public void handleAfterHit (Hit hit){
                if (hit.getDamage() == 0) {
                    missCounter++;
                    if (missCounter == 4) {
                        updatePhase(Phase.nextPhase(demonic.phase));
                        missCounter = 0;
                    }
                }
            }

            private void boulderToss (Entity entity, Entity target){
                Tile boulderTile = target.tile().clone();
                entity.animate(7228);
                Projectile p = new Projectile(target.tile().transform(-1, -1, 0), target.tile(), 0, 856, 165, 41, 127, 0, 0);
                entity.executeProjectile(p);
                Hit hit = target.hit(entity, (int) Math.ceil(target.maxHp() * 0.33), 0, null).setAccurate(true);
                Chain.bound(null).name("boulderTask").cancelWhen(() -> !target.tile().inSqRadius(boulderTile, 1) || target.dead()).runFn(11, () -> {
                    if (target.tile().inSqRadius(boulderTile, 1)) {
                        hit.submit();
                    }
                });
                World.getWorld().tileGraphic(305, boulderTile, 5, p.getSpeed());
            }
        }
    }
