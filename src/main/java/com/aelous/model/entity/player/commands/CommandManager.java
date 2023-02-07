package com.aelous.model.entity.player.commands;

import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.impl.dev.*;
import com.aelous.model.entity.player.commands.impl.member.*;
import com.aelous.model.entity.player.commands.impl.owner.*;
import com.aelous.model.entity.player.commands.impl.players.*;
import com.aelous.model.entity.player.commands.impl.staff.admin.*;
import com.aelous.model.entity.player.commands.impl.staff.moderator.*;
import com.aelous.model.entity.player.commands.impl.staff.server_support.StaffZoneCommand;
import com.aelous.model.entity.player.commands.impl.super_member.YellColourCommand;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.*;

import java.util.HashMap;
import java.util.Map;

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
        commands.put("spawnkey2", new EscapeKeyCommand());
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
