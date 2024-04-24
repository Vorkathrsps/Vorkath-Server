package com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon;

import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.PerilOfMoonInstance;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.area.BlueMoonArea;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.state.BlueMoonState;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.List;
import java.util.function.BooleanSupplier;

public class BlueMoonNPC extends NPC {
    PerilOfMoonInstance instance;
    int intervalCount = 0;
    int attackInterval = 6;
    int attackCount = 0;

    public BlueMoonNPC(int id, Tile tile, PerilOfMoonInstance instance) {
        super(id, tile);
        this.instance = instance;
        BooleanSupplier cancel = this::checkState;
        this.setVisibleMenuOptions(false, false, false, false, false);
        Chain.noCtxRepeat().repeatingTask(1, initiate -> this.waitUntil(cancel, () -> {
            initiate.stop();
            this.unlock();
            this.instance.setState(BlueMoonState.STAGE_1);
        }));
    }

    @Override
    public void combatSequence() {
        if (this.dead()) return;

        if (BlueMoonState.DOCILE.equals(this.instance.getState())) {
            heal();
            return;
        }

        if (this.locked()) return;

        interpolateStage();

        this.intervalCount++;
        this.attackInterval--;
        if (intervalCount >= 6 && attackInterval <= 0 && !this.dead()) {
            this.attackCount++;
            this.intervalCount = 0;
            this.attackInterval = 6;
            enraged();
        }
    }

    private void interpolateStage() {
        int currentState = this.instance.getState().ordinal();
        if (BlueMoonState.INDEX.containsKey(currentState)) {
            int currentIndex = BlueMoonState.INDEX.get(currentState);
            int nextIndex = currentIndex + 1;
            if (nextIndex < this.instance.getCircleNpcs().length && attackCount >= 4) {
                this.instance.getCircleNpcs()[currentIndex].hidden(true);
                this.instance.getCircleNpcs()[nextIndex].hidden(false);
                this.instance.setState(this.instance.getState().nextStage());
                this.attackCount = 0;
            } else if (nextIndex >= this.instance.getCircleNpcs().length && attackCount >= 4) {
                reset(currentIndex);
            }
        }
    }

    private void reset(int currentIndex) {
        this.setPositionToFace(new Tile(1, 1));
        this.setVisibleMenuOptions(false, false, false, false, false);
        this.instance.getCircleNpcs()[currentIndex].hidden(true);
        this.instance.setState(BlueMoonState.DOCILE);
        this.attackCount = 0;
        Chain.noCtxRepeat().repeatingTask(1, docile -> {

        });
    }

    final void enraged() {
        Player randomTarget = Utils.randomElement(this.instance.getPlayers());
        this.face(randomTarget);
        this.animate(11014);
        for (var player : this.instance.getPlayers()) {
            if (player == null || !BlueMoonArea.ROOM.transformArea(0, 0, 0, 0, instance.getzLevel()).contains(player.tile())) continue;
            handleHit(player, 2);
            handleHit(player, 3);
            handleHit(player, 4);
        }
    }

    final void handleHit(Player player, int delay) {
        int currentIndex = BlueMoonState.INDEX.getOrDefault(this.instance.getState().ordinal(), -1);
        if (currentIndex == -1) return;
        new Hit(this, player, delay, CombatType.MELEE)
            .checkAccuracy(true)
            .submit()
            .postDamage(hit -> {
                if (hit.isAccurate() && hit.getDamage() > 0 && player.tile().inBounds(this.instance.getCircleNpcs()[currentIndex].bounds())) {
                    hit.setDamage(hit.getDamage() / 2);
                }
            });
    }

    final void heal() {
        intervalCount++;
        attackInterval--;
        if (intervalCount >= 6 && attackInterval <= 0 && !this.dead()) {
            this.intervalCount = 0;
            this.attackInterval = 6;
            this.healHit(this, Utils.random(1, 5));
        }
    }

    final boolean checkState() {
        List<NPC> tornadoList = this.instance.getTornadoList();
        if (tornadoList.isEmpty()) {
            this.instance.getCircleNpcs()[0].hidden(false);
            this.setPositionToFace(this.instance.getCircleNpcs()[0].tile());
            this.setVisibleMenuOptions(false, true, false, false, false);
            return true;
        }
        return false;
    }
}
