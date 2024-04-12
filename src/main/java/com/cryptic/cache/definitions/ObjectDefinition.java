package com.cryptic.cache.definitions;

import com.cryptic.GameConstants;
import com.cryptic.model.World;
import com.cryptic.network.codec.RSBuffer;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bart Pelle on 10/4/2014.
 */
public class ObjectDefinition implements Definition {

    public static Int2ObjectMap<ObjectDefinition> cached = new Int2ObjectLinkedOpenHashMap<>();

    public static ObjectDefinition get(int id) {
        return World.getWorld().definitions().get(ObjectDefinition.class, id);
    }

    public String name = "null";
    public String description;
    public boolean randomAnimStart;
    public int[] modelIds;
    public int[] models;
    public int sizeX = 1;

    public Map<Integer, Object> params = null;
    public int sizeY = 1;
    public int interactType = 2;
    public boolean boolean1 = true; // formerly projectileClipped
    public int int1 = -1;
    public int clipType = -1;
    public boolean nonFlatShading = false;
    public boolean modelClipped = false;
    public int animationId = -1;
    public int int2 = -1;
    public int ambient = 0;
    public int contrast = 0;
    public short[] recolorFrom;
    public short[] recolorTo;
    public short[] retextureFrom;
    public short[] retextureTo;
    public int anInt2286 = -1;
    public boolean isRotated = false;
    public boolean clipped = true;
    public int modelSizeX = 128;
    public int modelHeight = 128;
    public int modelSizeY = 128;
    public int mapsceneId = -1;
    public int offsetX = 0;
    public int offsetHeight = 0;
    public int offsetY = 0;
    public boolean boolean2 = false;
    public boolean isSolid = false;
    public int int3 = -1;
    public int varbit = -1;
    public int anInt2302 = -1;
    public int int8 = 0;
    public boolean rev220SoundData = true;
    public int ambientSoundRetain;
    public int varp = -1;
    public int ambientSoundId = 0;
    public int int7 = 0;
    public int cflag = 0;
    public int[] anIntArray2306;
    public int[] to_objs;
    public String[] actions = new String[5];
    public Map<Integer, Object> clientScriptData;

    public int id;
    private int mapIconId;

    /**
     * Door data, server side, non-cache from Runite team
     */

    public boolean gateType;

    public boolean longGate;

    public int doorOppositeId = -1;

    public boolean doorReversed, doorClosed;

    public int doorOpenSound = -1, doorCloseSound = -1;
    public boolean reversedConstructionDoor;

    public ObjectDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));

        cached.put(id, this);

    }

    void decode(RSBuffer buffer) {
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            processOp(buffer, op);
        }

        if(id == 23311) {
            name = GameConstants.SERVER_NAME+ " Teleporter";
            actions = new String[] {"Teleport", null, null, null, null};
        }

        if (id == 7811) {
            name = "Supplies";
            actions[1] = "Vote-Rewards";
            actions[1] = null;
        }

        if (id == 29149) {
            actions = new String[]{"Pray-at", "Ancients", "Lunar", "Modern", null};
        }

        if (id == 29150) {
            actions = new String[]{ "Switch-Moderns", "Switch-Ancients", "Switch-Lunars", null, null};
        }

        if (id == 13641) {
            name = "Teleports";
            actions = new String[] { "View", "Previous", null, null, null};
        }

        if(id == 33020) {
            name = "Forging table";
            actions = new String[] {"Forge", null, null, null, null};
        }

        if (id == 29165) {
            name = "Pile Of Coins";
            actions[0] = null;
            actions[1] = null;
            actions[2] = null;
            actions[3] = null;
            actions[4] = null;
        }

        if(id == 8878) {
            name = "Item dispenser";
            actions = new String[] {"Dispense", "Exchange coins", null, null, null};
        }

        if(id == 637) {
            name = "Item cart";
            actions = new String[] {"Check cart", "Item list", "Clear cart", null, null};
        }

        if (id == 13291) {
            actions = new String[] {"Open", null, null, null, null};
        }

        if (id == 27269) {
            name = "Wilderness key chest";
        }

        if (id == 172) {
            name = "Crystal key chest";
        }

        if (id == 173) {
            name = "Open crystal key chest";
        }

        if (id == 23709) {
            actions[0] = "Use";
        }

        if (id == 2156) {
            name = "World Boss Portal";
        }

        if (id == 27780) {
            name = "Scoreboard";
        }

        if (id == 27097) {
            name = "Boss Portal";
            actions[0] = "Teleport to";
            actions[1] = null;
            actions[2] = null;
            actions[3] = null;
        }

        if (id == 14986) {
            name = "Key Chest";
        }

        if (id == 13291) {
            name = "Enchanted chest";
        }

        if(id == 11508 || id == 11509) {
            //curtain
            interactType = 0;
        }

        cached.put(id, this);
    }

    private void processOp(RSBuffer is, int opcode)
    {
        if (opcode == 1)
        {
            int length = is.readUByte();
            if (length > 0)
            {
                int[] objectTypes = new int[length];
                int[] objectModels = new int[length];

                for (int index = 0; index < length; ++index)
                {
                    objectModels[index] = is.readUShort();
                    objectTypes[index] = is.readUByte();
                }

                modelIds = (objectTypes);
                models = (objectModels);
            }
        }
        else if (opcode == 2)
        {
            name = (is.readString());
        }
        else if (opcode == 5)
        {
            int length = is.readUByte();
            if (length > 0)
            {
                modelIds = (null);
                int[] objectModels = new int[length];

                for (int index = 0; index < length; ++index)
                {
                    objectModels[index] = is.readUShort();
                }

                models = (objectModels);
            }
        }
        else if (opcode == 14)
        {
            sizeX = (is.readUByte());
        }
        else if (opcode == 15)
        {
            sizeY = (is.readUByte());
        }
        else if (opcode == 17)
        {
            interactType = (0);
            boolean1 = (false);
        }
        else if (opcode == 18)
        {
            boolean1 = (false);
        }
        else if (opcode == 19)
        {
            int1 = (is.readUByte());
        }
        else if (opcode == 21)
        {
            clipType = (0);
        }
        else if (opcode == 22)
        {
            nonFlatShading = (true);
        }
        else if (opcode == 23)
        {
            modelClipped = (true);
        }
        else if (opcode == 24)
        {
            animationId = (is.readUShort());
            if (animationId == 0xFFFF)
            {
                animationId = (-1);
            }
        }
        else if (opcode == 27)
        {
            interactType = (1);
        }
        else if (opcode == 28)
        {
            int2 = (is.readUByte());
        }
        else if (opcode == 29)
        {
            ambient = (is.readByte());
        }
        else if (opcode == 39)
        {
            contrast = (is.readByte() * 25);
        }
        else if (opcode >= 30 && opcode < 35)
        {
            actions[opcode - 30] = is.readString();
            if (actions[opcode - 30].equalsIgnoreCase("Hidden"))
            {
                actions[opcode - 30] = null;
            }
        }
        else if (opcode == 40)
        {
            int length = is.readUByte();
            short[] recolorToFind = new short[length];
            short[] recolorToReplace = new short[length];

            for (int index = 0; index < length; ++index)
            {
                recolorToFind[index] = is.readShort();
                recolorToReplace[index] = is.readShort();
            }

            recolorFrom = (recolorToFind);
            recolorTo = (recolorToReplace);
        }
        else if (opcode == 41)
        {
            int length = is.readUByte();
            short[] retextureToFind = new short[length];
            short[] textureToReplace = new short[length];

            for (int index = 0; index < length; ++index)
            {
                retextureToFind[index] = is.readShort();
                textureToReplace[index] = is.readShort();
            }

            retextureFrom = (retextureToFind);
            retextureTo = (textureToReplace);
        }
        else if (opcode == 61)
        {
            is.readUShort();
        }
        else if (opcode == 62)
        {
            isRotated = (true);
        }
        else if (opcode == 64)
        {
            clipped = (false);
        }
        else if (opcode == 65)
        {
            modelSizeX = (is.readUShort());
        }
        else if (opcode == 66)
        {
            modelHeight = (is.readUShort());
        }
        else if (opcode == 67)
        {
            modelSizeY = (is.readUShort());
        }
        else if (opcode == 68)
        {
            mapsceneId = (is.readUShort());
        }
        else if (opcode == 69)
        {
            cflag = (is.readByte());
        }
        else if (opcode == 70)
        {
            offsetX = (is.readUShort());
        }
        else if (opcode == 71)
        {
            offsetHeight = (is.readUShort());
        }
        else if (opcode == 72)
        {
            offsetY = (is.readUShort());
        }
        else if (opcode == 73)
        {
            boolean2 = (true);
        }
        else if (opcode == 74)
        {
            isSolid = (true);
        }
        else if (opcode == 75)
        {
            int3 = (is.readUByte());
        }
        else if (opcode == 77)
        {
            int varpID = is.readUShort();
            if (varpID == 0xFFFF)
            {
                varpID = -1;
            }
            varbit = (varpID);

            int configId = is.readUShort();
            if (configId == 0xFFFF)
            {
                configId = -1;
            }
            varp = (configId);

            int length = is.readUByte();
            int[] configChangeDest = new int[length + 2];

            for (int index = 0; index <= length; ++index)
            {
                configChangeDest[index] = is.readUShort();
                if (0xFFFF == configChangeDest[index])
                {
                    configChangeDest[index] = -1;
                }
            }

            configChangeDest[length + 1] = -1;

            to_objs = (configChangeDest);
        }
        else if (opcode == 78)
        {
            is.readUShort();
            is.readUByte();
            is.readUByte();
        }
        else if (opcode == 79)
        {
            ambientSoundId = (is.readUShort());
            int7 = (is.readUShort());
            int8 = (is.readUByte());
            if (rev220SoundData)
            {
                this.ambientSoundRetain = is.readUByte();
            }
            int length = is.readUByte();
            int[] anIntArray2084 = new int[length];

            for (int index = 0; index < length; ++index)
            {
                anIntArray2084[index] = is.readUShort();
            }

            anIntArray2306 = (anIntArray2084);
        }
        else if (opcode == 81)
        {
            clipType = (is.readUByte() * 256);
        }
        else if (opcode == 82)
        {
            mapIconId = (is.readUShort());
        }
        else if (opcode == 89)
        {
            randomAnimStart = false;
        }
        else if (opcode == 92)
        {
            int varpID = is.readUShort();
            if (varpID == 0xFFFF)
            {
                varpID = -1;
            }
            varbit = (varpID);

            int configId = is.readUShort();
            if (configId == 0xFFFF)
            {
                configId = -1;
            }
            varp = (configId);


            int var = is.readUShort();
            if (var == 0xFFFF)
            {
                var = -1;
            }

            int length = is.readUByte();
            int[] configChangeDest = new int[length + 2];

            for (int index = 0; index <= length; ++index)
            {
                configChangeDest[index] = is.readUShort();
                if (0xFFFF == configChangeDest[index])
                {
                    configChangeDest[index] = -1;
                }
            }

            configChangeDest[length + 1] = var;

            to_objs = (configChangeDest);
        }
        else if (opcode == 249)
        {
            int length = is.readUByte();

            Map<Integer, Object> params = new HashMap<>(length);
            for (int i = 0; i < length; i++)
            {
                boolean isString = is.readUByte() == 1;
                int key = is.read24BitInt();
                Object value;

                if (isString)
                {
                    value = is.readString();
                }

                else
                {
                    value = is.readInt();
                }

                params.put(key, value);
            }
        }

        postDecode();

        if (isSolid) {
            interactType = 0;
            boolean1 = false;
        }
    }

    void postDecode() {
        if (this.int1 == -1) {
            this.int1 = 0;
            if (this.models != null && (this.models == null || this.models[0] == 10)) {
                this.int1 = 1;
            }

            for (int var1 = 0; var1 < 5; ++var1) {
                if (this.actions[var1] != null) {
                    this.int1 = 1;
                }
            }
        }

        if (this.int3 == -1) {
            this.int3 = this.interactType != 0 ? 1 : 0;
        }

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

    public boolean hasOption(String... searchOptions) {
        return getOption(searchOptions) != -1;
    }

    public int getOption(String... searchOptions) {
        if (actions != null) {
            for (String s : searchOptions) {
                for (int i = 0; i < actions.length; i++) {
                    String option = actions[i];
                    if (s.equalsIgnoreCase(option))
                        return i + 1;
                }
            }
        }
        return -1;
    }

    public int optionsCount() {
        if (actions != null) {
            var opts = 0;
            for (String option : actions) {
                if (option != null && !option.equals("null"))
                    opts++;
            }
            return opts;
        }
        return 0;
    }
    public boolean isClippedDecoration() {
        return int1 != 0 || interactType == 1 || boolean2;
    }

    public String toStringBig() {
        return "ObjectDefinition{" +
            "name='" + name + '\'' +
            ", modeltypes=" + Arrays.toString(modelIds) +
            ", models=" + Arrays.toString(models) +
            ", sizeX=" + sizeX +
            ", sizeY=" + sizeY +
            ", clipType=" + interactType +
            ", tall=" + boolean1 +
            ", anInt2292=" + int1 +
            ", anInt2296=" + clipType +
            ", aBool2279=" + nonFlatShading +
            ", aBool2280=" + modelClipped +
            ", anInt2281=" + animationId +
            ", anInt2291=" + int2 +
            ", anInt2283=" + ambient +
            ", anInt2285=" + contrast +
            ", recol_s=" + Arrays.toString(recolorFrom) +
            ", recol_d=" + Arrays.toString(recolorTo) +
            ", retex_s=" + Arrays.toString(retextureFrom) +
            ", retex_d=" + Arrays.toString(retextureTo) +
            ", anInt2286=" + anInt2286 +
            ", vflip=" + isRotated +
            ", aBool2284=" + clipped +
            ", op65Render0x1=" + modelSizeX +
            ", op66Render0x2=" + modelHeight +
            ", op67Render0x4=" + modelSizeY +
            ", anInt2287=" + mapsceneId +
            ", anInt2307=" + offsetX +
            ", anInt2294=" + offsetHeight +
            ", anInt2295=" + offsetY +
            ", aBool2264=" + boolean2 +
            ", unclipped=" + isSolid +
            ", anInt2298=" + int3 +
            ", varbit=" + varbit +
            ", anInt2302=" + anInt2302 +
            ", anInt2303=" + int8 +
            ", varp=" + varp +
            ", anInt2304=" + ambientSoundId +
            ", anInt2290=" + int7 +
            ", cflag=" + cflag +
            ", anIntArray2306=" + Arrays.toString(anIntArray2306) +
            ", to_objs=" + Arrays.toString(to_objs) +
            ", options=" + Arrays.toString(actions) +
            ", clientScriptData=" + clientScriptData +
            ", id=" + id +
            ", anInt2167=" + mapIconId +
            '}';
    }
}
