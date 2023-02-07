package com.aelous.model.content.title.req.impl.other;

import com.aelous.model.content.title.req.TitleRequirement;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Utils;

import java.util.EnumSet;
import java.util.Set;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;

public class TitleUnlockRequirement extends TitleRequirement {

    public enum UnlockableTitle {
        MILLIONAIRE(10000), BILLIONAIRE(10000), KING(10000), QUEEN(10000), SIR(10000), MR(10000), MISS(10000), MRS(10000), THE_IDIOT(10000), LAZY(10000), NOOB(10000), DRUNKEN(10000), THE_MAGNIFICENT(10000), THE_AWESOME(10000), COWARDLY(10000);

        public static final Set<UnlockableTitle> SET = EnumSet.allOf(UnlockableTitle.class);

        private final int cost;

        UnlockableTitle(int cost) {
            this.cost = cost;
        }

        public String getName() {
            String name = name().toLowerCase().replaceAll("_", " ");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            return name;
        }
    }

    private final UnlockableTitle title;

    public TitleUnlockRequirement(UnlockableTitle title) {
        super("Costs " + Utils.formatNumber(title.cost) + " BM<br>to unlock");
        this.title = title;
    }

    @Override
    public boolean satisfies(Player player) {
        if (!player.getUnlockedTitles().contains(title)) {

            int bmInInventory = player.inventory().count(BLOOD_MONEY);
            if (bmInInventory > 0) {
                if(bmInInventory >= title.cost) {
                    player.inventory().remove(BLOOD_MONEY, title.cost);
                    player.getUnlockedTitles().add(title);
                }
            } else {
                player.message("You do not have enough Blood money to purchase this title.");
            }
        }
        return player.getUnlockedTitles().contains(title);
    }
}
