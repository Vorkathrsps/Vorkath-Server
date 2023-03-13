package com.aelous.model.inter.lootkeys;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.utility.Utils;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;
import static com.aelous.model.entity.attributes.AttributeKey.*;

public class SkullySettingsD extends Dialogue {

    private void base() {
        boolean lootKeysActive = player.<Boolean>getAttribOr(LOOT_KEYS_ACTIVE, false);
        String option1 = lootKeysActive ? "Turn loot keys off" : "Turn loot keys on";

        boolean lootKeysDropConsumables = player.<Boolean>getAttribOr(LOOT_KEYS_DROP_CONSUMABLES, false);
        String option2 = lootKeysDropConsumables ? "Send food to loot key" : "Drop food to floor";

        boolean sendValuableItemsToLootKey = player.<Boolean>getAttribOr(SEND_VALUABLES_TO_LOOT_KEYS, false);
        String option3 = sendValuableItemsToLootKey ? "Drop valuables to floor" : "Send valuables to loot key";

        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, option1, option2, option3, "Change valuable item threshold");
        setPhase(0);
    }

    @Override
    protected void start(Object... parameters) {
        boolean unlocked = player.<Boolean>getAttribOr(LOOT_KEYS_UNLOCKED, false);
        if(unlocked) {
            base();
        } else {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "You do not have access to the loot keys.");
            setPhase(3);
        }
    }

    //Doesnt even triger when opening the dialogue its defo something in ur client
    //i think it has too do with getphase lmao, because other next() for dailogues work ur dialogue doesnt even open unless u press
    @Override
    protected void next() {
        switch (getPhase()) {
            case 1 -> base();
            case 2 -> {
                stop();
                player.setAmountScript("Enter Amount:", value -> {
                    int input = (Integer) value;
                    if (input <= 0) {
                        DialogueManager.npcChat(player, Expression.NODDING_ONE, SKULLY, "Hump. Yeah, real funny, wise guy.");
                    }
                    if (input > Integer.MAX_VALUE) {
                        input = Integer.MAX_VALUE;
                    }
                    player.putAttrib(LOOT_KEYS_VALUABLE_ITEM_THRESHOLD, input);
                    DialogueManager.npcChat(player, Expression.NODDING_ONE, SKULLY, "Ok, now items worth at least " + Utils.formatRunescapeStyle(input) + "gp will drop to", "the floor.");
                    return true;
                });
            }
            case 3 -> stop();
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(0)) {
            if(option == 1) {
                boolean lootKeysActive = player.<Boolean>getAttribOr(LOOT_KEYS_ACTIVE, false);
                if(!lootKeysActive) {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ok, you'll now get loot keys when you kill another", "person in the Wilderness.");
                    setPhase(1);
                    player.putAttrib(LOOT_KEYS_ACTIVE, true);
                } else {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ok, whenever you kill someone else now, their items will", "go to the floor like normal. Just remember, they could", "have loots keys on them, and they'll still go to your", "inventory!");
                    setPhase(1);
                    player.putAttrib(LOOT_KEYS_ACTIVE, false);
                }
            }
            if(option == 2) {
                boolean lootKeysDropConsumables = player.<Boolean>getAttribOr(LOOT_KEYS_DROP_CONSUMABLES, false);
                if(!lootKeysDropConsumables) {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ok, food and potions will now be dropped to the floor", "instead of stored in a loot key.");
                    setPhase(1);
                    player.putAttrib(LOOT_KEYS_DROP_CONSUMABLES, true);
                } else {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ok, food and potions will now be sent to your loot keys.");
                    setPhase(1);
                    player.putAttrib(LOOT_KEYS_DROP_CONSUMABLES, false);
                }
            }
            if(option == 3) {
                boolean sendValuableItemsToLootKey = player.<Boolean>getAttribOr(SEND_VALUABLES_TO_LOOT_KEYS, false);
                if(!sendValuableItemsToLootKey) {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ok, valuable items will now be sent to your loot keys.");
                    setPhase(1);
                    player.putAttrib(SEND_VALUABLES_TO_LOOT_KEYS, true);
                } else {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ok, valuable items will now be dropped to the floor", "instead of stored in a loot key.");
                    setPhase(1);
                    player.putAttrib(SEND_VALUABLES_TO_LOOT_KEYS, false);
                }
            }
            if(option == 4) {
                long lootKeysValuableItemThreshold = player.<Integer>getAttribOr(LOOT_KEYS_VALUABLE_ITEM_THRESHOLD, 150_000);
                boolean sendValuableItemsToLootKey = player.<Boolean>getAttribOr(SEND_VALUABLES_TO_LOOT_KEYS, false);
                if (!sendValuableItemsToLootKey) {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Currently, items worth " + Utils.formatRunescapeStyle(lootKeysValuableItemThreshold) + " gp or more will be", "dropped to the floor. What do you want to change that", "value to?");
                    setPhase(2);
                } else {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Currently, if you were to have valuable items drop to", "the floor, they'd need to be worth at least "+Utils.formatRunescapeStyle(lootKeysValuableItemThreshold)+" gp.", "What do you want to change that value to?");
                    setPhase(2);
                }
            }
        }
    }
}
