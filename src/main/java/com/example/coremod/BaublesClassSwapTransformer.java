package com.example.coremod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;

public class BaublesClassSwapTransformer implements IClassTransformer {

    private static final Set<String> TARGETS = new HashSet<String>();
    private static final String DISABLED_REASON = detectDisabledReason();
    private static final String REPLACEMENTS_ROOT = "baubles/";
    static {
        TARGETS.add("baubles.api.cap.BaublesContainer");
        // TARGETS.add("baubles.api.inv.BaublesInventoryWrapper");
        TARGETS.add("baubles.api.BaubleType");
        TARGETS.add("baubles.common.container.ContainerPlayerExpanded");
        TARGETS.add("baubles.common.container.ContainerPlayerExpanded$1");
        TARGETS.add("baubles.common.container.ContainerPlayerExpanded$2");
        TARGETS.add("baubles.common.container.SlotBauble");
        TARGETS.add("baubles.common.event.EventHandlerEntity");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName == null || !TARGETS.contains(transformedName)) return basicClass;
        if (DISABLED_REASON != null) {
            return basicClass;
        }
        String path = REPLACEMENTS_ROOT + transformedName.replace('.', '/') + ".class";
        InputStream is = null;
        try {
            ClassLoader cl = BaublesClassSwapTransformer.class.getClassLoader();
            is = cl.getResourceAsStream(path);
            if (is != null) {
                byte[] bytes = readFully(is);
                System.out.println("[BaublesClassSwap] Replaced " + transformedName + " from " + path);
                return bytes;
            } else {
                System.err.println("[BaublesClassSwap] Replacement not found: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) try { is.close(); } catch (IOException ignored) {}
        }
        return basicClass;
    }

    private static byte[] readFully(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(Math.max(4096, is.available()));
        byte[] chunk = new byte[4096];
        int n;
        while ((n = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, n);
        }
        return buffer.toByteArray();
    }

    private static String detectDisabledReason() {
        // BaublesEX fork marker
        if (hasClassResource("baubles/api/BaubleTypeEx.class")) {
            System.out.println("[BaublesClassSwap] BaublesEX detected - class swap disabled.");
            return "BaublesEX";
        }

        // Bubbles fork markers (modid is also 'baubles').
        // Use stricter detection to avoid false positives on classic Baubles.
        if (hasClassResource("baubles/api/BaubleTypeImpl.class")) {
            System.out.println("[BaublesClassSwap] Bubbles detected (BaubleTypeImpl) - class swap disabled.");
            return "Bubbles";
        }
        // IMPORTANT: do not touch FML Loader here.
        // Loading net.minecraftforge.fml.common.Loader during coremod bootstrap can
        // break early mixin targets (seen as "Loader was loaded too early").
        System.out.println("[BaublesClassSwap] Classic Baubles-like API detected - class swap enabled.");
        return null;
    }

    private static boolean hasClassResource(String classResourcePath) {
        ClassLoader cl = BaublesClassSwapTransformer.class.getClassLoader();
        return cl != null && cl.getResource(classResourcePath) != null;
    }
}
