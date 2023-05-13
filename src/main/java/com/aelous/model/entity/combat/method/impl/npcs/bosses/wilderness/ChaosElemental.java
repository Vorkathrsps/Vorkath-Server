package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

public class ChaosElemental extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC npc = (NPC) entity;
        int random = Utils.random(7);
        npc.animate(npc.attackAnimation());
        switch (random) {
            case 1:
                if(!target.tile().memberCave()) {
                    teleport_attack(npc, target);
                } else {
                    primary_attack(npc, target);
                }
                break;
            case 2:
                disarming_attack(npc, target);
                break;
            default:
                primary_attack(npc, target);
        }
        return true;
    }

    private void disarming_attack(NPC npc, Entity target) {
        attack_logic(npc, target, 550, 551, 552);
        Player player = (Player) target;
        final Item item = player.getEquipment().get(EquipSlot.WEAPON);
        if (player.inventory().hasFreeSlots(1)) {
            if(item != null) {
                player.getEquipment().remove(item, EquipSlot.WEAPON, true);
                player.getEquipment().unequip(EquipSlot.WEAPON);
                player.getCombat().setRangedWeapon(null);//Also reset ranged weapons, otherwise it might think we still have on equipped.
                WeaponInterfaces.updateWeaponInterface(player);
                CombatSpecial.updateBar(player);
                player.setSpecialActivated(false);
                Autocasting.setAutocast(player, null);
                player.looks().resetRender();
                player.inventory().add(item);
            }
        }
    }

    private void teleport_attack(NPC npc, Entity target) {
        int random = Utils.random(5 + 1);//+1 cuz we can hit 0
        attack_logic(npc, target, 553, 554, 555);
        target.teleport(target.tile().x - random, target.tile().y - random, target.tile().level);
    }

    private void primary_attack(NPC npc, Entity target) {
        attack_logic(npc, target, 556, 557, 558);
    }

    private void attack_logic(NPC npc, Entity target, int initial_graphic, int projectile, int end_graphic) {
        CombatType combat_style = World.getWorld().random(10) > 7 ? CombatType.MAGIC : CombatType.RANGED;
        npc.graphic(initial_graphic, GraphicHeight.HIGH, 0);
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, projectile, 51, duration, 43, 31, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, combat_style), delay, combat_style).checkAccuracy().submit();
        target.graphic(end_graphic, GraphicHeight.HIGH, p.getSpeed());
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
