package com.cryptic.model.entity.combat.magic.impl;

import com.cryptic.model.World;
import com.cryptic.model.content.mechanics.MultiwayCombat;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.combat.magic.data.AncientSpells;
import com.cryptic.model.entity.combat.magic.data.AutoCastWeaponSpells;
import com.cryptic.model.entity.combat.method.impl.MagicCombatMethod;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.timers.TimerKey;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 *
 * @author lare96
 */
public abstract class CombatEffectSpell extends CombatSpell {

    int[] ignored_npcs = new int[]{7710, 7709, 8358, 8379};

    public void whenSpellCast(Entity cast, Entity castOn) {
        if (spellRadius() == 0) return;

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

                    if (castOn == n) continue;
                    if (n.getCombatInfo() != null && n.getCombatInfo().unattackable) continue;
                    if (!MultiwayCombat.includes(n)) continue;
                    if (ArrayUtils.contains(ignored_npcs, n.id())) continue;
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
                    if (!MultiwayCombat.includes(p))
                        continue;

                    if (!CombatFactory.canAttack(cast, CombatFactory.MAGIC_COMBAT, p)) {
                        cast.getCombat().reset();
                        continue;
                    }
                    targets.add(p);
                }
            }
        }

        for (Entity target : targets) {
            Hit hit = new Hit(cast, castOn, delay, cast.getCombat().getCombatType());
            if (cast.isPlayer() && target.isPlayer() && WildernessArea.inWilderness(target.tile()))
                Skulling.skull(cast.getAsPlayer(), target.getAsPlayer(), SkullType.WHITE_SKULL);
            if (isImmune(target, hit)) return;
            else hit.checkAccuracy(true).submit();
            if (hit.isAccurate()) spellEffect(cast, target, hit);
            boolean ancientSpells = cast.isPlayer() && cast.player().getSpellbook() == MagicSpellbook.ANCIENTS;
            boolean isWearingPoweredStaff = cast.isPlayer() && cast.player().getEquipment().containsAny(TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SWAMP, SANGUINESTI_STAFF, TUMEKENS_SHADOW, DAWNBRINGER, ACCURSED_SCEPTRE_A);
            int projectile = -1, startSpeed = -1, startHeight = -1, endHeight = -1, endGraphic = -1, stepMultiplier = -1, duration = -1;
            GraphicHeight startGraphicHeight = GraphicHeight.HIGH;
            GraphicHeight endGraphicHeight = GraphicHeight.HIGH;
            AncientSpells findProjectileDataAncients = AncientSpells.findSpellProjectileData(spellId(), startGraphicHeight, endGraphicHeight);
            AutoCastWeaponSpells findAutoCastWeaponsData = AutoCastWeaponSpells.findSpellProjectileData(spellId(), endGraphicHeight);
            int distance = cast.tile().getChevDistance(target.tile());
            if (findProjectileDataAncients != null && ancientSpells && cast.getCombat().getCastSpell() != null && cast.getCombat().getCastSpell().spellId() == findProjectileDataAncients.spellID) {
                projectile = findProjectileDataAncients.projectile;
                startSpeed = findProjectileDataAncients.startSpeed;
                startHeight = findProjectileDataAncients.startHeight;
                endHeight = findProjectileDataAncients.endHeight;
                endGraphic = findProjectileDataAncients.endGraphic;
                stepMultiplier = findProjectileDataAncients.stepMultiplier;
                duration = (startSpeed + -5 + (stepMultiplier * distance));
                endGraphicHeight = findProjectileDataAncients.endGraphicHeight;
            } else if (isWearingPoweredStaff && findAutoCastWeaponsData != null && cast.getCombat().getPoweredStaffSpell() != null && cast.getCombat().getPoweredStaffSpell().spellId() == findAutoCastWeaponsData.spellID) {
                projectile = findAutoCastWeaponsData.projectile;
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

    private boolean isImmune(Entity target, Hit hit) {
        if (target instanceof NPC npc) {
            if (ArrayUtils.contains(MagicCombatMethod.immune_to_magic, npc.id())) {
                hit.checkAccuracy(false).block().submit();
                return true;
            }
        }
        return false;
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
