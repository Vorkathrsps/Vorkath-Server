package com.cryptic.model.content.skill.impl.prayer;

import com.cryptic.model.action.Action;
import com.cryptic.model.action.policy.WalkablePolicy;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.GameMode;
import com.cryptic.model.entity.player.InputScript;
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

/**
 * Created by Carl on 2015-08-12.
 */
public class BoneBurying extends PacketInteraction {
    public static final double multiplier = 30.0D;
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            for (Bone bone : Bone.values()) {
                if (item.getId() == bone.itemId) {
                    var gameModeMultiplier = player.getGameMode().equals(GameMode.REALISM) ? 10.0 : 50.0;
                    bury(player, bone, gameModeMultiplier);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        int[] altars = new int[]{ALTAR_14860, ALTAR, ALTAR_2640, CHAOS_ALTAR_411};
        for (int altar : altars) {
            if (object.getId() == altar) {
                int bone = player.getAttribOr(AttributeKey.ITEM_ID, -1);
                GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);
                Bone bones = Bone.get(bone);

                if (bones != null) {
                    startBonesOnAltar(player, bones, obj);
                }
                return true;
            }
        }
        return false;
    }

    private void bury(Player player, Bone bone, double multiplier) {
        if (player.getTimers().has(TimerKey.BONE_BURYING))
            return;

        player.getMovementQueue().clear();
        player.getTimers().extendOrRegister(TimerKey.BONE_BURYING, 2);
        player.inventory().remove(new Item(bone.itemId), player.getAttribOr(AttributeKey.ITEM_SLOT, 0), true);
        player.animate(827);
        player.message("You dig a hole in the ground...");

        var xp = bone.xp * multiplier;

        // Lava drag isle check
        if (bone.itemId == 11943 && player.tile().inArea(3172, 3799, 3232, 3857)) {
            xp *= 4;
        }

        String mes = "You bury the bones.";

        player.getSkills().addXp(Skills.PRAYER, xp);
        player.playSound(380);
        Chain.bound(player).runFn(1, () -> player.message(mes));
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
        var gameModeMultiplier = player.getGameMode().equals(GameMode.REALISM) ? 10.0 : 50.0;


        if (amt == 1) {
            boneOnAltar(player, bones, obj, gameModeMultiplier);
            return;
        }

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

            boneOnAltar(player, bones, obj, gameModeMultiplier);
            count.getAndIncrement();
        });
    }

    public void boneOnAltar(Player player, Bone bones, GameObject object, double multiplier) {
        player.animate(896);
        World.getWorld().tileGraphic(624, object.tile(), 0, 0);

        var removeBone = true;

        if (object.getId() == CHAOS_ALTAR_411 && object.tile().equals(2947, 3820, 0)) {
            if (World.getWorld().rollDie(2, 1)) {
                removeBone = false; // 50% chance that your bone is not removed.
            }
        }

        if (removeBone) {
            player.inventory().remove(new Item(bones.itemId), true);
        }

        if (ObjectManager.objById(13213, new Tile(3095, 3506)) != null &&
            ObjectManager.objById(13213, new Tile(3098, 3506)) != null) {
            player.message("The gods are very pleased with your offerings.");
            player.getSkills().addXp(Skills.PRAYER, bones.xp * multiplier);
        } else if (object.getId() == CHAOS_ALTAR_411 && object.tile().equals(2947, 3820, 0)) {
            player.message("The gods are pleased with your offerings.");
            player.getSkills().addXp(Skills.PRAYER, bones.xp * multiplier);
        } else {
            player.message("The gods are pleased with your offerings.");
            player.getSkills().addXp(Skills.PRAYER, bones.xp * multiplier);
        }
    }

}
