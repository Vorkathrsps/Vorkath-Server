package com.aelous.model.entity.combat;

import com.aelous.GameServer;
import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.GameEngine;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.duel.Dueling;
import com.aelous.model.content.mechanics.MultiwayCombat;
import com.aelous.model.content.members.MemberZone;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.Splat;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.MagicCombatMethod;
import com.aelous.model.entity.combat.method.impl.MeleeCombatMethod;
import com.aelous.model.entity.combat.method.impl.RangedCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.abyssalsire.AbyssalSireState;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath.Vorkath;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.armadyl.KreeArra;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.bandos.Graardor;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.saradomin.Zilyana;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.zamorak.Kril;
import com.aelous.model.entity.combat.method.impl.npcs.hydra.AlchemicalHydra;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.CaveKraken;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.DesertLizards;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.Gargoyle;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken.EnormousTentacle;
import com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.ranged.requirements.BowReqs;
import com.aelous.model.entity.combat.ranged.requirements.CbowReqs;
import com.aelous.model.entity.combat.ranged.RangedData;
import com.aelous.model.entity.combat.ranged.RangedData.RangedWeapon;
import com.aelous.model.entity.combat.ranged.RangedData.RangedWeaponType;
import com.aelous.model.entity.combat.weapon.AttackType;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.Equipment;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.areas.ControllerManager;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.network.SessionState;
import com.aelous.utility.*;
import com.aelous.utility.timers.TimerKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.model.inter.InterfaceConstants.BARROWS_REWARD_WIDGET;
import static com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss.KRAKEN_WHIRLPOOL;
import static com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken.KrakenBoss.TENTACLE_WHIRLPOOL;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * Acts as a utility class for combat.
 *
 * @author Professor Oak
 */
public class CombatFactory {

    private static final Logger logger = LogManager.getLogger(CombatFactory.class);

    /**
     * The default melee combat method.
     */
    public static final CombatMethod MELEE_COMBAT = new MeleeCombatMethod();

    /**
     * The default ranged combat method
     */
    public static final CombatMethod RANGED_COMBAT = new RangedCombatMethod();

    /**
     * The default magic combat method
     */
    public static final CombatMethod MAGIC_COMBAT = new MagicCombatMethod();

    /**
     * Upon using a special attack, block teleporting for up to 6 seconds after that battle started.
     */
    public static void check_spec_and_tele(Player player, Entity target) {
        if (target.isPlayer()) {
            if (WildernessArea.wildernessLevel(target.tile()) >= 1) {
                Map<Integer, Integer> historyMap = player.getAttribOr(AttributeKey.PVP_WILDY_AGGRESSION_TRACKER, new HashMap<Integer, Integer>());
                var agroTick = historyMap.getOrDefault(target.getIndex(), World.getWorld().cycleCount());
                var dif = World.getWorld().cycleCount() - agroTick;
                if (dif < 10) {
                    player.getTimers().extendOrRegister(TimerKey.BLOCK_SPEC_AND_TELE, 10 - dif);
                }
            }
        }
    }

    private static final Area[] GWD_ROOMS = {KreeArra.getENCAMPMENT(), Zilyana.getENCAMPMENT(), Kril.getENCAMPMENT(), Graardor.getBandosArea()};

    public static boolean bothInFixedRoom(Entity mob, Entity other) {
        for (Area area : GWD_ROOMS) {
            if (area.contains(mob) && !area.contains(other) || !area.contains(mob) && area.contains(other)) {
                return false;
            }
        }
        return true;
    }

    public static boolean takeSpecialEnergy(Player player, int specialAmount) {
        if (player.getSpecialAttackPercentage() < specialAmount) {
            player.message("You don't have enough power left.");
            player.setSpecialActivated(false);
            CombatSpecial.updateBar(player);
            return false;
        }
        CombatSpecial.drain(player, specialAmount);
        return true;
    }

    /**
     * Gets a mob's combat method.
     *
     * @param attacker The mob to get the combat method for.
     */
    public static CombatMethod getMethod(Entity attacker) {
        if (attacker.isPlayer()) {
            Player p = attacker.getAsPlayer();

            // Update player data..

            // Update ranged ammo / weapon
            if (p.getEquipment().getWeapon() != null)
                p.getCombat().setRangedWeapon(RangedWeapon.getFor(p));

            /**
             * KINDA COMPLEX ORDERING:
             * the first one to trigger is the one that is executes, the others are ignored
             *   1. TRIGGER EXISTING SPELL (MANUAL SPELL ON PLAYER)
             *   2. TRIGGER NMS SPEC
             *   3. TRIGGER AUTOCAST
             *   4. TRIGGER NMS NORMAL SPELL
             *   5. TRIGGER ALL SPECIAL ATTACKS
             *   6. TRIGGER RANGE-WEP BASED ATTACK
             *   7. DEFAULT TO MELEE
             */

            int wep = p.getEquipment().getId(3);
            boolean specialWeapons = wep == ELDRITCH_NIGHTMARE_STAFF || wep == VOLATILE_NIGHTMARE_STAFF || wep == DRAGON_THROWNAXE || wep == DRAGON_THROWNAXE_21207;

            // check spec activated on NMS staff.
            if (specialWeapons && p.isSpecialActivated()) {
                return p.getCombatSpecial().getCombatMethod();
            }

            // Order here is important: Spell on player takes priority over
            // having the special attack button just "on"
            // Check if player is maging..
            if (p.getCombat().getCastSpell() != null || p.getCombat().getAutoCastSpell() != null || p.getCombat().getPoweredStaffSpell() != null) {
                return MAGIC_COMBAT;
            }

            // Check special attacks..
            if (p.getCombatSpecial() != null) {
                boolean isGmaul = Combat.gmauls.stream().anyMatch(granite_maul -> p.getEquipment().hasAt(EquipSlot.WEAPON, granite_maul));

                if (p.isSpecialActivated() || (isGmaul && p.<Integer>getAttribOr(AttributeKey.GRANITE_MAUL_SPECIALS, 0) > 0)) { // spec bar can be off and gmaul will still activate
                    return p.getCombatSpecial().getCombatMethod();
                }
            }

            // Check if player is ranging..
            if (p.getCombat().getRangedWeapon() != null) {
                //System.out.println("Player is ranging with " + p.getCombat().getRangedWeapon());
                return RANGED_COMBAT;
            }
        } else if (attacker.isNpc()) {
            NPC npc = attacker.getAsNpc();

            // Attempt to return the npc's defined combat method..
            if (npc.getCombatMethod() != null) {
                //System.out.println("Combat method is: " + npc.getCombatMethod());
                return npc.getCombatMethod();
            }
        }

        // Return melee by default
        return MELEE_COMBAT;
    }

    /**
     * Generates a random {@link Splat} based on the argued entity's stats.
     *
     * @param attacker the entity to generate the random hit for.
     * @param target   the victim being attacked.
     * @param type     the combat type being used.
     * @return the HitDamage.
     */
    public static int calcDamageFromType(Entity attacker, Entity target, CombatType type) {
        int max_damage = 1;

        //We have a null typ, ignore.
        if (type == null) {
            return 0;
        }

        switch (type) {
            case MELEE ->
                max_damage = attacker.isNpc() ? attacker.getAsNpc().getCombatInfo() == null ? 0 : attacker.getAsNpc().getCombatInfo().maxhit : attacker.getCombat().getMaximumMeleeDamage();
            case RANGED -> {
                if (attacker.isPlayer()) {
                    Player p = attacker.getAsPlayer();
                    RangedWeapon rangeWeapon = p.getCombat().getRangedWeapon();
                    boolean ignoreArrows = rangeWeapon != null && rangeWeapon.ignoreArrowsSlot();

                    max_damage = p.getCombat().getMaximumRangedDamage(ignoreArrows);
                } else {
                    //Npcs
                    max_damage = attacker.getCombat().getMaximumRangedDamage(true);
                }
            }
            case MAGIC -> max_damage = attacker.getCombat().getMaximumMagicDamage();
        }

        //Ability to override max hits
        if (attacker.isNpc() && attacker.<Integer>getAttribOr(MAXHIT_OVERRIDE, -1) != -1) {
            max_damage = attacker.<Integer>getAttribOr(MAXHIT_OVERRIDE, -1);
        }

        int damage = Utils.inclusive(0, max_damage);

        if (target != null && target.isNpc() && target.getAsNpc().isCombatDummy()) {
            CombatSpell spell = attacker.getCombat().getCastSpell() != null ? attacker.getCombat().getCastSpell() : attacker.getCombat().getAutoCastSpell() != null ? attacker.getCombat().getAutoCastSpell() : attacker.getCombat().getPoweredStaffSpell() != null ? attacker.getCombat().getPoweredStaffSpell() : null;
            damage = spell == CombatSpells.CRUMBLE_UNDEAD.getSpell() ? target.hp() : max_damage;
        }

        if (type == CombatType.MELEE) {
            // Do melee effects with the calculated damage..
            if (attacker.isPlayer() && target != null) {
                boolean pvpDummy = target.isNpc() && target.getAsNpc().isPvPCombatDummy();
                if (target.isPlayer() || pvpDummy) {

                    Player player = attacker.getAsPlayer();

                    //Hard cap melee special attacks
                    if (player.isSpecialActivated()) {
                        if (player.getCombatSpecial() == CombatSpecial.DRAGON_DAGGER) {
                            if (damage > 48) {
                                damage = 48;
                            }
                        }
                        if (player.getCombatSpecial() == CombatSpecial.ANCIENT_GODSWORD) {
                            if (damage > 89) {
                                damage = 89;
                            }
                        }
                        if (player.getCombatSpecial() == CombatSpecial.ARMADYL_GODSWORD) {
                            if (damage > 88) {
                                damage = 88;
                            }
                        }
                    }
                }
            }
        } else if (type == CombatType.RANGED) {
            if (attacker.isPlayer()) {
                Player player = attacker.getAsPlayer();
                // Handle bolt special effects for a player whose using crossbow
                if (player.getCombat().getWeaponType() == WeaponType.CROSSBOW) {
                    damage = RangedData.getBoltSpecialAttack(player, target, damage);
                }

                //Hard cap ranged special attacks
                if (player.isSpecialActivated()) {
                    if (player.getCombatSpecial() == CombatSpecial.DARK_BOW) {
                        if (damage > 48) {
                            damage = 48;
                        }
                    }
                }
            }
        } else if (type == CombatType.MAGIC) {
            // Do magic effects with the calculated damage..
            if (attacker.isPlayer()) {
                Player player = attacker.getAsPlayer();
                if (player.getCombatSpecial() == CombatSpecial.VOLATILE_NIGHTMARE_STAFF) {
                    if (damage > 87) {
                        damage = 87;
                    }
                }
            }
        }

        if (target != null && target.isNpc() && attacker.isPlayer()) {
            NPC npc = target.getAsNpc();
            Player player = (Player) attacker;

            //Disturb the whirlpools
            if (npc.id() == TENTACLE_WHIRLPOOL) {
                EnormousTentacle.onHit(player, npc);
            }

            if (npc.id() == KRAKEN_WHIRLPOOL) {
                damage = 0;
                KrakenBoss.onHit(player, npc);
            }

            //Cave Kraken
            if (npc.id() == 493) {
                npc.animate(7135);
                npc.transmog(492);
                npc.setHitpoints(125);//reset hp when disturbed
                npc.setHitpoints(target.hp() - damage);
                npc.setCombatMethod(new CaveKraken());
            }

            if (npc instanceof AlchemicalHydra hydra) {
                if (!hydra.getShieldDropped()) {
                    player.message("The Alchemical Hydra's defences partially absorb your attack!");
                    damage = Math.round(damage * 0.5F);
                }
            }

            if (attacker.isPlayer() && target.getAsNpc().id() == (NpcIdentifiers.CORPOREAL_BEAST)) {
                if (!attacker.getAsPlayer().getCombat().getCombatType().equals(CombatType.MAGIC)) {
                    if (!attacker.getCombat().getFightType().getAttackType().equals(AttackType.STAB) &&
                        !FormulaUtils.wearingSpearsOrHalberds(player)) {
                        damage = (int) Math.floor(damage * 0.5F);
                    }
                }
            }
        }

        //PvP protection prayers
        if (damage > 0 && target != null) {
            if (target.isPlayer() && attacker.isPlayer()) {
                Player targ = target.getAsPlayer();
                boolean isProtecting = false;
                if (type == CombatType.MELEE && targ.lastActiveOverhead == PROTECT_FROM_MELEE) {
                    isProtecting = true;
                } else if (type == CombatType.RANGED && targ.lastActiveOverhead == PROTECT_FROM_MISSILES) {
                    isProtecting = true;
                } else if (type == CombatType.MAGIC && targ.lastActiveOverhead == PROTECT_FROM_MAGIC) {
                    isProtecting = true;
                }

                //System.err.println("New prayer formula: Target is Praying=" + (isProtecting ? "YES" : "NO"));
                //System.err.println("Damage Before=" + damage);

                if (isProtecting) {
                    damage = (int) Math.floor(damage * 0.4F);
                }
                //System.err.println("Damage After=" + damage);
            }
        }

        if (target instanceof Player) {
            Item shield = target.getAsPlayer().getEquipment().get(EquipSlot.SHIELD);
            if (shield != null && shield.getId() == 12817) {
                if (target.isPlayer() && Utils.securedRandomChance(0.7F)) {
                    damage = (int) Math.floor(damage * CombatConstants.ELYSIAN_DAMAGE_REDUCTION);
                    target.performGraphic(new Graphic(321, GraphicHeight.MIDDLE)); // Elysian spirit shield effect gfx
                }
            }

            if (target.isPlayer()) {
                if (!WildernessArea.inWilderness(target.getAsPlayer().tile())) {
                    if (damage > 0 && Equipment.justiciarSet(target.getAsPlayer())) {
                        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(target, World.getWorld().equipmentInfo());
                        int bonus = attackerBonus.crushdef;
                        int formula = bonus / 3000;
                        damage = damage - formula;
                        damage = damage - 1;
                    }
                }
            }

            //If we have used the SOTD special attack, reduce incoming melee damage by 50%.
            if (target.isPlayer()) {
                if (target.getTimers().has(TimerKey.SOTD_DAMAGE_REDUCTION) && target.getAsPlayer().getEquipment().containsAny(STAFF_OF_THE_DEAD, TOXIC_STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED, STAFF_OF_LIGHT) && type == CombatType.MELEE) {
                    damage = (int) Math.floor(damage * CombatConstants.TSTOD_DAMAGE_REDUCTION);
                }
            }

            // Passive effect of Dinh's Bulwark on Block mode
            if (target.isPlayer()) {
                if (shield != null && shield.getId() == 21015 && target.getAsPlayer().getCombat().getFightType().getChildId() == 2) {
                    damage -= damage / 5;
                    damage = (int) Math.floor(damage * CombatConstants.DINHS_BULWARK_REDUCTION);
                }
            }

            if (target.isPlayer()) {
                Player player = (Player) target;
                // Under 10% hp, hit won't kill us
                if (player.hp() - damage > 0 && player.hp() <= player.getSkills().xpLevel(Skills.HITPOINTS) / 10) {
                    boolean ring = player.getEquipment().contains(2570);

                    boolean defenceCape = (int) player.getAttribOr(AttributeKey.DEFENCE_PERK_TOGGLE, 0) == 1 && (player.getEquipment().contains(DEFENCE_CAPE));
                    if (ring || (player.getEquipment().wearingMaxCape() && (int) player.getAttribOr(AttributeKey.MAXCAPE_ROL_ON, 0) == 1) || defenceCape) {

                        if (Teleports.rolTeleport(player)) {
                            Teleports.ringOfLifeTeleport(player);
                            if (ring) {
                                player.getEquipment().remove(new Item(RING_OF_LIFE), EquipSlot.RING, true);
                                player.message("Your Ring of Life shatters as it teleports you to safety!");
                            } else if (defenceCape) {
                                player.message("Your Defence Cape's Ring of Life effects kicks in and teleports you to safety!");
                            } else {
                                player.message("Your Max Cape's Ring of Life effect kicks in and teleports you to safety!");
                            }
                        }
                    }
                }
            }

            target.takeHit();

        }

        // Return our hitDamage that may have been modified slightly.
        return damage;
    }


    /**
     * Checks if an entity is a valid target.
     */
    public static boolean validTarget(Entity attacker, Entity target) {
        //Check if target is online and alive
        if (!target.isRegistered() || !attacker.isRegistered() || target.hp() <= 0) {
            attacker.getCombat().reset();//Target not valid reset combat
            return false;
        }

        // Check if teleporting away/teleported away
        if (attacker.tile().distance(target.tile()) >= 16) {
            attacker.getCombat().reset();
            return false;
        }

        // Check if any of the two have wrong session state.
        if (target.isPlayer()) {
            if (target.getAsPlayer().getSession().getState() != SessionState.LOGGED_IN) {
                return false;
            }
        }
        if (attacker.isPlayer()) {
            if (attacker.getAsPlayer().getSession().getState() != SessionState.LOGGED_IN) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if an entity can reach a target.
     * <br> PERFORMANCE NOTE: this includes PROJECTILE PATH FINDING - try to only call it once per cycle!
     *
     * @param attacker The entity which wants to attack.
     * @param method   The combat type the attacker is using.
     * @param target   The victim.
     * @return True if attacker has the proper distance to attack, otherwise false.
     */
    public static boolean canReach(Entity attacker, CombatMethod method, Entity target) {
        Debugs.CB_FOLO.debug(attacker, "enter can reach via " + method + " vs " + target, target, true);
        if (attacker == null || target == null || method == null) {
            return false;
        }

        if (!validTarget(attacker, target)) {
            if (attacker.isPlayer()) {
                attacker.getAsPlayer().debugMessage("You do not have a valid target.");
            }
            return false;
        }

        if (target.getCombat() == null) {
            System.out.println("combat = " + target.getCombat().toString());
            return false;
        }

        if (target.dead()) {
            return false;
        }

        if (attacker.isNpc()) {
            if (attacker.getCombat().getTarget() == null) {
                if (!attacker.tile().isWithinDistance(target.tile())) {
                    return false;
                }
            }
            if (method instanceof CommonCombatMethod commonCombatMethod) {
                commonCombatMethod.set(attacker, target);
                if (attacker.isNpc() && !commonCombatMethod.inAttackRange()) {
                    DumbRoute.route(attacker, attacker.tile().getX(), attacker.tile().getY());
                    return false;
                }
            } else {
                DumbRoute.withinDistance(attacker, target, method.getAttackDistance(attacker));
                return true;
            }
        }
        return true;
    }

    private static long pjTimerForArena() {
        return 4_600L;
    }

    /**
     * Checks if an entity can attack a target.
     *
     * @param entity The entity which wants to attack.
     * @param method The combat type the attacker is using.
     * @param other  The victim.
     * @return True if attacker has the requirements to attack, otherwise false.
     */
    public static boolean canAttack(Entity entity, CombatMethod method, Entity other) {
        boolean b = canAttackInnerCheck(entity, method, other);
        return b;
    }

    private static boolean canAttackInnerCheck(Entity entity, CombatMethod method, Entity other) { // im expecting this is print for the player, that fact its not is odd as shit

        boolean message = false;

        if (entity == null || other == null) {
            Debugs.CMB.debug(entity, "attacker or target null", other, true);
            return false;
        }

        if (entity.dead() || other.dead()) {
            Debugs.CMB.debug(entity, "ded", other, true);
            return false;
        }

        if (entity.getIndex() == -1 || other.getIndex() == -1) { // Target logged off.
            Debugs.CMB.debug(entity, "attacker or target logged off", other, true);
            return false;
        }

        if (other instanceof NPC && ((NPC) other).cantInteract()) {
            Debugs.CMB.debug(entity, "cant interact", other, true);
            return false;
        }

        if (entity.stunned()) {
            // Calling stun interrupts combat, but this will force stop it too.
            Debugs.CMB.debug(entity, "cant attack stunned", other, true);
            return false;
        }

        if (entity.locked()) {
            Debugs.CMB.debug(entity, "cant attack locked", other, true);
            return false;
        }
        if (other.tile().level != entity.tile().level) {
            Debugs.CMB.debug(entity, "cant attack not on the same height level", other, true);
            return false;
        }

        if (entity.isNpc() && entity.getAsNpc().attackNpcListener != null && !entity.getAsNpc().attackNpcListener.allow(entity.getAsPlayer(), entity.getAsNpc(), message)) {
            Debugs.CMB.debug(entity, "kys 1", other, true);
            return false;
        }
        if (entity.isNpc() && !entity.npc().canAttack()) {
            Debugs.CMB.debug(entity, "kys 2", other, true);
            return false;
        }

        if (entity.isNpc() && entity.getAsNpc().getBotHandler() != null) {
            if (!(WildernessArea.inWilderness(entity.tile())) || !(WildernessArea.inWilderness(other.tile()))) {
                //Only reset combat if NPC is trying to attack player outside of wilderness. Don't reset players combat regardless of if they are attacker or target.
                Debugs.CMB.debug(entity, "bots cannot attack outside of wilderness.", other, true);
                return false;
            }
        }

        if (other.isNpc() && other.getAsNpc().getBotHandler() != null) {
            if (!(WildernessArea.inWilderness(entity.tile())) || !(WildernessArea.inWilderness(other.tile()))) {
                Debugs.CMB.debug(entity, "bots cannot be attacked outside of wilderness.", other, true);
                return false;
            }
        }

        if (!MemberZone.canAttack(entity, other)) {
            Debugs.CMB.debug(entity, "cant attack member zone npc", other, true);
            return false;
        }

        if (entity.isNpc() && entity.getAsNpc().def().gwdRoomNpc && !CombatFactory.bothInFixedRoom(entity, other)) {
            Debugs.CMB.debug(entity, "not in same room", other, true);
            return false;
        }

        if (other.isNpc()) {
            var npc = other.getAsNpc();
            // special case fuck knows why
            if (npc.hidden() || (entity.isPlayer() && npc.id() == 7707)) {
                Debugs.CMB.debug(entity, "cant attack idk what this is hidden" + npc.hidden(), other, true);
                return false;
            }

            if (npc.id() == 2668) // you can always attack combat dummies
                return true;
        }

        var wep = -1;

        if (entity.isPlayer()) {
            Player player = (Player) entity;
            wep = (entity.getAsPlayer()).getEquipment().get(EquipSlot.WEAPON) != null ? (entity.getAsPlayer()).getEquipment().get(EquipSlot.WEAPON).getId() : -1;

            // Check if we can attack in this area
            if (!ControllerManager.canAttack(player, other)) {
                entity.getMovementQueue().reset();
                Debugs.CMB.debug(entity, "kys 3", other, true);
                return false;
            }
            // Check if we're using a special attack..
            if (entity.isSpecialActivated() && entity.getAsPlayer().getCombatSpecial() != null) {
                if (entity.isPlayer()) {
                    if (player.getCombatSpecial() == CombatSpecial.DRAGON_DAGGER) {
                        player.getCombatSpecial().setDrainAmount(25);
                    }
                }
                // Check if we have enough special attack percentage.


                int specPercentage = entity.getSpecialAttackPercentage();

                //Make sure the player has enough special attack
                if (specPercentage < entity.getAsPlayer().getCombatSpecial().getDrainAmount()) {
                    entity.message("You do not have enough special attack energy left!");
                    entity.setSpecialActivated(false);
                    CombatSpecial.updateBar(entity.getAsPlayer());
                    Debugs.CMB.debug(entity, "nospec", other, true);
                    return false;
                }
            }
        }

        if (other.isPlayer()) {
            // Can't attack invis
            var them = other.getAsPlayer();
            if (them.looks().hidden()) {
                Debugs.CMB.debug(entity, "cant attack invisible target", other, true);
                return false;
            }

            // This check for being in an attackable zone has no message because this check is done elsewhere
            // Which gives a messages in those other places.
            // This check if for multi-target attacks like barrage/burst/chins where extra targets have to be checked
            // For combat validity.
            if (entity.isPlayer() && !WildernessArea.inAttackableArea(them)) {
                Debugs.CMB.debug(entity, "cant attack not in an attackable area", other, true);
                return false;
            }

            // Kraken attacking Players.
            if (entity.isNpc()) {
                if ((entity.getAsNpc()).id() == KrakenBoss.KRAKEN_NPCID || (entity.getAsNpc()).id() == KrakenBoss.TENTACLE_NPCID || (entity.getAsNpc()).id() == KrakenBoss.TENTACLE_NPCID || (entity.getAsNpc()).id() == TENTACLE_WHIRLPOOL) {
                    return true;
                }
            }
        } else if (other.isNpc()) {
            if ((other.getAsNpc()).getCombatInfo() == null) {
                entity.message("Without combat attributes this monster is unattackable.");
                Debugs.CMB.debug(entity, "missing npccbinfo", other, true);
                return false;
            } else if ((other.getAsNpc()).getCombatInfo().unattackable) {
                Debugs.CMB.debug(entity, "npc unattackable", other, true);
                entity.message("You cannot attack this monster.");
                return false;
            }

            //If we're attacking the Abyssal Sire and it's currently disoriented..
            if ((other.getAsNpc()).id() == 5886) {
                var combatState = other.<AbyssalSireState>getAttribOr(AttributeKey.ABYSSAL_SIRE_STATE, AbyssalSireState.DISORIENTED);

                if (combatState == AbyssalSireState.DISORIENTED) {
                    Debugs.CMB.debug(entity, "sire fixed", other, true);
                    entity.message("The sire is disoriented. Maybe you can do something useful while it's unable to control the tentacles.");
                    return false;
                }
            }
        }

        // The last time our target was attacked
        var targetLastAttackedTime = System.currentTimeMillis() - other.<Long>getAttribOr(AttributeKey.LAST_WAS_ATTACKED_TIME, 0L);

        if (entity.isPlayer() && other.isNpc()) {
            var oppNpc = other.getAsNpc();
            if (oppNpc.getCombatInfo() != null) {
                var noRequirementNeeded = entity.getAsPlayer().getSlayerRewards().getUnlocks().containsKey(SlayerConstants.NO_SLAYER_REQ);
                var slayerReq = Math.max(SlayerCreature.slayerReq(oppNpc.id()), oppNpc.getCombatInfo().slayerlvl);
                if (!noRequirementNeeded && slayerReq > (entity.getAsPlayer()).getSkills().level(Skills.SLAYER)) {
                    entity.message("You need a slayer level of " + slayerReq + " to harm this NPC.");
                    Debugs.CMB.debug(entity, "slayreq", other, true);
                    return false;
                }
            }

            if (wep == 10501) {
                entity.message("You can only pelt other players with a snowball.");
                Debugs.CMB.debug(entity, "snowball", other, true);
                return false;
            } else if (oppNpc.id() == 5914) {
                var respiratoryState = other.<AbyssalSireState>getAttribOr(AttributeKey.ABYSSAL_SIRE_STATE, AbyssalSireState.STASIS);

                if (respiratoryState == AbyssalSireState.STASIS) {
                    entity.message("I can't reach that!");
                    Debugs.CMB.debug(entity, "sire statis", other, true);
                    return false;
                }
            }

            // The kraken boss already has a focus. Multi doesn't matter now.
            if (oppNpc.id() == KRAKEN_WHIRLPOOL && oppNpc.transmog() == KrakenBoss.KRAKEN_NPCID) {
                if (other.<WeakReference<Entity>>getAttribOr(AttributeKey.TARGET, new WeakReference(null)).get() != entity && targetLastAttackedTime < 10000L) {
                    entity.message("The Kraken already has a target.");
                    Debugs.CMB.debug(entity, "kraken other", other, true);
                    return false;
                }
            }

        }

        // Last person to hit our target.
        var targetLastAttacker = other.<Entity>getAttrib(AttributeKey.LAST_DAMAGER);

        // Last time we were attacked
        var myLastAttackedTime = System.currentTimeMillis() - entity.<Long>getAttribOr(AttributeKey.LAST_WAS_ATTACKED_TIME, 0L);

        // Last person to hit us.
        var myLastAttacker = entity.<Entity>getAttrib(AttributeKey.LAST_DAMAGER);

        var me_edgepk = GameServer.properties().edgeDitch10secondPjTimerEnabled && WildernessArea.inside_extended_pj_timer_zone(entity.tile());
        var targ_edgepk = GameServer.properties().edgeDitch10secondPjTimerEnabled && WildernessArea.inside_extended_pj_timer_zone(other.tile());

        var myTimeToPj = me_edgepk ? 10_000L : 10_000L;
        var areaPjTimer = pjTimerForArena();
        if (areaPjTimer != 10_000L)
            myTimeToPj = areaPjTimer;
        var targTimeToPj = targ_edgepk ? 10_000L : 10_000L;
        if (areaPjTimer != 10_000L)
            targTimeToPj = areaPjTimer;

        if (entity.isPlayer() && other.isPlayer()) {
            Player playerAttacker = entity.getAsPlayer();

            //As of 06/07/2021 you can no longer use tridents and elder wand on players
            if (playerAttacker.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SWAMP) || playerAttacker.getEquipment().hasAt(EquipSlot.WEAPON, TRIDENT_OF_THE_SEAS) || playerAttacker.getEquipment().hasAt(EquipSlot.WEAPON, SANGUINESTI_STAFF)) {
                entity.message(Color.RED.wrap("You cannot use a this magic weapon against a player."));
                Debugs.CMB.debug(entity, "wep specific", other, true);
                return false;
            }

            // Staking security
            if (Dueling.not_in_area(entity, other, "You can't attack them.")) {
                Debugs.CMB.debug(entity, "duel1", other, true);
                return false;
            }
            if (Dueling.stake_not_started(entity, other)) {
                Debugs.CMB.debug(entity, "duel2", other, true);
                entity.message("The fight hasn't started yet!");
                return false;
            }
        }

        //If the NPC isn't visible we should no longer be attacking them.
        if (entity.isNpc()) {
            var npc = entity.getAsNpc();
            if (npc.hidden()) {
                Debugs.CMB.debug(entity, "cant attack npc not visible", other, true);
                return false;
            }
            if (other.isPlayer()) {
                if ((other.getAsPlayer()).getInterfaceManager().getMain() == BARROWS_REWARD_WIDGET) {
                    // When viewing the barrows loot interface, NPCs are not aggressive. Interesting eh.
                    Debugs.CMB.debug(entity, "cant attack player in barrows reward widget", other, true);
                    return false;
                }
            }
        }

        var inArena = Dueling.in_duel(entity);
        var lastDamager = other.<Entity>getAttribOr(AttributeKey.LAST_DAMAGER, null);

        // Level checks only apply to PvP
        if (other.isPlayer() && entity.isPlayer()) {
            // Is the player deep enough in the wilderness?
            // FFA Clan wars does not make any checks for levels. Free for all :)
            if (!inArena) {

                var oppWithinLvl = entity.getSkills().combatLevel() >= getLowestLevel(other, entity) &&
                    entity.getSkills().combatLevel() <= getHighestLevel(other, entity);

                if (!oppWithinLvl) {
                    entity.message((!WildernessArea.inWilderness(entity.tile())) ? "Your level difference is too great! You need to move deeper into the Wilderness." : "Your level difference is too great.");
                    Debugs.CMB.debug(entity, "lvldif", other, true);
                    return false;
                } else {
                    var withinLvl = (other.getSkills().combatLevel() >= getLowestLevel(entity, other) &&
                        other.getSkills().combatLevel() <= getHighestLevel(entity, other));
                    if (!withinLvl) {
                        entity.message((!WildernessArea.inWilderness(entity.tile())) ? "Your level difference is too great! You need to move deeper into the Wilderness." : "Your level difference is too great.");
                        Debugs.CMB.debug(entity, "lvldif2", other, true);
                        return false;
                    }
                }
            }
        }

        var isOpponentDead = myLastAttacker == null || myLastAttacker.dead();

        if (myLastAttackedTime < myTimeToPj && myLastAttacker != null && myLastAttacker != other && !isOpponentDead) {
            // Multiway check bro!
            if (entity.isPlayer()) {
                if (entity.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) != 1 && !MultiwayCombat.includes(other)) {
                    entity.message("You're already under attack.");
                    Debugs.CMB.debug(entity, "already under1", other, true);
                    return false;
                }
            } else {
                if (!MultiwayCombat.includes(entity)) {
                    entity.message("I'm already under attack.");
                    Debugs.CMB.debug(entity, "already under2", other, true);
                    return false;
                }
            }
        }

        // if (other.getMobName().toLowerCase().equalsIgnoreCase("origin3"))
        // System.out.println(targetLastAttacker+" vs "+ entity+" "+targetLastAttackedTime);
        if (targetLastAttackedTime < targTimeToPj && targetLastAttacker != null && targetLastAttacker != entity) {
            // Multiway check bro!
            if (other.isPlayer()) {
                if (other.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) != 1 && !MultiwayCombat.includes(other)) {
                    entity.message("Someone else is already fighting your opponent.");
                    Debugs.CMB.debug(entity, "in battle1", other, true);
                    return false;
                }
            } else {
                if (!MultiwayCombat.includes(other)) {
                    entity.message("Someone else is fighting that.");
                    Debugs.CMB.debug(entity, "in battle2", other, true);
                    return false;
                }
            }
        }

        if (other.isNpc() && other.getAsNpc().getCombatMethod() != null && other.getAsNpc().getCombatMethod().canMultiAttackInSingleZones()) {
            return true;
        }

        // Check immune npcs..
        if (other.isNpc()) {
            if (other instanceof NPC) {
                NPC npc = (NPC) other;
                if (npc.getBotHandler() != null) {
                    if (entity.isPlayer()) {
                        var oppWithinLvl = entity.getSkills().combatLevel() >= getLowestLevel(entity, npc) &&
                            entity.getSkills().combatLevel() <= getHighestLevel(entity, npc);

                        if (!oppWithinLvl) {
                            entity.message((!WildernessArea.inWilderness(entity.tile())) ? "Your level difference is too great! You need to move deeper into the Wilderness." : "Your level difference is too great.");
                            return false;
                        } else {
                            var withinLvl = npc.def().combatlevel >= getLowestLevel(entity, npc) && npc.def().combatlevel <= getHighestLevel(entity, npc);
                            if (!withinLvl) {
                                entity.message((!WildernessArea.inWilderness(entity.tile())) ? "Your level difference is too great! You need to move deeper into the Wilderness." : "Your level difference is too great.");
                                return false;
                            }
                        }
                    }
                }

                if (npc.getTimers().has(TimerKey.ATTACK_IMMUNITY)) {
                    if (entity.isPlayer()) {
                        ((Player) entity).message("This npc is currently immune to attacks.");
                    }
                    Debugs.CMB.debug(entity, "cant attack 7", other, true);
                    return false;
                }
            }
        }

        Debugs.CMB.debug(entity, "Passed canAttack checks", other, true);
        return true;
    }

    /**
     * handles printing/logging. will always print. should only be called from {@link Debugs#} which includes enabled check.
     *
     * @param attacker
     * @param s
     * @param victim
     * @param debugMessage
     */
    public static void debug(Entity attacker, String s, @Nullable Entity victim, boolean debugMessage) {
        debugMessage = true;
        boolean print = !GameServer.properties().production && (attacker != null && attacker.isPlayer() && attacker.getAsPlayer().getPlayerRights().isDeveloper(attacker.getAsPlayer())) ||
            (victim != null && victim.isPlayer() && victim.getAsPlayer().getPlayerRights().isDeveloper(victim.getAsPlayer()));
        boolean debug = false;
        Player player = null;
        if (attacker != null && attacker.isPlayer()) {
            player = attacker.getAsPlayer();
            debug = player.getAttribOr(AttributeKey.DEBUG_MESSAGES, debug);
        } else if (victim != null && victim.isPlayer()) {
            player = victim.getAsPlayer();
            debug = player.getAttribOr(AttributeKey.DEBUG_MESSAGES, debug);
        }
        String vicname = victim == null ? "?" : victim.getMobName();
        if (attacker != null && print && debug) {
            attacker.forceChat(GameEngine.gameTicksIncrementor + ": " + attacker.getMobName() + " v " + vicname + ": " + s);
        }
        if (attacker != null && debug && debugMessage && player != null) {
            System.out.println(GameEngine.gameTicksIncrementor + ": " + attacker.getMobName() + " v " + vicname + ": " + s);
        }
        if (attacker != null) {
            if (attacker.getLocalPlayers().stream().anyMatch(p -> p.tile().distance(attacker.tile()) < 10)) {
                logger.info(GameEngine.gameTicksIncrementor + ": " + attacker.getMobName() + " v " + vicname + ": " + s);
            }
        } else {
            logger.info(GameEngine.gameTicksIncrementor + ": " + s);
        }
    }

    /**
     * Adds a hit to a target's queue.
     *
     * @param hit
     */
    public static void addPendingHit(Hit hit) {
        Entity attacker = hit.getAttacker();
        Entity target = hit.getTarget();

        if (target.dead()) {
            return;
        }

        if (attacker != null && attacker.isNpc()) {
            NpcDefinition def = attacker.getAsNpc().def();
            String name = def.name;
            boolean isRevenant = attacker.isNpc() && name != null && name.toLowerCase().contains("revenant");
            if (isRevenant && target.isPlayer()) {
                Player playerTarget = target.getAsPlayer();
                var memberCave = playerTarget.tile().memberCave() && playerTarget.getMemberRights().isExtremeMemberOrGreater(playerTarget);
                if (playerTarget.getEquipment().hasAt(EquipSlot.HANDS, BRACELET_OF_ETHEREUM) || memberCave) {
                    int newDamage = hit.getDamage() * 25 / 100;
                    //Wearing the bracelet of ethereum no longer gives complete immunity of revenant attacks, now reducing incoming damage from them by 75%.
                    hit.setDamage(newDamage);
                }
            }
        }
        if (target.isNpc() && attacker != null && attacker.isPlayer()) {
            if (target instanceof NPC npc) {
                Player player = (Player) attacker;
                if (player.getCombat() == null)
                    return; // should never happen lol

                if (npc.capDamage() != -1 && hit.getDamage() > npc.capDamage()) {
                    hit.setDamage(npc.capDamage());
                }

                if (npc.getCombatMethod() instanceof Vorkath combatMethod) {
                    if (combatMethod.resistance != null) {
                        switch (combatMethod.resistance) {
                            case PARTIAL -> hit.setDamage((int) (hit.getDamage() * 0.5d));
                            case FULL -> hit.setDamage(0);
                        }
                    }
                }

                CombatSpell spell = player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() : player.getCombat().getAutoCastSpell();
                if (spell != null && spell.name().equals("Crumble Undead")) {
                    if (npc.def().name.equalsIgnoreCase("Zombified Spawn")) {
                        hit.setDamage(npc.hp());
                    }
                }

                //One in 175 chance of dealing the finishing blow. This does not count towards world bosses
                boolean ignore = npc.isWorldBoss() || npc.id() == NpcIdentifiers.TZTOKJAD || npc.id() == NpcIdentifiers.NEX || npc.id() == NpcIdentifiers.NEX_11280 || npc.id() == NpcIdentifiers.NEX_11282 || npc.id() == NpcIdentifiers.NEX_11281 || npc.id() == NpcIdentifiers.NEX_11279 || npc.id() == NpcIdentifiers.CORPOREAL_BEAST || npc.isCombatDummy() || (player.getRaids() != null && player.getRaids().raiding(player)) || npc.id() == THE_NIGHTMARE_9430;
                if (player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.KILL_BLOW) && World.getWorld().rollDie(350, 1) && !ignore && !npc.locked()) {
                    hit.setDamage(npc.hp());
                }

                if ((hit.getCombatType() == CombatType.RANGED || hit.getCombatType() == CombatType.MAGIC) && npc.id() == NpcIdentifiers.KALPHITE_QUEEN_6500) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.KALPHITE_QUEEN_6501 && hit.getCombatType() == CombatType.MELEE) {
                    hit.setDamage(0);
                }

                if (npc.id() == VANGUARD_7527 && hit.getCombatType() != CombatType.MELEE) {
                    hit.setDamage(0);
                }

                if (npc.id() == TEKTON_ENRAGED_7544 && hit.getCombatType() != CombatType.MELEE) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.VANGUARD_7528 && hit.getCombatType() != CombatType.RANGED) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.VANGUARD_7529 && hit.getCombatType() != CombatType.MAGIC) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.DEMONIC_GORILLA && hit.getCombatType() == CombatType.MELEE) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.DEMONIC_GORILLA_7145 && hit.getCombatType() == CombatType.RANGED) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.DEMONIC_GORILLA_7146 && hit.getCombatType() == CombatType.MAGIC) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.CORRUPTED_HUNLLEF && hit.getCombatType() == CombatType.MELEE) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.CORRUPTED_HUNLLEF_9036 && hit.getCombatType() == CombatType.RANGED) {
                    hit.setDamage(0);
                }

                if (npc.id() == NpcIdentifiers.CORRUPTED_HUNLLEF_9037 && hit.getCombatType() == CombatType.MAGIC) {
                    hit.setDamage(0);
                }

                boolean vetionHellhoundSpawned = npc.getAttribOr(AttributeKey.VETION_HELLHOUND_SPAWNED, false);

                if ((npc.id() == 6611 || npc.id() == 6612) && vetionHellhoundSpawned) {
                    hit.setDamage(0);
                    player.message("Vet'ion is immune until the hellhound spawns are killed off.");
                }
            }
        }

        if (attacker != null && attacker.isPlayer() && target.isNpc()) {
            CombatMethod method = CombatFactory.getMethod(target);
            if (method instanceof CommonCombatMethod o) {
                o.preDefend(hit);
            }
        }

        if (attacker != null && attacker.isPlayer() && !hit.reflected && hit.getCombatType() != null) {
            if (attacker instanceof Player playerAttacker) {
                // Reward the player experience for this attack (as long as it's not a combat dummy)..
                if (!target.isNpc() || !target.getAsNpc().isCombatDummy()) {
                    // while damage can appear visually higher than opponents HP, you cannot get that EXP (stops boosters)
                    addCombatXp(playerAttacker, target, Math.min(hit.getDamage(), target.hp()), hit.getCombatType(), playerAttacker.getCombat().getFightType().getStyle());
                }
            }
        }

        // If target is teleporting or needs placement
        // Dont continue to add the hit.
        if (target.isNullifyDamageLock() || target.isNeedsPlacement()) {
            return;
        }

        target.getCombat().getHitQueue().add(hit);
    }

    /**
     * Executes a hit that has been ticking until now.
     *
     * @param hit The QueueableHit to execute.
     */
    public static void executeHit(Hit hit) {
        final Entity attacker = hit.getAttacker();
        final Entity target = hit.getTarget();
        final CombatType combatType = hit.getCombatType();
        int damage = hit.getDamage();

        if (target.isNpc()) {
            NPC npc = (NPC) target;

            if (npc.getCombatInfo() == null) {
                //logger.info("Missing combat attributes for npc {}", npc.id());
                Utils.sendDiscordInfoLog("Missing combat attributes for npc " + npc.id());
                return;
            }
        }

        if (target.dead())
            return;

        // If target/attacker is dead, don't continue.
        // hits with no type and method are probably recoil, retribution, wrath, which can apply when the source is of course death
        if (attacker != null && attacker.dead() && combatType != null) {
            return;
        }

        // If target is teleporting or needs placement
        // Don't continue to add the hit.
        if (target.isNullifyDamageLock() || target.isNeedsPlacement()) {
            return;
        }

        if (target instanceof Player) {
            target.getAsPlayer().getPacketSender().sendInterfaceRemoval();
        }

        // Before target takes damage, manipulate the hit to handle
        // last-second effects
        hit = target.manipulateHit(hit);

        // Do block animation
        target.action.reset();
        if (attacker != null && attacker.isNpc() && hit.getCombatType() == CombatType.MAGIC && !target.getUpdateFlag().flagged(Flag.ANIMATION)) {
            target.animate(new Animation(target.getBlockAnim()));
        }

        // no need to process anything more
        if (hit.splatType == SplatType.NPC_HEALING_HITSPLAT) {
            hit.getTarget().heal(damage, 0);
            return;
        }

        if (target.isNpc() && attacker != null && attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            NPC npcTarget = target.getAsNpc();
            if (player.getRaids() != null) {
                if (player.getRaids().raiding(player)) {
                    player.getRaids().addDamagePoints(player, npcTarget, hit.getDamage());
                }
            }
        }

        if (target.isNpc() && attacker != null && attacker.isPlayer()) {
            Player player = attacker.getAsPlayer();
            NPC npcTarget = target.getAsNpc();
            if (player.getRaids() != null) {
                if (player.getRaids().raiding(player)) {
                    player.getRaids().addDamagePoints(player, npcTarget, hit.getDamage());
                }
            }
        }

        // Do other stuff for players..
        if (attacker != null && target.isPlayer()) {
            final Player targetAsPlayer = target.getAsPlayer();

            // Prayer effects
            if (hit.isAccurate()) {

                // full dh 25% chance to cause 15% of damage as additional hit
                if (Equipment.hasAmmyOfDamned(targetAsPlayer) && fullDharoks(targetAsPlayer) && Utils.rollDie(100, 25)) {
                    targetAsPlayer.hit(attacker, (int) (damage * 0.15));
                }

                handlePrayerEffects(attacker, target, damage, hit.getCombatType());
            }
        } //blood fury here

        if (hit.postDamage != null)
            hit.postDamage.accept(hit);
        CombatMethod method = CombatFactory.getMethod(target);
        if (method instanceof CommonCombatMethod o) {
            o.postDamage(hit);
        }

        if (attacker != null && attacker.isPlayer()) {
            Player player = (Player) attacker;
            if (hit.isAccurate() && combatType == CombatType.MELEE) {
                if (player.getEquipment().hasAt(EquipSlot.AMULET, AMULET_OF_BLOOD_FURY)) {
                    if (Utils.securedRandomChance(0.20F)) {
                        int healAmount = damage * 30 / 100;
                        player.heal(healAmount);
                        player.graphic(1542);
                    }
                }
            }
        }

        if (attacker != null && attacker.isPlayer() && target.isPlayer()) {
            Player player = (Player) attacker;
            if (hit.isAccurate() && combatType == CombatType.RANGED) {
                if (FormulaUtils.wearingFullKarils(player)) {
                    if (Utils.securedRandomChance(0.25F)) {
                        if (target.getSkills().level(Skills.AGILITY) > 20) {
                            target.graphic(401, GraphicHeight.HIGH, 0);
                            target.getSkills().setLevel(Skills.AGILITY, target.getSkills().level(Skills.AGILITY) - 20);
                        }
                    }
                }
            }
            if (hit.isAccurate() && combatType == CombatType.MELEE) {
                if (FormulaUtils.wearingFullVerac(player)) {
                    if (Utils.securedRandomChance(0.25F)) {
                        hit.ignorePrayer();
                    }
                }
                if (FormulaUtils.wearingFullTorag(player)) {
                    if (Utils.securedRandomChance(0.25F)) {
                        target.graphic(399, GraphicHeight.HIGH, 0);
                        target.getAttribOr(AttributeKey.RUN_ENERGY, -20);
                    }
                }
            }
        }

        if (attacker != null && attacker.isPlayer()) {
            Player player = (Player) attacker;
            if (hit.isAccurate() && combatType == CombatType.MELEE) {
                if (FormulaUtils.wearingFullGuthan(player)) {
                    if (Utils.securedRandomChance(0.25F)) {
                        target.graphic(398, GraphicHeight.LOW, 0);
                        player.heal(damage);
                    }
                }
            }
        }
        // Check for poisonous weapons..
        // And do other effects, such as barrows effects..
        if (attacker != null && attacker.isPlayer()) {


            Player attackerAsPlayer = attacker.getAsPlayer();

            if (attackerAsPlayer.getEquipment().wearingBeginnerWeapon()) {
                var hitsLeft = attackerAsPlayer.<Integer>getAttribOr(STARTER_WEAPON_DAMAGE, 500) - 1;
                attackerAsPlayer.putAttrib(STARTER_WEAPON_DAMAGE, hitsLeft);

                Item weapon = attackerAsPlayer.getEquipment().getWeapon();
                if (weapon != null && hitsLeft == 100)
                    attackerAsPlayer.message(Color.RED.wrap("You have 100 hits left on your " + weapon.name() + "."));
                if (weapon != null && hitsLeft == 50)
                    attackerAsPlayer.message(Color.RED.wrap("You have 50 hits left on your " + weapon.name() + "."));
                if (weapon != null && hitsLeft == 25)
                    attackerAsPlayer.message(Color.RED.wrap("You have 25 hits left on your " + weapon.name() + "."));

                if (weapon != null && hitsLeft <= 0) {
                    attackerAsPlayer.message(Color.RED.wrap("Your " + weapon.name() + " crumbles to dust."));
                    attackerAsPlayer.getEquipment().remove(weapon, EquipSlot.WEAPON, true);

                    //Always reset ranged weapon when unequipping weapon
                    if (attackerAsPlayer.getCombat().getRangedWeapon() != null) {
                        attackerAsPlayer.getCombat().setRangedWeapon(null);
                    }
                }
            }

            if (target.isNpc()) {
                NPC npc = target.getAsNpc();

                npc.onHit(hit);

                NpcDefinition defs = npc.def();

                if (defs == null) {
                    System.err.println("Error getting NPC defs for ID=" + npc.id() + " " + npc.getMobName());
                    return;
                }

                if (npc.getMobName().contains("turoth") || npc.getMobName().contains("kurask")) {
                    boolean block = false;
                    boolean leafbladedWeapon = attackerAsPlayer.getEquipment().contains(ItemIdentifiers.LEAFBLADED_SWORD) || attackerAsPlayer.getEquipment().contains(ItemIdentifiers.LEAFBLADED_BATTLEAXE) || attackerAsPlayer.getEquipment().contains(ItemIdentifiers.LEAFBLADED_SPEAR);
                    boolean leafbladedAmmo = attackerAsPlayer.getEquipment().getId(EquipSlot.AMMO) == BROAD_BOLTS || attackerAsPlayer.getEquipment().getId(EquipSlot.AMMO) == AMETHYST_BROAD_BOLTS || attackerAsPlayer.getEquipment().getId(EquipSlot.AMMO) == BROAD_ARROWS_4160;
                    boolean magicDart = attackerAsPlayer.getCombat().getCastSpell().spellId() == CombatSpells.MAGIC_DART.getSpell().spellId();
                    if (hit.getCombatType() == CombatType.MELEE && !leafbladedWeapon) {
                        block = true;
                    } else if (hit.getCombatType() == CombatType.RANGED && !leafbladedAmmo) {
                        block = true;
                    } else if (hit.getCombatType() == CombatType.MAGIC && !magicDart) {
                        block = true;
                    }

                    if (block) {
                        hit.setDamage(0);
                        attackerAsPlayer.message("This monster is only vulnerable to leaf-bladed melee weapons and broad ammunition.");
                    }
                }

                //Dustdevil
                if (npc.id() == NpcIdentifiers.DUST_DEVIL || npc.id() == NpcIdentifiers.DUST_DEVIL_7249 || npc.id() == NpcIdentifiers.CHOKE_DEVIL) {
                    if (attackerAsPlayer.getEquipment().getId(EquipSlot.HEAD) != FACEMASK && !attackerAsPlayer.getEquipment().wearingSlayerHelm()) {
                        hit.setDamage(0);
                        attackerAsPlayer.message("Blinded by the monster's dust, you miss your attack!");
                    }
                }

                // Gargs
                if (npc.hp() - damage <= 0) {
                    if (npc.id() == 412) {
                        damage = npc.hp();
                        hit.setDamage(npc.hp());

                        boolean isGmaul = Combat.gmauls.stream().anyMatch(granite_maul -> attackerAsPlayer.getEquipment().hasAt(EquipSlot.WEAPON, granite_maul));
                        if (attackerAsPlayer.inventory().contains(ROCK_HAMMER) || isGmaul) {
                            Gargoyle.smash(attackerAsPlayer, npc, false);
                        } else {
                            attackerAsPlayer.message("Gargoyles can only be killed using a Rockhammer.");
                            hit.setDamage(npc.hp() == 1 ? 0 : npc.hp() - 1);
                        }
                    }
                }

                // Desert lizards
                if (npc.hp() - damage <= 0) {
                    if (npc.id() == NpcIdentifiers.DESERT_LIZARD || npc.id() == NpcIdentifiers.DESERT_LIZARD_460 || npc.id() == NpcIdentifiers.DESERT_LIZARD_461) {
                        damage = npc.hp();
                        hit.setDamage(npc.hp());
                        DesertLizards.iceCooler(attackerAsPlayer, npc, false);
                    }
                }

                // rockslugs
                if (npc.hp() - damage <= 0) {
                    if (npc.id() == NpcIdentifiers.ROCKSLUG || npc.id() == NpcIdentifiers.ROCKSLUG_422) {
                        damage = npc.hp();
                        hit.setDamage(npc.hp());
                        if (attackerAsPlayer.inventory().contains(BAG_OF_SALT, 1) || attackerAsPlayer.getEquipment().contains(BRINE_SABRE)) {
                            if (!attackerAsPlayer.getEquipment().contains(BRINE_SABRE)) {
                                attackerAsPlayer.animate(1574);
                                npc.graphic(327);
                            }
                        } else {
                            attackerAsPlayer.message("Rockslugs can only be killed using a bag of salt.");
                            hit.setDamage(npc.hp() == 1 ? 0 : npc.hp() - 1);
                        }
                    }
                }
            }

            var weapon = attackerAsPlayer.getEquipment().get(EquipSlot.WEAPON);
            if (weapon != null && hit.getCombatType() == CombatType.MELEE && !hit.reflected) {

                // Abyssal tent and dds have 25% chance to poison. Poison strength varies.
                if (weapon.getId() == 12006 && World.getWorld().rollDie(100, 25)) {
                    target.poison(4);
                } else if ((weapon.getId() == 5698 || weapon.getId() == 13271) && World.getWorld().rollDie(100, 25)) {
                    target.poison(6);
                }
            }
            boolean venom = Venom.attempt(attacker, target, hit.getCombatType(), hit.isAccurate());
            if (venom)
                target.venom(attacker);
            // Handle barrows effects if damage is more than zero.
            if (hit.getDamage() > 0 && !hit.reflected) {
                if (Utils.getRandom(10) >= 8) {

                    // Apply Guthan's effect..
                    if (fullGuthans(attackerAsPlayer)) {
                        handleGuthans(attackerAsPlayer, target, hit.getDamage());
                    }

                    // Other barrows effects here..
                }
            }
        } else if (attacker != null && attacker.isNpc()) {
            NPC npc = attacker.getAsNpc();
            // Poison?
            if (hit.getDamage() > 0 && (npc.getCombatInfo() != null && npc.getCombatInfo().poisonous())) {
                var chance = npc.getCombatInfo().poisonchance;
                if (chance >= 100 || World.getWorld().rollDie(100, npc.getCombatInfo().poisonchance)) {
                    target.poison(npc.getCombatInfo().poison);
                }
            }
        }

        // Handle ring of recoil for target
        // Also handle vengeance for target
        if (attacker != null && hit.getDamage() > 0) {
            if (target.isPlayer()) {
                Player player = target.getAsPlayer();

                if (player.getEquipment().hasAt(EquipSlot.WEAPON, FROZEN_ABYSSAL_WHIP)) {
                    //20% chance to freeze your target.
                    if (Utils.rollDie(5, 1)) {
                        target.freeze(5, player);
                    }
                } else if (attacker.isPlayer() && attacker.getAsPlayer().getEquipment().hasAt(EquipSlot.WEAPON, VOLCANIC_ABYSSAL_WHIP)) {
                    //20% chance to set your target on fire.
                    if (Utils.rollDie(5, 1)) {
                        target.hit(player, Utils.random(1, 5));
                        if (target instanceof Player ptarg) {
                            ptarg.animate(3170);
                            ptarg.message("You feel a hot blaze caused by the lava whip.");
                        }
                    }
                }

                //We don't have to check if ring is null here we already check that in the main method.
                if (!hit.reflected)
                    handleRecoil(player, attacker, hit.getDamage());
            }

            boolean hasVengeance = target.getAttribOr(AttributeKey.VENGEANCE_ACTIVE, false);

            if (hasVengeance && !hit.reflected) {
                handleVengeance(target, attacker, hit.getDamage());
            }

            //Set the dmg at the bottom after are effects have been calculated for.
            if (attacker.isPlayer()) {
                Player player = attacker.getAsPlayer();
                if (hit.getCombatType() == CombatType.MELEE) {
                    player.putAttrib(AttributeKey.MELEE_DAMAGE, player.<Integer>getAttribOr(AttributeKey.MELEE_DAMAGE, 0) + damage);
                    player.putAttrib(AttributeKey.LAST_ATTACK_WAS_MELEE, true);
                    player.clearAttrib(AttributeKey.LAST_ATTACK_WAS_RANGED);
                    player.clearAttrib(AttributeKey.LAST_ATTACK_WAS_MAGIC);
                } else if (hit.getCombatType() == CombatType.RANGED) {
                    player.putAttrib(AttributeKey.RANGED_DAMAGE, player.<Integer>getAttribOr(AttributeKey.RANGED_DAMAGE, 0) + damage);
                    player.putAttrib(AttributeKey.LAST_ATTACK_WAS_RANGED, true);
                    player.clearAttrib(AttributeKey.LAST_ATTACK_WAS_MELEE);
                    player.clearAttrib(AttributeKey.LAST_ATTACK_WAS_MAGIC);
                } else if (hit.getCombatType() == CombatType.MAGIC) {
                    player.putAttrib(AttributeKey.MAGIC_DAMAGE, player.<Integer>getAttribOr(AttributeKey.MAGIC_DAMAGE, 0) + damage);
                    player.putAttrib(AttributeKey.LAST_ATTACK_WAS_MAGIC, true);
                    player.clearAttrib(AttributeKey.LAST_ATTACK_WAS_MELEE);
                    player.clearAttrib(AttributeKey.LAST_ATTACK_WAS_RANGED);
                }
            }
        }

        // Auto-retaliate
        if (attacker != null && !CombatFactory.isAttacking(target) && !hit.reflected) { // is mob fighting someone?
            if (attacker.isPlayer()) {
                //if (player.getCombat().autoRetaliate()) {
                // Players only auto retal the attacker when out of combat.
                boolean mayAttack = true;

                // Check attackability
                if (!canAttack(attacker, getMethod(attacker), target)) {
                    mayAttack = false;
                    attacker.getCombat().reset();//Can't attack, reset combat
                }

                if (mayAttack) {
                    target.autoRetaliate(attacker);
                }
                //}
            } else {
                NPC npc = (NPC) attacker;

                if (!npc.isCombatDummy())
                    target.autoRetaliate(attacker);
            }
        } else if (target.isNpc()) {
            // Npcs do switch aggro context if they get attacked.
            if (!target.getAsNpc().isCombatDummy())
                target.autoRetaliate(attacker);
        }

        if (attacker != null && !attacker.getCombat().getFightTimer().isRunning()) {
            attacker.getCombat().getFightTimer().start();
        }

        if (attacker != null && damage > 0) {
            if (target.isPlayer() && attacker.isNpc()) {//NPC PROTECTION PRAYERS
                if (attacker.getAsNpc().getBotHandler() != null) {
                    if (Prayers.usingPrayer(target, Prayers.getProtectingPrayer(hit.getCombatType()))) {
                        hit.setDamage((damage));
                    }
                } else {
                    boolean meleePrayer = hit.getCombatType() == CombatType.MELEE && Prayers.usingPrayer(target, PROTECT_FROM_MELEE);
                    boolean rangedPrayer = hit.getCombatType() == CombatType.RANGED && Prayers.usingPrayer(target, PROTECT_FROM_MISSILES);
                    boolean magicPrayer = hit.getCombatType() == CombatType.MAGIC && Prayers.usingPrayer(target, PROTECT_FROM_MAGIC);
                    if (!hit.prayerIgnored && (meleePrayer || rangedPrayer || magicPrayer))
                        hit.setDamage(0);
                }
            }
        }

        if (attacker != null) {
            // Add damage to target damage map
            target.getCombat().addDamage(attacker, hit.getDamage());

            if (target.isPlayer()) {
                target.getAsPlayer().setLastActiveOverhead();
                //Store the latest damage being dealt to a player.
                attacker.putAttrib(AttributeKey.LATEST_DAMAGE, hit.getDamage());
            }

            //Send the hit sound
            attacker.takehitSound(hit);
        }

        if (attacker instanceof Player damageDealer) {
            AchievementsManager.activate(damageDealer, Achievements.DAMAGE_DEALER_I, hit.getDamage());
            AchievementsManager.activate(damageDealer, Achievements.DAMAGE_DEALER_II, hit.getDamage());
            AchievementsManager.activate(damageDealer, Achievements.DAMAGE_DEALER_III, hit.getDamage());
            AchievementsManager.activate(damageDealer, Achievements.DAMAGE_DEALER_IV, hit.getDamage());
        }

        target.decrementHealth(hit);
    }

    private static void addCombatXp(Player player, Entity target, int hitDamage, CombatType style, FightStyle mode) {
        var hit = hitDamage;
        // Rather than putting hooks into every style of attacking a target, centralize it and stop giving XP
        // when certain styles can't damage an opponent.
        if (target.isNpc()) {
            var ntarg = target.getAsNpc();
            var id = ntarg.id();
            if (id == 5534) hit = 0; // Can't damage tentacles much
            if (id == 496) {
                if (ntarg.transmog() != 494) // Whirlpool as kraken
                    hit = 0;
                else if (style != CombatType.MAGIC)
                    hit = 0;
            }
            if (id == 319) { // Corp beast

            }
        }

        if (style == null)
            return;
        switch (style) {
            case MELEE -> {
                switch (mode) {
                    case ACCURATE -> {
                        player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                        player.getSkills().addXp(Skills.ATTACK, (hit * 4.0), !target.isPlayer());
                    }

                    case AGGRESSIVE -> {
                        player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                        player.getSkills().addXp(Skills.STRENGTH, (hit * 4.0), !target.isPlayer());
                    }

                    case DEFENSIVE -> {
                        player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                        player.getSkills().addXp(Skills.DEFENCE, (hit * 4.0), !target.isPlayer());
                    }

                    case CONTROLLED -> {
                        var xp = (hit * 4.0);
                        player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                        player.getSkills().addXp(Skills.ATTACK, xp / 3.0, !target.isPlayer());
                        player.getSkills().addXp(Skills.STRENGTH, xp / 3.0, !target.isPlayer());
                        player.getSkills().addXp(Skills.DEFENCE, xp / 3.0, !target.isPlayer());
                    }
                }
            }

            case RANGED -> {
                switch (mode) {
                    case ACCURATE, AGGRESSIVE -> {
                        player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                        player.getSkills().addXp(Skills.RANGED, (hit * 4.0), !target.isPlayer());
                    }

                    case DEFENSIVE -> {
                        player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                        player.getSkills().addXp(Skills.RANGED, (hit * 2.0), !target.isPlayer());
                        player.getSkills().addXp(Skills.DEFENCE, (hit * 2.0), !target.isPlayer());
                    }
                }
            }

            case MAGIC -> {
                CombatSpell spell = player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() : player.getCombat().getAutoCastSpell() != null ? player.getCombat().getAutoCastSpell() : player.getCombat().getPoweredStaffSpell() != null ? player.getCombat().getPoweredStaffSpell() : null;
                if (spell != null) {
                    if (hit > 0) {
                        // Accurate? Or normal autocast? aka non defensive
                        if (!player.<Boolean>getAttribOr(AttributeKey.DEFENSIVE_AUTOCAST, false)) {
                            player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                            player.getSkills().addXp(Skills.MAGIC, (hit * 2.0 + spell.baseExperience()), !target.isPlayer());
                        } else {
                            // Defensive autocast...
                            player.getSkills().addXp(Skills.HITPOINTS, (hit + (hit / 3.0)), !target.isPlayer());
                            player.getSkills().addXp(Skills.MAGIC, (hit + spell.baseExperience() + (hit / 3.0)), !target.isPlayer());
                            player.getSkills().addXp(Skills.DEFENCE, hit + spell.baseExperience(), !target.isPlayer());
                        }
                    } else {
                        //Splash should only give 52 exp..
                        player.getSkills().addXp(Skills.MAGIC, 1);
                    }
                }
            }
        }
    }

    /**
     * Checks if a mob is currently attacking.
     *
     * @param mob The mob to check for.
     * @return true if mob is attacking, false otherwise.
     */
    public static boolean isAttacking(Entity mob) {
        return mob.getCombat().getTarget() != null;
    }

    /**
     * Finds the last person that attacked the given Entity
     */
    public static Entity lastAttacker(Entity entity) {
        return entity.getAttribOr(AttributeKey.LAST_DAMAGER, null);
    }

    /**
     * Checks if a mob is currently in combat.
     *
     * @param mob The mob to check for.
     * @return true if mob is in combat, false otherwise.
     */
    @SuppressWarnings("unchecked")
    public static boolean inCombat(Entity mob) {
        var target = ((WeakReference<Entity>) mob.getAttribOr(AttributeKey.TARGET, new WeakReference<>(null))).get();
        var lastAttacked = System.currentTimeMillis() - mob.<Long>getAttribOr(AttributeKey.LAST_WAS_ATTACKED_TIME, 0L);
        var lastAttack = System.currentTimeMillis() - mob.<Long>getAttribOr(AttributeKey.LAST_ATTACK_TIME, 0L);
        return (mob.getTimers().has(TimerKey.COMBAT_LOGOUT) || lastAttack < 10000L || lastAttacked < 10000L) && target != null && mob != target;
    }

    public static boolean wasRecentlyAttacked(Entity mob) {
        var lastAttacked = System.currentTimeMillis() - mob.<Long>getAttribOr(AttributeKey.LAST_WAS_ATTACKED_TIME, 0L);
        return lastAttacked < 10000L;
    }

    /**
     * Disables protection prayers for a player.
     *
     * @param player The player to disable protection prayers for.
     */
    public static void disableProtectionPrayers(Player player) {
        disableProtectionPrayers(player, true, true);
    }

    public static void disableProtectionPrayers(Entity player, boolean sendMsg, boolean block) {
        if (block)
            player.getTimers().register(TimerKey.OVERHEADS_BLOCKED, 9);
        Prayers.deactivatePrayer(player, PROTECT_FROM_MAGIC);
        Prayers.deactivatePrayer(player, PROTECT_FROM_MISSILES);
        Prayers.deactivatePrayer(player, PROTECT_FROM_MELEE);
        Prayers.deactivatePrayer(player, RETRIBUTION);
        Prayers.deactivatePrayer(player, REDEMPTION);
        if (sendMsg)
            player.message("You have been disabled and can no longer use protection prayers.");
    }

    /**
     * Handles the item "Ring of Recoil" for a player. The item returns damage to
     * the attacker.
     *
     * @param player
     * @param attacker
     * @param damage
     */
    public static void handleRecoil(Player player, Entity attacker, int damage) {
        if (player == attacker) // dont recoil self-caused damage (rockcake)
            return;

        if (damage == 1) {
            return;
        }

        if (player.getEquipment().hasAt(EquipSlot.RING, RING_OF_SUFFERING)) {
            attacker.hit(player, damage > 10 ? (damage / 10) : 1, 0, null).setIsReflected().submit();
        }

        if (player.getEquipment().hasAt(EquipSlot.RING, RING_OF_SUFFERING_I)) {
            attacker.hit(player, damage > 10 ? (damage / 10) : 1, 0, null).setIsReflected().submit();
        }

        if (player.getEquipment().hasAt(EquipSlot.RING, 2550)) {
            int charges;
            charges = (int) player.getAttribOr(AttributeKey.RING_OF_RECOIL_CHARGES, 40) - 1;

            if (charges == 0) {
                player.putAttrib(AttributeKey.RING_OF_RECOIL_CHARGES, 40);
                player.getEquipment().remove(new Item(2550), true);
                player.message("<col=804080>Your ring of recoil has shattered!");
            } else {
                player.putAttrib(AttributeKey.RING_OF_RECOIL_CHARGES, charges);

                // hmm ok so this doesnt throw an exception because its adding a hit to
                // the Attacker, which is not the same Iterator i think
                attacker.hit(player, damage > 10 ? (damage / 10) : 1, 0, null).setIsReflected().submit();
                if (attacker.isNpc()) {
                    if (((NPC) attacker).id() == 319) {
                        //TODO update string corp beast lair, we don't have this string yet
                    }
                }
            }
        }
    }

    /**
     * Handles the spell "Vengeance" for a player. The spell returns damage to the
     * attacker.
     *
     * @param entity
     * @param attacker
     * @param damage
     */
    public static void handleVengeance(Entity entity, Entity attacker, int damage) {
        if (entity == attacker) // dont recoil self-caused damage (rockcake)
            return;

        entity.clearAttrib(AttributeKey.VENGEANCE_ACTIVE);
        attacker.hit(entity, (int) (damage * 0.75), 0, null).setIsReflected().submit();
        entity.forceChat("Taste Vengeance!");
    }

    /**
     * Handles the Guthan's set effect for a player. Wearing full guthan's has a
     * small chance of healing the player.
     *
     * @param player
     * @param target
     * @param damage
     */
    public static void handleGuthans(Player player, Entity target, int damage) {
        // Ammy of damned allows healing 10HP above base HP level
        player.heal(damage, Equipment.hasAmmyOfDamned(player) ? 10 : 0);
        target.graphic(398, GraphicHeight.HIGH, 0);
    }

    public static void unfreezeWhenOutOfRange(Entity entity) {
        Entity freezer = entity.getAttribOr(AttributeKey.FROZEN_BY, null);

        if (freezer == null) {
            return;
        }

        entity.getTimers().cancel(TimerKey.FROZEN);
        entity.getTimers().cancel(TimerKey.REFREEZE);

        if (entity.isPlayer()) {
            Player player = entity.getAsPlayer();
            player.getPacketSender().sendEffectTimer(0, EffectTimer.FREEZE);
        }
    }

    /**
     * Handles various prayer effects for the attacker and victim.
     *
     * @param attacker the attacker's combat builder.
     * @param target   the attacker's combat container.
     * @param damage   the total amount of damage dealt.
     */
    protected static void handlePrayerEffects(Entity attacker, Entity target, int damage, CombatType combatType) {
        // note : code on death wont work here. victim.hp will be its value BEFORE damage is actually taken
        if (attacker == null || target == null)
            return;
        // Prayer effects can only be done with victims that are players.
        if (target.isPlayer() && damage > 0) {
            Player victim = (Player) target;

            // The redemption (HEALING) prayer effect.
            if (Prayers.usingPrayer(victim, Prayers.REDEMPTION)
                && victim.hp() <= (victim.getSkills().xpLevel(Skills.HITPOINTS) / 10)) {
                int amountToHeal = (int) (victim.getSkills().xpLevel(Skills.PRAYER) * .25);
                victim.graphic(436, GraphicHeight.HIGH, 0);
                victim.getSkills().setLevel(Skills.PRAYER, 0);
                victim.getSkills().setLevel(Skills.HITPOINTS, victim.hp() + amountToHeal);
                victim.message("You've run out of prayer points!");
                Prayers.closeAllPrayers(victim);
                return;
            }

            // These last prayers can only be done with player attackers.
            if (attacker.isPlayer()) {
                Player playerAttacker = (Player) attacker;

                if (Prayers.usingPrayer(playerAttacker, Prayers.SMITE)) {
                    int removePoints = (int) (damage * 0.25F);

                    victim.getSkills().alterSkill(Skills.PRAYER, -removePoints);

                    int smiteDmg = (Integer) playerAttacker.getAttribOr(AttributeKey.SMITE_DAMAGE, 0) + removePoints;
                    playerAttacker.putAttrib(AttributeKey.SMITE_DAMAGE, smiteDmg);
                }
            }
        }
    }

    /**
     * Checks if a player has enough ammo to perform a ranged attack
     *
     * @param player The player to run the check for
     * @return True if player has ammo, false otherwise
     */
    public static boolean checkAmmo(Player player) {
        var weaponId = player.getEquipment().getId(EquipSlot.WEAPON);
        var wepName = weaponId == -1 ? "" : new Item(weaponId).definition(World.getWorld()).name.toLowerCase();
        var ammoId = player.getEquipment().getId(EquipSlot.AMMO);
        var ammoName = ammoId == -1 ? "" : new Item(ammoId).definition(World.getWorld()).name.toLowerCase();
        var crystalBow = (weaponId >= 4212 && weaponId <= 4223);
        var crawsBow = weaponId == ItemIdentifiers.CRAWS_BOW;
        var webWeaver = weaponId == ItemIdentifiers.WEBWEAVER_BOW;
        var bowOfFaerdhinen = FormulaUtils.hasBowOfFaerdhenin(player);

        WeaponType weaponType = player.getCombat().getWeaponType();

        if (!bowOfFaerdhinen && !webWeaver && !crawsBow && !crystalBow && ((weaponType == WeaponType.BOW || weaponType == WeaponType.CROSSBOW) && ammoName == "")) {
            player.message("There's no ammo left in your quiver.");
            return false;
        }

        // Check if we use the right type of ammo first
        if (weaponType == WeaponType.CROSSBOW) {
            if (!correctBolts(weaponId, ammoName)) {// bolt ammo
                player.message("You can't use that ammo with your crossbow.");
                return false;
            }
        } else if (weaponId == 19478 || weaponId == 19481) { // Light & heavy ballista
            if (!(ammoId >= 825 && ammoId <= 830) && !(ammoId >= 19484 && ammoId <= 19490)) {
                player.message("You can't use that ammo with your Ballista.");
                return false;
            }
        }

        if (!bowOfFaerdhinen && !webWeaver && !crawsBow && !crystalBow && (weaponType == WeaponType.BOW && !ammoName.contains("arrow"))) {
            player.message("You can't use that ammo with your bow.");
            return false;
        }

        if (wepName.replace(" (i)", "").endsWith("bow") && !webWeaver && !bowOfFaerdhinen && !crawsBow && !crystalBow && !wepName.startsWith("kari") && !wepName.contains("cross")) {
            // bolt ammo
            if (!correctArrows(weaponId, ammoId)) {
                var lit = (ammoName.contains("rack") || ammoName.contains("arrow")) && !ammoName.contains("arrows") ? "s" : "";
                player.message("You can't use " + ammoName + "" + lit + " with a " + wepName + ".");
                return false;
            }
        }
        return true;
    }

    public static boolean correctArrows(int bow1, int ammo) {
        var ok = false;
        for (BowReqs bow : BowReqs.values()) {
            if (bow1 == bow.bow) {
                for (int ammo2 : bow.ammo) {
                    if (ammo == ammo2) {
                        ok = true;
                    }
                }
            }
        }
        return ok;
    }

    public static boolean correctBolts(int bow1, String ammo) {
        var ok = false;
        for (CbowReqs bow : CbowReqs.values()) {
            if (bow1 == bow.getBow()) {
                String[] compatibleBolts = Arrays.deepToString(bow.getAmmo()).split(", ");
                for (String compatibleBolt : compatibleBolts) {
                    compatibleBolt = compatibleBolt.replaceAll("]", "").replaceAll("\\[", "");
                    if (ammo.toLowerCase().contains(compatibleBolt.toLowerCase())) {
                        ok = true;
                    }
                }
            }
        }
        return ok;
    }

    /**
     * Decrements the amount ammo the {@link Player} currently has equipped.
     *
     * @param player the player to decrement ammo for.
     */
    public static void decrementAmmo(Player player) {

        // Get the ranged weapon data
        final RangedWeapon rangedWeapon = player.getCombat().getRangedWeapon();

        if (rangedWeapon == null)
            return;

        Entity target = player.getCombat().getTarget();

        boolean targ_is_dummy = false;
        if (target instanceof NPC) {
            NPC npc = (NPC) target;

            if (npc.isCombatDummy()) {
                targ_is_dummy = true;
            }
        }
        boolean blowpipe = rangedWeapon == RangedWeapon.TOXIC_BLOWPIPE;
        boolean cryBow = rangedWeapon == RangedWeapon.CRYSTAL_BOW;
        boolean bowOfFaerdhinen = rangedWeapon == RangedWeapon.BOW_OF_FAERDHINEN;
        boolean crawsBow = rangedWeapon == RangedWeapon.CRAWS_BOW;
        boolean webWeaverBow = rangedWeapon == RangedWeapon.WEBWEAVER_BOW;
        boolean chins = rangedWeapon == RangedWeapon.CHINCHOMPA;
        boolean axes = rangedWeapon.getType() == RangedWeaponType.THROWING_AXES;
        boolean darts = rangedWeapon.getType() == RangedWeaponType.DARTS;
        boolean knifes = rangedWeapon.getType() == RangedWeaponType.KNIVES;
        boolean ballista = rangedWeapon.getType() == RangedWeaponType.BALLISTA;

        if (!blowpipe && !cryBow && !bowOfFaerdhinen && !crawsBow && !webWeaverBow && !targ_is_dummy) {

            // Determine which slot we are decrementing ammo from.
            int equipSlot = (chins || darts || knifes || axes) ? EquipSlot.WEAPON : EquipSlot.AMMO;

            Item ammo = player.getEquipment().get(equipSlot);

            // chins & javelins always lost. darts and ammo slot can be saved!
            boolean remove = (chins || ballista) || Equipment.notAvas(player);

            if (remove && ammo != null) {
                Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
                int ammoToRemove = weapon != null && Equipment.darkbow(weapon.getId()) ? 2 : 1; // dark bow removes 2 arrows

                // |------save------|---break---|-drop-|
                // As per wiki, normal avas is 72% save, 20% break, leaving 8% to go to the floor.
                // New avas is 80% save, 20% break, 0% floor
                double saveChance = player.getEquipment().containsAny(21898, 22109) ? 0.8001 : Equipment.wearingAvasEffect(player) ? 0.7201 : 0.0;
                double roll = Math.random();

                if (saveChance == 0.0 || roll >= saveChance) {
                    ammo.decrementAmountBy(ammoToRemove);
                    if (ammo.getAmount() == 0) {
                        player.getEquipment().remove(ammo, equipSlot, true);
                        player.getCombat().setRangedWeapon(null);
                    }

                    boolean skipAmmo = player.getDueling().inDuel() || player.getDueling().endingDuel() || ammo.getValue() <= 0;
                    // These items do not drop ammo to the floor if lost.
                    if (!chins && !ballista && ammo.getId() != 4740 && roll >= saveChance + 0.2) {
                        // 8% chance to drop if normal ava, otherwise will never drop (roll cannot be over 1.0)
                        if (player.tile().inArea(2269, 10023, 2302, 10046)) {
                            GroundItemHandler.createGroundItem(new GroundItem(new Item(ammo.getId(), 1), player.tile(), player));
                        } else {
                            if (!skipAmmo)
                                GroundItemHandler.createGroundItem(new GroundItem(new Item(ammo.getId(), 1), player.getCombat().getTarget().tile(), player));
                        }
                        if (weapon != null && Equipment.darkbow(weapon.getId())) {// support dropping 2nd arrow as well
                            if (player.tile().inArea(2269, 10023, 2302, 10046)) {
                                GroundItemHandler.createGroundItem(new GroundItem(new Item(ammo.getId(), 1), player.tile(), player));
                            } else {
                                //In these areas ammo is saved to return after the fight
                                if (!skipAmmo)
                                    GroundItemHandler.createGroundItem(new GroundItem(new Item(ammo.getId(), 1), player.getCombat().getTarget().tile(), player));
                            }
                        }
                    }
                }

                // Refresh the equipment interface.
                player.getEquipment().refresh();
            }
        }
    }

    /**
     * Determines if the entity is wearing full veracs.
     *
     * @param entity the entity to determine this for.
     * @return true if the player is wearing full veracs.
     */
    public static boolean fullVeracs(Entity entity) {
        return entity.isNpc() ? entity.getAsNpc().id() == NpcIdentifiers.VERAC_THE_DEFILED
            : entity.getAsPlayer().getEquipment().containsAll(4753, 4757, 4759, 4755);
    }

    /**
     * Determines if the entity is wearing full dharoks.
     *
     * @param entity the entity to determine this for.
     * @return true if the player is wearing full dharoks.
     */
    public static boolean fullDharoks(Entity entity) {
        return entity.isNpc() ? entity.getAsNpc().id() == NpcIdentifiers.DHAROK_THE_WRETCHED
            : entity.getAsPlayer().getEquipment().containsAll(4716, 4720, 4722, 4718);
    }

    /**
     * Determines if the entity is wearing full karils.
     *
     * @param entity the entity to determine this for.
     * @return true if the player is wearing full karils.
     */
    public static boolean fullKarils(Entity entity) {
        return entity.isNpc() ? entity.getAsNpc().id() == NpcIdentifiers.KARIL_THE_TAINTED
            : entity.getAsPlayer().getEquipment().containsAll(4732, 4736, 4738, 4734);
    }

    /**
     * Determines if the entity is wearing full ahrims.
     *
     * @param entity the entity to determine this for.
     * @return true if the player is wearing full ahrims.
     */
    public static boolean fullAhrims(Entity entity) {
        return entity.isNpc() ? entity.getAsNpc().id() == NpcIdentifiers.AHRIM_THE_BLIGHTED
            : entity.getAsPlayer().getEquipment().containsAll(4708, 4712, 4714, 4710);
    }

    public static boolean fullShayzien(Entity entity) {
        return entity.getAsPlayer().getEquipment().containsAll(13377, 13378, 13379, 13380, 13381);
    }

    /**
     * Determines if the entity is wearing full torags.
     *
     * @param entity the entity to determine this for.
     * @return true if the player is wearing full torags.
     */
    public static boolean fullTorags(Entity entity) {
        return entity.isNpc() ? entity.getAsNpc().def().name.equals("Torag the Corrupted")
            : entity.getAsPlayer().getEquipment().containsAll(4745, 4749, 4751, 4747);
    }

    /**
     * Determines if the entity is wearing full guthans.
     *
     * @param entity the entity to determine this for.
     * @return true if the player is wearing full guthans.
     */
    public static boolean fullGuthans(Entity entity) {
        return entity.isNpc() ? entity.getAsNpc().def().name.equals("Guthan the Infested")
            : entity.getAsPlayer().getEquipment().containsAll(4724, 4728, 4730, 4726);
    }

    public static int getLowestLevel(Entity entity, Entity target) {
        var combat = entity.isNpc() ? entity.getAsNpc().def().combatlevel : entity.getSkills().combatLevel();
        var wilderness = WildernessArea.wildernessLevel(entity.tile());
        var min = combat - wilderness;
        if (min < 3) {
            min = 3;
        }
        return min;
    }

    public static int getHighestLevel(Entity entity, Entity target) {
        var combat = entity.isNpc() ? entity.getAsNpc().def().combatlevel : entity.getSkills().combatLevel();
        var wilderness = WildernessArea.wildernessLevel(entity.tile());
        var max = combat + wilderness;
        if (max > 126) {
            max = 126;
        }
        return max;
    }

    public static Queue<Direction> pendingSpears(Entity p) {
        return p.getAttribOr(AttributeKey.SPEAR_MOVES, new LinkedList<Direction>());
    }

    /**
     * Records the game tick of when you attacked that target. Used for anti-rag mechanics.
     * Should always be called for any PvP attack - sits next to skulling code nicely.
     */
    public static void trackPvpAggression(Player player, Player target) {
        Map<Integer, Integer> historyMap = player.getAttribOr(AttributeKey.PVP_WILDY_AGGRESSION_TRACKER, new HashMap<Integer, Integer>());
        int targetEnterWildTick = target.getAttribOr(AttributeKey.INWILD, 0);
        int lastAgroToTarget = historyMap.getOrDefault(target.getIndex(), -1);
        // Either not tracked yet or they left the wildy and re-entered, so we'll start tracking this new battle from after they returned to the wild.
        if (lastAgroToTarget == -1 || (lastAgroToTarget != -1 && targetEnterWildTick > lastAgroToTarget)) {
            historyMap.put(target.getIndex(), World.getWorld().cycleCount());
            //player.debugMessage("Recorded battle start vs " + target.getUsername() + " on tick " + World.getWorld().getElapsedTicks() + ".");
        }
        player.putAttrib(AttributeKey.PVP_WILDY_AGGRESSION_TRACKER, historyMap);
    }

    public static boolean targetOk(Entity me, Entity target) {
        return targetOk(me, target, 16);
    }

    // While this returns true, the npc will continue aggressing a target.
    public static boolean targetOk(Entity me, Entity target, int maxdist) {
        var ok = target != null && !target.finished() && !me.finished() && !target.dead() && !me.dead() && me.hp() > 0 && (target.tile().distance(me.tile()) < maxdist && bothInFixedRoom(me, target));
        if (!ok) {
            me.stopActions(true);
        }
        return ok;
    }
}
