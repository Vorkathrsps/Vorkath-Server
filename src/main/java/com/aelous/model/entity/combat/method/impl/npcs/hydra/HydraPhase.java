package com.aelous.model.entity.combat.method.impl.npcs.hydra;

import com.aelous.utility.chainedwork.Chain;

/**
 * The hydra phases.
 *
 * @author Gabriel || Wolfsdarker
 */
public enum HydraPhase {

    /**
     * The first phase with poison special.
     */
    GREEN(8616, 8615, 8237) {
        @Override
        public void switchPhase(AlchemicalHydra hydra) {
            hydra.lock();
            hydra.transmog(phaseSwitchID);
            hydra.animate(phaseSwitchAnimID);
            Chain.bound(hydra).runFn(3, () -> {
                hydra.animate(phaseSwitchAnimID + 1);
                hydra.transmog(BLUE.phaseID);
            }).then(2, hydra::unlock);
        }
    },

    /**
     * The second phase with lightning special.
     */
    BLUE(8617, 8619, 8244) {
        @Override
        public void switchPhase(AlchemicalHydra hydra) {
            hydra.lock();
            hydra.transmog(phaseSwitchID);
            hydra.animate(phaseSwitchAnimID);
            Chain.bound(hydra).runFn(2, () -> {
                hydra.animate(phaseSwitchAnimID + 1);
                hydra.transmog(RED.phaseID);
            }).then(2, hydra::unlock);
        }
    },

    /**
     * The third phase with fire special.
     */
    RED(8618, 8620, 8251) {
        @Override
        public void switchPhase(AlchemicalHydra hydra) {
            hydra.lock();
            hydra.transmog(phaseSwitchID);
            hydra.animate(phaseSwitchAnimID);
            Chain.bound(hydra).runFn(3, () -> {
                hydra.animate(phaseSwitchAnimID + 1);
                hydra.transmog(ENRAGED.phaseID);
            }).then(2, hydra::unlock);
        }
    },

    /**
     * The final phase, stronger and with poison special
     */
    ENRAGED(8622, 8621, 8262) {
        @Override
        public void switchPhase(AlchemicalHydra hydra) {

        }
    },

    ;

    /**
     * The NPC ID for the phase switching moment.
     */
    public int phaseSwitchID;

    /**
     * The NPC ID for the phase already switched.
     */
    public int phaseID;

    /**
     * The animation of switching phases.
     */
    public int phaseSwitchAnimID;

    /**
     * Constructor for the phase.
     */
    HydraPhase(int phaseSwitchID, int phaseID, int phaseSwitchAnimID) {
        this.phaseSwitchID = phaseSwitchID;
        this.phaseID = phaseID;
        this.phaseSwitchAnimID = phaseSwitchAnimID;
    }

    /**
     * Called upon phase switch.
     */
    public abstract void switchPhase(AlchemicalHydra hydra);

}
