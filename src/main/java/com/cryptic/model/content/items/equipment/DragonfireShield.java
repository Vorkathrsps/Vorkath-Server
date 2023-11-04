package com.cryptic.model.content.items.equipment;

import com.cryptic.GameServer;
import com.cryptic.model.World;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.timers.TimerKey;

import static com.cryptic.utility.ItemIdentifiers.DRAGONFIRE_WARD;

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
                if (!player.getPlayerRights().isOwner(player)) {
                    player.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 4);
                    player.getTimers().register(TimerKey.DRAGONFIRE_SPECIAL, 200);
                    player.getCombat().reset();
                }

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
            target.freeze(25, attacker, true);
        }

        Hit hit = target.hit(attacker, dmg, delay, null).checkAccuracy(true).postDamage(p -> {
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
        attacker.animate(new Animation(6696, Priority.HIGH));
        attacker.graphic(1165);
        int tileDist = attacker.tile().transform(1, 1).getChevDistance(target.tile());
        int duration = (80 + 11 + (5 * tileDist));
        Projectile p1 = new Projectile(attacker, target, 1166, 80, duration, 40, 30, 0, target.getSize(), 5);
        final int delay = attacker.executeProjectile(p1);
        Hit hit = target.hit(attacker, dmg, delay, null).checkAccuracy(true).postDamage(p -> {
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
