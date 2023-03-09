package com.aelous.model.content.skill.impl.runecrafting;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.pets.PetDefinitions;
import com.aelous.model.inter.dialogue.DialogueManager;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Created by Carl on 2015-08-26.
 * Edited & finished by Situations on 2015-09-12
 */
public class RuneConversion extends PacketInteraction {

    public enum Altar {

        AIR(1, 5.0, AIR_TALISMAN, AIR_RUNE, 34760, 34813, new Tile(2841, 4830), 34748, new Tile(2983, 3293), false, 11, 2800, PetDefinitions.RIFT_GUARDIAN_AIR),
        MIND(2, 5.5, MIND_TALISMAN, MIND_RUNE, 34761, 34814, new Tile(2792, 4827), 34749, new Tile(2984, 3512), false, 14, 2700, PetDefinitions.RIFT_GUARDIAN_MIND),
        WATER(5, 6.0, WATER_TALISMAN, WATER_RUNE, 34762, 34815, new Tile(2726, 4832), 34750, new Tile(3183, 3167), false, 19, 2600, PetDefinitions.RIFT_GUARDIAN_WATER),
        EARTH(9, 6.5, EARTH_TALISMAN, EARTH_RUNE, 34763, 34816, new Tile(2655, 4830), 34751, new Tile(3305, 3472), false, 29, 2500, PetDefinitions.RIFT_GUARDIAN_EARTH),
        FIRE(14, 7.0, FIRE_TALISMAN, FIRE_RUNE, 34764, 34817, new Tile(2574, 4849), -1, new Tile(3312, 3253), false, 35, 2400, PetDefinitions.RIFT_GUARDIAN_FIRE),
        BODY(20, 7.5, BODY_TALISMAN, BODY_RUNE, 34765, 34818, new Tile(2521, 4834), 34753, new Tile(3054, 3443), false, 46, 2300, PetDefinitions.RIFT_GUARDIAN_BODY),
        COSMIC(27, 8.0, COSMIC_TALISMAN, COSMIC_RUNE, 34766, 34819, new Tile(2162, 4833), 34754, new Tile(2410, 4377), true, 59, 2200, PetDefinitions.RIFT_GUARDIAN_COSMIC),
        LAW(54, 9.5, LAW_TALISMAN, LAW_RUNE, 34767, 34820, new Tile(2464, 4818), 34755, new Tile(2860, 3381), true, 200, 2100, PetDefinitions.RIFT_GUARDIAN_LAW),
        NATURE(44, 9.0, NATURE_TALISMAN, NATURE_RUNE, 34768, 34821, new Tile(2400, 4835), 34754, new Tile(2868, 3017), true, 91, 2000, PetDefinitions.RIFT_GUARDIAN_NATURE),
        CHAOS(35, 8.5, CHAOS_TALISMAN, CHAOS_RUNE, 34769, 34822, new Tile(2281, 4837), 34757, new Tile(3062, 3590), true, 74, 1900, PetDefinitions.RIFT_GUARDIAN_CHAOS),
        DEATH(65, 10.0, DEATH_TALISMAN, DEATH_RUNE, 34770, 34823, new Tile(2208, 4830), 34758, new Tile(1862, 4639), true, 200, 1800, PetDefinitions.RIFT_GUARDIAN_DEATH),
        ASTRAL(40, 8.7, -1, ASTRAL_RUNE, 34771, -1, new Tile(2156, 3863), -1, new Tile(2156, 3863), true, 42, 1700, PetDefinitions.RIFT_GUARDIAN_ASTRAL),
        BLOOD(77, 23.8, -1, BLOOD_RUNE, 27978, -1, null, -1, null, true, 42, 7990, PetDefinitions.RIFT_GUARDIAN_BLOOD);

        private final int levelReq;
        private final double xp;
        private final int talisman;
        private final int rune;
        private final int altarObj;
        private final int entranceObj;
        private final Tile entranceTile;
        private final int exitObject;
        private final Tile exitTile;
        private final boolean pure;
        private final int multiplier;
        public int petOdds;
        public PetDefinitions petDefinitionsTransform;

        Altar(int levelReq, double xp, int talisman, int rune, int altarObj, int entranceObj, Tile entranceTile, int exitObject, Tile exitTile, boolean pure, int multiplier, int petOdds, PetDefinitions petDefinitionsTransform) {
            this.levelReq = levelReq;
            this.xp = xp;
            this.talisman = talisman;
            this.rune = rune;
            this.altarObj = altarObj;
            this.entranceObj = entranceObj;
            this.entranceTile = entranceTile;
            this.exitObject = exitObject;
            this.exitTile = exitTile;
            this.pure = pure;
            this.multiplier = multiplier;
            this.petOdds = petOdds;
            this.petDefinitionsTransform = petDefinitionsTransform;
        }

        public static Altar get(int talisman) {
            for (Altar altar : Altar.values()) {
                if (talisman == altar.talisman) {
                    return altar;
                }
            }
            return null;
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            for (Altar altar : Altar.values()) {
                if (altar.altarObj == object.getId()) {
                    craft(player, altar);
                    return true;
                }

                if (altar.exitObject == object.getId()) {
                    player.lock();
                    player.message("You step through the portal...");
                    player.teleport(altar.exitTile);
                    Chain.bound(player).runFn(1, player::unlock);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        Altar talisman = Altar.get(item.getId());

        for (Altar altar : Altar.values()) {
            if (object.getId() == altar.entranceObj) {
                if (talisman != null && altar.talisman == item.getId()) {
                    player.lock();
                    Chain.bound(player).runFn(1, () -> {
                        player.animate(827);
                        player.message("You hold the " + altar.name().toLowerCase() + " talisman towards the mysterous ruins.");
                    }).then(2, () -> {
                        player.message("You feel a powerful force take hold of you...");
                        player.lock();
                    }).then(1, () -> {
                        player.teleport(altar.entranceTile);
                        player.unlock();
                    });
                } else {
                    String aAn = (altar == Altar.EARTH || altar == Altar.ASTRAL || altar == Altar.AIR) ? "an" : "a";
                    player.message("You need " + aAn + " " + altar.name().toLowerCase() + " talisman to access the " + altar.name().toLowerCase() + " altar.");
                }
                return true;
            }
        }
        return false;
    }

    private static void craft(Player player, Altar altar) {
        if (player.getSkills().xpLevel(Skills.RUNECRAFTING) >= altar.levelReq) {
            int amount = player.inventory().count(PURE_ESSENCE);
            if (!altar.pure)
                amount += player.inventory().count(RUNE_ESSENCE);

            String msg = "pure";
            if (!altar.pure)
                msg = "rune";

            if (amount >= 1) {
                player.getInterfaceManager().close();
                player.lock();
                player.animate(791);
                player.graphic(186);
                int finalAmount = amount;
                Chain.bound(player).runFn(4, () -> {
                    if (altar.pure)
                        player.inventory().remove(new Item(PURE_ESSENCE, finalAmount), true);
                    else {
                        player.inventory().remove(new Item(PURE_ESSENCE, player.inventory().count(PURE_ESSENCE)), true);
                        player.inventory().remove(new Item(RUNE_ESSENCE, player.inventory().count(RUNE_ESSENCE)), true);
                    }
                    int multi = 1;
                    for (int i = altar.multiplier; i < altar.multiplier * 10; i += altar.multiplier) {
                        if (player.getSkills().xpLevel(Skills.RUNECRAFTING) >= i)
                            multi++;
                    }

                    if (altar == Altar.DEATH) {
                        player.getTaskMasterManager().increase(Tasks.CRAFT_DEATH_RUNES, finalAmount);
                    }

                    player.inventory().add(new Item(altar.rune, finalAmount * multi), true);
                    player.getSkills().addXp(Skills.RUNECRAFTING, altar.xp * finalAmount);
                    player.putAttrib(AttributeKey.RUNECRAFTING, false);


                    player.unlock();
                });
            } else {
                player.putAttrib(AttributeKey.RUNECRAFTING, false);
                player.getInterfaceManager().close();
                DialogueManager.sendStatement(player, "You do not have any " + msg + " essence to bind.");
            }
        } else {
            player.putAttrib(AttributeKey.RUNECRAFTING, false);
            player.getInterfaceManager().close();
            player.message("You need a Runecrafting level of " + altar.levelReq + " to infuse these runes.");
        }
    }

    public static Action<Player> action(Player player, Altar altar, int amount) {
        return new Action<>(player,1,true) {
            int ticks = 0;

            @Override
            public void execute() {
                craft(player, altar);
                if (++ticks == amount) {
                    stop();
                }
            }

            @Override
            public String getName() {
                return "";
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
