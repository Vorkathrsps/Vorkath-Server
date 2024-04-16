package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.annotate.CombatScript;
import com.cryptic.model.content.duel.DuelRule;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.combat.method.CombatMethod;
import com.cryptic.model.entity.combat.method.EntityCombatBuilder;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.utility.Debugs;
import com.cryptic.utility.chainedwork.Chain;
import org.jetbrains.annotations.Nullable;

import javax.management.Attribute;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.VESPULA;


@CombatScript
public abstract class CommonCombatMethod implements CombatMethod {

    public Entity entity, target;

    public void set(Entity entity, Entity target) {
        this.entity = entity;
        this.target = target;
    }

    protected boolean withinDistance(int distance) {
        return DumbRoute.withinDistance(entity, target, distance);
    }

    public EntityCombatBuilder combatBuilder(Entity entity) {
        return new EntityCombatBuilder(entity, target);
    }

    public void onRespawn(NPC npc) {

    }

    public void process(Entity entity, @Nullable Entity target) {

    }

    protected boolean isInsideCombatBoundary() {
        var center = calculateDistanceBetweenCenters();
        return center > calculateCombatBoundary();
    }
    
    protected double calculateCombatBoundary() {
        double maxAllowedDistance = Math.ceil(calculateMaxAllowedDistance());
        double maximumReach = maxAllowedDistance * maxAllowedDistance;
        return maximumReach * maximumReach;
    }

    protected boolean withinDistance() {
        double maxAllowedDistance = Math.ceil(calculateMaxAllowedDistance());
        double maximumReach = maxAllowedDistance * maxAllowedDistance;
        double distanceBetweenCenters = Math.floor(calculateDistanceBetweenCenters());
        return distanceBetweenCenters <= maximumReach;
    }

    protected boolean isReachable() {
        double distanceBetweenCenters = calculateDistanceBetweenCenters();
        double maxAllowedDistance = calculateMaxAllowedDistance();
        return Math.round(distanceBetweenCenters) <= Math.round(maxAllowedDistance);
    }

    private double calculateDistanceBetweenCenters() {
        double entityCenterX = entity.tile().getX() + entity.getSize() / 2.0;
        double entityCenterY = entity.tile().getY() + entity.getSize() / 2.0;
        double targetCenterX = target.tile().getX() + target.getSize() / 2.0;
        double targetCenterY = target.tile().getY() + target.getSize() / 2.0;
        double dx = entityCenterX - targetCenterX;
        double dy = entityCenterY - targetCenterY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    protected double calculateMaxAllowedDistance() {
        return (double) entity.getSize() / 2 + (double) target.getSize() / 2;
    }

    public void onRetreat(Entity entity, BooleanSupplier waitUntil, BooleanSupplier cancel, AttributeKey key) {
        var npc = (NPC) entity;
        final int[] ticks = {4};
        npc.setEntityInteraction(null);
        npc.waitUntil(waitUntil, () ->
            Chain.noCtx().repeatingTask(1, tick -> {
                ticks[0]--;
                if (ticks[0] <= 0) {
                    npc.clearAttribs();
                    tick.stop();
                }
            }).cancelWhen(() -> {
                npc.clearAttrib(key);
                return cancel.getAsBoolean();
            })).cancelWhen(() -> {
            npc.clearAttrib(key);
            return cancel.getAsBoolean();
        });
    }

    /**
     * npc only
     */
    public void doFollowLogic() {
        // override me if you want any other logic
        DumbRoute.step(entity, target, moveCloseToTargetTileRange(entity));
    }

    protected void follow(int distance) {
        DumbRoute.step(entity, target, distance);
    }

    /**
     * player only
     */
    public void postAttack() {
        if (entity.isPlayer() && this == CombatFactory.MAGIC_COMBAT) {
            entity.setEntityInteraction(null);
            entity.getCombat().setCastSpell(null);
        }
    }

    public void onHit(Entity entity, Entity target, Hit hit) {

    }

    /**
     * npc only
     */
    public void onDeath(@Nullable Player killer, NPC npc) {
    }

    public void postDefend(Hit hit) {

    }

    public void postTargetDefend(Hit hit, Entity mob) {

    }

    /**
     * npc only
     */
    public void postDamage(Hit hit) {

    }

    /**
     * npc only
     */
    public void preDefend(Hit hit) {
    }

    /**
     * Handler functions
     */

    public void init(NPC npc) {

    }

    public List<Entity> getPossibleTargets(Entity entity) {
        return getPossibleTargets(entity, 14, true, false);
    }

    public List<Entity> getPossibleTargets(Entity entity, int ratio, boolean players, boolean npcs) {
        List<Entity> possibleTargets = new ArrayList<>();
        if (players) {
            for (Player player : World.getWorld().getPlayers()) {
                if (player == null || player.dead() || player.tile().distance(entity.getCentrePosition()) > ratio || player.tile().level != entity.tile().level) {
                    continue;
                }
                possibleTargets.add(player);
            }
        }
        if (npcs) {
            for (NPC npc : World.getWorld().getNpcs()) {
                if (npc == null || npc == entity || npc.dead() || npc.getCentrePosition().distance(entity.getCentrePosition()) > ratio || npc.tile().level != entity.tile().level) {
                    continue;
                }
                possibleTargets.add(npc);
            }
        }
        return possibleTargets;
    }

    public boolean canAttackStyle(Entity entity, Entity other, CombatType type) {
        //Specific combat style checks
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            boolean magicOnly = player.getAttribOr(AttributeKey.MAGEBANK_MAGIC_ONLY, false);
            CombatSpell spell = player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() : player.getCombat().getAutoCastSpell();

            // If you're in the mage arena, where it is magic only.
            if (type != CombatType.MAGIC && magicOnly) {
                player.message("You can only use magic inside the arena!");
                player.getCombat().reset();
                return false;
            }

            if (type == CombatType.MAGIC) {
                if (spell != null && !spell.canCast(player, other, false)) {
                    player.getCombat().reset();//We can't cast this spell reset combat
                    player.getCombat().setCastSpell(null);
                    Debugs.CMB.debug(entity, "spell !cancast.", other, true);
                    return false;
                }

                // Duel, disabled magic?
                if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MAGIC.ordinal()]) {
                    DialogueManager.sendStatement(player, "Magic has been disabled in this duel!");
                    player.getCombat().reset();
                    Debugs.CMB.debug(entity, "no magic in duel.", other, true);
                    return false;
                }
            } else if (type == CombatType.RANGED) {
                // Duel, disabled ranged?
                if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_RANGED.ordinal()]) {
                    DialogueManager.sendStatement(player, "Ranged has been disabled in this duel!");
                    player.getCombat().reset();//Ranged attacks disabled, stop combat
                    Debugs.CMB.debug(entity, "no range in duel.", other, true);
                    return false;
                }

                // Check that we have the ammo required
                if (!CombatFactory.checkAmmo(player)) {
                    Debugs.CMB.debug(entity, "no ammo", other, true);
                    player.getCombat().reset();//Out of ammo, stop combat
                    return false;
                }
            } else if (type == CombatType.MELEE) {
                if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MELEE.ordinal()]) {
                    DialogueManager.sendStatement(player, "Melee has been disabled in this duel!");
                    player.getCombat().reset();//Melee attacks disabled, stop combat
                    Debugs.CMB.debug(entity, "no melee in duel.", other, true);
                    return false;
                }
                //Att acking Aviansie with melee.
                if (other.isNpc()) {
                    int id = other.getAsNpc().id();

                    if (id == VESPULA) {
                        entity.message("Vespula is flying too high for you to hit with melee!");
                        entity.getCombat().reset();//Vespula out of range, stop combat
                        return false;
                    }

                    if (id == 3166 || id == 3167 || id == 3168 || id == 3169 || id == 3170 || id == 3171
                        || id == 3172 || id == 3173 || id == 3174 || id == 3175 || id == 3176 || id == 3177
                        || id == 3178 || id == 3179 || id == 3180 || id == 3181 || id == 3182 || id == 3183) {
                        entity.message("The Aviansie is flying too high for you to attack using melee.");
                        entity.getCombat().reset();//Aviansie out of range, stop combat
                        return false;
                    } else if (id >= 3162 && id <= 3165 || id == 15016 || id == 11113) {
                        entity.message("It's flying too high for you to attack using melee.");
                        entity.getCombat().reset();//Monster out of range, stop combat
                        return false;
                    }
                }
            }
        }
        if (other.isPlayer()) {
            other.getAsPlayer().setLastActiveOverhead();
        }
        return true;
    }

    public CombatType styleOf() {
        if (entity instanceof NPC) {
            return null;
        } else {
            if (this instanceof MagicCombatMethod || this.getClass().getPackageName().contains("magic"))
                return CombatType.MAGIC;
            if (this instanceof RangedCombatMethod || this.getClass().getPackageName().contains("range"))
                return CombatType.RANGED;
            if (this instanceof MeleeCombatMethod || this.getClass().getPackageName().contains("melee"))
                return CombatType.MELEE;

            System.err.println("Unknown player styleOf combat script: " + this + " wep " + entity.getAsPlayer().getEquipment().getId(3));
        }
        return null;
    }


    public boolean isAggressive() {
        return entity.isNpc() && entity.npc().getCombatInfo() != null && entity.npc().getCombatInfo().aggressive && entity.npc().inViewport();
    }
}
