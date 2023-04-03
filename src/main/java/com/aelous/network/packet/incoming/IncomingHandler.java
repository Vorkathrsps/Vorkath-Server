package com.aelous.network.packet.incoming;

import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.impl.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class IncomingHandler {

    public static final PacketListener[] PACKETS = new PacketListener[257];
    public static final String[] PACKET_NAMES = new String[257];
    public static final int[] PACKET_SIZES = new int[256];

    public static final int MAGIC_ON_OBJECT_OPCODE = 35;
    public static final int WIDGET_SLOT_CHANGE = 177;
    public static final int SPECIAL_ATTACK_OPCODE = 184;
    public static final int BUTTON_CLICK_OPCODE = 185;
    public static final int TEXT_CLICK_OPCODE = 186;
    public static final int CHAT_OPCODE_1 = 4;
    public static final int DROP_ITEM_OPCODE = 87;
    public static final int FINALIZED_MAP_REGION_OPCODE = 121;
    public static final int CHANGE_MAP_REGION_OPCODE = 210;
    public static final int CLOSE_INTERFACE_OPCODE = 130;
    public static final int EXAMINE_ITEM_OPCODE = 2;
    public static final int EXAMINE_NPC_OPCODE = 6;
    public static final int CHANGE_APPEARANCE = 101;
    public static final int DIALOGUE_OPCODE = 40;
    public static final int ENTER_AMOUNT_OPCODE = 208, ENTER_SYNTAX_OPCODE = 60;
    public static final int EQUIP_ITEM_OPCODE = 41;
    public static final int PLAYER_INACTIVE_OPCODE = 202;
    public static final int CHAT_SETTINGS_OPCODE = 95;
    public static final int COMMAND_OPCODE = 103;
    public static final int UPDATE_PLANE_OPCODE = 229;
    public static final int COMMAND_MOVEMENT_OPCODE = 98;
    public static final int GAME_MOVEMENT_OPCODE = 164;
    public static final int MINIMAP_MOVEMENT_OPCODE = 248;
    public static final int PICKUP_ITEM_OPCODE = 236;
    public static final int SECOND_GROUNDITEM_OPTION_OPCODE = 235;
    public static final int FIRST_ITEM_CONTAINER_ACTION_OPCODE = 145;
    public static final int SECOND_ITEM_CONTAINER_ACTION_OPCODE = 117;
    public static final int THIRD_ITEM_CONTAINER_ACTION_OPCODE = 43;
    public static final int FOURTH_ITEM_CONTAINER_ACTION_OPCODE = 129;
    public static final int FIFTH_ITEM_CONTAINER_ACTION_OPCODE = 135;
    public static final int ADD_FRIEND_OPCODE = 188;
    public static final int REMOVE_FRIEND_OPCODE = 215;
    public static final int ADD_IGNORE_OPCODE = 133;
    public static final int REMOVE_IGNORE_OPCODE = 74;
    public static final int SEND_PM_OPCODE = 126;
    public static final int ATTACK_PLAYER_OPCODE = 153;
    public static final int PLAYER_OPTION_1_OPCODE = 128;
    public static final int PLAYER_OPTION_2_OPCODE = 37;
    public static final int PLAYER_OPTION_3_OPCODE = 227;
    public static final int SWITCH_ITEM_SLOT_OPCODE = 214;
    public static final int FOLLOW_PLAYER_OPCODE = 73;
    public static final int MAGIC_ON_PLAYER_OPCODE = 249;
    public static final int MAGIC_ON_ITEM_OPCODE = 237;
    public static final int MAGIC_ON_GROUND_ITEM_OPCODE = 181;
    public static final int TRADE_REQUEST_OPCODE = 139;
    public static final int CREATION_MENU_OPCODE = 166;
    public static final int GAMBLE_REQUEST_ACCEPT = 127;
    public static final int CUSTOM_CLIENT_REPORT = 160;
    public static final int DISCONNECTED_BY_PACKET = 161;

    public static final int
        OBJECT_FIRST_CLICK_OPCODE = 132,
        OBJECT_SECOND_CLICK_OPCODE = 252,
        OBJECT_THIRD_CLICK_OPCODE = 70,
        OBJECT_FOURTH_CLICK_OPCODE = 228;

    public static final int
        ATTACK_NPC_OPCODE = 72,
        FIRST_CLICK_OPCODE = 155,
        MAGE_NPC_OPCODE = 131,
        SECOND_CLICK_OPCODE = 17,
        THIRD_CLICK_OPCODE = 21,
        FOURTH_CLICK_OPCODE = 18;

    public static final int FIRST_ITEM_ACTION_OPCODE = 122;
    public static final int SECOND_ITEM_ACTION_OPCODE = 75;
    public static final int THIRD_ITEM_ACTION_OPCODE = 16;

    public static final int
        ITEM_ON_NPC = 57,
        ITEM_ON_ITEM = 53,
        ITEM_ON_OBJECT = 192,
        ITEM_ON_GROUND_ITEM = 25,
        ITEM_ON_PLAYER = 14;

    public static final int WITHDRAW_ALL_BUT_ONE_OPCODE = 140;
    public static final int MODIFIABLE_X_OPCODE = 141;
    public static final int INPUT_FIELD_OPCODE = 142;
    public static final int CONFIRM_OPCODE = 213;

    public static final int SPAWN_TAB_ACTION_OPCODE = 187;

    /*
     * The packets are -1 or -2 for variable length/size, other numbers for fixed sizes.
     * These are the client packets
     */
    static {
        for (int i = 0; i < PACKETS.length; i++) {
            PACKETS[i] = new SilencedPacketListener();
        }
        Arrays.fill(PACKET_NAMES, "unknown");
        //PACKETS[187] = new SpawnTabPacketListener();
        PACKETS[213] = new ConfirmPacketListener();
        PACKETS[184] = new SpecialAttackPacketListener();
        PACKETS[185] = new ButtonClickPacketListener();
        PACKETS[186] = new TextClickPacketListener();
        PACKETS[4] = new ChatMessagePacketListener();
        PACKETS[177] = new WidgetSlot();
        PACKETS[87] = new DropItemPacketListener();
        PACKETS[121] = new FinalizedMapRegionChangePacketListener();
        PACKETS[210] = new RegionChangePacketListener();
        PACKETS[130] = new CloseInterfacePacketListener();
        PACKETS[2] = new ExamineItemPacketListener();
        PACKETS[6] = new ExamineNpcPacketListener();
        PACKETS[101] = new AppearanceChangePacketListener();
        PACKETS[40] = new DialoguePacketListener();
        PACKETS[41] = new EquipPacketListener();
        PACKETS[202] = new PlayerInactivePacketListener();
        PACKETS[95] = new ChatSettingsPacketListener();
        PACKETS[103] = new CommandPacketListener();
        // PACKETS[229] = new HeightCheckPacketListener();
        PACKETS[98] = new MovementPacketListener();
        PACKETS[164] = new MovementPacketListener();
        PACKETS[248] = new MovementPacketListener();
        PACKETS[236] = new PickupItemPacketListener();
        PACKETS[235] = new SecondGroundItemOptionPacketListener();
        PACKETS[214] = new SwitchItemSlotPacketListener();
        PACKETS[73] = new FollowPlayerPacketListener();
        PACKETS[249] = new MagicOnPlayerPacketListener();
        //PACKETS[35] = new MagicOnObjectPacketListener();
        PACKETS[237] = new MagicOnItemPacketListener();
        PACKETS[181] = new MagicOnItemPacketListener();

        PACKETS[145] = new FirstItemContainerActionPacketListener();
        PACKETS[117] = new SecondItemContainerActionPacketListener();
        PACKETS[43] = new ThirdItemContainerActionPacketListener();
        PACKETS[129] = new FourthItemContainerActionPacketListener();
        PACKETS[135] = new FifthItemContainerActionPacketListener();
        PACKETS[140] = new WithdrawAllButOnePacketListener();
        PACKETS[141] = new ModifiableXPacketListener();

        PACKETS[153] = new AttackPlayerPacketListener();
        PACKETS[128] = new PlayerOptionOnePacketListener();
        //PACKETS[37] = new PlayerOptionTwoPacketListener();
        //PACKETS[227] = new PlayerOptionThreePacketListener();


        ObjectInteractionHandler objectHandler = new ObjectInteractionHandler();

        PACKETS[132] = objectHandler;
        PACKETS[252] = objectHandler;
        PACKETS[70] = objectHandler;
        PACKETS[228] = objectHandler;


        PACKETS[131] = new MagicOnNpcPacketListener();

        NPCInteractionHandler npcInteraction = new NPCInteractionHandler();

        PACKETS[155] = npcInteraction;
        PACKETS[17] = npcInteraction;
        PACKETS[21] = npcInteraction;
        PACKETS[18] = npcInteraction;
        PACKETS[72] = npcInteraction;

        PACKETS[122] = new ItemActionOnePacketListener();
        PACKETS[75] = new ItemActionTwoPacketListener();
        PACKETS[16] = new ItemActionThreePacketListener();
        PACKETS[3] = new KeyPressedListener();
        PACKETS[57] = new ItemOnNpcPacketListener();
        PACKETS[53] = new ItemOnItemPacketListener();
        PACKETS[192] = new ItemOnObjectPacketListener();
        PACKETS[25] = new ItemOnGroundItemPacketListener();
        PACKETS[14] = new ItemOnPlayerPacketListener();

        PACKETS[188] = new PlayerRelationPacketListener();
        PACKETS[215] = new PlayerRelationPacketListener();
        PACKETS[133] = new PlayerRelationPacketListener();
        PACKETS[74] = new PlayerRelationPacketListener();
        PACKETS[126] = new PlayerRelationPacketListener();

        PACKETS[208] = new InputListener();
        PACKETS[60] = new InputListener();

        PACKETS[139] = new TradeRequestPacketListener();

        PACKETS[142] = new InputFieldPacketListener();

        PACKETS[127] = new GambleRequestAccept();
        PACKETS[125] = new PacketListener() {
            @Override
            public void handleMessage(Player player, Packet packet) throws Exception {
                System.out.println("hello "+packet.readUnsignedShort());
            }
        };

        PACKETS[160] = new PacketListener() {
            private final Logger logger = LogManager.getLogger(PacketListener.class);
            @Override
            public void handleMessage(Player player, Packet packet) {
                final String text = packet.readString(); // this was just feedback reporting i added to client er doesnt really mean anything
                logger.trace("player {}. Report is: '{}' ", player, text); // cant see issue at all guess we just print if its replicatable 100% of the time, is it? yeah pretty
                //pretty sure everytime we login you can see the parse error
            }
        };

        PACKETS[161] = new PacketListener() {
            private final Logger logger = LogManager.getLogger(PacketListener.class);
            @Override
            public void handleMessage(Player player, Packet packet) {
                final boolean disconnected = packet.readByte() == 1;
                //logger.info("player {} disconnected by packet: {}", player, disconnected);
            }
        };

        PACKET_NAMES[3] = "KeystrokeListner";
       // PACKET_NAMES[187] = "SPAWN_TAB_ACTION_OPCODE";
        PACKET_NAMES[35] = "MAGIC_ON_OBJECT";
        PACKET_NAMES[127] = "GAMBLE_REQUEST_ACCEPT";
        PACKET_NAMES[177] = "WIDGET_SLOT_CHANGE";
        PACKET_NAMES[184] = "SPECIAL_ATTACK_OPCODE";
        PACKET_NAMES[185] = "BUTTON_CLICK_OPCODE";
        PACKET_NAMES[186] = "TEXT_CLICK_OPCODE";
        PACKET_NAMES[4] = "CHAT_OPCODE_1";
        PACKET_NAMES[87] = "DROP_ITEM_OPCODE";
        PACKET_NAMES[121] = "FINALIZED_MAP_REGION_OPCODE";
        PACKET_NAMES[210] = "CHANGE_MAP_REGION_OPCODE";
        PACKET_NAMES[130] = "CLOSE_INTERFACE_OPCODE";
        PACKET_NAMES[2] = "EXAMINE_ITEM_OPCODE";
        PACKET_NAMES[6] = "EXAMINE_NPC_OPCODE";
        PACKET_NAMES[101] = "CHANGE_APPEARANCE";
        PACKET_NAMES[40] = "DIALOGUE_OPCODE";
        PACKET_NAMES[208]  = "ENTER_AMOUNT_OPCODE";
        PACKET_NAMES[60] = "ENTER_SYNTAX_OPCODE";
        PACKET_NAMES[41] = "EQUIP_ITEM_OPCODE";
        PACKET_NAMES[202] = "PLAYER_INACTIVE_OPCODE";
        PACKET_NAMES[95] = "CHAT_SETTINGS_OPCODE";
        PACKET_NAMES[103] = "COMMAND_OPCODE";
        PACKET_NAMES[229] = "UPDATE_PLANE_OPCODE";
        PACKET_NAMES[98] = "COMMAND_MOVEMENT_OPCODE";
        PACKET_NAMES[164] = "GAME_MOVEMENT_OPCODE";
        PACKET_NAMES[248] = "MINIMAP_MOVEMENT_OPCODE";
        PACKET_NAMES[236] = "PICKUP_ITEM_OPCODE";
        PACKET_NAMES[235] = "SECOND_GROUNDITEM_OPTION_OPCODE";
        PACKET_NAMES[145] = "FIRST_ITEM_CONTAINER_ACTION_OPCODE";
        PACKET_NAMES[117] = "SECOND_ITEM_CONTAINER_ACTION_OPCODE";
        PACKET_NAMES[43] = "THIRD_ITEM_CONTAINER_ACTION_OPCODE";
        PACKET_NAMES[129] = "FOURTH_ITEM_CONTAINER_ACTION_OPCODE";
        PACKET_NAMES[135] = "FIFTH_ITEM_CONTAINER_ACTION_OPCODE";
        PACKET_NAMES[138] = "SIXTH_ITEM_CONTAINER_ACTION_OPCODE";
        PACKET_NAMES[188] = "ADD_FRIEND_OPCODE";
        PACKET_NAMES[215] = "REMOVE_FRIEND_OPCODE";
        PACKET_NAMES[133] = "ADD_IGNORE_OPCODE";
        PACKET_NAMES[74] = "REMOVE_IGNORE_OPCODE";
        PACKET_NAMES[126] = "SEND_PM_OPCODE";
        PACKET_NAMES[153] = "ATTACK_PLAYER_OPCODE";
        PACKET_NAMES[128] = "PLAYER_OPTION_1_OPCODE";
        PACKET_NAMES[37] = "PLAYER_OPTION_2_OPCODE";
        PACKET_NAMES[227] = "PLAYER_OPTION_3_OPCODE";
        PACKET_NAMES[214] = "SWITCH_ITEM_SLOT_OPCODE";
        PACKET_NAMES[73] = "FOLLOW_PLAYER_OPCODE";
        PACKET_NAMES[249] = "MAGIC_ON_PLAYER_OPCODE";
        PACKET_NAMES[237] = "MAGIC_ON_ITEM_OPCODE";
        PACKET_NAMES[181] = "MAGIC_ON_GROUND_ITEM_OPCODE";
        PACKET_NAMES[139] = "TRADE_REQUEST_OPCODE";
        PACKET_NAMES[166] = "CREATION_MENU_OPCODE";
        PACKET_NAMES[OBJECT_FIRST_CLICK_OPCODE] = "OBJECT_FIRST_CLICK_OPCODE";
        PACKET_NAMES[252] = "OBJECT_SECOND_CLICK_OPCODE";
        PACKET_NAMES[70] = "OBJECT_THIRD_CLICK_OPCODE";
        PACKET_NAMES[72] = "ATTACK_NPC_OPCODE";
        PACKET_NAMES[155] = "FIRST_CLICK_OPCODE";
        PACKET_NAMES[131] = "MAGE_NPC_OPCODE";
        PACKET_NAMES[17] = "SECOND_CLICK_OPCODE";
        PACKET_NAMES[21] = "THIRD_CLICK_OPCODE";
        PACKET_NAMES[18] = "FOURTH_CLICK_OPCODE";
        PACKET_NAMES[122] = "FIRST_ITEM_ACTION_OPCODE";
        PACKET_NAMES[75] = "SECOND_ITEM_ACTION_OPCODE";
        PACKET_NAMES[16] = "THIRD_ITEM_ACTION_OPCODE";
        PACKET_NAMES[57] = "ITEM_ON_NPC";
        PACKET_NAMES[53] = "ITEM_ON_ITEM";
        PACKET_NAMES[192] = "ITEM_ON_OBJECT";
        PACKET_NAMES[25] = "ITEM_ON_GROUND_ITEM";
        PACKET_NAMES[14] = "ITEM_ON_PLAYER";
        PACKET_NAMES[140] = "WITHDRAW_ALL_BUT_ONE_OPCODE";
        PACKET_NAMES[141] = "MODIFIABLE_X_OPCODE";
        PACKET_NAMES[142] = "INPUT_FIELD_OPCODE";
        PACKET_NAMES[213] = "CONFIRM_OPCODE";
        PACKET_NAMES[160] = "CUSTOM_LAG_REPORT";
        PACKET_NAMES[161] = "DISCONENCTED_BY_PACKET";

        PACKET_SIZES[127] = 3;
        PACKET_SIZES[0] = 0;
        PACKET_SIZES[1] = -3;
        PACKET_SIZES[2] = 6;
        PACKET_SIZES[3] = 12;
        PACKET_SIZES[4] = -1;
        PACKET_SIZES[5] = -3;
        PACKET_SIZES[6] = 2;
        PACKET_SIZES[7] = -3;
        PACKET_SIZES[8] = 2;
        PACKET_SIZES[9] = -3;
        PACKET_SIZES[10] = -3;
        PACKET_SIZES[11] = 13;
        PACKET_SIZES[12] = -3;
        PACKET_SIZES[13] = -3;
        PACKET_SIZES[14] = 8;
        PACKET_SIZES[15] = -3;
        PACKET_SIZES[16] = 6;
        PACKET_SIZES[17] = 2;
        PACKET_SIZES[18] = 2;
        PACKET_SIZES[19] = -3;
        PACKET_SIZES[20] = -3;
        PACKET_SIZES[21] = 2;
        PACKET_SIZES[22] = -3;
        PACKET_SIZES[23] = 6;
        PACKET_SIZES[24] = -3;
        PACKET_SIZES[25] = 12;
        PACKET_SIZES[26] = -3;
        PACKET_SIZES[27] = -3;
        PACKET_SIZES[28] = -3;
        PACKET_SIZES[29] = -3;
        PACKET_SIZES[30] = -3;
        PACKET_SIZES[31] = -3;
        PACKET_SIZES[32] = -3;
        PACKET_SIZES[33] = -3;
        PACKET_SIZES[34] = -3;
        PACKET_SIZES[35] = 8;
        PACKET_SIZES[36] = 4;
        PACKET_SIZES[37] = -3;
        PACKET_SIZES[38] = -3;
        PACKET_SIZES[39] = 2;
        PACKET_SIZES[40] = 2;
        PACKET_SIZES[41] = 6;
        PACKET_SIZES[42] = -3;
        PACKET_SIZES[43] = 8;
        PACKET_SIZES[44] = -3;
        PACKET_SIZES[45] = -1;
        PACKET_SIZES[46] = -3;
        PACKET_SIZES[47] = -3;
        PACKET_SIZES[48] = -3;
        PACKET_SIZES[49] = -3;
        PACKET_SIZES[50] = -3;
        PACKET_SIZES[51] = -3;
        PACKET_SIZES[52] = -3;
        PACKET_SIZES[53] = 12;
        PACKET_SIZES[54] = -3;
        PACKET_SIZES[55] = -3;
        PACKET_SIZES[56] = -3;
        PACKET_SIZES[57] = 8;
        PACKET_SIZES[58] = 8;
        PACKET_SIZES[59] = 12;
        PACKET_SIZES[60] = -1;
        PACKET_SIZES[61] = 8;
        PACKET_SIZES[62] = -3;
        PACKET_SIZES[63] = -3;
        PACKET_SIZES[64] = -3;
        PACKET_SIZES[65] = -3;
        PACKET_SIZES[66] = -3;
        PACKET_SIZES[67] = -3;
        PACKET_SIZES[68] = -3;
        PACKET_SIZES[69] = -3;
        PACKET_SIZES[OBJECT_THIRD_CLICK_OPCODE] = 8;
        PACKET_SIZES[71] = -3;
        PACKET_SIZES[72] = 2;
        PACKET_SIZES[73] = 2;
        PACKET_SIZES[74] = -1;
        PACKET_SIZES[75] = 6;
        PACKET_SIZES[76] = -3;
        PACKET_SIZES[77] = -1;
        PACKET_SIZES[78] = -3;
        PACKET_SIZES[79] = -3;
        PACKET_SIZES[80] = -3;
        PACKET_SIZES[81] = -3;
        PACKET_SIZES[82] = -3;
        PACKET_SIZES[83] = -3;
        PACKET_SIZES[84] = -3;
        PACKET_SIZES[85] = 1;
        PACKET_SIZES[86] = 4;
        PACKET_SIZES[87] = 6;
        PACKET_SIZES[88] = -3;
        PACKET_SIZES[89] = -3;
        PACKET_SIZES[90] = -3;
        PACKET_SIZES[91] = -3;
        PACKET_SIZES[92] = -3;
        PACKET_SIZES[93] = -3;
        PACKET_SIZES[94] = -3;
        PACKET_SIZES[95] = 4;
        PACKET_SIZES[96] = -3;
        PACKET_SIZES[97] = -3;
        PACKET_SIZES[98] = -1;
        PACKET_SIZES[99] = -3;
        PACKET_SIZES[100] = -3;
        PACKET_SIZES[101] = 13;
        PACKET_SIZES[102] = -3;
        PACKET_SIZES[103] = -1;
        PACKET_SIZES[104] = -3;
        PACKET_SIZES[105] = -3;
        PACKET_SIZES[106] = -3;
        PACKET_SIZES[107] = -3;
        PACKET_SIZES[108] = -3;
        PACKET_SIZES[109] = -3;
        PACKET_SIZES[110] = -3;
        PACKET_SIZES[111] = -3;
        PACKET_SIZES[112] = -3;
        PACKET_SIZES[113] = -3;
        PACKET_SIZES[114] = -3;
        PACKET_SIZES[115] = -3;
        PACKET_SIZES[116] = 7;
        PACKET_SIZES[117] = 8;
        PACKET_SIZES[118] = -3;
        PACKET_SIZES[119] = -3;
        PACKET_SIZES[120] = 1;
        PACKET_SIZES[121] = 0;
        PACKET_SIZES[122] = 6;
        PACKET_SIZES[123] = -3;
        PACKET_SIZES[124] = -3;
        PACKET_SIZES[125] = 2;
        PACKET_SIZES[126] = -1;
        PACKET_SIZES[128] = 2;
        PACKET_SIZES[129] = 8;
        PACKET_SIZES[130] = 0;
        PACKET_SIZES[131] = 4;
        PACKET_SIZES[OBJECT_FIRST_CLICK_OPCODE] = 8;
        PACKET_SIZES[133] = -1;
        PACKET_SIZES[134] = -3;
        PACKET_SIZES[135] = 8;
        PACKET_SIZES[136] = -3;
        PACKET_SIZES[137] = -3;
        PACKET_SIZES[138] = -3;
        PACKET_SIZES[139] = 2;
        PACKET_SIZES[140] = 6;
        PACKET_SIZES[141] = 10;
        PACKET_SIZES[142] = -1;
        PACKET_SIZES[143] = -3;
        PACKET_SIZES[144] = -3;
        PACKET_SIZES[145] = 8;
        PACKET_SIZES[146] = -3;
        PACKET_SIZES[147] = -3;
        PACKET_SIZES[148] = -3;
        PACKET_SIZES[149] = 7;
        PACKET_SIZES[150] = 6;
        PACKET_SIZES[151] = -3;
        PACKET_SIZES[152] = 1;
        PACKET_SIZES[153] = 2;
        PACKET_SIZES[154] = -3;
        PACKET_SIZES[155] = 2;
        PACKET_SIZES[156] = 6;
        PACKET_SIZES[157] = -3;
        PACKET_SIZES[158] = -3;
        PACKET_SIZES[159] = -3;
        PACKET_SIZES[160] = -1;
        PACKET_SIZES[161] = 1;
        PACKET_SIZES[162] = -3;
        PACKET_SIZES[163] = -3;
        PACKET_SIZES[164] = -1;
        PACKET_SIZES[165] = -1;
        PACKET_SIZES[166] = -3;
        PACKET_SIZES[167] = -3;
        PACKET_SIZES[168] = -3;
        PACKET_SIZES[169] = -3;
        PACKET_SIZES[170] = -3;
        PACKET_SIZES[171] = -3;
        PACKET_SIZES[172] = -3;
        PACKET_SIZES[173] = -3;
        PACKET_SIZES[174] = -3;
        PACKET_SIZES[175] = -3;
        PACKET_SIZES[176] = -3;
        PACKET_SIZES[177] = 1;
        PACKET_SIZES[178] = -3;
        PACKET_SIZES[179] = -3;
        PACKET_SIZES[180] = -3;
        PACKET_SIZES[181] = 8;
        PACKET_SIZES[182] = -3;
        PACKET_SIZES[183] = -3;
        PACKET_SIZES[184] = 4;
        PACKET_SIZES[185] = 4;
        PACKET_SIZES[186] = 5;
        PACKET_SIZES[187] = -3;
        PACKET_SIZES[188] = -1;
        PACKET_SIZES[189] = 1;
        PACKET_SIZES[190] = -3;
        PACKET_SIZES[191] = -3;
        PACKET_SIZES[192] = 12;
        PACKET_SIZES[193] = -3;
        PACKET_SIZES[194] = -3;
        PACKET_SIZES[195] = -3;
        PACKET_SIZES[196] = -3;
        PACKET_SIZES[197] = -3;
        PACKET_SIZES[198] = -3;
        PACKET_SIZES[199] = -3;
        PACKET_SIZES[200] = 2;
        PACKET_SIZES[201] = -3;
        PACKET_SIZES[202] = 0;
        PACKET_SIZES[203] = -3;
        PACKET_SIZES[204] = -3;
        PACKET_SIZES[205] = -3;
        PACKET_SIZES[206] = -3;
        PACKET_SIZES[207] = -3;
        PACKET_SIZES[208] = 5;
        PACKET_SIZES[209] = -3;
        PACKET_SIZES[210] = 4;
        PACKET_SIZES[211] = -3;
        PACKET_SIZES[212] = -3;
        PACKET_SIZES[213] = 5;
        PACKET_SIZES[214] = 9;
        PACKET_SIZES[215] = -1;
        PACKET_SIZES[216] = -3;
        PACKET_SIZES[217] = -3;
        PACKET_SIZES[218] = 10;
        PACKET_SIZES[219] = -3;
        PACKET_SIZES[220] = -3;
        PACKET_SIZES[221] = -3;
        PACKET_SIZES[222] = -3;
        PACKET_SIZES[223] = -3;
        PACKET_SIZES[224] = -3;
        PACKET_SIZES[225] = -3;
        PACKET_SIZES[226] = -1;
        PACKET_SIZES[227] = -3;
        PACKET_SIZES[OBJECT_FOURTH_CLICK_OPCODE] = 8;
        PACKET_SIZES[229] = -3;
        PACKET_SIZES[230] = 1;
        PACKET_SIZES[231] = -3;
        PACKET_SIZES[232] = -3;
        PACKET_SIZES[233] = -3;
        PACKET_SIZES[234] = -3;
        PACKET_SIZES[235] = 6;
        PACKET_SIZES[236] = 6;
        PACKET_SIZES[237] = 8;
        PACKET_SIZES[238] = 1;
        PACKET_SIZES[239] = -3;
        PACKET_SIZES[240] = -3;
        PACKET_SIZES[241] = -3;
        PACKET_SIZES[242] = -3;
        PACKET_SIZES[243] = -3;
        PACKET_SIZES[244] = -3;
        PACKET_SIZES[245] = -3;
        PACKET_SIZES[246] = -1;
        PACKET_SIZES[247] = -3;
        PACKET_SIZES[248] = -1;
        PACKET_SIZES[249] = 4;
        PACKET_SIZES[250] = -3;
        PACKET_SIZES[251] = -3;
        PACKET_SIZES[OBJECT_SECOND_CLICK_OPCODE] = 8;
        PACKET_SIZES[253] = 6;
        PACKET_SIZES[254] = -3;
        // Exceeds array no idea why its set to [257] PACKET_SIZES[256] = -3;
    }

}
