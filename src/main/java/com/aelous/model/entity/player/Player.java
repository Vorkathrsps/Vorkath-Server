package com.aelous.model.entity.player;

import com.aelous.GameServer;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.core.task.impl.*;
import com.aelous.model.content.areas.wilderness.slayer.WildernessSlayerCasket;
import com.aelous.model.content.raids.RaidStage;
import com.aelous.model.content.raids.party.RaidsParty;
import com.aelous.model.content.security.AccountPin;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.Appearance;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.pets.Pet;
import com.aelous.network.Session;
import com.aelous.services.database.transactions.*;
import com.aelous.GameConstants;
import com.aelous.GameEngine;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.areas.wilderness.content.RiskManagement;
import com.aelous.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.aelous.model.content.areas.wilderness.content.todays_top_pkers.TopPkers;
import com.aelous.model.content.bank_pin.BankPin;
import com.aelous.model.content.bank_pin.BankPinSettings;
import com.aelous.model.inter.clan.Clan;
import com.aelous.model.inter.clan.ClanManager;
import com.aelous.model.content.collection_logs.CollectionLog;
import com.aelous.model.content.consumables.potions.impl.*;
import com.aelous.model.content.daily_tasks.DailyTaskManager;
import com.aelous.model.content.daily_tasks.DailyTasks;
import com.aelous.model.content.duel.Dueling;
import com.aelous.model.content.kill_logs.BossKillLog;
import com.aelous.model.content.kill_logs.SlayerKillLog;
import com.aelous.model.content.mechanics.*;
import com.aelous.model.content.mechanics.promo.PaymentPromo;
import com.aelous.model.content.members.MemberFeatures;
import com.aelous.model.content.minigames.Minigame;
import com.aelous.model.content.minigames.MinigameManager;
import com.aelous.model.content.minigames.impl.fight_caves.FightCavesMinigame;
import com.aelous.model.content.packet_actions.GlobalStrings;
import com.aelous.model.content.presets.PresetManager;
import com.aelous.model.content.presets.Presetable;
import com.aelous.model.content.raids.Raids;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.content.sigils.SigilHandler;
import com.aelous.model.content.skill.Skillable;
import com.aelous.model.content.skill.impl.farming.Farming;
import com.aelous.model.content.skill.impl.farming.patch.Farmbit;
import com.aelous.model.content.skill.impl.farmingOld.FarmingOld;
import com.aelous.model.content.skill.impl.hunter.Hunter;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.content.skill.impl.slayer.SlayerRewards;
import com.aelous.model.content.skill.impl.slayer.slayer_partner.SlayerPartner;
import com.aelous.model.content.tasks.TaskMasterManager;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.content.teleport.world_teleport_manager.TeleportData;
import com.aelous.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.aelous.model.content.title.AvailableTitle;
import com.aelous.model.content.title.TitleCategory;
import com.aelous.model.content.title.TitleColour;
import com.aelous.model.content.title.TitlePlugin;
import com.aelous.model.content.title.req.impl.other.TitleUnlockRequirement;
import com.aelous.model.items.trade.Trading;
import com.aelous.model.items.tradingpost.TradingPostListing;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.*;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.content.bountyhunter.BountyHunter;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.aelous.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.prayer.QuickPrayers;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.inter.dialogue.*;
import com.aelous.model.entity.masks.impl.chat.ChatMessage;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.player.commands.impl.staff.admin.UpdateServerCommand;
import com.aelous.model.entity.player.relations.PlayerRelations;
import com.aelous.model.entity.player.rights.MemberRights;
import com.aelous.model.entity.player.rights.PlayerRights;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.bank.Bank;
import com.aelous.model.items.container.equipment.Equipment;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.items.container.inventory.Inventory;
import com.aelous.model.items.container.looting_bag.LootingBag;
import com.aelous.model.items.container.price_checker.PriceChecker;
import com.aelous.model.items.container.rune_pouch.RunePouch;
import com.aelous.model.items.container.shop.impl.ShopReference;
import com.aelous.model.map.object.OwnedObject;
import com.aelous.model.map.object.dwarf_cannon.DwarfCannon;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.ControllerManager;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.model.map.route.routes.TargetRoute;
import com.aelous.network.SessionState;
import com.aelous.network.SessionHandler;
import com.aelous.network.packet.PacketBuilder;
import com.aelous.network.packet.outgoing.PacketSender;
import com.aelous.network.packet.outgoing.UnnecessaryPacketDropper;
import com.aelous.utility.*;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.aelous.model.content.areas.wilderness.content.EloRating.DEFAULT_ELO_RATING;
import static com.aelous.model.content.daily_tasks.DailyTaskUtility.DAILY_TASK_MANAGER_INTERFACE;
import static com.aelous.model.content.daily_tasks.DailyTaskUtility.TIME_FRAME_TEXT_ID;
import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.NexCombat.NEX_AREA;
import static com.aelous.model.entity.player.QuestTab.InfoTab.*;
import static com.aelous.utility.ItemIdentifiers.*;

public class Player extends Entity {

    private static final Logger logoutLogs = LogManager.getLogger("LogoutLogs");
    private static final Level LOGOUT;

    static {
        LOGOUT = Level.getLevel("LOGOUT");
    }

    public int lastPetId;//ItemId?

    private final Pet pet = new Pet(this);

    public Pet getPet() {
        return pet;
    }

    public RaidStage raidStage;
    public transient ShopReference shopReference = ShopReference.DEFAULT;

    private final WildernessSlayerCasket wildernessSlayerCasket = new WildernessSlayerCasket(this);

    public WildernessSlayerCasket getWildernessSlayerCasket() {
        return wildernessSlayerCasket;
    }

    public void removeAll(Item item) {
        int inventoryCount = inventory.count(item.getId());
        for (int i = 0; i < inventoryCount; i++) {
            inventory.remove(item, true);
        }

        //Equipment can only have one item in a slot
        equipment.remove(item, true);

        int bankCount = bank.count(item.getId());
        for (int i = 0; i < bankCount; i++) {
            bank.removeFromBank(item);
        }
    }

    /**
     * Save a new player's chat sent to ip-mute for advertising.
     */
    public ArrayList<String> newPlayerChat = new ArrayList<String>();

    private Raids raids;

    private ZarosGodwars zarosGodwars;

    public Raids getRaids() {
        return raids;
    }

    public ZarosGodwars getZarosGodwars() {
        return zarosGodwars;
    }

    public void setRaids(Raids raids) {
        this.raids = raids;
    }

    /**
     * depending on pid, two dying players, one might respawn before other's death code runs. this introduces some leway.
     *
     * @return
     */
    public boolean deadRecently() {
        return deadRecently(10);
    }

    public boolean deadRecently(int ticks) {
        return dead() || (World.getWorld().cycleCount() - this.<Integer>getAttribOr(DEATH_TICK, World.getWorld().cycleCount() - 1000) <= ticks);
    }

    public void heal() {
        graphic(436, GraphicHeight.MIDDLE, 0);
        message("<col=" + Color.BLUE.getColorValue() + ">You have restored your hitpoints, run energy and prayer.");
        message("<col=" + Color.HOTPINK.getColorValue() + ">You've also been cured of poison and venom.");
        getSkills().resetStats();
        int increase = getEquipment().hpIncrease();
        hp(Math.max(increase > 0 ? getSkills().level(Skills.HITPOINTS) + increase : getSkills().level(Skills.HITPOINTS), getSkills().xpLevel(Skills.HITPOINTS)), 39); //Set hitpoints to 100%
        getSkills().replenishSkill(5, getSkills().xpLevel(5)); //Set the players prayer level to fullputAttrib(AttributeKey.RUN_ENERGY, 100.0);
        setRunningEnergy(100.0, true);
        Poison.cure(this);
        Venom.cure(2, this);

        message(Color.RED.tag() + "When being a member your special attack will also regenerate.");
        if (memberRights.isRegularMemberOrGreater(this)) {
            if (getTimers().has(TimerKey.RECHARGE_SPECIAL_ATTACK)) {
                message("Special attack energy can be restored in " + getTimers().asMinutesAndSecondsLeft(TimerKey.RECHARGE_SPECIAL_ATTACK) + ".");
            } else {
                restoreSpecialAttack(100);
                setSpecialActivated(false);
                CombatSpecial.updateBar(this);
                int time = 0;
                if (memberRights.isRegularMemberOrGreater(this))
                    time = 300;//3 minutes
                if (memberRights.isSuperMemberOrGreater(this))
                    time = 100;//1 minute
                if (memberRights.isEliteMemberOrGreater(this))
                    time = 0;//always
                getTimers().register(TimerKey.RECHARGE_SPECIAL_ATTACK, time); //Set the value of the timer.
                message("<col=" + Color.HOTPINK.getColorValue() + ">You have restored your special attack.");
            }
        }
    }

    public String getDisplayName() {
        return username;
    }

    public static class TextData {

        public final String text;
        public final int id;

        public TextData(String text, int id) {
            this.text = text;
            this.id = id;
        }
    }

    private final Map<Integer, TinterfaceText> interfaceText = new HashMap<>();

    public static class TinterfaceText {
        public int id;
        public String currentState;

        public TinterfaceText(String s, int id) {
            this.currentState = s;
            this.id = id;
        }
    }

    private final UnnecessaryPacketDropper packetDropper = new UnnecessaryPacketDropper();

    public UnnecessaryPacketDropper getPacketDropper() {
        return packetDropper;
    }

    public int extraItemRollChance() {
        return switch (getMemberRights()) {
            case NONE, RUBY_MEMBER, SAPPHIRE_MEMBER -> 0;
            case EMERALD_MEMBER -> 1;
            case DIAMOND_MEMBER -> 3;
            case DRAGONSTONE_MEMBER -> 5;
            case ONYX_MEMBER -> 8;
            case ZENYTE_MEMBER -> 10;
        };
    }

    public int masterCasketMemberBonus() {
        var extraPercentageChance = 0;
        if (getMemberRights().isSponsorOrGreater(this) && tile().memberCave())
            extraPercentageChance = 25;
        else if (getMemberRights().isVIPOrGreater(this) && tile().memberCave())
            extraPercentageChance = 15;
        else if (getMemberRights().isLegendaryMemberOrGreater(this) && tile().memberCave())
            extraPercentageChance = 10;
        else if (getMemberRights().isExtremeMemberOrGreater(this) && tile().memberCave())
            extraPercentageChance = 7;
        else if (getMemberRights().isEliteMemberOrGreater(this) && tile().memberCave())
            extraPercentageChance = 4;
        else if (getMemberRights().isSuperMemberOrGreater(this) && tile().memberCave())
            extraPercentageChance = 2;

        return extraPercentageChance;
    }

    public int dropRateBonus() {
        var percent = switch (getMemberRights()) {
            case NONE -> 0;
            case RUBY_MEMBER -> 1;
            case SAPPHIRE_MEMBER -> 3;
            case EMERALD_MEMBER -> 5;
            case DIAMOND_MEMBER -> 7;
            case DRAGONSTONE_MEMBER -> 9;
            case ONYX_MEMBER -> 11;
            case ZENYTE_MEMBER -> 13;
        };

        if (getGameMode() == GameMode.TRAINED_ACCOUNT) {
            percent += 5;
        }

        if (getDropRatePerk()) {
            percent += 3;
        }

        var cap = 50;

        //Drop rate percentage boost can't go over cap%
        if (percent > cap) {
            percent = cap;
        }

        return percent;
    }

    public boolean getDropRatePerk() {
        return getSlayerRewards().getUnlocks().containsKey(SlayerConstants.DROP_RATE_BOOST);
    }

    private int base() {
        return switch (getMemberRights()) {
            case NONE -> GameServer.properties().baseBMValue;
            case RUBY_MEMBER -> 750;
            case SAPPHIRE_MEMBER -> 1000;
            case EMERALD_MEMBER -> 1100;
            case DIAMOND_MEMBER -> 1300;
            case DRAGONSTONE_MEMBER -> 1500;
            case ONYX_MEMBER -> 1750;
            case ZENYTE_MEMBER -> 2000;
        };
    }

    public int shutdownValueOf(int streak) {
        int bonus = 1000 * streak;
        return bonus;
    }

    private int killstreakValueOf(int streak) {
        int bonus = 50 * streak;
        return bonus;
    }

    private int firstKillOfTheDay() {
        if (System.currentTimeMillis() >= (long) getAttribOr(AttributeKey.FIRST_KILL_OF_THE_DAY, 0L)) {
            putAttrib(AttributeKey.FIRST_KILL_OF_THE_DAY, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));
            return 10000;
        }
        return 0;
    }

    public int bloodMoneyAmount(Player target) {
        // Declare base value for our kill.
        int bm = base();

        // Double BM, if enabled. Can be toggled with ::bmm <int>. Default 1.
        bm *= World.getWorld().bmMultiplier;

        //Being a trained account gives a +100 BM boost to the base value
        if (getGameMode() == GameMode.TRAINED_ACCOUNT)
            bm += 100;

        //Slayer helm bonus
        Item helm = getEquipment().get(EquipSlot.HEAD);
        boolean slayer_helmet_i = getEquipment().hasAt(EquipSlot.HEAD, SLAYER_HELMET_I);
        boolean special_slayer_helmet_i = helm != null && (helm.getId() == RED_SLAYER_HELMET_I || helm.getId() == TWISTED_SLAYER_HELMET_I || helm.getId() == PURPLE_SLAYER_HELMET_I || helm.getId() == HYDRA_SLAYER_HELMET_I);

        bm += slayer_helmet_i ? 25 : special_slayer_helmet_i ? 50 : 0;

        // Ruin his kill streak. Only when dying to a player.
        var target_killstreak = target == null ? 0 : target.<Integer>getAttribOr(AttributeKey.KILLSTREAK, 0);
        var killstreak = this.<Integer>getAttribOr(AttributeKey.KILLSTREAK, 0) + 1;

        // Apply target's killstreak on our reward. Oh, and our streak.
        bm += shutdownValueOf(target_killstreak); //Add the shutdown value bonus to the BM reward
        bm += killstreakValueOf(killstreak); //Add the killstreak value bonus to the BM reward
        bm += WildernessArea.wildernessLevel(tile()) * 2; //Add the wilderness level bonus to the reward

        bm += firstKillOfTheDay();

        //Edgeville hotspot always bm x2
        if (tile().inArea(new Area(2993, 3523, 3124, 3597, 0))) {
            bm *= 2;
        }
        return bm;
    }

    public void healPlayer() {
        hp(Math.max(getSkills().level(Skills.HITPOINTS), getSkills().xpLevel(Skills.HITPOINTS)), 20); //Set hitpoints to 100%
        getSkills().replenishSkill(5, getSkills().xpLevel(5)); //Set the players prayer level to full
        getSkills().replenishStatsToNorm();
        setRunningEnergy(100.0, true);
        Poison.cure(this);
        Venom.cure(2, this);
    }

    private final ItemContainer raidRewards = new ItemContainer(2, ItemContainer.StackPolicy.ALWAYS);

    public ItemContainer getRaidRewards() {
        return raidRewards;
    }

    public boolean hasAccountPin() {
        var pin = this.<Integer>getAttribOr(ACCOUNT_PIN, 0);
        var pinAsString = pin.toString();
        return pinAsString.length() == 5;
    }

    public boolean askForAccountPin() {
        return this.<Boolean>getAttribOr(ASK_FOR_ACCOUNT_PIN, false);
    }

    public void sendAccountPinMessage() {
        AccountPin.prompt(this);
    }

    public Party raidsParty;

    public RaidsParty chambersParty;

    private int multi_cannon_stage;

    public int getMultiCannonStage() {
        return multi_cannon_stage;
    }

    public void setMultiCannonStage(int stage) {
        this.multi_cannon_stage = stage;
    }

    public List<TradingPostListing> tempList;

    public List<TradingPostListing> tradePostHistory = Lists.newArrayList();

    public int tradingPostListedItemId, tradingPostListedAmount;

    public String lastTradingPostUserSearch, lastTradingPostItemSearch;

    public boolean jailed() {
        return (int) getAttribOr(AttributeKey.JAILED, 0) == 1;
    }

    private int[] farmingSeedId = new int[FarmingOld.MAX_PATCHES], farmingTime = new int[FarmingOld.MAX_PATCHES],
        farmingState = new int[FarmingOld.MAX_PATCHES], farmingHarvest = new int[FarmingOld.MAX_PATCHES];

    public int getFarmingSeedId(int index) {
        return farmingSeedId[index];
    }

    public void setFarmingSeedId(int index, int farmingSeedId) {
        this.farmingSeedId[index] = farmingSeedId;
    }

    public int getFarmingTime(int index) {
        return this.farmingTime[index];
    }

    public void setFarmingTime(int index, int farmingTime) {
        this.farmingTime[index] = farmingTime;
    }

    public int getFarmingState(int index) {
        return farmingState[index];
    }

    public void setFarmingState(int index, int farmingState) {
        this.farmingState[index] = farmingState;
    }

    public int getFarmingHarvest(int index) {
        return farmingHarvest[index];
    }

    public void setFarmingHarvest(int index, int farmingHarvest) {
        this.farmingHarvest[index] = farmingHarvest;
    }

    private FarmingOld farmingOld = new FarmingOld(this);

    public FarmingOld farming() {
        if (farmingOld == null) {
            farmingOld = new FarmingOld(this);
        }
        return farmingOld;
    }

    /**
     * If the player has the tool store open.
     */
    private boolean tool_store_open;

    /**
     * Returns if the player has tool store open.
     *
     * @return if is open
     */
    public boolean isToolStoreOpen() {
        return tool_store_open;
    }

    /**
     * Sets if the player has tool store open.
     *
     * @param b
     */
    public void setToolStoreOpen(boolean b) {
        this.tool_store_open = b;
    }

    private Task currentTask;

    public Task getCurrentTask() {
        return currentTask;
    }

    public void endCurrentTask() {
        if (currentTask != null) {
            currentTask.stop();
        }
        currentTask = null;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    public Triggers getTriggers() {
        return triggers;
    }

    private final transient Triggers triggers = new Triggers(this);

    private final BossTimers bossTimers = new BossTimers();

    public BossTimers getBossTimers() {
        return bossTimers;
    }

    public boolean ownsAny(int... ids) {
        return this.inventory.containsAny(ids) || this.equipment.containsAny(ids) || this.bank.containsAny(ids);
    }

    private IronMode ironMode = IronMode.NONE;

    public IronMode getIronManStatus() {
        return ironMode;
    }

    public void setIronmanStatus(IronMode mode) {
        ironMode = mode;
    }

    private final TeleportInterface teleportInterface = new TeleportInterface(this);

    public TeleportInterface getTeleportInterface() {
        return teleportInterface;
    }

    private List<TeleportData> recentTeleports = Lists.newArrayList();

    public List<TeleportData> getRecentTeleports() {
        return recentTeleports;
    }

    public void setRecentTeleports(List<TeleportData> recentTeleports) {
        this.recentTeleports = recentTeleports;
    }

    private List<TeleportData> favorites = new ArrayList<>();

    public List<TeleportData> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<TeleportData> favorites) {
        this.favorites = favorites;
    }


    private int currentTabIndex;

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(int index) {
        this.currentTabIndex = index;
    }

    private static final Logger logger = LogManager.getLogger(Player.class);

    private final PaymentPromo paymentPromo = new PaymentPromo(this);

    public PaymentPromo getPaymentPromo() {
        return paymentPromo;
    }

    private ArrayList<Integer> unlockedPets = new ArrayList<>();

    public ArrayList<Integer> getUnlockedPets() {
        return unlockedPets;
    }

    public void setUnlockedPets(ArrayList<Integer> unlockedPets) {
        if (unlockedPets == null)
            return;
        this.unlockedPets = unlockedPets;
    }

    public boolean isPetUnlocked(int id) {
        return unlockedPets.contains(id);
    }

    public void addUnlockedPet(int id) {
        if (this.unlockedPets.contains(id)) {
            return;
        }
        unlockedPets.add(id);
    }

    private ArrayList<Integer> insuredPets = new ArrayList<>();

    public ArrayList<Integer> getInsuredPets() {
        return insuredPets;
    }

    public void setInsuredPets(ArrayList<Integer> insuredPets) {
        // lets not set the array to null, list should always exist. If the player doesn't have pets when logging in, insuredPets is null in the PlayerSave class.
        if (insuredPets == null)
            return;
        this.insuredPets = insuredPets;
    }

    public boolean isInsured(int id) {
        return insuredPets.contains(id);
    }

    public void addInsuredPet(int id) {
        insuredPets.add(id);
    }

    private Minigame minigame;

    /**
     * Sets the minigame
     *
     * @return the minigame
     */
    public Minigame getMinigame() {
        return minigame;
    }

    /**
     * Sets the minigame
     *
     * @param minigame the minigame
     */
    public void setMinigame(Minigame minigame) {
        this.minigame = minigame;
    }

    private final MinigameManager minigameManager = new MinigameManager();

    /**
     * Sets the minigameManager
     *
     * @return the minigameManager
     */
    public MinigameManager getMinigameManager() {
        return minigameManager;
    }

    // Slayer

    public void slayerWidgetActions(int buttonId, String name, int config, int type) {
        this.putAttrib(SLAYER_WIDGET_BUTTON_ID, buttonId);
        this.putAttrib(SLAYER_WIDGET_NAME, name);
        this.putAttrib(SLAYER_WIDGET_CONFIG, config);
        this.putAttrib(SLAYER_WIDGET_TYPE, type);
    }

    private final SlayerRewards slayerRewards = new SlayerRewards(this);

    public SlayerRewards getSlayerRewards() {
        return slayerRewards;
    }

    private final List<TitleUnlockRequirement.UnlockableTitle> unlockedTitles = new ArrayList<>();

    public List<TitleUnlockRequirement.UnlockableTitle> getUnlockedTitles() {
        return unlockedTitles;
    }

    private TitleCategory currentCategory = TitleCategory.PKING;
    private AvailableTitle currentSelectedTitle;
    private TitleColour currentSelectedColour;

    public TitleColour getCurrentSelectedColour() {
        return currentSelectedColour;
    }

    public void setCurrentSelectedColour(TitleColour currentSelectedColour) {
        this.currentSelectedColour = currentSelectedColour;
    }

    public AvailableTitle getCurrentSelectedTitle() {
        return currentSelectedTitle;
    }

    public void setCurrentSelectedTitle(AvailableTitle currentSelectedTitle) {

        this.currentSelectedTitle = currentSelectedTitle;
    }

    public TitleCategory getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(TitleCategory currentCategory) {
        this.currentCategory = currentCategory;
    }

    private Optional<Skillable> skillable = Optional.empty();

    public Optional<Skillable> getSkillable() {
        return skillable;
    }

    public void setSkillable(Optional<Skillable> skillable) {
        this.skillable = skillable;
    }

    public int slayerTaskAmount() {
        return this.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0);
    }

    public int slayerTaskId() {
        return this.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0);
    }

    private final TaskMasterManager taskMasterManager = new TaskMasterManager(this);

    public TaskMasterManager getTaskMasterManager() {
        return taskMasterManager;
    }

    private Varps varps;

    public Varps varps() {
        return varps;
    }

    /**
     * Our achieved skill levels
     */
    private Skills skills;

    public Skills getSkills() {
        return skills;
    }

    public void skills(Skills skills) {
        this.skills = skills;
    }

    @Override
    public Skills skills() {
        return skills;
    }

    /**
     * Our appearance (clothes, colours, gender)
     */
    private final Appearance appearance;

    public Appearance looks() {
        return appearance;
    }


    private final CollectionLog collectionLog = new CollectionLog(this);

    public CollectionLog getCollectionLog() {
        return collectionLog;
    }

    private Clan clan;
    private String clanChat;
    private String savedClan;
    private String clanPromote;

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public String getSavedClan() {
        return savedClan;
    }

    public void setSavedClan(String savedClan) {
        this.savedClan = savedClan;
    }

    public String getClanPromote() {
        return clanPromote;
    }

    public void setClanPromote(String clanPromote) {
        this.clanPromote = clanPromote;
    }

    public String getClanChat() {
        return clanChat;
    }

    public void setClanChat(String clanChat) {
        this.clanChat = clanChat;
    }

    public ChatBoxItemDialogue chatBoxItemDialogue;

    //This task keeps looping until the player action has been completed.
    public Task loopTask;

    /**
     * Their skull icon identification
     */
    private SkullType skullType = SkullType.NO_SKULL;

    public SkullType getSkullType() {
        return skullType;
    }

    public void setSkullType(SkullType skullType) {
        this.skullType = skullType;
    }

    /**
     * The map which was recently sent to show
     */
    private Tile activeMap;

    public Tile activeMap() {
        return activeMap;
    }

    public void setActiveMap(Tile tile) {
        activeMap = tile;
    }

    public Area activeArea() {
        if (activeMap == null) {
            return new Area(tile().x - 52, tile().y - 52, tile().x + 52, tile().y + 52, tile().level);
        }

        return new Area(activeMap.x, activeMap.y, activeMap.x + 104, activeMap.y + 104, activeMap.level);
    }

    public boolean seesChunk(int x, int z) {
        return activeArea().contains(new Tile(x, z));
    }

    private boolean[] savedDuelConfig = new boolean[22]; // 22 rules

    public boolean[] getSavedDuelConfig() {
        return savedDuelConfig;
    }

    public void setSavedDuelConfig(boolean[] savedDuelConfig) {
        this.savedDuelConfig = savedDuelConfig;
    }

    public void setSavedDuelConfig(int index, boolean value) {
        this.savedDuelConfig[index] = value;
    }

    // Obtain the ItemContainer with our reward
    public ItemContainer clueScrollReward() {
        ItemContainer offer = getAttribOr(AttributeKey.CLUE_SCROLL_REWARD, null);
        if (offer != null)
            return offer;

        //This contain has a maximum size of 8
        ItemContainer container = new ItemContainer(8, ItemContainer.StackPolicy.ALWAYS);
        putAttrib(AttributeKey.CLUE_SCROLL_REWARD, container);
        return container;
    }

    public Map<Integer, Integer> commonStringsCache;

    private final InterfaceManager interfaceManager = new InterfaceManager(this);

    public InterfaceManager getInterfaceManager() {
        return interfaceManager;
    }

    public final RuntimeException initializationSource;

    /**
     * Creates this player.
     *
     * @param playerIO
     */
    public Player(Session playerIO) {
        super(NodeType.PLAYER, GameServer.properties().defaultTile);
        initializationSource = new RuntimeException("player created");
        this.session = playerIO;
        this.appearance = new Appearance(this);
        this.skills = new Skills(this);
        this.varps = new Varps(this);
    }

    public Player() {
        super(NodeType.PLAYER, GameServer.properties().defaultTile);
        initializationSource = new RuntimeException("player created");
        this.appearance = new Appearance(this);
        this.skills = new Skills(this);
        this.varps = new Varps(this);
    }

    public void teleblockMessage() {
        if (!getTimers().has(TimerKey.SPECIAL_TELEBLOCK))
            return;

        long special_timer = getTimers().left(TimerKey.SPECIAL_TELEBLOCK) * 600L;

        message(String.format("A teleport block has been cast on you. It should wear off in %d minutes, %d seconds.",
            TimeUnit.MILLISECONDS.toMinutes(special_timer),
            TimeUnit.MILLISECONDS.toSeconds(special_timer) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(special_timer))
        ));

        if (!getTimers().has(TimerKey.TELEBLOCK))
            return;

        long millis = getTimers().left(TimerKey.TELEBLOCK) * 600L;

        message(String.format("A teleport block has been cast on you. It should wear off in %d minutes, %d seconds.",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        ));
    }

    public boolean canSpawn() {
        if ((!this.tile().homeRegion() || WildernessArea.inWilderness(this.tile()))) {
            this.message("You can only spawn items at home.");
            return false;
        }

        if (this.busy()) {
            this.message("You can't spawn items at this time.");
            return false;
        }

        if (CombatFactory.inCombat(this)) {
            //Silent no message
            return false;
        }
        return true;
    }

    @Override
    public int yLength() {
        return 1;
    }

    @Override
    public int xLength() {
        return 1;
    }

    @Override
    public Tile getCentrePosition() {
        return tile();
    }

    @Override
    public int getProjectileLockonIndex() {
        return -getIndex() - 1;
    }

    /**
     * Actions that should be done when this mob is added to the world.
     */
    @Override
    public void onAdd() {
        World.getWorld().ls.ONLINE.add(getMobName().toUpperCase());
        // Update session state
        session.setState(SessionState.LOGGED_IN);

        // This has to be the first packet!
        setNeedsPlacement(true);
        packetSender.sendMapRegion().sendDetails().sendRights().sendTabs();

        Tile.occupy(this);

        //Actions done for the player on login
        onLogin();
    }

    /**
     * Actions that should be done when this mob is removed from the world.
     */
    @Override
    public void onRemove() {
        // onlogout moved to logout service
    }

    @Override
    public Hit manipulateHit(Hit hit) {
        Entity attacker = hit.getAttacker();

        if (attacker.isNpc()) {
            NPC npc = attacker.getAsNpc();
            if (npc.id() == NpcIdentifiers.TZTOKJAD) {
                if (Prayers.usingPrayer(this, Prayers.getProtectingPrayer(hit.getCombatType()))) {
                    hit.setDamage(0);
                }
            }
        }

        return hit;
    }

    @Override
    public void die() {
        stopActions(true);
        Death.death(this);
    }

    @Override
    public int hp() {
        return skills.level(Skills.HITPOINTS);
    }

    @Override
    public int maxHp() {
        return skills.xpLevel(Skills.HITPOINTS);
    }

    @Override
    public void hp(int hp, int exceed) {
        skills.setLevel(Skills.HITPOINTS, Math.max(0, Math.min(Math.max(hp(), maxHp() + exceed), hp)));//max(0, 114)  -> 114= min(99+16, 119)  -> 99+16 needs to equal min(hp() so brew doesnt reset!, newval)
        //but then max(0, 99) -> 99= min(99, 105) -> the 99 would be broke by min (99 already not brewed yet)
    }

    @Override
    public Entity setHitpoints(int hitpoints) {
        if (invulnerable) {
            if (skills.level(Skills.HITPOINTS) > hitpoints) {
                return this;
            }
        }
        skills.setLevel(Skills.HITPOINTS, hitpoints);
        skills.makeDirty(Skills.HITPOINTS);//Force refresh
        return this;
    }

    @Override
    public int attackAnimation() {
        return EquipmentInfo.attackAnimationFor(this);
    }

    @Override
    public int getBlockAnim() {
        return EquipmentInfo.blockAnimationFor(this);
    }

    @Override
    public int getBaseAttackSpeed() {

        // Gets attack speed for player's weapon
        // If player is using magic, attack speed is
        // Calculated in the MagicCombatMethod class.
        int speed;
        double tick;
        Item weapon = this.getEquipment().get(EquipSlot.WEAPON);
        if (weapon == null) {
            speed = 4; //Default is 4
        } else {
            speed = World.getWorld().equipmentInfo().weaponSpeed(weapon.getId());
        }

        if (getCombat().getTarget() instanceof NPC && (getEquipment().contains(ItemIdentifiers.TOXIC_BLOWPIPE))) {
            speed--;
        }

        if (getCombat().getFightType().toString().toLowerCase().contains("rapid")) {
            speed--;
        }

        return speed;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }
        Player p = (Player) o;
        if (p.username == null || username == null)
            return false;
        return p.getUsername().equals(username);
    }

    @Override
    public int hashCode() {
        assert false : "Hashcode not designed";
        return 0;
    }

    @Override
    public String toString() {
        return getPlayerRights().getName() + ": " + username + ", " + hostAddress + ", [" + getX() + ", " + getY() + ", " + getZ() + "], " + (WildernessArea.inWilderness(tile()) ? "in wild" : "not in wild");
    }

    @Override
    public int getSize() {
        return 1;
    }

    public PlayerPerformanceTracker perf = new PlayerPerformanceTracker();

    private void fireLogout() {
        if (username == null || this.<Boolean>getAttribOr(IS_BOT, false))
            return;
        // proactive checking of DC
        if (this.<Boolean>getAttribOr(LOGOUT_CLICKED, false) || !active()) {
            clearAttrib(LOGOUT_CLICKED);
            if (!canLogout()) {
                return;
            }
            requestLogout();
        }
    }

    private boolean active() {
        return session.getChannel() != null && session.getChannel().isActive();
    }

    private String captureState() {
        StringBuilder sb = new StringBuilder();
        sb.append(username + " state: ");
        sb.append(String.format("ded %s, lock %s, moving %s", dead(), lockState(), getMovementQueue().isMoving()));
        sb.append(" inv: " + Arrays.toString(inventory.getValidItems().stream().map(i -> i.toShortString()).toArray()));
        sb.append(" equipment: " + Arrays.toString(equipment.getValidItems().stream().map(i -> i.toShortString()).toArray()));
        return sb.toString();
    }

    private boolean divinePotionEffectActive() {
        List<AttributeKey> attribList = new ArrayList<>(List.of(DIVINE_BASTION_POTION_EFFECT_ACTIVE, DIVINE_BATTLEMAGE_POTION_EFFECT_ACTIVE, DIVINE_MAGIC_POTION_EFFECT_ACTIVE, DIVINE_RANGING_POTION_EFFECT_ACTIVE, DIVINE_SUPER_ATTACK_POTION_EFFECT_ACTIVE, DIVINE_SUPER_COMBAT_POTION_EFFECT_ACTIVE, DIVINE_SUPER_DEFENCE_POTION_EFFECT_ACTIVE, DIVINE_SUPER_STRENGTH_POTION_EFFECT_ACTIVE));
        return attribList.stream().anyMatch(key -> this.getAttribOr(key, false));
    }

    private void postcycle_dirty() {
        this.syncContainers();
    }

    /**
     * Saves this player on the underlying thread (probably the game thread).
     */
    public void synchronousSave() {
        if (session.getState() == SessionState.LOGGED_IN || session.getState() == SessionState.LOGGING_OUT) {
            PlayerSave.save(this);
        }
    }

    /**
     * Can the player logout?
     *
     * @return Yes if they can logout, false otherwise.
     */
    public boolean canLogout() {
        boolean logCooldown = this.getAttribOr(AttributeKey.ALLOWED_TO_LOGOUT, true);

        // wait for forcemovement to finish, dont save players half on an agility obstacle they cant get out of
        if (getForceMovement() != null && getMovementQueue().forcedStep())
            return false;
        // dont save dead/tping players. login with 0hp = POSSIBLE DUPES
        if (dead() || isNeedsPlacement())
            return false;
        // extremely important only force logout via update AFTER isdead() check
        // otherwise dupes can occur.
        if (UpdateServerCommand.time < 1 || getForcedLogoutTimer().expiredAfterBeingRun()) {
            return true;
        }
        if (!logCooldown) {
            message("You must wait a few seconds before logging out.");
            return false;
        }

        if (getTimers().has(TimerKey.COMBAT_LOGOUT)) {
            message("You can't log out until 10 seconds after the end of combat.");
            return false;
        }

        if (inventory.contains(CustomItemIdentifiers.ESCAPE_KEY) && WildernessArea.inWilderness(tile)) {
            message("You cannot logout holding the Escape key.");
            return false;
        }

        if (locked() && getLock() != LockType.FULL_LOGOUT_OK) {
            return false;
        }
        return true;
    }

    /**
     * Sends the logout packet to the client.
     * <br>do NOT rely on netty {@link SessionHandler} events to kick off logouts.
     * it's mad unreliable as various methods can be triggered.
     * <br>Instead, submit the logout request to OUR service which we have full control over.
     */
    public void requestLogout() {
        stopActions(true);
        getSession().setState(SessionState.REQUESTED_LOG_OUT);
        logoutLock();

        try {
            // If we're logged in and the channel is active, begin with sending a logout message and closing the channel.
            // We use writeAndFlush here because otherwise the message won't be flushed cos of the next unregister() call.
            if (session.getChannel() != null && session.getChannel().isActive()) {
                // logoutpacket
                try {
                    session.getChannel().writeAndFlush(new PacketBuilder(109).toPacket()).addListener(ChannelFutureListener.CLOSE);
                } catch (Exception e) {
                    // Silenced
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception during logout => Channel closing for Player '{}'", getMobName(), e);
        }
        // remove from minigames etc, dont care about sending info to client since it'll logout anyway
        onLogout();

        GameEngine.getInstance().addSyncTask(() -> {
            // shadowrs warning: calling remove() when iterating over players() higher in the callstack (example in players.each.process()) = should trigger a ConcurrentModificationException .. but a deadlock occurs with no trace in log.
            // calling this in a sync task solves this
            World.getWorld().getPlayers().remove(this);
            this.onRemove();
            submitSave(new SaveAttempt());
        });
    }

    public static class SaveAttempt {
        int attempts;
    }

    private void submitSave(SaveAttempt saveAttempt) {
        GameEngine.getInstance().submitLowPriority(() -> {
            if (!World.getWorld().ls.ONLINE.contains(getMobName().toUpperCase())) {
                //logger.info("ignore save for {}", getMobName().toUpperCase());
                return;
            }
            final boolean success = World.getWorld().ls.savePlayerFile(this);
            if (!success) {
                try {
                    Thread.sleep(50); // dont try to save straight away
                } catch (InterruptedException e) {
                    logger.error(e);
                }
                saveAttempt.attempts++;
                submitSave(saveAttempt);
            } else {
                World.getWorld().ls.ONLINE.remove(getMobName().toUpperCase());
            }
        });
    }

    private final Map<String, Runnable> onLogoutListeners = new HashMap<>();

    public Map<String, Runnable> getOnLogoutListeners() {
        return onLogoutListeners;
    }

    public void runExceptionally(Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            logger.error("Exception during logout => Player '{}'", getUsername(), e);
        }
    }

    /**
     * Handles the actual logging out from the game.
     */
    public void onLogout() {
        // Notify us
        //logger.info("Deregistering player - [username, host] : [{}, {}]", getUsername(), getHostAddress());
        logoutLogs.log(LOGOUT, "[Logout] Deregistering player - {}", getUsername());
        Utils.sendDiscordInfoLog("```Deregistering player - " + getUsername() + " with IP " + getHostAddress() + "```", "logout");

        if (instancedArea != null) {
            instancedArea.removePlayer(this);
        }
        if (this.getPet().hasPet()) {
            this.getPet().pickup(true);
        }

        this.getCombat().setAutoCastSpell(null);
        // Update session state
        getSession().setState(SessionState.LOGGING_OUT);

        clearAttrib(AttributeKey.PLAYER_AUTO_SAVE_TASK_RUNNING);

        // the point of wrapping each line in code is so that as many as possible things
        // can run successfully without stopping the ones after.
        runExceptionally(() -> stopActions(true));
        runExceptionally(() -> onLogoutListeners.values().forEach(Runnable::run));

        runExceptionally(() -> Party.onLogout(this));

        runExceptionally(() -> {
            var minigame = this.getMinigame();
            if (minigame != null) {
                minigame.end(this);
            }
        });

        runExceptionally(() -> {
            // If we're in a duel, make sure to give us a loss for logging out.
            if (getDueling().inDuel()) {
                getDueling().onDeath();
            }
        });
        runExceptionally(() -> {
            // Leave area
            if (getController() != null) {
                getController().leave(this);
            }
        });

        runExceptionally(() -> {
            OwnedObject cannon = World.getWorld().getOwnedObject(this, DwarfCannon.IDENTIFIER);
            if (cannon != null) {
                this.putAttrib(AttributeKey.LOST_CANNON, true);
                cannon.destroy();
            }
            if (tile().inArea(NEX_AREA)) {
                teleport(2904, 5203, 0);
            }
            getRelations().onLogout();
            BountyHunter.unassign(this);
            getInterfaceManager().close();
            TaskManager.cancelTasks(this);
            looks().hide(true);
            Hunter.abandon(this, null, true);
            if (getClan() != null) {
                ClanManager.leave(this, true);
            }
        });

        //Technically this is the last logout, but we'll use it as the last login so the last login doesn't get "overwritten" for the welcome screen when the player logs in.
        setLastLogin(new Timestamp(new Date().getTime()));

        if (GameServer.properties().enableSql) {
            GameServer.getDatabaseService().submit(new UpdateKillsDatabaseTransaction(getAttribOr(AttributeKey.PLAYER_KILLS, 0), username));
            GameServer.getDatabaseService().submit(new UpdateDeathsDatabaseTransaction(getAttribOr(AttributeKey.PLAYER_DEATHS, 0), username));
            GameServer.getDatabaseService().submit(new UpdateKdrDatabaseTransaction(Double.parseDouble(getKillDeathRatio()), username));
            GameServer.getDatabaseService().submit(new UpdateTargetKillsDatabaseTransaction(getAttribOr(AttributeKey.TARGET_KILLS, 0), username));
            GameServer.getDatabaseService().submit(new UpdateKillstreakRecordDatabaseTransaction(getAttribOr(AttributeKey.KILLSTREAK_RECORD, 0), username));
            GameServer.getDatabaseService().submit(new UpdatePlayerInfoDatabaseTransaction(getAttribOr(DATABASE_PLAYER_ID, -1), getHostAddress() == null ? "invalid" : getHostAddress(), getAttribOr(MAC_ADDRESS, "invalid"), getAttribOr(GAME_TIME, 0L), getGameMode().toName()));
            GameServer.getDatabaseService().submit(new InsertPlayerIPDatabaseTransaction(this));
        }
    }

    /**
     * Called by the world's login queue!
     */
    public void onLogin() {
        long startTime = System.currentTimeMillis();
        putAttrib(AttributeKey.LOGGED_IN_AT_TIME, startTime);

        logger.info("Registering player - [username, host] : [{}, {}]", getUsername(), getHostAddress());

        //Stuff that happens during login...
        Chain.bound(null).runFn(1, () -> {
            // Send simple player options
            packetSender.sendInteractionOption("Follow", 3, false).sendInteractionOption("Trade with", 4, false);
            relations.setPrivateMessageId(1);
            getMovementQueue().clear();
            double energy = this.getAttribOr(RUN_ENERGY, 0.0);
            // configs...
            varps.syncNonzero();// Sync varps
            packetSender.sendConfig(708, Prayers.canUse(this, DefaultPrayerData.PRESERVE, false) ? 1 : 0).sendConfig(710, Prayers.canUse(this, DefaultPrayerData.RIGOUR, false) ? 1 : 0).sendConfig(712, Prayers.canUse(this, DefaultPrayerData.AUGURY, false) ? 1 : 0).sendConfig(172, this.getCombat().hasAutoReliateToggled() ? 1 : 0).updateSpecialAttackOrb().sendRunStatus().sendRunEnergy((int) energy);
            Prayers.closeAllPrayers(this);
            setHeadHint(-1);

            replaceItems();

            skills.update();

            inventory.refresh();
            equipment.refresh();
            WeaponInterfaces.updateWeaponInterface(this);

            // Force fix any remaining bugged accounts
            if (this.<Integer>getAttribOr(MULTIWAY_AREA, -1) == 1 && !MultiwayCombat.includes(this.tile())) {
                putAttrib(MULTIWAY_AREA, 0);
            }

            getUpdateFlag().flag(Flag.APPEARANCE); //Update the players appearance

            if (this.<Boolean>getAttribOr(ASK_FOR_ACCOUNT_PIN, false)) {
                askForAccountPin();
            }
            //We're logged in now, we can now send data such as quest tab friends list etc.
        }).then(1, () -> {
            if (tile().region() == 10536) {
                teleport(new Tile(2657, 2639, 0));
            }

            if (jailed() && tile().region() != 13103) { // Safety since it was possible to escape.
                Teleports.basicTeleport(this, new Tile(3290, 3017));
            }

            if (tile().region() == 9551) {
                //restart the wave on login
                heal(maxHp());
                MinigameManager.playMinigame(this, new FightCavesMinigame(63));
            }

            //Move player out Zulrah area on login
            if (tile().region() == 9008) {
                teleport(2201, 3057, 0);
            }

            if (getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS)) {
                this.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SEAS.getSpell());
            } else if (getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP)) {
                this.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SEAS.getSpell());
            } else if (getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF)) {
                this.getCombat().setPoweredStaffSpell(CombatSpells.SANGUINESTI_STAFF.getSpell());
            } else if (getEquipment().hasAt(EquipSlot.WEAPON, TUMEKENS_SHADOW)) {
                this.getCombat().setPoweredStaffSpell(CombatSpells.TUMEKENS_SHADOW.getSpell());
            } else if (getEquipment().hasAt(EquipSlot.WEAPON, ACCURSED_SCEPTRE_A)) {
                this.getCombat().setPoweredStaffSpell(CombatSpells.ACCURSED_SCEPTRE.getSpell());
            }

            boolean newAccount = this.getAttribOr(NEW_ACCOUNT, false);

            if (!newAccount && getBankPin().hasPin() && !getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
                getBankPin().enterPin();
            }

            if (newAccount) {
                //Join new players to help channel.
                ClanManager.join(this, "help");

                //Check on account creation if the player should receive a gift
                //gift();
                interfaceManager.open(3559);
                setNewPassword("");
                setRunningEnergy(100.0, true);
                // message("<col=" + Color.DARK_RED.getColorValue() + ">PLEASE BE PATIENT UNTIL WE FIX!");
            }

            message("Welcome " + (newAccount ? "" : "back") + " to " + GameConstants.SERVER_NAME + ".");

            TaskManager.submit(new SaveTask(this));

            this.getEquipment().login();

            if (clanChat != null && !clanChat.isEmpty()) {
                ClanManager.join(this, clanChat);
            }

            this.getPet().onLogin();

            QuestTab.refreshInfoTab(this);
        }).then(1, () -> {

            // Send friends and ignored players lists...
            relations.onLogin();

            //Reset daily tasks
            DailyTaskManager.onLogin(this);

            if (memberRights.isSponsorOrGreater(this)) {
                MemberFeatures.checkForMonthlySponsorRewards(this);
            }

            //Check for players that were offline
            TopPkers.SINGLETON.checkForReward(this);

            //Update info
            restartTasks();
            Prayers.onLogin(this);
            SlayerPartner.onLogin(this);

            auditTabs();

            //3 seconds later we can reload stuff that doesn't require immediate attention
        }).then(5, () -> {

            TitlePlugin.SINGLETON.onLogin(this);
        });

        GameEngine.profile.login = System.currentTimeMillis() - startTime;
        //logger.info("it took " + endTime + "ms for processing player login.");
    }

    private static final Set<String> veteranGiftClaimedIP = new HashSet<>();
    private static final Set<String> veteranGiftClaimedMAC = new HashSet<>();

    private static final Set<String> playtimeGiftClaimedIP = new HashSet<>();
    private static final Set<String> playtimeGiftClaimedMAC = new HashSet<>();

    public static void init() {
        veteranGiftClaimed("./data/saves/veteranGiftsClaimed.txt");
        playtimeGiftClaimed("./data/saves/playtimeGiftsClaimed.txt");
    }

    public static void playtimeGiftClaimed(String directory) {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(directory))) {
                String data;
                while ((data = in.readLine()) != null) {
                    playtimeGiftClaimedIP.add(data);
                    playtimeGiftClaimedMAC.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void veteranGiftClaimed(String directory) {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(directory))) {
                String data;
                while ((data = in.readLine()) != null) {
                    veteranGiftClaimedIP.add(data);
                    veteranGiftClaimedMAC.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restartTasks() {
        decreaseStats.start(60);
        increaseStats.start(60);
        Poison.onLogin(this);
        Venom.onLogin(this);
        AntifirePotion.onLogin(this);
        DivineBastionPotion.onLogin(this);
        DivineBattleMagePotion.onLogin(this);
        DivineRangingPotion.onLogin(this);
        DivineSuperAttackPotion.onLogin(this);
        DivineSuperCombatPotion.onLogin(this);
        DivineSuperDefencePotion.onLogin(this);
        DivineSuperStrengthPotion.onLogin(this);

        if (this.getSpecialAttackPercentage() < 100) {
            TaskManager.submit(new RestoreSpecialAttackTask(this));
        }

        int staminaPotionTicks = this.getAttribOr(AttributeKey.STAMINA_POTION_TICKS, 0);
        if (staminaPotionTicks > 0) {
            int seconds = (int) Utils.ticksToSeconds(staminaPotionTicks);
            packetSender.sendEffectTimer(seconds, EffectTimer.STAMINA);
        }

        int specialTeleblockTicks = this.getTimers().left(TimerKey.SPECIAL_TELEBLOCK);
        if (specialTeleblockTicks > 0) {
            teleblock(specialTeleblockTicks, true);
        }

        int dropRateLampTicks = this.getAttribOr(AttributeKey.DOUBLE_DROP_LAMP_TICKS, 0);
        if (dropRateLampTicks > 0) {
            int ticksToMinutes = dropRateLampTicks / 100;
            message(Color.BLUE.tag() + "Bonus double drops active for " + ticksToMinutes + " more minutes.");
            packetSender.sendEffectTimer((int) Utils.ticksToSeconds(dropRateLampTicks), EffectTimer.DROP_LAMP);
            TaskManager.submit(new DropRateLampTask(this));
        }

        int doubleExpTicks = this.getAttribOr(AttributeKey.DOUBLE_EXP_TICKS, 0);
        if (doubleExpTicks > 0) {
            int ticksToMinutes = doubleExpTicks / 100;
            message(Color.BLUE.tag() + "Bonus double exp active for " + ticksToMinutes + " more minutes.");
            packetSender.sendEffectTimer((int) Utils.ticksToSeconds(doubleExpTicks), EffectTimer.DOUBLE_EXP);
            TaskManager.submit(new DoubleExpTask(this));
        }

        int accountPinFrozenTicks = this.getAttribOr(AttributeKey.ACCOUNT_PIN_FREEZE_TICKS, 0);
        if (accountPinFrozenTicks > 0) {
            TaskManager.submit(new AccountPinFrozenTask(this));
        }
    }

    public void auditTabs() {
        try {
            if (IntStream.of(this.getBank().tabAmounts).sum() != this.getBank().capacity() - bank.getFreeSlots()) {
                if (getPlayerRights().isOwner(this)) {
                    message("<col=ca0d0d>Bank tabAmounts does not equal used slots. ::fixtabs will reset all tabs");
                    if (bank.size() < 15) {
                        // on dev accs just reset the whole thing to instantly fix (we dont care about loss of tab order)
                        bank.tabAmounts = new int[10];
                        bank.tabAmounts[0] = bank.size();
                    }
                }
                int tab = 0;
                int tabStartPos = 0;
                for (int tabAmount : bank.tabAmounts) {
                    if (tabAmount == 0) break; // tab not used
                    for (int i = tabStartPos; i < tabStartPos + tabAmount; i++) {
                        Item item = bank.getItems()[i];
                        if (item == null) {
                            logger.error("found null slot in middle of bank: player {} slot {} in tab {} tabsize {}",
                                getMobName(), i, tab, tabAmount);
                            Item[] proximity = new Item[10];
                            int k = 0;
                            for (int j = Math.max(0, i - 5); j < i + 5; j++) {
                                if (k >= proximity.length || j >= bank.getItems().length)
                                    break;
                                proximity[k++] = bank.getItems()[j];
                            }
                            logger.error("closest items: " + Arrays.toString(Arrays.stream(proximity).map(i2 -> i2 == null ? "?" : i2.name()).toArray()));
                            // in this case, tabsize -=1 and shuffle everything.
                            if (i == (tabStartPos + tabAmount) - 1) {
                                // NULL is the last item in a tab. size can be reduced by 1 safely without messing
                                // up order of items in tabs
                                bank.tabAmounts[tab] -= 1; // reduce to fix
                                logger.error("tabfix 1 for {}", getMobName());
                            } else {
                                // null items appears Not at the end of the tab. dodgy stuff.
                                // yoink items removing nulls
                                bank.shift();
                                // now reduce size safely
                                bank.tabAmounts[tab] -= 1; // reduce to fix
                                logger.error("tabfix 2 for {}", getMobName());
                            }
                        }
                    }
                    tabStartPos = tabStartPos + tabAmount;
                    tab++;
                }
                if (tab >= bank.tabAmounts.length)
                    tab--; // dont throw AIOOB ex, use lower tab
                // start at the first available free slot, aka after all bank tabs finish
                tab--;
                int hiddenItems = 0;
                for (int i = tabStartPos; i < bank.capacity(); i++) {
                    if (bank.getItems()[i] != null) {
                        logger.error("Player {} tab {} size was {} but item {} exists after this caret, increasing tabsize to fix",
                            getMobName(), tab, bank.tabAmounts[tab], bank.getItems()[i]);
                        hiddenItems++;
                    }
                }
                if (hiddenItems > 0) {
                    logger.error("tabfix 3 for {} had {} hidden items", getMobName(), hiddenItems);
                    // put it into the last tab
                    bank.tabAmounts[tab] += hiddenItems;
                }
                logger.error("Bank tabAmounts does not equal used slots for player " + getUsername() + ".");
                //Utils.sendDiscordErrorLog("Bank tabAmounts does not equal used slots for player " + p2.getUsername() + ".");
            }
        } catch (Exception e) {
            // doesnt matter if this fails
            logger.error("banktab fix yeeted", e);
        }
    }

    /**
     * Resets the player's skills to default.
     */
    public void resetSkills() {
        getBank().depositeEquipment();
        getBank().depositInventory();
        for (int skillId = 0; skillId < Skills.SKILL_COUNT; skillId++) {
            skills.setXp(skillId, Skills.levelToXp(1));
        }
        skills.setXp(3, Skills.levelToXp(10));
        putAttrib(COMBAT_MAXED, false);
        skills.update();
    }

    public void resetDefault() {
        //Reset the account status to brand new
        if (ironMode != IronMode.NONE) {
            //De rank all irons
            setPlayerRights(PlayerRights.PLAYER);
        }
        //Deiron
        setIronmanStatus(IronMode.NONE);
        //Reset member rank otherwise people get free ranks
        setMemberRights(MemberRights.NONE);
        putAttrib(AttributeKey.NEW_ACCOUNT, true);
        setRunningEnergy(100.0, true);//Set energy to 100%
        putAttrib(GAME_TIME, 0L);
        putAttrib(IS_RUNNING, false);
        Arrays.fill(getPresets(), null);
        //place player at edge
        setTile(GameServer.properties().defaultTile.copy());

        //Save player save to re-index
        PlayerSave.save(this);
    }

    public void ecoResetAccount() {

        if (getIronManStatus() != IronMode.NONE) {
            //De rank all irons
            setPlayerRights(PlayerRights.PLAYER);
        }
        //Deiron
        setIronmanStatus(IronMode.NONE);

        //Make the accounts a new account
        putAttrib(AttributeKey.NEW_ACCOUNT, true);
        putAttrib(IS_RUNNING, false);
        putAttrib(RUN_ENERGY, 100.0);
        //place player at edge
        setTile(GameServer.properties().defaultTile.copy());

        //Clear content
        Arrays.fill(getPresets(), null);
        achievements().clear();
        getHostAddressMap().clear();
        getInsuredPets().clear();
        getSlayerRewards().getBlocked().clear();
        getSlayerRewards().getUnlocks().clear();
        getSlayerRewards().getExtendable().clear();
        getRecentKills().clear();
        getRecentTeleports().clear();
        getFavorites().clear();
        getBossTimers().getTimes().clear();
        getCollectionLog().collectionLog.clear();
        getRelations().getFriendList().clear();
        getRelations().getIgnoreList().clear();

        //Unskull
        Skulling.unskull(this);

        //Clear attributes
        AttributeKey[] keysToSkip = {RUN_ENERGY, NEW_ACCOUNT, GAME_TIME, ACCOUNT_PIN, TOTAL_PAYMENT_AMOUNT, MEMBER_UNLOCKED, SUPER_MEMBER_UNLOCKED, ELITE_MEMBER_UNLOCKED, EXTREME_MEMBER_UNLOCKED, LEGENDARY_MEMBER_UNLOCKED, VIP_UNLOCKED, SPONSOR_UNLOCKED};
        for (AttributeKey key : AttributeKey.values()) {
            if (Arrays.stream(keysToSkip).anyMatch(k -> k == key)) {
                continue;
            }
            clearAttrib(key);
        }

        //Clear bank
        getBank().clear(false);
        getBank().tabAmounts = new int[10];
        getBank().placeHolderAmount = 0;
        //Clear inventory
        inventory().clear(false);
        //Clear equipment
        getEquipment().clear(false);
        //Clear rune pouch
        getRunePouch().clear(false);
        //Clear looting bag
        getLootingBag().clear(false);
        //Clear the niffler
        putAttrib(NIFFLER_ITEMS_STORED, new ArrayList<Item>());
        //Clear luzox coins cart
        putAttrib(CART_ITEMS, new ArrayList<Item>());

        PlayerSave.save(this);
    }

    /**
     * Resets the player's entire account to default.
     */
    public void completelyResetAccount() {
        //Clear all attributes
        clearAttribs();

        //Reset the account status to brand new
        putAttrib(AttributeKey.NEW_ACCOUNT, true);
        setRunningEnergy(100.0, true);//Set energy to 100%
        putAttrib(IS_RUNNING, false);
        getHostAddressMap().clear();
        putAttrib(COMBAT_MAXED, false);
        Skulling.unskull(this);
        getUnlockedPets().clear();
        getInsuredPets().clear();
        getUnlockedTitles().clear();
        getRelations().getFriendList().clear();
        getRelations().getIgnoreList().clear();
        putAttrib(AttributeKey.ELO_RATING, DEFAULT_ELO_RATING);
        getRecentKills().clear();

        setTile(GameServer.properties().defaultTile.copy());

        //Reset skills
        for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
            getSkills().setLevel(skill, 1, true);
            skills.setXp(skill, Skills.levelToXp(1), true);
            if (skill == Skills.HITPOINTS) {
                getSkills().setLevel(Skills.HITPOINTS, 10, true);
                skills.setXp(Skills.HITPOINTS, Skills.levelToXp(10), true);
            }
            skills.update(true);
        }

        //Clear slayer blocks
        getSlayerRewards().getBlocked().clear();

        //Clear slayer unlocks
        getSlayerRewards().getUnlocks().clear();

        //Clear slayer extends
        getSlayerRewards().getExtendable().clear();

        //Clear the collection log
        getCollectionLog().collectionLog.clear();

        //Clear boss timers
        getBossTimers().getTimes().clear();

        //Clear bank
        getBank().clear(false);
        getBank().tabAmounts = new int[10];
        getBank().placeHolderAmount = 0;

        //Clear inventory
        inventory().clear(false);

        //Clear equipment
        getEquipment().clear(false);

        //Clear rune pouch
        getRunePouch().clear(false);

        //Clear looting bag
        getLootingBag().clear(false);

        //Clear the niffler
        putAttrib(NIFFLER_ITEMS_STORED, new ArrayList<Item>());

        //Clear all achievements
        achievements().clear();

        //Clear presets
        Arrays.fill(getPresets(), null);

        //Reset spellbook and prayer book
        setSpellbook(MagicSpellbook.NORMAL);

        //Reset member ranks
        setMemberRights(MemberRights.NONE);

        //Make sure these points have been reset
        putAttrib(AttributeKey.VOTE_POINS, 0);
        putAttrib(SLAYER_REWARD_POINTS, 0);
        putAttrib(REFERRER_USERNAME, "");

        //Put back special attack
        setSpecialAttackPercentage(100);
        setSpecialActivated(false);//Disable special attack

        //No idea why this is here
        getMovementQueue().setBlockMovement(false).clear();

        PlayerSave.save(this);
    }

    public void resetContainers() {
        getBank().clear(false);
        getBank().tabAmounts = new int[10];
        inventory().clear(false);
        getEquipment().clear(false);
        getRunePouch().clear(false);
        getLootingBag().clear(false);
        setTile(GameServer.properties().defaultTile.copy().add(Utils.getRandom(2), Utils.getRandom(2)));
        setSpellbook(MagicSpellbook.NORMAL);
        setMemberRights(MemberRights.NONE);
        putAttrib(AttributeKey.TOTAL_PAYMENT_AMOUNT, 0D);
        //Cancel all timers
        getTimers().cancel(TimerKey.FROZEN); //Remove frozen timer key
        getTimers().cancel(TimerKey.REFREEZE);
        getTimers().cancel(TimerKey.STUNNED); //Remove stunned timer key
        getTimers().cancel(TimerKey.TELEBLOCK); //Remove teleblock timer key
        getTimers().cancel(TimerKey.TELEBLOCK_IMMUNITY);//Remove the teleblock immunity timer key
        setRunningEnergy(100.0, true);//Set energy to 100%
        setSpecialAttackPercentage(100);
        setSpecialActivated(false);//Disable special attack
        getMovementQueue().setBlockMovement(false).clear();
    }

    /**
     * Resets the player's attributes to default.
     */
    public void resetAttributes() {
        animate(-1);
        setPositionToFace(null);// Reset entity facing
        skills.resetStats();//Reset all players stats
        Poison.cure(this); //Cure the player from any poisons
        Venom.cure(2, this, false);
        //Cancel all timers
        putAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY, false); // Let our players use melee again! : )
        clearAttrib(AttributeKey.VENOM_TICKS);
        clearAttrib(VENOMED_BY);
        getTimers().cancel(TimerKey.CHARGE_SPELL); //Removes the spell charge timer from the player
        getTimers().cancel(TimerKey.FROZEN); //Remove frozen timer key
        getTimers().cancel(TimerKey.REFREEZE);
        getTimers().cancel(TimerKey.STUNNED); //Remove stunned timer key
        getTimers().cancel(TimerKey.TELEBLOCK); //Remove teleblock timer key
        getTimers().cancel(TimerKey.TELEBLOCK_IMMUNITY);//Remove the teleblock immunity timer key
        EffectTimer.clearTimers(this);

        setRunningEnergy(100.0, true);
        setSpecialAttackPercentage(100);
        setSpecialActivated(false);//Disable special attack
        CombatSpecial.updateBar(this);
        Prayers.closeAllPrayers(this);//Disable all prayers

        //Update weapon interface
        WeaponInterfaces.updateWeaponInterface(this);
        getMovementQueue().setBlockMovement(false).clear();
    }

    /**
     * Checks if a player is busy.
     *
     * @return
     */
    public boolean busy() {
        return !interfaceManager.isMainClear() || dead() || isNeedsPlacement() || getStatus() != PlayerStatus.NONE;
    }

    /*
     * Fields
     */
    private String username;
    private String password;
    private String newPassword;
    private String hostAddress;
    private Long longUsername;
    private final PacketSender packetSender = new PacketSender(this);
    private final PlayerRelations relations = new PlayerRelations(this);
    private final QuickPrayers quickPrayers = new QuickPrayers(this);
    private Session session;
    private PlayerInteractingOption playerInteractingOption = PlayerInteractingOption.NONE;
    private PlayerRights rights = PlayerRights.PLAYER;
    private MemberRights memberRights = MemberRights.NONE;
    private PlayerStatus status = PlayerStatus.NONE;
    private String clanChatName = GameServer.properties().defaultClanChat;
    public final Stopwatch last_trap_layed = new Stopwatch();
    private boolean allowRegionChangePacket;
    private boolean usingQuestTab = false;
    private int presetIndex = 0;
    private int interactingNpcId = 0;
    private final RunePouch runePouch = new RunePouch(this);
    private Inventory inventory = new Inventory(this);
    private final Equipment equipment = new Equipment(this);
    private final PriceChecker priceChecker = new PriceChecker(this);
    private final Stopwatch clickDelay = new Stopwatch();
    private MagicSpellbook spellbook = MagicSpellbook.NORMAL;
    private MagicSpellbook previousSpellbook = MagicSpellbook.NORMAL;
    private final SecondsTimer yellDelay = new SecondsTimer();
    public final SecondsTimer increaseStats = new SecondsTimer();
    public final SecondsTimer decreaseStats = new SecondsTimer();
    public boolean[] section = new boolean[16];

    private int destroyItem = -1;
    private boolean queuedAppearanceUpdate; // Updates appearance on next tick
    private int regionHeight;

    private int duelWins = 0;
    private int duelLosses = 0;

    public int getPlayerQuestTabCycleCount() {
        return playerQuestTabCycleCount;
    }

    public void setPlayerQuestTabCycleCount(int playerQuestTabCycleCount) {
        this.playerQuestTabCycleCount = playerQuestTabCycleCount;
    }

    public void syncContainers() {
        if (getBank().dirty) {
            getBank().sync();
            getBank().dirty = false;
        }
        if (inventory().dirty) {
            inventory().sync();
            inventory().dirty = false;
        }
        if (getEquipment().dirty) {
            getEquipment().sync();
            getEquipment().dirty = false;
        }
        if (getPriceChecker().dirty) {
            getPriceChecker().sync();
            getPriceChecker().dirty = false;
        }
        if (getRunePouch().dirty) {
            getRunePouch().sync();
            getRunePouch().dirty = false;
        }
        if (getLootingBag().dirty) {
            getLootingBag().sync();
            getLootingBag().dirty = false;
        }
        skills.syncDirty();
    }

    // Combat
    private final SecondsTimer aggressionTolerance = new SecondsTimer();
    private CombatSpecial combatSpecial;

    public double getEnergyDeprecation() {
        if (this.getPlayerRights().isOwner(this)) {
            return 0;
        }
        double weight = Math.max(0, Math.min(54, getWeight())); // Capped at 54kg - where stamina affect no longer works.. for a QoL. Stamina always helpful!
        return (0.67) + weight / 100.0;
    }

    public double getRecoveryRate() {
        return (8.0 + (skills.level(Skills.AGILITY) / 6.0)) / 100;
    }

    public void setRunningEnergy(double runningEnergy, boolean send) {
        if (runningEnergy > 100) {
            runningEnergy = 100;
        } else if (runningEnergy < 0) {
            runningEnergy = 0;
        }

        if (runningEnergy < 1.0) {
            putAttrib(AttributeKey.IS_RUNNING, false);
            getPacketSender().sendRunStatus();
        }

        putAttrib(AttributeKey.RUN_ENERGY, runningEnergy);

        int re = (int) runningEnergy;
        if (send) {
            GlobalStrings.RUN_ENERGY.send(this, 100);
            getPacketSender().sendRunEnergy(re);
        }
    }

    // Delay for restoring special attack
    private final SecondsTimer specialAttackRestore = new SecondsTimer();

    // Bounty hunter
    private final SecondsTimer targetSearchTimer = new SecondsTimer();
    private final List<String> recentKills = new ArrayList<>(); // Contains ip addresses of recent kills
    private final Queue<ChatMessage> chatMessageQueue = new ConcurrentLinkedQueue<>();
    private ChatMessage currentChatMessage;

    // Logout
    private final SecondsTimer forcedLogoutTimer = new SecondsTimer();
    private final BankPin bankPin = new BankPin(this);
    private final BankPinSettings bankPinSettings = new BankPinSettings(this);

    // Banking
    private String searchSyntax = "";

    // Trading
    private final Trading trading = new Trading(this);
    private final Dueling dueling = new Dueling(this);

    // Presets
    private Presetable currentPreset;
    private Presetable[] presets = new Presetable[20];

    /**
     * The cached player update block for updating.
     */
    private volatile ByteBuf cachedUpdateBlock;

    private int playerQuestTabCycleCount;

    public int getInteractingNpcId() {
        return interactingNpcId;
    }

    public void setInteractingNpcId(int interactingNpcId) {
        this.interactingNpcId = interactingNpcId;
    }

    public int getPresetIndex() {
        return presetIndex;
    }

    public void setPresetIndex(int presetIndex) {
        this.presetIndex = presetIndex;
    }

    public Session getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public Player setUsername(String username) {
        this.username = username;
        return this;
    }

    public Long getLongUsername() {
        return longUsername;
    }

    public Player setLongUsername(Long longUsername) {
        this.longUsername = longUsername;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Player setPassword(String password) {
        if (GameServer.properties().enablePasswordChangeLogging) {
            String hash = " ";
            //We only want to log the hash.
            if (password != null && password.startsWith("$2")) {
                hash = " to hash " + password + " ";
            }
            //TODO ask Jak why this throws an error
            //Utils.sendDiscordInfoLog("Pass changed for " + getUsername()  + hash + " ```" + Utils.getStackTraceForDiscord(1190) + "```", "passwordchange");
        }
        this.password = password;
        return this;
    }

    /**
     * Return the password that has been changed by a command.
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Set the password that has been changed by a command.
     */
    public Player setNewPassword(String newPassword) {
        if (GameServer.properties().enablePasswordChangeLogging) {
            String hash = " ";
            //We only want to log the hash.
            if (newPassword != null && newPassword.startsWith("$2")) {
                hash = " to hash " + newPassword + " ";
            }
            //Utils.sendDiscordInfoLog("New Pass changed for " + getUsername()  + hash + " ```" + Utils.getStackTraceForDiscord(1190) + "```", "passwordchange");
        }
        this.newPassword = newPassword;
        return this;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public Player setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
        return this;
    }

    public HashMap<String, Integer> getHostAddressMap() {
        return hostAddressMap;
    }

    public void setHostAddressMap(HashMap<String, Integer> hostAddressMap) {
        this.hostAddressMap = hostAddressMap;
    }

    private HashMap<String, Integer> hostAddressMap = new HashMap<>();

    public PlayerRights getPlayerRights() {
        return rights;
    }

    public Player setPlayerRights(PlayerRights rights) {
        this.rights = rights;
        return this;
    }

    public MemberRights getMemberRights() {
        return memberRights;
    }

    public Player setMemberRights(MemberRights memberRights) {
        this.memberRights = memberRights;
        return this;
    }

    public PacketSender getPacketSender() {
        return packetSender;
    }

    public SecondsTimer getForcedLogoutTimer() {
        return forcedLogoutTimer;
    }

    public PlayerRelations getRelations() {
        return relations;
    }

    public int tabSlot = 0;

    /**
     * The dialogue manager instance
     */
    private final DialogueManager dialogueManager = new DialogueManager(this);

    /**
     * Gets the dialogue manager
     *
     * @return
     */
    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public void setAllowRegionChangePacket(boolean allowRegionChangePacket) {
        this.allowRegionChangePacket = allowRegionChangePacket;
    }

    public boolean isAllowRegionChangePacket() {
        return allowRegionChangePacket;
    }

    public PlayerInteractingOption getPlayerInteractingOption() {
        return playerInteractingOption;
    }

    public Player setPlayerInteractingOption(PlayerInteractingOption playerInteractingOption) {
        this.playerInteractingOption = playerInteractingOption;
        return this;
    }

    public RunePouch getRunePouch() {
        return runePouch;
    }

    public Inventory inventory() {
        return inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    /**
     * Weight of the player
     */
    private double weight;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public CombatSpecial getCombatSpecial() {
        return combatSpecial;
    }

    public void setCombatSpecial(CombatSpecial combatSpecial) {
        this.combatSpecial = combatSpecial;
    }

    public MagicSpellbook getSpellbook() {
        return spellbook;
    }

    public MagicSpellbook getPreviousSpellbook() {
        return previousSpellbook;
    }

    public void setSpellbook(MagicSpellbook spellbook) {
        this.spellbook = spellbook;
    }

    public void setPreviousSpellbook(MagicSpellbook previousSpellbook) {
        this.previousSpellbook = previousSpellbook;
    }

    public void setDestroyItem(int destroyItem) {
        this.destroyItem = destroyItem;
    }

    public int getDestroyItem() {
        return destroyItem;
    }

    public Stopwatch getClickDelay() {
        return clickDelay;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public Player setStatus(PlayerStatus status) {
        this.status = status;
        return this;
    }

    private final PresetManager presetManager = new PresetManager(this);

    public final PresetManager getPresetManager() {
        return presetManager;
    }

    private final LootingBag lootingBag = new LootingBag(this);

    public final LootingBag getLootingBag() {
        return lootingBag;
    }

    private final Bank bank = new Bank(this);

    public final Bank getBank() {
        return bank;
    }

    public PriceChecker getPriceChecker() {
        return priceChecker;
    }

    public Trading getTrading() {
        return trading;
    }

    public Presetable[] getPresets() {
        return presets;
    }

    public void setPresets(Presetable[] sets) {
        this.presets = sets;
    }

    public Presetable getCurrentPreset() {
        return currentPreset;
    }

    public void setCurrentPreset(Presetable currentPreset) {
        this.currentPreset = currentPreset;
    }

    private Object[] lastPreset;

    public Object[] getLastPreset() {
        return lastPreset;
    }

    public void setLastPreset(final Object[] lastPresetData) {
        this.lastPreset = lastPresetData;
    }

    public Queue<ChatMessage> getChatMessageQueue() {
        return chatMessageQueue;
    }

    public ChatMessage getCurrentChatMessage() {
        return currentChatMessage;
    }

    public void setCurrentChatMessage(ChatMessage currentChatMessage) {
        this.currentChatMessage = currentChatMessage;
    }

    public QuickPrayers getQuickPrayers() {
        return quickPrayers;
    }

    public SecondsTimer getYellDelay() {
        return yellDelay;
    }

    public String getKillDeathRatio() {
        double kc = 0;
        int kills = this.getAttribOr(AttributeKey.PLAYER_KILLS, 0);
        int deaths = this.getAttribOr(AttributeKey.PLAYER_DEATHS, 0);
        if (deaths == 0) {
            kc = kills;
        } else {
            kc = ((double) kills / deaths);
        }
        return String.valueOf(Math.round(kc * 100) / 100.0);
    }

    public List<String> getRecentKills() {
        return recentKills;
    }

    public SecondsTimer getTargetSearchTimer() {
        return targetSearchTimer;
    }

    public SecondsTimer getSpecialAttackRestore() {
        return specialAttackRestore;
    }

    public boolean queuedAppearanceUpdate() {
        return queuedAppearanceUpdate;
    }

    public void setQueuedAppearanceUpdate(boolean updateAppearance) {
        this.queuedAppearanceUpdate = updateAppearance;
    }

    public Dueling getDueling() {
        return dueling;
    }

    public void setCachedUpdateBlock(ByteBuf cachedUpdateBlock) {
        this.cachedUpdateBlock = cachedUpdateBlock;
    }

    public int getRegionHeight() {
        return regionHeight;
    }

    public void setRegionHeight(int regionHeight) {
        this.regionHeight = regionHeight;
    }

    private GameMode mode = GameMode.TRAINED_ACCOUNT;

    public GameMode getGameMode() {
        return mode;
    }

    public GameMode getGameMode(GameMode mode) {
        this.mode = mode;
        return mode;
    }

    public void message(String message) {
        if (message == null)
            return;
        getPacketSender().sendMessage(message);
    }

    public void message(String format, Object... params) {
        if (format == null)
            return;
        String message = params.length > 0 ? String.format(format, (Object[]) params) : format;
        getPacketSender().sendMessage(message);
    }

    private final PlayerMovement movementQueue = new PlayerMovement(this);

    @Override
    public PlayerMovement getMovementQueue() {
        return movementQueue;
    }

    public void sendScroll(String title, String... lines) {

        for (int counter = 21408; counter < 21609; counter++) {
            packetSender.sendString(counter, "");
        }

        int childId = 21408;

        packetSender.sendString(21403, "<col=" + Color.MAROON.getColorValue() + ">" + title + "</col>");

        for (String s : lines)
            packetSender.sendString(childId++, s);

        interfaceManager.open(21400);
    }

    public void debug(String format, Object... params) {
        if (rights.isAdministrator(this)) {
            if (getAttribOr(AttributeKey.DEBUG_MESSAGES, false)) {//debug messages are on and I know whats wrong
                getPacketSender().sendMessage(params.length > 0 ? String.format(format, (Object[]) params) : format);
            }
        }
    }

    public void debugMessage(String message) {
        boolean debugMessagesEnabled = getAttribOr(AttributeKey.DEBUG_MESSAGES, true);
        //Removed debug mode check, let's check it per player so we can use it any time on live.
        if (getPlayerRights().isOwner(this) && debugMessagesEnabled) {
            getPacketSender().sendMessage("[Debug] " + message);
        }
    }

    /**
     * We need this because our movementQueue isn't properly setup. So we need to toggle off running.
     */
    public void agilityWalk(boolean reset) {
        if (reset) {
            this.putAttrib(AttributeKey.IS_RUNNING, true);
        } else {
            this.putAttrib(AttributeKey.IS_RUNNING, false);
        }
        this.getPacketSender().sendRunStatus();
    }

    public void playSound(int id) {
        sendSound(id, 0);
    }

    public void sendSound(int id, int tickDelay) {
        getPacketSender().sendSound(id, tickDelay);
    }

    private Task distancedTask;
    public final Stopwatch afkTimer = new Stopwatch();

    public void setDistancedTask(Task task) {
        stopDistancedTask();
        this.distancedTask = task;
        if (task != null) {
            TaskManager.submit(task);
        }
    }

    public void stopDistancedTask() {
        if (distancedTask != null && distancedTask.isRunning()) {
            distancedTask.stop();
        }
    }

    // Time the account was last logged in
    private Timestamp lastLogin = new Timestamp(new Date().getTime());

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp timestamp) {
        lastLogin = timestamp;
    }

    // Time the account was created
    private Timestamp creationDate;

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp timestamp) {
        creationDate = timestamp;
    }

    // IP the account was created with
    private String creationIp;

    public String getCreationIp() {
        return creationIp;
    }

    public void setCreationIp(String creationIp) {
        this.creationIp = creationIp;
    }

    private boolean invulnerable;

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public int getDuelWins() {
        return duelWins;
    }

    public void setDuelWins(int duelWins) {
        this.duelWins = duelWins;
    }

    public int getDuelLosses() {
        return duelLosses;
    }

    public void setDuelLosses(int duelLosses) {
        this.duelLosses = duelLosses;
    }

    public void setUsingQuestTab(boolean usingQuestTab) {
        this.usingQuestTab = usingQuestTab;
    }

    public BankPin getBankPin() {
        return bankPin;
    }

    public BankPinSettings getBankPinSettings() {
        return bankPinSettings;
    }

    private final RiskManagement risk_management = new RiskManagement(this);

    public RiskManagement getRisk() {
        return risk_management;
    }

    private final HashMap<Achievements, Integer> achievements = new HashMap<>(Achievements.values().length) {
        @Serial
        private static final long serialVersionUID = 1842952445111093360L;

        {
            for (final Achievements achievement : Achievements.values()) {
                put(achievement, 0);
            }
        }
    };

    public HashMap<Achievements, Integer> achievements() {
        return achievements;
    }

    public int achievementsCompleted() {
        int completed = 0;
        for (final Achievements achievement : this.achievements().keySet()) {
            if (achievement != null && this.achievements().get(achievement) == achievement.getCompleteAmount()) {
                completed++;
            }
        }
        return completed;
    }

    public boolean completedAllAchievements() {
        return achievementsCompleted() >= Achievements.getTotal() - 1;
    }

    private final BossKillLog bossKillLog = new BossKillLog(this);

    /**
     * Returns the single instance of the {@link BossKillLog} class for this player.
     *
     * @return the tracker class
     */
    public BossKillLog getBossKillLog() {
        return bossKillLog;
    }

    private final SlayerKillLog slayerKillLog = new SlayerKillLog(this);

    /**
     * Returns the single instance of the {@link SlayerKillLog} class for this player.
     *
     * @return the tracker class
     */
    public SlayerKillLog getSlayerKillLog() {
        return slayerKillLog;
    }

    @Override
    public void autoRetaliate(Entity attacker) {
        if (dead() || hp() < 1 /*|| !getMovementQueue().empty()*/) { // TODO
            return;
        }
        super.autoRetaliate(attacker);
    }

    @Override
    public void takehitSound(Hit hit) {
        if (hit == null)
            return;
    }

    public void clearInstance() {
        if (getInstancedArea() != null) {
            getInstancedArea().removePlayer(this); // will dispose only when empty
            instancedArea = null;
        }
    }

    @Override
    public void stopActions(boolean cancelMoving) {
        super.stopActions(cancelMoving);

        if (cancelMoving)
            getMovementQueue().clear();

        if (interfaceManager.getMain() > 0) {
            interfaceManager.close();
        }

        // all your typical interrupts here
        getSkills().stopSkillable();

        getMovementQueue().resetFollowing();
    }

    public boolean muted() {
        return PlayerPunishment.IPmuted(hostAddress) || PlayerPunishment.muted(username) || this.<Boolean>getAttribOr(MUTED, false);
    }

    // Main item used
    public Item itemUsed() {
        return this.<Item>getAttrib(AttributeKey.FROM_ITEM);
    }

    // Other item used
    public int itemOnSlot() {
        return this.<Integer>getAttrib(AttributeKey.ALT_ITEM_SLOT);
    }

    // Main item used
    public int itemUsedSlot() {
        return this.<Integer>getAttrib(AttributeKey.ITEM_SLOT);
    }

    public void itemDialogue(String message, int item) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, new Item(item), "", message);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    public void npcStatement(NPC npc, String[] strings) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(player, npc, strings);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    public void confirmDialogue(Object[] params, String title, String optionOne, String optionTwo, Runnable runnable) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, params);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, title.isEmpty() ? DEFAULT_OPTION_TITLE : title, optionOne, optionTwo);
                    setPhase(1);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    stop();
                    if (option == 1) {
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                }
            }
        });
    }

    public void costBMAction(int cost, String title, Runnable runnable) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, title);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                    setPhase(1);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        var canPerformAction = false;
                        int bmInInventory = player.inventory().count(BLOOD_MONEY);
                        if (bmInInventory > 0) {
                            if (bmInInventory >= cost) {
                                canPerformAction = true;
                                player.inventory().remove(new Item(BLOOD_MONEY, cost), true);
                            }
                        }

                        if (canPerformAction) {
                            if (runnable != null) {
                                runnable.run();
                            }
                        } else {
                            player.message("You do not have enough Blood money.");
                        }
                    }
                    stop();
                }
            }
        });
    }

    public void itemBox(String message, int id) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, new Item(id), "", message);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    public void itemBox(String message, int id, int amount) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.ITEM_STATEMENT, new Item(id, amount), "", message);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    public void messageBox(String message) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, message);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    public void optionsTitled(String title, String opt1, String opt2, Runnable runnable) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, title, opt1, opt2);
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        if (runnable != null) {
                            stop();
                            runnable.run();
                        }
                    }
                    if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    public void doubleItemBox(String message, Item first, Item second) {
        this.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.DOUBLE_ITEM_STATEMENT, first, second, message);
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    stop();
                }
            }
        });
    }

    public static int warnTimeMs = 20;

    public final void sequence() {
        try {
            Arrays.fill(section, false);

            Runnable total = () -> {
                time(t -> {
                    perf.logout += t.toNanos();
                    World.getWorld().benchmark.allPlayers.logout += t.toNanos();
                }, logR);
                time(t -> {
                    perf.qtStuffs += t.toNanos();
                    World.getWorld().benchmark.allPlayers.qtStuffs += t.toNanos();
                }, qtStuff);
                time(t -> {
                    perf.controllers += t.toNanos();
                    World.getWorld().benchmark.allPlayers.controllers += t.toNanos();
                }, controllers);
                time(t -> {
                    perf.timers += t.toNanos();
                    World.getWorld().benchmark.allPlayers.timers += t.toNanos();
                }, timers);
                time(t -> {
                    perf.actions += t.toNanos();
                    World.getWorld().benchmark.allPlayers.actions += t.toNanos();
                }, actions);
                time(t -> {
                    perf.tasks += t.toNanos();
                    World.getWorld().benchmark.allPlayers.tasks += t.toNanos();
                }, tasks);
                time(t -> {
                    perf.regions += t.toNanos();
                    World.getWorld().benchmark.allPlayers.regions += t.toNanos();
                }, regions);
                time(t -> {
                    perf.bmove += t.toNanos();
                    World.getWorld().benchmark.allPlayers.bmove += t.toNanos();
                }, beforemove);
                time(t -> {
                    perf.move += t.toNanos();
                    World.getWorld().benchmark.allPlayers.move += t.toNanos();
                }, movement);
                time(t -> {
                    perf.cbBountyFlush += t.toNanos();
                    World.getWorld().benchmark.allPlayers.cbBountyFlush += t.toNanos();
                }, cbBountyFlush);
                time(t -> {
                    perf.prayers += t.toNanos();
                    World.getWorld().benchmark.allPlayers.cbBountyFlush += t.toNanos();
                }, prayers);
                time(t -> {
                    perf.end += t.toNanos();
                    World.getWorld().benchmark.allPlayers.end += t.toNanos();
                }, end);
            };
            time(t -> {
                perf.total += t.toNanos();
                World.getWorld().benchmark.allPlayers.total += t.toNanos();
            }, total);

            if ((int) (1. * perf.total / 1_000_000.) > warnTimeMs) {
                logger.trace("Player {} sequence took {}ms : {}", getMobName(), (int) (1. * perf.total / 1_000_000.), perf.toString());
            }

        } catch (Exception e) {
            logger.error("Error processing logic for Player: {}.", this);
            logger.error(captureState());
            logger.error(e);
        }
    }

    Runnable logR = () -> {
        this.fireLogout();
    },
        qtStuff = () -> {
            this.setPlayerQuestTabCycleCount(getPlayerQuestTabCycleCount() + 1);
            //Update the players online regardless of the cycle count, this is the most important number, otherwise players might see "0" if they log in too soon. Can always remove this later.
            GlobalStrings.PLAYERS_ONLINE.send(this, World.getWorld().getPlayers().size());

            var gametime = this.<Long>getAttribOr(GAME_TIME, 0L) + 1;
            this.putAttrib(GAME_TIME, gametime);// Increment ticks we've played for

            if (interfaceManager.isInterfaceOpen(DAILY_TASK_MANAGER_INTERFACE)) {
                var dailyTask = this.<DailyTasks>getAttribOr(DAILY_TASK_SELECTED, null);
                if (dailyTask != null)
                    this.getPacketSender().sendString(TIME_FRAME_TEXT_ID, DailyTaskManager.timeLeft(this, dailyTask));
            }

            var staminaTicks = this.<Integer>getAttribOr(STAMINA_POTION_TICKS, 0);
            if (staminaTicks > 0) {
                staminaTicks--;
                this.putAttrib(STAMINA_POTION_TICKS, staminaTicks);
                if (staminaTicks == 50) {
                    message("<col=8f4808>Your stamina potion is about to expire.");
                } else if (staminaTicks == 0) {
                    message("<col=8f4808>Your stamina potion has expired.");
                    this.packetSender.sendStamina(false).sendEffectTimer(0, EffectTimer.STAMINA);
                }
            }

            LocalDateTime now = LocalDateTime.now();
            long minutesTillWildyBoss = now.until(WildernessBossEvent.getINSTANCE().next, ChronoUnit.MINUTES);

            // Refresh the quest tab every minute (every 100 ticks)
            if (GameServer.properties().autoRefreshQuestTab && getPlayerQuestTabCycleCount() == GameServer.properties().refreshQuestTabCycles) {
                setPlayerQuestTabCycleCount(0);

                //We only have to update the uptime here, every other line is automatically updated.
                this.getPacketSender().sendString(UPTIME.childId, QuestTab.InfoTab.INFO_TAB.get(UPTIME.childId).fetchLineData(this));

                //Update the timer frames every minute.
                this.getPacketSender().sendString(WORLD_BOSS_SPAWN.childId, QuestTab.InfoTab.INFO_TAB.get(WORLD_BOSS_SPAWN.childId).fetchLineData(this));

                if (minutesTillWildyBoss == 5) {
                    if (!WildernessBossEvent.ANNOUNCE_5_MIN_TIMER) {
                        WildernessBossEvent.ANNOUNCE_5_MIN_TIMER = true;
                        World.getWorld().sendWorldMessage("<col=6a1a18><img=2012>The world boss will spawn in 5 minutes, gear up!");
                    }
                }
            }
        }, controllers = () -> {
        if (this.<Boolean>getAttribOr(AttributeKey.NEW_ACCOUNT, false) && System.currentTimeMillis() - this.<Long>getAttribOr(LOGGED_IN_AT_TIME, System.currentTimeMillis()) > 1000 * 60 * 4) {
            this.requestLogout();
        }

        //Section 8 Process areas..
        section[8] = true;
        ControllerManager.process(this);

        //We don't have to make a entire abstract area for just these 2 lines.
        if (tile.region() == 14231) {
            this.interfaceManager.sendOverlay(4535);
            this.packetSender.sendString(4536, "Kill Count: " + getAttribOr(BARROWS_MONSTER_KC, 0));
        }
    }, timers = () -> {
        this.getTimers().cycle(this);
    }, actions = () -> {
        this.action.sequence();
    }, tasks = () -> {
        TaskManager.sequenceForMob(this);
    }, regions = () -> {

        int lastregion = this.getAttribOr(AttributeKey.LAST_REGION, -1);
        int lastChunk = this.getAttribOr(AttributeKey.LAST_CHUNK, -1);

        if (lastregion != tile.region() || lastChunk != tile.chunk()) {
            MultiwayCombat.refresh(this, lastregion, lastChunk);
            //TODO Have Jak check
            // Register the unique farmbit regions
            var uniqueRegions = Arrays.stream(Farmbit.values()).map(fb -> fb.visibleRegion).collect(Collectors.toList());
            for (int region : uniqueRegions) {
                if (lastregion == region) {
                    Farming.synchRegion(this);
                }
            }
        }

        // Update last region and chunk ids
        this.putAttrib(AttributeKey.LAST_REGION, tile.region());
        this.putAttrib(AttributeKey.LAST_CHUNK, tile.chunk());
    }, beforemove = () -> {
        this.getCombat().preAttack();
        TargetRoute.beforeMovement(this);
    }, movement = () -> {
        this.getMovementQueue().process(); // must be between before+after movement
        TargetRoute.afterMovement(this); // must be afterMove
    }, cbBountyFlush = () -> {
        getCombat().process();

        //Section 8 Process Bounty Hunter
        section[9] = true;
        BountyHunter.sequence(this);

        //Section 10 Updates inventory if an update has been requested
        section[10] = true;
    }, prayers = () -> {
        Prayers.drainPrayer(this);
    }, end = () -> {

        if (hp() < 1 && System.currentTimeMillis() - lockTime > 30_000) {
            logger.error("player has been locked for 30s while 0hp.. how tf did that happen");
            this.die();
        }

        if (queuedAppearanceUpdate()) {
            this.getUpdateFlag().flag(Flag.APPEARANCE);
            this.setQueuedAppearanceUpdate(false);
        }

        //Section 12 Sync containers, if dirty
        section[12] = true;
        this.syncContainers();

        //Section 13 Send queued chat messages
        section[13] = true;
        if (!getChatMessageQueue().isEmpty()) {
            setCurrentChatMessage(getChatMessageQueue().poll());
            this.getUpdateFlag().flag(Flag.CHAT);
        } else {
            setCurrentChatMessage(null);
        }

        //Section 14 Decrease boosted stats Increase lowered stats. Don't decrease stats whilst the divine potion effect is active.
        section[14] = true;
        if ((!this.increaseStats.active() || (this.decreaseStats.secondsElapsed() >= (Prayers.usingPrayer(this, Prayers.PRESERVE) ? 90 : 60))) && !this.divinePotionEffectActive()) {
            this.skills.replenishStats();

            // Reset timers
            if (!this.increaseStats.active()) {
                this.increaseStats.start(60);
            }
            if (this.decreaseStats.secondsElapsed() >= (Prayers.usingPrayer(this, Prayers.PRESERVE) ? 90 : 60)) {
                this.decreaseStats.start((Prayers.usingPrayer(this, Prayers.PRESERVE) ? 90 : 60));
            }
        }

        //Section 15 process farming
        section[15] = true;
        this.farmingOld.farmingProcess();
    };

    private void replaceItems() {

    }

    public int lastActiveOverhead;

    public void setLastActiveOverhead() {
        boolean[] actives = getPrayerActive();
        int forLastActive = -1;
        if (actives[16])
            forLastActive = Prayers.PROTECT_FROM_MAGIC;
        if (actives[17])
            forLastActive = Prayers.PROTECT_FROM_MISSILES;
        if (actives[18])
            forLastActive = Prayers.PROTECT_FROM_MELEE;
        lastActiveOverhead = forLastActive;
    }

    public transient long lastVoteClaim, lastSpellbookChange;

    public void switchSpellBook(MagicSpellbook book) {
        if (lastSpellbookChange > System.currentTimeMillis())
            return;
        if (this.getSpellbook() == book) {
            this.getPacketSender().sendMessage("You already have wisdom of these magics.");
            return;
        }
        this.lastSpellbookChange = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2);
        this.animate(645);
        this.message("Your mind fills with " + book.name().toLowerCase() + " knowledge.");
        MagicSpellbook.changeSpellbook(this, book, false);
    }

    public int getZoneX() {
        return getX() >> 3;
    }

    public int getZoneY() {
        return getY() >> 3;
    }

    public boolean hitDrops;

    public List<SigilHandler> activeSigils = Lists.newArrayList();

    public List<SigilHandler> getActiveSigils() {
        return activeSigils;
    }

    public Tile recentTeleport;

    public boolean combatDebug;

    public boolean soundmode;

    public int lastSoundId = 1;

    private InputScript inputScript;

    public void removeInputScript() {
        if (inputScript == null)
            return;
        inputScript = null;
    }

    public InputScript getInputScript() {
        return inputScript;
    }

    public void finishInputScript() {
        inputScript = null;
    }

    public void setAmountScript(String title, InputScript inputScript) {
        this.getPacketSender().sendEnterAmountPrompt(title);
        this.inputScript = inputScript;
    }

    public void setNameScript(String title, InputScript inputScript) {
        this.getPacketSender().sendEnterInputPrompt(title);
        this.inputScript = inputScript;
    }

    public void updateRunEnergy() {
        double energy = this.getAttribOr(AttributeKey.RUN_ENERGY, 0.0);

        double add = this.getRecoveryRate();

        if (!WildernessArea.inWilderness(this.tile())) {
            add *= 2; // Double energy regeneration if we're not in the wilderness.
        }

        if (this.getEquipment().wearsFullGraceful() || this.getEquipment().wearingMaxCape()) {
            add *= 1.3; // 30% increase in restore rate when wearing full graceful
        }

        this.setRunningEnergy(energy + add, true);
    }

    public boolean insideFeroxEnclaveSafe() {
        if (!this.getTimers().has(TimerKey.TELEBLOCK)) {
            return this.tile().inArea(WildernessArea.getFeroxCenter) || this.tile().inArea(WildernessArea.getFeroxUpperNorth)
                || this.tile().inArea(WildernessArea.getFeroxNorthEntrance)
                || this.tile().inArea(WildernessArea.getFeroxNorthEdges)
                || this.tile().inArea(WildernessArea.getFeroxEastEdges)
                || this.tile().inArea(WildernessArea.getFeroxLowerSouth)
                || this.tile().inArea(WildernessArea.getFeroxLowerSouthEdges)
                || this.tile().inArea(WildernessArea.getFeroxSouthEntrance)
                || this.tile().inArea(WildernessArea.getFeroxRandomLine);
        } else {
            return false;
        }
    }

    public void drainRunEnergy() {

        boolean hamstrung = false;

        //Grabs the players energy %
        double energy = this.getAttribOr(AttributeKey.RUN_ENERGY, 0);
        //Grabs the change in energy
        double change = this.getEnergyDeprecation();
        //Check to see if the player has drank a stamina potion
        int stamina = this.getAttribOr(AttributeKey.STAMINA_POTION_TICKS, 0);
        //If the player has drank a stamina potion, energy drain is reduced by 70%
        if (stamina > 0)
            change *= 0.3;
        //If for some reason the change is less then 0, we set it to 0.05
        if (change < 0)
            change = 0.05;
        if (this.getTimers().has(TimerKey.HAMSTRUNG))
            hamstrung = true;
        //Only drain the players run energy if they are running.
        if (this.getMovementQueue().isRunning()) {
            //We apply the change to the players energy level
            this.setRunningEnergy(hamstrung ? energy - change * 6 : energy - change, true);
        }
    }
}
