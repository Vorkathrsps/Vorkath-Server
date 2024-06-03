package com.cryptic.model.content.areas.edgevile;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.services.database.Vote;
import com.cryptic.GameConstants;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.FANCY_DAN;

/**
 * @author Ynneh
 */
public class VoteManager extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {

        if (npc.getId() != FANCY_DAN) {
            /**
             * Fancy Dan ONLY!
             */
            return false;
        }

        switch (option) {

            case 1: {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        sendOption("Select an Option", "View Vote Rewards", "Open Vote URL", "Claim Vote");
                    }

                    @Override
                    protected void select(int option) {
                            if (option == 1) {
                                openRewards(player);
                            } else if (option == 2) {
                                openVoteURL(player);
                            } else if (option == 3) {
                                claimVote(player, npc);
                            }
                        stop();
                    }
                });
                return true;
            }
            case 2: {
                openRewards(player);
                return true;
            }
            case 3: {
                openVoteURL(player);
                return true;
            }
            case 4: {
                claimVote(player, npc);
                return true;
            }
        }
        return false;
    }

    public static void claimVote(Player player, NPC npc) {
        if (player.lastVoteClaim > System.currentTimeMillis()) {
            player.npcStatement(npc, new String[] { "You recently sent a vote claim request.", "You need to wait another <col=ff0000>"+((player.lastVoteClaim - System.currentTimeMillis()) / 1_000)+"</col> Seconds", "before you can submit another claim request" });
            return;
        }
        new Thread(new Vote(player)).start();
        return;
    }

    private void openRewards(Player player) {
        Shop.open(player, 6);
    }

    public static void openVoteURL(Player player) {
        player.optionsTitled("Would you like to open our voting page?", "Yes.", "No.", () ->
            player.getPacketSender().sendURL(GameConstants.VOTE_URL));
    }
}
