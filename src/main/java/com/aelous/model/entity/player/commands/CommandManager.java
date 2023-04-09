package com.aelous.model.entity.player.commands;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.content.areas.theatre.ViturRoom;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion.Vetion;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.Nex;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.impl.dev.*;
import com.aelous.model.entity.player.commands.impl.member.*;
import com.aelous.model.entity.player.commands.impl.owner.*;
import com.aelous.model.entity.player.commands.impl.players.*;
import com.aelous.model.entity.player.commands.impl.staff.admin.*;
import com.aelous.model.entity.player.commands.impl.staff.moderator.ModZoneCommand;
import com.aelous.model.entity.player.commands.impl.staff.moderator.TeleToMePlayerCommand;
import com.aelous.model.entity.player.commands.impl.staff.moderator.UnVanishCommand;
import com.aelous.model.entity.player.commands.impl.staff.moderator.VanishCommand;
import com.aelous.model.entity.player.commands.impl.staff.server_support.StaffZoneCommand;
import com.aelous.model.entity.player.commands.impl.super_member.YellColourCommand;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.Region;
import com.aelous.model.map.region.RegionManager;
import com.aelous.utility.Debugs;
import com.aelous.utility.Utils;
import com.aelous.utility.Varbit;
import com.aelous.utility.chainedwork.Chain;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.VERZIKS_THRONE_32737;
import static com.aelous.model.entity.attributes.AttributeKey.LOOT_KEYS_ACTIVE;
import static com.aelous.model.entity.attributes.AttributeKey.LOOT_KEYS_UNLOCKED;
import static com.aelous.model.entity.masks.Direction.*;
import static com.aelous.utility.Debugs.CLIP;

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
        commands.put("spellbook", new SpellbookCommand());
        commands.put("energy", new RunEnergyCommand());
        commands.put("toggledebug", new ToggleDebugCommand());
        commands.put("toggledebugmessages", new ToggleDebugCommand());
        commands.put("savealltp", new SaveAllTPCommand());
        commands.put("savetp", new SaveTPCommand());
        commands.put("olm", new StartOlmScriptCommand());

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
            p.setPositionToFace(new Tile(p.tile().tileToDir(n).x * 2 + 1, p.tile().tileToDir(n).y * 2 +1));
            p.getPacketSender().sendPositionalHint(p.tile().tileToDir(n), 2);
        });
        dev("test4", (p, c, s) -> {
            var n = new Nex(NpcIdentifiers.NEX, p.tile()).spawn();
            n.lockNoAttack();
            n.respawns(false);
            ((CommonCombatMethod)n.getCombatMethod()).set(n, p);
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
           var b = p.getRouteFinder().routeAbsolute(p.tile().transform(4, 0).x, p.tile().transform( 0, 4).y).reachable;
            System.out.println(b);
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
        });
        dev("hit1", (p, c, s) -> {
            p.hit(p, 1, SplatType.NPC_HEALING_HITSPLAT);
        });
        dev("hit2", (p, c, s) -> {
            p.hit(p, 1, SplatType.POISON_HITSPLAT);;
        });
        dev("hit3", (p, c, s) -> {
            p.hit(p, 1, SplatType.VENOM_HITSPLAT);;
        });
        dev("hit4", (p, c, s) -> {
            p.hit(p, 1, SplatType.MAX_HIT);;
        });
        dev("hit5", (p, c, s) -> {
            var i = 1;
            for (SplatType value : SplatType.values()) {
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
            GameObject throne = GameObject.spawn(VERZIKS_THRONE_32737, 3167, 4324, p.getZ(),10,0);
        });
        dev("tob", (p, c, s) -> {
            p.teleport(3678, 3216);
        });
        dev("teleto", (p, c, s) -> {
            Optional<Player> plr = World.getWorld().getPlayerByName(c.substring(s[0].length() + 1));
            p.teleport(plr.get().tile());
        });
        dev("up", (p, c, s) -> {
            p.teleport(p.tile().transform(0,0,1));
        });
        dev("up4", (p, c, s) -> {
            p.teleport(p.tile().transform(0,0,4));
        });
        dev("runes", (p, c, s) -> {
            for (int i = 554; i <= 566; i++) {
                p.inventory().add(i, 1000000);
            }
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
                        System.out.println("clip : "+clip);
                    if (clip != 0) {
                        GroundItem gi = new GroundItem(new Item(item, 1), Tile.create(x, y, player.tile().level), player);
                        player.getPacketSender().createGroundItem(gi);
                        gis.add(gi);
                    }
                }
            }
            Chain.bound(player).runFn(10, () ->  {
                gis.forEach(GroundItemHandler::sendRemoveGroundItem);
            });
        });
        dev("calv", (p, c, s) -> {
            p.teleport(1888, 11547, 1);
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
            World.getWorld().tileGraphic(Integer.parseInt(s[1]), new Tile(p.tile().x + 1, p.tile().y), 0, 0);
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
            n.forceChat("assessed as "+dir);
            Vetion.spawnShieldInDir(new Vetion(), n.tile(), dir);
        });

        dev("vet3", (p, c, s) -> {
            var n = p.getLocalNpcs().get(0);
            var base = n.tile().transform(-2, -2);
            System.out.println("size "+n.getSize());
            for (int x = 0 ; x < n.getSize() + 4; x++) {
                for (int y = 0 ; y <  n.getSize() + 4; y++) {

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
                for(int i = 160; i < 400; i++) {
                    player.getPacketSender().sendMessage("Icon="+i+" - <img="+i+">");
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
