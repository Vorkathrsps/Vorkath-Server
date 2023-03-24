package com.aelous.model.content.teleport;

import com.aelous.GameServer;
import com.aelous.model.content.duel.Dueling;
import com.aelous.model.content.teleport.world_teleport_manager.TeleportData;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.concurrent.TimeUnit;

import static com.aelous.model.content.presets.PresetManager.lastTimeDied;

/**
 * @author Patrick van Elderen | January, 10, 2021, 11:08
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Teleports {

    /**
     * Determines if the player is able to teleport. The inform parameter
     * allows us to inform the player of the reason they cannot teleport
     * if we so wish to.
     */
    public static boolean canTeleport(Player player, boolean inform, TeleportType teletype) {

        if (player.getController() != null) {
            if (!player.getController().canTeleport(player)) {
                player.message("A magical force prevents you from teleporting.");
                player.getInterfaceManager().closeDialogue();
                return false;
            }
        }

        if (Dueling.in_duel(player)) {
            if (inform) {
                player.message("You cannot teleport out of a duel.");
            }
            return false;
        }

        if (player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
            if(inform) {
                long millis = player.getTimers().left(TimerKey.SPECIAL_TELEBLOCK) * 600L;
                player.message(String.format("A teleport block has been cast on you. It should wear off in %d minutes, %d seconds.", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
            }
            return false;
        }

        if (player.getTimers().has(TimerKey.TELEBLOCK)) {
            if (inform) {
                player.teleblockMessage();
            }
            return false;
        }

        var capLvl = teletype == TeleportType.ABOVE_20_WILD ? 30 : 20;
        if (WildernessArea.wildernessLevel(player.tile()) > capLvl && (!player.getPlayerRights().isDeveloper(player))) {

            if (inform) {
                player.message("A mysterious force blocks your teleport spell!");
                player.message("You can't use this teleport after level " + capLvl+ " wilderness.");
            }
            return false;
        }

        if (player.jailed()) {
            if (inform) {
                player.message("You can't leave the jail yet.");
            }
            return false;
        }

        //Is our player currently in an active Fight Cave?
        if (player.getMinigame() != null && !player.getMinigame().canTeleportOut()) {
            player.message("You cannot do that right now.");
            return false;
        }

        if (player.getTimers().has(TimerKey.BLOCK_SPEC_AND_TELE) && player.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA,-1) == 0) {
            player.message("<col=804080>Teleport blocked for "+ player.getTimers().asSeconds(TimerKey.BLOCK_SPEC_AND_TELE)+" more secs after using spec at the start of a battle.");
            return false;
        }

        var mage_arena = player.<Boolean>getAttribOr(AttributeKey.MAGEBANK_MAGIC_ONLY,false);


        if (player.looks().hidden()) {
            player.looks().hide(false);
        }

        return true;
    }

    public static void teleportToTarget(Player player, Tile targetTile) {
        player.lockNoDamage();
        if (player.getSpellbook() == MagicSpellbook.NORMAL) {
            //Modern spells
            player.animate(714);
            player.graphic(111, GraphicHeight.HIGH, 0);
        } else if (player.getSpellbook() == MagicSpellbook.ANCIENT) {
            //Ancient spells
            player.animate(1979);
            player.graphic(392);
        }

        Chain.bound(null).runFn(3, () -> {
            player.teleport(World.getWorld().randomTileAround(targetTile, 1));
            player.animate(-1);
            player.graphic(-1);
            player.unlock();
        });
    }

    public static boolean pkTeleportOk(Player player, Tile tile) {
        return pkTeleportOk(player, tile.x, tile.y, true);
    }

    public static boolean pkTeleportOk(Player player, int x, int z) {
        return pkTeleportOk(player, x, z, true);
    }

    public static boolean pkTeleportOk(Player player, Tile tile, boolean preventQuickRespawn) {
        return pkTeleportOk(player, tile.x, tile.y, preventQuickRespawn);
    }

    // Execute a teleport, checking if locked or jailed etc.
    public static boolean pkTeleportOk(Player player, int x, int z, boolean preventQuickRespawn) {
        if (player.locked()) {
            // Stops players doing ::mb while jumping over, for example, the wildy ditch. This would fly them off into random places.
            return false;
        }
        if (!player.getPlayerRights().isDeveloper(player)) {
            if (player.jailed()) {
                player.message("You can't use commands when Jailed.");
                return false;
            }
            if (!wildernessTeleportAntiragOk(x, z, player, preventQuickRespawn)) {
                return false;
            }
        } else {
            player.message("As an admin you bypass pk-tele restrictions.");
        }
        return true;
    }

    public static boolean wildernessTeleportAntiragOk(int x, int z, Player player, boolean preventQuickRespawn) {
        if (WildernessArea.inWilderness(new Tile(x, z))) {
            if (preventQuickRespawn && lastTimeDied(player, GameServer.properties().pkTelesAfterSetupSet)) {
                player.message("Quick wilderness teleports are off limits %ds <col=FF0000>after death.</col>", (int)Utils.ticksToSeconds(GameServer.properties().pkTelesAfterSetupSet));
                return false;
            }

            if (player.inventory().count(ItemIdentifiers.SARADOMIN_BREW4) > GameServer.properties().brewCap) {
                player.message("You cannot take more than " + GameServer.properties().brewCap + " Saradomin brews into the wilderness.");
                return false;
            }
        }
        return true;
    }

    public static boolean rolTeleport(Player player) {
        // rol ringoflife ring of life
        player.stopActions(true);
        return canTeleport(player, true, TeleportType.GENERIC);
    }

    public static void ringOfLifeTeleport(Player player) {
        player.lockNoDamage();
        player.animate(714);
        player.graphic(111, GraphicHeight.HIGH, 0);
        Chain.bound(null).runFn(3, () -> {
            player.teleport(3094, 3469); //Teleport the player edge coffin spot
            player.animate(-1);
            player.graphic(-1);
            player.unlock();
        });
    }

    public static void basicTeleport(Player player, Tile tile) {
        basicTeleport(player, tile, 714, new Graphic(111, GraphicHeight.HIGH));
    }

    public static void fromInterface(Player player, TeleportData data, boolean previous) {
        Tile tile = data.tile;
        player.recentTeleport = tile;
        basicTeleport(player, tile, 714, new Graphic(111, GraphicHeight.HIGH));
    }

    public static void basicTeleport(Player player, Tile tile, int anim, Graphic gfx) {
        //If the player is locked or dead
        if (player.locked() || player.dead() || player.hp() <= 0)
            return;

        player.getInterfaceManager().close();

        player.stopActions(true);

        if (player.hasAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY)) {
            player.clearAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY);
        }

        player.lockNoDamage();
        player.animate(anim);
        player.sendSound(202, 0);
        player.graphic(gfx.id(), gfx.getHeight(), gfx.delay());
        Chain.bound(null).runFn(3, () -> {
            player.teleport(tile);
            player.animate(new Animation(-1, Priority.HIGH));
            player.graphic(-1);
            player.unlock();
        });
    }

    /**
     * For uninterruptable scripts (BOTS ONLY!)
     */
    public static void teleportContextless(Player player, Tile tile, int anim, Graphic gfx) {
        player.lockNoDamage();
        player.animate(anim);
        player.graphic(gfx.id(), gfx.getHeight(), gfx.delay());
        Chain.bound(null).runFn(3, () -> {
            player.teleport(tile);
            player.animate(-1);
            player.graphic(-1);
            player.unlock();
        });
    }

}
