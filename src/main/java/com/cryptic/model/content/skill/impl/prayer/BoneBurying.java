package com.cryptic.model.content.skill.impl.prayer;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.perks.SkillingSets;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.inter.dialogue.ChatBoxItemDialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

import java.util.concurrent.atomic.AtomicInteger;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

public class BoneBurying extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        int id = item.getId();
        Bone bones = Bone.get(id);
        if (option == 1) {
            if (bones != null) {
                if (item.getId() == bones.itemId) {
                    bury(player, bones);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        int[] altars = new int[]{ALTAR_14860, ALTAR, ALTAR_2640, CHAOS_ALTAR_411};
        int id = item.getId();
        Bone bones = Bone.get(id);
        for (var a : altars) {
            if (object.getId() == a) {
                if (bones != null) {
                    startBonesOnAltar(player, bones, object);
                    return true;
                }
            }
        }
        return false;
    }

    private void bury(Player player, Bone bone) {
        if (player.getTimers().has(TimerKey.BONE_BURYING)) return;
        player.getMovementQueue().clear();
        player.getTimers().extendOrRegister(TimerKey.BONE_BURYING, 3);
        player.getInventory().remove(bone.itemId);
        player.animate(827);
        player.message("You dig a hole in the ground...");
        player.sendPrivateSound(2738, 0);
        var xp = bone.xp / 2;
        if (bone.itemId == 11943 && player.tile().inArea(3172, 3799, 3232, 3857)) xp *= 4;
        xp = isSetExperienceBoost(player, xp);
        player.getSkills().addXp(Skills.PRAYER, xp);
        Chain.bound(player).runFn(1, () -> player.message("You bury the bones."));
    }

    private static double isSetExperienceBoost(Player player, double xp) {
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.PRAYER)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    xp *= set.experienceBoost;
                    break;
                }
            }
        }
        return xp;
    }

    private void startBonesOnAltar(Player player, Bone bones, GameObject obj) {
        int amt = player.inventory().count(bones.itemId);

        if (amt == 1) {
            altarTask(player, bones, obj, 1);
            return;
        }

        ChatBoxItemDialogue.sendInterface(player, 1746, 170, bones.itemId);
        player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
            @Override
            public void firstOption(Player player) {
                altarTask(player, bones, obj, 1);
            }

            @Override
            public void secondOption(Player player) {
                altarTask(player, bones, obj, 5);
            }

            @Override
            public void thirdOption(Player player) {
                player.setAmountScript("Enter amount.", value -> {
                    altarTask(player, bones, obj, (Integer) value);
                    return true;
                });
            }

            @Override
            public void fourthOption(Player player) {
                altarTask(player, bones, obj, amt);
            }
        };

    }

    private void altarTask(Player player, Bone bones, GameObject obj, int amt) {
        AtomicInteger count = new AtomicInteger(0);

        if (amt == 1) {
            boneOnAltar(player, bones, obj);
            return;
        }

        boneOnAltar(player, bones, obj);

        player.repeatingTask(4, altarTask -> {
            if (altarTask.isStopped()) {
                return;
            }

            if (player.getInventory().isEmpty() || player.dead()) {
                altarTask.stop();
                return;
            }

            if (count.get() == amt) {
                count.getAndSet(0);
                altarTask.stop();
                return;
            }

            boneOnAltar(player, bones, obj);
            count.getAndIncrement();
        });
    }

    public void boneOnAltar(Player player, Bone bones, GameObject object) {
        player.animate(3705);
        World.getWorld().sendUnclippedTileGraphic(624, object.tile(), 0, 0);

        var removeBone = true;

        int chance = 2;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER, SAPPHIRE_MEMBER -> chance = 3;
            case EMERALD_MEMBER,DIAMOND_MEMBER -> chance = 4;
            case DRAGONSTONE_MEMBER, ONYX_MEMBER -> chance = 5;
            case ZENYTE_MEMBER -> chance = 6;
        }
        if (object.getId() == CHAOS_ALTAR_411 && object.tile().equals(2947, 3820, 0)) {
            if (World.getWorld().rollDie(chance, 1)) {
                removeBone = false;
            }
        }

        if (removeBone) {
            player.inventory().remove(new Item(bones.itemId), true);
        }

        player.sendPrivateSound(958, 0);

        double experienece = bones.xp;
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.PRAYER)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    experienece *= set.experienceBoost;
                    break;
                }
            }
        }
        if (ObjectManager.objById(13213, new Tile(3095, 3506)) != null &&
            ObjectManager.objById(13213, new Tile(3098, 3506)) != null) {
            player.message("The gods are very pleased with your offerings.");
            player.getSkills().addXp(Skills.PRAYER, experienece);
        } else if (object.getId() == CHAOS_ALTAR_411 && object.tile().equals(2947, 3820, 0)) {
            player.message("The gods are pleased with your offerings.");
            player.getSkills().addXp(Skills.PRAYER, experienece);
        } else {
            player.message("The gods are pleased with your offerings.");
            player.getSkills().addXp(Skills.PRAYER, experienece);
        }
    }

}
