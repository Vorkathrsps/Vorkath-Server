package com.aelous.cache.definitions;

import com.aelous.model.content.consumables.FoodConsumable;
import com.aelous.model.content.consumables.potions.Potions;
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
    public boolean isCrystal;
    public boolean tradeable_special_items;
    public boolean changes;
    public boolean autoKeptOnDeath;
    public BloodMoneyPrices bm;
    public boolean pvpAllowed;//this isnt pvp mode lol ik, but have a feeling its possibly fucking with it, if values arent set ? idk
    public boolean consumable;

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

        if (id == DHAROKS_ARMOUR_SET || id == KARILS_ARMOUR_SET || id == GUTHANS_ARMOUR_SET || id == AHRIMS_ARMOUR_SET || id == VERACS_ARMOUR_SET) {
            ioptions = new String[]{null, "Open", null, null, "Drop"};
        }

        //Bounty hunter emblem hardcoding.
        if (id == 12746 || (id >= 12748 && id <= 12756)) {
            unprotectable = true;
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
        bm = new BloodMoneyPrices(id, this.getKeptOnDeathValue());

        for (FoodConsumable.Food food : FoodConsumable.Food.values()) {
            if (food.getItemId() == id) {
                consumable = true;
                break;
            }
        }

        for (Potions.Potion potion : Potions.Potion.values()) {
            for (int potionId : potion.ids) {
                if (potionId == id) {
                    consumable = true;
                    break;
                }
            }
        }
    }

    public int highAlchValue() {
        if (cost <= 0)
            return 0;
        return cost *= 0.65;
    }

    public final static Item[] BANK_ITEMS = {
      new Item(ANCESTRAL_ROBE_BOTTOM),
      new Item(ANCESTRAL_ROBE_TOP)
    };

    private boolean nameToLowerCase(String name) {
        return this.name.toLowerCase().contains(name);
    }

    public int getKeptOnDeathValue() {
        if (nameToLowerCase("ancestral") || nameToLowerCase("masori")
        || nameToLowerCase("eldritch") || nameToLowerCase("volatile nightmare staff")
        || nameToLowerCase("harmonised nightmare staff") || nameToLowerCase("venator bow")
        || nameToLowerCase("bow of faerdhinin")) {
            cost = 2_000_000;
        }
        if (nameToLowerCase("vesta") || nameToLowerCase("morrigan") || nameToLowerCase("statius")) {
            cost = 1_500_000;
        }
        if (nameToLowerCase("prayer scroll") && !nameToLowerCase("torn prayer scroll")
        || nameToLowerCase("avernic")) {
            cost = 1_250_000;
        }
        if (nameToLowerCase("twisted bow") || nameToLowerCase("elysian spirit shield")
            || nameToLowerCase("voidwaker") || nameToLowerCase("scythe of vitur")) {
            cost = 5_000_000;
        }
        if (nameToLowerCase("zaryte crossbow") || nameToLowerCase("ancient godsword")) {
            cost = 1_800_000;
        }
        if (nameToLowerCase("zenyte") || nameToLowerCase("torture")
            || nameToLowerCase("anguish") || nameToLowerCase("tormented bracelet")
            || nameToLowerCase("crystal helm") || nameToLowerCase("crystal body")
            || nameToLowerCase("crystal legs") || nameToLowerCase("dragon hunter lance")) {
            cost = 1_000_000;
        }
        if (nameToLowerCase("pegasian") || nameToLowerCase("primordial")
            || nameToLowerCase("eternal boots") || nameToLowerCase("dragon hunter crossbow")) {
            cost = 980_000;
        }
        if (nameToLowerCase("armadyl") || nameToLowerCase("bandos") && !nameToLowerCase("godsword")) {
            cost = 925_000;
        }
        if (nameToLowerCase("armadyl godsword") || nameToLowerCase("armadyl crossbow")
            || nameToLowerCase("heavy ballista") || nameToLowerCase("ancient sceptre")
            || nameToLowerCase("craws bow") || nameToLowerCase("viggoras")
            || nameToLowerCase("ursine") || nameToLowerCase("webweaver")
            || nameToLowerCase("dinhs bulwark") || nameToLowerCase("thammarons sceptre")) {
            cost = 950_000;
        }
        if (nameToLowerCase("dragonfire ward") || nameToLowerCase("ancient wyvern shield")
            || nameToLowerCase("toxic blowpipe") || nameToLowerCase("toxic staff of the dead")) {
            cost = 925_000;
        }
        if (nameToLowerCase("staff of light") || nameToLowerCase("dragon crossbow")
            || nameToLowerCase("ancient sceptre") || nameToLowerCase("brimstone ring")
            || nameToLowerCase("bandos godsword") || nameToLowerCase("zamorak godsword")
            || nameToLowerCase("saradomin godsword")) {
            cost = 915_000;
        }
        return cost;
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
