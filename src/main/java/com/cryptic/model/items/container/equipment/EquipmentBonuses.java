package com.cryptic.model.items.container.equipment;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.NPCCombatInfo;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import lombok.Data;

import static com.cryptic.utility.ItemIdentifiers.TOXIC_BLOWPIPE;

@Data
public class EquipmentBonuses {
    public int stab;
    public int slash;
    public int crush;
    public int range;
    public int mage;
    public int stabdef;
    public int slashdef;
    public int crushdef;
    public int rangedef;
    public int magedef;
    public int str;
    public int rangestr;
    public int magestr;
    public int pray;

    public EquipmentBonuses totalBonuses(Entity mob, EquipmentInfo info) {
        return totalBonuses(mob, info, false);
    }

    public EquipmentBonuses totalBonuses(Entity entity, EquipmentInfo info, boolean ignoreAmmo) {
        EquipmentBonuses bonuses = new EquipmentBonuses();

        if (entity instanceof Player player) {
            Item wep = player.getEquipment().get(EquipSlot.WEAPON);
            int weaponID = wep != null ? wep.getId() : -1;

            for (int i = 0; i < 14; i++) {
                if (i == EquipSlot.AMMO && ignoreAmmo) continue;
                Item equipped = player.getEquipment().get(i);
                if (equipped != null) {
                    if (i == EquipSlot.AMMO && ((weaponID >= 4212 && weaponID <= 4223) || weaponID == TOXIC_BLOWPIPE || weaponID == 28688)) {
                        continue;
                    }

                    var equipmentBonuses = World.getWorld().getEquipmentLoader().getInfo(equipped.getId()).getEquipment();
                    bonuses.stab += equipmentBonuses.getAstab();
                    bonuses.slash += equipmentBonuses.getAslash();
                    bonuses.crush += equipmentBonuses.getAcrush();
                    bonuses.range += equipmentBonuses.getArange();
                    bonuses.mage += equipmentBonuses.getAmagic();
                    bonuses.stabdef += equipmentBonuses.getDstab();
                    bonuses.slashdef += equipmentBonuses.getDslash();
                    bonuses.crushdef += equipmentBonuses.getDcrush();
                    bonuses.rangedef += equipmentBonuses.getDrange();
                    bonuses.magedef += equipmentBonuses.getDmagic();
                    bonuses.str += equipmentBonuses.getStr();
                    bonuses.rangestr += equipmentBonuses.getRstr();
                    bonuses.magestr += equipmentBonuses.getMdmg();
                    bonuses.pray += equipmentBonuses.getPrayer();
                }
            }

            int boost = player.sigil.processEquipmentModification(player);
            bonuses.stab += boost;
            bonuses.slash += boost;
            bonuses.crush += boost;

        } else {
            if (entity instanceof NPC npc && npc.getCombatInfo() != null) {
                NPCCombatInfo.Bonuses i = npc.getCombatInfo().originalBonuses;
                bonuses.stabdef = i.stabdefence;
                bonuses.slashdef = i.slashdefence;
                bonuses.crushdef = i.crushdefence;
                bonuses.rangedef = i.rangeddefence;
                bonuses.magedef = i.magicdefence;
                bonuses.range = i.ranged;
                bonuses.mage = i.magic;
                bonuses.str = i.strength;
                bonuses.crush = i.attack;
                bonuses.stab = i.attack;
                bonuses.slash = i.attack;
                bonuses.rangestr = i.rangestrength;
                bonuses.magestr = i.magicstrength;
            }
        }
        return bonuses;
    }

    @Override
    public String toString() {
        return "EquipmentBonuses{" +
            "stab=" + stab +
            ", slash=" + slash +
            ", crush=" + crush +
            ", range=" + range +
            ", mage=" + mage +
            ", stabdef=" + stabdef +
            ", slashdef=" + slashdef +
            ", crushdef=" + crushdef +
            ", rangedef=" + rangedef +
            ", magedef=" + magedef +
            ", str=" + str +
            ", rangestr=" + rangestr +
            ", magestr=" + magestr +
            ", pray=" + pray +
            '}';
    }
}
