package com.aelous.model.content.skill.impl.slayer.slayer_reward_interface;

import com.aelous.model.content.skill.impl.slayer.SlayerRewards;
import com.aelous.utility.Utils;

import java.util.HashMap;

/**
 * @author Patrick van Elderen | December, 21, 2020, 13:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum SlayerRewardButtons {

    /**
     * Basic core actions
     */
    SLAYER_UNLOCK_INTERFACE(new int[]{63201, 64301, 63401, 63501, 64006}, 63400, SlayerRewardActions.UNLOCK_INTERFACE),
    SLAYER_EXTEND_INTERFACE(new int[]{63202, 64302, 63402, 63502, 64007}, 64300, SlayerRewardActions.EXTEND_INTERFACE),
    SLAYER_BUY_INTERFACE(new int[]{63203, 64303, 63403, 64503}, 64000, SlayerRewardActions.BUY_INTERFACE),
    SLAYER_TASK_INTERFACE(new int[]{63204, 64304, 63404, 63504, 64009}, 63200, SlayerRewardActions.TASK_INTERFACE),
    BACK(new int[]{63102}, SlayerRewards.getPreviousInterface(), SlayerRewardActions.BACK),
    CLOSE(new int[]{63002, 64002}, -1, SlayerRewardActions.CLOSE),
    CONFIRM(new int[]{63103}, -1, SlayerRewardActions.CONFIRM),
    BLOCK(new int[]{63210}, -1, SlayerRewardActions.BLOCK),
    UNBLOCK(new int[]{63226, 63227, 63228, 63229, 63230, 63231}, -1, SlayerRewardActions.UNBLOCK),
    CANCEL(new int[]{63209}, -1, SlayerRewardActions.CANCEL),

    /**
     * Unlock Buttons
     */
    DOUBLE_SLAYER_POINTS(new int[]{63406}, 0, SlayerRewardActions.UNLOCK_BUTTON, 750),
    BM_FROM_KILLING_BOSSES(new int[]{63407}, 0, SlayerRewardActions.UNLOCK_BUTTON, 751),
    DOUBLE_DROP_CHANCE(new int[]{63408}, 0, SlayerRewardActions.UNLOCK_BUTTON, 752),
    KILL_BLOW(new int[]{63409}, 0, SlayerRewardActions.UNLOCK_BUTTON, 753),
    BIGGER_AND_BADDER(new int[]{63410}, 0, SlayerRewardActions.UNLOCK_BUTTON, 754),
    REVENANT_TELEPORT(new int[]{63411}, 0, SlayerRewardActions.UNLOCK_BUTTON, 755),
    LARRANS_LUCK(new int[]{63412}, 0, SlayerRewardActions.UNLOCK_BUTTON, 756),
    NO_SLAYER_REQ(new int[]{63413}, 0, SlayerRewardActions.UNLOCK_BUTTON, 757),
    MORE_BM_THIEVING(new int[]{63414}, 0, SlayerRewardActions.UNLOCK_BUTTON, 758),
    DROP_RATE_BOOST(new int[]{63415}, 0, SlayerRewardActions.UNLOCK_BUTTON, 759),
    ZRIAWK_BOOST(new int[]{63416}, 0, SlayerRewardActions.UNLOCK_BUTTON, 760),
    DOUBLE_DROP_LAMPS(new int[]{63417}, 0, SlayerRewardActions.UNLOCK_BUTTON, 761),
    LIKE_A_BOSS(new int[]{63418}, 0, SlayerRewardActions.UNLOCK_BUTTON, 762),
    KEY_OF_DROPS(new int[]{63419}, 0, SlayerRewardActions.UNLOCK_BUTTON, 763),
    NOTED_DRAGON_BONES(new int[]{63420}, 0, SlayerRewardActions.UNLOCK_BUTTON, 764),
    GODWARS_ENTRY(new int[]{63421}, 0, SlayerRewardActions.UNLOCK_BUTTON, 765),
    WORLD_BOSS_TELEPORT(new int[]{63422}, 0, SlayerRewardActions.UNLOCK_BUTTON, 766),
    RUNAWAY(new int[]{63423}, 0, SlayerRewardActions.UNLOCK_BUTTON, 767),
    TREASURE_HUNT(new int[]{63424}, 0, SlayerRewardActions.UNLOCK_BUTTON, 768),
    TZTOK_JAD(new int[]{63425}, 0, SlayerRewardActions.UNLOCK_BUTTON, 769),
    BIGGEST_AND_BADDEST(new int[]{63426}, 0, SlayerRewardActions.UNLOCK_BUTTON, 770),
    ELECTION_DAY(new int[]{63427}, 0, SlayerRewardActions.UNLOCK_BUTTON, 771),
    WHOS_KEYS_ARE_THESE(new int[]{63428}, 0, SlayerRewardActions.UNLOCK_BUTTON, 772),
    WEAK_SPOT(new int[]{63429}, 0, SlayerRewardActions.UNLOCK_BUTTON, 773),
    DIGGING_FOR_TREASURE(new int[]{63430}, 0, SlayerRewardActions.UNLOCK_BUTTON, 774),

    /**
     * Extend Actions
     */
    ADAMIND_SOME_MORE(new int[]{64306}, 0, SlayerRewardActions.EXTEND_BUTTON, 560),
    RUUUUUNE(new int[]{64307}, 0, SlayerRewardActions.EXTEND_BUTTON, 561),
    BARRELCHEST(new int[]{64308}, 0, SlayerRewardActions.EXTEND_BUTTON, 562),
    FLUFFY(new int[]{64309}, 0, SlayerRewardActions.EXTEND_BUTTON, 563),
    PURE_CHAOS(new int[]{64310}, 0, SlayerRewardActions.EXTEND_BUTTON, 564),
    CORPOREAL_LECTURE(new int[]{64311}, 0, SlayerRewardActions.EXTEND_BUTTON, 565),
    CRAZY_SCIENTIST(new int[]{64312}, 0, SlayerRewardActions.EXTEND_BUTTON, 566),
    GORILLA_DEMON(new int[]{64313}, 0, SlayerRewardActions.EXTEND_BUTTON, 567),
    DRAGON_SLAYER(new int[]{64314}, 0, SlayerRewardActions.EXTEND_BUTTON, 568),
    SCYLLA(new int[]{64315}, 0, SlayerRewardActions.EXTEND_BUTTON, 569),
    JUMPING_JACKS(new int[]{64316}, 0, SlayerRewardActions.EXTEND_BUTTON, 570),
    SPOOKY_SCARY_SKELETONS(new int[]{64317}, 0, SlayerRewardActions.EXTEND_BUTTON, 571),
    ATOMIC_BOMB(new int[]{64318}, 0, SlayerRewardActions.EXTEND_BUTTON, 572),
    VORKI(new int[]{64319}, 0, SlayerRewardActions.EXTEND_BUTTON, 573),
    NAGINI(new int[]{64320}, 0, SlayerRewardActions.EXTEND_BUTTON, 574),
    WYVER_ANOTHER_ONE(new int[]{64321}, 0, SlayerRewardActions.EXTEND_BUTTON, 575),
    ARAGOG(new int[]{64322}, 0, SlayerRewardActions.EXTEND_BUTTON, 576),
    BEWEAR(new int[]{64323}, 0, SlayerRewardActions.EXTEND_BUTTON, 577),
    DRAKE(new int[]{64324}, 0, SlayerRewardActions.EXTEND_BUTTON, 578),
    WYRM_ME_ON(new int[]{64325}, 0, SlayerRewardActions.EXTEND_BUTTON, 579),
    DR_CHAOS(new int[]{64326}, 0, SlayerRewardActions.EXTEND_BUTTON, 580),
    DIG_ME_UP(new int[]{64327}, 0, SlayerRewardActions.EXTEND_BUTTON, 581),
    LAVA(new int[]{64328}, 0, SlayerRewardActions.EXTEND_BUTTON, 582),
    WORLD_BOSSILONGER(new int[]{64329}, 0, SlayerRewardActions.EXTEND_BUTTON, 583),
    GOD_WAR(new int[]{64330}, 0, SlayerRewardActions.EXTEND_BUTTON, 584);

    private final int[] button;
    private final int interfaceId;
    private final SlayerRewardActions action;
    private int configId;

    public int getConfigId() {
        return configId;
    }

    SlayerRewardButtons(int[] button, int interfaceId, SlayerRewardActions action) {
        this.button = button;
        this.interfaceId = interfaceId;
        this.action = action;
    }

    SlayerRewardButtons(int[] button, int interfaceId, SlayerRewardActions action, int configId) {
        this.button = button;
        this.interfaceId = interfaceId;
        this.action = action;
        this.configId = configId;
    }

    public int[] getButton() {
        return button;
    }

    public int getInterface() {
        return interfaceId;
    }

    public SlayerRewardActions getAction() {
        return action;
    }

    public String format() {
        return Utils.capitalizeFirst(name().toLowerCase().replaceAll("_", " ").trim());
    }

    public static HashMap<Integer, SlayerRewardButtons> rewardButtonsHashMap = new HashMap<>();

    static {
        for (final SlayerRewardButtons buttonData : SlayerRewardButtons.values()) {
            for (final int button : buttonData.getButton()) {
                rewardButtonsHashMap.put(button, buttonData);
            }
        }
    }
}
