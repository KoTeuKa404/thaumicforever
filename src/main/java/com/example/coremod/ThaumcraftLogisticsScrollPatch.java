package com.example.coremod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lwjgl.input.Mouse;

public final class ThaumcraftLogisticsScrollPatch {

    private ThaumcraftLogisticsScrollPatch() {}

    public static void handleLogisticsWheel(Object guiObj) {
        try {
            int wheel = Mouse.getDWheel();
            if (wheel == 0 || guiObj == null) {
                return;
            }

            Object container = getField(guiObj, "field_147002_h");
            if (container == null || !"thaumcraft.common.container.ContainerLogistics".equals(container.getClass().getName())) {
                return;
            }

            int start = getIntField(container, "start", 0);
            int end = Math.max(0, getIntField(container, "end", 0));
            int steps = Math.min(8, Math.max(1, Math.abs(wheel) / 120));
            int dir = wheel < 0 ? 1 : -1;

            int targetStart = clamp(start + dir * steps, 0, end);
            if (targetStart == start) {
                return;
            }

            Object mc = getField(guiObj, "field_146297_k");
            if (mc == null) {
                return;
            }
            Object controller = getField(mc, "field_71442_b");
            if (controller == null) {
                return;
            }

            int windowId = getIntField(container, "field_75152_c", 0);
            int buttonId = 100 + targetStart;

            Method m = controller.getClass().getMethod("func_78756_a", int.class, int.class);
            m.setAccessible(true);
            m.invoke(controller, windowId, buttonId);
        } catch (Throwable ignored) {
        }
    }

    private static Object getField(Object obj, String name) {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(obj);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            } catch (Throwable t) {
                return null;
            }
        }
        return null;
    }

    private static int getIntField(Object obj, String name, int def) {
        Object val = getField(obj, name);
        return val instanceof Integer ? (Integer) val : def;
    }

    private static int clamp(int v, int min, int max) {
        return v < min ? min : (v > max ? max : v);
    }
}

