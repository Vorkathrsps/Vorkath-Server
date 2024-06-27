package com.cryptic.utility;

import com.cryptic.GameServer;
import com.cryptic.cache.DataStore;
import com.cryptic.cache.definitions.AnimationDefinition;
import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.npc.NPCCombatInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cryptic.SettingsKt.getCacheLocation;

public class NpcStatsConverter {

    static final Int2ObjectOpenHashMap<NPCCombatInfo> info = new Int2ObjectOpenHashMap<>();
    static final Map<String, List<Integer>> combatAnimationMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        MonsterLoader.load();
        GameServer.fileStore = new DataStore(getCacheLocation());
        GameServer.definitions = new DefinitionRepository();
        mapAnimsAndNpcs();
       /* ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<NPCCombatInfo> cbinfoList = new ArrayList<>();
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonPropertyValidator()).create();
        File defs = new File("data/combat/scriptloader");
        Int2ObjectOpenHashMap<NPCCombatInfo.Scripts> scriptmap = new Int2ObjectOpenHashMap<>();
        mapScripts(defs, gson, scriptmap);
        //Map<String, List<Integer>> tempIds = getLinkedNpcIdsForName();
        //System.out.println(tempIds);
        MonsterLoader.monsters.int2ObjectEntrySet().fastForEach(monster -> {
            var npc = monster.getValue();
            NPCCombatInfo cbinfo = new NPCCombatInfo();
            var defName = NpcDefinition.get(npc.getId()).name;
            int[] ids = null;
           *//* for (var e : tempIds.entrySet()) {
                if (e.getKey().equals(defName)) {
                    ids = new int[e.getValue().size()];
                    for (int i = 0; i < e.getValue().size(); i++) {
                        ids[i] = e.getValue().get(i);
                    }
                }
            }*//*
            cbinfo.setIds(new int[]{monster.getIntKey()});
            cbinfo.setAttackspeed(npc.getAttackSpeed());
            cbinfo.setAggressive(npc.isAggressive());
            cbinfo.setImmunePoison(npc.isImmunePoison());
            cbinfo.setImmuneVenom(npc.isImmuneVenom());
            cbinfo.setSlayerlvl(npc.getSlayerLevel());
            cbinfo.setMaxhit(npc.getMaxHit());
            cbinfo.setCombattype(determineCombatType(npc.getAttackType().toString()));
            var mappedScripts = scriptmap.get(monster.getIntKey());
            if (mappedScripts != null) cbinfo.setScripts(mappedScripts);
            var monsterName = monster.getValue().getName();
            var mappedAnims = combatAnimationMap.get(monsterName);
            List<Integer> result = new ArrayList<>();
            Set<Integer> finalResult = new HashSet<>(3);
            NPCCombatInfo.Animations animations = new NPCCombatInfo.Animations();
            if (mappedAnims != null) {
                for (Integer animIndex : mappedAnims) {
                    if (!result.contains(animIndex)) {
                        result.add(animIndex);
                    }
                }
                boolean add10 = true;
                boolean add6 = true;
                boolean add = true;

                for (var r : result.stream().sorted().toList()) {
                    if (finalResult.size() == 3) break;
                    var def = World.getWorld().definitions().get(AnimationDefinition.class, r);
                    if (def.priority == 10 && add10) {
                        finalResult.add(r);
                        add10 = false;
                    } else if (def.priority == 6 && add6) {
                        finalResult.add(r);
                        add6 = false;
                    } else if (def.priority == -1 && add) {
                        finalResult.add(r);
                        add = false;
                    }
                }
                var sortedResult = new ArrayList<>(finalResult.stream().toList());
                Comparator<Integer> priorityComparator = (r1, r2) -> {
                    AnimationDefinition def1 = World.getWorld().definitions().get(AnimationDefinition.class, r1);
                    AnimationDefinition def2 = World.getWorld().definitions().get(AnimationDefinition.class, r2);
                    int priority1 = (def1 != null) ? def1.priority : Integer.MIN_VALUE;
                    int priority2 = (def2 != null) ? def2.priority : Integer.MIN_VALUE;
                    return Integer.compare(priority1, priority2);
                };
                sortedResult.sort(priorityComparator);
                if (!sortedResult.isEmpty()) animations.setBlock(sortedResult.get(0));
                if (sortedResult.size() > 1) animations.setAttack(sortedResult.get(1));
                if (sortedResult.size() > 2) animations.setDeath(sortedResult.get(2));
                cbinfo.setAnimations(animations);
            }
            NPCCombatInfo.Bonuses bonuses = new NPCCombatInfo.Bonuses();
            bonuses.setAttack(npc.getAttackBonus());
            bonuses.setMagic(npc.getMagicBonus());
            bonuses.setRanged(npc.getRangedBonus());
            bonuses.setStrength(npc.getStrengthBonus());
            bonuses.setMagicstrength(npc.getAttackMagic());
            bonuses.setStabdefence(npc.getDefenceStab());
            bonuses.setSlashdefence(npc.getDefenceSlash());
            bonuses.setCrushdefence(npc.getDefenceCrush());
            bonuses.setRangeddefence(npc.getDefenceRanged());
            bonuses.setMagicdefence(npc.getDefenceMagic());
            bonuses.setRangestrength(npc.getAttackRanged());
            cbinfo.setBonuses(bonuses);
            NPCCombatInfo.Stats stats = new NPCCombatInfo.Stats();
            stats.setHitpoints(npc.getHitpoints());
            stats.setAttack(npc.getAttackLevel());
            stats.setStrength(npc.getStrengthLevel());
            stats.setDefence(npc.getDefenceLevel());
            stats.setMagic(npc.getMagicLevel());
            stats.setRanged(npc.getRangedLevel());
            cbinfo.setStats(stats);
            cbinfoList.add(cbinfo);
            info.put(npc.getId(), cbinfo);
        });
        SimpleModule module = new SimpleModule();
        module.addSerializer(NPCCombatInfo.class, new NPCCombatInfoSerializer());
        mapper.registerModule(module);
        mapper.writeValue(new File("C:\\Users\\gucci\\IdeaProjects\\Aelous210\\data\\combat\\stattest\\npc_stats.json"), cbinfoList);
        System.out.println("Successfully wrote all cbinfo objects to the JSON file.");*/
    }

    @NotNull
    private static Map<String, List<Integer>> getLinkedNpcIdsForName() {
        Map<String, List<Integer>> tempIds = new HashMap<>();
        MonsterLoader.monsters.int2ObjectEntrySet().fastForEach(monster -> {
            String npcName = monster.getValue().getName();
            if (!tempIds.containsKey(npcName)) {
                List<Integer> idList = new ArrayList<>();
                Set<String> visitedNames = new HashSet<>();
                collectNamesForSameId(npcName, idList, visitedNames);
                tempIds.put(npcName, idList);
            }
        });
        return tempIds;
    }

    private static void collectNamesForSameId(String currentName, List<Integer> collectedIds, Set<String> visitedNames) {
        if (visitedNames.contains(currentName)) {
            return;
        }
        visitedNames.add(currentName);
        MonsterLoader.monsters.int2ObjectEntrySet().fastForEach(monster -> {
            int npcId = monster.getValue().getId();
            String npcName = monster.getValue().getName();
            NpcDefinition npcDef = NpcDefinition.get(npcId);
            if (npcDef != null && npcName.equalsIgnoreCase(currentName)) {
                collectedIds.add(npcId);
                collectNamesForSameId(npcDef.name, collectedIds, visitedNames);
            }
        });
    }

    private static void mapAnimsAndNpcs() throws IOException {
        List<Integer> npcs = new ArrayList<>();
        String url = "https://media.z-kris.com/2024/03/220.4%20frame%20map.txt";
        String animsPattern = "Linked animations: \\[(.*?)\\]";
        String npcsPattern = "Linked NPCs: .*?\\((\\d+)\\)";
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        Map<Integer[], Integer[]> tempMap = new HashMap<>();
        Pattern animsPatternCompiled = Pattern.compile(animsPattern);
        Pattern npcsPatternCompiled = Pattern.compile(npcsPattern);
        Integer[] currentAnimations = null;
        Integer[] currentNpcs = null;
        while ((inputLine = reader.readLine()) != null) {
            Matcher animsMatcher = animsPatternCompiled.matcher(inputLine);
            Matcher npcsMatcher = npcsPatternCompiled.matcher(inputLine);
            if (animsMatcher.find()) {
                String indicesString = animsMatcher.group(1).trim();
                String[] indicesArray = indicesString.split(",\\s*");
                currentAnimations = new Integer[indicesArray.length];
                for (int i = 0; i < indicesArray.length; i++)
                    currentAnimations[i] = Integer.parseInt(indicesArray[i].trim());
                tempMap.put(currentAnimations, new Integer[0]);
            }
            while (npcsMatcher.find()) {
                if (currentAnimations != null) {
                    String indicesString = npcsMatcher.group(1).trim();
                    String[] indicesArray = indicesString.split(",\\s*");
                    if (currentNpcs == null) currentNpcs = new Integer[indicesArray.length];
                    for (int i = 0; i < indicesArray.length; i++) {
                        currentNpcs[i] = Integer.parseInt(indicesArray[i].trim());
                    }
                }
            }
            if (currentAnimations != null && currentNpcs != null) {
                var def = NpcDefinition.cached.get(currentNpcs.length);
                if (def.actions != null && def.isInteractable) {
                    tempMap.put(currentAnimations, currentNpcs);
                }
                currentAnimations = null;
                currentNpcs = null;
            }
        }
        reader.close();
        Map<String, List<Integer>> nonTranslatedTemp = new HashMap<>();
        if (!tempMap.isEmpty()) {
            for (Map.Entry<Integer[], Integer[]> entry : tempMap.entrySet()) {
                if (entry.getValue() == null) continue;
                for (var npc : entry.getValue()) {
                    List<Integer> nonTranslatedAnimations = new ArrayList<>();
                    var cached = NpcDefinition.cached.get(npc);
                    for (var anim : entry.getKey()) {
                        int[] translated = new int[]{cached.runAnimation, cached.standingAnimation, cached.walkingAnimation, cached.idleRotateLeftAnimation, cached.idleRotateRightAnimation, cached.rotate180Animation, cached.rotate180Animation, cached.rotate90LeftAnimation, cached.rotate90RightAnimation};
                        if (!ArrayUtils.contains(translated, anim)) {
                            nonTranslatedAnimations.add(anim);
                        }
                    }
                    if (!nonTranslatedAnimations.isEmpty()) {
                        var name = cached.name;
                        nonTranslatedTemp.put(name, nonTranslatedAnimations);
                    }
                }
            }
            for (var npc : nonTranslatedTemp.entrySet()) {
                List<Integer> sortedPriority = new ArrayList<>(npc.getValue());
                sortedPriority.sort((priority_one, priority_two) -> {
                    AnimationDefinition def1 = GameServer.definitions.get(AnimationDefinition.class, priority_one);
                    AnimationDefinition def2 = GameServer.definitions.get(AnimationDefinition.class, priority_two);
                    return Integer.compare(def2.priority, def1.priority);
                });
                combatAnimationMap.put(npc.getKey(), sortedPriority);

                List<Integer> result = new ArrayList<>();
                Set<Integer> finalResult = new HashSet<>(3);
                for (Integer animIndex : sortedPriority) {
                    if (!result.contains(animIndex)) {
                        result.add(animIndex);
                    }
                }
                boolean add10 = true;
                boolean add6 = true;
                boolean add = true;

                for (var r : result.stream().sorted().toList()) {
                    if (finalResult.size() == 3) break;
                    var def = World.getWorld().definitions().get(AnimationDefinition.class, r);
                    if (def.priority == 10 && add10) {
                        finalResult.add(r);
                        add10 = false;
                    } else if (def.priority == 6 && add6) {
                        finalResult.add(r);
                        add6 = false;
                    } else if (def.priority == -1 && add) {
                        finalResult.add(r);
                        add = false;
                    }
                }
                var sortedResult = new ArrayList<>(finalResult.stream().toList());
                Comparator<Integer> priorityComparator = (r1, r2) -> {
                    AnimationDefinition def1 = World.getWorld().definitions().get(AnimationDefinition.class, r1);
                    AnimationDefinition def2 = World.getWorld().definitions().get(AnimationDefinition.class, r2);
                    int priority1 = (def1 != null) ? def1.priority : Integer.MIN_VALUE;
                    int priority2 = (def2 != null) ? def2.priority : Integer.MIN_VALUE;
                    return Integer.compare(priority1, priority2);
                };
                sortedResult.sort(priorityComparator);
                for (var mapentry : combatAnimationMap.entrySet()) {
                    if (mapentry.getKey() == null) continue;
                    if (mapentry.getKey().contains(npc.getKey())) {
                        System.out.println("Linked NPC [" + mapentry.getKey() + "]");
                        System.out.println("block [ " + (!sortedResult.isEmpty() ? sortedResult.get(0) : -1) + " ]");
                        System.out.println("attack [ " + (sortedResult.size() > 1 ? sortedResult.get(1) : -1) + " ]");
                        System.out.println("death [ " + (sortedResult.size() > 2 ? sortedResult.get(2) : -1) + " ]");
                    }
                }
            }
        }
    }

    private static void mapScripts(File defs, Gson gson, Int2ObjectOpenHashMap<NPCCombatInfo.Scripts> scriptmap) throws FileNotFoundException {
        for (File def : Objects.requireNonNull(defs.listFiles())) {
            if (def.getName().endsWith(".json")) {
                NPCCombatInfo[] s = gson.fromJson(new FileReader(def), NPCCombatInfo[].class);
                if (s == null) continue;
                for (NPCCombatInfo cbInfo : s) {
                    if (cbInfo == null) continue;
                    if (cbInfo.scripts != null) {
                        for (int i : cbInfo.ids) {
                            scriptmap.put(i, cbInfo.scripts);
                        }
                    }
                }
            }
        }
    }

    private static CombatType determineCombatType(String attackType) {
        if (attackType.contains("crush") || attackType.contains("slash") || attackType.contains("melee")) {
            return CombatType.MELEE;
        } else if (attackType.contains("magic")) {
            return CombatType.MAGIC;
        } else if (attackType.contains("range")) {
            return CombatType.RANGED;
        } else {
            return CombatType.TYPELESS;
        }
    }

    static class NPCCombatInfoSerializer extends JsonSerializer<NPCCombatInfo> {
        @Override
        public void serialize(NPCCombatInfo cbinfo, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            if (cbinfo.ids != null && cbinfo.ids.length > 0) {
                jsonGenerator.writeArrayFieldStart("ids");
                for (int id : cbinfo.ids) {
                    jsonGenerator.writeNumber(id);
                }
                jsonGenerator.writeEndArray();
            }
            jsonGenerator.writeNumberField("respawntime", 15);
            if (cbinfo.attackspeed != 0) {
                jsonGenerator.writeNumberField("attackspeed", cbinfo.attackspeed);
            }
            if (cbinfo.aggressive) {
                jsonGenerator.writeBooleanField("aggressive", cbinfo.aggressive);
            }
            if (cbinfo.immunePoison) {
                jsonGenerator.writeBooleanField("immunePoison", cbinfo.immunePoison);
            }
            if (cbinfo.immuneVenom) {
                jsonGenerator.writeBooleanField("immuneVenom", cbinfo.immuneVenom);
            }
            if (cbinfo.slayerlvl != 0) {
                jsonGenerator.writeNumberField("slayerlvl", cbinfo.slayerlvl);
            }
            if (cbinfo.maxhit != 0) {
                jsonGenerator.writeNumberField("maxhit", cbinfo.maxhit);
            }
            if (cbinfo.combattype != null) {
                jsonGenerator.writeStringField("combattype", cbinfo.combattype.name());
            }
            if (cbinfo.stats != null) {
                jsonGenerator.writeObjectFieldStart("stats");
                jsonGenerator.writeNumberField("hitpoints", cbinfo.stats.hitpoints);
                jsonGenerator.writeNumberField("attack", cbinfo.stats.attack);
                jsonGenerator.writeNumberField("strength", cbinfo.stats.strength);
                jsonGenerator.writeNumberField("defence", cbinfo.stats.defence);
                jsonGenerator.writeNumberField("magic", cbinfo.stats.magic);
                jsonGenerator.writeNumberField("ranged", cbinfo.stats.ranged);
                jsonGenerator.writeEndObject();
            }
            if (cbinfo.bonuses != null) {
                jsonGenerator.writeObjectFieldStart("bonuses");
                jsonGenerator.writeNumberField("attack", cbinfo.bonuses.attack);
                jsonGenerator.writeNumberField("magic", cbinfo.bonuses.magic);
                jsonGenerator.writeNumberField("ranged", cbinfo.bonuses.ranged);
                jsonGenerator.writeNumberField("strength", cbinfo.bonuses.strength);
                jsonGenerator.writeNumberField("magicstrength", cbinfo.bonuses.magicstrength);
                jsonGenerator.writeNumberField("stabdefence", cbinfo.bonuses.stabdefence);
                jsonGenerator.writeNumberField("slashdefence", cbinfo.bonuses.slashdefence);
                jsonGenerator.writeNumberField("crushdefence", cbinfo.bonuses.crushdefence);
                jsonGenerator.writeNumberField("rangeddefence", cbinfo.bonuses.rangeddefence);
                jsonGenerator.writeNumberField("magicdefence", cbinfo.bonuses.magicdefence);
                jsonGenerator.writeNumberField("rangestrength", cbinfo.bonuses.rangestrength);
                jsonGenerator.writeEndObject();
            }
            if (cbinfo.scripts != null) {
                jsonGenerator.writeObjectFieldStart("scripts");
                jsonGenerator.writeStringField("combat", cbinfo.scripts.combat);
                jsonGenerator.writeEndObject();
            }
            if (cbinfo.animations != null) {
                jsonGenerator.writeObjectFieldStart("animations");
                jsonGenerator.writeNumberField("attack", cbinfo.animations.attack != 0 ? cbinfo.animations.attack : -1);
                jsonGenerator.writeNumberField("block", cbinfo.animations.block != 0 ? cbinfo.animations.block : -1);
                jsonGenerator.writeNumberField("death", cbinfo.animations.death != 0 ? cbinfo.animations.death : -1);
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndObject();
        }
    }
}
