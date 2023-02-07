package com.aelous.model.content.teleport.tabs;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * The teleportation data for teletabs and teleportation scrolls.
 *
 * @author Patrick van Elderen | Zerikoth (PVE) | 23 sep. 2019 : 13:42
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 * @version 1.0
 *
 */
public enum TabletData {

    VARROCK(new Item(VARROCK_TELEPORT), new Tile(3210, 3424, 0), false),
    LUMBRIDGE(new Item(LUMBRIDGE_TELEPORT), new Tile(3222, 3218, 0), false),
    FALADOR(new Item(FALADOR_TELEPORT), new Tile(2964, 3378, 0), false),
    CAMELOT(new Item(CAMELOT_TELEPORT), new Tile(2757, 3477, 0), false),
    ARDOUGNE(new Item(ARDOUGNE_TELEPORT), new Tile(2662, 3305, 0), false),
    WATCH_TOWER(new Item(WATCHTOWER_TELEPORT), new Tile(2549, 3112, 0), false),
    HOUSE(new Item(TELEPORT_TO_HOUSE), GameServer.properties().defaultTile, false),
    RIMMINGTON(new Item(RIMMINGTON_TELEPORT), new Tile(2956, 3223, 0), false),
    TAVERLEY(new Item(TAVERLEY_TELEPORT), new Tile(2896, 3456, 0), false),
    POLLNIVNEACH(new Item(POLLNIVNEACH_TELEPORT), new Tile(3356, 2966, 0), false),
    RELLEKA(new Item(RELLEKKA_TELEPORT), new Tile(2669, 3636, 0), false),
    BRIMHAVEN(new Item(BRIMHAVEN_TELEPORT), new Tile(2760, 3178, 0), false),
    YANILLE(new Item(YANILLE_TELEPORT), new Tile(2544, 3092, 0), false),
    TROLLHEIM(new Item(TROLLHEIM_TELEPORT), new Tile(2890, 3676, 0), false),
    ANNAKARL(new Item(ANNAKARL_TELEPORT), new Tile(3288, 3886, 0), false),
    CARRALLANGAR(new Item(CARRALLANGAR_TELEPORT), new Tile(3156, 3666, 0), false),
    DAREEYAK(new Item(DAREEYAK_TELEPORT), new Tile(2966, 3695, 0), false),
    GHORROCK(new Item(GHORROCK_TELEPORT), new Tile(2977, 3873, 0), false),
    KHARYRLL(new Item(KHARYRLL_TELEPORT), new Tile(3492, 3471, 0), false),
    LASSAR(new Item(LASSAR_TELEPORT), new Tile(3006, 3471, 0), false),
    PADDEWWA(new Item(PADDEWWA_TELEPORT), new Tile(3098, 9884, 0), false),
    SENNTISTEN(new Item(SENNTISTEN_TELEPORT), new Tile(3322, 3336, 0), false),
    NARDAH(new Item(NARDAH_TELEPORT), new Tile(3423, 2914, 0), true),
    DIGSITE(new Item(DIGSITE_TELEPORT), new Tile(3350, 3344, 0), true),
    FELDIP_HILLS(new Item(FELDIP_HILLS_TELEPORT), new Tile(2556, 2982, 0), true),
    LUNAR_ISLE(new Item(LUNAR_ISLE_TELEPORT), new Tile(2108, 3914, 0), true),
    MORTTON(new Item(MORTTON_TELEPORT), new Tile(3487, 3284, 0), true),
    PEST_CONTROL(new Item(PEST_CONTROL_TELEPORT), new Tile(2662, 2647, 0), true),
    PISCATORIS(new Item(PISCATORIS_TELEPORT), new Tile(2342, 3692, 0), true),
    TAI_BWO_WANNAI(new Item(TAI_BWO_WANNAI_TELEPORT), new Tile(2790, 3065, 0), true),
    IORWERTH_CAMP(new Item(IORWERTH_CAMP_TELEPORT), new Tile(2195, 3253, 0), true),
    MOSLE_HARMLESS(new Item(MOS_LEHARMLESS_TELEPORT), new Tile(3677, 2976, 0), true),
    LUMBERYARD(new Item(LUMBERYARD_TELEPORT), new Tile(3306, 3483, 0), true),
    ZUL_ANDRA(new Item(ZULANDRA_TELEPORT), new Tile(2200, 3055, 0), true),
    KEY_MASTER(new Item(KEY_MASTER_TELEPORT), new Tile(1312, 1250), true),
    DRAYNOR_MANOR(new Item(DRAYNOR_MANOR_TELEPORT), new Tile(3111, 3326, 0), false),
    MIND_ALTAR(new Item(MIND_ALTAR_TELEPORT), new Tile(2979, 3509), false),
    SALVE_GRAVEYARD(new Item(SALVE_GRAVEYARD_TELEPORT), new Tile(3431, 3461), false),
    FENKENSTRAINS_CASTLE(new Item(FENKENSTRAINS_CASTLE_TELEPORT), new Tile(3549, 3528, 0), false),
    WEST_ARDOUGNE(new Item(WEST_ARDOUGNE_TELEPORT), new Tile(2524, 3306, 0), false),
    HARMONY_ISLAND(new Item(HARMONY_ISLAND_TELEPORT), new Tile(World.getWorld().EDGEHOME.getX(), World.getWorld().EDGEHOME.getY(), World.getWorld().HOME.getZ()), false),
    CEMETERY(new Item(CEMETERY_TELEPORT), new Tile(2976, 3750, 0), false),
    BARROWS(new Item(BARROWS_TELEPORT), new Tile(3565, 3306, 0), false),
    APE_ATOLL(new Item(APE_ATOLL_TELEPORT), new Tile(2778, 2786, 0), false),
    HOSIDIUS(new Item(HOSIDIUS_TELEPORT), new Tile(1645, 3667, 0), false),
    REVENANTS(new Item(REVENANT_CAVE_TELEPORT), new Tile(3076, 3651, 0), true),
    VORKATH(new Item(769), new Tile(2273, 4049, 0), true);

    /**
     * The teletab id
     */
    private final Item tablet;

    /**
     * The position to teleport to
     */
    private final Tile tile;

    /**
     * Is the item a scroll or tablet
     */
    private final boolean scroll;

    /**
     * The TabletData constructor
     *
     * @param item
     *            The teletab or scroll
     * @param tile
     *            The tile to teleport to
     * @param scroll
     *            Are we using an tablet or scroll
     */
    TabletData(final Item item, final Tile tile, final boolean scroll) {
        this.tablet = item;
        this.tile = tile;
        this.scroll = scroll;
    }

    public Item getTablet() {
        return tablet;
    }

    public Tile getTile() {
        return tile;
    }

    public boolean isScroll() {
        return scroll;
    }

    private static final Set<TabletData> tab_set = Collections.unmodifiableSet(EnumSet.allOf(TabletData.class));

    public static Optional<TabletData> getTab(int tabId) {
        return tab_set.stream().filter(tabs -> tabs.getTablet().getId() == tabId).findFirst();
    }

}
