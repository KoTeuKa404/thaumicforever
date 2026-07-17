package com.example.coremod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ConcurrentModificationException;
import java.util.Set;

import net.minecraft.item.crafting.Ingredient;

public final class IngredientInvalidateAllPatch {
    private static volatile Method invalidateMethod;

    private IngredientInvalidateAllPatch() {}

    public static void invalidateAllSnapshot(Set<?> instances) {
        if (instances == null || instances.isEmpty()) {
            return;
        }

        Object[] snapshot = snapshot(instances);
        if (snapshot == null) {
            return;
        }

        Method method = getInvalidateMethod();
        if (method == null) {
            return;
        }

        for (Object ingredient : snapshot) {
            if (!(ingredient instanceof Ingredient)) {
                continue;
            }

            try {
                method.invoke(ingredient);
            } catch (Throwable ignored) {
                // A broken ingredient should not abort global recipe remapping.
            }
        }
    }

    private static Object[] snapshot(Set<?> instances) {
        for (int attempt = 0; attempt < 8; attempt++) {
            try {
                return instances.toArray();
            } catch (ConcurrentModificationException ignored) {
                Thread.yield();
            }
        }

        try {
            synchronized (instances) {
                return instances.toArray();
            }
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Method getInvalidateMethod() {
        Method method = invalidateMethod;
        if (method != null) {
            return method;
        }

        method = findInvalidateMethod();
        invalidateMethod = method;
        return method;
    }

    private static Method findInvalidateMethod() {
        for (Method method : Ingredient.class.getDeclaredMethods()) {
            if (method.getParameterTypes().length == 0
                    && method.getReturnType() == Void.TYPE
                    && !Modifier.isStatic(method.getModifiers())
                    && ("invalidate".equals(method.getName()) || method.getName().startsWith("func_"))) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }
}
