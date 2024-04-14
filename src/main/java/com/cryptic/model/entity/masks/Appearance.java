package com.cryptic.model.entity.masks;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.codec.RSBuffer;
import com.cryptic.network.packet.PacketBuilder;
import com.cryptic.network.packet.ValueType;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cryptic.model.entity.attributes.AttributeKey.LOOT_KEYS_CARRIED;

/**
 * @author PVE
 * @Since augustus 16, 2020
 */
public class Appearance {

    private final Player player;
    private static final int[] TRANSLATION_TABLE_BACK = new int[]{-1, -1, -1, -1, 2, -1, 3, 5, 0, 4, 6, 1};
    private static final int[] WRONG_LOOKS = {18, 26, 36, 7, 33, 42, 10};
    public static final int[] GOOD_LOOKS = {0, 10, 18, 26, 33, 36, 42};
    private int[] renderpairOverride;
    private boolean female;
    private int transmog = -1;
    private boolean hide;
    @Setter
    public boolean resetLooks = false;
    private int[] looks = {0, 10, 18, 26, 33, 36, 42};
    private byte[] colors = new byte[5];

    public Appearance(Player player) {
        this.player = player;
    }

    public void transmog(int id) {
        transmog = id;
        player.getUpdateFlag().flag(Flag.ANIMATION);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
        if (id != -1) {
            var def = World.getWorld().definitions().get(NpcDefinition.class, id);
            renderpairOverride = new int[]{def.standingAnimation, def.walkingAnimation, def.walkingAnimation, def.rotate180Animation, def.rotate90LeftAnimation, def.rotate90RightAnimation, def.runAnimation};
        }
    }

    public void colors(byte[] c) {
        colors = c;
    }

    public void looks(int[] l) {
        looks = l;

        if (Arrays.equals(looks, WRONG_LOOKS)) {
            System.arraycopy(GOOD_LOOKS, 0, looks, 0, GOOD_LOOKS.length);
        }
    }

    public void female(boolean female) {
        this.female = female;
    }

    public boolean female() {
        return female;
    }

    public int[] looks() {
        return looks;
    }

    public byte[] colors() {
        return colors;
    }

    public void hide(boolean hide) {
        this.hide = hide;
    }

    public boolean hidden() {
        return hide;
    }

    public int trans() {
        return transmog;
    }

    public void render(int... pair) {
        renderpairOverride = pair;
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public void renderData(int[] data) {
        renderpairOverride = data;
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public void resetRender() {
        renderpairOverride = null;
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public void hideLooks(boolean state) {
        player.looks().setResetLooks(state);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public void update() {
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    private static final byte[] EMPTY_EQUIPMENT_DATA = new byte[12];

    private void writeEquipmentData(PacketBuilder out, Player player) {
        List<Integer> skippedSlots = new ArrayList<>();
        for (int index = 0; index < player.getEquipment().capacity(); index++) {
            var slot = player.getEquipment().get(index);
            if (slot == null) continue;
            var equipmentId = slot.getId();
            ItemDefinition def = ItemDefinition.cached.get(equipmentId);
            if (def.wearPos2 != -1) {
                if (!skippedSlots.contains(def.wearPos2)) {
                    skippedSlots.add(def.wearPos2);
                }
            }
            if (def.wearPos3 != -1) {
                if (!skippedSlots.contains(def.wearPos3)) {
                    skippedSlots.add(def.wearPos3);
                }
            }
        }

        for (int index = 0; index < 12; index++) {
            if (skippedSlots.contains(index)) {
                out.put(0);
                continue;
            }

            var slot = player.getEquipment().get(index);
            if (slot == null) {
                var appearanceValue = this.getAppearanceInSlot(index);
                if (appearanceValue < 1) {
                    out.put(0);
                } else {
                    out.putShort(appearanceValue);
                }
            } else {
                out.putShort(0x200 + slot.getId());
            }
        }
    }

    public int getAppearanceInSlot(int slot) {
        int part = -1;
        if (slot == 8) {
            part = this.looks[0];
        } else if (slot == 11) {
            part = this.looks[1];
        } else if (slot == 4) {
            part = this.looks[2];
        } else if (slot == 6) {
            part = this.looks[3];
        } else if (slot == 9) {
            part = this.looks[4];
        } else if (slot == 7) {
            part = this.looks[5];
        } else if (slot == 10) {
            part = this.looks[6];
        }

        if (part == -1) {
            return 0;
        } else {
            return 256 + part;
        }
    }


    public void update(PacketBuilder out, Player target) {
        try (final PacketBuilder packetBuilder = new PacketBuilder()) {
            String title = target.getAttribOr(AttributeKey.TITLE, "");
            if (title.length() > 15) {
                title = title.substring(0, 15);
            }
            String titleColor = target.getAttribOr(AttributeKey.TITLE_COLOR, "");
            if (titleColor.length() > 13) {
                titleColor = titleColor.substring(0, 13);
            }
            packetBuilder.putString(title);
            packetBuilder.putString(titleColor);
            packetBuilder.put(female ? 1 : 0); // Gender

            //Head icon, prayers
            packetBuilder.put(target.getHeadHint());
            //Skull icon
            var lootKeysCarried = target.<Integer>getAttribOr(LOOT_KEYS_CARRIED, 0);
            var skullType = switch (lootKeysCarried) {
                case 1 -> 2;
                case 2 -> 3;
                case 3 -> 4;
                case 4 -> 5;
                case 5 -> 6;
                default -> target.getSkullType().getCode();
            };

            packetBuilder.put(skullType);

            packetBuilder.put(0);

            final NpcDefinition definitions = NpcDefinition.cached.get(transmog);
            if (definitions != null) {
                packetBuilder.putShort(-1);
                packetBuilder.putShort(transmog);
            } else if (!resetLooks) {
                System.out.println("writting equip data");
                writeEquipmentData(packetBuilder, target);
            } else {
                System.out.println("writting empty");
                packetBuilder.writeByteArray(EMPTY_EQUIPMENT_DATA);
            }

            packetBuilder.writeByteArray(colors);

            int weapon = target.getEquipment().hasAt(EquipSlot.WEAPON) ? target.getEquipment().get(EquipSlot.WEAPON).getId() : -1;

            int[] renderpair = renderpairOverride != null ? renderpairOverride : World.getWorld().equipmentInfo().renderPair(weapon);

            for (int renderAnim : renderpair) {
                packetBuilder.putShort(renderAnim);
            }

            packetBuilder.putString(target.getUsername());
            packetBuilder.put(target.getSkills().combatLevel());
            packetBuilder.put(target.getPlayerRights().ordinal());
            packetBuilder.put(target.getMemberRights().ordinal());
            packetBuilder.put(target.getIronManStatus().ordinal());

            out.put(packetBuilder.buffer().writerIndex(), ValueType.C);
            out.puts(packetBuilder.buffer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
