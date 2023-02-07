package com.aelous.cache.definitions;

import com.aelous.GameConstants;
import com.aelous.model.entity.npc.pets.PetDefinitions;
import com.aelous.network.codec.RSBuffer;
import io.netty.buffer.Unpooled;
import com.aelous.cache.DataStore;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static com.aelous.utility.CustomNpcIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * Created by Bart Pelle on 10/4/2014.
 */
public class NpcDefinition implements Definition {

    public int getOption(String... searchOptions) {
        if (options != null) {
            for (String s : searchOptions) {
                for (int i = 0; i < options.length; i++) {
                    String option = options[i];
                    if (s.equalsIgnoreCase(option))
                        return i + 1;
                }
            }
        }
        return -1;
    }

    public int[] models;
    public String name = null;
    public int size = 1;
    public int idleAnimation = -1;
    public int walkAnimation = -1;
    public boolean isFollower;
    public int render3 = -1;
    public int render4 = -1;
    public int render5 = -1;
    public int render6 = -1;
    public int render7 = -1;
    public int category;
    public boolean isClickable;

    public Map<Integer, Object> params;
    short[] recol_s;
    short[] recol_d;
    short[] retex_s;
    short[] retex_d;
    int[] anIntArray2224;
    public boolean mapdot = true;
    public int combatlevel = -1;
    int width = -1;
    int height = -1;
    public boolean render = false;
    int anInt2242 = 0;
    int contrast = 0;
    public int headIcon = -1;
    public int turnValue = -1;
    int varbit = -1;
    public boolean rightclick = true;
    int varp = -1;
    public boolean aBool2227 = true;
    public int[] altForms;
    public boolean isPet = false;
    public int anInt2252 = -1;
    public String[] options = new String[5];
    public Map<Integer, Object> clientScriptData;
    public int id;

    public static void main(String[] args) throws Exception {
        DataStore ds = new DataStore("./data/cache/");
        System.out.println(discoverNPCAnims(ds, 3727, false));
    }

    private static List<Integer> discoverNPCAnims(DataStore store, int id, boolean debug) {
        NpcDefinition npcdef = new NpcDefinition(id, store.getIndex(2).getContainer(9).getFileData(id, true, true));
        int animId = -1;
        if (debug) System.out.println("Beginning discovery for " + npcdef.name + ".");
        if (debug) System.out.print("Using stand animation to grab kinematic set... ");
        if (debug) System.out.println(animId);
        AnimationDefinition stand = new AnimationDefinition(animId, store.getIndex(2).getContainer(12).getFileData(animId, true, true));
        if (debug) System.out.print("Finding skin set... ");
        int set = stand.skeletonSets[0] >> 16;
        if (debug) System.out.println(set);
        if (debug) System.out.println("Using that set to find related animations...");
        int skin = AnimationSkeletonSet.get(store, set).loadedSkins.keySet().iterator().next();

        if (skin == 0) {
            return new ArrayList<>(0);
        }

        List<Integer> work = new LinkedList<>();
        for (int i = 0; i < 30000; i++) {
            AnimationDefinition a = new AnimationDefinition(i, store.getIndex(2).getContainer(12).getFileData(i, true, true));
            int skel = a.skeletonSets[0] >> 16;
            try {
                AnimationSkeletonSet sett = AnimationSkeletonSet.get(store, skel);
                if (sett.loadedSkins.keySet().contains(skin)) {
                    work.add(i);
                    //System.out.println("Animation #" + i + " uses player kinematic set.");
                }
                //System.out.println(skel);
            } catch (Exception e) {

            }
        }

        if (debug) System.out.println("Found a total of " + work.size() + " animations: " + work);
        return work;
    }

    private static final int[] GWD_ROOM_NPCIDS = new int[]{
        3165, 3163, 3164, 3162,
        2215, 2216, 2217, 2218,
        3129, 3130, 3132, 3131,
        2206, 2207, 2208, 2205
    };

    public boolean gwdRoomNpc;
    public boolean inferno;
    public boolean roomBoss;

    public NpcDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
        custom();

        gwdRoomNpc = ArrayUtils.contains(GWD_ROOM_NPCIDS, id);
        inferno = id >= 7677 && id <= 7710;
        roomBoss = name != null && ((id >= 2042 && id <= 2044 || inferno) || gwdRoomNpc);
    }

    void decode(RSBuffer buffer) {
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            decode(buffer, op);
        }
    }

    void custom() {

        Arrays.stream(PetDefinitions.values()).filter(p -> p.npc == id).forEach(p -> {
            isPet = true;
            size = 1;
        });

        if (id == 2882) {
            /**
             * Horkiv
             */
            options[2] = null;
        }

        if (id == 2895) {
            name = "Consumables store";
            options[0] = "Trade";
        }

        if (id == 7817) {
            name = "Lava beast";
            combatlevel = 435;//exact combat here
            walkAnimation = 7675;
            options = new String[]{null, "Attack", null, null, null, null};
        }
        if (id == 15031) {
            name = "Ice demon";
            combatlevel = 230;//exact combat here
            options = new String[]{null, "Attack", null, null, null, null};
        }
        if (id == 7632) {
            name = "Men in black";
            combatlevel = 80;
            options = new String[]{null, "Attack", null, null, null, null};
            size = 1;
        }
        if (id == NAGINI) {
            name = "Nagini";
        }
        if (id == LORD_VOLDEMORT) {
            name = "Lord voldemort";
            combatlevel = 1433;
        }
        if (id == SKELETON_HELLHOUND_PET) {
            name = "Skeleton hellhound pet";
        }
        if (id == 15021) {
            name = "Grim";
            size = 3;
            combatlevel = 1322;
        }
        if (id == BLOOD_REAPER) {
            name = "Blood Reaper";
        }

        if (id == ZRIAWK) {
            name = "Zriawk";
        }

        if (id == 15030) {
            name = "Male centaur";
            size = 2;
        }
        if (id == 15032) {
            name = "Female centaur";
            size = 2;
        }
        if (id == 15028) {
            name = "Dementor";
            size = 1;
        }
        if (id == 15020) {
            name = "Aragog";
            size = 4;
        }
        if (id == 15026) {
            name = "Fluffy";
            size = 5;
        }
        if (id == 15034) {
            name = "Hungarian horntail";
            size = 4;
        }
        if (id == 15050) {
            name = "Fenrir greyback";
            size = 1;
        }
        if (id == 15019 || id == 15016) {
            name = "Brutal lava dragon";
            size = 4;
        }
        if (id == 11113) {
            name = "El Fuego";
            size = 4;
        }
        if (id == 9340) {
            name = "Zriawk";
        }
        if (id == 15040) {
            name = "Centaur male";
        }
        if (id == 15042) {
            name = "Centaur female";
        }
        if (id == 9338) {
            name = "Fluffy Jr";
        }
        if (id == 9339) {
            name = "Fenrir greyback Jr";
        }
        if (id == 15044) {
            name = "Dementor";
        }
        if (id == 15000) {
            name = "Founder imp";
        }
        if (id == 15001) {
            name = "Corrupted nechryarch";
            size = 2;
        }
        if (id == 15002) {
            name = "Corrupted nechryarch";
        }
        if (id == 15005) {
            name = "Mini necromancer";
        }
        if (id == 15008) {
            name = "Jaltok-jad";
        }
        if (id == 15017) {
            name = "Baby lava dragon";
        }
        if (id == 10981) {
            name = "Fawkes";
        }
        if (id == 7315) {
            name = "Blood money";
        }
        if (id == 336) {
            name = "Elysian";
        }
        if (id == 16008) {
            name = "Kerberos";
            combatlevel = 318;
            size = 5;
        }
        if (id == 16009) {
            name = "Skorpios";
            combatlevel = 225;
            size = 5;
        }
        if (id == 16010) {
            name = "Arachne";
            combatlevel = 464;
            size = 4;
        }
        if (id == 16011) {
            name = "Artio";
            combatlevel = 470;
            size = 5;
        }
        if (id == 16000) {
            name = "Ancient revenant dark beast";
            combatlevel = 120;
            size = 3;
        }
        if (id == 16001) {
            name = "Ancient revenant ork";
            combatlevel = 105;
            size = 3;
        }
        if (id == 16002) {
            name = "Ancient revenant cyclops";
            combatlevel = 82;
            size = 3;
        }
        if (id == 16003) {
            name = "Ancient revenant dragon";
            combatlevel = 135;
            size = 5;
        }
        if (id == 16004) {
            name = "Ancient revenant knight";
            combatlevel = 126;
            size = 1;
        }
        if (id == 16005) {
            name = "Ancient barrelchest";
            combatlevel = 190;
            size = 3;
        }
        if (id == 16006) {
            name = "Ancient king black dragon";
            combatlevel = 276;
            size = 5;
        }
        if (id == 16007) {
            name = "Ancient chaos elemental";
            combatlevel = 305;
            size = 3;
        }
        if (id == 9330) {
            name = "Ancient king black dragon";
            size = 4;
        }
        if (id == 9331) {
            name = "Ancient chaos elemental";
            size = 3;
        }
        if (id == 9332) {
            name = "Ancient barrelchest";
            size = 5;
        }
        if (id == 7370) {
            name = "Blood firebird";
            size = 1;
        }
        if (id == 13000 || id == 13001) {
            name = "Pure bot";
            size = 1;
            combatlevel = 80;
        }
        if (id == 13002 || id == 13003) {
            name = "F2p bot";
            size = 1;
            combatlevel = 68;
        }

        if (id == 13004) {
            name = "Maxed bot";
            size = 1;
            combatlevel = 126;
        }

        if (id == 13005) {
            name = "Maxed bot";
            size = 1;
            combatlevel = 126;
        }

        if (id == 4159) {
            name = "Teleport Master";
            options[0] = "View";
            options[2] = "Previous-Teleport";
        }

        if (id == 13006) {
            name = "Archer bot";
            size = 1;
            combatlevel = 90;
        }

        if (id == 13008 || id == 13009) {
            name = "Pure Archer bot";
            size = 1;
            combatlevel = 80;
        }
        if (id == 15035) {
            name = "Kerberos";
        }
        if (id == 15036) {
            name = "Skorpios";
        }
        if (id == 15037) {
            name = "Arachne";
        }
        if (id == 15038) {
            name = "Artio";
        }
        if (id == 9413) {
            name = "Referral Manager";
        }
        if (id == THORODIN_5526) {
            name = "Boss slayer master";
            options = new String[]{"Talk-to", null, "Slayer-Equipment", "Slayer-Rewards", null};
        } else if (id == 3358) {
            name = "Ket'ian";
            combatlevel = 420;
            width *= 2;
            height *= 2;
            size = 2;
        } else if (id == 3329) {
            name = "Sapphires Champion";
            combatlevel = 600;
            width *= 2;
            height *= 2;
            size = 2;
        } else if (id == 3142) {
            name = "Aragog";
            options = new String[]{null, "Attack", null, null, null};
            combatlevel = 1123;
            models = new int[]{28294, 28295};
            width = 190;
            height = 190;
            idleAnimation = 5318;
            size = 4;
            walkAnimation = 5317;
        } else if (id == TWIGGY_OKORN) {
            options = new String[]{"Talk-to", null, "Rewards", "Claim-cape", null};
        } else if (id == FANCY_DAN) {
            name = "Vote Manager";
            options[0] = "Talk-to";
            options[2] = "View Store";
            options[3] = "Quick-Vote Open";
            options[4] = "Quick-Vote Claim";

        } else if (id == WISE_OLD_MAN) {
            name = "Credit Manager";
            options[0] = "Talk-to";
            options[2] = "Open-Shop";
            options[3] = "Claim-purchases";
        } else if (id == TRADE_REFEREE) {
            name = "RiskZone Point Shop";
            options[0] = "Talk-to";
            options[2] = "Shop";
            options[3] = null;
            options[4] = null;
        } else if (id == EMERALD_BENEDICT) {
            name = "Tournament Point Shop";
            options[0] = "Talk-to";
            options[2] = "Bank";
            options[3] = "Shop";
            options[4] = null;
        } else if (id == SECURITY_GUARD) {
            name = "Security Advisor";
            options[0] = "Check Pin Settings";
        } else if (id == SIGMUND_THE_MERCHANT) {
            options[0] = "Buy-items";
            options[2] = "Sell-items";
            options[3] = "Sets";
            options[4] = null;
        } else if (id == GRAND_EXCHANGE_CLERK) {
            options[0] = "Exchange";
            options[2] = null;
            options[3] = null;
            options[4] = null;
        } else if (id == 6942) {
            options[0] = "Bank";
            options[2] = null;
            options[3] = null;
            options[4] = null;
        } else if (id == MAKEOVER_MAGE_1307) {
            options[0] = "Change-looks";
            options[2] = "Title-unlocks";
            options[3] = null;
            options[4] = null;
        } else if (id == FRANK) {
            name = "Shop";
            options[0] = "Untradeable";
        } else if (id == CLAUS_THE_CHEF) {
            name = "Shop";
            options[0] = "Consumable";
        } else if (id == RADIGAD_PONFIT) {
            name = "Ranged Shop";
            options = new String[]{"Weapons", null, "Armour", "Ironman", null};
        } else if (id == GUNNJORN) {
            name = "Melee Shop";
            options = new String[]{"Weapons", null, "Armour", "Ironman", null};
        } else if (id == SPICE_SELLER_4579) {
            name = "Shop";
            options[0] = "Misc";
        } else if (id == LISA) {
            name = "Tournament Manager";
            options = new String[]{"Sign-up", null, "Quick-join", "Quick-spectate", null, null, null};
        } else if (id == VANNAKA) {
            name = "Task master";
            options = new String[]{"Talk-to", null, "Progress", null, null};
        } else if (id == 6481) {
            options = new String[]{"Talk-to", null, "Skillcape", null, null, null, null};
        } else if (id == 9413) {
            name = "Referral Manager";
        } else if (id == 306) {
            name = GameConstants.SERVER_NAME + " Guide";
        } else if (id == 3359) {
            combatlevel = 785;
        } else if (id == 3254) {
            options = new String[]{"Talk-to", null, "Trade", null, null};
        } else if (id == 1220) {
            name = "Wampa";
        } else if (id == 6635) {
            name = "Niffler";
        } else if (id == 4927) {
            name = "Fawkes";
        } else if (id == 3343) {
            name = "Prayer & Hitpoints Healer";
            options[0] = "Heal";
        } else if (id == 1221) {
            name = "Zilyana Jr";
        } else if (id == 1222) {
            name = "General Graardor Jr";
        } else if (id == 1223) {
            name = "Kree'arra Jr";
        } else if (id == 1224) {
            name = "K'ril Tsutsaroth Jr";
        } else if (id == 1225) {
            name = "Baby Squirt";
        } else if (id == 1182) {
            name = "Baby Barrelchest";
        } else if (id == 1228) {
            name = "Grim Reaper";
        } else if (id == 1216) {
            name = "Baby Dark Beast";
        } else if (id == 1214) {
            name = "Baby Aragog";
        } else if (id == 1213) {
            name = "Jawa";
        } else if (id == 1217) {
            name = "Dharok the Wretched";
        } else if (id == 6849) {
            name = "Genie";
        } else if (id == 1218) {
            name = "Baby Abyssal Demon";
        } else if (id == 1219) {
            name = "Zombies champion";
            models = new int[]{20949};
            idleAnimation = 5573;
            size = 1;
            walkAnimation = 5582;
            combatlevel = 0;
            mapdot = false;
            width = 63;
            height = 63;
        }

        if (isPet) {
            this.name = this.name + " pet";
        }

        switch (id) {
            case 9425 -> {
                name = "The Nightmare";
                options = new String[]{null, "Attack", null, null, null};
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39182, 41454};
                idleAnimation = 8593;
                render5 = 8593;
                render6 = 8593;
                render7 = 8593;
                size = 5;
                mapdot = true;
                walkAnimation = 8592;
            }
            case 9426 -> {
                name = "The Nightmare";
                options = new String[]{null, "Attack", null, null, null};
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39186, 41454};
                idleAnimation = 8593;
                render5 = 8593;
                render6 = 8593;
                render7 = 8593;
                size = 5;
                mapdot = true;
                walkAnimation = 8592;
            }
            case 9427 -> {
                name = "The Nightmare";
                options = new String[]{null, "Attack", null, null, null};
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39188, 41454};
                idleAnimation = 8593;
                render5 = 8593;
                render6 = 8593;
                render7 = 8593;
                size = 5;
                mapdot = true;
                walkAnimation = 8592;
            }
            case 9428 -> {
                name = "The Nightmare";
                options = new String[]{null, "Attack", null, null, null};
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39196, 41454};
                idleAnimation = 8593;
                render5 = 8593;
                render6 = 8593;
                render7 = 8593;
                size = 5;
                mapdot = true;
                walkAnimation = 8592;
            }
            case 9429 -> {
                name = "The Nightmare";
                options = new String[]{null, "Attack", null, null, null};
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39185, 41454};
                idleAnimation = 8593;
                render5 = 8593;
                render6 = 8593;
                render7 = 8593;
                size = 5;
                mapdot = true;
                walkAnimation = 8592;
            }
            case 9430 -> {
                name = "The Nightmare";
                options = new String[]{null, "Attack", null, null, null};
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39195, 41454};
                idleAnimation = 8593;
                render5 = 8593;
                render6 = 8593;
                render7 = 8593;
                size = 5;
                mapdot = true;
                walkAnimation = 8592;
            }
            case 9431 -> {
                name = "The Nightmare";
                options = new String[]{null, "Attack", null, null, null};
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39208, 41454};
                idleAnimation = 8603;
                render5 = 8603;
                render6 = 8603;
                render7 = 8603;
                size = 5;
                mapdot = true;
                walkAnimation = 8592;
            }
            case 9432 -> {
                name = "The Nightmare";
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39196, 41454};
                idleAnimation = 8613;
                render5 = 8613;
                render6 = 8613;
                render7 = 8613;
                size = 5;
                mapdot = true;
                walkAnimation = 8613;
            }
            case 9433 -> {
                name = "The Nightmare";
                anInt2242 = 15;
                combatlevel = 814;
                contrast = 100;
                models = new int[]{39196, 41454};
                idleAnimation = 8613;
                render5 = 8613;
                render6 = 8613;
                render7 = 8613;
                size = 5;
                mapdot = true;
                walkAnimation = 8613;
            }
        }
    }

    private void decode(RSBuffer stream, int opcode) {
        int length;
        int index;
        if (opcode == 1) {
            length = stream.readUByte();
            models = new int[length];

            for (index = 0; index < length; ++index) {
                models[index] = stream.readUShort();
            }
        } else if (opcode == 2) {
            name = stream.readJagexString();
        } else if (opcode == 12) {
            size = stream.readUByte();
        } else if (opcode == 13) {
            idleAnimation = stream.readUShort();
        } else if (opcode == 14) {
            walkAnimation = stream.readUShort();
        } else if (opcode == 15) {
            render3 = stream.readUShort();
        } else if (opcode == 16) {
            render4 = stream.readUShort();
        } else if (opcode == 17) {
            walkAnimation = stream.readUShort();
            render5 = stream.readUShort();
            render6 = stream.readUShort();
            render7 = stream.readUShort();
        } else if (opcode == 18) {
            category = stream.readUShort();
        } else if (opcode >= 30 && opcode < 35) {
            options[opcode - 30] = stream.readString();
            if (options[opcode - 30].equalsIgnoreCase("Hidden")) {
                options[opcode - 30] = null;
            }
        } else if (opcode == 40) {
            length = stream.readUByte();
            recol_s = new short[length];
            recol_d = new short[length];

            for (index = 0; index < length; ++index) {
                recol_s[index] = (short) stream.readUShort();
                recol_d[index] = (short) stream.readUShort();
            }

        } else if (opcode == 41) {
            length = stream.readUByte();
            retex_s = new short[length];
            retex_d = new short[length];

            for (index = 0; index < length; ++index) {
                retex_s[index] = (short) stream.readUShort();
                retex_d[index] = (short) stream.readUShort();
            }

        } else if (opcode == 60) {
            length = stream.readUByte();
            anIntArray2224 = new int[length];

            for (index = 0; index < length; ++index) {
                anIntArray2224[index] = stream.readUShort();
            }

        } else if (opcode == 93) {
            mapdot = false;
        } else if (opcode == 95) {
            combatlevel = stream.readUShort();
        } else if (opcode == 97) {
            width = stream.readUShort();
        } else if (opcode == 98) {
            height = stream.readUShort();
        } else if (opcode == 99) {
            render = true;
        } else if (opcode == 100) {
            anInt2242 = stream.readByte();
        } else if (opcode == 101) {
            contrast = stream.readByte();
        } else if (opcode == 102) {
            int bitfield = stream.readUByte();
            int len = 0;
            for (int var5 = bitfield; var5 != 0; var5 >>= 1) {
                ++len;
            }

            headIconArchiveIds = new int[len];
            headIconSpriteIndex = new short[len];

            for (int i = 0; i < len; i++) {
                if ((bitfield & 1 << i) == 0) {
                    headIconArchiveIds[i] = -1;
                    headIconSpriteIndex[i] = -1;
                } else {
                    headIconArchiveIds[i] = stream.readBigSmart2();
                    headIconSpriteIndex[i] = (short) stream.readUnsignedShortSmartMinusOne();
                }
            }
        } else if (opcode == 103) {
            turnValue = stream.readUShort();
        } else if (opcode == 106) {
            varbit = stream.readUShort();
            if (varbit == 65535) {
                varbit = -1;
            }
            varp = stream.readUShort();
            if (varp == 65535) {
                varp = -1;
            }
            length = stream.readUByte();
            altForms = new int[length + 2];
            for (index = 0; index <= length; ++index) {
                altForms[index] = stream.readUShort();
                if (altForms[index] == '\uffff') {
                    altForms[index] = -1;
                }
            }
            altForms[length + 1] = -1;
        } else if (opcode == 107) {
            isInteractable = false;
        } else if (opcode == 109) {
            rotationFlag = false;
        } else if (opcode == 111) {
            isPet = true;
        } else if (opcode == 114) {
            runAnimation = stream.readUShort();
        } else if (opcode == 115) {
            runAnimation = stream.readUShort();
            runrender5 = stream.readUShort();
            runrender6 = stream.readUShort();
            runrender7 = stream.readUShort();
        } else if (opcode == 116) {
            crawlAnimation = stream.readUShort();
        } else if (opcode == 117) {
            crawlAnimation = stream.readUShort();
            crawlrender5 = stream.readUShort();
            crawlrender6 = stream.readUShort();
            crawlrender7 = stream.readUShort();
        } else if (opcode == 118) {
            varbit = stream.readUShort();
            if (varbit == 65535) {
                varbit = -1;
            }

            varp = stream.readUShort();
            if (varp == 65535) {
                varp = -1;
            }

            int var = stream.readUShort();
            if (var == 0xFFFF) {
                var = -1;
            }

            length = stream.readUByte();
            altForms = new int[length + 2];

            for (index = 0; index <= length; ++index) {
                altForms[index] = stream.readUShort();
                if (altForms[index] == '\uffff') {
                    altForms[index] = -1;
                }
            }

            altForms[length + 1] = var;
        }   else if (opcode == 249)
        {
            length = stream.readUByte();

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

    public static int method32(int var0) {
        --var0;
        var0 |= var0 >>> 1;
        var0 |= var0 >>> 2;
        var0 |= var0 >>> 4;
        var0 |= var0 >>> 8;
        var0 |= var0 >>> 16;
        return var0 + 1;
    }

    public int[] renderpairs() {
        return new int[]{idleAnimation, render7, walkAnimation, render7, render5, render6, walkAnimation};
    }

    public int[] headIconArchiveIds;
    public short[] headIconSpriteIndex;
    public int runrender5 = -1;
    public int runrender6 = -1;
    public int runrender7 = -1;
    public int crawlAnimation = -1;
    public int crawlrender5 = -1;
    public int runAnimation = -1;
    public int crawlrender6 = -1;
    public int crawlrender7 = -1;

    public boolean isInteractable = true;

    public boolean ignoreOccupiedTiles;
    public boolean flightClipping, swimClipping;

    public boolean rotationFlag = true;
}
