package com.cryptic.clientscripts.constants;

import com.cryptic.model.map.region.RegionManager;

public class ScriptID {
    public static final int GUIDE_PRICE_SEARCH = 750;
    public static final int GUIDE_PRICE_ITEMSLOTS = 785;
    public static final int ADD_AMOUNT_MENU_OPTIONS = 149;
    public static final int WEAPON_INFORMATION_COMBAT_LEVEL = 5224;
    public static final int CHARACTER_SUMMARY_COMBAT_LEVEL = 3954;
    public static final int TIME_PLAYED = 3970;
    public static final int OUT_OF_PRAYER = 5224;
    public static final int SEND_AMOUNT_SCRIPT = 108;
    public static final int TOTAL_GUIDE_PRICE_AMOUNT = 600;
    public static final int WORLD_MAP_POSITION = 1749;
    public static final int HEALTH_HUDE_FADE_IN = 2376;
    public static final int HEALTH_HUDE_FADE = 2498; //TODO naming may not be correct
    public static final int HEALTH_HUDE_FADE_OUT = 2889;
    public static final int IF_SETTEXTALIGN = find("if_settextalign");
    public static final int MESLAYER_CLOSE = find("meslayer_close");
    public static final int SKILL_MULTI_SETUP = find("skillmulti_setup");
    public static final int DUEL_CHECK_BUTTON = -1/*find("duel_check_button")*/; // TODO(update)
    public static final int INTERFACE_INV_INIT = find("interface_inv_init");
    public static final int INTERFACE_INV_INIT_BIG = find("interface_inv_init_big");
    public static final int INTERFACE_INVOTHER_INIT = find("interface_invother_init");
    public static final int TOPLEVEL_MAINMODAL_BACKGROUND = find("toplevel_mainmodal_background");
    public static final int DUEL_OPTIONS_CHANGED = -1/*find("duel_options_changed")*/; // TODO(update)
    public static final int SHOP2_MAIN_INIT = find("shop2_main_init");
    public static final int SHOP2_MAIN_CONFIRM_OPEN = find("shop2_main_confirm_open");
    public static final int SHOP2_MAIN_CONFIRM_CLOSE = find("shop2_main_confirm_close");
    public static final int DUEL_WAIT_BUTTON = -1/*find("duel_wait_button")*/; // TODO(update)
    public static final int DUEL_STAKE_REMAKE_CHANGED = find("duel_stake_remake_changed");
    public static final int DUEL_STAKE_REMAKE_OPPONENTS_INV = find("duel_stake_remake_opponents_inv");
    public static final int DUEL_STAKE_REMAKE_OPPONENTS_WORN = find("duel_stake_remake_opponents_worn");
    public static final int ACHIEVEMENT_DIARY_BUILD = find("achievement_diary_build");
    public static final int ACHIEVEMENT_DIARY_UPDATE = find("achievement_diary_update");
    public static final int ACHIEVEMENTS_LISTING_ADD = find("achievements_listing_add");
    public static final int ACHIEVEMENTS_PROGRESS_BAR = find("achievements_progress_bar");
    public static final int DROPVIEWER_OBJECTS_CLEAR = find("dropviewer_objects_clear");
    public static final int DROPVIEWER_OBJECTS_FINISH = find("dropviewer_objects_finish");
    public static final int DROPVIEWER_OBJECTS_ADD = find("dropviewer_objects_add");
    public static final int DROPVIEWER_SEARCH_RESULTS_INFO = find("dropviewer_npcs_search_results_info");
    public static final int DROPVIEWER_NPCS_SEARCH_RESULTS = find("dropviewer_npcs_search_results");
    public static final int TELEPORT_INIT = find("teleport_init");
    public static final int TELEPORT_CATEGORY_HIGHLIGHT = find("teleport_category_highlight");
    public static final int TELEPORT_TELEPORT_ADD_CHANGE = find("teleport_teleport_add_change");
    public static final int TELEPORT_TELEPORT_FINISH = find("teleport_teleport_finish");
    public static final int TELEPORT_TELEPORT_CLEAR = find("teleport_teleport_clear");
    public static final int TELEPORT_SUBCAT_INIT = find("teleport_subcat_init");
    public static final int ACHIEVEMENTS_LISTING_CLEAR = find("achievements_listing_clear");
    public static final int ACHIEVEMENT_DIARY_PROGRESS = find("achievement_diary_progress");
    public static final int POH_BOARD_ADDLINE = find("poh_board_addline");
    public static final int BANK_SPACE_TOOLTIP = 1495;
    public static final int BANKMAIN_ADDITIONS_PRESET_BUTTON_OPS = find("bankmain_additions_preset_button_ops");
    public static final int MBOX_INIT = find("mbox_init");
    public static final int MBOX_SPINNER_SPIN = find("mbox_spinner_spin");
    public static final int MBOX_STATS = find("mbox_stats");
    public static final int INSTANCE_CREATION_BOSS_LISTING_INIT = find("instance_creation_boss_listing_init");
    public static final int INSTANCE_CREATION_OPTIONS_INIT = find("instance_creation_options_init");
    public static final int GAUNTLET_TIMER_UPDATE = find("gauntlet_timer_update");
    public static final int FADE_OVERLAY = find("fade_overlay");
    public static final int FADE_OVERLAY_LEGACY = find("fade_overlay_legacy");
    public static final int SCROLL_SACK_INIT = find("scroll_sack_init");
    public static final int OPENURL_SILENT = find("openurl_raw_silent");
    public static final int SPECTATOR_SINGLE = find("deadman_spectator_single");
    public static final int TOB_PARTYLIST_ADDLINE = find("tob_partylist_addline");
    public static final int XPDROPS_FAKE = find("xpdrops_fake");
    public static final int XPDROPS_FAKE_NO_ICON = find("xpdrops_fake_noicon");
    public static final int TOURNAMENT_TIME_SETUP = find("tournament_overlay_time_setup");
    public static final int TOURNAMENT_ROUND_SETUP = find("tournament_overlay_round_setup");
    public static final int GRAPHIC_SWAPPER = find("graphic_swapper");

    public static final int TOB_PARTYDETAILS_ADD_MEMBER = find("tob_partydetails_addmember");
    public static final int TOB_PARTYDETAILS_ADD_APPLICANT = find("tob_partydetails_addapplicant");
    public static final int TOB_PARTYDETAILS_INIT = 2323;
    public static final int XERIC_PARTYLIST_ADDLINE = find("raids_partylist_addline");
    public static final int XERIC_PARTYDETAILS_ADD_MEMBER = find("raids_partydetails_addline");
    public static final int XERIC_PARTYDETAILS_SETUP = 1524;

    public static final int PRESETS_LIST_LISTING_INIT = find("presets_list_listing_init");
    public static final int PRESETS_LISTING_INIT = find("presets_listing_init");
    public static final int PRESETS_USAGE_TOOLTIP = find("presets_usage_tooltip");

    public static final int PRESETS_LIST_USAGE_TOOLTIP = find("presets_list_usage_tooltip");
    public static final int PLAYERMEMBER = find("playermember");
    public static final int CHATDEFAULT_RESTOREINPUT = find("chatdefault_restoreinput");
    public static final int CHATDEFAULT_STOPINPUT = find("chatdefault_stopinput");
    public static final int BUGREPORT_INFO = find("bugreport_info");

    public static final int OVERLAY_PORTAL = find("overlay_portal");
    public static final int OVERLAY_PORTAL_REMOVE = find("overlay_portal_remove");
    public static final int GRAVESTONE_DATA = find("gravestone_transmit_data");
    public static final int DEATHKEEP_INIT = find("deathkeep_init");

    public static final int GI_MANAGEMENT_INIT = find("gi_management_init");
    public static final int GI_MANAGEMENT_APPLICANTS_INIT = find("gi_management_applicants_init");

    public static final int GI_STORAGE_HISTORY_INIT = find("gi_storage_history_init");
    public static final int GI_STORAGE_HISTORY_ADDLINE = find("gi_storage_history_addline");
    public static final int GI_STORAGE_HISTORY_FINISH = find("gi_storage_history_finish");

    public static final int GI_TAB_TRANSMIT = find("gi_tab_transmit");

    public static final int HP_HUD_OPEN = find("hp_hud_open");
    public static final int HP_HUD_UPDATE = find("hp_hud_update");
    public static final int HP_HUD_FADE_IN = find("hp_hud_fade_in");
    public static final int HP_HUD_FADE_OUT = find("hp_hud_fade_out");

    public static final int WORLDMAP_TRANSMITDATA = find("worldmap_transmitdata");

    public static final int CATACOMBS_ALTAR_UPDATE = find("cata_altar_update");
    public static final int PVP_STORE_INIT = find("pvp_store_init");
    public static final int MAGIC_SPELLBOOK_LASTTELEPORT = find("magic_spellbook_lastteleport");
    public static final int SCREEN_GLOW_START = find("screen_glow_start");
    public static final int SCREEN_GLOW_END = find("screen_glow_end");
    public static final int NIGHTMARE_TOTEM_HUD_UPDATE = find("nightmare_totem_hud_update");

    public static final int SET_RENDERSELF = find("set_renderself");

    public static final int TOB_HUD_STATUSNAMES = find("tob_hud_statusnames");
    public static final int TOB_HUD_FADE = find("tob_hud_fade");
    public static final int TOB_HUD_PORTAL = find("tob_hud_portal"); //red
    public static final int TOB_HUD_WHITE = 2308; //white

    public static final int TRADINGPOST_INDEX_INIT = find("tradingpost_index_init");
    public static final int TRADINGPOST_INDEX_DRAWSLOT = find("tradingpost_index_drawslot");
    public static final int TRADINGPOST_INDEX_FINISH = find("tradingpost_index_finish");
    public static final int TRADINGPOST_INDEX_COFFER_TRANSMIT = find("tradingpost_index_coffer_transmit");
    public static final int TRADINGPOST_SEARCH_SETPLAYERNAME = find("tradingpost_search_setplayername");
    public static final int TRADINGPOST_HISTORY_ADDLINE = find("tradingpost_history_addline");
    public static final int TRADINGPOST_HISTORY_FINISH = find("tradingpost_history_finish");
    public static final int TRADINGPOST_HISTORY_INIT = find("tradingpost_history_init");

    public static final int GE_SEARCH_ITEMS = find("meslayer_mode14");
    public static final int WILDERNESS_BOSS_DAMAGERS_TRANSMIT = find("wilderness_boss_damagers_transmit");

    public static final int TOURNAMENT_SPECTATE_BUILD = find("tournament_spectate_build");
    public static final int DEADMAN_SPECTATOR_ENABLE = find("deadman_spectator_enable");
    public static final int DEADMAN_SPECTATOR_NAMES = 2179;

    public static final int GODWARS_FADER_INIT = find("godwars_fader_init");

    public static final int LOST_PROPERTY_INIT = find("lost_property_init");

    public static final int NOTIFICATION_DISPLAY_INIT = find("notification_display_init");

    public static final int MESLAYER_MODE100 = find("meslayer_mode100");

//    public static final int DISPLAY_NAME_LOOKUP = find("displayname_lookup");

    public static final int PK_HISCORES_INIT = find("pk_hiscores_init");
    public static final int PK_HISCORES_CLEAR = find("pk_hiscores_clear");
    public static final int PK_HISCORES_ADDLINE = find("pk_hiscores_addline");
    public static final int PK_HISCORES_TIMEUNTILEND = find("pk_hiscores_timeuntilend");
    public static final int PK_HISCORES_NORESULTS = find("pk_hiscores_noresults");

    public static final int CHAT_TIMEDBROADCAST = find("chat_timedbroadcast");

    public static final int REWARD_SHOP_INIT = find("reward_shop_init");
    public static final int ZAROS_REWARD_SHOP_INIT = find("zaros_reward_shop_init");

    public static final int TITLES_INIT = find("titles_init");
    public static final int TITLES_FINISH = find("titles_finish");
    public static final int TITLES_DRAW_TABS = find("titles_draw_tabs");
    public static final int TITLES_DRAW_TITLE = find("titles_draw_title");

    public static final int STATISTICS_ENTRY_SETHOTSPOT = find("statistics_entry_sethotspot");
    public static final int STATISTICS_ENTRY_PLAYER_COUNT_TOOLTIP_TRANSMIT = find("statistics_entry_player_count_tooltip_transmit");

    public static final int CHATBOX_MULTI_INIT = find("chatbox_multi_init");
    public static final int KEYBOARD_SHOW = find("keyboard_show");
    public static final int TUTORIAL_DEFAULT_SETTINGS = find("tutorial_default_settings");

    public static final int GRAPHICBOX_SETBUTTONS = find("graphicbox_setbuttons");
    public static final int GRAPHICBOX_SET_GRAPHIC = find("graphicbox_set_graphic");

    public static final int CONFIRMDESTROY_INIT = find("confirmdestroy_init");

    public static final int DEADMAN_DELAY = find("deadman_delay");
    public static final int DEADMANLOOT_VALUE_UPDATE = find("deadmanloot_value_update");

    public static final int POH_COSTUME_INIT = find("poh_costumes_init");
    public static final int POH_BUILD_MENU_ADD_ENTRY = 1404;
    public static final int POH_BUILT_MENU_FINISH = 1406;

    public static final int PRIF_OVERLAY_PORTAL = find("prif_overlay_portal");
    public static final int PRIF_REMOVE_PORTAL = find("prif_remove_portal");

    public static final int HALLOWED_TIMER_UPDATE = find("hallowed_timer_update");

    public static final int COLLECTION_LOG_ITEM_UPDATE = 4100;
    public static final int COLLECTION_LOG_DRAW_TABS_ALL = find("collection_draw_tabs_all");
    public static final int COLLECTION_LOG_DRAW_LIST = find("collection_draw_list");

    public static final int SETTINGS_CLIENT_MODE = find("settings_client_mode");

    public static final int CLAN_CREATE_SIDE_INIT = find("clan_create_side_init");
    public static final int CLAN_SIDEPANEL_SET_NAME = 4447;
    public static final int CLAN_SIDEPANEL_SET_LEADERS = 4448;

    public static final int COLOUR_PICKER_INIT = 4185;

    public static final int PRESETS_COLORPICKERPOPUP = find("presets_colorpickerpopup");

    public static final int TZHAAR_KET_RAK_SCOREBOARD_INIT = 4023;

    public static final int TEMPOROSS_HUD_UPDATE = find("tempoross_hud_update");
    public static final int TEMPOROSS_WAVE_START = 4068;
    public static final int TEMPOROSS_WAVE_END = 4069;

    public static final int DEADMAN_SPECTATOR_TRANSMIT_NAMES = find("deadman_spectator_transmit_names");
    public static final int DEADMAN_SPECTATOR_TRANSMIT_FIGHTS = find("deadman_spectator_transmit_fights");
    public static final int DEADMAN_SPECTATOR_TRANSMIT_HOTSPOTS = find("deadman_spectator_transmit_hotspots");

    public static final int II_TRACKER_BUILD = find("ii_tracker_build");

    public static final int RR_HUD_UPDATE = find("rr_hud_update");

    public static final int PET_RACE_BUILD = find("pet_race_build");
    public static final int PET_RACE_READY = find("pet_race_ready");
    public static final int PET_RACE_TIMER_SET = find("pet_race_timer_set");

    public static final int EVENT_TAB_INIT = find("event_tab_init");
    public static final int EVENT_TAB_EVENT_SETUP = find("event_tab_event_setup");
    public static final int EVENT_TAB_EVENT_FINISH = find("event_tab_event_finish");
    public static final int EVENT_TAB_EVENT_VIEW = find("event_tab_event_view");

    public static final int EVENT_LIST_CLEAR = find("event_list_clear");
    public static final int EVENT_LIST_ADD = find("event_list_add");
    public static final int EVENT_LIST_FINISH = find("event_list_finish");

    public static final int EVENT_CREATE_INIT = find("event_create_init");

    public static final int CC_DELETEALL = find("cc_deleteall");

    public static final int SUMMARY_SIDEPANEL_DRAW = find("summary_sidepanel_draw");
    public static final int SUMMARY_SIDEPANEL_COMBAT_LEVEL_TRANSMIT = find("summary_sidepanel_combat_level_transmit");
    public static final int SUMMARY_SIDEPANEL_TIME_PLAYED_TRANSMIT = 3970;
    public static final int SUMMARY_SIDEPANEL_ACHIEVEMENTS_TRANSMIT = find("summary_sidepanel_achievements_transmit");
    public static final int SUMMARY_SIDEPANEL_TOWN_BOARD_TRANSMIT = find("summary_sidepanel_town_board_transmit");

    public static final int WANDERING_TRADER_CLEAR = find("wandering_trader_clear");
    public static final int WANDERING_TRADER_ITEM_ADD = find("wandering_trader_item_add");

    public static final int TOWN_BOARD_INIT = find("town_board_init");
    public static final int TOWN_BOARD_META_SYNC = find("town_board_meta_sync");
    public static final int TOWN_BOARD_TASK_ADD = find("town_board_task_add");
    public static final int TOWN_BOARD_TASK_ADD_LOCKED = find("town_board_task_add_locked");
    public static final int TOWN_BOARD_TASK_ADD_COOLDOWN = find("town_board_task_add_cooldown");
    public static final int TOWN_BOARD_FINISH = find("town_board_finish");

    public static final int TOWN_BOARD_LEVEL_INIT = find("town_board_level_init");

    public static final int TOWN_BOARD_TASKS_INIT = find("town_board_tasks_init");
    public static final int TOWN_BOARD_TASKS_TASK_ADD = find("town_board_tasks_task_add");

    public static final int MOLE_GO_SPLAT = find("mole_go_splat");

    public static final int BONUS_INFO_INIT = find("bonus_info_init");
    public static final int BONUS_INFO_ADD = find("bonus_info_add");
    public static final int BONUS_INFO_FINISH = find("bonus_info_finish");
    public static final int BONUS_INFO_ADD_SUB = find("bonus_info_add_sub");
    public static final int BONUS_INFO_ADD_SUB_TEMPORARY = find("bonus_info_add_sub_temporary");

    public static final int PET_SHRINE_INIT = find("pet_shrine_init");

    public static final int PET_SHRINE_OVERVIEW_ADD_PERK = find("pet_shrine_overview_add_perk");

    public static final int PET_SHRINE_PERK_POOL_ADD_PERK = find("pet_shrine_perk_pool_add_perk");

    public static final int PET_SHRINE_ROLL_PERKS_INIT = find("pet_shrine_roll_perks_init");
    public static final int PET_SHRINE_ROLL_PERKS_SET_SLOT = find("pet_shrine_roll_perks_set_slot");
    public static final int PET_SHRINE_ROLL_PERKS_VISUALIZE = find("pet_shrine_roll_perks_visualize");

    public static final int PET_SHRINE_PET_LIST_ADD = find("pet_shrine_pet_list_add");

    public static final int HIGHLIGHT_SCREEN = find("highlight_screen");
    public static final int HIGHLIGHT_TEXTBOX = find("highlight_textbox");

    public static final int GODWARS_DAMAGERS_TRANSMIT = find("godwars_damagers_transmit");

    public static final int BUFF_BAR_INIT = 5929;
    public static final int BUFF_BAR_REBUILD = 5937;

    public static final int BINGO_CELL_DRAW_ITEM = find("bingo_cell_draw_item");
    public static final int BINGO_CELL_DRAW_TEXT = find("bingo_cell_draw_text");

    public static final int BINGO_OVERVIEW_UPDATE_PATTERN = find("bingo_overview_update_pattern");

    public static final int FLOWER_POKER_WAIT_BUTTON = find("flower_poker_wait_button");

    public static final int COLLECTION_LOG_OVERVIEW_INIT = find("collection_log_overview_init");

    public static final int SANCTUM_INIT = find("sanctum_init");
    public static final int SANCTUM_ITEM_UPGRADE_INIT = find("sanctum_item_upgrade_init");
    public static final int SANCTUM_ITEM_UPGRADE_STATS = find("sanctum_item_upgrade_stats");
    public static final int SANCTUM_ITEM_UPGRADE_LANDING_INIT = find("sanctum_item_upgrade_landing_init");

    public static final int EXTRADIMENSIONAL_BAG_SETUP = 3704;

    public static final int POLL_RESULTS_ADDQUESTION_FULL = find("poll_results_addquestion_full");
    public static final int POLL_VOTING_ADDQUESTION_FULL = find("poll_voting_addquestion_full");
    public static final int POLL_VOTING_ADDQUESTION_REFRESH = find("poll_voting_addquestion_refresh");
    public static final int POLL_INITIALISE = find("poll_initialise");
    public static final int POLL_SETBUTTON = find("poll_setbutton");
    public static final int POLL_CONCLUDE = find("poll_conclude");
    public static final int POLL_HISTORY_SETUP = find("poll_history_setup");
    public static final int POLL_ADDLINK = 610;

    public static final int PET_INSURANCE_INIT = find("pet_insurance_init");

    public static final int INFO_BOX_INIT = 4212;

    public static final int TOA_PARTYLIST_ADDLINE = find("toa_partylist_addline");
    public static final int TOA_PARTYDETAILS_INIT = 6729;
    public static final int TOA_PARTYDETAILS_ADDMEMBER = find("toa_partydetails_addmember");
    public static final int TOA_PARTYDETAILS_ADDAPPLICANT = find("toa_partydetails_addapplicant");
    public static final int TOA_SPEEDRUN_TIME_UPDATE = find("toa_speedrun_time_update");
    public static final int TOA_TIME_UPDATE_TIMER = find("toa_time_update_timer");
    public static final int TOA_HUD_STATUSNAMES = find("toa_hud_statusnames");
    public static final int TOA_RAID_SUMMARY_ADD_LINE = 6590;

    public static final int SEQUENCE_PRELOAD = 1846;

    public static final int SETTINGS_SET_SEARCH = 5968;

    public static final int FOUNDRY_SWEET_SPOT = 6122;

    public static final int TRY_YOUR_LUCK_INIT = find("try_your_luck_init");

    public static final int SMITHING_CLOSE = find("smithing_close");
    public static final int CRAFTING_CLOSE = find("crafting_close");

    public static final int STORE_INIT = find("store_init");
    public static final int STORE_ITEMS_INIT = find("store_items_init");
    public static final int STORE_ITEMS_ADD = find("store_items_add");
    public static final int STORE_ITEMS_FINISH = find("store_items_finish");
    public static final int STORE_CART_INIT = find("store_cart_init");
    public static final int STORE_CART_ADD = find("store_cart_add");
    public static final int STORE_CART_FINISH = find("store_cart_finish");
    public static final int STORE_SETLOADING = find("store_setloading");
    public static final int STORE_REMOVELOADING = find("store_removeloading");
    public static final int STORE_USERDATA = find("store_userdata");

    public static final int STORE_SCROLLS_ADD = find("store_scrolls_add");
    public static final int STORE_SCROLLS_SETTOTAL = find("store_scrolls_settotal");
    public static final int STORE_CART_NOTIFICATION = find("store_cart_notification");

    public static final int DOMINION_TOWER_TIMER_SET = find("dominion_tower_timer_set");
    public static final int DOMINION_TOWER_TIMER_START = find("dominion_tower_timer_start");

    public static final int COX_SETUP_INIT = find("cox_setup_init");
    public static final int COX_SETUP_SHOW_LAYOUTS = find("cox_setup_show_layouts");
    public static final int COX_SETUP_CLOSE_LAYOUTS = find("cox_setup_close_layouts");

    public static final int ADMIN_CONSOLE_STATS_INIT = find("admin_console_stats_init");
    public static final int ADMIN_CONSOLE_CYCLE_GRAPH_INIT = find("admin_console_cycle_graph_init");

    public static final int VOTE_INIT = find("vote_init");
    public static final int VOTE_SITE_ADD = find("vote_site_add");
    public static final int VOTE_REWARD_BUTTON = find("vote_rewards_button");
    public static final int VOTE_LEADERBOARDS_SET = find("vote_leaderboards_set");

    public static final int BROADCAST_INIT = find("broadcast_init");

    public static final int COLLECTION_SECTION_REWARDS = find("collection_section_rewards");

    public static final int PVP_LEAGUES_INIT = find("pvp_leagues_init");

    /**
     * Method used just to load the class on startup.
     */
    public static void init() {
    }

    /**
     * Finds a clientscript by its name.
     *
     * @param name The scripts name.
     * @return The scripts id.
     */
    private static int find(String name) {
        var clientscripts = RegionManager.cache.index(12).archiveId("[clientscript," + name + "]");
        if (clientscripts == -1) {
            throw new RuntimeException("unable to find [clientscript," + name + "]");
        }
        return clientscripts;
    }
}
