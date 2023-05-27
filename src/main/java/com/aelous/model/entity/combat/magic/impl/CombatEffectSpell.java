package com.aelous.model.entity.combat.magic.impl;

import com.aelous.model.World;
import com.aelous.model.content.mechanics.MultiwayCombat;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.data.AncientSpells;
import com.aelous.model.entity.combat.magic.data.AutoCastWeaponSpells;
import com.aelous.model.entity.combat.magic.data.ModernSpells;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.timers.TimerKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 *
 * @author lare96
 */
public abstract class CombatEffectSpell extends CombatSpell {

    public void whenSpellCast(Entity cast, Entity castOn) {
        if (spellRadius() == 0) {
            return;
        }

        int delay = (int) (2 + Math.floor((1 + cast.tile().getManHattanDist(cast.tile(), castOn.tile())) / 3D));

        castOn.putAttrib(AttributeKey.LAST_DAMAGER, cast);
        castOn.putAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME, System.currentTimeMillis());
        castOn.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);
        cast.putAttrib(AttributeKey.LAST_ATTACK_TIME, System.currentTimeMillis());
        cast.putAttrib(AttributeKey.LAST_TARGET, castOn);
        cast.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);

        ArrayList<Entity> targets = new ArrayList<>();

        Iterator<? extends Entity> it = null;
        if (cast.isPlayer() && castOn.isPlayer()) {
            it = cast.getLocalPlayers().iterator();
        } else if (cast.isPlayer() && castOn.isNpc()) {
            it = cast.getLocalNpcs().iterator();
        } else if (cast.isNpc() && castOn.isNpc()) {
            it = World.getWorld().getNpcs().iterator();
        } else if (cast.isNpc() && castOn.isPlayer()) {
            it = World.getWorld().getPlayers().iterator();
        }

        if (it != null) {
            while (it.hasNext()) {
                Entity next = it.next();

                if (next == null) {
                    continue;
                }

                if (!next.tile().isWithinDistance(castOn.tile(), spellRadius()) || next.dead()) {
                    continue;
                }

                if (next.isNpc()) {
                    NPC n = (NPC) next;
                    if (castOn == n) {
                        continue;
                    }

                    if (n.getCombatInfo() != null && n.getCombatInfo().unattackable) {
                        continue;
                    }

                    if (!MultiwayCombat.includes(n)) {
                        continue;
                    }

                    if (n.id() == 7710 || n.id() == 7709) {
                        continue;
                    }

                    if (!CombatFactory.canAttack(cast, CombatFactory.MAGIC_COMBAT, n)) {
                        cast.getCombat().reset();
                        continue;
                    }
                    targets.add(n);
                } else {
                    Player p = (Player) next;
                    if (castOn == p) {
                        continue;
                    }

                    if (p.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 0) {
                        continue;
                    }
                    if (!WildernessArea.inAttackableArea(p))
                        continue;
                    if ( !MultiwayCombat.includes(p))
                        continue;

                    if (!CombatFactory.canAttack(cast, CombatFactory.MAGIC_COMBAT, p)) {
                        cast.getCombat().reset();
                        continue;
                    }
                    targets.add(p);
                }
            }
        }

        //System.out.println("targets: "+ Arrays.toString(targets.stream().map(e -> e.getMobName()).toArray()));
        for (Entity target : targets) {
            Hit hit = target.hit(cast, CombatFactory.calcDamageFromType(cast, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
            if (cast.isPlayer() && target.isPlayer() && WildernessArea.inWilderness(target.tile())) {
                Skulling.skull(cast.getAsPlayer(), target.getAsPlayer(), SkullType.WHITE_SKULL);
            }
            hit.submit();
            if (hit.isAccurate()) {
                spellEffect(cast, target, hit);
            }
            boolean modernSpells = cast.isPlayer() && cast.player().getSpellbook() == MagicSpellbook.NORMAL;
            boolean ancientSpells = cast.isPlayer() && cast.player().getSpellbook() == MagicSpellbook.ANCIENT;
            boolean isWearingPoweredStaff = cast.isPlayer() && cast.player().getEquipment().containsAny(TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP, SANGUINESTI_STAFF, TUMEKENS_SHADOW, DAWNBRINGER, ACCURSED_SCEPTRE_A);
            AutoCastWeaponSpells data = AutoCastWeaponSpells.findSpellProjectileData(spellId(), GraphicHeight.HIGH);
            int projectile = -1;
            int startgraphic = -1;
            int castAnimation = -1;
            int startSpeed = -1;
            int startHeight = -1;
            int endHeight = -1;
            int endGraphic = -1;
            int stepMultiplier = -1;
            int duration = -1;
            GraphicHeight startGraphicHeight = GraphicHeight.HIGH;
            GraphicHeight endGraphicHeight = GraphicHeight.HIGH;
            ModernSpells findProjectileDataModern = ModernSpells.findSpellProjectileData(spellId(), endGraphicHeight);
            AncientSpells findProjectileDataAncients = AncientSpells.findSpellProjectileData(spellId(), startGraphicHeight, endGraphicHeight);
            AutoCastWeaponSpells findAutoCastWeaponsData = AutoCastWeaponSpells.findSpellProjectileData(spellId(), endGraphicHeight);
            int distance = cast.tile().getChevDistance(target.tile());

            if (findProjectileDataAncients != null && ancientSpells && cast.getCombat().getCastSpell() != null && cast.getCombat().getCastSpell().spellId() == findProjectileDataAncients.spellID) {
                projectile = findProjectileDataAncients.projectile;
                startgraphic = findProjectileDataAncients.startGraphic;
                castAnimation = findProjectileDataAncients.castAnimation;
                startSpeed = findProjectileDataAncients.startSpeed;
                startHeight = findProjectileDataAncients.startHeight;
                endHeight = findProjectileDataAncients.endHeight;
                endGraphic = findProjectileDataAncients.endGraphic;
                stepMultiplier = findProjectileDataAncients.stepMultiplier;
                duration = (startSpeed + -5 + (stepMultiplier * distance));
                endGraphicHeight = findProjectileDataAncients.endGraphicHeight;
            } else if (isWearingPoweredStaff && findAutoCastWeaponsData != null && cast.getCombat().getPoweredStaffSpell() != null && cast.getCombat().getPoweredStaffSpell().spellId() == findAutoCastWeaponsData.spellID) {
                projectile = findAutoCastWeaponsData.projectile;
                startgraphic = findAutoCastWeaponsData.startGraphic;
                castAnimation = findAutoCastWeaponsData.castAnimation;
                startSpeed = findAutoCastWeaponsData.startSpeed;
                startHeight = findAutoCastWeaponsData.startHeight;
                endHeight = findAutoCastWeaponsData.endHeight;
                endGraphic = findAutoCastWeaponsData.endGraphic;
                stepMultiplier = findAutoCastWeaponsData.stepMultiplier;
                duration = (startSpeed + -5 + (stepMultiplier * distance));
                endGraphicHeight = findAutoCastWeaponsData.endGraphicHeight;
            }
            Projectile p = new Projectile(cast, target, projectile, startSpeed, duration, startHeight, endHeight, 0, target.getSize(), stepMultiplier);
            if (hit.isAccurate()) {
                target.performGraphic(new Graphic(endGraphic, endGraphicHeight, p.getSpeed()));
            } else {
                target.performGraphic(new Graphic(85, GraphicHeight.LOW, p.getSpeed()));
            }
        }
    }


    @Override
    public List<Item> equipmentRequired(Player player) {
        return List.of();
    }

    @Override
    public final void finishCast(Entity cast, Entity castOn, boolean accurate, int damage) {
    }

    /**
     * The effect this spell has on the target.
     *
     * @param cast   the entity casting this spell.
     * @param castOn the person being hit by this spell.
     * @param hit
     */
    public abstract void spellEffect(Entity cast, Entity castOn, Hit hit);

    /**
     * The radius of this spell, only comes in effect when the victim is hit in
     * a multicombat area.
     *
     * @return how far from the target this spell can hit when targeting
     * multiple entities.
     */
    public abstract int spellRadius();
}
