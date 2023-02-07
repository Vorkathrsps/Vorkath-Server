package com.aelous.model.content.emote;

import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import java.util.HashMap;
import java.util.Map;

import static com.aelous.model.content.items.equipment.max_cape.MaxCape.FROST_IMBUED_MAX_CAPE;
import static com.aelous.utility.ItemIdentifiers.*;

public class Emotes {

    private enum EmoteData {
        YES(168, new Animation(855), null),
        NO(169, new Animation(856), null),
        BOW(164, new Animation(858), null),
        ANGRY(165, new Animation(859), null),
        THINK(162, new Animation(857), null),
        WAVE(163, new Animation(863), null),
        SHRUG(13370, new Animation(2113), null),
        CHEER(171, new Animation(862), null),
        BECKON(167, new Animation(864), null),
        LAUGH(170, new Animation(861), null),
        JUMP_FOR_JOY(13366, new Animation(2109), null),
        YAWN(13368, new Animation(2111), null),
        DANCE(166, new Animation(866), null),
        JIG(13363, new Animation(2106), null),
        SPIN(13364, new Animation(2107), null),
        HEADBANG(13365, new Animation(2108), null),
        CRY(161, new Animation(860), null),
        KISS(11100, new Animation(1374), new Graphic(574, GraphicHeight.LOW)),
        PANIC(13362, new Animation(2105), null),
        RASPBERRY(13367, new Animation(2110), null),
        CRAP(172, new Animation(865), null),
        SALUTE(13369, new Animation(2112), null),
        GOBLIN_BOW(13383, new Animation(2127), null),
        GOBLIN_SALUTE(13384, new Animation(2128), null),
        GLASS_BOX(667, new Animation(1131), null),
        CLIMB_ROPE(6503, new Animation(1130), null),
        LEAN(6506, new Animation(1129), null),
        GLASS_WALL(666, new Animation(1128), null),
        ZOMBIE_WALK(18464, new Animation(3544), null),
        ZOMBIE_DANCE(18465, new Animation(3543), null),
        SCARED(15166, new Animation(2836), null),
        RABBIT_HOP(18686, new Animation(6111), null),

        /*ZOMBIE_HAND(15166, new Animation(7272), new Graphic(1244)),
        SAFETY_FIRST(6540, new Animation(8770), new Graphic(1553)),
        AIR_GUITAR(11101, new Animation(2414), new Graphic(1537)),
        SNOWMAN_DANCE(11102, new Animation(7531), null),
        FREEZE(11103, new Animation(11044), new Graphic(1973))*/;

        EmoteData(int button, Animation animation, Graphic graphic) {
            this.button = button;
            this.animation = animation;
            this.graphic = graphic;
        }

        private final int button;
        public Animation animation;
        public Graphic graphic;

        private static final Map<Integer, EmoteData> emotes = new HashMap<>();

        static {
            for (EmoteData t : EmoteData.values()) {
                emotes.put(t.button, t);
            }
        }

        public static EmoteData forId(int button) {
            return emotes.get(button);
        }
    }

    public static boolean doEmote(Player player, int button) {
        EmoteData data = EmoteData.forId(button);
        if (data != null) {
            animation(player, data.animation, data.graphic);
            player.stopActions(false);
            return true;
        }

        //Skill cape button
        if (button == 19052) {
            Item cape = player.getEquipment().get(EquipSlot.CAPE);
            if (cape == null) {
                player.message("You need to be wearing a Skill Cape to perform this emote.");
                return true;
            }
            int capeid = cape.getId();
            switch (capeid) {
                case 9747, 9748 -> {
                    player.animate(4959);
                    player.graphic(823);
                }
                case 9753, 9754 -> {
                    player.animate(4961);
                    player.graphic(824);
                }
                case 9750, 9751 -> {
                    player.animate(4981);
                    player.graphic(828);
                }
                case 9768, 9769 -> {
                    player.animate(4971);
                    player.graphic(833);
                }
                case 9756, 9757 -> {
                    player.animate(4973);
                    player.graphic(832);
                }
                case 9762, 9763 -> {
                    player.animate(4939);
                    player.graphic(813);
                }
                case 9759, 9760 -> {
                    player.animate(4979);
                    player.graphic(829);
                }
                case 9801, 9802 -> {
                    player.animate(4955);
                    player.graphic(821);
                }
                case 9807, 9808 -> {
                    player.animate(4957);
                    player.graphic(822);
                }
                case 9783, 9784 -> {
                    player.animate(4937);
                    player.graphic(812);
                }
                case 9798, 9799 -> {
                    player.animate(4951);
                    player.graphic(819);
                }
                case 9804, 9805 -> {
                    player.animate(4975);
                    player.graphic(8831);
                }
                case 9780, 9781 -> {
                    player.animate(4949);
                    player.graphic(818);
                }
                case 9795, 9796 -> {
                    player.animate(4943);
                    player.graphic(815);
                }
                case 9792, 9793 -> {
                    player.animate(4941);
                    player.graphic(814);
                }
                case 9774, 9775 -> {
                    player.animate(4969);
                    player.graphic(835);
                }
                case 9771, 9772 -> {
                    player.animate(4977);
                    player.graphic(830);
                }
                case 9777, 9778 -> {
                    player.animate(4965);
                    player.graphic(826);
                }
                case 9786, 9787 -> {
                    player.animate(4967);
                    player.graphic(1656);
                }
                case 9810, 9811 -> {
                    player.animate(4963);
                    player.graphic(826);
                }
                case 9765, 9766 -> {
                    player.animate(4947);
                    player.graphic(817);
                }
                case 9789, 9790 -> {
                    player.animate(4953);
                    player.graphic(820);
                }
                case 9948, 9949 -> {
                    player.animate(5158);
                    player.graphic(907);
                }
                case 9813 -> {
                    player.animate(4945);
                    player.graphic(816);
                }
                case 13069 -> {
                    player.animate(7121);
                    player.graphic(1286);
                }
                case MAX_CAPE, FIRE_MAX_CAPE, SARADOMIN_MAX_CAPE, ZAMORAK_MAX_CAPE, GUTHIX_MAX_CAPE, ACCUMULATOR_MAX_CAPE, MAX_CAPE_13342, ARDOUGNE_MAX_CAPE, INFERNAL_MAX_CAPE_21285, IMBUED_SARADOMIN_MAX_CAPE, IMBUED_ZAMORAK_MAX_CAPE, IMBUED_GUTHIX_MAX_CAPE, ASSEMBLER_MAX_CAPE, MYTHICAL_MAX_CAPE, FROST_IMBUED_MAX_CAPE-> {
                    player.animate(7121);
                    player.graphic(1286);
                }
            }
            player.stopActions(false);
            return true;
        }

        return false;
    }

    private static void animation(Player player, Animation anim, Graphic graphic) {
        if (CombatFactory.inCombat(player)) {
            player.message("You cannot do this right now.");
            return;
        }

        //Stop movement..
        player.getMovementQueue().clear();

        if (anim != null)
            player.animate(anim);
        if (graphic != null)
            player.performGraphic(graphic);
    }
}
