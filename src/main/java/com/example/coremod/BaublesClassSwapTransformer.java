package com.example.coremod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;

public class BaublesClassSwapTransformer implements IClassTransformer {

    private static final Set<String> TARGETS = new HashSet<String>();
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
        String path = transformedName.replace('.', '/') + ".class"; 
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
}
