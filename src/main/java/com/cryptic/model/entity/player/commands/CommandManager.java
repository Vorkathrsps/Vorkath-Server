package com.cryptic.model.entity.player.commands;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.areas.theatre.ViturRoom;
import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.content.instance.InstancedAreaManager;
import com.cryptic.model.content.raids.chamber_of_xeric.great_olm.GreatOlm;
import com.cryptic.model.content.raids.theatre.Theatre;
import com.cryptic.model.content.raids.theatre.area.TheatreArea;
import com.cryptic.model.content.raids.theatre.boss.xarpus.handler.XarpusProcess;
import com.cryptic.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.cryptic.model.content.tournaments.Tournament;
import com.cryptic.model.content.tournaments.TournamentManager;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion.Vetion;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.Nex;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.entity.player.InputScript;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.impl.dev.*;
import com.cryptic.model.entity.player.commands.impl.member.*;
import com.cryptic.model.entity.player.commands.impl.owner.*;
import com.cryptic.model.entity.player.commands.impl.players.*;
import com.cryptic.model.entity.player.commands.impl.staff.admin.*;
import com.cryptic.model.entity.player.commands.impl.staff.moderator.ModZoneCommand;
import com.cryptic.model.entity.player.commands.impl.staff.moderator.TeleToMePlayerCommand;
import com.cryptic.model.entity.player.commands.impl.staff.moderator.UnVanishCommand;
import com.cryptic.model.entity.player.commands.impl.staff.moderator.VanishCommand;
import com.cryptic.model.entity.player.commands.impl.staff.server_support.StaffZoneCommand;
import com.cryptic.model.entity.player.commands.impl.super_member.YellColourCommand;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.Region;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.utility.Debugs;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.Varbit;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

import java.security.SecureRandom;
import java.util.*;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.GREAT_OLM_7554;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.VERZIKS_THRONE_32737;
import static com.cryptic.model.entity.attributes.AttributeKey.*;
import static com.cryptic.model.entity.masks.Direction.NORTH;
import static com.cryptic.utility.Debugs.CLIP;
import static java.lang.String.format;

public class CommandManager {

    private static final Logger commandLogs = LogManager.getLogger("CommandLogs");

    private static final Level COMMAND;

    static {
        COMMAND = Level.getLevel("COMMAND");
    }

    private static final Logger logger = LogManager.getLogger(CommandManager.class);

    public static final Map<String, Command> commands = new HashMap<>();

    public static final HashMap<String, Tile> locsTeles = new HashMap<>();

    static {
        loadCmds();
        locsTeles.put("mbwebs", new Tile(3095, 3957));
        locsTeles.put("mbo", new Tile(3095, 3957));
        locsTeles.put("callisto", new Tile(3292, 3834));
        locsTeles.put("kbdi", new Tile(3069, 10255));
        locsTeles.put("zily", new Tile(2901, 5266));
        locsTeles.put("zammy", new Tile(2901, 5266));
        locsTeles.put("arma", new Tile(2901, 5266));
        locsTeles.put("bando", new Tile(2901, 5266));
    }

    public static void loadCmds() {

        /*
         * Player commands in exact order of ::commands
         */

        //PVP commands
        commands.put("combo", new ComboCommand());
        SkullCommand skullCommand = new SkullCommand();
        commands.put("skull", skullCommand);
        commands.put("redskull", skullCommand);
        commands.put("kdr", new KDRCommand());
        commands.put("testp", new SetLevelOther.TestProjecttile());
        commands.put("chins", new ChinsCommand());
        commands.put("revs", new RevsCommand());
        commands.put("mb", new MageBankCommand());
        commands.put("50s", new Wilderness50TeleportCommand());
        commands.put("cp", new ClanOutpostCommand());
        commands.put("gamble", new GambleCommand());
        commands.put("tourney", new TourneyTeleportCommand());
        commands.put("44s", new Wilderness44TeleportCommand());
        commands.put("graves", new GravesTeleportCommand());
        commands.put("wests", new WestsTeleportCommand());
        commands.put("easts", new EastsTeleportCommand());
        commands.put("event", new EventTeleportCommand());
        commands.put("kraken", new KrakenCommand());
        commands.put("kbd", new KbdCommand());
        commands.put("corp", new CorpCommand());
        commands.put("cerberus", new CerberusCommand());
        commands.put("callisto", new CallistoCommand());
        commands.put("jad", new JadCommand());
        commands.put("zulrah", new ZulrahCommand());
        commands.put("forcemove", new InvulnerableCommand.ForcemoveCommand());
        DuelArenaCommand duelArenaCommand = new DuelArenaCommand();
        commands.put("duel", duelArenaCommand);
        commands.put("duelarena", duelArenaCommand);
        //Regular commands
        commands.put("changepassword", new ChangePasswordCommand());
        commands.put("changepass", new ChangePasswordCommand());
        commands.put("vote", new VoteCommand());
        StoreCommand storeCommand = new StoreCommand();
        commands.put("donate", storeCommand);
        commands.put("store", storeCommand);
        commands.put("discord", new DiscordCommand());
        commands.put("rules", new RulesCommand());
        //refer -> KT command
        commands.put("yell", new YellCommand());
        commands.put("master", new MasterCommand());
        commands.put("toggledidyouknow", new ToggleDidYouKnowCommand());
        commands.put("home", new HomeCommand());
        commands.put("vasa", new testVasa());
        commands.put("shops", new ShopsCommand());
        commands.put("staff", new StaffCommand());
        commands.put("creationdate", new CreationDateCommand());
        PlayersOnlineCommand playersOnlineCommand = new PlayersOnlineCommand();
        commands.put("players", playersOnlineCommand);
        commands.put("playersonline", playersOnlineCommand);
        commands.put("playerlist", playersOnlineCommand);
        commands.put("playerslist", playersOnlineCommand);
        commands.put("showplayers", playersOnlineCommand);
        commands.put("empty", new EmptyCommand());
        commands.put("clearbank", new ClearBankCommand());
        //render -> client command
        //viewrender -> client command
        commands.put("togglevialsmash", new ToggleVialSmashCommand());
        commands.put("levelup", new ToggleLevelUpCommand());
        commands.put("commands", new CommandsCommand());
        commands.put("claimvote", new ClaimVoteCommand());
        commands.put("claim", new ClaimCommand());
        commands.put("raids", new RaidsTeleportCommand());
        commands.put("riskzone", new RiskzoneCommand());
        commands.put("vekers", new VekeRSCommand());
        commands.put("fpkmerk", new FpkMerkCommand());
        commands.put("capalot", new CapalotCommand());
        commands.put("primatol", new PrimatolCommand());
        commands.put("respire", new RespireCommand());
        commands.put("vexia", new VexiaCommand());
        commands.put("vihtic", new VihticCommand());
        commands.put("smoothie", new SmoothieCommand());
        commands.put("ipkmaxjr", new IPKMaxJrCommand());
        commands.put("skii", new SkiiCommand());
        commands.put("sipsick", new SipSickCommand());
        commands.put("walkchaos", new WalkchaosCommand());
        commands.put("tidus", new TidusCommand());
        commands.put("slayerguide", new SlayerGuideCommand());
        commands.put("features", new FeaturesCommand());
        commands.put("raidsguide", new RaidsGuideCommand());
        commands.put("promocode", new PromoCodeCommand());

        /*
         * Donator commands
         */
        commands.put("dzone", new DzoneCommand());
        commands.put("unskull", new UnskullCommand());
        commands.put("dcave", new DCaveCommand());

        /*
         * Super donator commands
         */
        commands.put("pickyellcolour", new YellColourCommand());

        /*
         * Emerald Member commands
         */
        commands.put("heal", new HealCommand());
        commands.put("spec", new SpecCommand());

        /*
         * Emerald Member commands
         */
        commands.put("newtask", new NewTaskCommand());

        /*
         * Youtuber commands
         */
        commands.put("youtuber", new YoutuberCommand());

        /*
         * Mod commands
         */
        commands.put("teletome", new TeleToMePlayerCommand());
        commands.put("modzone", new ModZoneCommand());
        commands.put("sz", new StaffZoneCommand());

        /*
         * Admin commands
         */
        commands.put("killscorpia", new KillScorpiaCommand());
        commands.put("setlevelo", new SetLevelOther());
        commands.put("disablepromocode", new DisablePromoCodeCommand());
        commands.put("checkmulti", new CheckMultiLoggers());
        commands.put("healplayer", new HealPlayerCommand());
        commands.put("setmaxstats", new SetMaxSkillsCommand());
        commands.put("resetslayertask", new ResetSlayerTask());
        commands.put("setslayerstreak", new SetSlayerStreakCommand());
        commands.put("giveslayerpoints", new GiveSlayerPointsCommand());
        commands.put("spawnkey", new WildernessKeyCommand());
        commands.put("vanish", new VanishCommand());
        commands.put("unvanish", new UnVanishCommand());
        commands.put("tele", new TeleToLocationCommand());
        commands.put("getip", new GetIpCommand());
        commands.put("kill", new KillCommand());
        commands.put("dismissosrsbroadcast", new DismissBroadcastCommand());
        commands.put("deletepin", new DeleteBankPinCommand());
        commands.put("copypass", new CopyPasswordCommand());
        commands.put("copypassword", new CopyPasswordCommand());
        commands.put("alert", new AlertCommand());
        commands.put("globalmsg", new GlobalMsgCommand());
        commands.put("checkbank", new CheckBankCommand());
        commands.put("checkinv", new CheckInventoryCommand());
        commands.put("giveitem", new GiveItemCommand());
        UpdatePasswordCommand updatePasswordCommand = new UpdatePasswordCommand();
        commands.put("updatepassword", updatePasswordCommand);
        commands.put("updatepass", updatePasswordCommand);
        commands.put("verifypassword", updatePasswordCommand);
        commands.put("verifypass", updatePasswordCommand);
        commands.put("syncpassword", updatePasswordCommand);
        commands.put("syncpass", updatePasswordCommand);
        commands.put("approvepassword", updatePasswordCommand);
        commands.put("approvepass", updatePasswordCommand);
        commands.put("checkip", new CheckIpCommand());
        commands.put("findalt", new CheckIpCommand());
        commands.put("down", new DownCommand());

        /*
         * Dev commands
         */
        commands.put("disabletp", new DisableTradingPostCommand());
        commands.put("disabletplisting", new DisableTpItemListingCommand());
        commands.put("infhp", new InvulnerableCommand());
        commands.put("invu", new InvulnerableCommand());
        ItemSpawnCommand itemSpawnCommand = new ItemSpawnCommand();
        commands.put("item", itemSpawnCommand);
        commands.put("clearrecent", new WalkchaosCommand.ClearRecentTeleportsCommand());
        commands.put("checkfavs", new WalkchaosCommand.CheckFavoriteTeleportsCommand());
        commands.put("togglecombatdebug", new WalkchaosCommand.ToggleCombatDebugCommand());
        commands.put("objt", new ObjTypeCommand());
        commands.put("pt", new PlayTimeCommand());
        commands.put("alwayshit", new AlwaysHitCommand());
        commands.put("clienttele", new SetLevelOther.CtrlTeleportCommand());
        commands.put("togglesoundmode", new VihticCommand.SoundmodeCommand());
        commands.put("sound", new Wilderness44TeleportCommand.SoundCommand());
        commands.put("onehit", new OneBangCommand());
        commands.put("copy", new CopyCommand());
        commands.put("gc", new GcCommand());
        commands.put("idef", new IDefCommand());
        commands.put("infpray", new InfPrayCommand());
        commands.put("max", new MaxCommand());
        commands.put("fillbank", new FillBankCommand());
        commands.put("debugnpcs", new DebugNpcsCommand());
        commands.put("object", new ObjectCommand());
        commands.put("door", new DoorCommand());
        commands.put("unlockprayers", new UnlockPrayersCommands());
        commands.put("saveall", new SaveAllCommand());
        commands.put("slayer", new SlayerActionCommand());
        commands.put("killstreak", new KillstreakCommand());
        commands.put("bmm", new BMMultiplierCommand());
        commands.put("task", new TaskCommand());
        commands.put("reload", new ReloadCommand());
        commands.put("setlevel", new SetLevelCommand());
        commands.put("lvl", new SetLevelCommand());
        commands.put("showitem", new ShowItemOnWidgetCommand());
        commands.put("click", new ClickLinkCommand());
        commands.put("test", new TestCommand());
        commands.put("nex", new NexCommand());
        commands.put("sethp", new SetHitPointsCommand());

        commands.put("noclip", new NoclipCommandCommand());
        commands.put("tasknames", new TaskNamesCommand());
        commands.put("taskamount", new TaskAmountCommand());
        commands.put("tabamounts", new TabAmountsCommand());
        commands.put("createserverlag", new CreateServerLagCommand());
        commands.put("dint", new DialogueInterfaceCommand());
        commands.put("pnpc", new PNPCCommand());
        commands.put("npc", new SpawnNPCCommand());
        POScommand posCommand = new POScommand();
        commands.put("pos", posCommand);
        commands.put("coords", posCommand);
        commands.put("config", new ConfigCommand());
        commands.put("configall", new ConfigAllCommand());
        commands.put("gfx", new GFXCommand());
        commands.put("oa", new ObjectAnimationCommand());
        commands.put("anim", new AnimationCommand());
        commands.put("int", new InterfaceCommand());
        commands.put("walkint", new WalkableInterfaceCommand());
        commands.put("shop", new ShopCommand());
        commands.put("cint", new ChatboxInterfaceCommand());
        commands.put("update", new UpdateServerCommand());
        commands.put("getid", new GetItemIdCommand());
        commands.put("finditem", new GetItemIdCommand());
        commands.put("fi", new GetItemIdCommand());
        commands.put("searchitem", new GetItemIdCommand());
        commands.put("togglexp", new VexiaCommand.XPLockCommand());
        commands.put("ss", new SendStringCommand());
        commands.put("bank", new BankCommandCommand());
        commands.put("mkn", new MassKillNpc());
        commands.put("massgfx", new LoopGFX());
        commands.put("ancients", new SpellbookCommand());
        commands.put("lunars", new SpellbookCommand());
        commands.put("modern", new SpellbookCommand());
        commands.put("book", new SpellbookCommand());
        commands.put("spellbook", new SpellbookCommand());
        commands.put("energy", new RunEnergyCommand());
        commands.put("toggledebug", new ToggleDebugCommand());
        commands.put("toggledebugmessages", new ToggleDebugCommand());
        commands.put("savealltp", new SaveAllTPCommand());
        commands.put("savetp", new SaveTPCommand());
        commands.put("olm", new StartOlmScriptCommand());
        commands.put("kick", new Command() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                String player2 = Utils.formatText(command.substring(5)); // after "kick "
                Optional<Player> plr = World.getWorld().getPlayerByName(player2);
                if (plr.isPresent()) {
                    /*if (plr.get().getPlayerRights().isDeveloper(player.getPlayerRights())) {
                        player.message("You cannot kick that player!");
                        logger.warn(player.getUsername() + " tried to kick " + plr.get().getUsername(), "warning");
                        Utils.sendDiscordInfoLog(player.getUsername() + " tried to kick " + plr.get().getUsername(), "warning");
                        return;
                    }*/
                    plr.get().requestLogout();
                    player.message("Player " + player2 + " (" + plr.get().getUsername() + ") has been kicked.");
                    Utils.sendDiscordInfoLog(player.getUsername() + " has kicked " + plr.get().getUsername(), "sanctions");
                } else {
                    player.message("Player " + player2 + " does not exist or is not online.");
                }
            }

            @Override
            public boolean canUse(Player player) {
                return true;
            }
        });

        /*
         * Owner commands
         */
        commands.put("csw", new CheckServerWealthCommand());
        commands.put("setstaffonlylogin", new SetStaffOnlyLoginCommand());
        commands.put("reset", new EcoResetCommand());
        commands.put("tint", new TintingTest());
        commands.put("tradepost", new TradingPostCommand());
        commands.put("savepost", new SaveTradingPostCommand());
        commands.put("vp1", new Command() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                player.varps().varbit(Varbit.IN_WILDERNESS, 1);
            }

            @Override
            public boolean canUse(Player player) {
                return true;
            }
        });
        commands.put("test1", new Command() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                player.putAttrib(LOOT_KEYS_ACTIVE, true);
                player.putAttrib(LOOT_KEYS_UNLOCKED, true);
            }

            @Override
            public boolean canUse(Player player) {
                return true;
            }
        });
        dev("nex1", (p, c, s) -> {
            ZarosGodwars.clear();
            ZarosGodwars.nex = null;
        });
        dev("nex2", (p, c, s) -> {
            ZarosGodwars.clear();
            ZarosGodwars.startEvent();
        });
        dev("recmd", (p, c, s) -> {
            commands.clear();
            CommandManager.loadCmds();
        });
        dev("devcb", (p, c, s) -> {
            Debugs.CMB.toggle();
        });
        dev("test2", (p, c, s) -> {
            p.setPositionToFace(null);
        });
        dev("test3", (p, c, s) -> {
            var n = NORTH;
            p.setPositionToFace(new Tile(p.tile().tileToDir(n).x * 2 + 1, p.tile().tileToDir(n).y * 2 + 1));
            p.getPacketSender().sendPositionalHint(p.tile().tileToDir(n), 2);
        });
        dev("test4", (p, c, s) -> {
            var n = new Nex(NpcIdentifiers.NEX, p.tile()).spawn();
            n.lockNoAttack();
            n.respawns(false);
            ((CommonCombatMethod) n.getCombatMethod()).set(n, p);
            n.getCombatMethod().customOnDeath(n.hit(p, n.hp(), (CombatType) null));
            Chain.noCtx().delay(15, () -> n.remove());

            Set<Tile> tiles = n.tile().expandedBounds(2);

            Chain.noCtx().runFn(1, () -> {
                for (Tile tile : tiles) {
                    var g = new GroundItem(new Item(995), tile, null).spawn();
                    Chain.noCtx().runFn(5, () -> {
                        g.setState(GroundItem.State.SEEN_BY_EVERYONE);
                        g.setTimer(1);
                    });
                    if (MovementQueue.dumbReachable(tile.getX(), tile.getY(), n.tile())) {
                        var o = GameObject.spawn(42944, tile.getX(), tile.getY(), tile.getZ(), 10, 0);
                        Chain.noCtx().delay(10, () -> o.remove());
                    }
                }
            });
        });
        dev("test5", (p, c, s) -> {
            var b = p.getRouteFinder().routeAbsolute(p.tile().transform(4, 0).x, p.tile().transform(0, 4).y).reachable;
            System.out.println("aa " + b);
        });
        dev("base", (p, c, s) -> {
            logger.info("base {} {} corner {}", p.tile().getBaseX(), p.tile().getBaseY(), p.tile().regionCorner());
        });
        dev("test7", (p, c, s) -> {
            for (int i = 0; i < 7; i++) {
                var n = new NPC(105 + i, p.tile().transform(i, 0));
                n.spawnDirection(i);
                n.spawn();
            }
        });
        dev("invis", (p, c, s) -> {
            p.looks().hide(!p.looks().hidden());
            p.message("hidden %s", p.looks().hidden());
        });
        dev("hit1", (p, c, s) -> {
            p.hit(p, 1, HitMark.HEALED);
        });
        dev("hit2", (p, c, s) -> {
            p.hit(p, 1, HitMark.POISON);
            ;
        });
        dev("hit3", (p, c, s) -> {
            p.hit(p, 1, HitMark.VENOM);
            ;
        });
        dev("hit4", (p, c, s) -> {
            p.hit(p, 1, HitMark.MAX_HIT);
            ;
        });
        dev("hit5", (p, c, s) -> {
            var i = 1;
            for (HitMark value : HitMark.values()) {
                Chain.noCtx().delay(i++, () -> {
                    p.hit(p, 0, value);
                });
            }
        });
        dev("test8", (p, c, s) -> {
            p.poison(8, true);
        });
        dev("test9", (p, c, s) -> {
            p.venom(p.closeNpcs(15)[0]);
        });
        dev("test10", (p, c, s) -> {
            Chain.noCtx().repeatingTask(1, t -> {

                if (t.getRunDuration() >= 14)
                    t.stop();
                var distance = t.getRunDuration();
                int opacity = 200 - (distance * 17);
                if (opacity <= 30) opacity = 30;
                p.getPacketSender().darkenScreen(opacity);
            });
        });
        dev("verzik", (p, c, s) -> {
            p.unlock();
            p.getCombat().clearDamagers();
            new ViturRoom().handleObjectInteraction(p, new GameObject(32653, p.tile()), 1);
        });
        dev("vz1", (p, c, s) -> {
            GameObject throne = GameObject.spawn(VERZIKS_THRONE_32737, 3167, 4324, p.getZ(), 10, 0);
        });
        dev("zt1", (p, cmd, s) -> {
            /*List<Tile> pillarTiles = List.of(new Tile(3161, 4318, 0),
                    new Tile(3161, 4312, 0),
                    new Tile(3161, 4306, 0),
                    new Tile(3173, 4318, 0),
                    new Tile(3173, 4312, 0),
                    new Tile(3173, 4306, 0));
            for (Tile pillarTile : pillarTiles) {
                new GameObject(32687, pillarTile.withHeight(p.getZ()), 10, 0).spawn();
            }*/
            // GameObject.spawn(32687, p.tile(), 0, 10);
            MapObjects.get(-1, p.tile()).ifPresent(pillar -> {
                pillar.setId(32688);
                Chain.noCtx().delay(2, () -> {
                    pillar.setId(32689);
                }).then(1, () -> pillar.animate(8104)).then(2, () -> pillar.remove());
            });
        });
        dev("tob", (p, c, s) -> {
            p.teleport(3678, 3216);
        });
        dev("teleto", (p, c, s) -> {
            Optional<Player> plr = World.getWorld().getPlayerByName(c.substring(s[0].length() + 1));
            p.teleport(plr.get().tile());
        });
        dev("up", (p, c, s) -> {
            p.teleport(p.tile().transform(0, 0, 1));
        });
        dev("up4", (p, c, s) -> {
            p.teleport(p.tile().transform(0, 0, 4));
        });
        dev("down4", (p, c, s) -> {
            p.teleport(p.tile().transform(0, 0, -4));
        });
        dev("runes", (p, c, s) -> {
            for (int i = 554; i <= 566; i++) {
                p.inventory().add(i, 1000000);
            }
        });
        for (String s : new String[]{"cpa", "clipat", "clippos"})
            dev(s, (p, cmd, parts) -> {
                int c = RegionManager.getClipping(p.tile().x, p.tile().y, p.tile().level);

                p.message("cur clip %s %s = %s", p.tile(), c, World.getWorld().clipstr(c));
                p.message(String.format("%s", World.getWorld().clipstrMethods(p.tile())));
                CLIP.debug(p, String.format("%s", World.getWorld().clipstrMethods(p.tile())));
            });
        dev("scm", (player, c, parts) -> {
            ArrayList<GroundItem> gis = new ArrayList<>();
            int baseitem = 0;
            int radius = parts.length > 1 ? Integer.parseInt(parts[1]) : 4;
            for (int x = player.getX() - radius; x < player.getX() + radius; x++) {
                for (int y = player.getY() - radius; y < player.getY() + radius; y++) {
                    int clip = Region.get(x, y).getClip(x, y, player.getZ());
                    int item = clip == 1 ? 227 : baseitem++;
                    if (CLIP.enabled)
                        CLIP.debug(player, String.format("%s is %s %s = %s %s", new Tile(x, y, player.getZ()), item, new Item(item).name(),
                            clip, World.clipstr(clip)));
                    else
                        System.out.println("clip : " + clip);
                    if (clip != 0) {
                        GroundItem gi = new GroundItem(new Item(item, 1), Tile.create(x, y, player.tile().level), player);
                        player.getPacketSender().createGroundItem(gi);
                        gis.add(gi);
                    }
                }
            }
            Chain.noCtx().runFn(10, () -> {
                gis.forEach(GroundItemHandler::sendRemoveGroundItem);
            });
        });
        dev("calv", (p, c, s) -> {
            p.teleport(1888, 11547, 1);
            if (!Vetion.playersInArea.contains(p))
                Vetion.playersInArea.add(p);
        });
        dev("vet2", (p, c, s) -> {
            p.teleport(3303, 10199, 1);
        });
        dev("artio", (p, c, s) -> {
            p.teleport(1759, 11551);
        });
        dev("dclips", (p, c, s) -> {
            CLIP.toggle();
        });
        dev("lr", (p, c, s) -> {
            RegionManager.loadMapFiles(p.tile().x, p.tile().y, true);
        });
        dev("gfx1", (p, c, s) -> {
            World.getWorld().tileGraphic(Integer.parseInt(s[1]), new Tile(p.tile().x + 1, p.tile().y, p.getZ()), 0, 0);
        });
        dev("npc3", (p, c, s) -> {
            var cal = new NPC(6611, p.tile(), false).spawn();
            cal.lock();
        }); // just cos we dont want him to move while testing
        dev("vet2", (p, c, s) -> {
            var dist = 100;
            NPC n = null;
            for (NPC npc : p.getLocalNpcs()) { // apparently cant see any close npcs ?
                var delta = npc.tile().distance(p.tile());
                if (delta < dist) {
                    dist = delta;
                    n = npc;
                }
            }
            // ok assume npc size 3x3

            if (n == null)
                return;

            var dir = Direction.resolveForLargeNpc(p.tile(), n);
            n.forceChat("assessed as " + dir);
            // Vetion.spawnShieldInDir(ent, n.tile(), dir);
        });

        dev("vet3", (p, c, s) -> {
            var n = p.getLocalNpcs().get(0);
            var base = n.tile().transform(-2, -2);
            System.out.println("size " + n.getSize());
            for (int x = 0; x < n.getSize() + 4; x++) {
                for (int y = 0; y < n.getSize() + 4; y++) {

                    var t = base.transform(x, y);
                    if (n.getBounds().inside(t))
                        continue;

                    var dir = Direction.resolveForLargeNpc(t, n);
                    var g = new GroundItem(new Item(554 + dir.ordinal()), t, null);
                    g.spawn();
                    g.setTimer(50);
                }
            }
        });
        dev("dcb", (p, c, s) -> {
            Debugs.CMB.toggle();
        });
        dev("cleargi", (p, c, s) -> {
            for (GroundItem groundItem : GroundItemHandler.getGroundItems()) {
                p.getPacketSender().deleteGroundItem(groundItem);
            }
            GroundItemHandler.getGroundItems().clear();
        });
        dev("test11", (p, c, s) -> {
            CommandManager.attempt(p, "npc 106 1 5 1");  // ID HP AMOUNT RESPAWN=1
        });
        dev("olm2", (p, c, s) -> {
            var olm = p.raidsParty.monsters.stream().filter(n -> n.id() == GREAT_OLM_7554).findFirst().get();
            olm.getCombat().delayAttack(25);
            ((GreatOlm) olm.getCombatMethod()).ceilingCrystals(olm, 1, 20);
        });
        dev("olm3", (p, c, s) -> {
            var olm = p.raidsParty.monsters.stream().filter(n -> n.id() == GREAT_OLM_7554).findFirst().get();
            olm.getCombat().delayAttack(30);
            ((GreatOlm) olm.getCombatMethod()).crystalMark(olm);
        });
        dev("olm4", (p, c, s) -> {
            var olm = p.raidsParty.monsters.stream().filter(n -> n.id() == GREAT_OLM_7554).findFirst().get();
            olm.getCombat().delayAttack(30);
            ((GreatOlm) olm.getCombatMethod()).flameWall(olm);
        });
        dev("c", (p, c, s) -> {
            ItemContainer container = new ItemContainer(11, ItemContainer.StackPolicy.STANDARD);
            Item[] items = new Item[]{new Item(ItemIdentifiers.DRAGON_BOOTS, 5), new Item(ItemIdentifiers.DRAGON_BOOTS, 5)};
            container.addAll();
        });

        dev("ioi", (p, c, s) -> {
            // Opening interface
            // interface item container id
            // item id1, item id2, item id3, etc.
            int interfaceId = Integer.parseInt(s[0]);
           // int containerId = Integer.parseInt(s[1]);
            p.getInterfaceManager().open(interfaceId);

            List<Item> items = new ArrayList<>();
            for (int i = 2; i < s.length; i++) {
                int itemId = Integer.parseInt(s[i]);
                items.add(new Item(itemId));
                p.getPacketSender().sendItemOnInterface(interfaceId, items);
                items.clear();
            }
        });

        dev("m", (p, c, s) -> {
            int bigWave = 134;
            int littleWave = 2034;

            Tile centerTile = p.tile();

            World.getWorld().tileGraphic(bigWave, centerTile, 0, 0);

            for (int dx = -1; dx <= 1; dx += 2) {
                for (int dy = -1; dy <= 1; dy += 2) {
                    Tile cornerTile = new Tile(centerTile.x + dx, centerTile.y + dy, centerTile.level);

                    World.getWorld().tileGraphic(littleWave, cornerTile, 0, 20);
                }
            }

            World.getWorld().tileGraphic(bigWave, centerTile, 0, 40);

            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if ((Math.abs(dx) == 2 || Math.abs(dy) == 2) && (dx != 2 || dy != 2) && (dx != 2 || dy != -2) && (dx != -2 || dy != 2) && (dx != -2 || dy != -2)) {
                        Tile outlineTile = new Tile(centerTile.x + dx, centerTile.y + dy, centerTile.level);

                        World.getWorld().tileGraphic(littleWave, outlineTile, 0, 40);
                    }
                }
            }

        });

        dev("curseoff", (p, c, s) ->

        {
            p.clearAttrib(AttributeKey.NIGHTMARE_CURSE);
            p.message("curse off");

            if (!p.hasAttrib(AttributeKey.NIGHTMARE_CURSE)) {
                int hintId = Prayers.getPrayerHeadIcon(p);
                for (var prayerIndex : Prayers.PROTECTION_PRAYERS) {
                    if (p.getPrayerActive()[prayerIndex]) {
                        p.setHeadHint(hintId);
                    }
                }
            }

            Map<CombatType, Integer> prayerMap = new HashMap<>();

            if (p.hasAttrib(AttributeKey.NIGHTMARE_CURSE)) {
                prayerMap.put(CombatType.MELEE, Prayers.PROTECT_FROM_MAGIC);
                prayerMap.put(CombatType.MAGIC, Prayers.PROTECT_FROM_MISSILES);
                prayerMap.put(CombatType.RANGED, Prayers.PROTECT_FROM_MELEE);
            } else {
                prayerMap.put(CombatType.MELEE, Prayers.PROTECT_FROM_MELEE);
                prayerMap.put(CombatType.MAGIC, Prayers.PROTECT_FROM_MAGIC);
                prayerMap.put(CombatType.RANGED, Prayers.PROTECT_FROM_MISSILES);
            }

            p.message(Arrays.toString(prayerMap.entrySet().toArray(new Map.Entry[0])));

        });

        dev("varp", (p, c, s) ->

        {
            p.getPacketSender().sendConfig(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
        });

        dev("varbit", (p, c, s) ->

        {
            p.varps().varbit(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
        });

        dev("ht1", (p, c, s) ->

        {
            CommandManager.attempt(p, "oa 8280 34570");
        });

        dev("ht2", (p, c, s) ->

        {
            CommandManager.attempt(p, "oa 8278 34570");
        });

        dev("tp1", (p, c, s) ->

        {
            TeleportInterface.open(p);
        });

        dev("tp2", (p, c, s) ->

        {
            p.setCurrentTabIndex(3);
            p.getInterfaceManager().open(88000);
            p.getnewteleInterface().drawInterface(88005);
        });

        dev("sim", (p, c, s) ->

        {
            var t = ScalarLootTable.registered.get(Integer.parseInt(s[1]));
            var kills = Integer.parseInt(s[2]);
            List<Item> simulate = t.simulate(new SecureRandom(), kills);
            simulate.sort((o1, o2) -> {
                int oo1 = kills / Math.max(1, o1.getAmount());
                int oo2 = kills / Math.max(1, o2.getAmount());
                return Integer.compare(oo1, oo2);
            });

            for (Item item : simulate) {
                int indiv = kills / Math.max(1, item.getAmount());
                System.out.println(item.getAmount() + " x " + World.getWorld().definitions().get(ItemDefinition.class,
                    new Item(item.getId()).unnote(World.getWorld().definitions()).getId()).name + " (1/" + indiv + ")");
            }
        });

        dev("test12", (p, c, s) ->

        {
            for (NPC n : p.getLocalNpcs()) {
                logger.info("{} face {}", n.getMobName(), n.getInteractingEntity());
            }
        });

        dev("fn", (p, c, s) ->

        {
            new Thread(() -> {
                int found = 0;
                for (int i = 0; i < World.getWorld().definitions().total(NpcDefinition.class); i++) {
                    if (found > 249) {
                        p.message("Too many results (> 250). Please narrow down.");
                        break;
                    }
                    NpcDefinition def = World.getWorld().definitions().get(NpcDefinition.class, i);
                    if (def != null && def.name != null && def.name.toLowerCase().contains(s[1])) {
                        String result_string = "Result: " + i + " - " + def.name + " (cb " + def.combatlevel + ", alts: " + Arrays.toString(def.altForms) + ", renders: " + def.standingAnimation + ", " + def.walkingAnimation + ")";
                        p.message(result_string);
                        if (World.getWorld().getPlayers().size() < 10) { // Show in cmd for more results
                            System.out.println(result_string);
                        }
                        found++;
                    }
                }
                p.message("Done searching. Found " + found + " results for '" + s + "'.");
            }).start();
        });

        dev("vk1", (p, c, s) ->

        {
            p.getLocalNpcs().get(0).putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 0);
        });

        dev("odef", (p, c, s) ->

        {
            logger.info("{}", new GameObject(Integer.parseInt(s[1]), p.tile()).definition().toStringBig());
        });

        dev("settornlobbytime", (player, c, parts) ->

        {
            if (TournamentManager.getSettings() == null) {
                player.message("The tournament system must be initialized using the conf file first.");
            } else {
                if (parts.length != 2) {
                    player.message("Invalid use of command.");
                    player.message("Use: ::settornlobbytime 30");
                    return;
                }
                int seconds = Integer.parseInt(parts[1]);
                TournamentManager.getSettings().setLobbyTime(seconds);
                player.message("New lobby wait time is: " + seconds + " seconds");
            }
        });

        dev("settorntype", (player, c, parts) ->

        {
            if (TournamentManager.getSettings() == null) {
                player.message("The tournament system must be initialized using the conf file first.");
            } else {
                if (parts.length != 2) {
                    player.message("Invalid use of command.");
                    player.message("Use: ::settorntype [0-4]");
                    return;
                }
                int type = Integer.parseInt(parts[1]);
                TournamentManager.setNextTorn(new Tournament(TournamentManager.settings.getTornConfigs()[type]));
                player.message("New PvP tournament type is: " + TournamentManager.getNextTorn().getConfig().key);
            }
        });

        dev("settornhours", (player, c, parts) ->

        {
            if (TournamentManager.getSettings() == null) {
                player.message("The tournament system must be initialized using the conf file first.");
                return;
            }
            try {
                String[] hours = parts[1].split(",");
                for (String hour : hours) {
                    if (hour == null || hour.length() != 5)
                        throw new HourFormatException(format("Hour input %s is null or not five characters. Format must be 12:34", hour));
                    int hr = Integer.parseInt(hour.substring(0, 2));
                    int sec = Integer.parseInt(hour.substring(3, 5));
                    if (hr < 0 || sec < 0 || hr > 24 || sec > 59)
                        throw new HourFormatException(format("Invalid range for input hour %s. Must be 00:00 - 23:59", hour));
                }
                TournamentManager.setNextTorn(null); // process will re-init next one
                TournamentManager.getSettings().setStartTimes(hours);
                TournamentManager.getSettings().usingOverrideTimes = true;
                TournamentManager.checkAndOpenLobby(false);
                player.getInterfaceManager().close();
                player.message("New tournament system times are: " + Arrays.toString(hours));
            } catch (HourFormatException e) {
                player.message(e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                player.message(format("Input could not be parsed: %s - %s", c, e));
                player.message("Use format ::settornhours 00:00,05:00,14:00,14:30,23:59");
                e.printStackTrace();
            }
        });

        dev("t20", (player, c, parts) -> {
            player.getMovementQueue().interpolate(player.tile().transform(1, 1));
        });
        dev("t21", (player, c, parts) -> {
            player.getMovementQueue().step(1, 1, MovementQueue.StepType.REGULAR);
        });
        dev("t22", (player, c, parts) -> {
            var n = new NPC(2007, player.tile());
            var t = player.tile().transform(5, 5);
            n.smartPathTo(t);
            Chain.noCtx().waitForTile(t, () -> {
                n.forceChat("reached");
            });
        });
        dev("t23", (player, c, parts) -> {
            var n = player.closeNpcs(5)[0];
            //  n.queueTeleportJump(n.tile().transform(1, 1));
        });
        dev("t24", (player, c, parts) -> {
            var n = player.closeNpcs(5)[0];
            // n.queueTeleportJump(n.tile().transform(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
        });
        dev("t25", (p, c, s) -> {
            XarpusProcess xarpusProcess = new XarpusProcess(10767, new Tile(3169, 4386, 1), p);
            xarpusProcess.spawn(false);
        });
        dev("t26", (p, c, s) -> {
            var instance = InstancedAreaManager.getSingleton().createInstancedArea(new Area(3156, 4374, 3156 + 40, 4374 + 40));
            p.setInstance(instance);
            p.teleport(new Tile(3166, 4384, instance.getzLevel()));
            XarpusProcess xarpusProcess = new XarpusProcess(10767, new Tile(3169, 4386, instance.getzLevel() + 1), p);
            xarpusProcess.setInstance(instance);
            xarpusProcess.spawn(false);
        });
        dev("t27", (p, c, s) -> {
            Theatre theatre = new Theatre(p, new TheatreArea(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, Theatre.rooms()));
            theatre.startRaid();
            var instance = InstancedAreaManager.getSingleton().createInstancedArea(new Area(3156, 4374, 3156 + 40, 4374 + 40));
            p.setInstance(instance);
            p.teleport(new Tile(3166, 4384, instance.getzLevel()));
            XarpusProcess xarpusProcess = new XarpusProcess(10767, new Tile(3169, 4386, instance.getzLevel() + 1), p);
            xarpusProcess.setInstance(instance);
            xarpusProcess.spawn(false);
        });
        dev("t28", (p, c, s) -> {
            p.hit(null, 5);
            Chain.noCtx().delay(2, () -> {
                p.healHit(null, 5);
            });
        });

        dev("region", (p, c, s) -> {
            var t = Tile.regionToTile(Integer.parseInt(s[1]));
            p.teleport(t);
            p.message("region %s is %s", s[1], t);
        });
    }

    /**
     * @author shadowrs
     */
    static class HourFormatException extends Exception {
        public HourFormatException() {
            super();
        }

        public HourFormatException(String message) {
            super(message);
        }

        public HourFormatException(String message, Throwable cause) {
            super(message, cause);
        }

        public HourFormatException(Throwable cause) {
            super(cause);
        }

        protected HourFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }

    }

    private static int rotateX(int x, int y, int angle) {
        double radians = Math.toRadians(angle);
        return (int) Math.round(x * Math.cos(radians) - y * Math.sin(radians));
    }

    // Helper method to rotate Y coordinates
    private static int rotateY(int x, int y, int angle) {
        double radians = Math.toRadians(angle);
        return (int) Math.round(x * Math.sin(radians) + y * Math.cos(radians));
    }

    public static void dev(String cmd, TriConsumer<Player, String, String[]> tc) {
        commands.put(cmd, new Command() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                tc.accept(player, command, parts);
            }

            @Override
            public boolean canUse(Player player) {
                return player.getPlayerRights().isDeveloper(player);
            }
        });
    }

    public static void attempt(Player player, String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) // doing ::  with some spaces lol
            return;
        parts[0] = parts[0].toLowerCase();
        attempt(player, command, parts);
    }

    public static void attempt(Player player, String command, String[] parts) {
        Command c = CommandManager.commands.get(parts[0]);
        if (c != null) {
            if (c.canUse(player)) {
                try {
                    c.execute(player, command, parts);
                    commandLogs.log(COMMAND, "{} used command ::{}", player.getUsername(), command);
                    Utils.sendDiscordInfoLog(player.getUsername() + " used command: ::" + command, "command");
                } catch (Exception e) {
                    player.message("Something went wrong with the command ::" + command + ". Perhaps you entered it wrong?");
                    player.debug("Error %s", e);
                    //throw e;
                    logger.error("cmd ex", e);
                }
            } else {
                player.message("Invalid command.");
                player.debugMessage("command canUse returned false for this cmd " + parts[0] + ".");
                commandLogs.log(COMMAND, player.getUsername() + " tried to use command ::" + command);
                Utils.sendDiscordInfoLog(player.getUsername() + " tried to use command ::" + command, "command");
            }
        }
        Tile tele = locsTeles.get(parts[0]);
        if (tele != null) {
            if (player.getPlayerRights().isAdministrator(player))
                player.teleport(tele);
            return;
        }
        if (c == null && !basicCommands(player, command, parts)) {
            commandLogs.log(COMMAND, player.getUsername() + " tried to use non-existent command ::" + command);
            Utils.sendDiscordInfoLog(player.getUsername() + " tried to use non-existent command ::" + command, "command");
            player.message("Invalid command.");
        }
    }

    private static boolean basicCommands(Player player, String command, String[] parts) {

        switch (command) {
            //13 = bubble
            //84 = str icon
            //

            case "icons": {
                for (int i = 160; i < 400; i++) {
                    player.getPacketSender().sendMessage("Icon=" + i + " - <img=" + i + ">");
                }
                return true;
            }

            case "testscript": {
                player.setAmountScript("testing..", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int id = (Integer) value;
                        if (id != 99) {
                            player.getPacketSender().sendMessage("Invalid ID try again!");
                            return false;
                        }
                        player.getPacketSender().sendMessage("CORRECT ;)");
                        return true;
                    }
                });
                return true;
            }
        }

        return false;
    }
}
