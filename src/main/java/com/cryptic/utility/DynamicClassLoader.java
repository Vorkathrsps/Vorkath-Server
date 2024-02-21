package com.cryptic.utility;

import com.cryptic.annotate.CombatScript;
import com.cryptic.model.entity.combat.method.CombatMethod;
import io.github.classgraph.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;


/**
 * @Author: Origin
 * @Date: 2/19/24
 */
public class DynamicClassLoader {
    public static Object2ObjectMap<Class<? extends CombatMethod>, Class<? extends CombatMethod>> scriptmap = new Object2ObjectOpenHashMap<>();

    public static void load() {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().enableAnnotationInfo().scan()) {
            ClassInfoList directBoxes = scanResult.getClassesWithAnnotation(CombatScript.class);
            for (var d : directBoxes) {
                for (var n : d.getSubclasses().directOnly()) {
                    scriptmap.put((Class<? extends CombatMethod>) n.loadClass(), (Class<? extends CombatMethod>) n.loadClass());
                }
            }
        }
    }
}
