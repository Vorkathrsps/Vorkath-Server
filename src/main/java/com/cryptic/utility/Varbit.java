package com.cryptic.utility;

/**
 * Created by Bart on 8/16/2015.
 * <p>
 * Definition IDs - the definition will include the bit count shifted within the storing 32-bit integer to represent the value stored.
 */
public class Varbit {
    public static final int STAMINA_POTION = 25;
    public static final int GAME_FILTER = 26;
    public static final int MAGEBANK_MAGIC_ONLY = 271;
    public static final int AUTOCAST_SELECTED = 276;
    public static final int IN_WILDERNESS = 5963; // 0 OUTSIDE, 1 INSIDE
    public static final int VENGEANCE_ACTIVE = 2450;

    // Barrows
    public static final int DHAROK = 458;
    public static final int AHRIM = 457;
    public static final int KARIL = 460;
    public static final int VERAC = 462;
    public static final int GUTHAN = 459;
    public static final int TORAG = 461;
    public static final int BARROWS_MONSTER_KC = 464;
    public static final int UNLOCK_GOBLIN_BOW_AND_SALUTE_EMOTE = 532; // 7 = unlocked

    public static final int ABYSS_MAP = 625;

    // Duel arena
    public static final int HELM_SLOT = 643;
    public static final int CAPE_SLOT = 644;
    public static final int AMULET_SLOT = 645;
    public static final int WEAPON_SLOT = 646;
    public static final int BODY_SLOT = 647;
    public static final int SHIELD_SLOT = 648;
    public static final int LEGS_SLOT = 650;
    public static final int GLOVES_SLOT = 652;
    public static final int BOOTS_SLOT = 653;
    public static final int RING_SLOT = 655;
    public static final int AMMO_SLOT = 656;

    //Farming
    public static final int FARMING_RAKE = 1435;
    public static final int SEED_DIBBER = 1436;
    public static final int ALL_PURPOSE_SPADE = 1437;
    public static final int SECATEURS = 1438;
    public static final int WATERING_CAN = 1439;
    public static final int GARDENING_TROWEL = 1440;
    public static final int EMPTY_BUCKETS = 1441;
    public static final int NORMAL_COMPOST = 1442;
    public static final int SUPER_COMPOST = 1443;

    public static final int SLAYER_SOMETHING_1 = 1766; // max val 1
    public static final int SLAYER_SOMETHING_2 = 1767; // max val 1
    public static final int SLAYER_SOMETHING_3 = 2013; // max val 1

    public static final int LUMBRIDGE_CANOE_STATION = 1839; // Varp 674 and 675 for canoes
    public static final int CHAMPIONS_GUILD_CANOE_STATION = 1840;
    public static final int BARBARIAN_VILLAGE_CANOE_STATION = 1841;
    public static final int EDGEVILLE_CANOE_STATION = 1842;
    public static final int CHINS_CANOE_STATION = 1843;

    public static final int UNLOCK_FLAP_EMOTE = 2309;
    public static final int UNLOCK_SLAP_HEAD_EMOTE = 2310;
    public static final int UNLOCK_IDEA_EMOTE = 2311;
    public static final int UNLOCK_STAMP_EMOTE = 2312;

    public static final int VENGENACE_COOLDOWN = 2451;

    public static final int CHATBOX_SOLID = 2570;
    public static final int MIDDLE_MOUSE = 4134;
    public static final int QUEST_LIST_ORB_COLOR = 3217;
    public static final int SHIFT_CLICK_DROP = 5542;
    public static final int FOLLOWER_PRIORITY = 5599;
    public static final int PRAYER_TOOLTIP = 5711;
    public static final int SPEC_TOOLTIP = 5712;

    public static final int RAID_CMB_LVL_PREF = 5426;
    public static final int RAID_SKILL_LVL_PREF = 5427;
    public static final int RAID_PARTY_SIZE_PREF = 5433;

    /**
     * values: 0=A, 1=D, 2=C, 3=B
     */
    public static final int FAIRY_RING_LEFT = 3985;
    /**
     * values: 0=I, 1=L, 2=K, 3=J
     */
    public static final int FAIRY_RING_MIDDLE = 3986;
    /**
     * values: 0=LOGOUT_TAB, 1=S, 2=R, 3=Q
     */
    public static final int FAIRY_RING_RIGHT = 3987;
    public static final int DATAORBS_HIDDEN = 4084;
    public static final int CHATBOX_TRANSPARENT = 4608;
    public static final int TABS_CAN_BE_CLOSED_BY_HOTKEY = 4611;
    public static final int SIDEPANELS_OPAQUE = 4609;
    public static final int SHOW_XP_TILL_LEVEL = 4181;
    public static final int SCROLLBAR_POS = 6374;
    public static final int HIDE_PM_WHEN_CHAT_HIDDEN = 4089;
    public static final int BOUNTY_HUNTER_RECORD_OVERLAY_HIDDEN = 1621;
    public static final int LOGIN_TIMEOUT_HIDDEN = 1627;
    public static final int SIDESTONES_ARRANGEMENT = 4607;
    public static final int FRIEND_IGNORE_TOGGLE = 6516;

    //Farming
    public static final int FALADOR_COMPOST_BIN_FULLNESS = 4961;
    public static final int FALADOR_COMPOST_BIN_TYPE = 4962;

    public static final int RAIDS_CHEST = 1327;
    public static final int RAIDS_REWARD = 5456;

    // Pets
    public static final int UNLOCKED_PET_DAGANNOTH_SUPREME = 4338;
    public static final int UNLOCKED_PET_DAGANNOTH_PRIME = 4339;
    public static final int UNLOCKED_PET_DAGANNOTH_REX = 4340;
    public static final int UNLOCKED_PET_PENANCE_QUEEN = 4341;
    public static final int UNLOCKED_PET_KREEARRA = 4342;
    public static final int UNLOCKED_PET_GRAARDOR = 4343;
    public static final int UNLOCKED_PET_ZILYANA = 4344;
    public static final int UNLOCKED_PET_KRIL = 4345;
    public static final int UNLOCKED_PET_MOLE = 4346;
    public static final int UNLOCKED_PET_KBD = 4347;
    public static final int UNLOCKED_PET_KQ = 4348;
    public static final int UNLOCKED_PET_SMOKE_DEVIL = 4349;
    public static final int UNLOCKED_PET_KRAKEN = 4350;
    public static final int UNLOCKED_PET_CHOMPY = 4445;
    public static final int UNLOCKED_PET_CALLISTO = 4568;
    public static final int UNLOCKED_PET_VENENATIS = 4429;
    public static final int UNLOCKED_PET_VETION_PURPLE = 4569;
    public static final int UNLOCKED_PET_SCORPIA = 4570;
    public static final int UNLOCKED_PET_JAD = 4699;
    public static final int UNLOCKED_JAL_NIB = 5644;
    public static final int UNLOCKED_PET_CERBERUS = 4726;
    public static final int UNLOCKED_PET_HERON = 4846;
    public static final int UNLOCKED_PET_GOLEM = 4847;
    public static final int UNLOCKED_PET_BEAVER = 4848;
    public static final int UNLOCKED_PET_CHINCHOMPA = 4849;
    public static final int UNLOCKED_PET_ABYSSAL = 204;
    public static final int UNLOCKED_PET_CORE = 997;
    public static final int UNLOCKED_PET_ZULRAH = 1526;
    public static final int UNLOCKED_PET_CHAOS_ELE = 3962;
    public static final int UNLOCK_PET_GIANT_SQUIRREL = 2169;
    public static final int UNLOCK_PET_TANGLEROOT = 2170;
    public static final int UNLOCK_PET_RIFT_GUARDIAN = 2171;
    public static final int UNLOCKED_PET_ROCKY = 2172;
    public static final int UNLOCK_PET_BLOODHOUND = 5181;
    public static final int UNLOCK_PET_PHOENIX = 5363;
    public static final int UNLOCK_PET_OLMLET = 5448;
    public static final int UNLOCKED_PET_NOON = 6013;
    public static final int UNLOCKED_PET_HERBI = 5735;
    public static final int UNLOCKED_PET_SKOTOS = 5632;
    public static final int UNLOCKED_PET_VORKI = 6102;

    public static final int GARGOYLE_SMASHER = 4027;
    public static final int SLUG_SALTER = 4028;
    public static final int REPTILE_FREEZER = 4029;
    public static final int SHROOM_SPRAYER = 4030;
    public static final int NEED_MORE_DARKNESS = 4031;
    public static final int ANKOU_VERY_MUCH = 4085;
    public static final int SUQ_ANOTHER_ONE = 4086;
    public static final int FIRE_AND_DARKNESS = 4087;
    public static final int PEDAL_TO_THE_METALS = 4088;
    public static final int AUGMENT_MY_ABBIES = 4090;
    public static final int ITS_DARK_IN_HERE = 4091;
    public static final int GREATER_CHALLENGE = 4092;
    public static final int I_HOPE_YOU_MITH_ME = 4094;
    public static final int WATCH_THE_BIRDIE = 4095;
    public static final int HOT_STUFF = 4691;
    public static final int LIKE_A_BOSS = 4724;
    public static final int BLEED_ME_DRY = 4746;
    public static final int SMELL_YA_LATER = 4747;
    public static final int BIRDS_OF_A_FEATHER = 4748;
    public static final int I_REALLY_MITH_YOU = 4749;
    public static final int HORRORIFIC = 4750;
    public static final int TO_DUST_YOU_SHALL_RETURN = 4751;
    public static final int WYVER_NOTHER_ONE = 4752;
    public static final int GET_SMASHED = 4753;
    public static final int NECHS_PLEASE = 4754;
    public static final int KRACK_ON = 4755;
    public static final int SPIRITUAL_FERVOUR = 4757;
    public static final int REPTILE_GOT_RIPPED = 4996;

    public static final int SLAYER_UNLOCKED_HELM = 3202;
    public static final int RING_BLING = 3207;
    public static final int BROADER_FLETCHING = 3208;
    public static final int KING_BLACK_BONNET = 5080;
    public static final int KALPHITE_KHAT = 5081;
    public static final int UNHOLY_HELMET = 5082;
    public static final int BIGGER_AND_BADDER = 5358;
    public static final int SEEING_RED = 4262;
    public static final int GET_SCABARIGHT_ON_IT = 4391;
    public static final int UNLOCK_BLOCK_TASK_SIX = 4538;
    public static final int UNLOCK_DULY_NOTED = 4589;
    //649 ??  651 654

    //Blocked slayer tasks
    public static final int BLOCKED_TASK_SLOT_ONE = 3209; // belongs to varp 661. max val 128
    public static final int BLOCKED_TASK_SLOT_TWO = 3210; // belongs to varp 1096! max val 128
    public static final int BLOCKED_TASK_SLOT_THREE = 3211; // max val 128
    public static final int BLOCKED_TASK_SLOT_FOUR = 3212; // max val 128
    public static final int BLOCKED_TASK_SLOT_FIVE = 4441; // belongs to varp 439.  max val 128
    public static final int BLOCKED_TASK_SLOT_SIX = 5023; // varp 1096. // max val 128

    public static final int SMITHING_BAR_TYPE = 3216;
    public static final int BANK_NOTE = 3958;
    public static final int BANK_QUANTITY = 6590;
    public static final int BANK_PLACEHOLDERS = 3755;
    public static final int BANK_INSERT = 3959;
    public static final int BANK_SELECTED_TAB = 4150;
    public static final int BANK_YOUR_LOOT = 4139;
    public static final int LAST_BANK_X = 3960;
    public static final int BANK_INCINERATOR = 5102;
    public static final int GOD_WARS_DUNGEON = 3966;
    public static final int GOD_WARS_SARADOMIN_FIRST_ROPE = 3967;
    public static final int GOD_WARS_SARADOMIN_SECOND_ROPE = 3968;
    public static final int GWD_SARADOMIN_KC = 3972;
    public static final int GWD_ARMADYL_KC = 3973;
    public static final int GWD_BANDOS_KC = 3975;
    public static final int GWD_ZAMORAK_KC = 3976;
    public static final int SLAYER_MASTER = 4067;
    public static final int SLAYER_POINTS = 4068;
    public static final int REGULAR_PRAYERS = 4101;
    public static final int QUICK_PRAYER = 4102;
    public static final int QUICK_PRAYERS_ON = 4103;
    public static final int THICK_SKIN = 4104;
    public static final int BURST_OF_STRENGTH = 4105;
    public static final int CLARITY_OF_THOUGHT = 4106;
    public static final int ROCK_SKIN = 4107;
    public static final int SUPERHUMAN_STRENGTH = 4108;
    public static final int IMPROVED_REFLEXIS = 4109;
    public static final int RAPID_RESTORE = 4110;
    public static final int RAPID_HEAL = 4111;
    public static final int PROTECT_ITEM = 4112;
    public static final int STEEL_SKIN = 4113;
    public static final int ULTIMATE_STRENGTH = 4114;
    public static final int INCREDIBLE_REFLEXES = 4115;
    public static final int PROTECT_FROM_MAGIC = 4116;
    public static final int PROTECT_FROM_MISSILES = 4117;
    public static final int PROTECT_FROM_MELEE = 4118;
    public static final int RETRIBUTION = 4119;
    public static final int REDEMPTION = 4120;
    public static final int SMITE = 4121;
    public static final int SHARP_EYE = 4122;
    public static final int MYSTIC_WILL = 4123;
    public static final int HAWK_EYE = 4124;
    public static final int MYSTIC_LORE = 4125;
    public static final int EAGLE_EYE = 4126;
    public static final int MYSTIC_MIGHT = 4127;
    public static final int CHIVALRY = 4128;
    public static final int PIETY = 4129;
    public static final int RIGOUR = 5464;
    public static final int AUGURY = 5465;
    public static final int PRESERVE = 5466;
    public static final int KDR_OVERLAY = 4143;
    public static final int TRADE_MODIFIED_LEFT = 4374;
    public static final int TRADE_MODIFIED_RIGHT = 4375;
    public static final int MULTIWAY_AREA = 4605;
    public static final int COMBAT_HOTKEY = 4675;
    public static final int SKILLS_HOTKEY = 4676;
    public static final int QUESTS_HOTKEY = 4677;
    public static final int INVENTORY_HOTKEY = 4678;
    public static final int EQUIPMENT_HOTKEY = 4679;
    public static final int PRAYERS_HOTKEY = 4680;
    public static final int ESC_CLOSES_INTERFACES = 4681;
    public static final int MAGIC_HOTKEY = 4682;
    public static final int CLANCHAT_HOTKEY = 4683;
    public static final int FRIENDS_HOTKEY = 4684;
    public static final int IGNORES_HOTKEY = 4685;
    public static final int SETTINGS_HOTKEY = 4686;
    public static final int EMOTES_HOTKEY = 4687;
    public static final int MUSIC_HOTKEY = 4688;
    public static final int LOGOUT_HOTKEY = 4689;
    public static final int XP_DROPS_POSITION = 4692;
    public static final int XP_DROPS_SIZE = 4693;
    public static final int XP_DROPS_DURATION = 4694;
    public static final int XP_DROPS_COLOR = 4695;
    public static final int XP_DROPS_GROUP = 4696;
    public static final int XP_DROPS_COUNTER = 4697;
    public static final int XP_DROPS_PROGRESSBAR = 4698;
    public static final int XP_DROPS_VISIBLE = 4702;
    /**
     * Button id PLUS ONE by the way before you go fucking insane wondering why its not the same as slot
     */
    public static final int XP_DROPS_TRACKER_STAT_VIEWING = 4703;
    public static final int XP_DROPS_TRACKER_TYPE_ACTIVE = 4704;
    public static final int XP_DROPS_SPEED = 4722;
    public static final int SARADOMINS_LIGHT = 4733;
    public static final int XMAS2015_STAGE = 4824;
    public static final int DEADMAN_SKULL_TIMER = 4854;
    public static final int DEADMAN_KEYS_CARRIED = 4855;
    public static final int XMAS2015_RANGESTORE_KID = 4857;
    public static final int XMAS2015_AUBURY_KID = 4858;
    public static final int XMAS2015_GENSTORE_KID = 4860;
    public static final int XMAS2015_EAST_KID = 4862;

    public static final int RUNEPOUCH_TYPE_LEFT = 29;
    public static final int RUNEPOUCH_TYPE_MIDDLE = 1622;
    public static final int RUNEPOUCH_TYPE_RIGHT = 1623;
    public static final int RUNEPOUCH_NUM_LEFT = 1624;
    public static final int RUNEPOUCH_NUM_MIDDLE = 1625;
    public static final int RUNEPOUCH_NUM_RIGHT = 1626;
    public static final int ACCOUNT_TYPE = 1777;

    // Grand exchange related
    public static final int GRAND_EXCHANGE_SLOT = 4439;
    public static final int GRAND_EXCHANGE_AMOUNT = 4396;
    public static final int GRAND_EXCHANGE_PRICE = 4398;


    public static final int BOUNTY_HUNTER_POINTS = 1132;

    public static final int SPELLBOOK = 4070;
    public static final int BANKTAB_DISPLAY_TYPE = 4170;

    // At 2439, 3519
    public static final int GNOME_STRONGHOLD_MUSHROOM_HIDDEN = 5027;

    public static final int DEADMAN_ACTIVE_KEY_VIEWED = 4842;
    public static final int DEADMAN_WITHDRAW_LOOT_TYPE = 4843;
    public static final int LOCK_CAMERA = 4606;

    public static final int BANK_DEPOSIT_WORN_ITEMS_BTN = 5364;

    public static final int CAROL_CHRISTMAS_EVENT = 5442;

    public static final int TARGET_ENTITY_HP = 5653;
    public static final int TARGET_ENTITY_MAX_HP = 5654;

    // Non-cache varbits that we have hardcoded into the server.
    public static final int INFHP = 1;
    public static final int INFPRAY = 2;
    public static final int INFSPEC = 3;
    public static final int INFRUN = 4;
    public static final int XPLOCKED = 5;
    public static final int XP_X1 = 6;
    public static final int KS_SKULLS_HIDDEN = 10;
    public static final int MAXCAPE_ROL_ON = 11;
    public static final int HIDE_KS_BROADCASTS = 13;
    public static final int RING_OF_SUFFERING_RECOIL_DISABLED = 16;
    public static final int ROW_COIN_COLLECTION_OFF = 17;
    public static final int HP_OVERLAY_ENTITY_UID = 18;
    public static final int HP_OVERLAY_TOGGLED = 19;
    public static final int ROW_CU_ITEM_BANKING = 20;
    public static final int HISCORE_BLOCKED = 21;
    public static final int HIDE_DICON = 21;

    public static final int UNLOCK_RIGOUR = 5451;
    public static final int UNLOCK_AUGURY = 5452;
    public static final int UNLOCK_PRESERVE = 5453;

    public static final int ENABLE_UNTRADABLE_LOOT_NOTIFICATIONS_BUTTONS = 5399;
    public static final int UNTRADABLE_LOOT_NOTIFICATIONS = 5402;
    public static final int DROP_ITEM_WARNING = 5411;
    public static final int BOSS_KC_NOTICATION = 4930;
    public static final int LOOT_DROP_THRESHOLD_VALUE = 5400;
    public static final int DROP_ITEMS_WARNING_VALUE = 5412;

    public static final int TINTED_HITSPLATS = 10236;

    public static final int INFERNO_BOSS_ROOF = 5652;
}
