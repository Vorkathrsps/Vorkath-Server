package com.cryptic.model.entity.npc.droptables;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.collection_logs.LogType;
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
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.utility.ItemIdentifiers.*;

public class ItemDrops {

    public static final List<Integer> BONES = Arrays.asList(ItemIdentifiers.BONES, BURNT_BONES, BAT_BONES, BIG_BONES, BABYDRAGON_BONES, DRAGON_BONES, JOGRE_BONES, ZOGRE_BONES, OURG_BONES, WYVERN_BONES, DAGANNOTH_BONES, LONG_BONE, CURVED_BONE, LAVA_DRAGON_BONES, SUPERIOR_DRAGON_BONES, WYRM_BONES, DRAKE_BONES, HYDRA_BONES);

    public static void rollTheDropTable(Player player, NPC npc) {
        int npcId = NpcDropRepository.getDropNpcId(npc.getId());
        NpcDropTable table = NpcDropRepository.forNPC(npcId);
        var dropUnderPlayer = npc.id() == NpcIdentifiers.KRAKEN || npc.id() == NpcIdentifiers.CAVE_KRAKEN || npc.id() >= NpcIdentifiers.ZULRAH && npc.id() <= NpcIdentifiers.ZULRAH_2044 || npc.id() >= NpcIdentifiers.VORKATH_8059 && npc.id() <= NpcIdentifiers.VORKATH_8061;
        Tile tile = dropUnderPlayer ? player.tile() : npc.tile();
        if (table != null) {
            List<Item> rewards = table.getDrops(player);
            for (var item : rewards) {
                if (skipLootingBag(player, item)) continue;
                LogType.BOSSES.log(player, npc.id(), item);
                LogType.OTHER.log(player, npc.id(), item);
                var drop = item.noted() ? item.unnote().note() : item;
                if (player.hasAttrib(AttributeKey.DEVOTION) && ArrayUtils.contains(BONES.toArray(), drop.getId())) {
                    for (Bone bone : Bone.values()) {
                        if (bone.itemId == drop.getId()) {
                            player.getSkills().addXp(Skill.PRAYER.getId(), bone.xp * 2.0D);
                            break;
                        }
                    }
                    continue;
                }
                for (var i : table.getDrops()) {
                    var parsedID = ItemRepository.getItemId(i.getItem());
                    if (i.isRareDrop() && drop.getId() == parsedID) {
                        var inWild = WildernessArea.inWilderness(player.tile());
                        var level = WildernessArea.getWildernessLevel(player.tile());
                        World.getWorld().sendWorldMessage("<img=2010> " + Color.BURNTORANGE.wrap("<shad=0>" + player.getUsername() + " has received a " + drop.name() + " from a " + npc.getMobName() + (!inWild ? "." : " Level: " + level + " wilderness.") + "</shad>"));
                        break;
                    }
                }
                GroundItemHandler.createGroundItem(new GroundItem(drop, tile, player));
            }
        }
    }

    private static boolean skipLootingBag(Player player, Item item) {
        return (player.getInventory().containsAny(ItemIdentifiers.LOOTING_BAG, ItemIdentifiers.LOOTING_BAG_22586) || player.getBank().containsAny(ItemIdentifiers.LOOTING_BAG, ItemIdentifiers.LOOTING_BAG_22586)) && item.getId() == ItemIdentifiers.LOOTING_BAG;
    }

}
