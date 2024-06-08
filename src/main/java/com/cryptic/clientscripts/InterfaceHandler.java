package com.cryptic.clientscripts;

import com.cryptic.clientscripts.constants.InterfaceID;
import com.cryptic.clientscripts.impl.*;
import com.cryptic.clientscripts.impl.clanchat.*;
import com.cryptic.clientscripts.impl.emotes.EmoteInterface;
import com.cryptic.clientscripts.impl.equipment.*;
import com.cryptic.clientscripts.impl.equipment.guideprice.GuidePriceInterface;
import com.cryptic.clientscripts.impl.equipment.guideprice.GuidePriceInventory;
import com.cryptic.clientscripts.impl.inventory.InventoryInterface;
import com.cryptic.clientscripts.impl.journal.AchievementTabInterface;
import com.cryptic.clientscripts.impl.journal.CharacterSummaryInterface;
import com.cryptic.clientscripts.impl.journal.JournalRootInterface;
import com.cryptic.clientscripts.impl.journal.QuestListInterface;
import com.cryptic.clientscripts.impl.journal.collectionlog.CollectionLogInterface;
import com.cryptic.clientscripts.impl.skills.crafting.TanningInterface;
import com.cryptic.clientscripts.impl.skills.magic.MagicTab;
import com.cryptic.clientscripts.impl.skills.prayer.PrayerTab;
import com.cryptic.clientscripts.impl.skills.prayer.QuickPrayerInterface;
import com.cryptic.clientscripts.impl.settings.SettingsInterface;
import com.cryptic.clientscripts.impl.settings.SettingsTab;
import com.cryptic.clientscripts.impl.skills.information.SkillInformationInterface;
import com.cryptic.clientscripts.impl.skills.SkillTab;
import com.cryptic.clientscripts.impl.skills.smithing.SmithingInterface;
import com.cryptic.clientscripts.impl.social.FriendListInterface;
import com.cryptic.clientscripts.impl.social.IgnoreListInterface;
import com.cryptic.clientscripts.impl.weaponinterface.WeaponInformationInterface;
import com.cryptic.clientscripts.impl.dialogue.impl.*;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.InterfaceType;
import com.cryptic.model.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class InterfaceHandler {
    public static final Map<Integer, InterfaceBuilder> interfaces = new HashMap<>();
    public final Map<Integer, InterfaceBuilder> visible = new HashMap<>();

    static {
        interfaces.put(InterfaceID.EMOTES, new EmoteInterface());
        interfaces.put(InterfaceID.LOGOUT_PANEL, new LogoutTab());
        interfaces.put(InterfaceID.SPELLBOOK, new MagicTab());
        interfaces.put(InterfaceID.CHARACTER_SUMMARY, new CharacterSummaryInterface());
        interfaces.put(InterfaceID.QUEST_LIST, new QuestListInterface());
        interfaces.put(InterfaceID.ACHIEVEMENT_DIARY, new AchievementTabInterface());
        interfaces.put(InterfaceID.QUEST_ROOT, new JournalRootInterface());
        interfaces.put(InterfaceID.MINIMAP, new MinimapOrbs());
        interfaces.put(InterfaceID.PRAYER, new PrayerTab());
        interfaces.put(InterfaceID.QUICK_PRAYER, new QuickPrayerInterface());
        interfaces.put(InterfaceID.FIXED_VIEWPORT, new ViewportFixed());
        interfaces.put(InterfaceID.COMBAT, new WeaponInformationInterface());
        interfaces.put(InterfaceID.INVENTORY, new InventoryInterface());
        interfaces.put(InterfaceID.EQUIPMENT, new EquipmentTab());
        interfaces.put(InterfaceID.KEPT_ON_DEATH, new KeptOnDeathInterface());
        interfaces.put(InterfaceID.EQUIPMENT_STATS, new EquipmentStats());
        interfaces.put(InterfaceID.EQUIPMENT_INVENTORY, new EquipmentInventory());
        interfaces.put(InterfaceID.DIALOG_NPC, new DialogueNpc());
        interfaces.put(InterfaceID.DIALOG_OPTION, new DialogueOptions());
        interfaces.put(InterfaceID.DIALOG_PLAYER, new DialoguePlayer());
        interfaces.put(InterfaceID.DIALOG_MESSAGE_BOX, new DialogueStatement());
        interfaces.put(InterfaceID.DIALOG_SPRITE, new DialogueItemSingle());
        interfaces.put(InterfaceID.DIALOG_DOUBLE_SPRITE, new DialogueItemDouble());
        interfaces.put(InterfaceID.DESTROY_ITEM, new DialogueDestroyItem());
        interfaces.put(InterfaceID.PRODUCE_ITEM, new DialogueProduceItem());
        interfaces.put(InterfaceID.FRIEND_LIST, new FriendListInterface());
        interfaces.put(InterfaceID.IGNORE_LIST, new IgnoreListInterface());
        interfaces.put(InterfaceID.SKILL_INFORMATION, new SkillInformationInterface());
        interfaces.put(InterfaceID.SKILLS, new SkillTab());
        interfaces.put(InterfaceID.SETTINGS_SIDE, new SettingsTab());
        interfaces.put(InterfaceID.SETTINGS, new SettingsInterface());
        interfaces.put(InterfaceID.CLAN_CHAT, new ClanChatTab());
        interfaces.put(InterfaceID.FRIENDS_CHAT, new FriendsChannelInterface());
        interfaces.put(InterfaceID.CLAN, new YourChannelInterface());
        interfaces.put(InterfaceID.CLAN_GUEST, new GuestChannelInterface());
        interfaces.put(InterfaceID.CLAN_SETUP, new SetupChannelInterface());
        interfaces.put(InterfaceID.GUIDE_PRICES, new GuidePriceInterface());
        interfaces.put(InterfaceID.GUIDE_PRICES_INVENTORY, new GuidePriceInventory());
        interfaces.put(InterfaceID.COLLECTION_LOG, new CollectionLogInterface());
        interfaces.put(InterfaceID.LEATHER_TANNING_INTERFACE, new TanningInterface());
        interfaces.put(InterfaceID.SMITHING, new SmithingInterface());
    }

    public static InterfaceBuilder find(int interfaceId) {
        return interfaces.getOrDefault(interfaceId, null);
    }

    public static void closeModals(Player player) {
        for (InterfaceBuilder inter : player.activeInterface.values()) {
            GameInterface gameInterface = inter.gameInterface();
            if (gameInterface.getPosition().getType() == InterfaceType.MODAL && !gameInterface.name().contains("VIEWPORT")) {
                gameInterface.close(player);
                inter.close(player);
            }
        }
    }
}