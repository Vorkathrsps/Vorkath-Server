package com.cryptic.clientscripts.impl.emotes

import com.cryptic.interfaces.Varbits
import com.cryptic.model.content.items.equipment.max_cape.MaxCape
import com.cryptic.model.entity.combat.CombatFactory
import com.cryptic.model.entity.masks.impl.animations.Animation
import com.cryptic.model.entity.masks.impl.graphics.Graphic
import com.cryptic.model.entity.player.EquipSlot
import com.cryptic.model.entity.player.Player
import com.cryptic.utility.ItemIdentifiers

//TODO
//Uri
//FORTIS_SALITE
//Handle Skill cape

enum class Emote(
    val slot: Int, val anim: Int, val gfx: Int = -1,
    val varbit: Int = -1, val requiredVarbitValue: Int = 1,
    val unlockDescription: String? = null) {
    YES(slot = 0, anim = 855),
    NO(slot = 1, anim = 856),
    BOW(slot = 2, anim = 858),
    ANGRY(slot = 3, anim = 859),
    THINK(slot = 4, anim = 857),
    WAVE(slot = 5, anim = 863),
    SHRUG(slot = 6, anim = 2113),
    CHEER(slot = 7, anim = 862),
    BECKON(slot = 8, anim = 864),
    LAUGH(slot = 9, anim = 861),
    JUMP_FOR_JOY(slot = 10, anim = 2109),
    YAWN(slot = 11, anim = 2111),
    DANCE(slot = 12, anim = 866),
    JIG(slot = 13, anim = 2106),
    SPIN(slot = 14, anim = 2107),
    HEADBANG(slot = 15, anim = 2108),
    CRY(slot = 16, anim = 860),
    BLOW_KISS(slot = 17, anim = 1374, gfx = 574),
    PANIC(slot = 18, anim = 2105),
    RASPBERRY(slot = 19, anim = 2110),
    CLAP(slot = 20, anim = 865),
    SALUTE(slot = 21, anim = 2112),
    GOBLIN_BOW(slot = 22, anim = 2127, varbit = Varbits.GOBLIN_EMOTES_VARBIT, requiredVarbitValue = 7),
    GOBLIN_SALUTE(slot = 23, anim = 2128, varbit = Varbits.GOBLIN_EMOTES_VARBIT, requiredVarbitValue = 7),
    GLASS_BOX(slot = 24, anim = 1131, varbit = Varbits.GLASS_BOX_EMOTE_VARBIT),
    CLIMB_ROPE(slot = 25, anim = 1130, varbit = Varbits.CLIMB_ROPE_EMOTE_VARBIT),
    LEAN(slot = 26, anim = 1129, varbit = Varbits.LEAN_EMOTE_VARBIT),
    GLASS_WALL(slot = 27, anim = 1128, varbit = Varbits.GLASS_WALL_EMOTE_VARBIT),
    IDEA(slot = 28, anim = 4276, gfx = 712, varbit = Varbits.IDEA_EMOTE_VARBIT),
    STAMP(slot = 29, anim = 1745, varbit = Varbits.STAMP_EMOTE_VARBIT),
    FLAP(slot = 30, anim = 4280, varbit = Varbits.FLAP_EMOTE_VARBIT),
    SLAP_HEAD(slot = 31, anim = 4275, varbit = Varbits.SLAP_HEAD_EMOTE_VARBIT),
    ZOMBIE_WALK(slot = 32, anim = 3544, varbit = Varbits.ZOMBIE_WALK_EMOTE_VARBIT),
    ZOMBIE_DANCE(slot = 33, anim = 3543, varbit = Varbits.ZOMBIE_DANCE_EMOTE_VARBIT),
    SCARED(slot = 34, anim = 2836, varbit = Varbits.SCARED_EMOTE_VARBIT),
    RABBIT_HOP(slot = 35, anim = 6111, varbit = Varbits.RABBIT_HOP_EMOTE_VARBIT), //
    SIT_UP(slot = 36, anim = 2763, varbit = Varbits.EXERCISE_EMOTES),
    PUSH_UP(slot = 37, anim = 2762, varbit = Varbits.EXERCISE_EMOTES),
    STAR_JUMP(slot = 38, anim = 2761, varbit = Varbits.EXERCISE_EMOTES),
    JOG(slot = 39, anim = 2764, varbit = Varbits.EXERCISE_EMOTES),
    FLEX(slot = 40, anim = 8917, gfx = -1, varbit = Varbits.FLEX_EMOTE_VARBIT),
    ZOMBIE_HAND(slot = 41, anim = 1708, gfx = 320, varbit = Varbits.ZOMBIE_HAND_EMOTE_VARBIT),
    HYPERMOBILE_DRINKER(slot = 42, anim = 7131, varbit = Varbits.HYPERMOBILE_DRINKER_EMOTE_VARBIT),
    SKILLCAPE(slot = 43, anim = -1),
    AIR_GUITAR(slot = 44, anim = 4751, gfx = 1239, varbit = Varbits.AIR_GUITAR_EMOTE_VARBIT),
    URI_TRANSFORM(slot = 45, anim = -1, gfx = -1, varbit = Varbits.URI_TRANSFORM_EMOTE_VARBIT),
    SMOOTH_DANCE(slot = 46, anim = 7533, varbit = Varbits.SMOOTH_DANCE_EMOTE_VARBIT),
    CRAZY_DANCE(slot = 47, anim = 7536, varbit = Varbits.CRAZY_DANCE_EMOTE_VARBIT),
    // bronze, silver, and gold shield, referencing the 3, 6, and 12 month packages from the Premier Club.
    PREMIER_SHIELD(slot = 48, anim = 7751, gfx = 1412, varbit = Varbits.PREMIER_SHIELD_EMOTE_VARBIT),
    EXPLORE(slot = 49, anim = 8541, varbit = Varbits.EXPLORE_VARBIT),
    RELIC_UNLOCKED(slot = 50, anim = 8524, gfx = 1835, varbit = Varbits.RELIC_UNLOCKED_EMOTE_VARBIT, requiredVarbitValue = 9),
    PARTY(slot = 51, anim = 10031, gfx = 2365, varbit = Varbits.PARTY_EMOTE_VARBIT),
    FORTIS_SALITE(slot = 52, anim = 10031, gfx = 2365, varbit = Varbits.PARTY_EMOTE_VARBIT);

    companion object {
        val values = enumValues<Emote>()

        fun forId(slot: Int): Emote {
            return values[slot]
        }

        fun doEmote(player: Player, slot: Int): Boolean {
            val data: Emote = forId(slot)

            if (data != null) {
                animation(player, Animation(data.anim), Graphic(data.gfx))
                player.stopActions(false)
                return true
            }

            //Skill cape slot
            if (forId(slot) === SKILLCAPE) {
                val cape = player.equipment[EquipSlot.CAPE]
                if (cape == null) {
                    player.message("You need to be wearing a Skill Cape to perform this emote.")
                    return true
                }
                val capeid = cape.id
                when (capeid) {
                    9747, 9748 -> {
                        player.animate(4959)
                        player.graphic(823)
                    }

                    9753, 9754 -> {
                        player.animate(4961)
                        player.graphic(824)
                    }

                    9750, 9751 -> {
                        player.animate(4981)
                        player.graphic(828)
                    }

                    9768, 9769 -> {
                        player.animate(4971)
                        player.graphic(833)
                    }

                    9756, 9757 -> {
                        player.animate(4973)
                        player.graphic(832)
                    }

                    9762, 9763 -> {
                        player.animate(4939)
                        player.graphic(813)
                    }

                    9759, 9760 -> {
                        player.animate(4979)
                        player.graphic(829)
                    }

                    9801, 9802 -> {
                        player.animate(4955)
                        player.graphic(821)
                    }

                    9807, 9808 -> {
                        player.animate(4957)
                        player.graphic(822)
                    }

                    9783, 9784 -> {
                        player.animate(4937)
                        player.graphic(812)
                    }

                    9798, 9799 -> {
                        player.animate(4951)
                        player.graphic(819)
                    }

                    9804, 9805 -> {
                        player.animate(4975)
                        player.graphic(8831)
                    }

                    9780, 9781 -> {
                        player.animate(4949)
                        player.graphic(818)
                    }

                    9795, 9796 -> {
                        player.animate(4943)
                        player.graphic(815)
                    }

                    9792, 9793 -> {
                        player.animate(4941)
                        player.graphic(814)
                    }

                    9774, 9775 -> {
                        player.animate(4969)
                        player.graphic(835)
                    }

                    9771, 9772 -> {
                        player.animate(4977)
                        player.graphic(830)
                    }

                    9777, 9778 -> {
                        player.animate(4965)
                        player.graphic(826)
                    }

                    9786, 9787 -> {
                        player.animate(4967)
                        player.graphic(1656)
                    }

                    9810, 9811 -> {
                        player.animate(4963)
                        player.graphic(826)
                    }

                    9765, 9766 -> {
                        player.animate(4947)
                        player.graphic(817)
                    }

                    9789, 9790 -> {
                        player.animate(4953)
                        player.graphic(820)
                    }

                    9948, 9949 -> {
                        player.animate(5158)
                        player.graphic(907)
                    }

                    9813 -> {
                        player.animate(4945)
                        player.graphic(816)
                    }

                    13069 -> {
                        player.animate(7121)
                        player.graphic(1286)
                    }

                    ItemIdentifiers.MAX_CAPE, ItemIdentifiers.FIRE_MAX_CAPE, ItemIdentifiers.SARADOMIN_MAX_CAPE, ItemIdentifiers.ZAMORAK_MAX_CAPE, ItemIdentifiers.GUTHIX_MAX_CAPE, ItemIdentifiers.ACCUMULATOR_MAX_CAPE, ItemIdentifiers.MAX_CAPE_13342, ItemIdentifiers.ARDOUGNE_MAX_CAPE, ItemIdentifiers.INFERNAL_MAX_CAPE_21285, ItemIdentifiers.IMBUED_SARADOMIN_MAX_CAPE, ItemIdentifiers.IMBUED_ZAMORAK_MAX_CAPE, ItemIdentifiers.IMBUED_GUTHIX_MAX_CAPE, ItemIdentifiers.ASSEMBLER_MAX_CAPE, ItemIdentifiers.MYTHICAL_MAX_CAPE, MaxCape.FROST_IMBUED_MAX_CAPE -> {
                        player.animate(7121)
                        player.graphic(1286)
                    }
                }
                player.stopActions(false)
                return true
            }

            return false
        }

        private fun animation(player: Player, anim: Animation?, graphic: Graphic?) {
            if (CombatFactory.inCombat(player)) {
                player.message("You cannot do this right now.")
                return
            }

            //Stop movement..
            player.movementQueue.clear()

            if (anim != null) {
                player.animate(anim)
            }
            if (graphic != null) player.performGraphic(graphic)
        }

    }
}
