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
        try (var scanResult = new ClassGraph().enableClassInfo().enableExternalClasses().scan()) {
            ClassInfoList classes = scanResult.getClassesImplementing(CombatMethod.class);
            for (var c : classes) {
                if (c.isAbstract()) continue;
                Class<? extends CombatMethod> loadedClass = c.loadClass(CombatMethod.class);
                scriptmap.put(loadedClass, loadedClass);
            }
        }
    }
}
