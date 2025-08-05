package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;

public class BaublesLogSuppressTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("baubles.common.event.EventHandlerEntity".equals(transformedName)) {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new SuppressBaublesLogVisitor(Opcodes.ASM5, cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        return basicClass;
    }
}

class SuppressBaublesLogVisitor extends ClassVisitor {
    public SuppressBaublesLogVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodVisitor(api, mv) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String methodName, String methodDesc, boolean itf) {
                if (owner.equals("org/slf4j/Logger") && methodName.equals("error")) {
                    return;
                }
                super.visitMethodInsn(opcode, owner, methodName, methodDesc, itf);
            }
        };
    }
}
