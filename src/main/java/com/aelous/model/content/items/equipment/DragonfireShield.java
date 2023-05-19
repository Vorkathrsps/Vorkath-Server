package com.aelous.model.content.items.equipment;

import com.aelous.GameServer;
import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.DRAGONFIRE_WARD;

/**
 * This class represents all the dragonfire shield actions.
 *
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date januari 31, 2020 09:41
 */
public class DragonfireShield extends PacketInteraction {

    @Override
    public boolean handleEquipmentAction(Player player, Item item, int slot) {
        for (DragonfireShieldType type : DragonfireShieldType.values()) {
            if (item.getId() == type.charged() && slot == EquipSlot.SHIELD) {
                executeSpecialAttack(player, type);
                return true;
            }
        }
        return false;
    }

    private void executeSpecialAttack(Player player, DragonfireShieldType type) {
        if ((GameServer.properties().production || !player.getPlayerRights().isAdministrator(player)) && player.getTimers().has(TimerKey.DRAGONFIRE_SPECIAL)) {
            player.message("Your shield is still on cooldown from its last use.");
        } else {
            Item shield = player.getEquipment().get(EquipSlot.SHIELD);
            if (shield == null)
                return;

            Entity target = player.getCombat().getTarget();

            if (target == null)
                return;

            final boolean inDistance = (player.tile().distance(target.tile()) <= 10);
            if (target != player && !player.dead() && CombatFactory.inCombat(player) && inDistance) {

                player.setEntityInteraction(target);

                // Allow timers to fire before player event
                player.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 4);
                player.getTimers().register(TimerKey.DRAGONFIRE_SPECIAL, 200);
                player.getCombat().reset();

                if (shield.getId() == DragonfireShieldType.WYVERN.charged()) {
                    wyvernSpecial(player, target);
                } else {
                    dragonfireSpecial(player, target);
                }
            }
        }
    }

    private void wyvernSpecial(Player attacker, Entity target) {
        int dmg = World.getWorld().random(attacker.getEquipment().hasAt(EquipSlot.SHIELD, DRAGONFIRE_WARD) ? 25 : 15);

        attacker.animate(7700);

        int tileDist = attacker.tile().transform(1, 1).getChevDistance(target.tile());
        int duration = (80 + 11 + (5 * tileDist));
        Projectile p1 = new Projectile(attacker, target, 500, 80, duration, 40, 30, 0, target.getSize(), 5);
        final int delay = attacker.executeProjectile(p1);

        if (dmg > 0) {
            target.freeze(25, attacker);
        }

        Hit hit = target.hit(attacker, dmg, delay, null).checkAccuracy().postDamage(p -> {
            if (target.dead() || attacker.dead()) {
                return;
            }
            if (!attacker.tile().isWithinDistance(target.tile())) {
                return;
            }
            if (target instanceof NPC) {
                attacker.getSkills().addXp(Skills.MAGIC, dmg * 4);
                attacker.getSkills().addXp(Skills.DEFENCE, dmg * 4);
                attacker.getSkills().addXp(Skills.HITPOINTS, (int) (dmg * .70));
            } else {
                attacker.getSkills().addXp(Skills.DEFENCE, dmg * 4);
            }
        });
        hit.submit();
        target.graphic(367, GraphicHeight.HIGH, p1.getSpeed());
    }

    private void dragonfireSpecial(Player attacker, Entity target) {
        int dmg = World.getWorld().random(25);
        attacker.animate(6696);
        attacker.graphic(1165);
        int tileDist = attacker.tile().transform(1, 1).getChevDistance(target.tile());
        int duration = (80 + 11 + (5 * tileDist));
        Projectile p1 = new Projectile(attacker, target, 1166, 80, duration, 40, 30, 0, target.getSize(), 5);
        final int delay = attacker.executeProjectile(p1);
        Hit hit = target.hit(attacker, dmg, delay, null).checkAccuracy().postDamage(p -> {
            if (target.dead() || attacker.dead()) {
                return;
            }
            if (!attacker.tile().isWithinDistance(target.tile())) {
                return;
            }
            if (target instanceof NPC) {
                attacker.getSkills().addXp(Skills.MAGIC, dmg * 4);
                attacker.getSkills().addXp(Skills.DEFENCE, dmg * 4);
                attacker.getSkills().addXp(Skills.HITPOINTS, (int) (dmg * .70));
            } else {
                attacker.getSkills().addXp(Skills.DEFENCE, dmg * 4);
            }
        });
        hit.submit();
        target.graphic(1167, GraphicHeight.HIGH, p1.getSpeed());
    }

    public static DragonfireShieldType getType(Player player) {
        for (DragonfireShieldType type : DragonfireShieldType.values()) {
            Item shield = player.getEquipment().get(EquipSlot.SHIELD);
            if (shield != null) {
                if (shield.getId() == type.charged() || shield.getId() == type.uncharged()) {
                    return type;
                }
            }
        }
        return null;
    }

    public enum DragonfireShieldType {
        REGULAR(11284, 11283, 6700),
        HALLOWEEN(30241, 30241, 6700),
        WARD(22003, 22002, 65535),
        WYVERN(21634, 21633, 65535);

        DragonfireShieldType(int uncharged, int charged, int unchargeAnimationId) {
            this.charged = charged;
            this.uncharged = uncharged;
            this.unchargeAnimationId = unchargeAnimationId;
        }

        private final int uncharged;
        private final int charged;
        private final int unchargeAnimationId;

        public int charged() {
            return charged;
        }

        public int uncharged() {
            return uncharged;
        }

        public int unchargeAnimationId() {
            return unchargeAnimationId;
        }
    }
}
