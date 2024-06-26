package com.cryptic.utility;

import com.cryptic.GameConstants;
import io.github.classgraph.*;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sharky
 * @Since July 03, 2023
 */
public class Reflection {

    public static Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotationClass) {
        Set<Method> methods = new ObjectOpenHashSet<>();
        try (ScanResult result = new ClassGraph()
            .enableMethodInfo()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(GameConstants.PACKAGE)
            .scan()) {
            ClassInfoList classInfos = result.getClassesWithMethodAnnotation(annotationClass);
            for (ClassInfo classInfo : classInfos) {
                MethodInfoList methodInfos = classInfo.getMethodInfo();
                for (MethodInfo methodInfo : methodInfos) {
                    if (methodInfo.hasAnnotation(annotationClass)) {
                        Method method = methodInfo.loadClassAndGetMethod();
                        methods.add(method);
                    }
                }
            }
        }
        return methods;
    }

    public static Set<Method> getMethodsAnnotatedWith(String packageName, Class<? extends Annotation> annotationClass) {
        return new Reflections(packageName, new MethodAnnotationsScanner()).getMethodsAnnotatedWith(annotationClass);
    }

    public static <T> Set<Class<? extends T>> getSubClasses(Class<T> clazz) {
        return getSubClasses(clazz, true);
    }

    public static <T> Set<Class<? extends T>> getSubClasses(Class<T> clazz, boolean includeAbstract) {
        Set<Class<? extends T>> subClasses = getSubClasses(GameConstants.PACKAGE, clazz);
        if (!includeAbstract)
            subClasses = subClasses.stream().filter(it -> !Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers())).collect(Collectors.toSet());
        return subClasses;
    }

    public static <T> Set<Class<? extends T>> getSubClasses(String packageName, Class<T> clazz) {
        return new Reflections(packageName).getSubTypesOf(clazz);
    }

    public static <T> List<T> getSubClassInstances(Class<T> clazz) {
        return getSubClassInstances(GameConstants.PACKAGE, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getSubClassInstances(String packageName, Class<T> clazz) {
        return new Reflections(packageName).getSubTypesOf(clazz).stream().map(it -> {
            try {
                return (T) it.getConstructors()[0].newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public static <T> List<T> newInstances(Set<Class<? extends T>> classes) {
        return classes.stream().map(it -> {
            try {
                return (T) it.getConstructors()[0].newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }
}
