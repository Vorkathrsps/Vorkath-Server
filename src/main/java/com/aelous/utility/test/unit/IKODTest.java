package com.aelous.utility.test.unit;

import com.aelous.GameServer;
import com.aelous.model.inter.impl.BonusesInterface;
import com.aelous.model.content.mechanics.ItemsOnDeath;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.content.areas.wilderness.content.revenant_caves.AncientArtifacts;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.combat.weapon.WeaponInterfaces;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.map.position.Tile;
import com.aelous.network.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static com.aelous.utility.ItemIdentifiers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * see {@link ItemsOnDeath#droplootToKiller(Player, Mob)}
 * and {@link com.aelous.model.content.items_kept_on_death.ItemsKeptOnDeath#calculateItems(Player)}
 * @author Jak |Shadowrs
 */
public class IKODTest {

    public static boolean IKOD_DEBUG = false;

    public static void debug(Object o) {
        if (!IKOD_DEBUG)
            return;
        System.out.println("[ikod]: "+o);
    }

    @BeforeAll
    public static void b4() {
        IKOD_DEBUG = true;
        GameServer.properties(); // static {} block init
    }

    /*@Test
    public void mode() {
        assertTrue(Server.properties().pvpMode);
    }*/

    /**
     * see {@link Item#AUTO_KEPT_LIST}
     */
    //Changed June 3 2020, in PvP world all items are charged
    /*@Test
    public void tradableTS() {
        assertTrue(new Item(TOXIC_STAFF_OF_THE_DEAD).isTradeable());
    }*/

    /*@Test
    public void tradableBP() {
        assertTrue(new Item(TOXIC_BLOWPIPE).isTradeable());
    }*/

    @Test
    public void untradable1() {
        assertFalse(new Item(PET_ZILYANA).rawtradable());
    }

    //We no longer use getEmblem().getItemId() so I believe this test is unnecessary and invalid.
    /*@Test
    public void equal11() {
        assertEquals(new Item(BountyHunterEmblem.MYSTERIOUS_EMBLEM_5.getItemId()),
            BountyHunterEmblem.MYSTERIOUS_EMBLEM_5.getItemId());
    }*/

    @Test
    public void rsUntradableRP() {
        assertFalse(new Item(RUNE_POUCH).rawtradable());
    }

    @Test
    public void tradable2() {
        assertFalse(new Item(AncientArtifacts.ANCIENT_EMBLEM.getItemId()).rawtradable());
    }

    @Test
    public void tradable4() {
        assertTrue(new Item(SARADOMINS_BLESSED_SWORD).rawtradable());
    }

    // idk expected for these
    /*
    @Test
    public void tradable7() { // TODO expected?
        assertTrue(new Item(BRACELET_OF_ETHEREUM).isTradeable());
    }
    @Test
    public void tradable6() { // TODO expected?
        assertTrue(new Item(CRAWS_BOW).isTradeable());
    }
    */

    /*@Test
    public void loseRP() {
        assertFalse(Item.autoKeptOnDeath(new Item(RUNE_POUCH)));
    }

    @Test
    public void tradable5() {
        assertFalse(Item.autoKeptOnDeath(new Item(TOXIC_STAFF_OF_THE_DEAD)));
    }*/

    // idk expected for these
    /*@Test
    public void untradableBSS() {
        assertFalse(new Item(SARADOMINS_BLESSED_SWORD).isTradeable());
    }

    @Test
    public void untradableGM() {
        assertFalse(new Item(GRANITE_MAUL_20557).isTradeable());
    }*/

    @Test
    public void pk1() {
        Player p1 = new Player(new Session(null));
        p1.setLongUsername(1L);
        Player killer = new Player(new Session(null));
        killer.setLongUsername(2L);
        p1.getSkills().setXp(Skills.PRAYER, 14_000_000);
        p1.getSkills().setLevel(Skills.PRAYER, 99);
        p1.getSkills().setXp(Skills.RANGED, 14_000_000);
        p1.getSkills().setLevel(Skills.RANGED, 99);
        p1.setTile(new Tile(3092, 3530)); // wild somewhere around lvl 10
        p1.setPrayerActive(Prayers.PROTECT_ITEM, true);
        Skulling.assignSkullState(p1, SkullType.WHITE_SKULL);
        //p1.putAttrib(AttributeKey.BLOWPIPE_DART_ID, DRAGON_DART);
        //p1.putAttrib(AttributeKey.BLOWPIPE_DARTS_COUNT, 100);
        //p1.putAttrib(AttributeKey.BLOWPIPE_SCALES_COUNT, 200);
        WeaponInterfaces.updateWeaponInterface(p1);

        long s = System.currentTimeMillis();
        int[] invIds = new int[] {
            ABYSSAL_WHIP, DRAGON_DAGGER, SUPER_COMBAT_POTION4,
            RUNE_POUCH, RUNE_ARROW, TOXIC_BLOWPIPE // TWO blowpipes!
        };
        p1.inventory().add(new Item(SHARK+1, 20));
        int[] equipIds = new int[] { // not new items, requested inv items to move to equipment
            TOXIC_BLOWPIPE,RUNE_ARROW
        };
        int[] rpouch = new int[] {WATER_RUNE, DEATH_RUNE, BLOOD_RUNE};

        for (int invId : invIds) {
            p1.inventory().add(new Item(invId, 1));
        }
        for (int equipId : equipIds) {
            p1.getEquipment().equip(new Item(equipId, 1));
            BonusesInterface.sendBonuses(p1);
        }
        if (!p1.getEquipment().get(3).matchesId(TOXIC_BLOWPIPE)) {
            assertFalse(true); // fuck
        }
        for (int i : rpouch) {
            p1.getRunePouch().deposit(new Item(i, 1_000));
        }

        PlayerDeathDropResult p1d = ItemsOnDeath.droplootToKiller(p1, killer);
        debug(p1d);
        ItemContainer kept = new ItemContainer(100, ItemContainer.StackPolicy.ALWAYS, p1d.outKeep.stream().filter(Objects::nonNull).toArray(Item[]::new));
        ItemContainer del = new ItemContainer(100, ItemContainer.StackPolicy.ALWAYS, p1d.outDel.toArray(new Item[0]));
        ItemContainer dropped = p1d.allDrops();
        int match = 0;
        for (Item item : Arrays.asList(new Item(TOXIC_BLOWPIPE))) {
            if (kept.count(item.getId()) == item.getAmount()) {
                System.out.println("found tpb");
                match++;
            }
        }
        if (del.count(RUNE_POUCH) == 1) { // expect RP lost
            match++;
            System.out.println("found rp");
        }
        if (dropped.count(DRAGON_DART) == 100) {
            match++;
            System.out.println("found darts");
        }
        long t = System.currentTimeMillis() - s;
        debug(dropped.count(DRAGON_DART)+", "+del.count(RUNE_POUCH)+" "+kept.count(TOXIC_BLOWPIPE)+" "+t);

        assertEquals(match, 3);
    }

    @Test
    public void pk2() {
        Player p1 = new Player(new Session(null));
        p1.inventory().add(RUNE_POUCH, 1);
        p1.inventory().add(LOOTING_BAG, 1);
        // doesnt matter if not skulled, not in wild, both are always lost
        PlayerDeathDropResult p1d = ItemsOnDeath.droplootToKiller(p1, null);
        debug(p1d);
        ItemContainer del = new ItemContainer(100, ItemContainer.StackPolicy.ALWAYS, p1d.outDel.toArray(new Item[0]));
        int match = 0;
        if (del.count(RUNE_POUCH) == 1)
            match++;
        if (del.count(LOOTING_BAG) == 1)
            match++;
        assertEquals(match, 2);
    }
}
