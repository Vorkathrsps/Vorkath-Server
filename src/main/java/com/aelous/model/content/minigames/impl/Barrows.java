package com.aelous.model.content.minigames.impl;

import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Tuple;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * Created by Bart on 11/27/2015. <-- created literally the class name noob
 *
 * @author Jak write 99.9% ty
 **/
public class Barrows extends PacketInteraction {

    private static final List<Integer> possibles = Arrays.asList(20720, 20770, 20772, 20721, 20771, 20722);
    private static final List<Integer> lootItemIds = Arrays.asList(ItemIdentifiers.DEATH_RUNE, ItemIdentifiers.BLOOD_RUNE, ItemIdentifiers.CHAOS_RUNE, ItemIdentifiers.MIND_RUNE, BOLT_RACK);
    private static final List<Integer> lootItemAmts = Arrays.asList(180, 80, 270, 450, 120);
    private static final List<Integer> barrowsItemIds = Arrays.asList(AHRIMS_HOOD, AHRIMS_STAFF, AHRIMS_ROBETOP, AHRIMS_ROBESKIRT, DHAROKS_HELM, DHAROKS_GREATAXE, DHAROKS_PLATEBODY,
        DHAROKS_PLATELEGS, GUTHANS_HELM, GUTHANS_WARSPEAR, GUTHANS_PLATEBODY, GUTHANS_CHAINSKIRT, KARILS_COIF, KARILS_CROSSBOW, KARILS_LEATHERTOP, KARILS_LEATHERSKIRT, TORAGS_HELM,
        TORAGS_HAMMERS, TORAGS_PLATEBODY, TORAGS_PLATELEGS, VERACS_HELM, VERACS_FLAIL, VERACS_BRASSARD, VERACS_PLATESKIRT, AMULET_OF_THE_DAMNED);

    private static int barrowsBrotherKc(Player player) {
        return (int) player.getAttribOr(AHRIM, 0) +
            (int) player.getAttribOr(DHAROK, 0) +
            (int) player.getAttribOr(GUTHAN, 0) +
            (int) player.getAttribOr(KARIL, 0) +
            (int) player.getAttribOr(TORAG, 0) +
            (int) player.getAttribOr(VERAC, 0);
    }

    public static void onRegionChange(Player player) {
        // Make it dark. Thou shalt not see.. a brother..
        if (player.tile().region() == 14231) {
            //When we enter a cave..
            player.getPacketSender().changeMapVisibility(2);
        }

        if (player.tile().region() != 14231) {
            //And when we leave the cave..
            player.getPacketSender().changeMapVisibility(0);

            var npc = player.<NPC>getAttribOr(barrowsBroSpawned, null);
            if (npc != null) {
                if (!npc.dead() && npc.hp() > 0) {
                    npc.stopActions(true);
                    player.clearAttrib(barrowsBroSpawned);
                    npc.lockNoDamage();
                    npc.hidden(true);
                    World.getWorld().unregisterNpc(npc);
                    player.getPacketSender().sendEntityHintRemoval(false);
                }
            }
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        Optional<Stairways> stairways = Stairways.getByObject(obj.getId());
        if (stairways.isPresent()) {
            player.teleport(stairways.get().tile);
            player.getInterfaceManager().sendOverlay(-1);
            return true;
        }

        // final looting chest at the bottom crypt maze
        if (obj.getId() == 20973) {
            var killedAll = (int) player.getAttribOr(DHAROK, 0) == 1
                && (int) player.getAttribOr(TORAG, 0) == 1
                && (int) player.getAttribOr(AHRIM, 0) == 1
                && (int) player.getAttribOr(KARIL, 0) == 1
                && (int) player.getAttribOr(VERAC, 0) == 1
                && (int) player.getAttribOr(GUTHAN, 0) == 1;

            if (!killedAll && player.<NPC>getAttribOr(AttributeKey.barrowsBroSpawned, null) == null && Barrows.barrowsBrotherKc(player) >= 5) {
                //spawn the last bro
                var targ = player.<Integer>getAttribOr(AttributeKey.FINAL_BARROWS_BRO_COFFINID, 0);
                var broId = 0;

                switch (targ) {
                    case 20720 -> broId = 1673; //Dharock
                    case 20770 -> broId = 1672; //Ahrim
                    case 20772 -> broId = 1677; //Verac
                    case 20721 -> broId = 1676; //Torag
                    case 20771 -> broId = 1675; //Verac
                    case 20722 -> broId = 1674; //Guthan
                }

                if (broId != 0) {
                    var tile = new Tile(3548 + Utils.RANDOM_GEN.nextInt(6), 9691 + Utils.RANDOM_GEN.nextInt(1), player.tile().level);
                    var npc = new NPC(broId, tile);

                    World.getWorld().registerNpc(npc);
                    npc.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));
                    player.putAttrib(AttributeKey.barrowsBroSpawned, npc);
                    player.getPacketSender().sendEntityHint(npc);
                    npc.respawns(false);
                    npc.setPositionToFace(player.tile());
                    npc.forceChat("How dare you disturb my rest!");
                    npc.getCombat().attack(player);
                } else {
                    player.message("Unrecognised brother from coffin");
                }
            } else {
                if (Barrows.barrowsBrotherKc(player) < 6) {
                    player.message("You need to have killed all 6 brothers to loot the chest.");
                } else {
                    player.putAttrib(BARROWS_MONSTER_KC, 0);
                    player.putAttrib(AHRIM, 0);
                    player.putAttrib(DHAROK, 0);
                    player.putAttrib(GUTHAN, 0);
                    player.putAttrib(KARIL, 0);
                    player.putAttrib(TORAG, 0);
                    player.putAttrib(VERAC, 0);
                    player.clearAttrib(AttributeKey.FINAL_BARROWS_BRO_COFFINID);
                    player.message("Congratulations.");
                    player.putAttrib(AttributeKey.BARROWS_CHESTS_OPENED, 1 + player.<Integer>getAttribOr(AttributeKey.BARROWS_CHESTS_OPENED, 0));
                    player.message("Your Barrows chest count is: <col=FF0000>" + player.getAttribOr(AttributeKey.BARROWS_CHESTS_OPENED, 0) + "</col>.");

                    //Generate three items as a reward from the chest..
                    giveloot(player);
                }
            }
            return true;
        }

        //DHAROK, AHRIM, VERAC, TORAG, KARIL,GUTHAN
        for (int coffinId : possibles) {
            if (obj.getId() == coffinId) {
                var targ = player.<Integer>getAttribOr(AttributeKey.FINAL_BARROWS_BRO_COFFINID, 0);
                if (targ == 0) {
                    targ = setChest();
                    player.putAttrib(AttributeKey.FINAL_BARROWS_BRO_COFFINID, targ);
                }

                if (targ == coffinId) {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes, I'm not afraid!", "No way");
                        }

                        @Override
                        protected void next() {
                            if (isPhase(1)) {
                                stop();
                            }
                        }

                        @Override
                        protected void select(int option) {
                            if (option == 1) {
                                if (barrowsBrotherKc(player) < 5) {
                                    send(DialogueType.STATEMENT, "You must have killed 5 brothers to enter the final crypt!");
                                    setPhase(1);
                                } else {
                                    player.teleport(new Tile(3551, 9699));
                                }
                                stop();
                            } else if (option == 2) {
                                stop();
                            }
                        }
                    });
                } else {
                    var tile = new Tile(-1, -1);//Empty tile
                    var broId = 0;
                    var npc2 = player.<NPC>getAttribOr(AttributeKey.barrowsBroSpawned, null);
                    if (npc2 != null) {
                        player.message("This sarcophagus has already been raided!");
                    } else {
                        switch (coffinId) {
                            case 20720 -> {
                                tile = new Tile(3552 + Utils.RANDOM_GEN.nextInt(4), 9716, player.tile().level);
                                broId = DHAROK_THE_WRETCHED;
                                var dharokKc = player.<Integer>getAttribOr(DHAROK, 0);
                                if (dharokKc == 1) {
                                    player.message("You've already raided this coffin.");
                                    return true;
                                }
                            }
                            case 20770 -> {
                                tile = new Tile(3552 + Utils.RANDOM_GEN.nextInt(6), 9701, player.tile().level);
                                broId = AHRIM_THE_BLIGHTED;
                                var ahrimKc = player.<Integer>getAttribOr(AHRIM, 0);
                                if (ahrimKc == 1) {
                                    player.message("You've already raided this coffin.");
                                    return true;
                                }
                            }
                            case 20772 -> {
                                tile = new Tile(3576, 9708 - Utils.RANDOM_GEN.nextInt(4),
                                    player.tile().level);
                                broId = VERAC_THE_DEFILED;
                                var veracKc = player.<Integer>getAttribOr(VERAC, 0);
                                if (veracKc == 1) {
                                    player.message("You've already raided this coffin.");
                                    return true;
                                }
                            }
                            case 20721 -> {
                                System.out.println("euhm");
                                tile = new Tile(3567 + Utils.RANDOM_GEN.nextInt(5), 9684,
                                    player.tile().level);
                                broId = TORAG_THE_CORRUPTED;

                                var toragKc = player.<Integer>getAttribOr(TORAG, 0);
                                if (toragKc == 1) {
                                    player.message("You've already raided this coffin.");
                                    return true;
                                }
                            }
                            case 20771 -> {
                                tile = new Tile(3548, 9681 + Utils.RANDOM_GEN.nextInt(5), player.tile().level);
                                broId = KARIL_THE_TAINTED;
                                var karilKc = player.<Integer>getAttribOr(KARIL, 0);
                                if (karilKc == 1) {
                                    player.message("You've already raided this coffin.");
                                    return true;
                                }
                            }
                            case 20722 -> {
                                tile = new Tile(3536, 9701 + Utils.RANDOM_GEN.nextInt(5), player.tile().level);
                                broId = GUTHAN_THE_INFESTED;

                                var guthanKc = player.<Integer>getAttribOr(GUTHAN, 0);
                                if (guthanKc == 1) {
                                    player.message("You've already raided this coffin.");
                                    return true;
                                }
                            }
                            default -> player.message("unsupported coffin");
                        }
                        var npc = new NPC(broId, tile);
                        World.getWorld().registerNpc(npc);
                        npc.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));
                        player.putAttrib(AttributeKey.barrowsBroSpawned, npc);
                        player.getPacketSender().sendEntityHint(npc);
                        npc.respawns(false);
                        npc.setPositionToFace(player.tile());
                        npc.forceChat("How dare you disturb my rest!");
                        player.message("You don't find anything.");
                        npc.getCombat().attack(player);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static void testloot(Player p) {
        giveloot(p);
    }

    private static void giveloot(Player player) {
        var gotBarrow = false;

        var totalLoot = new ItemContainer(10, ItemContainer.StackPolicy.ALWAYS);

        for (int i = 1; i < 3; i++) {
            var rand = Utils.RANDOM_GEN;
            var idx = rand.nextInt(lootItemIds.size());
            //var multiplier = lootItemAmts.get(idx) > 600 ? 2 : 1;

            var item = new Item(lootItemIds.get(idx), rand.nextInt(lootItemAmts.get(idx))/* * multiplier*/);

            player.inventory().addOrDrop(item);

            totalLoot.add(item, true);

            var chance = 4; // 1/4 for any piece after killing 6 brothers

            var possibleBarrows = barrowsItemIds;

            if (!gotBarrow && rand.nextInt(chance) == 0) { // out of the 3 possible loots, only 1 can be a barrow. there IS however
                // a chance that this barrows loot will give the player ANOTHER
                gotBarrow = true;
                idx = rand.nextInt(possibleBarrows.size());
                item = new Item(possibleBarrows.get(idx), 1);
                player.inventory().addOrDrop(item);
                totalLoot.add(item, true);

                var doubleChance = 50;

                if (rand.nextInt(doubleChance) == 0) {
                    idx = rand.nextInt(possibleBarrows.size());
                    item = new Item(possibleBarrows.get(idx), 1);
                    player.inventory().addOrDrop(item);
                    totalLoot.add(item, true);
                }
            }
        }

        // Display the loot interface
        player.getInterfaceManager().open(InterfaceConstants.BARROWS_REWARD_WIDGET);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.BARROWS_REWARD_CONTAINER, totalLoot.toArray());
        AchievementsManager.activate(player, Achievements.BARROWS_I, 1);
        AchievementsManager.activate(player, Achievements.BARROWS_II, 1);
        AchievementsManager.activate(player, Achievements.BARROWS_III, 1);
        AchievementsManager.activate(player, Achievements.BARROWS_IV, 1);
        AchievementsManager.activate(player, Achievements.BARROWS_V, 1);
    }

    /**
     * Select a random coffin ID to be the enterance to the chest.
     */
    private static int setChest() {
        return possibles.get(Utils.RANDOM_GEN.nextInt(6));
    }

    // Register stairways to heaven.
    private enum Stairways {
        DHAROK(20668, new Tile(3575, 3297, 0)),
        AHRIM(20667, new Tile(3565, 3289, 0)),
        VERAC(20672, new Tile(3557, 3298, 0)),
        GUTHAN(20669, new Tile(3577, 3282, 0)),
        KARIL(20670, new Tile(3566, 3276, 0)),
        TORAG(20671, new Tile(3554, 3283, 0));

        private final int object;
        private final Tile tile;

        Stairways(int object, Tile tile) {
            this.object = object;
            this.tile = tile;
        }

        public static Optional<Stairways> getByObject(int object) {
            return Arrays.stream(values()).filter(stairways -> stairways.object == object).findAny();
        }
    }
}
