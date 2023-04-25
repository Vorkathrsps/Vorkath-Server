package com.aelous.model.content.achievements;

import com.aelous.GameServer;
import com.aelous.model.items.Item;
import com.aelous.utility.CustomItemIdentifiers;
import com.aelous.utility.ItemIdentifiers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | April, 14, 2021, 16:26
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public enum Achievements {

    COMPLETIONIST("Completionist", "Complete all Achievements besides this one.", 1, Difficulty.HARD, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 100_000 : 100_000_000), new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX,5)),
    WHATS_IN_THE_BOX_I("Mystery box I", "Open the mystery box 10 times.", 10, Difficulty.EASY, AchievementUtility.DEFAULT_REWARD, new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX)),
    WHATS_IN_THE_BOX_II("Mystery box II", "Open the mystery box 50 times.", 50, Difficulty.MED, new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX,2)),
    WHATS_IN_THE_BOX_III("Mystery box III", "Open the mystery box 100 times.", 100, Difficulty.HARD, new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX,5)),
    CRYSTAL_LOOTER_I("Crystal looter I", "Open the crystal chest 10 times.", 10, Difficulty.EASY, new Item(CRYSTAL_KEY,2)),
    CRYSTAL_LOOTER_II("Crystal looter II", "Open the crystal chest 50 times.", 50, Difficulty.MED, new Item(CRYSTAL_KEY,5)),
    CRYSTAL_LOOTER_III("Crystal looter III", "Open the crystal chest 100 times.", 100, Difficulty.HARD, new Item(CRYSTAL_KEY,10)),
    LARRANS_LOOTER_I("Larran's looter I", "Open the Larran's chest 10 times.", 10, Difficulty.EASY, new Item(LARRANS_KEY,2)),
    LARRANS_LOOTER_II("Larran's looter II", "Open the Larran's chest 50 times.", 50, Difficulty.MED, new Item(LARRANS_KEY,5)),
    LARRANS_LOOTER_III("Larran's looter III", "Open the Larran's chest 100 times.", 100, Difficulty.HARD, new Item(LARRANS_KEY,10)),

    //Pvm
    YAK_HUNTER("Yak hunter", "Kill 50 Yaks.", 50, Difficulty.EASY, new Item(DIVINE_SUPER_COMBAT_POTION4+1,25)),
    ROCK_CRAB_HUNTER("Rock crab hunter", "Kill 50 Rock crabs.", 50, Difficulty.EASY, new Item(DIVINE_SUPER_COMBAT_POTION4+1,25)),
    SAND_CRAB_HUNTER("Sand crab hunter", "Kill 50 Sand crabs.", 50, Difficulty.EASY, new Item(DIVINE_SUPER_COMBAT_POTION4+1,25)),
    EXPERIMENTS_HUNTER("Experiments hunter", "Kill 50 Experiments.", 50, Difficulty.EASY, new Item(DIVINE_SUPER_COMBAT_POTION4+1,25)),
    DRAGON_SLAYER_I("Dragon slayer I", "Kill 250 dragons.", 250, Difficulty.EASY, new Item(DRAGON_BONES+1,100)),
    DRAGON_SLAYER_II("Dragon slayer II", "Kill 50 black dragons.", 50, Difficulty.MED, new Item(KBD_HEADS)),
    DRAGON_SLAYER_III("Dragon slayer III", "Kill 100 King black dragons.", 100, Difficulty.HARD, new Item(ANCIENT_WYVERN_SHIELD)),
    DRAGON_SLAYER_IV("Dragon slayer IV", "Kill 100 adamant or rune dragons.", 100, Difficulty.HARD, new Item(DRAGONFIRE_WARD)),
    FLUFFY_I("Fluffy I", "Kill Cerberus 15 times.", 15, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 5000 : 5_000_000)),
    FLUFFY_II("Fluffy II", "Kill Cerberus 50 times.", 50, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 10000 : 10_000_000)),
    BUG_EXTERMINATOR_I("Bug exterminator I", "Kill the Kalphite Queen 25 times.", 25, Difficulty.EASY, new Item(BLOOD_MONEY,15_000)),
    BUG_EXTERMINATOR_II("Bug exterminator II", "Kill the Kalphite Queen 100 times.", 100, Difficulty.MED, new Item(WEAPON_MYSTERY_BOX)),
    ULTIMATE_CHAOS_I("Ultimate chaos I", "Kill 20 Chaos elementals.", 20, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 10000 : 10_000_000)),
    ULTIMATE_CHAOS_II("Ultimate chaos II", "Kill 100 Chaos elementals.", 100, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 30000 : 30_000_000)),
    ULTIMATE_CHAOS_III("Ultimate chaos III", "Kill 500 Chaos elementals.", 500, Difficulty.HARD, new Item(LEGENDARY_MYSTERY_BOX)),
    HOLEY_MOLEY_I("Holey moley I", "Kill the Giant mole 10 times.", 10, Difficulty.MED, new Item(BLOOD_MONEY, 5_000)),
    HOLEY_MOLEY_II("Holey moley II", "Kill the Giant mole 50 times.", 50, Difficulty.MED, new Item(BLOOD_MONEY, 25_000)),
    HOLEY_MOLEY_III("Holey moley III", "Kill the Giant mole 100 times.", 100, Difficulty.MED, new Item(BLOOD_MONEY, 50_000)),
    LORD_OF_THE_RINGS_I("Lord of the rings I", "Kill 100 dagannoth kings.", 100, Difficulty.MED, new Item(ARCHERS_RING_I), new Item(BERSERKER_RING_I), new Item(SEERS_RING_I), new Item(WARRIOR_RING_I)),
    LORD_OF_THE_RINGS_II("Lord of the rings II", "Kill 250 dagannoth kings.", 250, Difficulty.MED, new Item(RING_OF_SUFFERING_I)),
    SQUIDWARD_I("Squidward I", "Kill 25 Krakens.", 25, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 5000 : 5_000_000)),
    SQUIDWARD_II("Squidward II", "Kill 100 Krakens", 100, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 15000 : 15_000_000)),
    SQUIDWARD_III("Squidward III", "Kill 250 Krakens", 250, Difficulty.HARD, new Item(TRIDENT_OF_THE_SEAS)),
    DR_CURT_CONNORS_I("Dr. Curt Connors I", "Kill 10 Lizardman shaman.", 10, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 5000 : 5_000_000)),
    DR_CURT_CONNORS_II("Dr. Curt Connors II", "Kill 100 Lizardman shaman.", 100, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 10000 : 25_000_000)),
    DR_CURT_CONNORS_III("Dr. Curt Connors III", "Kill 300 Lizardman shaman.", 300, Difficulty.HARD, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 20000 : 100_000_000)),
    TSJERNOBYL_I("Tsjernobyl I", "Kill 25 Thermonuclear smoke devil.", 25, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995,GameServer.properties().pvpMode ? 5000 :  5_000_000)),
    TSJERNOBYL_II("Tsjernobyl II", "Kill 150 Thermonuclear smoke devil.", 150, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 35000 : 35_000_000)),
    TSJERNOBYL_III("Tsjernobyl III", "Kill 500 Thermonuclear smoke devil.", 500, Difficulty.HARD, new Item(SMOKE_BATTLESTAFF)),
    VETION_I("Vet'ion I", "Kill 25 Vet'ions.", 25, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 10000 : 10_000_000)),
    VETION_II("Vet'ion II", "Kill 75 Vet'ions.", 75, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 35000 : 35_000_000)),
    VETION_III("Vet'ion I", "Kill 150 Vet'ions.", 150, Difficulty.HARD, new Item(RING_OF_THE_GODS_I)),
    BABY_ARAGOG_I("Baby Aragog I", "Kill Venenatis 25 times.", 25, Difficulty.EASY, new Item(TREASONOUS_RING_I)),
    BABY_ARAGOG_II("Baby Aragog II", "Kill Venenatis 100 times.", 100, Difficulty.MED, new Item(DONATOR_MYSTERY_BOX), new Item(BLOOD_MONEY, 25_000)),
    BABY_ARAGOG_III("Baby Aragog III", "Kill Venenatis 350 times.", 350, Difficulty.HARD, new Item(LEGENDARY_MYSTERY_BOX)),
    BARK_SCORPION_I("Bark scorpion I", "Kill scorpia 25 times.", 25, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 10000 : 10_000_000)),
    BARK_SCORPION_II("Bark scorpion II", "Kill scorpia 75 times.", 75, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 25000 : 25_000_000)),
    BARK_SCORPION_III("Bark scorpion III", "Kill scorpia 150 times.", 150, Difficulty.HARD, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 50000 : 50_000_000)),
    BEAR_GRYLLS_I("Bear Grylls I", "Kill 25 Callisto.", 25, Difficulty.EASY, new Item(TYRANNICAL_RING_I)),
    BEAR_GRYLLS_II("Bear Grylls II", "Kill 50 Callisto.", 50, Difficulty.MED, new Item(WEAPON_MYSTERY_BOX), new Item(BLOOD_MONEY, 12_500)),
    BEAR_GRYLLS_III("Bear Grylls III", "Kill 100 Callisto.", 100, Difficulty.HARD, new Item(DONATOR_MYSTERY_BOX)),
    SNAKE_CHARMER_I("Snake charmer I", "Kill 10 Zulrah.", 10, Difficulty.EASY, new Item(BLOOD_MONEY, 15_000)),
    SNAKE_CHARMER_II("Snake charmer II", "Kill 50 Zulrah.", 50, Difficulty.MED, new Item(SERPENTINE_HELM)),
    SNAKE_CHARMER_III("Snake charmer III", "Kill 250 Zulrah.", 250, Difficulty.HARD, new Item(TOXIC_BLOWPIPE)),
    VORKY_I("Vorky I", "Kill 50 Vorkaths.", 50, Difficulty.HARD, new Item(VORKATHS_HEAD_21907)),
    VORKY_II("Vorky II", "Kill 100 Vorkaths.", 100, Difficulty.HARD, new Item(SUPERIOR_DRAGON_BONES+1, 100), new Item(DONATOR_MYSTERY_BOX,3)),
    REVENANT_HUNTER_I("Revenant hunter I", "Kill 250 revenants.", 250, Difficulty.EASY, new Item(ANCIENT_EMBLEM,2), new Item(ItemIdentifiers.ANCIENT_STATUETTE,2)),
    REVENANT_HUNTER_II("Revenant hunter II", "Kill 500 revenants.", 500, Difficulty.MED, new Item(AMULET_OF_AVARICE)),
    REVENANT_HUNTER_III("Revenant hunter III", "Kill 2500 revenants.", 2500, Difficulty.HARD, new Item(ItemIdentifiers.ANCIENT_STATUETTE,2)),
    REVENANT_HUNTER_IV("Revenant hunter IV", "Kill 10000 revenants.", 10000, Difficulty.HARD, new Item(CRAWS_BOW), new Item(VIGGORAS_CHAINMACE), new Item(THAMMARONS_SCEPTRE)),
    GODWAR("Godwar", "Kill 500 Godwars Dungeon Bosses", 500, Difficulty.HARD, new Item(ARMADYL_GODSWORD + 1,2), new Item(BANDOS_GODSWORD + 1,2), new Item(SARADOMIN_GODSWORD + 1,2), new Item(ZAMORAK_GODSWORD + 1,2)),
    CORPOREAL_CRITTER("Corporeal Critter", "Kill 100 Corporeal beasts.", 100, Difficulty.HARD, new Item(SPECTRAL_SPIRIT_SHIELD)),
    MAGE_ARENA_I("Mage arena I", "Kill 100 battle mages at the mage arena.", 100, Difficulty.EASY, new Item(ItemIdentifiers.STAFF_OF_LIGHT)),
    MAGE_ARENA_II("Mage arena II", "Kill 250 battle mages at the mage arena.", 250, Difficulty.MED, new Item(ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD)),
    MAGE_ARENA_III("Mage arena III", "Kill 500 battle mages at the mage arena.", 500, Difficulty.HARD, new Item(ItemIdentifiers.ZURIELS_STAFF)),
    MAGE_ARENA_IV("Mage arena IV", "Kill 1.000 battle mages at the mage arena.", 1000, Difficulty.HARD, new Item(ItemIdentifiers.ZURIELS_HOOD), new Item(ItemIdentifiers.ZURIELS_ROBE_TOP), new Item(ItemIdentifiers.ZURIELS_ROBE_BOTTOM)),

    SKILLER_I("Skiller I", "Earn a total level of 750 on a trained account.", 1, Difficulty.EASY, new Item(ItemIdentifiers.ANTIQUE_LAMP, 1), new Item(ItemIdentifiers.MYSTERY_BOX, 1)),
    SKILLER_II("Skiller II", "Earn a total level of 1000 on a trained account.", 1, Difficulty.MED, new Item(ItemIdentifiers.ANTIQUE_LAMP, 1), new Item(ARMOUR_MYSTERY_BOX, 2)),
    SKILLER_III("Skiller III", "Earn a total level of 1500 on a trained account.", 1, Difficulty.HARD, new Item(ItemIdentifiers.ANTIQUE_LAMP, 2), new Item(WEAPON_MYSTERY_BOX, 2)),
    SKILLER_IV("Skiller IV", "Earn level 99 in all skills on a trained account<br>with the exception of construction.", 1, Difficulty.HARD, new Item(DONATOR_MYSTERY_BOX, 5)),

    //Pvp
    PVP_I("PVP I", "Kill 100 players in the wilderness.",100, Difficulty.EASY, new Item(ItemIdentifiers.SARADOMIN_GODSWORD), new Item(ItemIdentifiers.BANDOS_GODSWORD), new Item(ItemIdentifiers.ZAMORAK_GODSWORD)),
    PVP_II("PVP II", "Kill 500 players in the wilderness.",500, Difficulty.MED, new Item(ItemIdentifiers.ARMADYL_GODSWORD)),
    PVP_III("PVP III", "Kill 1.000 players in the wilderness.",1000, Difficulty.HARD, new Item(ItemIdentifiers.GHRAZI_RAPIER)),

    BOUNTY_HUNTER_I("Bounty hunter I", "Kill 50 targets.", 50, Difficulty.EASY, new Item(ItemIdentifiers.BANDOS_CHESTPLATE), new Item(ItemIdentifiers.BANDOS_TASSETS), new Item(ItemIdentifiers.PRIMORDIAL_BOOTS)),
    BOUNTY_HUNTER_II("Bounty hunter II", "Kill 100 targets.", 100, Difficulty.MED, new Item(NEITIZNOT_FACEGUARD)),
    BOUNTY_HUNTER_III("Bounty hunter III", "Kill 300 targets.", 300, Difficulty.HARD,  new Item(FEROCIOUS_GLOVES), new Item(ItemIdentifiers.AMULET_OF_TORTURE)),

    DEEP_WILD_I("Deep wild I", "Kill 75 players in level 30+ wilderness.", 75, Difficulty.EASY, new Item(ItemIdentifiers.ANCIENT_WYVERN_SHIELD)),
    DEEP_WILD_II("Deep wild II", "Kill 150 players in level 30+ wilderness.", 150, Difficulty.MED, new Item(DONATOR_MYSTERY_BOX, 5)),
    DEEP_WILD_III("Deep wild III", "Kill 300 players in level 30+ wilderness.", 300, Difficulty.HARD, new Item(LEGENDARY_MYSTERY_BOX, 1)),

    EXTREME_DEEP_WILD_I("Extreme deep wild I", "Kill 50 players in level 50+ wilderness.", 50, Difficulty.EASY, new Item(ItemIdentifiers.DINHS_BULWARK)),
    EXTREME_DEEP_WILD_II("Extreme deep wild II", "Kill 100 players in level 50+ wilderness.", 100, Difficulty.MED, new Item(ItemIdentifiers.ANCESTRAL_HAT)),
    EXTREME_DEEP_WILD_III("Extreme deep wild III", "Kill 250 players in level 50+ wilderness.", 250, Difficulty.HARD, new Item(ItemIdentifiers.ANCESTRAL_ROBE_TOP), new Item(ItemIdentifiers.ANCESTRAL_ROBE_BOTTOM)),

    BLOODTHIRSTY_I("Bloodthirsty I", "Get a killstreak of 25.", 1, Difficulty.MED, new Item(ItemIdentifiers.ARMADYL_GODSWORD)),
    BLOODTHIRSTY_II("Bloodthirsty II", "Get a killstreak of 50.", 1, Difficulty.HARD, new Item(ItemIdentifiers.DRAGON_CLAWS)),
    BLOODTHIRSTY_III("Bloodthirsty III", "Kill someone that is on a killstreak of +50.", 1, Difficulty.HARD, new Item(ItemIdentifiers.ELDER_MAUL)),

    SURVIVOR_I("Survivor I", "Get a wilderness killstreak of 5.", 5, Difficulty.MED, new Item(ItemIdentifiers.DINHS_BULWARK)),
    SURVIVOR_II("Survivor II", "Get a wilderness killstreak of above 10.", 10, Difficulty.HARD, new Item(DRAGON_CLAWS)),

    PURE_I("Pure I", "Get 50 player kills with a defence level of 1.<br>You must have a CB level of at least 80!", 50, Difficulty.EASY, new Item(ItemIdentifiers.ELDER_CHAOS_HOOD), new Item(ItemIdentifiers.ELDER_CHAOS_TOP), new Item(ItemIdentifiers.ELDER_CHAOS_ROBE)),
    PURE_II("Pure II", "Get 100 player kills with a defence level of 1.<br>You must have a CB level of at least 80!", 100, Difficulty.MED, new Item(ItemIdentifiers.GRANITE_MAUL_12848)),
    PURE_III("Pure III", "Get 200 player kills with a defence level of 1.<br>You must have a CB level of at least 80!", 200, Difficulty.HARD, new Item(ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD)),
    PURE_IV("Pure IV", "Get 350 player kills with a defence level of 1.<br>You must have a CB level of at least 80!", 350, Difficulty.HARD, new Item(ItemIdentifiers.ELDER_MAUL)),

    ZERKER_I("Zerker I", "Get 50 player kills with a defence level of 45.<br>You mast have a CB level of at least 95!", 50, Difficulty.EASY, new Item(ItemIdentifiers.FIGHTER_HAT), new Item(ItemIdentifiers.FIGHTER_TORSO), new Item(ItemIdentifiers.FIRE_CAPE)),
    ZERKER_II("Zerker II", "Get 100 player kills with a defence level of 45.<br>You mast have a CB level of at least 95!", 100, Difficulty.MED, new Item(ItemIdentifiers.ODIUM_WARD), new Item(ItemIdentifiers.MALEDICTION_WARD)),
    ZERKER_III("Zerker III", "Get 200 player kills with a defence level of 45.<br>You mast have a CB level of at least 95!", 200, Difficulty.HARD, new Item(ItemIdentifiers.DRAGONFIRE_SHIELD), new Item(ItemIdentifiers.DRAGONFIRE_WARD), new Item(ItemIdentifiers.ANCIENT_WYVERN_SHIELD)),
    ZERKER_IV("Zerker IV", "Get 350 player kills with a defence level of 45.<br>You mast have a CB level of at least 95!", 350, Difficulty.HARD, new Item(ItemIdentifiers.STATIUSS_WARHAMMER)),

    TASK_MASTER_I("Task master I", "Complete 10 PvP tasks.", 10, Difficulty.EASY, new Item(ItemIdentifiers.TWISTED_ANCESTRAL_COLOUR_KIT)),
    TASK_MASTER_II("Task master II", "Complete 25 PvP tasks.", 25, Difficulty.MED, new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX)),
    TASK_MASTER_III("Task master III", "Complete 50 PvP tasks.", 50, Difficulty.HARD, new Item(ItemIdentifiers.VESTAS_LONGSWORD)),

    DHAROK_BOMBER_I("Dharok bomber I","Kill 35 players wearing full dharok.<br>Your hitpoints must be below 25.",35, Difficulty.EASY, new Item(DRAGON_BOOTS, 1), new Item(AMULET_OF_THE_DAMNED, 1), new Item(DHAROKS_ARMOUR_SET,1), new Item(BERSERKER_RING_I,1), new Item(ABYSSAL_TENTACLE,1), new Item(DWARVEN_ROCK_CAKE,1)),
    DHAROK_BOMBER_II("Dharok bomber II","Kill 50 players wearing full dharok.<br>Your hitpoints must be below 15.",50, Difficulty.MED, new Item(DHAROKS_ARMOUR_SET,1), new Item(PRIMORDIAL_BOOTS, 1), new Item(AMULET_OF_TORTURE,1), new Item(BERSERKER_RING_I,1),  new Item(ABYSSAL_TENTACLE,1), new Item(DWARVEN_ROCK_CAKE,1)),

    KEEP_IT_100_I("Keep it 100 I", "Kill 25 players without using a special attack.", 25, Difficulty.EASY, new Item(BLADE_OF_SAELDOR)),
    KEEP_IT_100_II("Keep it 100 II", "Kill 50 players without using a special attack.", 50, Difficulty.MED, new Item(ARMADYL_GODSWORD)),
    KEEP_IT_100_III("Keep it 100 III", "Kill 100 players without using a special attack.", 100, Difficulty.HARD,  new Item(ELDER_MAUL)),

    PUNCHING_BAGS_I("Punching bags I", "Kill 3 players barehanded.", 3, Difficulty.HARD, new Item(BOXING_GLOVES)),
    PUNCHING_BAGS_II("Punching bags II", "Kill 5 players barehanded.", 5, Difficulty.HARD, new Item(WEAPON_MYSTERY_BOX)),
    PUNCHING_BAGS_III("Punching bags III", "Kill 10 players barehanded.",10, "- Ability to choose the title<br>Rocky Balboa.", Difficulty.HARD),

    AMPUTEE_ANNIHILATION_I("Amputee annihilation I","Kill 50 players without wearing a body and legs.",50, Difficulty.EASY, new Item(BANDOS_CHESTPLATE), new Item(BANDOS_TASSETS), new Item(BANDOS_BOOTS)),
    AMPUTEE_ANNIHILATION_II("Amputee annihilation II","Kill 100 players without wearing a body and legs.",100, Difficulty.MED, new Item(AMULET_OF_TORTURE), new Item(PRIMORDIAL_BOOTS), new Item(BERSERKER_RING_I),new Item(BLOOD_MONEY,10_000)),
    AMPUTEE_ANNIHILATION_III("Amputee annihilation III","Kill 200 players without wearing a body and legs.",200, Difficulty.HARD, new Item(STATIUSS_FULL_HELM), new Item(STATIUSS_PLATEBODY),new Item(STATIUSS_PLATELEGS)),

    PET_TAMER_I("PetDefinitions tamer I","Kill 50 players whilst having a Vorki pet out.",50, "- Whilst having Vorki pet out<br>you are resistant against...<br>dragon fire.", Difficulty.HARD),
    PET_TAMER_II("PetDefinitions tamer II","Kill 50 players whilst having a Zulrah pet out.",50, "- Whilst having the Snakeling<br>pet out you are resistant...<br>against venom.", Difficulty.HARD),

    //Minigames
    BARROWS_I("Barrows I", "Complete 10 barrows runs.", 10, Difficulty.EASY, new Item(TORAGS_ARMOUR_SET)),
    BARROWS_II("Barrows II", "Complete 30 barrows runs.", 30, Difficulty.MED, new Item(ItemIdentifiers.DHAROKS_ARMOUR_SET, 1)),
    BARROWS_III("Barrows III", "Complete 50 barrows runs.", 50, Difficulty.HARD, new Item(KARILS_ARMOUR_SET, 1)),
    BARROWS_IV("Barrows IV", "Complete 75 barrows runs.", 75, Difficulty.HARD, new Item(AHRIMS_ARMOUR_SET, 1)),
    BARROWS_V("Barrows V", "Complete 100 barrows runs.", 100, Difficulty.HARD, new Item(GUTHANS_ARMOUR_SET, 1)),
    FIGHT_CAVES_I("Fight caves I", "Defeat TzTok-Jad.", 1, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 5000 : 5_000_000)),
    FIGHT_CAVES_II("Fight caves II", "Defeat TzTok-Jad 50 times.", 50, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 25000 : 50_000_000)),
    FIGHT_CAVES_III("Fight caves III", "Defeat TzTok-Jad 150 times.", 150, Difficulty.HARD, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 50000 : 150_000_000)),

    //Skilling

    /* Thieving */
    THIEF_I("Thief I", "Steal 150 times from the Crafting stall.", 150, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 10_000 : 10_000_000)),
    THIEF_II("Thief II", "Steal 350 times from the General stall.", 350, Difficulty.EASY, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 15_000 : 15_000_000)),
    THIEF_III("Thief III", "Steal 500 times from the Magic stall.", 500, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 50_000 : 50_000_000)),
    THIEF_IV("Thief IV", "Steal 750 times from the Scimitar stall.", 750, Difficulty.MED, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995,  GameServer.properties().pvpMode ? 75_000 : 75_000_000)),
    MASTER_THIEF("Master thief", "Steal times supplies from the any stall.", 5000, Difficulty.HARD, new Item(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 50_000 : 100_000_000)),
    MINING_I("Mining I", "Mine 50 copper ore.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(PROSPECTOR_HELMET), new Item(MYSTERY_BOX, 1)),
    MINING_II("Mining II", "Mine 100 coal.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(PROSPECTOR_JACKET), new Item(MYSTERY_BOX, 2)),
    MINING_III("Mining III", "Mine 250 adamant ore.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(PROSPECTOR_LEGS), new Item(MYSTERY_BOX, 3)),
    MINING_IV("Mining IV", "Mine 500 runite ore.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 4), new Item(PROSPECTOR_BOOTS)),
    SMELTING_I("Smelting I", "Smith 50 bronze platebody's.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(BLACKSMITHS_HELM), new Item(MYSTERY_BOX, 1)),
    SMELTING_II("Smelting II", "Smith 100 mithril platebody's.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(SMITHS_TUNIC), new Item(MYSTERY_BOX, 2)),
    SMELTING_III("Smelting III", "Smith 250 adamant platebody's.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(SMITHS_TROUSERS), new Item(MYSTERY_BOX, 3)),
    SMELTING_IV("Smelting IV", "Smith 500 runite platebody's.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 4), new Item(IMCANDO_HAMMER), new Item(SMITHS_BOOTS), new Item(SMITHS_GLOVES)),
    FISHING_I("Fishing I", "Fish 50 shrimp.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(ANGLER_HAT), new Item(MYSTERY_BOX, 1)),
    FISHING_II("Fishing II", "Fish 100 swordfish.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(ANGLER_TOP), new Item(MYSTERY_BOX, 2)),
    FISHING_III("Fishing III", "Fish 250 shark.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(ANGLER_WADERS), new Item(MYSTERY_BOX, 3)),
    FISHING_IV("Fishing IV", "Fish 500 anglerfish.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 4), new Item(ANGLER_BOOTS)),
    FIREMAKING_I("Firemaking I", "Light 100 fires.", 100, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(PYROMANCER_HOOD), new Item(MYSTERY_BOX, 1)),
    FIREMAKING_II("Firemaking II", "Light 150 fires.", 150, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(PYROMANCER_GARB), new Item(MYSTERY_BOX, 2)),
    FIREMAKING_III("Firemaking III", "Light 250 fires.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(PYROMANCER_ROBE), new Item(MYSTERY_BOX, 3)),
    FIREMAKING_IV("Firemaking IV", "Light 500 fires.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 4), new Item(PYROMANCER_BOOTS), new Item(MAGIC_LOGS, 100)),
    COOKING_I("Cooking I", "Cook 50 shrimp.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(LOBSTER, 250), new Item(MYSTERY_BOX, 1)),
    COOKING_II("Cooking II", "Cook 100 lobster.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(MONKFISH, 150), new Item(MYSTERY_BOX, 2)),
    COOKING_III("Cooking III", "Cook 250 shark.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(SHARK, 100), new Item(MYSTERY_BOX, 3)),
    COOKING_IV("Cooking IV", "Cook 500 anglerfish.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 4), new Item(ANGLERFISH, 250)),
    WOODCUTTING_I("Woodcutting I", "Cut 50 tree's.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(LUMBERJACK_HAT, 1), new Item(MYSTERY_BOX, 1)),
    WOODCUTTING_II("Woodcutting II", "Cut 100 willow tree's.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(LUMBERJACK_TOP, 1), new Item(MYSTERY_BOX, 2)),
    WOODCUTTING_III("Woodcutting III", "Cut 250 yew tree's.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(LUMBERJACK_LEGS, 1), new Item(MYSTERY_BOX, 3)),
    WOODCUTTING_IV("Woodcutting IV", "Cut 500 magic tree's.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(LUMBERJACK_BOOTS, 1), new Item(MYSTERY_BOX, 4)),
    CRAFTING_I("Crafting I", "Cut 50 sapphire gems.", 50, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX, 1)),
    CRAFTING_II("Crafting II", "Cut 100 emerald gems.", 100, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX, 1)),
    CRAFTING_III("Crafting III", "Cut 250 ruby gems.", 250, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(MYSTERY_BOX, 1)),
    CRAFTING_IV("Crafting IV", "Cut 500 diamond gems.", 500, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 2)),
    DAMAGE_DEALER_I("Damage Dealer I", "Deal 1000 damage.", 5000, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX, 1)),
    DAMAGE_DEALER_II("Damage Dealer II", "Deal 2000 damage.", 10000, Difficulty.MED, new Item(COINS_995, 10_000_000), new Item(MYSTERY_BOX, 1)),
    DAMAGE_DEALER_III("Damage Dealer III", "Deal 5000 damage.", 15000, Difficulty.MED, new Item(COINS_995, 20_000_000), new Item(MYSTERY_BOX, 1)),
    DAMAGE_DEALER_IV("Damage Dealer IV", "Deal 10000 damage.", 20000, Difficulty.HARD, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 2)),
    SLAYER_I("Slayer I", "Complete 25 slayer tasks.", 25, Difficulty.EASY, new Item(COINS_995, 5_000_000), new Item(MYSTERY_BOX, 1)),
    SLAYER_II("Slayer II", "Complete 50 slayer tasks.", 50, Difficulty.MED, new Item(COINS_995, 25_000_000), new Item(MYSTERY_BOX, 1)),
    SLAYER_III("Slayer III", "Complete 75 slayer tasks.", 75, Difficulty.MED, new Item(COINS_995, 35_000_000), new Item(MYSTERY_BOX, 1)),
    SLAYER_IV("Slayer IV", "Complete 150 slayer tasks.", 150, Difficulty.HARD, new Item(COINS_995, 50_000_000), new Item(MYSTERY_BOX, 2))

    ;

    public static List<Achievements> asList(Difficulty difficulty) {
        return Arrays.stream(values()).filter(a -> a.difficulty == difficulty).sorted((a, b) -> a.name().compareTo(b.name())).collect(Collectors.toList());
    }

    private final String name;
    private final String description;
    private final int completeAmount;
    private final String rewardString;
    private final Difficulty difficulty;
    private final Item[] reward;

    Achievements(String name, String description, int completeAmount, Difficulty difficulty, Item... reward) {
        this.name = name;
        this.description = description;
        this.completeAmount = completeAmount;
        this.rewardString = "";
        this.difficulty = difficulty;
        this.reward = reward;
    }

    Achievements(String name, String description, int completeAmount, String rewardString, Difficulty difficulty, Item... reward) {
        this.name = name;
        this.description = description;
        this.completeAmount = completeAmount;
        this.rewardString = rewardString;
        this.difficulty = difficulty;
        this.reward = reward;
    }

    public int getCompleteAmount() {
        return completeAmount;
    }

    public String getDescription() {
        return description;
    }

    public Item[] getReward() {
        return reward;
    }

    public String getName() {
        return name;
    }

    public String otherRewardString() {
        return rewardString;
    }

    public static int getTotal() {
        return values().length;
    }
}
