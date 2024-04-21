package com.cryptic.model.entity.npc.droptables;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.skill.impl.prayer.Ashes;
import com.cryptic.model.content.skill.impl.prayer.Bone;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.google.common.base.Objects;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.utility.ItemIdentifiers.*;

public class ItemDrops {

    public static final int[] BONES = new int[]{ItemIdentifiers.BONES, BURNT_BONES, BAT_BONES, BIG_BONES, BABYDRAGON_BONES, DRAGON_BONES, JOGRE_BONES, ZOGRE_BONES, OURG_BONES, WYVERN_BONES, DAGANNOTH_BONES, LONG_BONE, CURVED_BONE, LAVA_DRAGON_BONES, SUPERIOR_DRAGON_BONES, WYRM_BONES, DRAKE_BONES, HYDRA_BONES};
    public static final int[] ASHES = new int[]{FIENDISH_ASHES, VILE_ASHES, MALICIOUS_ASHES, ABYSSAL_ASHES, INFERNAL_ASHES};
    public static final int[] ENSOULED_HEADS = new int[]{ENSOULED_ABYSSAL_HEAD, ENSOULED_ABYSSAL_HEAD_13508, ENSOULED_AVIANSIE_HEAD, ENSOULED_BEAR_HEAD, ENSOULED_AVIANSIE_HEAD_13505, ENSOULED_BEAR_HEAD_13463, ENSOULED_BLOODVELD_HEAD, ENSOULED_BLOODVELD_HEAD_13496, ENSOULED_CHAOS_DRUID_HEAD, ENSOULED_CHAOS_DRUID_HEAD_13472, ENSOULED_DAGANNOTH_HEAD, ENSOULED_DAGANNOTH_HEAD_13493, ENSOULED_DEMON_HEAD, ENSOULED_DEMON_HEAD_13502, ENSOULED_DRAGON_HEAD, ENSOULED_DRAGON_HEAD_13511, ENSOULED_ELF_HEAD, ENSOULED_ELF_HEAD_13481, ENSOULED_GIANT_HEAD, ENSOULED_GIANT_HEAD_13475, ENSOULED_GOBLIN_HEAD, ENSOULED_GOBLIN_HEAD_13448,ENSOULED_HELLHOUND_HEAD, ENSOULED_HELLHOUND_HEAD_26997, ENSOULED_DRAGON_HEAD_13511, ENSOULED_DRAGON_HEAD, ENSOULED_IMP_HEAD, ENSOULED_IMP_HEAD_13454, ENSOULED_KALPHITE_HEAD, ENSOULED_KALPHITE_HEAD_13490, ENSOULED_TZHAAR_HEAD, ENSOULED_TZHAAR_HEAD_13499, ENSOULED_UNICORN_HEAD, ENSOULED_UNICORN_HEAD_13466, ENSOULED_SCORPION_HEAD, ENSOULED_SCORPION_HEAD_13460, ENSOULED_OGRE_HEAD, ENSOULED_OGRE_HEAD_13478, ENSOULED_MINOTAUR_HEAD, ENSOULED_MINOTAUR_HEAD_13457, ENSOULED_HORROR_HEAD, ENSOULED_HORROR_HEAD_13487};
    public void rollTheDropTable(Player player, NPC npc) {
        int npcId = NpcDropRepository.getDropNpcId(npc.getId());
        NpcDropTable table = NpcDropRepository.forNPC(npcId);
        var dropUnderPlayer = npc.id() == NpcIdentifiers.KRAKEN || npc.id() == NpcIdentifiers.CAVE_KRAKEN || npc.id() >= NpcIdentifiers.ZULRAH && npc.id() <= NpcIdentifiers.ZULRAH_2044 || npc.id() >= NpcIdentifiers.VORKATH_8059 && npc.id() <= NpcIdentifiers.VORKATH_8061;
        Tile tile = dropUnderPlayer ? player.tile() : npc.tile();
        if (table != null) {
            List<Item> rewards = table.getDrops(player);
            for (var item : rewards) {
                LogType.BOSSES.log(player, npc.id(), item);
                LogType.OTHER.log(player, npc.id(), item);
                var drop = item.noted() ? item.unnote().note() : item;
                if (skipLootingBag(player, drop)) continue;
                if (isUsingDevotionSigil(player, drop)) continue;
                if (isUsingAshSanctifier(player, drop)) continue;
                if (isUsingSoulBearer(player, drop)) continue;
                for (var i : table.getDrops()) {
                    var parsedID = ItemRepository.getItemId(i.getItem());
                    if (isRareDrop(player, npc, i, drop, parsedID)) break;
                }
                GroundItemHandler.createGroundItem(new GroundItem(drop, tile, player));
            }
        }
    }

    private boolean isUsingSoulBearer(Player player, Item drop) {
        if (player.getInventory().contains(SOUL_BEARER)) {
            if (ArrayUtils.contains(ENSOULED_HEADS, drop.getId())) {
                player.getBank().add(drop.getId());
                return true;
            }
        }
        return false;
    }

    private boolean isRareDrop(Player player, NPC npc, ItemDrop i, Item drop, int parsedID) {
        if (i.isRareDrop() && drop.getId() == parsedID) {
            var inWild = WildernessArea.inWilderness(player.tile());
            var level = WildernessArea.getWildernessLevel(player.tile());
            World.getWorld().sendWorldMessage("<img=2010> " + Color.BURNTORANGE.wrap("<shad=0>" + player.getUsername() + " has received a " + drop.name() + " from a " + npc.getMobName() + (!inWild ? "." : " Level: " + level + " wilderness.") + "</shad>"));
            return true;
        }
        return false;
    }

    final boolean isUsingAshSanctifier(final Player player, final Item drop) {
        if (player.getInventory().contains(ASH_SANCTIFIER)) {
            Ashes ash = Ashes.get(drop.getId());
            if (ash != null && Objects.equal(ash.id, drop.getId())) player.getSkills().addXp(Skill.PRAYER.getId(), ash.experience / 2);
            return true;
        }
        return false;
    }

    final boolean isUsingDevotionSigil(final Player player, final Item drop) {
        if (player.hasAttrib(AttributeKey.DEVOTION)) {
            if (ArrayUtils.contains(BONES, drop.getId())) {
                Bone bone = Bone.get(drop.getId());
                if (bone != null && Objects.equal(bone.itemId, drop.getId())) player.getSkills().addXp(Skill.PRAYER.getId(), bone.xp * 2.0D);
                return true;
            }
            if (ArrayUtils.contains(ASHES, drop.getId())) {
                Ashes ash = Ashes.get(drop.getId());
                if (ash != null && Objects.equal(ash.id, drop.getId())) player.getSkills().addXp(Skill.PRAYER.getId(), ash.experience * 2.0D);
                return true;
            }
        }
        return false;
    }

    final boolean skipLootingBag(final Player player, final Item item) {
        return (player.getInventory().containsAny(ItemIdentifiers.LOOTING_BAG, ItemIdentifiers.LOOTING_BAG_22586) || player.getBank().containsAny(ItemIdentifiers.LOOTING_BAG, ItemIdentifiers.LOOTING_BAG_22586)) && item.getId() == ItemIdentifiers.LOOTING_BAG;
    }

}
