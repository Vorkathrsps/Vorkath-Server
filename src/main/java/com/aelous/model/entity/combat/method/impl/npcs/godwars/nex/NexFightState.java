package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

/**
 * @author Patrick van Elderen <<a href="https://github.com/PVE95">...</a>>
 * @Since October 20, 2022
 */
public enum NexFightState {

    /**
     * Nex's first phase, the smoke phase, starts when she shouts Fill my soul with smoke! During this phase, Nex will use Smoke Rush against players,
     * which can poison. This is the only phase where her basic magic attacks do not drain prayer. All players should stand within melee range of her, a strategy
     * known as melee distancing (MD), which will cause her to use her melee attack which only targets one player, as opposed to her magic attack which targets multiple
     * players and deals more total damage. While in melee distance, the player being targeted by Nex may use Protect from Melee to reduce the overall damage taken, and
     * then immediately switch to Protect from Magic once Nex switches targets.
     */
    SMOKE_PHASE(1),

    /**
     * Nex's second phase, the shadow phase, starts when she shouts Darken my shadow! During this phase, Nex will shoot shadow shots which are considered as Ranged
     * attacks. Having Protect from Missiles active will cut the damage taken in half. Successful hits can drain prayer points slightly, which can be reduced by the
     * spectral spirit shield.
     * Players will take more damage from the shadow shots the closer to Nex they are, up to 30 with Protect from Missiles, so it is advised players keep their distance
     * and maintain high hitpoints at all times on this phase in case Nex happens to glide immediately next to players and begin attacking right after.
     */
    SHADOW_PHASE(2),

    /**
     * Nex's third phase, the blood phase, starts when she shouts Flood my lungs with blood! During this phase, Nex will use Blood Barrage against players, which heals
     * her a percentage of the damage dealt and drain prayer based on the damage dealt which can be reduced by the spectral spirit shield.
     * Unlike the rest of the fight, this attack only targets one player, although it has a 3x3 AoE centred on the current target. The player targeted by her should move
     * away from other players to reduce damage and effectively Nex's healing.
     */
    BLOOD_PHASE(3),

    /**
     * Nex's fourth phase, the ice phase, starts when she shouts Infuse me with the power of ice! During this phase, Nex will use Ice Barrage against players, which will
     * freeze them if they are not praying Protect from Magic and lowers their prayer points by half of the damage she deals, reduced to one third by the spectral spirit
     * shield. Players should stand in melee distance (MD) similarly to the Smoke and Zaros phases. Players should be wary of the 'Containment' special attack when standing
     * in melee distance, especially after Glacies has been activated to avoid being frozen in place.
     */
    ICE_PHASE(4),

    /**
     * Nex's fifth and final phase, the Zaros phase, starts when she shouts NOW, THE POWER OF ZAROS! During this phase, Nex will call on Zaros' power, healing her for 500
     * hitpoints, and will only use her normal magic and melee attacks. Her magic attacks drain 5 prayer points if they hit, which can be reduced with the use of the
     * spectral spirit shield. Similarly to the smoke and ice phases, all players should run up to Nex to reduce the team's overall damage taken. Players with the lowest
     * crush defence bonus are targeted more frequently by her melee attack.
     */
    ZAROS_PHASE(5);

    public final int fightState;

    NexFightState(final int fightState) {
        this.fightState = fightState;
    }
}
