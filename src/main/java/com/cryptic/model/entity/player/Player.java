package com.cryptic.model.entity.player;

import com.cryptic.GameConstants;
import com.cryptic.GameEngine;
import com.cryptic.GameServer;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.*;
import com.cryptic.model.World;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.areas.wilderness.content.RiskManagement;
import com.cryptic.model.content.areas.wilderness.content.activity.WildernessActivityManager;
import com.cryptic.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.cryptic.model.content.areas.wilderness.slayer.WildernessSlayerCasket;
import com.cryptic.model.content.areas.wilderness.wildernesskeys.WildernessKeys;
import com.cryptic.model.content.bank_pin.BankPin;
import com.cryptic.model.content.bank_pin.BankPinSettings;
import com.cryptic.model.content.bountyhunter.BountyHunter;
import com.cryptic.model.content.collection_logs.CollectionLog;
import com.cryptic.model.content.consumables.potions.impl.*;
import com.cryptic.model.content.daily_tasks.DailyTaskManager;
import com.cryptic.model.content.duel.Dueling;
import com.cryptic.model.content.items.mysterybox.MysteryBoxManager;
import com.cryptic.model.content.items_kept_on_death.ItemsKeptOnDeath;
import com.cryptic.model.content.kill_logs.BossKillLog;
import com.cryptic.model.content.kill_logs.SlayerKillLog;
import com.cryptic.model.content.mechanics.BossTimers;
import com.cryptic.model.content.mechanics.DeathProcess;
import com.cryptic.model.content.mechanics.MultiwayCombat;
import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.content.mechanics.promo.PaymentPromo;
import com.cryptic.model.content.members.MemberFeatures;
import com.cryptic.model.content.minigames.Minigame;
import com.cryptic.model.content.minigames.MinigameManager;
import com.cryptic.model.content.packet_actions.GlobalStrings;
import com.cryptic.model.content.presets.PresetManager;
import com.cryptic.model.content.presets.Presetable;
import com.cryptic.model.content.raids.Raids;
import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.content.raids.party.RaidsParty;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.interactions.TheatreInterface;
import com.cryptic.model.content.raids.theatre.party.RaidParty;
import com.cryptic.model.content.raids.theatre.stage.RoomState;
import com.cryptic.model.content.raids.theatre.stage.TheatreStage;
import com.cryptic.model.content.security.AccountPin;
import com.cryptic.model.content.sigils.Sigil;
import com.cryptic.model.content.skill.Skillable;
import com.cryptic.model.content.skill.impl.farming.Farming;
import com.cryptic.model.content.skill.impl.hunter.Hunter;
import com.cryptic.model.content.skill.impl.slayer.SlayerRewards;
import com.cryptic.model.content.skill.impl.slayer.slayer_partner.SlayerPartner;
import com.cryptic.model.content.skill.perks.SkillingItems;
import com.cryptic.model.content.tasks.TaskMasterManager;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.content.teleport.newinterface.NewTeleportInterface;
import com.cryptic.model.content.teleport.newinterface.SpecificTeleport;
import com.cryptic.model.content.teleport.world_teleport_manager.TeleportData;
import com.cryptic.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.cryptic.model.content.title.AvailableTitle;
import com.cryptic.model.content.title.TitleCategory;
import com.cryptic.model.content.title.TitleColour;
import com.cryptic.model.content.title.TitlePlugin;
import com.cryptic.model.content.title.req.impl.other.TitleUnlockRequirement;
import com.cryptic.model.content.tournaments.Tournament;
import com.cryptic.model.content.tournaments.TournamentManager;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.LockType;
import com.cryptic.model.entity.NodeType;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.Venom;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.magic.spells.CombatSpells;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.instance.NightmareInstance;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.PerilOfMoonInstance;
import com.cryptic.model.entity.combat.prayer.QuickPrayers;
import com.cryptic.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.combat.weapon.WeaponInterfaces;
import com.cryptic.model.entity.masks.Appearance;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.masks.impl.chat.ChatMessage;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.pets.Pet;
import com.cryptic.model.entity.player.commands.impl.staff.admin.UpdateServerCommand;
import com.cryptic.model.entity.player.relations.PlayerRelations;
import com.cryptic.model.entity.player.rights.MemberRights;
import com.cryptic.model.entity.player.rights.PlayerRights;
import com.cryptic.model.entity.player.save.PlayerSave;
import com.cryptic.model.entity.player.save.PlayerSaves;
import com.cryptic.model.inter.clan.Clan;
import com.cryptic.model.inter.clan.ClanManager;
import com.cryptic.model.inter.dialogue.ChatBoxItemDialogue;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.impl.BonusesInterface;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.bank.Bank;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import com.cryptic.model.items.container.inventory.Inventory;
import com.cryptic.model.items.container.looting_bag.LootingBag;
import com.cryptic.model.items.container.presets.PresetData;
import com.cryptic.model.items.container.price_checker.PriceChecker;
import com.cryptic.model.items.container.rune_pouch.RunePouch;
import com.cryptic.model.items.container.shop.impl.ShopReference;
import com.cryptic.model.items.trade.Trading;
import com.cryptic.model.items.tradingpost.TradingPostListing;
import com.cryptic.model.map.object.OwnedObject;
import com.cryptic.model.map.object.dwarf_cannon.DwarfCannon;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.model.map.position.areas.ControllerManager;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.model.map.region.Region;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.network.Session;
import com.cryptic.network.SessionHandler;
import com.cryptic.network.SessionState;
import com.cryptic.network.packet.PacketBuilder;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import com.cryptic.network.packet.outgoing.PacketSender;
import com.cryptic.network.packet.outgoing.UnnecessaryPacketDropper;
import com.cryptic.services.database.transactions.*;
import com.cryptic.utility.*;
import com.cryptic.utility.timers.TimerKey;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.cryptic.model.content.areas.wilderness.content.EloRating.DEFAULT_ELO_RATING;
import static com.cryptic.model.content.presets.newpreset.PresetHandler.EQUIPMENT_SIZE;
import static com.cryptic.model.content.presets.newpreset.PresetHandler.INVENTORY_SIZE;
import static com.cryptic.model.entity.attributes.AttributeKey.*;
import static com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.NexCombat.NEX_AREA;
import static com.cryptic.model.entity.player.QuestTab.InfoTab.WORLD_BOSS_SPAWN;
import static com.cryptic.utility.ItemIdentifiers.*;

public class Player extends Entity {

    private static final Logger logoutLogs = LogManager.getLogger("LogoutLogs");
    private static final Level LOGOUT;

    static {
        LOGOUT = Level.getLevel("LOGOUT");
    }

    @Getter private final Pet petEntity = new Pet();
    @Getter @Setter public TheatreInterface theatreInterface;
    @Getter @Setter public RoomState roomState;
    @Getter @Setter private NightmareInstance nightmareInstance;
    @Getter @Setter private TheatreInstance theatreInstance;
    @Getter @Setter private PerilOfMoonInstance perilInstance;
    @Getter @Setter private double[] savedTornamentXp;
    @Getter @Setter private Tile lastSavedTile;
    @Getter @Setter private boolean usingLastRecall = false;
    @Getter @Setter private int[] savedTornamentLevels;
    public transient ShopReference shopReference = ShopReference.DEFAULT;
    @Getter private final WildernessSlayerCasket wildernessSlayerCasket = new WildernessSlayerCasket();
    @Setter @Getter private PresetData[] presetData = new PresetData[8];
    @Getter private final WildernessKeys wildernessKeys = new WildernessKeys();
    @Getter private final MysteryBoxManager mysteryBox = new MysteryBoxManager(this);
    @Getter @Setter public boolean cursed = hasAttrib(NIGHTMARE_CURSE);

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

    @Getter public ArrayList<String> newPlayerChat = new ArrayList<>();
    @Setter @Getter private Raids raids;
    @Getter public BonusesInterface bonusInterface = new BonusesInterface(this);
    @Getter @Setter RaidParty raidParty;

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
                if (memberRights.isRegularMemberOrGreater(this)) time = 300;//3 minutes
                if (memberRights.isSuperMemberOrGreater(this)) time = 100;//1 minute
                if (memberRights.isEliteMemberOrGreater(this)) time = 0;//always
                getTimers().register(TimerKey.RECHARGE_SPECIAL_ATTACK, time); //Set the value of the timer.
                message("<col=" + Color.HOTPINK.getColorValue() + ">You have restored your special attack.");
            }
        }
    }

    public String getDisplayName() {
        return username;
    }

    private int[] sessionVarps = new int[5000];

    public int[] sessionVarps() {
        return sessionVarps;
    }

    public void setSessionVarps(int[] varps) {
        this.sessionVarps = varps;
    }

    @Getter
    private final Farming farming = new Farming(this);

    public static class TextData {

        public final String text;
        public final int id;

        public TextData(String text, int id) {
            this.text = text;
            this.id = id;
        }

        @Override
        public String toString() {
            return "TextData{" +
                "text='" + text + '\'' +
                ", id=" + id +
                '}';
        }
    }

    @Getter
    private final UnnecessaryPacketDropper packetDropper = new UnnecessaryPacketDropper();

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
        if (getMemberRights().isSponsorOrGreater(this) && tile().memberCave()) extraPercentageChance = 25;
        else if (getMemberRights().isVIPOrGreater(this) && tile().memberCave()) extraPercentageChance = 15;
        else if (getMemberRights().isLegendaryMemberOrGreater(this) && tile().memberCave()) extraPercentageChance = 10;
        else if (getMemberRights().isExtremeMemberOrGreater(this) && tile().memberCave()) extraPercentageChance = 7;
        else if (getMemberRights().isEliteMemberOrGreater(this) && tile().memberCave()) extraPercentageChance = 4;
        else if (getMemberRights().isSuperMemberOrGreater(this) && tile().memberCave()) extraPercentageChance = 2;

        return extraPercentageChance;
    }

    public int getPetDamageBonus(int damage) {
        int[] PETS = new int[]
            {
                NpcIdentifiers.CORPOREAL_CRITTER
            };

        if (this.getPetEntity() != null) {
            if (this.getPetEntity().getEntity() != null) {
                var identification = this.getPetEntity().getEntity().getId();
                if (identification == PETS[0]) {
                    damage += 15.0;
                }
            }
        }
        return damage;
    }

    public double getDropRateBonus() {
        var percent = switch (getMemberRights()) {
            case NONE -> 1.0;
            case RUBY_MEMBER -> 1.05;
            case SAPPHIRE_MEMBER -> 1.06;
            case EMERALD_MEMBER -> 1.07;
            case DIAMOND_MEMBER -> 1.08;
            case DRAGONSTONE_MEMBER -> 1.09;
            case ONYX_MEMBER -> 1.10;
            case ZENYTE_MEMBER -> 1.15;
        };

        switch (getIronManStatus()) {
            case REGULAR -> percent += 0.05;
            case HARDCORE -> percent += 0.065;
        }

        percent += this.getGameMode().dropRate;

        if (Skulling.skulled(this) && this.tile.insideRevCave()) {
            percent += 0.05;
        }

        if (getEquipment().contains(RING_OF_WEALTH_I)) {
            percent += 0.075;
        }

        return percent;
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
        return 1000 * streak;
    }

    private int killstreakValueOf(int streak) {
        return 50 * streak;
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
        if (getGameMode() == GameMode.TRAINED_ACCOUNT) bm += 100;

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
        bm += WildernessArea.getWildernessLevel(tile()) * 2; //Add the wilderness level bonus to the reward

        bm += firstKillOfTheDay();

        //Edgeville hotspot always bm x2
        if (tile().inArea(new Area(2993, 3523, 3124, 3597, 0))) {
            bm *= 2;
        }
        return bm;
    }

    public void updatePlayerPanel(Player player) {
        player.getPacketSender().sendString(80005, Utils.capitalizeJustFirst(player.getUsername()));
        player.getPacketSender().sendString(80008, "@gre@" + player.skills().combatLevel());
        player.getPacketSender().sendString(80011, "@gre@" + player.skills().totalLevel());
        player.getPacketSender().sendString(80014, "Total XP: " + "@gre@" + Utils.insertCommasToNumber(Long.toString(player.skills().getTotalExperience())));
        player.getPacketSender().sendString(80017, "@gre@" + "0/5");
        player.getPacketSender().sendString(80021, "@gre@" + player.achievementsCompleted() + "/" + player.achievements().entrySet().size());
        player.getPacketSender().sendString(80026, "@gre@" + player.getCollectionLog().totalAmountToCollect() + "/" + player.getCollectionLog().sumTotalObtained());
        player.getPacketSender().sendString(80028, "Time Played: " + QuestTabUtils.getTimeDHS(player));
    }

    public void updateAccountStatus(Player player) {
        player.getPacketSender().sendString(73005, "Donator Rank: " + player.getMemberRights().getName());
        player.getPacketSender().sendString(73015, "Name: " + player.getUsername());
    }

    public void updateServerInformation(Player player) {
        LocalDateTime now = LocalDateTime.now();
        long minutesTillWildyBoss = now.until(WildernessBossEvent.getINSTANCE().next, ChronoUnit.MINUTES);
        long risked = ItemsKeptOnDeath.getLostItemsValue();
        String formatted = QuestTabUtils.formatNumberWithSuffix(risked);
        player.getPacketSender().sendString(80055, GameConstants.SERVER_NAME + " Information");
        player.getPacketSender().sendString(80059, "Server Time: " + "@whi@" + QuestTabUtils.getFormattedServerTime());
        player.getPacketSender().sendString(80060, "Server Uptime: " + "@whi@" + QuestTabUtils.fetchUpTime());
        player.getPacketSender().sendString(80061, "Players Online: " + "@whi@" + World.getWorld().getPlayers().size());
        player.getPacketSender().sendString(80062, "Players In Wild: " + "@whi@" + World.getWorld().getPlayersInWild());
        //player.getPacketSender().sendString(80064, "Total Risk: " + "@whi@" + formatted);
        player.getPacketSender().sendString(80063, "Drop Rate: " + "@whi@" + Utils.formatpercent(player.getDropRateBonus()));
        player.getPacketSender().sendString(80064, "Tournament: " + "@whi@" + QuestTabUtils.getFormattedTournamentTime());
        player.getPacketSender().sendString(80065, "Wild Activity: " + "@whi@" + WildernessActivityManager.getSingleton().getActivityDescription());
        player.getPacketSender().sendString(80066, "Wilderness Boss: " + "@whi@" + minutesTillWildyBoss + " Minutes");
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

    public boolean isPerformingAction() {
        return this.hasAttrib(PERFORMING_ACTION);
    }

    public void setPerformingAction(boolean value) {
        this.putAttrib(PERFORMING_ACTION, value);
    }

    public void clearPerformingAction() {
        this.clearAttrib(PERFORMING_ACTION);
    }

    public boolean askForAccountPin() {
        return this.<Boolean>getAttribOr(ASK_FOR_ACCOUNT_PIN, false);
    }

    public void sendAccountPinMessage() {
        AccountPin.prompt(this);
    }

    public Party raidsParty;

    public RaidsParty chambersParty;

    @Getter
    @Setter
    public TheatreStage theatreStage;

    private int multi_cannon_stage;

    public int getMultiCannonStage() {
        return multi_cannon_stage;
    }

    public void setMultiCannonStage(int stage) {
        this.multi_cannon_stage = stage;
    }

    public List<TradingPostListing> tempList;

    public List<TradingPostListing> tradePostHistory = Lists.newArrayList();

    public int tradingPostListedItemId, tradingPostListedAmount, tpListingPrice;
    public TradingPostListing tradingPostSelectedListing;

    public String lastTradingPostUserSearch, lastTradingPostItemSearch;
    public int tpClickedFeaturedSpotIdx = -1;

    public boolean jailed() {
        return (int) getAttribOr(AttributeKey.JAILED, 0) == 1;
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

    public NewTeleportInterface getnewteleInterface() {
        return newteleInterface;
    }

    private final NewTeleportInterface newteleInterface = new NewTeleportInterface(this);

    private List<SpecificTeleport> newtelefavs = new ArrayList<>();

    public List<SpecificTeleport> getnewfavs() {
        return newtelefavs;
    }

    public void setnewtelefavs(List<SpecificTeleport> newtelefavs) {
        this.newtelefavs = newtelefavs;
    }

    private int currentTabIndex;

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(int index) {
        this.currentTabIndex = index;
    }

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Player.class);

    private final PaymentPromo paymentPromo = new PaymentPromo(this);

    public PaymentPromo getPaymentPromo() {
        return paymentPromo;
    }

    private ArrayList<Integer> unlockedPets = new ArrayList<>();

    public ArrayList<Integer> getUnlockedPets() {
        return unlockedPets;
    }

    public void setUnlockedPets(ArrayList<Integer> unlockedPets) {
        if (unlockedPets == null) return;
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
        if (insuredPets == null) return;
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
        if (offer != null) return offer;

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
        super(NodeType.PLAYER, GameServer.properties().defaultTile.tile());
        initializationSource = new RuntimeException("player created");
        this.session = playerIO;
        this.appearance = new Appearance(this);
        this.skills = new Skills(this);
        this.varps = new Varps(this);
    }

    public Player() {
        super(NodeType.PLAYER, GameServer.properties().defaultTile.tile());
        initializationSource = new RuntimeException("player created");
        this.appearance = new Appearance(this);
        this.skills = new Skills(this);
        this.varps = new Varps(this);
    }

    public void teleblockMessage() {
        if (!getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) return;

        long special_timer = getTimers().left(TimerKey.SPECIAL_TELEBLOCK) * 600L;

        message(String.format("A teleport block has been cast on you. It should wear off in %d minutes, %d seconds.", TimeUnit.MILLISECONDS.toMinutes(special_timer), TimeUnit.MILLISECONDS.toSeconds(special_timer) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(special_timer))));

        if (!getTimers().has(TimerKey.TELEBLOCK)) return;

        long millis = getTimers().left(TimerKey.TELEBLOCK) * 600L;

        message(String.format("A teleport block has been cast on you. It should wear off in %d minutes, %d seconds.", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
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
        Long uid = this.<Long>getAttribOr(PLAYER_UID, 0L);
        if (uid == 0L) {
            uid = Utils.generateUUID();
            this.putAttrib(AttributeKey.PLAYER_UID, uid);
        }
        World.getWorld().ls.ONLINE.add(getMobName().toUpperCase());
        session.setState(SessionState.LOGGED_IN);
        setNeedsPlacement(true);
        packetSender.sendMapRegion().sendDetails().sendRights().sendTabs();
        Tile.occupy(this);
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
    public Hit manipulateHit(@Nullable Hit hit) {
        Entity attacker = null;
        if (hit != null) {
            attacker = hit.getAttacker();
        }

        if (attacker != null && attacker.isNpc()) {
            NPC npc = attacker.getAsNpc();
            if (npc.id() == NpcIdentifiers.TZTOKJAD) {
                if (Prayers.usingPrayer(this, Prayers.getProtectingPrayer(hit.getCombatType(), this))) {
                    hit.setDamage(0);
                }
            }
        }

        return hit;
    }

    @Override
    public void die() {
        stopActions(true);
        DeathProcess deathProcess = new DeathProcess();
        deathProcess.handleDeath(this);
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
                logger.trace("{} is infhp, no dmg taken", getMobName());
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
        int attackSpeed;
        Item weapon = this.getEquipment().get(EquipSlot.WEAPON);
        if (weapon == null) {
            attackSpeed = 4;
        } else {
            attackSpeed = World.getWorld()
                .getEquipmentLoader()
                .getInfo(weapon.getId())
                .getEquipment()
                .getAspeed();
        }

        if (attackSpeed > 4) {
            if (this.hasAttrib(NINJA)) {
                if (!FormulaUtils.hasBowOfFaerdhenin(this) && !this.getEquipment().containsAny(CRYSTAL_BOW)) {
                    attackSpeed--;
                }
            }
        }

        if (this.getCombat().getCombatType() != null) {
            if (player().hasAttrib(FERAL_FIGHTER_ATTACKS_SPEED) && this.getCombat().getCombatType().isMelee()) {
                attackSpeed--;
            }
        }

        if (getCombat().getFightType().toString().toLowerCase().contains("rapid")) {
            attackSpeed--;
        }

        return attackSpeed;
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
        if (p.username == null || username == null) return false;
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
        if (username == null || this.<Boolean>getAttribOr(IS_BOT, false)) return;
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

    private void handleContainersDirty() {
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
        if (getForceMovement() != null && getMovementQueue().forcedStep()) return false;
        // dont save dead/tping players. login with 0hp = POSSIBLE DUPES
        if (dead() || isNeedsPlacement()) return false;
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
        onLogout();
        ObjectList<Item> temp = new ObjectArrayList<>();
        for (var o : World.getWorld().getOwnedObjects().values()) {
            if (o == null) continue;
            if (o instanceof DwarfCannon cannon) {
                if (cannon.isOwner(this)) {
                    IntStream.of(cannon.getStage().getParts()).mapToObj(Item::new).forEach(temp::add);
                    temp.add(new Item(ItemIdentifiers.CANNONBALL, cannon.getAmmo()));
                    this.getInventory().addOrBank(temp);
                    cannon.destroy();
                }
            }
        }
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

        try {
            logger.info("Starting save and cleanup task for player: {}", this.getMobName());

            // Perform the save operation asynchronously
            try {// it's being called, just that logger isn't some reason :P
                submitSave(() -> {
                    GameEngine.getInstance().addSyncTask(() -> { //oh ye this isnt even being called
                        // Perform player removal and cleanup
                        try {
                            logger.info("Removing player: {}", this.getMobName());
                            World.getWorld().getPlayers().remove(this);
                            this.onRemove();
                            logger.info("Player removed successfully: {}", this.getMobName());
                        } catch (Exception e) {
                            logger.error("Error during player removal and cleanup for player: {}", this.getMobName(), e);
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error("Error in save and cleanup task for player: {}", this.getMobName(), e);
        }
    }

    private void submitSave(Runnable whenComplete) {
        if (!World.getWorld().ls.ONLINE.contains(getMobName().toUpperCase())) {
            return;
        }

        PlayerSaves.requestSave(this, () -> {
            World.getWorld().ls.ONLINE.remove(getMobName().toUpperCase());
            whenComplete.run();
        });
    }

    private long calculateExponentialBackoffTime(int attempts) {
        int maxBackoffInterval = 5000;
        int baseBackoffTime = 100;

        long backoffTime = baseBackoffTime * (1 << attempts);

        return Math.min(backoffTime, maxBackoffInterval);
    }


    @Getter
    private final Map<String, Runnable> onLogoutListeners = new HashMap<>();

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
        logoutLogs.log(LOGOUT, "[Logout] Deregistering player - {}", getUsername());
        Utils.sendDiscordInfoLog("```[Logout]: [Player - " + getUsername() + " (IP " + getHostAddress() + ")```", "logout");

        if (tile.inArea(new Area(1356, 10254, 1380, 10280))) // hydra
            teleport(1353, 10258, 0);
        if (tile.region() == 9023 && getZ() > 3) // vorkath
            teleport(2272, 4050, 0);

        if (this.getParticipatingTournament() != null) {
            TournamentManager.leaveTourny(this, true);
        }

        if (this.getPetEntity().getEntity() != null) {
            this.getPetEntity().onLogout(this);
        }

        if (getInstancedArea() != null) {
            getInstancedArea().removePlayer(this);
        }

        if (this.getTimers().has(TimerKey.TELEBLOCK)) this.getTimers().cancel(TimerKey.TELEBLOCK);

        removeFromRegions();

        var party = this.getRaidParty();

        if (party != null) {
            for (var p : party.getPlayers()) {
                if (p.equals(this)) {
                    if (p.getTheatreInterface() != null)
                        p.getTheatreInterface().handleLogout(p);
                }
            }
        }

        this.getCombat().setAutoCastSpell(null);

        // Update session state
        getSession().setState(SessionState.LOGGING_OUT);

        clearAttrib(AttributeKey.PLAYER_AUTO_SAVE_TASK_RUNNING);

        // the point of wrapping each line in code is so that as many as possible things
        // can run successfully without stopping the ones after.
        runExceptionally(() ->

            stopActions(true));

        runExceptionally(() -> onLogoutListeners.values().

            forEach(Runnable::run));

        runExceptionally(() -> Party.onLogout(this));

        runExceptionally(() ->

        {
            var minigame = this.getMinigame();
            if (minigame != null) {
                minigame.end(this);
            }
        });

        runExceptionally(() ->

        {
            // If we're in a duel, make sure to give us a loss for logging out.
            if (getDueling().inDuel()) {
                getDueling().onDeath();
            }
        });

        runExceptionally(() ->

        {
            // Leave area
            if (!getControllers().isEmpty()) {
                for (Controller controller : getControllers()) {
                    controller.leave(this);
                }
            }
        });

        runExceptionally(() ->

        {
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
            TournamentManager.leaveTourny(this, true);
        });

        runExceptionally(() -> HealthHud.close(this));

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

    @Getter
    public Sigil sigil = new Sigil();

    /**
     * Called by the world's login queue!
     */
    public void onLogin() {
        logger.info("Registering player - [username, host] : [{}, {}]", getUsername(), getHostAddress());
        if (dead()) die();
        boolean newAccount = this.getAttribOr(NEW_ACCOUNT, false);
        if (!newAccount && getBankPin().hasPin() && !getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin)
            getBankPin().enterPin();
        if (newAccount) {
            ClanManager.join(this, "help");
            interfaceManager.open(3559);
            setNewPassword("");
            setRunningEnergy(100.0, true);
            this.putAttrib(STARTER_BOW_CHARGES, 2500);
            this.putAttrib(STARTER_STAFF_CHARGES, 2500);
            this.putAttrib(STARTER_SWORD_CHARGES, 2500);
        }
        message(STR."Welcome \{newAccount ? "" : "back "}to \{GameConstants.SERVER_NAME}!");
        switch (this.rights) {
            case SUPPORT ->
                World.getWorld().sendWorldMessage(STR."<img=\{PlayerRights.SUPPORT.getSpriteId()}><shad=1\{Color.BLUE.wrap(this.username + " has logged in! Feel free to message them for help!")}</shad>");
            case MODERATOR ->
                World.getWorld().sendWorldMessage(STR."<img=\{PlayerRights.MODERATOR.getSpriteId()}><shad=1\{Color.WHITE.wrap(this.username + " has logged in! Feel free to message them for help!")}</shad>");
            case ADMINISTRATOR ->
                World.getWorld().sendWorldMessage(STR."<img=\{PlayerRights.ADMINISTRATOR.getSpriteId()}><shad=1\{Color.GOLD.wrap(this.username + " has logged in! Feel free to message them for help!")}</shad>");
            case OWNER ->
                World.getWorld().sendWorldMessage(STR."<img=\{PlayerRights.OWNER.getSpriteId()}><shad=1\{Color.RED.wrap(this.username + " has logged in! Feel free to message them for help!")}</shad>");
        }
        handleForcedTeleports();
        applyAttributes();
        updatePlayer();
        handleOnLogin(this);
        this.getSigil().HandleLogin(this);
        applyPoweredStaffSpells();
        updatePlayerPanel(this);
        TaskManager.submit(new SaveTask(this));
        this.getEquipment().login();
        if (clanChat != null) ClanManager.join(this, "help");
        if (memberRights.isSponsorOrGreater(this)) MemberFeatures.checkForMonthlySponsorRewards(this);
        restartTasks();
        auditTabs();
        getUpdateFlag().flag(Flag.ANIMATION);
        getUpdateFlag().flag(Flag.APPEARANCE);
    }

    private static void handleOnLogin(Player player) {
        PacketInteractionManager.onLogin(player);
        TournamentManager.onLogin1(player);
        DailyTaskManager.onLogin(player);
        Prayers.onLogin(player);
        SlayerPartner.onLogin(player);
        TitlePlugin.SINGLETON.onLogin(player);
        ControllerManager.process(player);
    }

    private void updatePlayer() {
        double energy = this.getAttribOr(RUN_ENERGY, 0.0);
        packetSender.sendInteractionOption("Follow", 3, false).sendInteractionOption("Trade with", 4, false);
        relations.setPrivateMessageId(1);
        relations.onLogin();
        getMovementQueue().clear();
        varps.syncNonzero();
        packetSender.sendConfig(708, Prayers.canUse(this, DefaultPrayerData.PRESERVE, false) ? 1 : 0).sendConfig(710, Prayers.canUse(this, DefaultPrayerData.RIGOUR, false) ? 1 : 0).sendConfig(712, Prayers.canUse(this, DefaultPrayerData.AUGURY, false) ? 1 : 0).sendConfig(172, this.getCombat().hasAutoReliateToggled() ? 1 : 0).updateSpecialAttackOrb().sendRunStatus().sendRunEnergy((int) energy);
        Prayers.closeAllPrayers(this);
        setHeadHint(-1);
        skills.update();
        farming.handleLogin();
        inventory.refresh();
        equipment.refresh();
        WeaponInterfaces.updateWeaponInterface(this);
        this.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    private void handleForcedTeleports() {
        if (getInstancedArea() == null && getZ() > 3) this.teleport(3096, 3498, 0);
        if (jailed() && tile().region() != 13103) Teleports.basicTeleport(this, new Tile(3290, 3017));
    }

    private void applyAttributes() {
        long startTime = System.currentTimeMillis();
        putAttrib(AttributeKey.LOGGED_IN_AT_TIME, startTime);
        if (this.<Integer>getAttribOr(MULTIWAY_AREA, -1) == 1 && !MultiwayCombat.includes(this.tile()))
            putAttrib(MULTIWAY_AREA, 0);
        if (this.<Boolean>getAttribOr(ASK_FOR_ACCOUNT_PIN, false)) askForAccountPin();
    }

    private void applyPoweredStaffSpells() {
        if (getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SEAS.getSpell());
        } else if (getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.TRIDENT_OF_THE_SWAMP.getSpell());
        } else if (getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.SANGUINESTI_STAFF.getSpell());
        } else if (getEquipment().hasAt(EquipSlot.WEAPON, TUMEKENS_SHADOW) || getEquipment().hasAt(EquipSlot.WEAPON, CORRUPTED_TUMEKENS_SHADOW)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.TUMEKENS_SHADOW.getSpell());
        } else if (getEquipment().hasAt(EquipSlot.WEAPON, DAWNBRINGER)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.DAWNBRINGER.getSpell());
        } else if (getEquipment().hasAt(EquipSlot.WEAPON, ACCURSED_SCEPTRE_A)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.ACCURSED_SCEPTRE.getSpell());
        } else if (getEquipment().hasAt(EquipSlot.WEAPON, STARTER_STAFF)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.STARTER_STAFF.getSpell());
        } else if (getEquipment().hasAt(EquipSlot.WEAPON, THAMMARONS_SCEPTRE)) {
            this.getCombat().setPoweredStaffSpell(CombatSpells.THAMMARON_SCEPTRE.getSpell());
        }
    }

    public int getBorderRotation(int x, int y, int centerX, int centerY, int sideLength) {
        int rotation = 0;
        if (y == centerY + sideLength / 2 + 1 && x >= centerX - sideLength / 2 && x <= centerX + sideLength / 2) {
            // Object is in the northern direction
            rotation = 3;
        } else if (y == centerY - sideLength / 2 - 1 && x >= centerX - sideLength / 2 && x <= centerX + sideLength / 2) {
            // Object is in the southern direction
            rotation = 1;
        } else if (x == centerX - sideLength / 2 - 1 && y >= centerY - sideLength / 2 && y <= centerY + sideLength / 2) {
            // Object is in the western direction
            rotation = 2;
        } else if (x == centerX + sideLength / 2 + 1 && y >= centerY - sideLength / 2 && y <= centerY + sideLength / 2) {
            // Object is in the eastern direction
            rotation = 0;
        }
        return rotation;
    }

    public int getRotation(int tileX, int tileY, int startX, int startY, int squareWidth, int squareHeight) {
        if (tileX == startX + squareWidth && tileY == startY - 1) {
            return 0; // Bottom right corner - Rotate East
        } else if (tileX == startX - 1 && tileY == startY + squareHeight) {
            return 2; // Top left corner - Rotate West
        } else if (tileX == startX + squareWidth && tileY == startY + squareHeight) {
            return 3; // Bottom left corner - Rotate South
        } else if (tileX == startX - 1 && tileY == startY - 1) {
            return 1; // Top right corner - Rotate North
        } else if (tileX == startX - 1) {
            return 2; // West border outline - Rotate South
        } else if (tileX == startX + squareWidth) {
            return 0; // East border outline - Rotate North
        } else if (tileY == startY - 1) {
            return 1; // North border outline - Rotate West
        } else if (tileY == startY + squareHeight) {
            return 3; // South border outline - Rotate East
        }
        return 0;
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
                            logger.error("found null slot in middle of bank: player {} slot {} in tab {} tabsize {}", getMobName(), i, tab, tabAmount);
                            Item[] proximity = new Item[10];
                            int k = 0;
                            for (int j = Math.max(0, i - 5); j < i + 5; j++) {
                                if (k >= proximity.length || j >= bank.getItems().length) break;
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
                if (tab >= bank.tabAmounts.length) tab--; // dont throw AIOOB ex, use lower tab
                // start at the first available free slot, aka after all bank tabs finish
                tab--;
                int hiddenItems = 0;
                for (int i = tabStartPos; i < bank.capacity(); i++) {
                    if (bank.getItems()[i] != null) {
                        logger.error("Player {} tab {} size was {} but item {} exists after this caret, increasing tabsize to fix", getMobName(), tab, bank.tabAmounts[tab], bank.getItems()[i]);
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
        setTile(GameServer.properties().defaultTile.tile().copy());

        //Save player save to re-index
        PlayerSave.save(this);
    }

    public void ecoResetAccount() {

        if (getIronManStatus() != IronMode.NONE) {
            setPlayerRights(PlayerRights.PLAYER);
        }
        //Deiron
        setIronmanStatus(IronMode.NONE);

        //Make the accounts a new account
        putAttrib(AttributeKey.NEW_ACCOUNT, true);
        putAttrib(IS_RUNNING, false);
        putAttrib(RUN_ENERGY, 100.0);
        //place player at edge
        setTile(GameServer.properties().defaultTile.tile().copy());

        //Clear content
        Arrays.fill(getPresets(), null);
        achievements().clear();
        getHostAddressMap().clear();
        getInsuredPets().clear();
        getSlayerRewards().getBlockedSlayerTask().clear();
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

        setTile(GameServer.properties().defaultTile.tile().copy());

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
        getSlayerRewards().getBlockedSlayerTask().clear();

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
        putAttrib(AttributeKey.VOTE_POINTS, 0);
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
        setTile(GameServer.properties().defaultTile.tile().copy().add(Utils.getRandom(2), Utils.getRandom(2)));
        setSpellbook(MagicSpellbook.NORMAL);
        setMemberRights(MemberRights.NONE);
        putAttrib(AttributeKey.TOTAL_PAYMENT_AMOUNT, 0D);
        //Cancel all timers
        getTimers().cancel(TimerKey.FROZEN); //Remove frozen timer key
        getTimers().cancel(TimerKey.FREEZE_IMMUNITY);
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
        getTimers().cancel(TimerKey.FREEZE_IMMUNITY);
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
    @Getter
    private final PlayerRelations relations = new PlayerRelations(this);
    @Getter
    private final QuickPrayers quickPrayers = new QuickPrayers(this);
    private Session session;
    @Getter
    private PlayerInteractingOption playerInteractingOption = PlayerInteractingOption.NONE;
    private PlayerRights rights = PlayerRights.PLAYER;
    private MemberRights memberRights = MemberRights.NONE;
    @Getter
    private PlayerStatus status = PlayerStatus.NONE;
    private String clanChatName = GameServer.properties().defaultClanChat;
    public final Stopwatch last_trap_layed = new Stopwatch();
    @Getter
    @Setter
    private boolean allowRegionChangePacket;
    private boolean usingQuestTab = false;
    private int presetIndex = 0;
    private int interactingNpcId = 0;
    @Getter
    private final RunePouch runePouch = new RunePouch(this);
    @Setter
    @Getter
    private Inventory inventory = new Inventory(this);
    @Getter
    private final Equipment equipment = new Equipment(this);
    @Getter
    private final PriceChecker priceChecker = new PriceChecker(this);
    @Getter
    private final Stopwatch clickDelay = new Stopwatch();
    @Setter
    @Getter
    private MagicSpellbook spellbook = MagicSpellbook.NORMAL;
    @Setter
    @Getter
    private MagicSpellbook previousSpellbook = MagicSpellbook.NORMAL;
    @Getter
    private final SecondsTimer yellDelay = new SecondsTimer();
    public final SecondsTimer increaseStats = new SecondsTimer();
    public final SecondsTimer decreaseStats = new SecondsTimer();
    public boolean[] section = new boolean[16];

    @Getter
    @Setter
    private int destroyItem = -1;
    @Setter
    private boolean queuedAppearanceUpdate; // Updates appearance on next tick
    @Setter
    @Getter
    private int regionHeight;

    private int duelWins = 0;
    private int duelLosses = 0;

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
        if ((!this.increaseStats.active() || (this.decreaseStats.secondsElapsed() >= (Prayers.usingPrayer(this, Prayers.PRESERVE) ? 90 : 60))) && !this.divinePotionEffectActive()) {
            this.skills.replenishStats();
            if (!this.increaseStats.active()) this.increaseStats.start(60);
            if (this.decreaseStats.secondsElapsed() >= (Prayers.usingPrayer(this, Prayers.PRESERVE) ? 90 : 60))
                this.decreaseStats.start((Prayers.usingPrayer(this, Prayers.PRESERVE) ? 90 : 60));
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
    }

    private final SecondsTimer aggressionTolerance = new SecondsTimer();
    @Setter
    @Getter
    private CombatSpecial combatSpecial;

    public double getEnergyDeprecation() {
        double weight = Math.max(0, Math.min(54, getWeight())); // Capped at 54kg - where stamina effect no longer works.. for a QoL. Stamina always helpful!
        double clampWeight = Math.max(0, Math.min(64, weight));
        return (67 + Math.floorDiv((67 * (int) clampWeight), 64)) / 100.0;
    }

    public double getRecoveryRate() {
        int agilityLevel = skills.level(Skills.AGILITY);
        return (Math.floorDiv(agilityLevel, 6) + 8) / 100.0;
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
    @Getter
    private final SecondsTimer specialAttackRestore = new SecondsTimer();

    // Bounty hunter
    @Getter
    private final SecondsTimer targetSearchTimer = new SecondsTimer();
    @Getter
    private final List<String> recentKills = new ArrayList<>(); // Contains ip addresses of recent kills
    @Getter
    private final Queue<ChatMessage> chatMessageQueue = new ConcurrentLinkedQueue<>();
    @Setter
    @Getter
    private ChatMessage currentChatMessage;

    // Logout
    @Getter
    private final SecondsTimer forcedLogoutTimer = new SecondsTimer();
    private final BankPin bankPin = new BankPin(this);
    private final BankPinSettings bankPinSettings = new BankPinSettings(this);

    // Banking
    private String searchSyntax = "";

    // Trading
    @Getter
    private final Trading trading = new Trading(this);
    @Getter
    private final Dueling dueling = new Dueling(this);

    // Presets
    @Setter
    @Getter
    private Presetable currentPreset;
    // old i guess?
    @Setter
    @Getter
    private Presetable[] presets = new Presetable[20];

    /**
     * The cached player update block for updating.
     */
    @Setter
    private volatile ByteBuf cachedUpdateBlock;

    @Getter
    @Setter
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

    public int tabSlot = 0;

    /**
     * The dialogue manager instance
     * -- GETTER --
     *  Gets the dialogue manager
     *
     * @return

     */
    @Getter
    private final DialogueManager dialogueManager = new DialogueManager(this);

    public Player setPlayerInteractingOption(PlayerInteractingOption playerInteractingOption) {
        this.playerInteractingOption = playerInteractingOption;
        return this;
    }

    public Inventory inventory() {
        return inventory;
    }

    /**
     * Weight of the player
     */
    @Setter
    @Getter
    private double weight;

    public Player setStatus(PlayerStatus status) {
        this.status = status;
        return this;
    }

    private final PresetManager presetManager = new PresetManager(this);

    // these can go into their own class later
    @Getter
    public ItemContainer presetEquipment = new ItemContainer(EQUIPMENT_SIZE, ItemContainer.StackPolicy.STANDARD);
    @Getter
    public ItemContainer presetInventory = new ItemContainer(INVENTORY_SIZE, ItemContainer.StackPolicy.STANDARD);

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

    //old yeye
    @Setter
    @Getter
    private Object[] lastPreset;

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

    public Region lastRegion;
    private ArrayList<Region> mapRegions = new ArrayList<>();

    public void addRegion(Region region) {
        if (!region.players.contains(this)) region.players.add(this);
        for (var r : this.getSurroundingRegions()) {
            if (mapRegions.contains(r)) continue;
            mapRegions.add(r);
        }
    }

    public void removeFromRegions() {
        mapRegions.removeIf(region -> {
            region.players.remove(this);
            return true;
        });
    }

    public ArrayList<Region> getRegions() {
        return mapRegions;
    }

    public boolean queuedAppearanceUpdate() {
        return queuedAppearanceUpdate;
    }

    private GameMode mode = GameMode.TRAINED_ACCOUNT;

    public GameMode getGameMode() {
        return mode;
    }

    public GameMode setGameMode(GameMode mode) {
        this.mode = mode;
        return mode;
    }

    public void message(String message) {
        if (message == null) return;
        getPacketSender().sendMessage(message);
    }

    public void message(String format, Object... params) {
        if (format == null) return;
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
                System.out.println("[debug] " + String.format(format, params));
            }
        }
    }

    public void debugMessage(String message) {
        boolean debugMessagesEnabled = getAttribOr(AttributeKey.DEBUG_MESSAGES, true);
        if (PlayerRights.OWNER.equals(this.getPlayerRights()) && debugMessagesEnabled) {
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

    private Task distancedTask;
    public final Stopwatch afkTimer = new Stopwatch();
    public final Stopwatch prayerDrainTimer = new Stopwatch();

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

    /**
     * -- GETTER --
     *  Returns the single instance of the
     *  class for this player.
     *
     * @return the tracker class
     */
    @Getter
    private final BossKillLog bossKillLog = new BossKillLog(this);

    /**
     * -- GETTER --
     *  Returns the single instance of the
     *  class for this player.
     *
     * @return the tracker class
     */
    @Getter
    private final SlayerKillLog slayerKillLog = new SlayerKillLog(this);

    @Override
    public void autoRetaliate(Entity attacker) {
        if (dead() || hp() < 1) {
            return;
        }
        super.autoRetaliate(attacker);
    }

    @Override
    public void takehitSound(Hit hit) {
        if (hit == null) return;
    }

    @Override
    public void stopActions(boolean cancelMoving) {
        super.stopActions(cancelMoving);

        if (cancelMoving) {
            getMovementQueue().clear();
        }

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

    public final void sequence() {
        try {
            fireLogout();
            this.action.sequence();
            TaskManager.sequenceForMob(this);
            PacketInteractionManager.onPlayerProcess(this);
            this.getTimers().cycle();
            this.setPlayerQuestTabCycleCount(getPlayerQuestTabCycleCount() + 1);
            updateServerInformation(this);
            updateAccountStatus(this);
            GlobalStrings.PLAYERS_ONLINE.send(this, World.getWorld().getPlayers().size());
            var gametime = this.<Long>getAttribOr(GAME_TIME, 0L) + 1;
            this.putAttrib(GAME_TIME, gametime);
            LocalDateTime now = LocalDateTime.now();
            long minutesTillWildyBoss = now.until(WildernessBossEvent.getINSTANCE().next, ChronoUnit.MINUTES);
            if (GameServer.properties().autoRefreshQuestTab && getPlayerQuestTabCycleCount() == GameServer.properties().refreshQuestTabCycles) {
                this.setPlayerQuestTabCycleCount(0);
                this.updatePlayerPanel(this);
                this.getPacketSender().sendString(WORLD_BOSS_SPAWN.childId, QuestTab.InfoTab.INFO_TAB.get(WORLD_BOSS_SPAWN.childId).fetchLineData(this));

                if (minutesTillWildyBoss == 5) {
                    if (!WildernessBossEvent.ANNOUNCE_5_MIN_TIMER) {
                        WildernessBossEvent.ANNOUNCE_5_MIN_TIMER = true;
                        World.getWorld().sendWorldMessage("<col=6a1a18><img=2012>The world boss will spawn in 5 minutes, gear up!");
                    }
                }
            }
            if (this.<Boolean>getAttribOr(AttributeKey.NEW_ACCOUNT, false) && System.currentTimeMillis() - this.<Long>getAttribOr(LOGGED_IN_AT_TIME, System.currentTimeMillis()) > 1000 * 60 * 4) {
                this.requestLogout();
            }
            this.handleContainersDirty();
            this.getCombat().preAttack();
            TargetRoute.beforeMovement(this);
            this.getMovementQueue().process();
            TargetRoute.afterMovement(this);
            ControllerManager.process(this);
            this.handleLastRegion();
            this.getCombat().process();
            Prayers.drainPrayer(this);
            if (queuedAppearanceUpdate()) {
                this.getUpdateFlag().flag(Flag.APPEARANCE);
                this.setQueuedAppearanceUpdate(false);
            }
            if (!getChatMessageQueue().isEmpty()) {
                this.setCurrentChatMessage(getChatMessageQueue().poll());
                this.getUpdateFlag().flag(Flag.CHAT);
            } else setCurrentChatMessage(null);
        } catch (Exception e) {
            System.err.println("Error processing logic for Player: " + this);
            System.err.println(captureState());
            e.printStackTrace();
        }
    }

    private void handleLastRegion() {
        int lastregion = this.getAttribOr(AttributeKey.LAST_REGION, -1);
        int lastChunk = this.getAttribOr(AttributeKey.LAST_CHUNK, -1);
        if (lastregion != tile.region() || lastChunk != tile.chunk())
            MultiwayCombat.refresh(this, lastregion, lastChunk);
        this.putAttrib(AttributeKey.LAST_REGION, tile.region());
        this.putAttrib(AttributeKey.LAST_CHUNK, tile.chunk());
    }

    public int lastActiveOverhead;

    public void setLastActiveOverhead() {
        boolean[] actives = getPrayerActive();
        int forLastActive = -1;
        if (actives[16]) forLastActive = Prayers.PROTECT_FROM_MAGIC;
        if (actives[17]) forLastActive = Prayers.PROTECT_FROM_MISSILES;
        if (actives[18]) forLastActive = Prayers.PROTECT_FROM_MELEE;
        lastActiveOverhead = forLastActive;
    }

    void updateQuestTab() {
        getPacketSender().sendString(70005, Utils.capitalizeJustFirst(getDisplayName()));
        getPacketSender().sendString(70008, Integer.toString(getSkills().combatLevel()));
        getPacketSender().sendString(70011, Integer.toString(skills().totalLevel()));
        getPacketSender().sendString(70014, "Total XP: " + Color.GREEN.wrap(Utils.insertCommasToNumber(Long.toString(skills().getTotalExperience()))));
    }

    public transient long lastVoteClaim, lastSpellbookChange;

    public void switchSpellBook(MagicSpellbook book) {
        if (lastSpellbookChange > System.currentTimeMillis()) return;
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

    public Tile recentTeleport;

    public boolean combatDebug;

    public boolean soundmode;

    public int lastSoundId = 1;

    private InputScript inputScript;

    public void removeInputScript() {
        if (inputScript == null) return;
        inputScript = null;
    }

    public InputScript getInputScript() {
        return inputScript;
    }

    public void finishInputScript() {
        inputScript = null;
    }

    public <T> void setAmountScript(String title, InputScript<T> inputScript) {
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
            return this.tile().inArea(WildernessArea.getFeroxCenter) || this.tile().inArea(WildernessArea.getFeroxUpperNorth) || this.tile().inArea(WildernessArea.getFeroxNorthEntrance) || this.tile().inArea(WildernessArea.getFeroxNorthEdges) || this.tile().inArea(WildernessArea.getFeroxEastEdges) || this.tile().inArea(WildernessArea.getFeroxLowerSouth) || this.tile().inArea(WildernessArea.getFeroxLowerSouthEdges) || this.tile().inArea(WildernessArea.getFeroxSouthEntrance) || this.tile().inArea(WildernessArea.getFeroxRandomLine);
        } else {
            return false;
        }
    }

    public void drainRunEnergy() {
        boolean hamstrung = false;
        double energy = this.getAttribOr(AttributeKey.RUN_ENERGY, 0);
        double change = this.getEnergyDeprecation();
        int stamina = this.getAttribOr(AttributeKey.STAMINA_POTION_TICKS, 0);

        var skillingItems = SkillingItems.values();

        if (this.getEquipment().containsAny(AGILITY_CAPET, AGILITY_CAPE)) {
            return;
        }

        // Apply stamina potion effect
        if (stamina > 0) {
            change *= 0.3;
        }

        // Apply hamstrung effect
        if (this.getTimers().has(TimerKey.HAMSTRUNG)) {
            hamstrung = true;
        }

        // Only drain run energy if the player is running and has non-zero energy
        if (this.getMovementQueue().isRunning() && energy > 0) {
            // Calculate the modified change based on hamstrung state
            double modifiedChange = hamstrung ? change * 6 : change;

            for (var s : skillingItems) {
                if (this.getEquipment().hasAt(EquipSlot.RING, RING_OF_ENDURANCE)) {
                    modifiedChange *= s.getBoost();
                    break;
                }
            }

            // Calculate the new energy level after draining
            double newEnergy = energy - modifiedChange;

            // Ensure the energy level does not go below 0
            if (newEnergy < 0) {
                newEnergy = 0;
            }

            // Update the player's run energy level
            this.setRunningEnergy(newEnergy, true);
        }
    }

    @Getter
    @Setter
    boolean inTournamentLobby, tournamentSpectating;

    @Getter
    @Setter
    Tournament participatingTournament;

    public boolean inActiveTournament() {
        return participatingTournament != null;
    }

    @Getter
    @Setter
    Player tournamentOpponent;

}
