package com.aelous.model.inter.lootkeys;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.utility.Utils;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;
import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;

public class SkullyD extends Dialogue {

    //All copy psted? yes only difference was the um syntax shit

    @Override
    protected void start(Object... parameters) {//Ur parameters are different from mine
        send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Eyup. They call me Skully. I run this Wilderness Loot", "Chest. What can I do for you?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            boolean unlocked = player.<Boolean>getAttribOr(LOOT_KEYS_UNLOCKED, false);
            String option = unlocked ? "Can I change how these loot keys work?" : "Can I have access to the chest?";
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "How does the chest work?", option, "How much loot have I claimed?", "Who are you, exactly?", "Goodbye.");
            setPhase(1);//Have fun XD u need to reconfigure dialogue params urs are diff from mine I redid them
        } else if(isPhase(2)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Yeah?");
            setPhase(3);
        } else if(isPhase(3)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, " So, I made an enchantment that teleports the loot to", "another dimension instead!");
            setPhase(4);
        } else if(isPhase(4)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "How does that help?");
            setPhase(5);
        } else if(isPhase(5)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Well, instead of getting a pile of rubbish, you get a", "single key. You just take the key to this chest and", "BAM! There's your loot!");
            setPhase(6);
        } else if(isPhase(6)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "So the loot is safe in this other dimension?");
            setPhase(7);
        } else if(isPhase(7)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Eh, seems like it. As long as they've had the key, I've", "never had anyone unable to get the stuff back out.", "Though if you lose the key, you can't get the stuff out.");
            setPhase(8);
        } else if(isPhase(8)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Oh, and be careful running about with too many keys", "on you. Other people will know you've got them, and", "I'm not gonna stop them opening the chest!");
            setPhase(9);
        } else if(isPhase(9)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "So if I kill someone and take their keys, I can use them", "myself?");
            setPhase(10);
        } else if(isPhase(10)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "...Eh, if you wanna think about it that way, sure.");
            setPhase(11);
        } else if(isPhase(11)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "I got nothing better to do, so I'll keep track of the", "value of the stuff you unlock.");
            setPhase(12);
        } else if(isPhase(12)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "And anything you don't want, you can have the chest", "destroy it.");
            setPhase(13);
        } else if(isPhase(13)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Except the food. I'll eat that instead.");
            setPhase(14);
        } else if(isPhase(14)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Now, if you want your own keys instead of relying on", "the misfortune of others, I can put the enchantment on", "you as well, but it's gonna cost ya.");
            setPhase(15);
        } else if(isPhase(15)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "How does the chest work?", "Can I have access to the chest?", "How much loot have I claimed?", "Who are you, exactly?", "Goodbye.");
            setPhase(1);
        } else if(isPhase(16)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "So, if you have keys from someone else, you're more", "than welcome to put them in the chest and see what", "comes out.");
            setPhase(17);
        } else if(isPhase(17)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, " If you want the enchantment that makes the keys, I'm", "afraid it's gonna cost you 25k blood money.");
            setPhase(18);
        } else if(isPhase(18)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Sure, I'll pay for that.", "Not right now, thanks.", "How much?!");
            setPhase(19);
        } else if(isPhase(20)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Are you sure? 25k blood money is a lot, and you won't", "get them back!");
            setPhase(21);
        } else if(isPhase(21)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes, pay 25k blood money!", "No, I've changed my mind!");
            setPhase(22);
        } else if(isPhase(23)) {
            stop();
        } else if(isPhase(24)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Fair enough mate, want to talk about something else?");
            setPhase(15); // Shows initial options
        } else if(isPhase(25)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "25k blood money.");
            setPhase(26);
        } else if ((isPhase(26))) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Why do you charge so much?!");
            setPhase(27);
        } else if(isPhase(27)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Runes. People don't wanna deliver across the Wilderness,", "and I'm in no shape to go to them, so", "I have to pay through the nose.");
            setPhase(28);
        } else if(isPhase(28)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "I make maybe a couple hundred blood money of profit, which", "at least lets me buy a few beers.");
            setPhase(29);
        } else if ((isPhase(29))) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Why is the beer so cheap if it's so expensive to get", "deliveries out here?");
            setPhase(30);
        } else if(isPhase(30)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "The barman makes it himself with whatever scraps he can", "get his hands on.");
            setPhase(31);
        } else if(isPhase(31)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "You'd think it'd turn out horrible, but compared to every", "other 2 coin beer out there, it's not half bad.");
            setPhase(32);
        } else if(isPhase(32)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "But enough about beer. If you want access to the loot keys,", "it's gonna cost you 25k blood money.");
            setPhase(33);
        } else if(isPhase(33)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes, pay 25k blood money!", "No, I've changed my mind!");
            setPhase(22);
        } else if(isPhase(34)) {
            int keysLooted = player.<Integer>getAttribOr(LOOT_KEYS_LOOTED, 0);
            long totalLootKeysValue = player.<Long>getAttribOr(TOTAL_LOOT_KEYS_VALUE, 0L);
            if(keysLooted == 0) {
                send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Well, seeing as you haven't claimed a key yet, I reckon", "you've claimed a total of 0gp worth of loot.");
                setPhase(15); // Shows initial options
            } else {
                send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "You've claimed "+keysLooted+" keys, containing loot worth about "+ Utils.formatRunescapeStyle(totalLootKeysValue)+" gp.");
                setPhase(15); // Shows initial options
            }
        } else if(isPhase(35)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ah, used to be an adventurer like you, I guess. Used to", "roam the world, killing monsters for a living. 'Cept then,", "I discovered the Wilderness.");
            setPhase(36);
        } else if(isPhase(36)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Used to take on anyone and everyone who stood in me", "way, and made myself a small fortune doing it too.");
            setPhase(37);
        } else if(isPhase(37)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "It's how I got the name, used to go anywhere and", "everywhere with a skull over my head!");
            setPhase(38);
        } else if(isPhase(38)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "So how'd you end up like... this?");
            setPhase(39);
        } else if(isPhase(39)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ah, you live a life feeding on others, eventually", "something higher up the food chain finds you.");
            setPhase(40);
        } else if(isPhase(40)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, " I used to have a hidden outpost out near the Bone", "Yard, somewhere to keep my stuff in an emergency.", "One day all I come back to is a smouldering pile of", "rubble.");
            setPhase(41);
        } else if(isPhase(41)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Next thing I know, someone's snuck up behind me", "and has put a spear through my back.");
            setPhase(42);
        } else if(isPhase(42)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "And you survived?!");
            setPhase(43);
        } else if(isPhase(43)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Always carry a ring of life, friend! Never know when", "you'll need it.");
            setPhase(44);
        } else if(isPhase(44)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Still, the damage was done. Everything I ever owned", "up in smoke, and even though the wound's fully healed,", "I can still feel that spear in my back to this day.");
            setPhase(45);
        } else if(isPhase(45)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Thankfully, Ferox was kind enough to take me in, and", "I managed to repurpose some of my old storage magic", "for adventurers like you.");
            setPhase(46);
        } else if(isPhase(46)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "But why come back to the Wilderness?");
            setPhase(47);
        } else if(isPhase(47)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "What, go life a normal life behind a shop counter so I", "can pedal swamp paste for a living instead? I'll pass!");
            setPhase(48);
        } else if(isPhase(48)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Anyhow, enough about me. What can I do for you?");
            setPhase(15); // Shows initial options
        } else if(isPhase(49)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "I'll be here if you need anything.");
            setPhase(23);
        } else if(isPhase(50)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Well, I can turn the enchantment that creates the loot", "keys on and off for you. Won't cost you anything to", "turn it back on again, either.");
            setPhase(51);
        } else if(isPhase(51)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "If you'd like it so stuff like food and potions stay out of", "the keys so you can use them yourself, I can do that", "too.");
            setPhase(52);
        } else if(isPhase(52)) {
            send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "And if you like the feeling of picking up valuables, I can", "make it so that anything above a certain value hits the", "floor as well.");
            setPhase(53);
        } else if(isPhase(53)) {
            stop(); // Stop this dialogue and open the settings dialogue
            player.getDialogueManager().start(new SkullySettingsD());
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(1)) {
            if(option == 1) {
                send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Well, you know how when you kill someone all of their", "stuff just ends up in a pile on the floor and it's really", "hard to pick it all up quickly?");
                setPhase(2);
            }
            if(option == 2) {
                boolean unlocked = player.<Boolean>getAttribOr(LOOT_KEYS_UNLOCKED, false);
                if(unlocked) {
                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Can I change how these loot keys work?");
                    setPhase(50);
                } else {
                    send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Can I have access to the chest?");
                    setPhase(16);
                }
            }
            if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "How much loot have I claimed?");
                setPhase(34);
            }
            if(option == 4) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Who are you, exactly?");
                setPhase(35);
            }
            if(option == 5) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Goodbye.");
                setPhase(49);
            }
        } else if(isPhase(19)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Sure, I'll pay for that.");
                setPhase(20);
            }
            if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Not right now, thanks.");
                setPhase(24);
            }
            if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "How much?!");
                setPhase(25);
            }
        } else if(isPhase(22)) {
            if(option == 1) {
                if(!player.inventory().contains(BLOOD_MONEY, 25_000)) {
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Hmm... I don't think you have enough blood money on you there,", "mate.");
                    setPhase(23);
                } else {
                    player.inventory().remove(BLOOD_MONEY, 25_000);
                    send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Ok, let me just... and a bit of... and we're done! You'll", "now get loot keys whenever you kill someone in the", "Wilderness. Talk to me again if you have any", "questions.");
                    player.putAttrib(LOOT_KEYS_UNLOCKED,true);
                    setPhase(23);
                }
            }
            if(option == 2) {
                send(DialogueType.NPC_STATEMENT, SKULLY, Expression.DEFAULT, "Fair enough mate, talk to me again if you change your mind.");
                setPhase(23);
            }
        }
    }
}
