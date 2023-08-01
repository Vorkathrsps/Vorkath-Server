package com.cryptic.model.entity.combat.method.impl.npcs.raids.cox;

import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import static com.cryptic.model.entity.attributes.AttributeKey.MUTTADILE_EATING_STATE;
import static com.cryptic.model.entity.attributes.AttributeKey.MUTTADILE_HEAL_COUNT;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.MUTTADILE_7562;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.MUTTADILE_7563;

/**
 * @Author Origin
 * @Since October 29, 2021
 */
public class Muttadile extends CommonCombatMethod {

    private int triedHealCount = 0;

    private void heal() {
        NPC npc = entity.getAsNpc();
        Player player = target.getAsPlayer();
        Party party = player.raidsParty;
        int maxHp = npc.maxHp();
        int currentHp = npc.hp();
        final int hpPercentage = (currentHp * 100 / maxHp);
        GameObject meatTree = party.getMeatTree();

        npc.getCombat().reset();
        triedHealCount++;
        npc.startEvent(0, () -> {
            npc.lock();
            if ((hpPercentage < 40 || npc.<Boolean>getAttribOr(MUTTADILE_EATING_STATE, false)) && npc.<Integer>getAttribOr(MUTTADILE_HEAL_COUNT, 0) < 3) {
                if (npc.tile().isWithinDistance(meatTree.tile(), 2)) {
                    npc.setEntityInteraction(null);
                    npc.putAttrib(MUTTADILE_EATING_STATE, true);
                    npc.getRouteFinder().routeObject(party.getMeatTree());
                    npc.setPositionToFace(meatTree.tile());
                    npc.noRetaliation(true);
                    npc.animate(npc.attackAnimation());
                    npc.heal(World.getWorld().random(125, 200));
                }
                int newHpPercentage = (currentHp * 100 / maxHp);
                if (newHpPercentage > 70) {
                    npc.putAttrib(MUTTADILE_EATING_STATE, false);
                    npc.setPositionToFace(target.tile());
                    TargetRoute.reset(entity);
                    entity.unlock();
                }
            }
        });
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        Party party = target.getAsPlayer().raidsParty;

        if (!withinDistance(8)) {
            return false;
        }

        if (entity.getAsNpc().id() == MUTTADILE_7563)
            if (withinDistance(1) && Utils.rollDie(4, 1))
                meleeAttack(entity.getAsNpc(), target);
            else if (withinDistance(1))
                shockwaveAttack(entity.getAsNpc(), target);
            else if (Utils.rollDie(2, 1))
                magicAttack(entity.getAsNpc(), target);
            else if (triedHealCount < 3 && party != null)
                if (Utils.rollPercent(30))
                    heal();
                else
                    rangeAttack(entity.getAsNpc(), target);

        if (entity.getAsNpc().id() == MUTTADILE_7562)
            meleeAttack(entity.getAsNpc(), target);
        else if (withinDistance(1))
            rangeAttack(entity.getAsNpc(), target);

        return true;
    }

    private void meleeAttack(NPC npc, Entity entity) {
        npc.getCombatInfo().maxhit = 78;
        npc.animate(npc.attackAnimation());
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
    }

    private void rangeAttack(NPC npc, Entity entity) {
        if (target.isPlayer()) {
            Party party = target.getAsPlayer().raidsParty;
            if (party == null) {
                return;
            }

            for (Player member : party.getMembers()) {
                if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && member.tile().inArea(new Area(3300, 5313, 3324, 5338, member.raidsParty.getHeight()))) {
                    npc.getCombatInfo().maxhit = 35;
                    npc.animate(npc.attackAnimation());
                    var tileDist = npc.tile().transform(1, 1, 0).distance(member.tile());
                    var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
                    new Projectile(npc, member, 1291, 20, 12 * tileDist, npc.id() == MUTTADILE_7562 ? 15 : 35, 30, 0).sendProjectile();
                    member.hit(npc, CombatFactory.calcDamageFromType(npc, member, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
                }
            }
        }
    }

    private void magicAttack(NPC npc, Entity entity) {
        if (target.isPlayer()) {
            Party party = target.getAsPlayer().raidsParty;
            if (party == null) {
                return;
            }

            for (Player member : party.getMembers()) {
                if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && member.tile().inArea(new Area(3300, 5313, 3324, 5338, member.raidsParty.getHeight()))) {
                    npc.getCombatInfo().maxhit = 45;
                    npc.animate(npc.attackAnimation());
                    var tileDist = npc.tile().transform(1, 1, 0).distance(member.tile());
                    var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
                    new Projectile(npc, member, 393, 20, 12 * tileDist, 35, 30, 0).sendProjectile();
                    member.hit(npc, CombatFactory.calcDamageFromType(npc, member, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
                }
            }
        }
    }

    private void shockwaveAttack(NPC npc, Entity entity) {
        npc.getCombatInfo().maxhit = 118;
        npc.animate(7424);
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
        target.message(Color.RED.wrap("You have been hit by the Muttadiles stomp attack!"));
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}
