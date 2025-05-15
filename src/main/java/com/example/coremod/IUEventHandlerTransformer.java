package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import net.minecraft.launchwrapper.IClassTransformer;

public class IUEventHandlerTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("com.denfop.events.IUEventHandler".equals(transformedName)) {
            System.err.println("[Coremod] Transforming IUEventHandler: " + transformedName);
            ClassReader reader = new ClassReader(basicClass);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            IUEventHandlerClassVisitor visitor = new IUEventHandlerClassVisitor(writer);
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
        }
        return basicClass;
    }
}
