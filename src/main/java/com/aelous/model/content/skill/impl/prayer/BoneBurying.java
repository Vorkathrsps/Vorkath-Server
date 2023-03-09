package com.aelous.model.content.skill.impl.prayer;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.ChatBoxItemDialogue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * Created by Carl on 2015-08-12.
 */
public class BoneBurying extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 1) {
            for (Bone bone : Bone.values()) {
                if (item.getId() == bone.itemId) {
                    bury(player, bone);
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

    private void bury(Player player, Bone bone) {
        if (player.getTimers().has(TimerKey.BONE_BURYING))
            return;

        player.getMovementQueue().clear();
        player.getTimers().extendOrRegister(TimerKey.BONE_BURYING, 2);
        player.inventory().remove(new Item(bone.itemId), player.getAttribOr(AttributeKey.ITEM_SLOT, 0), true);
        player.animate(827);
        player.message("You dig a hole in the ground...");

        var xp = bone.xp;

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
            player.action.execute(boneOnAltarAction(player, bones, obj, 1), true);
        } else {
            ChatBoxItemDialogue.sendInterface(player, 1746, 170, bones.itemId);
            player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
                @Override
                public void firstOption(Player player) {
                    player.action.execute(boneOnAltarAction(player, bones, obj, 1), true);
                }

                @Override
                public void secondOption(Player player) {
                    player.action.execute(boneOnAltarAction(player, bones, obj, 5), true);
                }

                @Override
                public void thirdOption(Player player) {
                    player.setAmountScript("Enter amount.", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            player.action.execute(boneOnAltarAction(player, bones, obj, (Integer) value), true);
                            return true;
                        }
                    });
                }

                @Override
                public void fourthOption(Player player) {
                    player.action.execute(boneOnAltarAction(player, bones, obj, amt), true);
                }
            };
        }
    }

    private Action<Player> boneOnAltarAction(Player player, Bone bones, GameObject obj, int amount) {
        return new Action<>(player, 3) {
            int ticks = 0;

            @Override
            public void execute() {
                if (!player.inventory().contains(new Item(bones.itemId))) {
                    player.message("You have ran out of bones.");
                    stop();
                    return;
                }

                var removeBone = true;

                if(obj.getId() == CHAOS_ALTAR_411 && obj.tile().equals(2947,3820,0)) {
                    if(World.getWorld().rollDie(2,1)) {
                        removeBone = false; // 50% chance that your bone is not removed.
                    }
                }

                if(removeBone) {
                    player.inventory().remove(new Item(bones.itemId), true);
                }
                player.animate(896);

                //Tasks
                player.getTaskMasterManager().increase(Tasks.BONES_ON_ALTAR);

                World.getWorld().tileGraphic(624, obj.tile(), 0, 0);
                if (ObjectManager.objById(13213, new Tile(3095, 3506)) != null &&
                    ObjectManager.objById(13213, new Tile(3098, 3506)) != null) { // Gilded altar locations
                    player.message("The gods are very pleased with your offerings.");
                    player.getSkills().addXp(Skills.PRAYER, bones.xp * 3);
                } else if(obj.getId() == CHAOS_ALTAR_411 && obj.tile().equals(2947,3820,0)) {
                    player.message("The gods are pleased with your offerings.");
                    player.getSkills().addXp(Skills.PRAYER, bones.xp * 3);
                } else {
                    player.message("The gods are pleased with your offerings.");
                    player.getSkills().addXp(Skills.PRAYER, bones.xp * 2);
                }

                if (++ticks == amount) {
                    stop();
                }
            }

            @Override
            public String getName() {
                return "Bones on altar";
            }

            @Override
            public boolean prioritized() {
                return false;
            }

            @Override
            public WalkablePolicy getWalkablePolicy() {
                return WalkablePolicy.NON_WALKABLE;
            }
        };
    }

}
