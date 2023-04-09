package com.aelous.model.entity.combat.method.impl.npcs.vasilas;


import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Patrick van Elderen <<a href="https://github.com/PVE95">...</a>>
 * @Since January 07, 2022
 */
public class NylocasVasilias extends CommonCombatMethod {

    private static final Projectile RANGED_PROJECTILE = new Projectile(1560, 70, 20, 51, 56, 10, 16, 96);
    private static final Projectile MAGIC_PROJECTILE = new Projectile(1580, 70, 20, 51, 56, 10, 16, 96);

    private boolean isTransforming;
    private NylocasSize size;
    private Form[] forms;
    private Queue<Form> transformationsQueue;
    private Form form;

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
        npc.transmog(form.getNpcIdBySize(size));
    }

    @Override
    public boolean prepareAttack(Entity mob, Entity target) {
        var combatInfo = mob.npc().getCombatInfo();
        if (isTransforming) {
            return false;
        }
        switch (form.getCombatType()) {
            case MAGIC:
                if (target.isPlayer()) {
                    Arrays.stream(mob.closePlayers(9)).forEach(p -> {
                        int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                        mob.animate(animationId);
                        int delay = MAGIC_PROJECTILE.send(mob, target);
                        Hit hit = p.hit(mob, World.getWorld().random(combatInfo.maxhit), CombatType.MAGIC).clientDelay(delay).checkAccuracy();
                        hit.submit();
                    });
                }
                break;
            case MELEE:
                if (target.isPlayer()) {
                    int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                    mob.animate(animationId);
                    Hit hit = target.hit(mob, World.getWorld().random(combatInfo.maxhit), 2, CombatType.MELEE).checkAccuracy();
                    hit.submit();
                }
                break;
            case RANGED:
                if (target.isPlayer()) {
                    Arrays.stream(mob.closePlayers(9)).forEach(p -> {
                        int animationId = form == null ? combatInfo.animations.attack : form.getAttackAnims()[0];
                        mob.animate(animationId);
                        int delay = RANGED_PROJECTILE.send(mob, target);
                        Hit hit = p.hit(mob, World.getWorld().random(combatInfo.maxhit), CombatType.RANGED).clientDelay(delay).checkAccuracy();
                        hit.submit();
                    });
                }
                break;
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity mob) {
        return mob.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        return super.customOnDeath(hit);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }

    @Override
    public void doFollowLogic() {
        // Prevents Nylocas from following

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
