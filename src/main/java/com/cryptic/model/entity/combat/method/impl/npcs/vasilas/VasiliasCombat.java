package com.cryptic.model.entity.combat.method.impl.npcs.vasilas;


import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class VasiliasCombat extends CommonCombatMethod {
    private static final Projectile RANGED_PROJECTILE = new Projectile(1560, 70, 20, 51, 56, 10, 16, 96);
    private static final Projectile MAGIC_PROJECTILE = new Projectile(1580, 70, 20, 51, 56, 10, 16, 96);
    private boolean isTransforming;
    private NylocasSize size;
    private Form[] forms;
    private Queue<Form> transformationsQueue;
    private Form form;
    private static final Area room = new Area(3290, 4243, 3301, 4254);

    @Override
    public void init(NPC npc) {
        size = NylocasSize.BOSS;
        form = Form.MELEE;
        forms = new Form[] {Form.MELEE, Form.RANGED, Form.MAGIC};
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
        switch (form.getCombatType()) {
            case MAGIC -> {
                if (player.isPlayer()) {
                    Arrays.stream(mob.closePlayers(9)).forEach(p -> {
                        int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                        mob.animate(animationId);
                        int delay = MAGIC_PROJECTILE.send(mob, player);
                        Hit hit = p.hit(mob, World.getWorld().random(combatInfo.maxhit), CombatType.MAGIC).clientDelay(delay).checkAccuracy(true);
                        hit.submit();
                    });
                }
            }
            case MELEE -> {
                if (player.isPlayer()) { // wheres the new what? instance?n npc
                    if (!withinDistance(2)) {
                        follow(2);
                        return false;
                    }
                    int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                    mob.animate(animationId);
                    Hit hit = player.hit(mob, World.getWorld().random(combatInfo.maxhit), 2, CombatType.MELEE).checkAccuracy(true);
                    hit.submit();
                }
            }
            case RANGED -> {
                if (player.isPlayer()) {
                    Arrays.stream(mob.closePlayers(9)).forEach(p -> {
                        int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                        mob.animate(animationId);
                        int delay = RANGED_PROJECTILE.send(mob, player);
                        Hit hit = p.hit(mob, World.getWorld().random(combatInfo.maxhit), CombatType.RANGED).clientDelay(delay).checkAccuracy(true);
                        hit.submit();
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

    @Override
    public void onDeath(Player killer, NPC npc) {
        var party = killer.raidsParty;

        if (party != null) {
            var currentKills = party.getKills();
            party.setKills(currentKills + 1);

            //Progress to the next stage
            if (party.getKills() == 1) {
                party.setRaidStage(4);
                party.setKills(0);//Reset kills back to 0
            }

            int randomPoints = World.getWorld().random(10, 12);
            for (Player player : party.getMembers()) {
                var raidsPoints = player.<Integer>getAttribOr(AttributeKey.PERSONAL_POINTS, 0) + randomPoints;
                player.putAttrib(AttributeKey.PERSONAL_POINTS, raidsPoints);
                player.message("You now have " + raidsPoints + " points.");
            }
        }
    }
}
