package com.aelous.model.content.packet_actions.interactions.items;

import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.areas.wilderness.slayer.WildernessSlayerCasket;
import com.aelous.model.content.collection_logs.LogType;
import com.aelous.model.content.consumables.FoodConsumable;
import com.aelous.model.content.consumables.potions.Potions;
import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.content.items.RockCake;
import com.aelous.model.content.items.tools.ItemPacks;
import com.aelous.model.content.sigils.SigilHandler;
import com.aelous.model.content.skill.impl.herblore.Cleaning;
import com.aelous.model.content.skill.impl.hunter.Hunter;
import com.aelous.model.content.skill.impl.hunter.HunterItemPacks;
import com.aelous.model.content.skill.impl.hunter.trap.impl.Birds;
import com.aelous.model.content.skill.impl.hunter.trap.impl.Chinchompas;
import com.aelous.model.content.skill.impl.slayer.content.ImbuedHeart;
import com.aelous.model.content.skill.impl.woodcutting.BirdNest;
import com.aelous.model.content.treasure.pvpcache.FrozenCache;
import com.aelous.model.content.treasure.TreasureRewardCaskets;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.content.bountyhunter.dialogue.TeleportToTargetScrollD;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.model.entity.attributes.AttributeKey.VIEWING_RUNE_POUCH_I;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;

public class ItemActionOne {

    public static void click(Player player, Item item) {
        int id = item.getId();
        if (PacketInteractionManager.checkItemInteraction(player, item, 1)) {
            return;
        }
        if (TreasureRewardCaskets.openCasket(player, item)) {
            return;
        }

        if (FrozenCache.openCasket(player, item)) {
            return;
        }

        if (player.getWildernessSlayerCasket().open(player, item)) {
            return;
        }

        if (Potions.onItemOption1(player, item)) {
            return;
        }

        if (BirdNest.onItemOption1(player, item)) {
            return;
        }

        if (FoodConsumable.onItemOption1(player, item)) {
            return;
        }

        if (RockCake.onItemOption1(player, item)) {
            return;
        }

        if (ItemPacks.open(player, item)) {
            return;
        }

        if (Cleaning.onItemOption1(player, item)) {
            return;
        }

        if (HunterItemPacks.onItemOption1(player, item)) {
            return;
        }

        if (id == TARGET_TELEPORT_SCROLL) {
            boolean alreadyClaimed = player.getAttribOr(AttributeKey.BOUNTY_HUNTER_TARGET_TELEPORT_UNLOCKED, false);
            if (alreadyClaimed) {
                player.message("You already know this spell.");
                return;
            }
            if (player.inventory().contains(TARGET_TELEPORT_SCROLL)) {
                player.getDialogueManager().start(new TeleportToTargetScrollD());
            }
            return;
        }

        if (id == ItemIdentifiers.COLLECTION_LOG) {
            player.getCollectionLog().open(LogType.BOSSES);
            return;
        }

        if (id == ItemIdentifiers.IMBUED_HEART) {
            ImbuedHeart.activate(player);
            return;
        }

        if (id == VOTE_TICKET) {
            if (!player.inventory().contains(VOTE_TICKET)) {
                return;
            }

            if (WildernessArea.inWild(player)) {
                player.message("You cannot exchange vote points in the wilderness.");
                return;
            }

            int amount = player.inventory().count(VOTE_TICKET);
            int current = player.getAttribOr(AttributeKey.VOTE_POINS, 0);

            player.putAttrib(AttributeKey.VOTE_POINS, current + amount);
            player.getPacketSender().sendString(QuestTab.InfoTab.VOTE_POINTS.childId, QuestTab.InfoTab.INFO_TAB.get(QuestTab.InfoTab.VOTE_POINTS.childId).fetchLineData(player));
            player.inventory().remove(new Item(VOTE_TICKET, amount), true);
            player.message("You exchange " + Color.BLUE.tag() + "" + Utils.formatNumber(amount) + " vote points</col>.");
            return;
        }

        boolean hasVengeance = player.getAttribOr(AttributeKey.VENGEANCE_ACTIVE, false);

        if (id == VENGEANCE_SKULL) {
            if (player.getSpellbook() != MagicSpellbook.LUNAR) {
                player.message("You can only use the vengeance cast on the lunars spellbook.");
                return;
            }
            if (player.getSkills().level(Skills.DEFENCE) < 40) {
                player.message("You need 40 Defence to use Vengence.");
            } else if (player.getSkills().level(Skills.MAGIC) < 94) {
                player.message("Your Magic level is not high enough to use this spell.");
            } else if (hasVengeance) {
                player.message("You already have Vengeance casted.");
            } else if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MAGIC.ordinal()]) {
                player.message("Magic is disabled for this duel.");
            } else if (!player.getTimers().has(TimerKey.VENGEANCE_COOLDOWN)) {
                if (!player.inventory().contains(964)) {
                    return;
                }
                player.getTimers().register(TimerKey.VENGEANCE_COOLDOWN, 50);
                player.putAttrib(AttributeKey.VENGEANCE_ACTIVE, true);
                player.animate(new Animation(8316));
                player.performGraphic(new Graphic(726, GraphicHeight.HIGH, 0));
                player.getPacketSender().sendEffectTimer(30, EffectTimer.VENGEANCE).sendMessage("You now have Vengeance's effect.");
            } else {
                player.message("You can only cast vengeance spells every 30 seconds.");
            }
            return;
        }


        if (id == 10006) {
            Hunter.lay(player, new Birds(player));
            return;
        }

        if (id == 10008) {
            Hunter.lay(player, new Chinchompas(player));
            return;
        }

        if (SigilHandler.isSigil(item.getId())) {
            SigilHandler.handle(player, item.getId(), true, false);
            return;
        }

        /* Looting bag. */
        if (id == 11941 || id == 22586) {
            player.getLootingBag().openAndCloseBag(id);
            return;
        }
        if (id == RUNE_POUCH) {
            player.getRunePouch().open(RUNE_POUCH);
            player.putAttrib(VIEWING_RUNE_POUCH_I, false);
            return;
        }
    }
}
