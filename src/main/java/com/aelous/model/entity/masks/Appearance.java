package com.aelous.model.entity.masks;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.GameMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.PacketBuilder;
import com.aelous.network.packet.ValueType;

import java.util.Arrays;

import static com.aelous.model.entity.attributes.AttributeKey.LOOT_KEYS_CARRIED;

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
    private int[] looks = {0, 10, 18, 26, 33, 36, 42};
    private int[] colors = new int[5];

    public Appearance(Player player) {
        this.player = player;
    }

    public void transmog(int id) {
        transmog = id;
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public void colors(int[] c) {
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

    public int[] colors() {
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

    public void update() {
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public void update(PacketBuilder out, Player target) {
        PacketBuilder packetBuilder = new PacketBuilder();

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
        //Some sort of headhint (arrow over head)
        packetBuilder.put(0);

        if (transmog >= 0) {
            packetBuilder.putShort(-1);
            packetBuilder.putShort(transmog);
        } else {
            Item helm = target.getEquipment().get(EquipSlot.HEAD);
            if (helm != null && helm.getId() > 1) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.HEAD).getId());
            } else {
                packetBuilder.put(0);
            }

            if (target.getEquipment().get(EquipSlot.CAPE) != null) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.CAPE).getId());
            } else {
                packetBuilder.put(0);
            }

            if (target.getEquipment().get(EquipSlot.AMULET) != null) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.AMULET).getId());
            } else {
                packetBuilder.put(0);
            }

            if (target.getEquipment().get(EquipSlot.WEAPON) != null) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.WEAPON).getId());
            } else {
                packetBuilder.put(0);
            }

            Item torso = target.getEquipment().get(EquipSlot.BODY);
            if (torso != null && torso.getId() > 1) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.BODY).getId());
            } else {
                packetBuilder.putShort(0x100 + target.looks().looks()[2]);
            }

            if (target.getEquipment().get(EquipSlot.SHIELD) != null) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.SHIELD).getId());
            } else {
                packetBuilder.put(0);
            }

            if (torso != null && torso.getId() > 1 && World.getWorld().equipmentInfo().typeFor(torso.getId()) == 6) {
                packetBuilder.put(0);
            } else {
                packetBuilder.putShort(0x100 + target.looks().looks()[3]);
            }

            if (target.getEquipment().get(EquipSlot.LEGS) != null) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.LEGS).getId());
            } else {
                packetBuilder.putShort(0x100 + target.looks().looks()[5]);
            }

            boolean head = true;
            boolean beard = true;

            if (helm != null && helm.getId() > 1) {
                head = World.getWorld().equipmentInfo().typeFor(helm.getId()) == 0;
                beard = World.getWorld().equipmentInfo().showBeard(helm.getId());
            }

            if (head) {
                packetBuilder.putShort(0x100 + target.looks().looks()[0]);
            } else {
                packetBuilder.put(0);
            }

            if (target.getEquipment().get(EquipSlot.HANDS) != null) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.HANDS).getId());
            } else {
                packetBuilder.putShort(0x100 + target.looks().looks()[4]);
            }

            if (target.getEquipment().get(EquipSlot.FEET) != null) {
                packetBuilder.putShort(0x200 + target.getEquipment().get(EquipSlot.FEET).getId());
            } else {
                packetBuilder.putShort(0x100 + target.looks().looks()[6]);
            }

            if (!target.looks().female()) {
                if (beard) {
                    packetBuilder.putShort(0x100 + target.looks().looks()[1]);
                } else {
                    packetBuilder.put(0);
                }
            } else {
                packetBuilder.put(0);
            }
        }

        // Dem colors
        for (int color : colors) {
            int col = Math.max(0, color);
            packetBuilder.put(col);
        }

        int weapon = target.getEquipment().hasAt(EquipSlot.WEAPON) ? target.getEquipment().get(EquipSlot.WEAPON).getId() : -1;

        int[] renderpair = renderpairOverride != null ? renderpairOverride : World.getWorld().equipmentInfo().renderPair(weapon);
        // Stand, walk sideways, walk, turn 180, turn 90 cw, turn 90 ccw, run
        for (int
            renderAnim : renderpair)
            packetBuilder.putShort(renderAnim); // Renderanim
        //System.out.printf("%s %s %s %s%n", weapon, Arrays.toString(renderpair), target.getEquipment().get(EquipSlot.WEAPON), target.getEquipment().hasAt(EquipSlot.WEAPON));

        packetBuilder.putString(target.getUsername());
        packetBuilder.put(target.getSkills().combatLevel());
        packetBuilder.put(target.getPlayerRights().ordinal());
        packetBuilder.put(target.getMemberRights().ordinal());
        packetBuilder.put(target.getIronManStatus().ordinal());

        out.put(packetBuilder.buffer().writerIndex(), ValueType.C);
        out.puts(packetBuilder.buffer());
    }

}
