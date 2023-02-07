package com.aelous.model.entity.npc.pets;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.*;

import java.util.*;

import static com.aelous.model.entity.npc.pets.PetVarbits.*;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.LITTLE_NIGHTMARE_9399;

/**
 * Created by Bart on 2/18/2016.
 * <p>
 * UPDATE 14/10/2016 : npc_option ID no longer used in NPC Updating. See issue #557
 */
public enum PetDefinitions {

    YOUNGLLEF(ItemIdentifiers.YOUNGLLEF, NpcIdentifiers.YOUNGLLEF, -1),
    CORRUPTED_YOUNGLLEF(ItemIdentifiers.CORRUPTED_YOUNGLLEF, NpcIdentifiers.CORRUPTED_YOUNGLLEF, -1),
    THE_NIGHTMARE(ItemIdentifiers.LITTLE_NIGHTMARE, LITTLE_NIGHTMARE_9399, -1),
    TZKAL_ZUK(ItemIdentifiers.TZREKZUK, NpcIdentifiers.TZREKZUK_8011, NpcIdentifiers.JALNIBREK_7675, -1),
    JAL_NIB_REK(ItemIdentifiers.JALNIBREK, NpcIdentifiers.JALNIBREK_7675, NpcIdentifiers.TZREKZUK_8011, -1),

    //OSRS
    ABYSSAL_ORPHAN(ItemIdentifiers.ABYSSAL_ORPHAN, NpcIdentifiers.ABYSSAL_ORPHAN_5884, UNLOCKED_PET_ABYSSAL),
    CALLISTO_CUB(ItemIdentifiers.CALLISTO_CUB, NpcIdentifiers.CALLISTO_CUB_5558, UNLOCKED_PET_CALLISTO),
    HELLPUPPY(ItemIdentifiers.HELLPUPPY, NpcIdentifiers.HELLPUPPY_3099, UNLOCKED_PET_CERBERUS),
    KALPHITE_PRINCESS(ItemIdentifiers.KALPHITE_PRINCESS, NpcIdentifiers.KALPHITE_PRINCESS_6638, NpcIdentifiers.KALPHITE_PRINCESS, UNLOCKED_PET_KQ),
    KALPHITE_PRINCESS_2(ItemIdentifiers.KALPHITE_PRINCESS_12654, NpcIdentifiers.KALPHITE_PRINCESS, NpcIdentifiers.KALPHITE_PRINCESS_6638, UNLOCKED_PET_KQ),
    PET_CHAOS_ELEMENTAL(ItemIdentifiers.PET_CHAOS_ELEMENTAL, NpcIdentifiers.CHAOS_ELEMENTAL_JR, UNLOCKED_PET_CHAOS_ELE),
    PET_DAGANNOTH_PRIME(ItemIdentifiers.PET_DAGANNOTH_PRIME, NpcIdentifiers.DAGANNOTH_PRIME_JR_6629, UNLOCKED_PET_DAGANNOTH_PRIME),
    PET_DAGANNOTH_REX(ItemIdentifiers.PET_DAGANNOTH_REX, NpcIdentifiers.DAGANNOTH_REX_JR, UNLOCKED_PET_DAGANNOTH_REX),
    PET_DAGGANOTH_SUPREME(ItemIdentifiers.PET_DAGANNOTH_SUPREME, NpcIdentifiers.DAGANNOTH_SUPREME_JR_6628, UNLOCKED_PET_DAGANNOTH_SUPREME),
    PET_DARK_CORE(ItemIdentifiers.PET_DARK_CORE, NpcIdentifiers.DARK_CORE, NpcIdentifiers.CORPOREAL_CRITTER_8010, UNLOCKED_PET_CORE),
    PET_GENERAL_GRAARDOR(ItemIdentifiers.PET_GENERAL_GRAARDOR, NpcIdentifiers.GENERAL_GRAARDOR_JR, UNLOCKED_PET_GRAARDOR),
    PET_KRIL_TSUTSAROTH(ItemIdentifiers.PET_KRIL_TSUTSAROTH, NpcIdentifiers.KRIL_TSUTSAROTH_JR, UNLOCKED_PET_KRIL),
    PET_KREEARRA(ItemIdentifiers.PET_KREEARRA, NpcIdentifiers.KREEARRA_JR, UNLOCKED_PET_KREEARRA),
    PET_ZILYANA(ItemIdentifiers.PET_ZILYANA, NpcIdentifiers.ZILYANA_JR, UNLOCKED_PET_ZILYANA),
    PET_KRAKEN(ItemIdentifiers.PET_KRAKEN, NpcIdentifiers.KRAKEN_6640, UNLOCKED_PET_KRAKEN),
    PET_PENANCE_QUEEN(ItemIdentifiers.PET_PENANCE_QUEEN, NpcIdentifiers.PENANCE_PET_6674, UNLOCKED_PET_PENANCE_QUEEN),
    PET_SMOKE_DEVIL(ItemIdentifiers.PET_SMOKE_DEVIL, NpcIdentifiers.SMOKE_DEVIL_6639, UNLOCKED_PET_SMOKE_DEVIL),
    SNAKELING(ItemIdentifiers.PET_SNAKELING, NpcIdentifiers.SNAKELING_2130, NpcIdentifiers.SNAKELING_2128, UNLOCKED_PET_ZULRAH),
    MAGMA_SNAKELING(ItemIdentifiers.PET_SNAKELING_12939, NpcIdentifiers.SNAKELING_2131, NpcIdentifiers.SNAKELING_2129, UNLOCKED_PET_ZULRAH),
    TANZANITE_SNAKELING(ItemIdentifiers.PET_SNAKELING_12940, NpcIdentifiers.SNAKELING_2132, NpcIdentifiers.SNAKELING_2127, UNLOCKED_PET_ZULRAH),
    PRINCE_BLACK_DRAGON(ItemIdentifiers.PRINCE_BLACK_DRAGON, NpcIdentifiers.PRINCE_BLACK_DRAGON, UNLOCKED_PET_KBD),
    SCORPIAS_OFFSPRING(ItemIdentifiers.SCORPIAS_OFFSPRING, NpcIdentifiers.SCORPIAS_OFFSPRING_5561, UNLOCKED_PET_SCORPIA),
    TZREK_JAD(ItemIdentifiers.TZREKJAD, NpcIdentifiers.TZREKJAD_5893, UNLOCKED_PET_JAD),
    VENENATIS_SPIDERLING(ItemIdentifiers.VENENATIS_SPIDERLING, NpcIdentifiers.VENENATIS_SPIDERLING_5557, UNLOCKED_PET_VENENATIS),
    VETION_JR_PURPLE(ItemIdentifiers.VETION_JR, NpcIdentifiers.VETION_JR_5559, NpcIdentifiers.VETION_JR_5560, UNLOCKED_PET_VETION_PURPLE),
    VETION_JR_ORANGE(ItemIdentifiers.VETION_JR_13180, NpcIdentifiers.VETION_JR_5560, NpcIdentifiers.VETION_JR_5559, UNLOCKED_PET_VETION_PURPLE),
    NOON(ItemIdentifiers.NOON, NpcIdentifiers.NOON_7892, NpcIdentifiers.MIDNIGHT_7893, UNLOCKED_PET_NOON),
    MIDNIGHT(ItemIdentifiers.MIDNIGHT, NpcIdentifiers.MIDNIGHT_7893, NpcIdentifiers.NOON_7892, UNLOCKED_PET_NOON),
    SKOTOS(ItemIdentifiers.SKOTOS, NpcIdentifiers.SKOTOS_7671, UNLOCKED_PET_SKOTOS),
    VORKI(ItemIdentifiers.VORKI, NpcIdentifiers.VORKI_8029, UNLOCKED_PET_VORKI),
    OLMLET(ItemIdentifiers.OLMLET, NpcIdentifiers.OLMLET_7520, UNLOCK_PET_OLMLET),
    PUPPADILE(ItemIdentifiers.PUPPADILE, NpcIdentifiers.PUPPADILE_8201, NpcIdentifiers.OLMLET_7520, UNLOCK_PET_OLMLET),
    TEKTINY(ItemIdentifiers.TEKTINY, NpcIdentifiers.TEKTINY_8202, NpcIdentifiers.OLMLET_7520, UNLOCK_PET_OLMLET),
    VANGUARD(ItemIdentifiers.VANGUARD, NpcIdentifiers.VANGUARD_8203, NpcIdentifiers.OLMLET_7520, UNLOCK_PET_OLMLET),
    VASA_MINIRIO(ItemIdentifiers.VASA_MINIRIO, NpcIdentifiers.VASA_MINIRIO_8204, NpcIdentifiers.OLMLET_7520, UNLOCK_PET_OLMLET),
    VESPINA(ItemIdentifiers.VESPINA, NpcIdentifiers.VESPINA_8205, NpcIdentifiers.OLMLET_7520, UNLOCK_PET_OLMLET),
    IKKLE_HYDRA_GREEN(ItemIdentifiers.IKKLE_HYDRA, NpcIdentifiers.IKKLE_HYDRA, NpcIdentifiers.IKKLE_HYDRA_8493, -1),
    IKKLE_HYDRA_BLUE(ItemIdentifiers.IKKLE_HYDRA_22748, NpcIdentifiers.IKKLE_HYDRA_8493, NpcIdentifiers.IKKLE_HYDRA_8494, -1),
    IKKLE_HYDRA_RED(ItemIdentifiers.IKKLE_HYDRA_22750, NpcIdentifiers.IKKLE_HYDRA_8494, NpcIdentifiers.IKKLE_HYDRA_8495, -1),
    IKKLE_HYDRA_BLACK(ItemIdentifiers.IKKLE_HYDRA_22752, NpcIdentifiers.IKKLE_HYDRA_8495, NpcIdentifiers.IKKLE_HYDRA, -1),

    //Random junk pets
    BABY_CHINCHOMPA_BLACK(ItemIdentifiers.BABY_CHINCHOMPA_13325, NpcIdentifiers.BABY_CHINCHOMPA_6758, NpcIdentifiers.BABY_CHINCHOMPA_6759, UNLOCKED_PET_CHINCHOMPA),
    BABY_CHINCHOMPA_GREY(ItemIdentifiers.BABY_CHINCHOMPA_13324, NpcIdentifiers.BABY_CHINCHOMPA_6757, NpcIdentifiers.BABY_CHINCHOMPA_6758, UNLOCKED_PET_CHINCHOMPA),
    BABY_CHINCHOMPA_RED(ItemIdentifiers.BABY_CHINCHOMPA, NpcIdentifiers.BABY_CHINCHOMPA_6756, NpcIdentifiers.BABY_CHINCHOMPA_6757, UNLOCKED_PET_CHINCHOMPA),
    BABY_CHINCHOMPA_YELLOW(ItemIdentifiers.BABY_CHINCHOMPA_13326, NpcIdentifiers.BABY_CHINCHOMPA_6759, NpcIdentifiers.BABY_CHINCHOMPA_6756, UNLOCKED_PET_CHINCHOMPA),
    BEAVER(ItemIdentifiers.BEAVER, NpcIdentifiers.BEAVER_6724, UNLOCKED_PET_BEAVER),
    HERON(ItemIdentifiers.HERON, NpcIdentifiers.HERON_6722, UNLOCKED_PET_HERON),
    ROCK_GOLEM(ItemIdentifiers.ROCK_GOLEM, NpcIdentifiers.ROCK_GOLEM_7439, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_TIN(21187, NpcIdentifiers.ROCK_GOLEM_7440, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_COPPER(21188, NpcIdentifiers.ROCK_GOLEM_7441, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_IRON(21189, NpcIdentifiers.ROCK_GOLEM_7442, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_BLURITE(21190, NpcIdentifiers.ROCK_GOLEM_7443, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_SILVER(21191, NpcIdentifiers.ROCK_GOLEM_7444, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_DAEYALT(21360, -1, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_COAL(21192, NpcIdentifiers.ROCK_GOLEM_7445, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_ELEMENTAL(21359, NpcIdentifiers.ROCK_GOLEM_7737, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_GOLD(21193, NpcIdentifiers.ROCK_GOLEM_7446, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_GRANITE(21195, NpcIdentifiers.ROCK_GOLEM_7448, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_MITHRIL(21194, NpcIdentifiers.ROCK_GOLEM_7447, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_LOVAKITE(21358, NpcIdentifiers.ROCK_GOLEM_7736, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_ADAMANTITE(21196, NpcIdentifiers.ROCK_GOLEM_7449, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_RUNITE(21197, NpcIdentifiers.ROCK_GOLEM_7450, UNLOCKED_PET_GOLEM),
    ROCK_GOLEM_AMETHYST(21340, NpcIdentifiers.ROCK_GOLEM_7711, UNLOCKED_PET_GOLEM),
    GIANT_SQUIRREL(ItemIdentifiers.GIANT_SQUIRREL, NpcIdentifiers.GIANT_SQUIRREL_7351, UNLOCK_PET_GIANT_SQUIRREL),
    TANGLEROOT(ItemIdentifiers.TANGLEROOT, NpcIdentifiers.TANGLEROOT_7352, UNLOCK_PET_TANGLEROOT),
    ROCKY(ItemIdentifiers.ROCKY, NpcIdentifiers.ROCKY_7353, UNLOCKED_PET_ROCKY),
    RIFT_GUARDIAN_FIRE(ItemIdentifiers.RIFT_GUARDIAN, NpcIdentifiers.RIFT_GUARDIAN_7354, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_AIR(ItemIdentifiers.RIFT_GUARDIAN_20667, NpcIdentifiers.RIFT_GUARDIAN_7355, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_MIND(ItemIdentifiers.RIFT_GUARDIAN_20669, NpcIdentifiers.RIFT_GUARDIAN_7356, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_WATER(ItemIdentifiers.RIFT_GUARDIAN_20671, NpcIdentifiers.RIFT_GUARDIAN_7357, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_EARTH(ItemIdentifiers.RIFT_GUARDIAN_20673, NpcIdentifiers.RIFT_GUARDIAN_7358, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_BODY(ItemIdentifiers.RIFT_GUARDIAN_20675, NpcIdentifiers.RIFT_GUARDIAN_7359, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_COSMIC(ItemIdentifiers.RIFT_GUARDIAN_20677, NpcIdentifiers.RIFT_GUARDIAN_7360, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_CHAOS(ItemIdentifiers.RIFT_GUARDIAN_20679, NpcIdentifiers.RIFT_GUARDIAN_7361, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_NATURE(ItemIdentifiers.RIFT_GUARDIAN_20681, NpcIdentifiers.RIFT_GUARDIAN_7362, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_LAW(ItemIdentifiers.RIFT_GUARDIAN_20683, NpcIdentifiers.RIFT_GUARDIAN_7363, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_DEATH(ItemIdentifiers.RIFT_GUARDIAN_20685, NpcIdentifiers.RIFT_GUARDIAN_7364, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_SOUL(ItemIdentifiers.RIFT_GUARDIAN_20687, NpcIdentifiers.RIFT_GUARDIAN_7365, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_ASTRAL(ItemIdentifiers.RIFT_GUARDIAN_20689, NpcIdentifiers.RIFT_GUARDIAN_7366, UNLOCK_PET_RIFT_GUARDIAN),
    RIFT_GUARDIAN_BLOOD(ItemIdentifiers.RIFT_GUARDIAN_20691, NpcIdentifiers.RIFT_GUARDIAN_7367, UNLOCK_PET_RIFT_GUARDIAN),
    WRATH_RIFT_GUARDIAN(ItemIdentifiers.RIFT_GUARDIAN_21990, NpcIdentifiers.RIFT_GUARDIAN_8024, UNLOCK_PET_RIFT_GUARDIAN),
    HERBI(ItemIdentifiers.HERBI, NpcIdentifiers.HERBI_7760, UNLOCKED_PET_HERBI),
    BLOODHOUND(ItemIdentifiers.BLOODHOUND, NpcIdentifiers.BLOODHOUND_7232, UNLOCK_PET_BLOODHOUND),
    CHOMPY_CHICK(ItemIdentifiers.CHOMPY_CHICK, NpcIdentifiers.CHOMPY_CHICK_4002, UNLOCKED_PET_CHOMPY);

    public int item;
    public int npc;
    public int morphId;
    public int varbit;

    PetDefinitions(int item, int npc, int morphId, int varbit) {
        this.item = item;
        this.npc = npc;
        this.morphId = morphId;
        this.varbit = varbit;
    }

    PetDefinitions(int item, int npc, int varbit) {
        this.item = item;
        this.npc = npc;
        this.varbit = varbit;
    }

    private static final Map<Integer, PetDefinitions> MAP_BY_NPC = new HashMap<>();

    static {
        for (PetDefinitions petDefinitions : values()) {
            MAP_BY_NPC.put(petDefinitions.npc, petDefinitions);
        }
    }

    public static Optional<PetDefinitions> fromNpc(int identifier) {
        return Arrays.stream(values()).filter(s -> s.npc == identifier).findFirst();
    }

    public static PetDefinitions getByNpc(int npc) {
        return MAP_BY_NPC.get(npc);
    }

    public static PetDefinitions getPetByItem(int item) {
        for (PetDefinitions petDefinitions : values()) {
            if (petDefinitions.item == item) {
                return petDefinitions;
            }
        }
        return null;
    }

    public boolean canMorph() {
        return morphId > 0;
    }
}
