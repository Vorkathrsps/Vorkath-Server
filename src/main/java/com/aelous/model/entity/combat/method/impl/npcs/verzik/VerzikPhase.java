package com.aelous.model.entity.combat.method.impl.npcs.verzik;

import com.aelous.model.entity.Entity;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

public enum VerzikPhase {

    P0(8371, 8371, 8110) {
        @Override
        public void switchPhase(VerzikVitur verzikVitur) {
            verzikVitur.lock();
            Chain.bound(verzikVitur).runFn(10, () -> {
                verzikVitur.animate(phaseSwitchAnimID);
            }).then(2, () -> {
                verzikVitur.animate(8111);
            }).then(3, () -> {
                verzikVitur.animate(-1);
                verzikVitur.transmog(phaseSwitchID);
            });
            final Tile center = new Tile(3168, 4314);
            verzikVitur.getRouteFinder().routeAbsolute(center.x, center.y);
            Chain.bound(verzikVitur).runFn(9, () -> {
                verzikVitur.transmog(phaseID);
            }).then(2, verzikVitur::unlock);
        }
    },

    P1(8371, 10833, 8111) {
        @Override
        public void switchPhase(VerzikVitur verzikVitur) {
            verzikVitur.animate(phaseSwitchAnimID);
        }
    },

    P2(10833, 10834, 8118) {
        @Override
        public void switchPhase(VerzikVitur verzikVitur) {
            verzikVitur.lock();
            verzikVitur.transmog(phaseSwitchID);
            verzikVitur.animate(phaseSwitchAnimID);
            Chain.bound(verzikVitur).runFn(3, () -> {
                verzikVitur.transmog(10835);
                verzikVitur.animate(8214);
                verzikVitur.forceChat("Behold my true nature!");
            }).then(2, verzikVitur::unlock);
        }
    };


    public Entity entity, target;

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

    VerzikPhase(int phaseSwitchID, int phaseID, int phaseSwitchAnimID) {
        this.phaseSwitchID = phaseSwitchID;
        this.phaseID = phaseID;
        this.phaseSwitchAnimID = phaseSwitchAnimID;
    }

    public abstract void switchPhase(VerzikVitur verzikVitur);
}
