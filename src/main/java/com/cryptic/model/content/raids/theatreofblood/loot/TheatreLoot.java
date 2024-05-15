package com.cryptic.model.content.raids.theatreofblood.loot;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.List;

public class TheatreLoot {
    public final List<Item> COMMON = Arrays.asList(
        new Item(22406, Utils.random(50, 60)),
        new Item(ItemIdentifiers.DEATH_RUNE, Utils.random(500, 600)),
        new Item(ItemIdentifiers.BLOOD_RUNE, Utils.random(500, 600)),
        new Item(ItemIdentifiers.SWAMP_TAR, Utils.random(500, 600)),
        new Item(454, Utils.random(500, 600)),
        new Item(445, Utils.random(300, 360)),
        new Item(1776, Utils.random(200, 240)),
        new Item(450, Utils.random(130, 156)),
        new Item(452, Utils.random(60, 72)),
        new Item(246, Utils.random(50, 60)),
        new Item(3139, Utils.random(50, 60)),
        new Item(216, Utils.random(50, 60)),
        new Item(212, Utils.random(40, 48)),
        new Item(3050, Utils.random(37, 44)),
        new Item(214, Utils.random(34, 40)),
        new Item(210, Utils.random(34, 40)),
        new Item(208, Utils.random(30, 36)),
        new Item(3052, Utils.random(27, 32)),
        new Item(2486, Utils.random(26, 31)),
        new Item(218, Utils.random(24, 28)),
        new Item(220, Utils.random(20, 24)),
        new Item(1392, Utils.random(15, 18)),
        new Item(1374, 4),
        new Item(1128, 4),
        new Item(1114, 4),
        new Item(ItemIdentifiers.PALM_TREE_SEED, 3),
        new Item(ItemIdentifiers.YEW_SEED, 3),
        new Item(ItemIdentifiers.MAGIC_SEED, 3),
        new Item(ItemIdentifiers.MAHOGANY_SEED, Utils.random(10, 12))
    );
    public final List<Item> RARE = Arrays.asList(
        new Item(ItemIdentifiers.CABBAGE),
        new Item(ItemIdentifiers.AVERNIC_DEFENDER_HILT),
        new Item(ItemIdentifiers.GHRAZI_RAPIER),
        new Item(ItemIdentifiers.SANGUINESTI_STAFF),
        new Item(ItemIdentifiers.JUSTICIAR_FACEGUARD),
        new Item(ItemIdentifiers.JUSTICIAR_CHESTGUARD),
        new Item(ItemIdentifiers.JUSTICIAR_LEGGUARDS)
    );
    public final List<Item> VERY_RARE = Arrays.asList(
        new Item(ItemIdentifiers.SANGUINE_ORNAMENT_KIT),
        new Item(ItemIdentifiers.HOLY_ORNAMENT_KIT),
        new Item(ItemIdentifiers.SANGUINE_DUST),
        new Item(ItemIdentifiers.LIL_ZIK),
        new Item(ItemIdentifiers.SCYTHE_OF_VITUR)
    );

    public Item reward(Player player) {
        if (Utils.rollDie(100, 1)) {
            player.putAttrib(AttributeKey.RARE_TOB_REWARD, true);
            return Utils.randomElement(RARE);
        } else if (Utils.rollDie(200, 1)) {
            player.putAttrib(AttributeKey.RARE_TOB_REWARD, true);
            return Utils.randomElement(VERY_RARE);
        }
        return Utils.randomElement(COMMON);
    }

}
