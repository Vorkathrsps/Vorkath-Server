package com.aelous.model.content.areas.burthope.warriors_guild;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.masks.FaceDirection;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Tuple;
import com.aelous.utility.chainedwork.Chain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.MAGICAL_ANIMATOR;

/**
 * @author Patrick van Elderen | March, 26, 2021, 09:51
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MagicalAnimator extends PacketInteraction {

    public enum ArmourSets {
        BRONZE(2450, new Item(1075), new Item(1117), new Item(1155)),
        IRON(2451, new Item(1067), new Item(1115), new Item(1153)),
        STEEL(2452, new Item(1069), new Item(1119), new Item(1157)),
        BLACK(2453, new Item(1077), new Item(1125), new Item(1165)),
        MITHRIL(2454, new Item(1071), new Item(1121), new Item(1159)),
        ADAMANT(2455, new Item(1073), new Item(1123), new Item(1161)),
        RUNE(2456, new Item(1079), new Item(1127), new Item(1163));

        public final int npc;
        public final Item helm, legs, body;

        ArmourSets(final int npc, final Item helm, final Item legs, final Item body) {
            this.npc = npc;
            this.helm = helm;
            this.legs = legs;
            this.body = body;
        }
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);

        if(obj.getId() == MAGICAL_ANIMATOR) {

            // Resolve the armour
            List<ArmourSets> sets = Arrays.stream(ArmourSets.values()).filter(set -> item.getId() == set.helm.getId() || item.getId() == set.body.getId() || item.getId() == set.legs.getId()).collect(Collectors.toList());

            // Got one? Or na
            if (player.inventory().contains(sets.get(0).helm) && player.inventory().contains(sets.get(0).legs) && player.inventory().contains(sets.get(0).body)) {
                Tile spawnTile = new Tile(obj.tile().x, obj.tile().y, obj.tile().level);

                Chain.bound(player).name("MagicalAnimatorStartTask").runFn(1, () -> {
                    player.lock();
                    DialogueManager.sendStatement(player, "You place your armour on the platform where it disappears....");
                }).then(1, () -> player.animate(827)).then(3, () -> {
                    // Remove all the parts
                    player.inventory().remove(new Item(sets.get(0).helm));
                    player.inventory().remove(new Item(sets.get(0).legs));
                    player.inventory().remove(new Item(sets.get(0).body));
                }).then(2, () -> DialogueManager.sendStatement(player, "You place your armour on the platform where it disappears....", "The animator hums; something appears to be working...")).then(2, () -> TaskManager.submit(new ForceMovementTask(player, 1, new ForceMovement(player.tile().clone(), new Tile(0, +2), 45, 126, FaceDirection.SOUTH.direction)))).then(1, () -> player.animate(820, 5)).then(1, () -> {
                    NPC npc = new NPC(sets.get(0).npc, spawnTile);
                    npc.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));

                    World.getWorld().getNpcs().add(npc);
                    npc.setPositionToFace(npc.tile().transform(1, 0, 0));
                    npc.animate(4166, 1);
                    npc.forceChat("I'm ALIVE!");
                    npc.respawns(false);
                    player.getPacketSender().sendEntityHint(npc);
                    Chain.bound(null).name("MagicalAnimatorCloseDialogueTask").runFn(1, () -> {
                        //Close the players dialogue box & proceed with the rest
                        player.getInterfaceManager().closeDialogue();
                        player.unlock();
                    }).then(2, () -> npc.setPositionToFace(player.tile())).then(1, () -> npc.getCombat().attack(player));
                });
            } else {
                //If the player doesn't have a full set of armour, send this message instead of starting the sequence
                DialogueManager.sendStatement(player, "You need a platebody, legs and full helm of the same type to activate", "the armour animator.");
            }
            return true;
        }
        return false;
    }
}
