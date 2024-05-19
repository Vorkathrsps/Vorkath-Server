package com.cryptic.model.entity.combat.method.impl.npcs.vasilas;


import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatreofblood.TheatreInstance;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class VasiliasCombat extends CommonCombatMethod {
    private boolean isTransforming;
    private NylocasSize size;
    private Form[] forms;
    private Queue<Form> transformationsQueue;
    private Form form;
    TheatreInstance theatreInstance;

    @Override
    public void init(NPC npc) {
        this.theatreInstance = npc.getTheatreInstance();
        size = NylocasSize.BOSS;
        form = Form.MELEE;
        forms = new Form[]{Form.MELEE, Form.RANGED, Form.MAGIC};
    }

    public void transform(NPC npc) {
        if (transformationsQueue == null || transformationsQueue.isEmpty()) {
            transformationsQueue = new LinkedList<>(Arrays.asList(forms));
        }

        Form transformation = transformationsQueue.poll();
        if (transformation != null) {
            setTransformation(npc, transformation);
            isTransforming = false;
        } else {
            System.out.println("NYLOCAS ERROR: Transformation is null");
        }
    }

    private void setTransformation(NPC npc, Form form) {
        this.form = form;
        npc.transmog(form.getNpcIdBySize(size), false);
    }

    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        var combatInfo = mob.npc().getCombatInfo();
        var player = (Player) target;
        if (isTransforming) {
            return false;
        }
        if (form == null) return false;
        switch (form.getCombatType()) {
            case MAGIC -> {
                if (player.isPlayer()) {
                    Arrays.stream(mob.closePlayers(9)).forEach(p -> {
                        int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                        mob.animate(animationId);
                        double distance = entity.tile().distanceTo(p.tile());
                        int duration = (int) (25 + 20 + distance);
                        Tile nyloTile = entity.tile().center(entity.getSize());
                        Projectile projectile = new Projectile(nyloTile, p, 1610, 25, duration, 12, 16, 24, entity.getSize(), 48, 0);
                        projectile.send(entity, p);
                        int delay = (int) (projectile.getSpeed() / 20D);
                        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
                    });
                }
            }
            case MELEE -> {
                if (player.isPlayer()) {
                    if (!isReachable()) {
                        transform(this.entity.npc());
                        return true;
                    }
                    int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                    mob.animate(animationId);
                    new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
                }
            }
            case RANGED -> {
                if (player.isPlayer()) {
                    Arrays.stream(mob.closePlayers(9)).forEach(p -> {
                        int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                        mob.animate(animationId);
                        double distance = entity.tile().distanceTo(p.tile());
                        int duration = (int) (39 + 20 + distance);
                        Tile nyloTile = entity.tile().center(entity.getSize());
                        Projectile projectile = new Projectile(nyloTile, p, 1561, 39, duration, 8, 16, 16, entity.getSize(), 48, 0);
                        projectile.send(entity, p);
                        int delay = (int) (projectile.getSpeed() / 20D);
                        new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
                    });
                }
            }
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity mob) {
        return mob.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        return super.customOnDeath(hit);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public void doFollowLogic() {
        //It will change every 10 ticks (6 seconds), and it will always change to one of the other two styles (meaning it will not stay on one style twice in a row).
        if (World.getWorld().cycleCount() % 10 == 0) {
            if (forms != null && forms.length > 0) {
                entity.animate(-1); // Prevent buggy animations
                if (entity.isNpc() && !entity.dead()) {
                    isTransforming = true;
                    transform(entity.npc());
                    Entity mobTarget = entity.getCombat().getTarget();
                    if (mobTarget != null) {
                        mobTarget.animate(-1);
                        mobTarget.getCombat().reset();
                    }
                }
            }
        }
    }
}
