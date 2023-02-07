package com.aelous.cache.definitions;

import com.aelous.GameConstants;
import com.aelous.utility.loaders.BloodMoneyPrices;
import com.aelous.model.items.Item;
import com.aelous.network.codec.RSBuffer;
import com.aelous.utility.ItemIdentifiers;
import io.netty.buffer.Unpooled;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Created by Bart Pelle on 10/4/2014.
 */
public class ItemDefinition implements Definition {

    public boolean isNote() {
        return notelink != -1 && noteModel != -1;
    }

    public int resizey;
    public int xan2d;

    public String unknown1;

    public int wearPos1;
    public int wearPos2;

    public int cost = 1;
    public int inventoryModel;

    public Map<Integer, Object> params = null;
    public int resizez;
    public int category;
    public short[] recol_s;
    public short[] recol_d;
    public String name = "null";
    public int zoom2d = 2000;
    public int yan2d;
    public int zan2d;
    public int yof2d;
    private int stackable;
    public int[] countco;
    public boolean members = false;
    public String[] options = new String[5];
    public String[] ioptions = new String[5];
    public int maleModel0;
    public int maleOffset;
    public int maleModel1;
    public short[] retex_s;
    public short[] retex_d;
    public int femaleModel1;
    public int maleModel2;
    public int xof2d;
    public int manhead;
    public int manhead2;
    public int womanhead;
    public int womanhead2;
    public int[] countobj;
    public int femaleModel2;
    public int notelink;
    public int femaleModel0;

    public int wearPos3;

    public int weight;
    public int femaleOffset;
    public int resizex;
    public int noteModel;
    public int ambient;
    public int contrast;
    public int team;
    public boolean grandexchange;
    public boolean unprotectable;
    public boolean dummyitem;
    public int placeheld = -1;
    public int pheld14401 = -1;
    public int shiftClickDropType = -1;
    private int op139 = -1;
    private int op140 = -1;

    public int id;

    // our fields: optimized speed so you dont need 1k loops
    public boolean isCrystal;
    public boolean tradeable_special_items;
    public boolean changes;
    public boolean autoKeptOnDeath;
    public BloodMoneyPrices bm;
    public boolean pvpAllowed;

    public ItemDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
        custom();
    }

    void decode(RSBuffer buffer) {
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            decodeValues(buffer, op);
        }
        postDecode(id);
    }

    void custom() {
        if (id == ItemIdentifiers.TOXIC_BLOWPIPE || id == ItemIdentifiers.SERPENTINE_HELM || id == ItemIdentifiers.TRIDENT_OF_THE_SWAMP || id == ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD
            || id == ItemIdentifiers.TOME_OF_FIRE || id == ItemIdentifiers.SCYTHE_OF_VITUR || id == ItemIdentifiers.SANGUINESTI_STAFF || id == ItemIdentifiers.CRAWS_BOW
            || id == ItemIdentifiers.VIGGORAS_CHAINMACE || id == ItemIdentifiers.THAMMARONS_SCEPTRE || id == ItemIdentifiers.TRIDENT_OF_THE_SEAS || id == ItemIdentifiers.MAGMA_HELM
            || id == ItemIdentifiers.TANZANITE_HELM || id == ItemIdentifiers.DRAGONFIRE_SHIELD || id == ItemIdentifiers.DRAGONFIRE_WARD || id == ItemIdentifiers.ANCIENT_WYVERN_SHIELD
            || id == ItemIdentifiers.ABYSSAL_TENTACLE || id == BARRELCHEST_ANCHOR || id == ItemIdentifiers.SARADOMINS_BLESSED_SWORD) {
            ioptions = new String[]{null, "Wield", null, null, "Drop"};
        }

        boolean replace_drop_with_destroy = Arrays.stream(Item.AUTO_KEPT_LIST).anyMatch(auto_kept_id -> auto_kept_id == id);

        if (replace_drop_with_destroy) {
            ioptions = new String[]{null, null, null, null, "Destroy"};
        }

        int[] untradeables_with_destroy = new int[]{
            VOLATILE_NIGHTMARE_STAFF,
            HARMONISED_NIGHTMARE_STAFF,
            ELDRITCH_NIGHTMARE_STAFF,
        };

        if (IntStream.of(untradeables_with_destroy).anyMatch(untradeable -> id == untradeable)) {
            ioptions = new String[]{null, null, null, null, "Destroy"};
        }

        if(name.contains("slayer helmet") || name.contains("Slayer helmet")) {
            ioptions = new String[]{null, "Wear", null, null, "Drop"};
        }

        //Bounty hunter emblem hardcoding.
        if (id == 12746 || (id >= 12748 && id <= 12756)) {
            unprotectable = true;
        } else if (id == 17000) {
            name = GameConstants.SERVER_NAME + " coins";
            countco = new int[]{2, 3, 4, 5, 25, 100, 250, 1000, 10000, 0};
            countobj = new int[]{17001, 17002, 17003, 17004, 17005, 17006, 17007, 17008, 17009, 0};
            stackable = 1;
        } else if (id == 23490) {
            name = "Larran's key tier I";
            ioptions = new String[]{null, null, null, null, "Drop"};
            countco = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            countobj = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        } /*else if (id == 14900) {
            name = "Larran's key tier II";
            stackable = 1;
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 14901) {
            name = "Larran's key tier III";
            stackable = 1;
            ioptions = new String[]{null, null, null, null, "Drop"};
        }*/ else if (id == 25902 || id == 25907 || id == 24445 || id == 25913) {
            name = "Twisted slayer helmet (i)";
            ioptions = new String[]{null, "Wear", null, null, "Drop"};
        } else if (id == 12791) {
            ioptions = new String[]{"Open", null, "Quick-Fill", "Empty", "Drop"};
        } else if (id == 14525) {
            name = "Mystery chest";
        } else if (id == 20238) {
            name = "Imbuement scroll";
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 12646) {
            name = "Niffler pet";
        } else if (id == 20693) {
            name = "Fawkes pet";
        } else if (id == 619) {
            name = "Vote ticket";
            stackable = 1;
            ioptions = new String[]{"Convert to Points", null, null, null, "Drop"};
        } else if (id == 13188) {
            name = "Dragon claws (or)";
        } else if (id == 28007) {
            name = "Ethereal partyhat";
        } else if (id == 28008) {
            name = "Ethereal halloween mask";
        } else if (id == 28009) {
            name = "Ethereal santa hat";
        } else if (id == 30074) {
            name = "Lava d'hide coif";
        } else if (id == 30077) {
            name = "Lava d'hide body";
        } else if (id == 30080) {
            name = "Lava d'hide chaps";
        } else if (id == 30183) {
            name = "Twisted bow (i)";
        } else if (id == 30175) {
            name = "Ancestral hat (i)";
        } else if (id == 30177) {
            name = "Ancestral robe top (i)";
        } else if (id == 30179) {
            name = "Ancestral robe bottom (i)";
        } else if (id == 30038) {
            name = "Primordial boots (or)";
        } else if (id == 23650) {
            notelink = -1;
            noteModel = -1;
            name = "Rune pouch (i)";
            ioptions = new String[]{"Open", null, null, "Empty", "Destroy"};
        } else if (id == 4447) {
            name = "Double drops lamp";
        } else if (id == 6199) {
            ioptions = new String[]{"Quick-open", null, "Open", null, null};
        } else if (id == 16451) {
            name = "Weapon Mystery Box";
            stackable = 0;
        } else if (id == 16452) {
            name = "Armour Mystery Box";
            stackable = 0;
        } else if (id == 30185) {
            name = "Donator Mystery Box";
        } else if (id == 30026) {
            name = "Molten Mystery Box";
            stackable = 0;
        } else if (id == 30186) {
            name = "H'ween Mystery Box";
            stackable = 0;
        } else if (id == 30242) {
            name = "Winter Item Casket";
        } else if (id == 16454) {
            name = "Legendary Mystery Box";
            stackable = 0;
        } else if (id == 16455) {
            name = "Grand Mystery Box";
            stackable = 0;
        } else if (id == 16456) {
            name = "PetDefinitions Mystery Box";
            stackable = 0;
        } else if (id == 16458) {
            name = "Epic PetDefinitions Mystery Box";
            stackable = 0;
        } else if (id == 16459) {
            name = "Raids Mystery Box";
            stackable = 0;
        } /*else if (id == 16460) {
            name = "Zenyte Mystery Box";
            stackable = 0;
        }*/ else if (id == 16461) {
            name = "Starter Box";
            stackable = 0;
        } else if (id == 16462) {
            name = "Clan Box";
            stackable = 0;
        } else if (id == 6722) {
            name = "Zombies champion pet";
        } else if (id == 2866) {
            name = "Earth arrows";
        } else if (id == 4160) {
            name = "Fire arrows";
        } else if (id == 7806) {
            name = "Ancient warrior sword";
        } else if (id == 7807) {
            name = "Ancient warrior axe";
        } else if (id == 7808) {
            name = "Ancient warrior maul";
        } else if (id == 24983) {
            name = "Ancient warrior sword (c)";
        } else if (id == 24981) {
            name = "Ancient warrior axe (c)";
        } else if (id == 24982) {
            name = "Ancient warrior maul (c)";
        } else if (id == 2944) {
            name = "Key of Drops";
        } else if (id == 12773) {
            name = "Lava whip";
        } else if (id == 12774) {
            name = "Frost whip";
        } else if (id == 10586) {
            ioptions = new String[]{null, null, null, null, "Drop"};
            name = "Genie pet";
        } else if (id == 12102) {
            name = "Grim Reaper pet";
        } else if (id == 12081) {
            name = "Elemental bow";
        } else if (id == 4067) {
            name = "Donator ticket";
            stackable = 1;
        } else if (id == 13190) {
            name = "5$ bond";
        } else if (id == 8013) {
            name = "Home teleport";
        } else if (id == 964) {
            name = "Vengeance";
        } else if (id == 18335) {
            stackable = 0;
            name = "Lava partyhat";
        } else if (id == 16278) {
            stackable = 0;
            name = "$10 bond";
        } else if (id == 16263) {
            stackable = 0;
            name = "$20 bond";
        } else if (id == 16264) {
            stackable = 0;
            name = "$40 bond";
        } else if (id == 16265) {
            stackable = 0;
            name = "$50 bond";
        } else if (id == 16266) {
            stackable = 0;
            name = "$100 bond";
        } else if (id == 16012) {
            stackable = 0;
            name = "Baby Dark Beast pet";
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 16024) {
            stackable = 0;
            name = "Baby Abyssal demon pet";
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 15331) {
            stackable = 0;
            name = "Ring of confliction";
            ioptions = new String[]{null, "Wear", null, null, "Drop"};
        } else if (id == 16167) {
            stackable = 0;
            name = "Alchemist's ring";
            ioptions = new String[]{null, "Wear", null, null, "Drop"};
        } else if (id == 16168) {
            stackable = 0;
            name = "Deadeye's ring";
            ioptions = new String[]{null, "Wear", null, null, "Drop"};
        } else if (id == 16169) {
            stackable = 0;
            name = "Ring of perfection";
            ioptions = new String[]{null, "Wear", null, null, "Drop"};
        } else if (id == 13215) {
            name = "Bloody Token";
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 30235) {
            name = "H'ween token";
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 32236) {
            name = "Winter token";
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 30050) {
            name = "OS-GP token";
            ioptions = new String[]{null, null, null, null, "Drop"};
        } else if (id == 30297) {
            name = "Mythical boots";
        } else if (id == 27644) {
            name = "Salazar slytherins locket";
        } else if (id == 28643) {
            name = "Fenrir greyback Jr. pet";
        } else if (id == 28642) {
            name = "Fluffy Jr. pet";
        } else if (id == 28641) {
            name = "Talonhawk crossbow";
        } else if (id == 28640) {
            name = "Elder wand stick";
        } else if (id == 28639) {
            name = "Elder wand handle";
        } else if (id == 30181 || id == 30184) {
            name = "Elder wand";
        } else if (id == 30253) {
            name = "Cloak of invisibility";
        } else if (id == 30252) {
            name = "Marvolo Gaunts Ring";
        } else if (id == 30251) {
            name = "Tom Riddle's Diary";
        } else if (id == 30250) {
            name = "Nagini";
        } else if (id == 10858) {
            name = "Sword of gryffindor";
        } else if (id == 30338) {
            name = "Male centaur pet";
        } else if (id == 30340) {
            name = "Female centaur pet";
        } else if (id == 30342) {
            name = "Dementor pet";
        } else if(id == 21291) {
            name = "Jal-nib-rek pet";
        } else if(id == 8788) {
            name = "Corrupting stone";
        } else if(id == 30048) {
            name = "Corrupted gauntlets";
        } else if(id == 32102) {
            name = "Blood Reaper pet";
        } else if (id == 23757) {
            name = "Yougnleff pet";
        } else if (id == 23759) {
            name = "Corrupted yougnleff pet";
        } else if (id == 30016) {
            name = "Founder imp pet";
        } else if (id == 30018) {
            name = "Corrupted nechryarch pet";
        } else if (id == 30033) {
            name = "Mini necromancer pet";
        } else if (id == 30044) {
            name = "Jaltok-jad pet";
        } else if (id == 30131) {
            name = "Baby lava dragon pet";
        } else if (id == 16173) {
            name = "Jawa pet";
        } else if (id == 16172) {
            name = "Baby aragog pet";
        } else if (id == 16020) {
            name = "Dharok pet";
        } else if (id == 22319) {
            name = "TzRek-Zuk pet";
        } else if (id == 24491) {
            name = "Little nightmare pet";
        } else if (id == 30122) {
            name = "Corrupt totem base";
        } else if (id == 30123) {
            name = "Corrupt totem middle";
        } else if (id == 30124) {
            name = "Corrupt totem top";
        } else if (id == 30125) {
            name = "Corrupt totem";
        } else if (id == 16005) {
            name = "Baby Squirt pet";
            stackable = 0;
        } else if (id == 7999) {
            name = "PetDefinitions paint (black)";
        } else if (id == 8000) {
            name = "PetDefinitions paint (white)";
        } else if (id == 15300) {
            stackable = 0;
            name = "Recover special (4)";
        } else if (id == 15301) {
            stackable = 0;
            name = "Recover special (3)";
        } else if (id == 15302) {
            stackable = 0;
            name = "Recover special (2)";
        } else if (id == 15303) {
            stackable = 0;
            name = "Recover special (1)";
        } else if (id == 23818) {
            name = "Barrelchest pet";
            ioptions = new String[]{null, null, null, null, "Drop"};
            stackable = 0;
        } else if (id == 30049) {
            name = "Magma blowpipe";
        } else if (id == 16171) {
            name = "Wampa pet";
            ioptions = new String[]{null, null, null, null, "Drop"};
            stackable = 0;
        } else if (id == 16013) {
            name = "PetDefinitions kree'arra (white)";
            stackable = 0;
            ioptions = new String[]{null, null, null, "Wipe-off paint", null};
        } else if (id == 16014) {
            name = "PetDefinitions zilyana (white)";
            stackable = 0;
            ioptions = new String[]{null, null, null, "Wipe-off paint", null};
        } else if (id == 29102) {
            name = "Scythe of vitur kit";
        } else if (id == 29103) {
            name = "Twisted bow kit";
        } else if (id == 16015) {
            name = "PetDefinitions general graardor (black)";
            stackable = 0;
            ioptions = new String[]{null, null, null, "Wipe-off paint", null};
        } else if (id == 16016) {
            name = "PetDefinitions k'ril tsutsaroth (black)";
            stackable = 0;
            ioptions = new String[]{null, null, null, "Wipe-off paint", null};
        } else if (id == 12873 || id == 12875 || id == 12877 || id == 12879 || id == 12881 || id == 12883) {
            ioptions = new String[5];
            ioptions[0] = "Open";
        } else if (id == ELDER_MAUL_21205 || id == ItemIdentifiers.ARMADYL_GODSWORD_OR || id == ItemIdentifiers.BANDOS_GODSWORD_OR || id == ItemIdentifiers.SARADOMIN_GODSWORD_OR || id == ItemIdentifiers.ZAMORAK_GODSWORD_OR || id == ItemIdentifiers.GRANITE_MAUL_12848) {
            ioptions = new String[]{null, "Wield", null, null, "Drop"};
        } else if (id == ItemIdentifiers.ATTACKER_ICON || id == ItemIdentifiers.COLLECTOR_ICON || id == ItemIdentifiers.DEFENDER_ICON || id == ItemIdentifiers.HEALER_ICON || id == ItemIdentifiers.AMULET_OF_FURY_OR || id == ItemIdentifiers.OCCULT_NECKLACE_OR || id == ItemIdentifiers.NECKLACE_OF_ANGUISH_OR || id == ItemIdentifiers.AMULET_OF_TORTURE_OR || id == ItemIdentifiers.BERSERKER_NECKLACE_OR || id == ItemIdentifiers.TORMENTED_BRACELET_OR || id == ItemIdentifiers.DRAGON_DEFENDER_T || id == ItemIdentifiers.DRAGON_BOOTS_G) {
            ioptions = new String[]{null, "Wear", null, null, "Destroy"};
            stackable = 0;
        }
    }

    private void decodeValues(RSBuffer stream, int opcode)
    {
        if (opcode == 1)
        {
            inventoryModel = stream.readUShort();
        }
        else if (opcode == 2)
        {
            name = stream.readJagexString();
        }
        else if (opcode == 4)
        {
            zoom2d = stream.readUShort();
        }
        else if (opcode == 5)
        {
            xan2d = stream.readUShort();
        }
        else if (opcode == 6)
        {
            yan2d = stream.readUShort();
        }
        else if (opcode == 7)
        {
            xof2d = stream.readUShort();
            if (xof2d > 32767)
            {
                xof2d -= 65536;
            }
        }
        else if (opcode == 8)
        {
            yof2d = stream.readUShort();
            if (yof2d > 32767)
            {
                yof2d -= 65536;
            }
        }
        else if (opcode == 9)
        {
            unknown1 = stream.readString();
        }
        else if (opcode == 11)
        {
            stackable = 1;
        }
        else if (opcode == 12)
        {
            cost = stream.readInt();
        }
        else if (opcode == 13)
        {
            wearPos1 = stream.readByte();
        }
        else if (opcode == 14)
        {
            wearPos2 = stream.readByte();
        }
        else if (opcode == 16)
        {
            members = true;
        }
        else if (opcode == 23)
        {
            maleModel0 = stream.readUShort();
            maleOffset = stream.readUByte();
        }
        else if (opcode == 24)
        {
            maleModel1 = stream.readUShort();
        }
        else if (opcode == 25)
        {
            femaleModel0 = stream.readUShort();
            femaleOffset = stream.readUByte();
        }
        else if (opcode == 26)
        {
            femaleModel1 = stream.readUShort();
        }
        else if (opcode == 27)
        {
            wearPos3 = stream.readByte();
        }
        else if (opcode >= 30 && opcode < 35)
        {
            options[opcode - 30] = stream.readString();
            if (options[opcode - 30].equalsIgnoreCase("Hidden"))
            {
                options[opcode - 30] = null;
            }
        }
        else if (opcode >= 35 && opcode < 40)
        {
            ioptions[opcode - 35] = stream.readString();
        }
        else if (opcode == 40)
        {
            int var5 = stream.readUByte();
            recol_s = new short[var5];
            recol_d = new short[var5];

            for (int var4 = 0; var4 < var5; ++var4)
            {
                recol_s[var4] = (short) stream.readUShort();
                recol_d[var4] = (short) stream.readUShort();
            }

        }
        else if (opcode == 41)
        {
            int var5 = stream.readUByte();
            retex_s = new short[var5];
            retex_d = new short[var5];

            for (int var4 = 0; var4 < var5; ++var4)
            {
                retex_s[var4] = (short) stream.readUShort();
                retex_d[var4] = (short) stream.readUShort();
            }

        }
        else if (opcode == 42)
        {
            shiftClickDropType = stream.readByte();
        }
        else if (opcode == 65)
        {
            grandexchange = true;
        }
        else if (opcode == 75)
        {
            weight = stream.readShort();
        }
        else if (opcode == 78)
        {
            maleModel2 = stream.readUShort();
        }
        else if (opcode == 79)
        {
            femaleModel2 = stream.readUShort();
        }
        else if (opcode == 90)
        {
            manhead = stream.readUShort();
        }
        else if (opcode == 91)
        {
            womanhead = stream.readUShort();
        }
        else if (opcode == 92)
        {
            manhead2 = stream.readUShort();
        }
        else if (opcode == 93)
        {
            womanhead2 = stream.readUShort();
        }
        else if (opcode == 94)
        {
            category = stream.readUShort();
        }
        else if (opcode == 95)
        {
            zan2d = stream.readUShort();
        }
        else if (opcode == 97)
        {
            notelink = stream.readUShort();
        }
        else if (opcode == 98)
        {
            noteModel = stream.readUShort();
        }
        else if (opcode >= 100 && opcode < 110)
        {
            if (countobj == null)
            {
                countobj = new int[10];
                countco = new int[10];
            }

            countobj[opcode - 100] = stream.readUShort();
            countco[opcode - 100] = stream.readUShort();
        }
        else if (opcode == 110)
        {
            resizex = stream.readUShort();
        }
        else if (opcode == 111)
        {
            resizey = stream.readUShort();
        }
        else if (opcode == 112)
        {
            resizez = stream.readUShort();
        }
        else if (opcode == 113)
        {
            ambient = stream.readByte();
        }
        else if (opcode == 114)
        {
            contrast = stream.readByte();
        }
        else if (opcode == 115)
        {
            team = stream.readUByte();
        }
        else if (opcode == 139)
        {
            op139 = stream.readUShort();
        }
        else if (opcode == 140)
        {
            op140 = stream.readUShort();
        }
        else if (opcode == 148)
        {
            placeheld = stream.readUShort();
        }
        else if (opcode == 149)
        {
            pheld14401 = stream.readUShort();
        }
        else if (opcode == 249)
        {
            int length = stream.readUByte();

            params = new HashMap<>(length);

            for (int i = 0; i < length; i++)
            {
                boolean isString = stream.readUByte() == 1;
                int key = stream.read24BitInt();
                Object value;

                if (isString)
                {
                    value = stream.readString();
                }

                else
                {
                    value = stream.readInt();
                }

                params.put(key, value);
            }
        }
    }

    void postDecode(int id) {
        if (id == 6808) {
            name = "Scroll of Imbuement";
        }
        bm = new BloodMoneyPrices();
    }

    public int highAlchValue() {
        return (int) (cost * 0.65);
    }

    public static int method32(int var0) {
        --var0;
        var0 |= var0 >>> 1;
        var0 |= var0 >>> 2;
        var0 |= var0 >>> 4;
        var0 |= var0 >>> 8;
        var0 |= var0 >>> 16;
        return var0 + 1;
    }

    public Map<Integer, Object> clientScriptData;

    public boolean stackable() {
        return stackable == 1 || noteModel > 0 || id == 13215 || id == 32236 || id == 30050 || id == 30235;
    }

    public boolean noted() {
        return noteModel > 0;
    }

    @Override
    public String toString() {
        return "ItemDefinition{" +
            "resizey=" + resizey +
            ", xan2d=" + xan2d +
            ", cost=" + cost +
            ", inventoryModel=" + inventoryModel +
            ", resizez=" + resizez +
            ", recol_s=" + Arrays.toString(recol_s) +
            ", recol_d=" + Arrays.toString(recol_d) +
            ", name='" + name + '\'' +
            ", zoom2d=" + zoom2d +
            ", yan2d=" + yan2d +
            ", zan2d=" + zan2d +
            ", yof2d=" + yof2d +
            ", stackable=" + stackable +
            ", countco=" + Arrays.toString(countco) +
            ", members=" + members +
            ", options=" + Arrays.toString(options) +
            ", ioptions=" + Arrays.toString(ioptions) +
            ", maleModel0=" + maleModel0 +
            ", maleModel1=" + maleModel1 +
            ", retex_s=" + Arrays.toString(retex_s) +
            ", retex_d=" + Arrays.toString(retex_d) +
            ", femaleModel1=" + femaleModel1 +
            ", maleModel2=" + maleModel2 +
            ", xof2d=" + xof2d +
            ", manhead=" + manhead +
            ", manhead2=" + manhead2 +
            ", womanhead=" + womanhead +
            ", womanhead2=" + womanhead2 +
            ", countobj=" + Arrays.toString(countobj) +
            ", femaleModel2=" + femaleModel2 +
            ", notelink=" + notelink +
            ", femaleModel0=" + femaleModel0 +
            ", resizex=" + resizex +
            ", noteModel=" + noteModel +
            ", ambient=" + ambient +
            ", contrast=" + contrast +
            ", team=" + team +
            ", grandexchange=" + grandexchange +
            ", unprotectable=" + unprotectable +
            ", dummyitem=" + dummyitem +
            ", placeheld=" + placeheld +
            ", pheld14401=" + pheld14401 +
            ", shiftClickDropType=" + shiftClickDropType +
            ", op139=" + op139 +
            ", op140=" + op140 +
            ", id=" + id +
            ", clientScriptData=" + clientScriptData +
            '}';
    }
}
