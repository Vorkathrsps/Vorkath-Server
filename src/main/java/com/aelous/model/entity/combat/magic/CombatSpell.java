package com.aelous.model.entity.combat.magic;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.impl.CombatEffectSpell;
import com.aelous.model.entity.combat.magic.spells.Spell;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

import java.util.Optional;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * A {@link Spell} implementation used for combat related spells.
 *
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

    @Override
    public void startCast(Entity cast, Entity castOn) {
        // On 07, the player gets unfrozen when the freezer is at least X tiles away

        int delay = (int) (1 + Math.floor(1 + cast.tile().getChevDistance(castOn.tile()) / 3D));
        delay = (int) Math.min(Math.max(1.0, delay), 5.0);

        int castAnimation = -1;

        NPC npc = cast.isNpc() ? ((NPC) cast) : null;

        if (castAnimation().isPresent()) {
            castAnimation().ifPresent(cast::animate);
        } else {
            cast.animate(new Animation(castAnimation));
        }

        // Then send the starting graphic.
        if (npc != null) {
            if (npc.id() != 2000 && npc.id() != 109 && npc.id() != 3580 && npc.id() != 2007) {
                startGraphic().ifPresent(cast::performGraphic);
            }
        } else {
            startGraphic().ifPresent(cast::performGraphic);
        }

        CombatSpell spell = cast.getCombat().getCastSpell() != null ? cast.getCombat().getCastSpell() : cast.getCombat().getAutoCastSpell();

        //Special spells

        if (spell != null) {
            if (spell.name().equalsIgnoreCase("Confuse")) {
                if (cast.isPlayer()) {
                    var success = MagicAccuracy.doesHit(cast, castOn, CombatType.MAGIC) || cast.hasAttrib(AttributeKey.ALWAYS_HIT);
                    cast.graphic(102, GraphicHeight.HIGH, 0);
                    new Projectile(cast, castOn, 103, 0, 20, 43, 31, 0, 0, 0).sendProjectile();
                    var tileDist = cast.tile().getChevDistance(castOn.tile());
                    cast.skills().addXp(Skills.MAGIC, 13.0, castOn.isPlayer());
                    boolean isNpc = castOn.isNpc();
                    if (success) {
                        var level = 0;
                        if (isNpc) {
                            if (castOn.getAsNpc().combatInfo() != null && castOn.getAsNpc().combatInfo().stats != null) {
                                level = castOn.getAsNpc().combatInfo().stats.attack;
                            }
                        } else {
                            level = castOn.skills().level(Skills.ATTACK);
                        }

                        if (level < 1) {
                            cast.message("The spell has no effect because the player has already been weakened.");
                            return;
                        }

                        castOn.graphic(104, GraphicHeight.HIGH, delay * 30);//a tick in graphic format/maths is 30.

                        if (isNpc) {
                            int decrease = (int) (0.05 * level);
                            castOn.getAsNpc().combatInfo().stats.attack -= decrease;
                        } else {
                            int decrease = (int) (0.05 * (castOn.skills().level(Skills.ATTACK)));
                            castOn.skills().setLevel(Skills.ATTACK, castOn.skills().level(Skills.ATTACK) - decrease);
                            castOn.skills().update(Skills.ATTACK);
                            castOn.message("You feel slightly weakened.");
                        }
                    }
                }
                return;
            }

            if (spell.name().equalsIgnoreCase("Weaken")) {
                if (cast.isPlayer()) {
                    var success = MagicAccuracy.doesHit(cast, castOn, CombatType.MAGIC) || cast.hasAttrib(AttributeKey.ALWAYS_HIT);
                    cast.graphic(102, GraphicHeight.HIGH, 0);
                    new Projectile(cast, castOn, 105, 0, 20, 43, 31, 0, 0, 0).sendProjectile();
                    var tileDist = cast.tile().getChevDistance(castOn.tile());
                    cast.skills().addXp(Skills.MAGIC, 21, castOn.isPlayer());
                    boolean isNpc = castOn.isNpc();
                    if (success) {
                        var level = 0;
                        if (isNpc) {
                            if (castOn.getAsNpc().combatInfo() != null && castOn.getAsNpc().combatInfo().stats != null) {
                                level = castOn.getAsNpc().combatInfo().stats.strength;
                            }
                        } else {
                            level = castOn.skills().level(Skills.STRENGTH);
                        }

                        if (level < 1) {
                            cast.message("The spell has no effect because the player has already been weakened.");
                            return;
                        }

                        castOn.graphic(107, GraphicHeight.HIGH, delay * 30);//a tick in graphic format/maths is 30.

                        if (isNpc) {
                            int decrease = (int) (0.05 * level);
                            castOn.getAsNpc().combatInfo().stats.strength -= decrease;
                        } else {
                            int decrease = (int) (0.05 * (castOn.skills().level(Skills.STRENGTH)));
                            castOn.skills().setLevel(Skills.STRENGTH, castOn.skills().level(Skills.STRENGTH) - decrease);
                            castOn.skills().update(Skills.STRENGTH);
                            castOn.message("You feel slightly weakened.");
                        }
                    }
                }
                return;
            }

            if (spell.name().equalsIgnoreCase("Curse")) {
                if (cast.isPlayer()) {
                    var success = MagicAccuracy.doesHit(cast, castOn, CombatType.MAGIC) || cast.hasAttrib(AttributeKey.ALWAYS_HIT);
                    cast.graphic(102, GraphicHeight.HIGH, 0);
                    new Projectile(cast, castOn, 108, 0, 20, 43, 31, 0, 0, 0).sendProjectile();
                    var tileDist = cast.tile().getChevDistance(castOn.tile());
                    cast.skills().addXp(Skills.MAGIC, 29.0, castOn.isPlayer());
                    boolean isNpc = castOn.isNpc();
                    if (success) {
                        var level = 0;
                        if (isNpc) {
                            if (castOn.getAsNpc().combatInfo() != null && castOn.getAsNpc().combatInfo().stats != null) {
                                level = castOn.getAsNpc().combatInfo().stats.defence;
                            }
                        } else {
                            level = castOn.skills().level(Skills.DEFENCE);
                        }

                        if (level < 1) {
                            cast.message("The spell has no effect because the player has already been weakened.");
                            return;
                        }

                        castOn.graphic(110, GraphicHeight.HIGH, delay * 30);//a tick in graphic format/maths is 30.

                        if (isNpc) {
                            int decrease = (int) (0.05 * level);
                            castOn.getAsNpc().combatInfo().stats.defence -= decrease;
                        } else {
                            int decrease = (int) (0.05 * (castOn.skills().level(Skills.DEFENCE)));
                            castOn.skills().setLevel(Skills.DEFENCE, castOn.skills().level(Skills.DEFENCE) - decrease);
                            castOn.skills().update(Skills.DEFENCE);
                            castOn.message("You feel slightly weakened.");
                        }
                    }
                }
                return;
            }

            if (spell.name().equalsIgnoreCase("Vulnerability")) {
                if (cast.isPlayer()) {
                    var success = MagicAccuracy.doesHit(cast, castOn, CombatType.MAGIC) || cast.hasAttrib(AttributeKey.ALWAYS_HIT);
                    cast.graphic(167, GraphicHeight.HIGH, 0);
                    new Projectile(cast, castOn, 168, 0, 20, 43, 31, 0, 0, 0).sendProjectile();
                    var tileDist = cast.tile().getChevDistance(castOn.tile());
                    cast.skills().addXp(Skills.MAGIC, 76.0, castOn.isPlayer());
                    boolean isNpc = castOn.isNpc();
                    if (success) {
                        var level = 0;
                        if (isNpc) {
                            if (castOn.getAsNpc().combatInfo() != null && castOn.getAsNpc().combatInfo().stats != null) {
                                level = castOn.getAsNpc().combatInfo().stats.defence;
                            }
                        } else {
                            level = castOn.skills().level(Skills.DEFENCE);
                        }

                        if (level < 1) {
                            cast.message("The spell has no effect because the player is already weakened.");
                            return;
                        }

                        castOn.graphic(169, GraphicHeight.LOW, delay * 30);//a tick in graphic format/maths is 30.

                        if (isNpc) {
                            int decrease = (int) (0.10 * level);
                            castOn.getAsNpc().combatInfo().stats.defence -= decrease;
                        } else {
                            int decrease = (int) (0.10 * (castOn.skills().level(Skills.DEFENCE)));
                            castOn.skills().setLevel(Skills.DEFENCE, castOn.skills().level(Skills.DEFENCE) - decrease);
                            castOn.skills().update(Skills.DEFENCE);
                            castOn.message("You feel slightly weakened.");
                        }
                    }
                }
                return;
            }

            if (spell.name().equalsIgnoreCase("Enfeeble")) {
                if (cast.isPlayer()) {
                    var success = MagicAccuracy.doesHit(cast, castOn, CombatType.MAGIC) || cast.hasAttrib(AttributeKey.ALWAYS_HIT);
                    cast.graphic(170, GraphicHeight.HIGH, 0);
                    new Projectile(cast, castOn, 171, 0, 20, 43, 31, 0, 0, 0).sendProjectile();
                    var tileDist = cast.tile().getChevDistance(castOn.tile());
                    cast.skills().addXp(Skills.MAGIC, 83, castOn.isPlayer());
                    boolean isNpc = castOn.isNpc();
                    if (success) {
                        var level = 0;
                        if (isNpc) {
                            if (castOn.getAsNpc().combatInfo() != null && castOn.getAsNpc().combatInfo().stats != null) {
                                level = castOn.getAsNpc().combatInfo().stats.strength;
                            }
                        } else {
                            level = castOn.skills().level(Skills.STRENGTH);
                        }

                        if (level < 1) {
                            cast.message("The spell has no effect because the player is already weakened.");
                            return;
                        }

                        castOn.graphic(172, GraphicHeight.LOW, delay * 30);//a tick in graphic format/maths is 30.

                        if (isNpc) {
                            int decrease = (int) (0.10 * level);
                            castOn.getAsNpc().combatInfo().stats.strength -= decrease;
                        } else {
                            int decrease = (int) (0.10 * (castOn.skills().level(Skills.STRENGTH)));
                            castOn.skills().setLevel(Skills.STRENGTH, castOn.skills().level(Skills.STRENGTH) - decrease);
                            castOn.skills().update(Skills.STRENGTH);
                            castOn.message("You feel slightly weakened.");
                        }
                    }
                }
                return;
            }

            if (spell.name().equalsIgnoreCase("Stun")) {
                if (cast.isPlayer()) {
                    var success = MagicAccuracy.doesHit(cast, castOn, CombatType.MAGIC) || cast.hasAttrib(AttributeKey.ALWAYS_HIT);
                    cast.graphic(173, GraphicHeight.HIGH, 0);
                    new Projectile(cast, castOn, 174, 0, 20, 43, 31, 0, 0, 0).sendProjectile();
                    var tileDist = cast.tile().getChevDistance(castOn.tile());
                    cast.skills().addXp(Skills.MAGIC, 90.0, castOn.isPlayer());
                    boolean isNpc = castOn.isNpc();
                    if (success) {
                        var level = 0;
                        if (isNpc) {
                            if (castOn.getAsNpc().combatInfo() != null && castOn.getAsNpc().combatInfo().stats != null) {
                                level = castOn.getAsNpc().combatInfo().stats.attack;
                            }
                        } else {
                            level = castOn.skills().level(Skills.ATTACK);
                        }

                        if (level < 1) {
                            cast.message("The spell has no effect because the player is already weakened.");
                            return;
                        }

                        castOn.graphic(107, GraphicHeight.LOW, delay * 30);//a tick in graphic format/maths is 30.

                        if (isNpc) {
                            int decrease = (int) (0.10 * level);
                            castOn.getAsNpc().combatInfo().stats.attack -= decrease;
                        } else {
                            int decrease = (int) (0.10 * (castOn.skills().level(Skills.ATTACK)));
                            castOn.skills().setLevel(Skills.ATTACK, castOn.skills().level(Skills.ATTACK) - decrease);
                            castOn.skills().update(Skills.ATTACK);
                            castOn.message("You feel slightly weakened.");
                        }
                    }
                }
                return;
            }

            if (spell.name().equalsIgnoreCase("Teleblock")) {
                if (cast.isPlayer() && castOn.isPlayer()) {
                    Player player = (Player) cast;
                    Player target = (Player) castOn;
                    var success = MagicAccuracy.doesHit(player, target, CombatType.MAGIC) || player.hasAttrib(AttributeKey.ALWAYS_HIT);
                    new Projectile(player, target, success ? 1299 : 1300, 41, player.projectileSpeed(target), 43, 31, 0, 0, 0).sendProjectile();
                    var tileDist = player.tile().getChevDistance(target.tile());
                    var tbTime = 495;
                    var tb_delay = tileDist <= 2 ? 1 : tileDist <= 5 ? 2 : 3;
                    if (success) {
                        target.getTimers().extendOrRegister(TimerKey.COMBAT_LOGOUT, 20);
                        target.graphic(345, GraphicHeight.LOW, tb_delay * 30);//a tick in graphic format/maths is 30.

                        var half = Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC);
                        if (half) {
                            tbTime = 248; //Half teleblock
                        }
                        // After investigating on RS - the teleblock lands instantly even from 11 tiles away.
                        target.teleblock(tbTime);

                        // Add base XP - 82xp if a half, 95 is full!
                        player.skills().addXp(Skills.MAGIC, half ? 82.0 : 95.0, target.isPlayer());
                        Skulling.skull(player, target, SkullType.WHITE_SKULL);
                        player.message(Color.PURPLE.wrap("The teleblock was successful!"));
                    }
                }
                return;
            }
        }

        Optional<Projectile> optP = executeProjectile(cast, castOn, cast.tile().getChevDistance(castOn.tile()));
        if (optP.isPresent()) {
            Projectile p = optP.get();
            p.sendProjectile();
        }

        Hit hit = castOn.hit(cast, CombatFactory.calcDamageFromType(cast, castOn, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();

        if (spell instanceof CombatEffectSpell) {
            if (hit.isAccurate()) {
                CombatEffectSpell combatEffectSpell = (CombatEffectSpell) spell;
                combatEffectSpell.whenSpellCast(cast, castOn);
                combatEffectSpell.spellEffect(cast, castOn, hit);
            }
        }
    }

    public int getAttackSpeed(Entity attacker) {
        int speed = 5;
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;

            if (player.getEquipment().hasAt(EquipSlot.WEAPON, HARMONISED_NIGHTMARE_STAFF) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP) || player.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS) || player.getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF) || player.getEquipment().hasAt(EquipSlot.WEAPON, HOLY_SANGUINESTI_STAFF)) {
                speed = 4;
            }
        }
        return speed;
    }

    public int[] elementalStaff() {
        int projectile, gfx_impact;
        final int roll = Utils.random(5);
        if (roll == 0 || roll == 1) {
            projectile = 159; // Air
            gfx_impact = 160;
        } else if (roll == 2 || roll == 3) {
            projectile = 162; // Water
            gfx_impact = 163;
        } else if (roll == 4) {
            projectile = 165; // Earth
            gfx_impact = 166;
        } else {
            projectile = 156; // Fire
            gfx_impact = 157;
        }
        return new int[]{projectile, gfx_impact};
    }

    public abstract String name();

    /**
     * The fixed ID of the spell implementation as recognized by the protocol.
     *
     * @return the ID of the spell, or <tt>-1</tt> if there is no ID for this
     * spell.
     */
    public abstract int spellId();

    /**
     * The maximum hit an {@link Mob} can deal with this spell.
     *
     * @return the maximum hit able to be dealt with this spell implementation.
     */
    public abstract int baseMaxHit();

    /**
     * The animation played when the spell is cast.
     *
     * @return the animation played when the spell is cast.
     */
    public abstract Optional<Animation> castAnimation();

    /**
     * The starting graphic played when the spell is cast.
     *
     * @return the starting graphic played when the spell is cast.
     */
    public abstract Optional<Graphic> startGraphic();

    /**
     * The projectile played when this spell is cast.
     *
     * @param cast   the entity casting the spell.
     * @param castOn the entity targeted by the spell.
     * @return the projectile played when this spell is cast.
     */
    public Optional<Projectile> executeProjectile(Entity cast, Entity castOn, int dist) {
        return executeProjectile(cast, castOn);
    }

    public Optional<Projectile> executeProjectile(Entity cast, Entity castOn) {
        return executeProjectile(cast, castOn, cast.tile().getChevDistance(castOn.tile()));
    }

    public Optional<Projectile> bind(Entity cast, Entity castOn, int dist, int graphicID) {
        int realDelay = 75;
        int speed = 0;
        int duration = speed + dist * 5;
        //int duration = executeProjectile(new Projectile((1465, cast, castOn, cast.tile().getChevDistance(castOn.tile())));
        return Optional.of(new Projectile(cast, castOn, graphicID,
            realDelay, duration, 45, 0, 16, 0, 0));
    }

    public Optional<Projectile> modern(Entity cast, Entity castOn, int dist, int graphicID) {
        int delay = 51;
        int speed = 0;
        int duration = delay + speed + dist * 5;
        return Optional.of(new Projectile(cast, castOn, graphicID, delay, duration, 43, 31, 0, 0, 0));
    }

    public Optional<Projectile> ancient(Entity cast, Entity castOn, int dist, int graphicID) {
        int delay = 51;
        int speed = 0;
        int duration = delay + speed + dist * 5;
        return Optional.of(new Projectile(cast, castOn, graphicID, delay, duration, 43, 0, 0, 0, 0));
    }

    /**
     * The ending graphic played when the spell hits the victim.
     *
     * @return the ending graphic played when the spell hits the victim.
     */
    public abstract Optional<Graphic> endGraphic();

    public abstract MagicSpellbook spellbook();

    public abstract void finishCast(Entity cast, Entity castOn, boolean accurate, int damage);
}
