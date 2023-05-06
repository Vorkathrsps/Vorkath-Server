package com.aelous.model.content.minigames.impl.fight_caves.dialogue;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;

import com.aelous.model.items.Item;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Utils;

/**
 * @author Patrick van Elderen | December, 23, 2020, 15:33
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TzHaarMejJalDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        int interactionOption = player.getAttribOr(AttributeKey.INTERACTION_OPTION, 0);
        if (player.inventory().contains(6570) && interactionOption == 2) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "I have a fire cape here.");
            setPhase(36);
        } else {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "You want help JalYt-Mej-" + player.getUsername() + "?");
            setPhase(0);
        }
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            send(DialogueType.OPTION, "Select an Option", "What is this place?", "What did you call me?", "No i'm fine thanks.");
            setPhase(1);
        } else {
            if (getPhase() == 2) {
                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "This is the fight cave, Tzhaar-Xil made it for practise,", "but many JalYt come here to fight too.", "Just enter the cave and make sure you're prepared.");
                setPhase(3);
            } else {
                if (getPhase() == 3) {
                    send(DialogueType.OPTION, "Select an Option", "Are there any rules?", "Ok thanks.");
                    setPhase(4);
                } else {
                    if (getPhase() == 5) {
                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "Rules? Survival is the only rule in there.");
                        setPhase(7);
                    } else {
                        if (getPhase() == 6) {
                            stop();
                        } else {
                            if (getPhase() == 7) {
                                send(DialogueType.OPTION, "Select an Option", "Do I win anything?", "Sounds good.");
                                setPhase(29);
                            } else {
                                if (getPhase() == 8) {
                                    stop();
                                } else {
                                    if (getPhase() == 9) {
                                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "Are you not JalYt-Mej?");
                                        setPhase(10);
                                    } else {
                                        if (getPhase() == 10) {
                                            send(DialogueType.OPTION, "Select an Option", "What's a 'JalYt-Mej?", "I guess so...", "No I'm not!");
                                            setPhase(11);
                                        } else {
                                            if (getPhase() == 12) {
                                                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "That what you are... you user of mystic powers no?");
                                                setPhase(13);
                                            } else {
                                                if (getPhase() == 13) {
                                                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Well yes I suppose I am...");
                                                    setPhase(14);
                                                } else {
                                                    if (getPhase() == 14) {
                                                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "Then you JalYt-Mej!");
                                                        setPhase(15);
                                                    } else {
                                                        if (getPhase() == 15) {
                                                            send(DialogueType.OPTION, "Select an Option", "What's a 'What are you then?", "Thanks for explaining it.");
                                                            setPhase(16);
                                                        } else {
                                                            if (getPhase() == 17) {
                                                                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "Foolish JalYt, I am TzHaar-Mej, one of the mystics of", "this city.");
                                                                setPhase(19);
                                                            } else {
                                                                if (getPhase() == 18) {
                                                                    stop();
                                                                } else {
                                                                    if (getPhase() == 19) {
                                                                        send(DialogueType.OPTION, "Select an Option", "What other types are there?", "Ah ok then.");
                                                                        setPhase(20);
                                                                    } else {
                                                                        if (getPhase() == 21) {
                                                                            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "There are the mighty TzHaar-Ket who guard us, the", "swift TzHaar-Xil who hunt for our food. and the skilled", "TzHaar-Hur who craft our homes and tools.");
                                                                            setPhase(23);
                                                                        } else {
                                                                            if (getPhase() == 22) {
                                                                                stop();
                                                                            } else {
                                                                                if (getPhase() == 23) {
                                                                                    stop();
                                                                                } else {
                                                                                    if (getPhase() == 24) {
                                                                                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "Well then, no problems.");
                                                                                        setPhase(28);
                                                                                    } else {
                                                                                        if (getPhase() == 25) {
                                                                                            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "No I'm not!");
                                                                                            setPhase(26);
                                                                                        } else {
                                                                                            if (getPhase() == 26) {
                                                                                                send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "What ever you say, crazy JalYt!");
                                                                                                setPhase(27);
                                                                                            } else {
                                                                                                if (getPhase() == 27) {
                                                                                                    stop();
                                                                                                } else {
                                                                                                    if (getPhase() == 28) {
                                                                                                        stop();
                                                                                                    } else {
                                                                                                        if (getPhase() == 30) {
                                                                                                            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "You ask a lot of questions.", "Might give you TokKul if you last long enough.");
                                                                                                            setPhase(32);
                                                                                                        } else {
                                                                                                            if (getPhase() == 31) {
                                                                                                                stop();
                                                                                                            } else {
                                                                                                                if (getPhase() == 32) {
                                                                                                                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "...");
                                                                                                                    setPhase(33);
                                                                                                                } else {
                                                                                                                    if (getPhase() == 33) {
                                                                                                                        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "Before you ask, TokKul is like your Coins.");
                                                                                                                        setPhase(34);
                                                                                                                    } else {
                                                                                                                        if (getPhase() == 34) {
                                                                                                                            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "Gold is like you JalYt, soft and easily broken, we use", "hard rock forged in fire like TzHaar!");
                                                                                                                            setPhase(35);
                                                                                                                        } else {
                                                                                                                            if (getPhase() == 35) {
                                                                                                                                stop();
                                                                                                                            } else {
                                                                                                                                if (getPhase() == 36) {
                                                                                                                                    send(DialogueType.OPTION, "Sell your fire cape?", "Yes, sell it for 8,000 TokKul.", "No, keep it.", "Bargain for TzRek-Jad.");
                                                                                                                                    setPhase(37);
                                                                                                                                } else {
                                                                                                                                    if (getPhase() == 39) {
                                                                                                                                        stop();
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void select(int index) {
        if (getPhase() == 1) {
            switch (index) {
                case 1 -> {
                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "What is this place?");
                    setPhase(2);
                }
                case 2 -> {
                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "What did you call me?");
                    setPhase(9);
                }
                case 3 -> {
                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "No i'm fine thanks.");
                    setPhase(8);
                }
            }
        } else {
            if (getPhase() == 4) {
                switch (index) {
                    case 1 -> {
                        send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Are there any rules?");
                        setPhase(5);
                    }
                    case 2 -> {
                        send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Ok thanks.");
                        setPhase(6);
                    }
                }
            } else {
                if (getPhase() == 11) {
                    switch (index) {
                        case 1 -> {
                            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "What's a 'JalYt-Mej?");
                            setPhase(12);
                        }
                        case 2 -> {
                            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "I guess so...");
                            setPhase(24);
                        }
                        case 3 -> {
                            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "No I'm not!");
                            setPhase(25);
                        }
                    }
                } else {
                    if (getPhase() == 16) {
                        switch (index) {
                            case 1 -> {
                                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "What are you then?");
                                setPhase(17);
                            }
                            case 2 -> {
                                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Thanks for explaining it.");
                                setPhase(18);
                            }
                        }
                    } else {
                        if (getPhase() == 20) {
                            switch (index) {
                                case 1 -> {
                                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "What other types are there?");
                                    setPhase(21);
                                }
                                case 2 -> {
                                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Ah ok then.");
                                    setPhase(22);
                                }
                            }
                        } else {
                            if (getPhase() == 29) {
                                switch (index) {
                                    case 1 -> {
                                        send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Do I win anything?");
                                        setPhase(30);
                                    }
                                    case 2 -> {
                                        send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Sounds good.");
                                        setPhase(31);
                                    }
                                }
                            } else {
                                if (getPhase() == 37) {
                                    switch (index) {
                                        case 1 -> {
                                            player.inventory().remove(new Item(6570));
                                            player.inventory().addOrDrop(new Item(6529, 8000));
                                            player.getInterfaceManager().close();
                                        }
                                        case 2 -> {
                                            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "No, I'd like to keep my cape");
                                            setPhase(39);
                                        }
                                        case 3 -> {
                                            send(DialogueType.OPTION, "Sacrifice your firecape for a chance at TzRek-Jad?", "Yes, I know I won't get my cape back.", "No, I like my cape!");
                                            setPhase(38);
                                        }
                                    }
                                } else {
                                    if (getPhase() == 38) {
                                        switch (index) {
                                            case 1 -> {
                                                if(!player.inventory().contains(6570)) {
                                                    stop();
                                                    return;
                                                }
                                                player.inventory().remove(new Item(6570));
                                                boolean roll = Utils.securedRandomChance(1.0F);
                                                boolean receivedPet = false;
                                                if (receivedPet) {
                                                    send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "You lucky. Better train him good else TzTok-Jad find", "you JalYt.");
                                                } else {
                                                    send(DialogueType.NPC_STATEMENT, NpcIdentifiers.TZHAARMEJJAL, Expression.DEFAULT, "You not lucky. Maybe next time, JalYt.");
                                                }
                                                setPhase(39);
                                            }
                                            case 2 -> {
                                                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "No, I like my cape!");
                                                setPhase(39);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
