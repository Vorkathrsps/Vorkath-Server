package com.cryptic.model.content.achievements;

import com.cryptic.GameServer;
import com.cryptic.model.items.Item;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.ItemIdentifiers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cryptic.utility.CustomItemIdentifiers.*;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin | April, 14, 2021, 16:26
 */
public enum Achievements {

    CRABBY_1(80608, "Crabby I", "Kill 50 Rock Crabs.", 50, Difficulty.EASY, new Item(COINS_995, 150_000), new Item(DOUBLE_XP_LAMP)),
    CRABBY_2(80609, "Crabby II", "Kill 100 Rock Crabs.", 100, Difficulty.EASY, new Item(COINS_995, 250_000), new Item(DOUBLE_XP_LAMP)),
    CRABBY_3(80610, "Crabby III", "Kill 125 Rock Crabs.", 125, Difficulty.EASY, new Item(COINS_995, 500_000), new Item(DOUBLE_DROPS_LAMP)),
    SLAYER_1(80611, "Slayer I", "Complete 5 Slayer Tasks.", 5, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP)),
    SLAYER_2(80612, "Slayer II", "Complete 15 Slayer Tasks.", 15, Difficulty.EASY, new Item(COINS_995, 10_000_000), new Item(DOUBLE_XP_LAMP)),
    SLAYER_3(80613, "Slayer III", "Complete 30 Slayer Tasks.", 30, Difficulty.EASY, new Item(COINS_995, 15_000_000), new Item(DOUBLE_XP_LAMP), new Item(DOUBLE_DROPS_LAMP)),
    SLAYER_4(80614, "Slayer IV", "Complete 50 Slayer Tasks.", 50, Difficulty.EASY, new Item(COINS_995, 30_000_000), new Item(DOUBLE_XP_LAMP, 5), new Item(DOUBLE_DROPS_LAMP, 2), new Item(MYSTERY_BOX)),
    SLAYER_5(80615, "Slayer V", "Complete 75 Slayer Tasks.", 75, Difficulty.EASY, new Item(COINS_995, 50_000_000), new Item(DOUBLE_XP_LAMP, 5), new Item(DOUBLE_DROPS_LAMP, 2), new Item(MYSTERY_BOX)),
    SIGIL_HUNTER(80616, "Sigil Hunter", "Unlock the Attuned Luck slayer perk.", 1, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_XP_LAMP), new Item(DOUBLE_DROPS_LAMP)),
    WHAT_A_BLESSING(80617, "What A Blessing", "Unlock the Ancient Blessing slayer perk.", 1, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_XP_LAMP), new Item(DOUBLE_DROPS_LAMP)),
    GRIM(80618, "Grim", "Unlock the Death's Touch slayer perk.", 1, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_XP_LAMP), new Item(DOUBLE_DROPS_LAMP)),
    GREEDY(80619, "Greedy", "Unlock the Slayer's Greed slayer perk.", 1, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_XP_LAMP), new Item(DOUBLE_DROPS_LAMP)),
    WHATS_INSIDE_I(80620, "What's Inside I", "Open the Crystal chest 10 times.", 10, Difficulty.EASY, new Item(CRYSTAL_KEY, 5)),
    WHATS_INSIDE_II(80621, "What's Inside II", "Open the Crystal chest 50 times.", 50, Difficulty.MED, new Item(CRYSTAL_KEY, 10)),
    WHATS_INSIDE_III(80622, "What's Inside III", "Open the Crystal chest 100 times.", 100, Difficulty.HARD, new Item(CRYSTAL_KEY, 15)),
    LARRAN_FRENZY_I(80623, "Larran's Frenzy I", "Open the Larran's chest 10 times.", 10, Difficulty.EASY, new Item(LARRANS_KEY, 5)),
    LARRAN_FRENZY_II(80624, "Larran's Frenzy II", "Open the Larran's chest 50 times.", 50, Difficulty.MED, new Item(LARRANS_KEY, 10)),
    LARRAN_FRENZY_III(80625, "Larran's Frenzy III", "Open the Larran's chest 100 times.", 100, Difficulty.HARD, new Item(LARRANS_KEY, 15), new Item(MYSTERY_BOX)),
    DRAGON_SLAYER_I(80626, "Dragon slayer I", "Kill 50 Green Dragons.", 50, Difficulty.EASY, new Item(DRAGON_BONES + 1, 250), new Item(COINS_995, 2_500_000)),
    DRAGON_SLAYER_II(80627, "Dragon slayer II", "Kill 50 black dragons.", 50, Difficulty.MED, new Item(COINS_995, 2_500_000), new Item(DOUBLE_XP_LAMP)),
    DRAGON_SLAYER_III(80628, "Dragon slayer III", "Kill 100 King black dragons.", 100, Difficulty.HARD, new Item(DRAGONFIRE_SHIELD)),
    SKILLER_I(80629, "Skiller I", "Earn a total level of 750.", 1, Difficulty.EASY, new Item(DOUBLE_XP_LAMP, 2), new Item(MYSTERY_BOX)),
    SKILLER_II(80630, "Skiller II", "Earn a total level of 1000.", 1, Difficulty.MED, new Item(DOUBLE_XP_LAMP, 4), new Item(MYSTERY_BOX)),
    SKILLER_III(80631, "Skiller III", "Earn a total level of 1500.", 1, Difficulty.HARD, new Item(DOUBLE_XP_LAMP, 6), new Item(MYSTERY_BOX, 2)),
    SKILLER_IV(80632, "Skiller IV", "Earn level 99 in all skills, with the exception of construction.", 1, Difficulty.HARD, new Item(DOUBLE_XP_LAMP, 5), new Item(MYSTERY_BOX, 10)),
    DAMAGE_DEALER_I(80633, "Damage Dealer I", "Deal 5,000 damage.", 5000, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX)),
    DAMAGE_DEALER_II(80634, "Damage Dealer II", "Deal 10,000 damage.", 10000, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX)),
    DAMAGE_DEALER_III(80635, "Damage Dealer III", "Deal 15,000 damage.", 15000, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(MYSTERY_BOX)),
    DAMAGE_DEALER_IV(80636, "Damage Dealer IV", "Deal 20,000 damage.", 20000, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 2)),
    BARROWS_I(80637, "Barrows I", "Kill 15 barrows brothers.", 15, Difficulty.EASY, new Item(TORAGS_ARMOUR_SET), new Item(COINS_995, 5_000_000)),
    BARROWS_II(80638, "Barrows II", "Kill 50 barrows brothers.", 50, Difficulty.MED, new Item(GUTHANS_ARMOUR_SET), new Item(COINS_995, 5_000_000)),
    BARROWS_III(80639, "Barrows III", "Kill 75 barrows brothers.", 75, Difficulty.HARD, new Item(AHRIMS_ARMOUR_SET), new Item(COINS_995, 5_000_000)),
    BARROWS_IV(80640, "Barrows IV", "Kill 100 barrows brothers.", 100, Difficulty.HARD, new Item(KARILS_ARMOUR_SET), new Item(COINS_995, 5_000_000)),
    BARROWS_V(80641, "Barrows V", "Kill 125 barrows brothers.", 125, Difficulty.HARD, new Item(DHAROKS_ARMOUR_SET), new Item(COINS_995, 5_000_000)),
    THIEF_I(80642, "Thief I", "Steal from the stall at the home thieving area 150 times.", 150, Difficulty.EASY, new Item(COINS_995, 2_500_000), new Item(DOUBLE_XP_LAMP, 2)),
    THIEF_II(80643, "Thief II", "Steal from the stall at the home thieving area 350 times.", 350, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    THIEF_III(80644, "Thief III", "Steal from the stall at the home thieving area 500 times.", 500, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    THIEF_IV(80645, "Thief IV", "Steal from the stall at the home thieving area 750 times.", 750, Difficulty.MED, new Item(COINS_995, 15_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    MINING_I(80646, "Mining I", "Mine 50 copper ore.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    MINING_II(80647, "Mining II", "Mine 100 coal.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    MINING_III(80648, "Mining III", "Mine 250 adamant ore.", 250, Difficulty.MED, new Item(COINS_995, 15_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    MINING_IV(80649, "Mining IV", "Mine 500 runite ore.", 500, Difficulty.HARD, new Item(COINS_995, 20_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    SMELTING_I(80650, "Smelting I", "Smith 50 bronze platebody's.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    SMELTING_II(80651, "Smelting II", "Smith 100 mithril platebody's.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    SMELTING_III(80652, "Smelting III", "Smith 250 adamant platebody's.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    SMELTING_IV(80653, "Smelting IV", "Smith 500 runite platebody's.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FISHING_I(80654, "Fishing I", "Fish 50 shrimp.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FISHING_II(80655, "Fishing II", "Fish 100 swordfish.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FISHING_III(80656, "Fishing III", "Fish 250 shark.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FISHING_IV(80657, "Fishing IV", "Fish 500 anglerfish.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FIREMAKING_I(80658, "Firemaking I", "Light 100 fires.", 100, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FIREMAKING_II(80659, "Firemaking II", "Light 150 fires.", 150, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FIREMAKING_III(80660, "Firemaking III", "Light 250 fires.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    FIREMAKING_IV(80661, "Firemaking IV", "Light 500 fires.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    COOKING_I(80662, "Cooking I", "Cook 50 shrimp.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    COOKING_II(80663, "Cooking II", "Cook 100 lobster.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    COOKING_III(80664, "Cooking III", "Cook 250 shark.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    COOKING_IV(80665, "Cooking IV", "Cook 500 anglerfish.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    WOODCUTTING_I(80666, "Woodcutting I", "Cut 50 tree's.", 50, Difficulty.EASY, new Item(COINS_995, 850_000), new Item(DOUBLE_XP_LAMP, 2)),
    WOODCUTTING_II(80667, "Woodcutting II", "Cut 100 willow tree's.", 100, Difficulty.MED, new Item(COINS_995, 1_250_000), new Item(DOUBLE_XP_LAMP, 2)),
    WOODCUTTING_III(80668, "Woodcutting III", "Cut 250 yew tree's.", 250, Difficulty.MED, new Item(COINS_995, 2_500_000), new Item(DOUBLE_XP_LAMP, 2)),
    WOODCUTTING_IV(80669, "Woodcutting IV", "Cut 500 magic tree's.", 500, Difficulty.HARD, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP, 2)),
    CRAFTING_I(80670, "Crafting I", "Cut 50 sapphire gems.", 50, Difficulty.EASY, new Item(COINS_995, 850_000), new Item(DOUBLE_XP_LAMP)),
    CRAFTING_II(80671, "Crafting II", "Cut 100 emerald gems.", 100, Difficulty.MED, new Item(COINS_995, 1_250_000), new Item(DOUBLE_XP_LAMP)),
    CRAFTING_III(80672, "Crafting III", "Cut 250 ruby gems.", 250, Difficulty.MED, new Item(COINS_995, 2_500_000), new Item(DOUBLE_XP_LAMP)),
    CRAFTING_IV(80673, "Crafting IV", "Cut 500 diamond gems.", 500, Difficulty.HARD, new Item(COINS_995, 5_000_000), new Item(DOUBLE_XP_LAMP)),
    REVENANT_HUNTER_I(80674, "Revenant Hunter I", "Kill 250 revenants.", 250, Difficulty.EASY, new Item(ANCIENT_EMBLEM, 1), new Item(DOUBLE_DROPS_LAMP)),
    REVENANT_HUNTER_II(80675, "Revenant Hunter II", "Kill 500 revenants.", 500, Difficulty.MED, new Item(ANCIENT_STATUETTE, 1), new Item(DOUBLE_DROPS_LAMP)),
    REVENANT_HUNTER_III(80676, "Revenant Hunter III", "Kill 750 revenants.", 750, Difficulty.HARD, new Item(ANCIENT_MEDALLION, 1), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP, 2)),
    REVENANT_HUNTER_IV(80677, "Revenant Hunter IV", "Kill 1000 revenants.", 1000, Difficulty.HARD, new Item(ANCIENT_RELIC, 1), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP, 2)),
    VORKY_I(80678, "Vorkath I", "Kill 50 Vorkaths.", 50, Difficulty.HARD, new Item(COINS_995, 5_000_000), new Item(VORKATHS_HEAD_21907), new Item(BLUE_DRAGONHIDE + 1, 50), new Item(SUPERIOR_DRAGON_BONES + 1, 50)),
    VORKY_II(80679, "Vorkath II", "Kill 100 Vorkaths.", 100, Difficulty.HARD, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX), new Item(BLUE_DRAGONHIDE + 1, 100), new Item(SUPERIOR_DRAGON_BONES + 1, 100)),
    SNAKE_CHARMER_I(80680, "Swampletics I", "Kill 5 Zulrah.", 5, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(DOUBLE_DROPS_LAMP)),
    SNAKE_CHARMER_II(80681, "Swampletics II", "Kill 50 Zulrah.", 50, Difficulty.MED, new Item(COINS_995, 5_000_000), new Item(UNCUT_ONYX), new Item(DOUBLE_DROPS_LAMP)),
    SNAKE_CHARMER_III(80682, "Swampletics III", "Kill 100 Zulrah.", 100, Difficulty.HARD, new Item(COINS_995, 10_000_000), new Item(SERPENTINE_HELM), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP)),
    NEX_I(80683, "Frost Bite I", "Kill 5 Nex.", 5, Difficulty.HARD, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX)),
    NEX_II(80684, "Frost Bite II", "Kill 15 Nex.", 15, Difficulty.HARD, new Item(COINS_995, 15_000_000), new Item(MYSTERY_BOX)),
    NEX_III(80685, "Frost Bite III", "Kill 25 Nex.", 25, Difficulty.HARD, new Item(COINS_995, 20_000_000), new Item(MYSTERY_BOX)),
    GODWARS_I(80686, "Godwars I", "Kill 25 Godwars Bosses.", 25, Difficulty.HARD, new Item(COINS_995, 5_000_000), new Item(DOUBLE_DROPS_LAMP)),
    GODWARS_II(80687, "Godwars II", "Kill 50 Godwars Bosses.", 50, Difficulty.HARD, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX)),
    GODWARS_III(80688, "Godwars III", "Kill 100 Godwars Bosses.", 100, Difficulty.HARD, new Item(COINS_995, 20_000_000), new Item(MYSTERY_BOX)),
    SCURRIUS_I(80689, "Rat Poison I", "Kill 25 Scurrius.", 25, Difficulty.HARD, new Item(COINS_995, 2_000_000), new Item(DOUBLE_XP_LAMP), new Item(DOUBLE_DROPS_LAMP)),
    SCURRIUS_II(80690, "Rat Poison II", "Kill 50 Scurrius.", 50, Difficulty.HARD, new Item(COINS_995, 4_000_000), new Item(DOUBLE_XP_LAMP), new Item(DOUBLE_DROPS_LAMP)),
    SCURRIUS_III(80691, "Rat Poison III", "Kill 75 Scurrius.", 75, Difficulty.HARD, new Item(COINS_995, 6_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP)),
    TOB_I(80692, "Theatre I", "Complete 15 Theatre Of Blood raids.", 15, Difficulty.HARD, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX, 2), new Item(DOUBLE_DROPS_LAMP, 5)),
    TOB_II(80693, "Theatre II", "Complete 50 Theatre Of Blood raids.", 50, Difficulty.HARD, new Item(COINS_995, 25_000_000), new Item(MYSTERY_BOX, 2), new Item(DOUBLE_DROPS_LAMP, 5)),
    TOB_III(80694, "Theatre III", "Complete 100 Theatre Of Blood raids.", 100, Difficulty.HARD, new Item(COINS_995, 50_000_000), new Item(MYSTERY_BOX, 5), new Item(DOUBLE_DROPS_LAMP, 5)),
    COX_I(80695, "Chambers I", "Complete 15 Chambers of Xeric raids.", 15, Difficulty.HARD, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP, 2)),
    COX_II(80696, "Chambers II", "Complete 50 Chambers of Xeric raids.", 50, Difficulty.HARD, new Item(COINS_995, 25_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP, 2)),
    COX_III(80697, "Chambers III", "Complete 100 Chambers of Xeric raids.", 100, Difficulty.HARD, new Item(COINS_995, 50_000_000), new Item(MYSTERY_BOX, 2), new Item(DOUBLE_DROPS_LAMP, 2)),
    RUN_THE_WILD(80698, "Run The Wild", "Kill any Wilderness boss 100 times.", 100, Difficulty.HARD, new Item(COINS_995, 15_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP), new Item(DOUBLE_XP_LAMP)),
    HYDRATE(80699, "Hydrate", "Kill Alchemical Hydra 100 times.", 100, Difficulty.HARD, new Item(COINS_995, 15_000_000), new Item(MYSTERY_BOX), new Item(DOUBLE_DROPS_LAMP, 2), new Item(DOUBLE_XP_LAMP, 2)),
    WHATS_KRAKEN(80700, "What's Kraken?", "Kill The Kraken 150 times.", 100, Difficulty.HARD, new Item(COINS_995, 15_000_000), new Item(MYSTERY_BOX), new Item(TRIDENT_OF_THE_SEAS_FULL, 1), new Item(DOUBLE_DROPS_LAMP, 2));//COMPLETIONIST(80697, "Chambers III", "Complete 100 Chambers of Xeric raids.", 100, Difficulty.HARD, new Item(COINS_995, 50_000_000), new Item(MYSTERY_BOX, 2), new Item(DOUBLE_DROPS_LAMP, 2));

    public static List<Achievements> asList(Difficulty difficulty) {
        return Arrays.stream(VALUES).filter(Objects::nonNull).filter(a -> a.difficulty == difficulty).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
    }

    public final int child;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final int completeAmount;
    private final String rewardString;
    private final Difficulty difficulty;
    @Getter
    private final Item[] reward;
    public static final Achievements[] VALUES = values();

    Achievements(int child, String name, String description, int completeAmount, Difficulty difficulty, Item... reward) {
        this.child = child;
        this.name = name;
        this.description = description;
        this.completeAmount = completeAmount;
        this.rewardString = "";
        this.difficulty = difficulty;
        this.reward = reward;
    }

    public String otherRewardString() {
        return rewardString;
    }

    public static int getTotal() {
        return VALUES.length;
    }
}
