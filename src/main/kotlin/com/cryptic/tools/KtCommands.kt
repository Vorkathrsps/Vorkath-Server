package com.cryptic.tools

import com.cryptic.model.World
import com.cryptic.model.content.tournaments.Tournament
import com.cryptic.model.content.tournaments.TournamentManager
import com.cryptic.model.entity.attributes.AttributeKey
import com.cryptic.model.entity.masks.impl.chat.ChatMessage
import com.cryptic.model.entity.npc.NPC
import com.cryptic.model.entity.player.Player
import com.cryptic.model.entity.player.Skills
import com.cryptic.model.entity.player.commands.Command
import com.cryptic.model.entity.player.commands.CommandManager
import com.cryptic.model.entity.player.rights.PlayerRights
import com.cryptic.model.items.Item
import com.cryptic.model.map.position.Tile
import com.cryptic.utility.Utils
import com.cryptic.utility.chainedwork.Chain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Predicate


/**
 * by default all these commands are dev+ only, unless explicitly given a rank
 * <br>parts[0] = the command entered, such as ::bob >> is bob
 * <br>parts[1] = the first argument such as ::bob hello >> is hello
 *
 * @author Shadowrs/Jak tardisfan121@gmail.com
 */
@Suppress("UNUSED_VARIABLE")
object KtCommands {

    @JvmStatic
    var botAccIncrementor = 1

    private val logger: Logger = LoggerFactory.getLogger(KtCommands::class.java)

    fun init() {
        cmd("testbot") {
            val botcount = if (parts.size < 2) 1 else parts[1].toInt()
            val walk = if (parts.size < 3) false else parts[2].toBoolean()
            val walkdist = if (parts.size < 4) 3 else parts[3].toInt()
            val tome = if (parts.size < 5) 0 else parts[4].toInt()
            CommandManager.attempt(player, "addbots $botcount $walk $walkdist $tome")
        }
        cmd("addbots") {
            val spawnpos = player.tile().copy()
            val botcount = if (parts.size < 2) 100 else parts[1].toInt()
            val walk = if (parts.size < 3) false else parts[2].toBoolean()
            val walkdist = if (parts.size < 4) 3 else parts[3].toInt()
            val tome = if (parts.size < 5) 0 else parts[4].toInt()
            val max = if (parts.size < 6) 0 else parts[5].toInt()
            val dh = Tournament(Arrays.stream(TournamentManager.settings.tornConfigs).filter { c: TournamentManager.TornConfig ->
                c.key.equals(
                    "dharok",
                    ignoreCase = true
                )
            }.findFirst().get())
            for (i in 1..botcount) {
                var bot = Utils.createTestbot()
                bot.putAttrib(AttributeKey.DEBUG_MESSAGES, true)
                bot.setPlayerRights(PlayerRights.OWNER)
                if (max == 1) {
                    CommandManager.attempt(bot, "master")
                    dh.setLoadoutOnPlayer(bot)
                }
                Chain.bound(null).runFn(3) {
                    for (skill in 0 until Skills.SKILL_COUNT) {
                        bot.skills().setXp(skill, Skills.levelToXp(player.skills().level(skill)).toDouble())
                    }
                    bot.isInvulnerable = true
                    bot.hp(5000, 5000)
                    bot.putAttrib(AttributeKey.NEW_ACCOUNT, false) // save over saved attrib, starter is done
                }
                if (tome == 1) {
                    bot.runFn(1) {
                        bot.teleport(spawnpos)
                    }
                }
                if (walk) {
                    Chain.bound(null).repeatingTask(2 + Utils.random(2)) {
                        var pos = 10
                        var targPos =
                            bot.tile().transform(-walkdist + Utils.random(walkdist), -walkdist + Utils.random(walkdist))
                        while (pos-- > 0 && World.getWorld().clipAt(targPos) == 0) {
                            targPos = bot.tile()
                                .transform(-walkdist + Utils.random(walkdist), -walkdist + Utils.random(walkdist))
                        }
                        bot.smartPathTo(targPos);
                        val chat = "we ${targPos.distance(bot.tile())} i:${bot.relations.ignoreList.size}"
                        bot.forceChat(chat)
                        bot.chatMessageQueue.add(ChatMessage(0, 0, chat.toByteArray()))
                        if (!bot.isRegistered)
                            it.stop()
                    }
                }
            }
            player.message("$botcount submitted")
        }

        cmd("addbotswildypvm") {
            // pvm in wildy 1v1 fighting
            val startpos = Tile(3353, 3645)
            var ypos = 0
            while (ypos < 150) {
                ypos += 3
                val npcpos = startpos.transform(0, ypos)
                val npc = NPC(2077, npcpos).respawns(false)
                World.getWorld().registerNpc(npc)
                val bot = Utils.createTestbot()
                bot.runFn(3) {
                    bot.setPlayerRights(PlayerRights.OWNER)
                    CommandManager.attempt(bot, "heal")
                    bot.isInvulnerable = true
                    bot.teleport(npcpos.transform(0, -2))
                    bot.combat.attack(npc)
                    npc.combat.attack(bot)
                }
            }
        }

        cmd("addbotsditch") {
            // wildy ditch test pvm distanced attacking
            val startpos = Tile(3044, 3520)
            var pos = 0
            while (pos < 40) {
                pos += 1
                val npcpos = startpos.transform(pos, 0)
                val npc = NPC(
                    2077,
                    npcpos.transform(0, 3)
                ).respawns(false)
                World.getWorld().registerNpc(npc)
                val bot = Utils.createTestbot()
                bot.runFn(3) {
                    bot.setPlayerRights(PlayerRights.OWNER)
                    CommandManager.attempt(bot, "heal")
                    bot.isInvulnerable = true
                    bot.teleport(npcpos)
                    bot.equipment.set(3, Item(864, 1000), true)
                    bot.combat.attack(npc)
                    npc.combat.attack(bot)
                }
            }
        }
    }

    fun match(a: Int, b: Int) {
        if (a != b) {
            logger.error("mismatch $a to $b")
        } else {
            logger.info("matched $a to $b")
        }
    }

    var tempLoggingOfNPE = true



    /**
     * simple predicates used by [Command.execute] canUse(player)
     */
    val PLAYER = Predicate<Player> { true }
    val DEV = Predicate<Player> { it.playerRights.isOwner(it) }
    val ADMIN = Predicate<Player> { it.playerRights.isAdministrator(it) }
    val MOD = Predicate<Player> { it.playerRights.isModerator(it) || it.playerRights.isAdministrator(it) }

    /**
     * registers a command. does some complicated kotlin syntax to make the Higher Order Function able to
     * consume the 3 args from [Command.execute]
     */
    fun cmd(command: String, canUse: Predicate<Player> = DEV, job: CommandWrapper.() -> Unit) {
        CommandManager.commands[command] = object :
            Command {
            override fun execute(player: Player, command: String, parts: Array<out String>) {
                job(CommandWrapper(player, command, parts))
            }

            override fun canUse(player: Player): Boolean = canUse.test(player)

        }
    }

    class CommandWrapper(
        val player: Player,
        val command: String,
        val parts: Array<out String>,
    )
}
