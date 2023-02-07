package com.aelous.model.content.teleport;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.ItemIdentifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Patrick van Elderen | February, 20, 2021, 22:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class OrnateJewelleryBox {

    private enum JewelleryBoxData {

        DUEL_ARENA(65016, new Tile(3369, 3266)),
        CASTLE_WARS(65017, new Tile(2438, 3088)),
        CLAN_WARS(65018, new Tile(3385, 3152)),
        WARRIORS_GUILD(65019, new Tile(2884, 3550)),
        CHAMPIONS_GUILD(65020, new Tile(3193, 3368)),
        MONASTERY(65021, new Tile(3054, 3490)),
        RANGING_GUILD(65022, new Tile(2656, 3441)),
        MISCELLANIA(65023, new Tile(2538, 3860)),
        GRAND_EXCHANGE(65024, new Tile(3165, 3478)),
        FALADOR_PARK(65025, new Tile(2995, 3377)),
        DONDAKANS_ROCK(65026, new Tile(2829, 10169)),
        BURTHOPE(65027, new Tile(2898, 3552)),
        BARBARIAN_OUTPOST(65028, new Tile(2519, 3569)),
        CORPOREAL_BEAST(65029, new Tile(2966, 4382)),
        TEARS_OF_GUTHIX(65030, new Tile(3245, 9500)),
        WINTERTODT_CAMP(65031, new Tile(1623, 3937)),
        FISHING_GUILD(65032, new Tile(2610, 3391)),
        MINING_GUILD(65033, new Tile(3050, 9762)),
        CRAFTING_GUILD(65034, new Tile(2933, 3295)),
        COOKING_GUILD(65035, new Tile(3143, 3440)),
        WOODCUTTING_GUILD(65036, new Tile(1663, 3506)),
        //FARMING 1251, 3716
        EDGEVILE(65037, new Tile(3087, 3496)),
        KARAMJA(65038, new Tile(2918, 3176)),
        DRAYNOR_VILLAGE(65039, new Tile(3105, 3251)),
        AL_KHARID(65040, new Tile(3293, 3168));

        private int button;
        private Tile tile;

        JewelleryBoxData(int button, Tile tile) {
            this.button = button;
            this.tile = tile;
        }

        private static final Map<Integer, JewelleryBoxData> teleports = new HashMap<>();

        static {
            for (JewelleryBoxData teleport : JewelleryBoxData.values()) {
                teleports.put(teleport.getButton(), teleport);
            }
        }

        public static Optional<JewelleryBoxData> getAsButton(int button) {
            JewelleryBoxData teleport = teleports.get(button);
            if (teleport != null) {
                return Optional.of(teleport);
            }
            return Optional.empty();
        }

        public int getButton() {
            return button;
        }

        public Tile getTile() {
            return tile;
        }
    }

    public static void open(Player player) {
        player.getInterfaceManager().open(65000);
        player.getPacketSender().sendItemOnInterfaceSlot(65004, ItemIdentifiers.RING_OF_DUELING1, 1, 0);
        player.getPacketSender().sendItemOnInterfaceSlot(65005, ItemIdentifiers.COMBAT_BRACELET1, 1, 0);
        player.getPacketSender().sendItemOnInterfaceSlot(65006, ItemIdentifiers.RING_OF_WEALTH_2, 1, 0);
        player.getPacketSender().sendItemOnInterfaceSlot(65007, ItemIdentifiers.GAMES_NECKLACE1, 1, 0);
        player.getPacketSender().sendItemOnInterfaceSlot(65008, ItemIdentifiers.SKILLS_NECKLACE1, 1, 0);
        player.getPacketSender().sendItemOnInterfaceSlot(65009, ItemIdentifiers.AMULET_OF_GLORY1, 1, 0);
    }

    public static boolean teleport(Player player, int button) {
        Optional<JewelleryBoxData> teleport = JewelleryBoxData.getAsButton(button);

        if(teleport.isPresent()) {
            Teleports.basicTeleport(player, teleport.get().getTile());
            return true;
        }
        return false;
    }
}
