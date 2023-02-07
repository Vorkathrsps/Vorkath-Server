package com.aelous.model.content.areas.burthope.warriors_guild.dialogue;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class HarrallakMenarous {
    //TODO
    /*@JvmStatic @ScriptMain fun register(r: ScriptRepository) {
        r.onNpcOption1(HARRALLAK_MENAROUS) @Suspendable {
            it.chatNpc("Welcome to my humble guild ${it.player().name()}.", HARRALLAK_MENAROUS, 567)
            when (it.options("Quite a place you've got here.", "You any good with a sword?", "Bye!")) {
                1 -> {
                    it.chatPlayer("Quite a place you've got here.", 567)
                    it.chatNpc("Indeed we do. Would you like to know more about it?", HARRALLAK_MENAROUS, 567)
                    tell_me_about(it)
                }
                2 -> {
                    it.chatPlayer("You any good with a sword?", 554)
                    it.chatNpc("Am I any good with....a sword... Have you any clue<br>who I am?", HARRALLAK_MENAROUS, 572)
                    it.chatPlayer("Not really... no.", 588)
                    it.chatNpc("Why I could best any person alive in a rapier duel!", HARRALLAK_MENAROUS, 567)
                    it.chatPlayer("Try me then!", 554)
                    it.chatNpc("My dear man, I couldn't possibly duel you, I might<br>hurt you and then what would happen to my<br>reputation!" +
                        " Besides, I have this wonderful guild to run.<br>Why don't you take a look at the various activities we", HARRALLAK_MENAROUS, 570)
                    it.chatNpc("have. You might even collect enough tokens to be<br>allowed in to kill the strange beasts from the east!", HARRALLAK_MENAROUS, 568)
                    tell_me_about(it)
                }
                3 -> bye(it)
            }
        }
    }


    @Suspendable fun tell_me_about(it: Script) {
        when (it.options("Tell me about the strength training area.", "Tell me about the attack training area.",
            "Tell me about the defence training area.", "Tell me about the combat training area.", "Tell me about tokens.")) {
            1 -> strength(it)
            2 -> attack(it)
            3 -> defence(it)
            4 -> combat_training_area(it)
            5 -> tokens(it)
        }
    }

    @Suspendable fun strength(it: Script) {
        it.chatPlayer("Tell me about the strength training area.", 588)
        it.chatNpc("Ahh the mighty warrior Sloane guards the strength<br>training area. This intriguing little area consists of two<br>" +
            "shotput lanes for different weights of shot. It's fairly<br>simple, the Referee or Sloane can explain more. You", HARRALLAK_MENAROUS, 570)
        it.chatNpc("may find yourself panting for breath though.", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("Oh? Why?", 554)
        it.chatNpc("Well you see my dear boy, the games there really do<br>sap your energy quite a bit. You can find it just up<br>the stairs behind the bank.", HARRALLAK_MENAROUS, 570)
        when (it.options("Tell me about the attack training area.", "Tell me about the defence training area.", "Tell me about the combat training area.", "Tell me about tokens.", "Bye!")) {
            1 -> attack(it)
            2 -> defence(it)
            3 -> combat_training_area(it)
            4 -> tokens(it)
            5 -> bye(it)
        }
    }

    @Suspendable fun attack(it: Script) {
        it.chatPlayer("Tell me about the attack training area.", 588)
        it.chatNpc("Ahhh, dummies.", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("I'm no dummy, I just want to know what is there!", 575)
        it.chatNpc("Oh no my dear man, I did not mean you at all! The<br>training area has mechanical dummies which pop up out<br>of holes in the floor. The noble dwarf Gamfred " +
            "invented<br>the mechanism and Ajjat can explain more about what", HARRALLAK_MENAROUS, 570)
        it.chatNpc("to do there.", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("Oh, ok, I'll have to try it out!", 567)
        it.chatNpc("You can find it just down the corridor and on your<br>right.", HARRALLAK_MENAROUS, 568)
        when (it.options("Tell me about the strength training area.", "Tell me about the defence training area.", "Tell me about the combat training area.", "Tell me about tokens.", "Bye!")) {
            1 -> strength(it)
            2 -> defence(it)
            3 -> combat_training_area(it)
            4 -> tokens(it)
            5 -> bye(it)
        }
    }

    @Suspendable fun defence(it: Script) {
        it.chatPlayer("Tell me about the defence training area.", 588)
        it.chatNpc("To polish your defensive skills to the very highest level<br>we've employed a most inventive dwarf and a catapult.", HARRALLAK_MENAROUS, 568)
        it.chatPlayer("You're going to throw dwarves at me?", 554)
        it.chatNpc("Oh my no! I think Gamfred would object to that most<br>strongly.", HARRALLAK_MENAROUS, 606)
        it.chatNpc("He's an inventor you see and has built a marvellous<br>contraption that can throw all sorts of things at you<br>including magic missiles...", HARRALLAK_MENAROUS, 569)
        it.chatPlayer("Mmmm?", 588)
        it.chatNpc("....spiked iron balls.....", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("Err....", 588)
        it.chatNpc("....spinning slashing blades....", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("Ummm...", 596)
        it.chatNpc("... and even anvils.", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("ANVILS!?", 571)
        it.chatNpc("No need to be afraid, it's all under very controlled<br>conditions! You can find it just up the stairs behind the<br>bank.", HARRALLAK_MENAROUS, 569)
        when (it.options("Tell me about the strength training area.", "Tell me about the attack training area.", "Tell me about the combat training area.", "Tell me about tokens.", "Bye!")) {
            1 -> strength(it)
            2 -> attack(it)
            3 -> combat_training_area(it)
            4 -> tokens(it)
            5 -> bye(it)
        }
    }

    @Suspendable fun combat_training_area(it: Script) {
        it.chatPlayer("Tell me about the combat training area.", 588)
        it.chatNpc("Ahh yes, our resident magician from foreign lands<br>created a most amazing gadget which can turn your<br>own armour against you! It's really quite intriguing.", HARRALLAK_MENAROUS, 569)
        it.chatPlayer("That sounds dangerous. What if I'm wearing it at the<br>time?", 555)
        it.chatNpc("So far that's not happened. You need to speak to<br>Shanomi about the specifics of the process, but as I<br>understand it, putting a suit of armour in one of" +
            " these<br>devices will make it come to life some how. The better", HARRALLAK_MENAROUS, 608)
        it.chatNpc("the armour, the harder it is to 'kill'.", HARRALLAK_MENAROUS, 605)
        it.chatPlayer("Fighting my own armour, that sounds... weird. I could<br>be killed by it...", 606)
        it.chatNpc("Indeed we have had a few fatalities from warriors over<br>stretching themselves and not knowing their limits. Start<br>small and work up is my motto! That and go see" +
            " Lidio<br>for some food if you need it.", HARRALLAK_MENAROUS, 613)
        it.chatPlayer("Ok, thanks for the warning.", 567)
        when (it.options("Tell me about the strength training area.", "Tell me about the attack training area.", "Tell me about the defence training area.", "Tell me about tokens.", "Bye!")) {
            1 -> strength(it)
            2 -> attack(it)
            3 -> defence(it)
            4 -> tokens(it)
            5 -> bye(it)
        }
    }

    @Suspendable fun tokens(it: Script) {
        it.chatPlayer("Tell me about tokens.", 588)
        it.chatNpc("Ahh, yes, our token system is designed to allow you an<br>appropriate amount of time with my discovery in the<br>very top floor of the guild. I won't spoil the surprise" +
            " as<br>to what that is. Go up and see for yourself. Now, the", HARRALLAK_MENAROUS, 570)
        it.chatNpc("amount of tokens you collect from the various activities<br>around the guild will dictate how long Kamfreena will<br>allow you in the enclosure on the very top floor." +
            " More<br>tokens equals more time.", HARRALLAK_MENAROUS, 570)
        it.chatPlayer("So what's up there?", 554)
        it.chatNpc("If I told you it would spoil the surprise!", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("Ok ok... so how do I earn and claim these tokens?", 554)
        it.chatNpc("You can earn them simply by using the training<br>exercises around the guild, the staff will then enter this<br>into a ledger as you play. You can claim them by<br>simply" +
            " asking any of the staff at the training areas, or", HARRALLAK_MENAROUS, 570)
        it.chatNpc("myself.", HARRALLAK_MENAROUS, 567)
        it.chatPlayer("Sounds easy enough.", 567)
        when (it.options("Tell me about the strength training area.", "Tell me about the attack training area.", "Tell me about the defence training area.", "Tell me about the combat training area.", "Bye!")) {
            1 -> strength(it)
            2 -> attack(it)
            3 -> defence(it)
            4 -> combat_training_area(it)
            5 -> bye(it)
        }
    }

    @Suspendable fun bye(it: Script) {
        it.chatPlayer("Bye!", 567)
        it.chatNpc("Farewell brave warrior, I do hope you enjoy my guild.", HARRALLAK_MENAROUS, 588)
    }*/
}
