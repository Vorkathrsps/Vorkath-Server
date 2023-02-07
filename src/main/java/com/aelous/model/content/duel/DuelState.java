package com.aelous.model.content.duel;

/**
 * @author Patrick van Elderen | January, 27, 2021, 21:45
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum DuelState {
    NONE,
    ENDING_DUEL,
    REQUESTED_DUEL,
    DUEL_SCREEN,
    ACCEPTED_DUEL_SCREEN,
    CONFIRM_SCREEN,
    ACCEPTED_CONFIRM_SCREEN,
    STARTING_DUEL,
    IN_DUEL
}
