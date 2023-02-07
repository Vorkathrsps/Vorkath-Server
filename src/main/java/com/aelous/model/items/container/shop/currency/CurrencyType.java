package com.aelous.model.items.container.shop.currency;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.shop.currency.impl.*;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.google.common.collect.ImmutableSet;

/**
 * The enumerated type whom holds all the currencies usable for a server.
 *
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public enum CurrencyType {

    COINS(new ItemCurrency(ItemIdentifiers.COINS_995)),
    MARK_OF_GRACE(new ItemCurrency(ItemIdentifiers.MARK_OF_GRACE)),
    BLOOD_MONEY(new ItemCurrency(ItemIdentifiers.BLOOD_MONEY)),
    TARGET_POINTS(new TargetPointsCurrency()),
    SLAYER_REWARD_POINTS(new SlayerPointsCurrency()),
    VOTE_POINTS(new VotePointsCurrency()),
    BOSS_POINTS(new BossPointsCurrency()),
    TOURNAMENT_POINTS(new TournamentPointsCurrency()),
    RISKZONE_POINTS(new RiskzonePointsCurrency());

    private static final ImmutableSet<CurrencyType> VALUES = ImmutableSet.copyOf(values());

    public final Currency currency;

    CurrencyType(Currency currency) {
        this.currency = currency;
    }

    public static boolean isCurrency(int id) {
        return VALUES.stream().filter(i -> i.currency.tangible()).anyMatch(i -> ((ItemCurrency) i.currency).itemId == id);
    }

    public static String getValue(Player player, CurrencyType currency) {
        String value = "";
        switch (currency) {
            case MARK_OF_GRACE:
                value = Utils.formatNumber(player.inventory().contains(ItemIdentifiers.MARK_OF_GRACE) ? player.inventory().count(ItemIdentifiers.MARK_OF_GRACE) : 0);
                break;
            case BLOOD_MONEY:
                value = Utils.formatNumber(player.inventory().contains(ItemIdentifiers.BLOOD_MONEY) ? player.inventory().count(ItemIdentifiers.BLOOD_MONEY) : 0);
                break;
            case TARGET_POINTS:
                var targetPoints = player.<Integer>getAttribOr(AttributeKey.TARGET_POINTS, 0);
                value = Utils.formatNumber(targetPoints);
                break;
            case SLAYER_REWARD_POINTS:
                int slayerRewardPoints = player.getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
                value = Utils.formatNumber(slayerRewardPoints);
                break;
            case VOTE_POINTS:
                int votePoints = player.getAttribOr(AttributeKey.VOTE_POINS, 0);
                value = Utils.formatNumber(votePoints);
                break;
            case BOSS_POINTS:
                int bossPoints = player.getAttribOr(AttributeKey.BOSS_POINTS, 0);
                value = Utils.formatNumber(bossPoints);
                break;
            default:
                break;
        }
        return value.equals("0") ? "None!" : value;
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", " ");
    }
}
