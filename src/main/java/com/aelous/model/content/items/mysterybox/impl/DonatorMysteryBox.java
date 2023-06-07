package com.aelous.model.content.items.mysterybox.impl;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.content.items.mysterybox.MboxItem;
import com.aelous.model.content.items.mysterybox.MysteryBox;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.utility.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.aelous.utility.ItemIdentifiers.*;

public class DonatorMysteryBox extends MysteryBox {
    @Override
    protected String name() {
        return "Mystery box";
    }

    @Override
    public int mysteryBoxId() {
        return MYSTERY_BOX;
    }

    private static final int EXTREME_ROLL = 500;
    private static final int RARE_ROLL = 50;
    private static final int UNCOMMON_ROLL = 20;

    private static final MboxItem[] EXTREMELY_RARE = new MboxItem[]{
        new MboxItem(ARCANE_SPIRIT_SHIELD),
        new MboxItem(ELDER_MAUL),
    };

    private static final MboxItem[] RARE = new MboxItem[]{
        new MboxItem(DRAGON_CLAWS),
        new MboxItem(ARMADYL_GODSWORD),
        new MboxItem(GHRAZI_RAPIER),
        new MboxItem(SPECTRAL_SPIRIT_SHIELD),
        new MboxItem(HEAVY_BALLISTA),
        new MboxItem(TOXIC_STAFF_OF_THE_DEAD),
        new MboxItem(SERPENTINE_HELM),
    };

    private static final MboxItem[] UNCOMMON = new MboxItem[]{
        new MboxItem(DRAGON_HUNTER_CROSSBOW),
        new MboxItem(DRAGON_CROSSBOW),
        new MboxItem(BANDOS_GODSWORD),
        new MboxItem(SARADOMIN_GODSWORD),
        new MboxItem(ZAMORAK_GODSWORD),
        new MboxItem(LIGHT_BALLISTA),
        new MboxItem(STAFF_OF_THE_DEAD),
        new MboxItem(PRIMORDIAL_BOOTS),
        new MboxItem(PEGASIAN_BOOTS),
        new MboxItem(ETERNAL_BOOTS),
        new MboxItem(DRAGON_DEFENDER),
        new MboxItem(ANTIVENOM4 + 1, 100),
    };

    private static final MboxItem[] COMMON = new MboxItem[]{
        new MboxItem(BERSERKER_RING_I),
        new MboxItem(ARCHERS_RING_I),
        new MboxItem(SEERS_RING_I),
        new MboxItem(WARRIOR_RING_I),
        new MboxItem(BERSERKER_RING),
        new MboxItem(ARCHERS_RING),
        new MboxItem(SEERS_RING),
        new MboxItem(WARRIOR_RING),
        new MboxItem(BERSERKER_NECKLACE),
        new MboxItem(FIGHTER_HAT),
        new MboxItem(FIGHTER_TORSO),
        new MboxItem(AMULET_OF_FURY),
        new MboxItem(RANGER_BOOTS),
        new MboxItem(ROBIN_HOOD_HAT),
        new MboxItem(DRAGON_JAVELIN, 50),
        new MboxItem(DRAGON_DART, 25),
        new MboxItem(GRANITE_MAUL_24225),
        new MboxItem(ABYSSAL_TENTACLE),
        new MboxItem(OBSIDIAN_HELMET),
        new MboxItem(OBSIDIAN_PLATEBODY),
        new MboxItem(OBSIDIAN_PLATELEGS),
        new MboxItem(MAGES_BOOK),
        new MboxItem(STAFF_OF_LIGHT),
        new MboxItem(DARK_BOW),
    };

    private static final MboxItem[] EXTREMELY_RARE_ECO = new MboxItem[]{
        new MboxItem(1053).broadcastWorldMessage(true), //Green halloween mask
        new MboxItem(1055).broadcastWorldMessage(true), //Blue halloween mask
        new MboxItem(1057).broadcastWorldMessage(true), //Red halloween mask
        new MboxItem(11847).broadcastWorldMessage(true), //Black halloween mask
        new MboxItem(1050).broadcastWorldMessage(true), //Santa hat
        new MboxItem(13343).broadcastWorldMessage(true), //Black santa hat
        new MboxItem(13344).broadcastWorldMessage(true), //Inverted santa hat
        new MboxItem(1038).broadcastWorldMessage(true), //Red party hat
        new MboxItem(1040).broadcastWorldMessage(true), //Yellow party hat
        new MboxItem(1042).broadcastWorldMessage(true), //Blue party hat
        new MboxItem(1044).broadcastWorldMessage(true), //Green party hat
        new MboxItem(1046).broadcastWorldMessage(true), //Purple party hat
        new MboxItem(1048).broadcastWorldMessage(true), //White  party hat
        new MboxItem(11862).broadcastWorldMessage(true), //Black party hat
        new MboxItem(11863).broadcastWorldMessage(true), //Rainbow party hat
        new MboxItem(12399).broadcastWorldMessage(true), //Partyhat & specs
        new MboxItem(962).broadcastWorldMessage(true), // Xmas cracker
        new MboxItem(1050).broadcastWorldMessage(true), // Santa hat
        new MboxItem(12422).broadcastWorldMessage(true), // 3rd age wand
        new MboxItem(12424).broadcastWorldMessage(true), // 3rd age bow
        new MboxItem(12426).broadcastWorldMessage(true), // 3rd age sword
        new MboxItem(12437).broadcastWorldMessage(true), // 3rd age cloak
        new MboxItem(10330).broadcastWorldMessage(true), // 3rd age range top
        new MboxItem(10332).broadcastWorldMessage(true), // 3rd age range legs
        new MboxItem(10334).broadcastWorldMessage(true), // 3rd age range coif
        new MboxItem(10336).broadcastWorldMessage(true), // 3rd age range vanbraces
        new MboxItem(10338).broadcastWorldMessage(true), // 3rd age robe top
        new MboxItem(10340).broadcastWorldMessage(true), // 3rd age robe
        new MboxItem(10342).broadcastWorldMessage(true), // 3rd age mage hat
        new MboxItem(10344).broadcastWorldMessage(true), // 3rd age amulet
        new MboxItem(10346).broadcastWorldMessage(true), // 3rd age platelegs
        new MboxItem(10348).broadcastWorldMessage(true), // 3rd age platebody
        new MboxItem(10350).broadcastWorldMessage(true), // 3rd age fullhelm
        new MboxItem(10352).broadcastWorldMessage(true), // 3rd age kiteshield
    };

    private static final MboxItem[] RARE_ECO = new MboxItem[]{
        new MboxItem(12825).broadcastWorldMessage(true), //Arcane spirit shield
        new MboxItem(12821).broadcastWorldMessage(true), //Spectral spirit shield
        new MboxItem(22981).broadcastWorldMessage(true), //Ferocious gloves
        new MboxItem(22978).broadcastWorldMessage(true), //Dragon hunter lance
        new MboxItem(11806).broadcastWorldMessage(true), //Armadyl godsword
        new MboxItem(12821).broadcastWorldMessage(true), //Spectral spirit shield
        new MboxItem(13576).broadcastWorldMessage(true), //Dragon warhammer
        new MboxItem(11785).broadcastWorldMessage(true), //Armadyl crossbow
        new MboxItem(12902).broadcastWorldMessage(true), //Toxic staff of the dead
        new MboxItem(11791).broadcastWorldMessage(true), //Staff of the dead
        new MboxItem(12931).broadcastWorldMessage(true), //Serpentine helm
        new MboxItem(13235).broadcastWorldMessage(true), //Eternal boots
        new MboxItem(13237).broadcastWorldMessage(true), //Pegasian boots
        new MboxItem(13239).broadcastWorldMessage(true), //Primordial boots
        new MboxItem(11828).broadcastWorldMessage(true), //Armadyl chestplate
        new MboxItem(11830).broadcastWorldMessage(true), //Armadyl chainskirt
        new MboxItem(11826).broadcastWorldMessage(true), //Armadyl helmet
        new MboxItem(11834).broadcastWorldMessage(true), //Bandos tassets
        new MboxItem(11832).broadcastWorldMessage(true), //Bandos chestplate
        new MboxItem(11808).broadcastWorldMessage(true), //Zamorak godsword
        new MboxItem(11806).broadcastWorldMessage(true), //Saradomin godsword
        new MboxItem(11804).broadcastWorldMessage(true), //Bandos godsword
        new MboxItem(12696, World.getWorld().random(4000, 5000)).broadcastWorldMessage(true), //5000 Super combat potions
        new MboxItem(13442, World.getWorld().random(4000, 5000)).broadcastWorldMessage(true) //5000 Anglerfish
    };

    private static final MboxItem[] UNCOMMON_ECO = new MboxItem[]{
        new MboxItem(6737), //Berserker ring
        new MboxItem(6731), //Seers ring
        new MboxItem(13271), //Abyssal dagger
        new MboxItem(12791), //Rune pouch
        new MboxItem(2581), //Robin hood hat
        new MboxItem(12596), //Rangers' tunic
        new MboxItem(22975), //Brimstone ring
        new MboxItem(12371), //Lava dragon mask
        new MboxItem(11283), //Dragonfire shield
        new MboxItem(11791), //Staff of the dead
        new MboxItem(4224), //New crystal shield
        new MboxItem(12831), //Blessed spirit shield
        new MboxItem(11926), //Odium ward
        new MboxItem(11924), //Malediction ward
        new MboxItem(6735), //Warriors ring
        new MboxItem(6733), //Archers ring
        new MboxItem(6731), //Seers ring
        new MboxItem(11773), //Berserker ring (i)
        new MboxItem(11772), //Warriors ring (i)
        new MboxItem(11771), //Archers ring (i)
        new MboxItem(11770), //Seers ring (i))
        new MboxItem(12002), //Occult necklace
        new MboxItem(6585), //Amulet of fury
        new MboxItem(1419), // Scythe
        new MboxItem(1037), // Bunny ears
        new MboxItem(19988), // Blacksmith's helm
        new MboxItem(19991), // Bucket helm
        new MboxItem(20059), // Bucket helm (g)
        new MboxItem(20050), // Obsidian cape (f)
    };

    private static final MboxItem[] COMMON_ECO = new MboxItem[]{
        new MboxItem(4708), //Ahrim's hood
        new MboxItem(4712), //Ahrim's robetop
        new MboxItem(4714), //Ahrim's robeskirt
        new MboxItem(4716), //Dharok's helm
        new MboxItem(4720), //Dharok's platebody
        new MboxItem(4722), //Dharok's platelegs
        new MboxItem(4718), //Dharok's greataxe
        new MboxItem(4736), //Karil's leathertop
        new MboxItem(13442, 100), //100 Anglerfish
        new MboxItem(11235), //Dark bow
        new MboxItem(8927), //Bandana eyepatch
        new MboxItem(2643), //Dark cavalier
        new MboxItem(12301), //Blue headband
        new MboxItem(2577), //Ranger boots
        new MboxItem(12639), //Guthix halo
        new MboxItem(12430), //Afro
        new MboxItem(12245), //Beanie
        new MboxItem(12638), //Zamorak halo
        new MboxItem(12637), //Saraodmin halo
        new MboxItem(11899), //Decorative platebody (Melee)
        new MboxItem(11900), //Decorative platelegs (Melee)
        new MboxItem(11898), //Decorative hat (Magic)
        new MboxItem(11896), //Decorative robe top (Magic)
        new MboxItem(11897), //Decorative bottoms (Magic)
        new MboxItem(12375), //Black cane
        new MboxItem(12377), //Adamant cane
        new MboxItem(12365), //Iron dragon mask
        new MboxItem(12367), //Steel dragon mask
        new MboxItem(12369), //Mithril dragon mask
        new MboxItem(12518), //Green dragon mask
        new MboxItem(12522), //Red dragon mask
        new MboxItem(12524), //Black dragon mask
        new MboxItem(12763), //White dark bow paint
        new MboxItem(12761), //Yellow dark bow paint
        new MboxItem(12759), //Green dark bow paint
        new MboxItem(12757), //Blue dark bow paint
        new MboxItem(12769), //Frozen whip mix
        new MboxItem(12771), //Volcanic whip mix
        new MboxItem(12829), //Spirit shield
        new MboxItem(6922), //Infinity gloves
        new MboxItem(6918), //Infinity hat
        new MboxItem(6916), //Infinity top
        new MboxItem(6924), //Infinity bottoms
        new MboxItem(6528), //Tzhaar-ket-om
        new MboxItem(6525), //Toktz-xil-ek
        new MboxItem(4151), //Abyssal whip
        new MboxItem(24225), //Granite maul
        new MboxItem(6920), //Infinity boots
        new MboxItem(11128), //Berseker necklace
        new MboxItem(12696, 250), //250 Super combat potions
        new MboxItem(13442, 250), //250 Anglerfish
        new MboxItem(12696, 500), //500 Super combat potions
        new MboxItem(13442, 500), //500 Anglerfish
        new MboxItem(1037), //Bunny ears
        new MboxItem(6666), //Flippers
        new MboxItem(4566), //rubber chicken
        new MboxItem(13182), //Bunny feat
        new MboxItem(12006), //Abyssal tentacle
        new MboxItem(6665), //Mudskipper hat
        new MboxItem(11919), //Cow mask
        new MboxItem(12956), //Cow top
        new MboxItem(12957), //Cow bottoms
        new MboxItem(12958), //Cow gloves
        new MboxItem(12959), //Cow boots
        new MboxItem(12379), //Rune cane
        new MboxItem(12373), //Dragon cane
        new MboxItem(12363), //Bronze dragon mask
        new MboxItem(20517), //Elder chaos top
        new MboxItem(20520), //Elder chaos bottom
        new MboxItem(20595), //Elder chaos hood
        new MboxItem(12696, 1000), //1000 Super combat potions
        new MboxItem(13442, 1000), //1000 Anglerfish
        new MboxItem(12696, 1500), //1500 Super combat potions
        new MboxItem(13442, 1500), //1500 Anglerfish
        new MboxItem(12696, 5000), //5000 Super combat potions
        new MboxItem(13442, 5000), //5000 Anglerfish
        new MboxItem(20008), // Fancy tiara
        new MboxItem(20110), // Bowl wig
        new MboxItem(20020), // Lesser demon mask
        new MboxItem(20023), // Greater demon mask
        new MboxItem(20026), // Black demon mask
        new MboxItem(20029), // Old demon mask
        new MboxItem(20032) // Jungle demon mask
    };

    private MboxItem[] allRewardsCached;

    public MboxItem[] allPossibleRewards() {
        if (allRewardsCached == null) {
            ArrayList<MboxItem> mboxItems = new ArrayList<>();
            if (GameServer.properties().pvpMode) {
                mboxItems.addAll(Arrays.asList(EXTREMELY_RARE));
                mboxItems.addAll(Arrays.asList(RARE));
                mboxItems.addAll(Arrays.asList(UNCOMMON));
                mboxItems.addAll(Arrays.asList(COMMON));
            } else {
                mboxItems.addAll(Arrays.asList(EXTREMELY_RARE_ECO));
                mboxItems.addAll(Arrays.asList(RARE_ECO));
                mboxItems.addAll(Arrays.asList(UNCOMMON_ECO));
                mboxItems.addAll(Arrays.asList(COMMON_ECO));
            }
            allRewardsCached = mboxItems.toArray(new MboxItem[0]);
        }
        return allRewardsCached;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.REGULAR_MYSTERY_BOXES_OPENED;
    }

    @Override
    public MboxItem rollReward() {
        if (Utils.rollDie(EXTREME_ROLL, 1)) {
            return Utils.randomElement(EXTREMELY_RARE);
        } else if (Utils.rollDie(RARE_ROLL, 1)) {
            return Utils.randomElement(RARE);
        } else if (Utils.rollDie(UNCOMMON_ROLL, 1)) {
            return Utils.randomElement(UNCOMMON);
        } else {
            return Utils.randomElement(COMMON);
        }
    }
}
