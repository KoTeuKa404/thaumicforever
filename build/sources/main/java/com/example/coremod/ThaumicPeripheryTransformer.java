package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;

public class ThaumicPeripheryTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("thaumicperiphery.items.ItemVisPhylactery".equals(transformedName)) {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new Change50To1000ClassVisitor(Opcodes.ASM5, cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        return basicClass;
    }
}

class Change50To1000ClassVisitor extends ClassVisitor {
    public Change50To1000ClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("onWornTick".equals(name) && desc.equals("(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V")) {
            return new ReplaceConstantMethodVisitor(Opcodes.ASM5, mv);
        }
        return mv;
    }
}

class ReplaceConstantMethodVisitor extends MethodVisitor {
    public ReplaceConstantMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if ((opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) && operand == 50) {
            super.visitLdcInsn(1000);
        } else {
            super.visitIntInsn(opcode, operand);
        }
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (cst instanceof Integer && ((Integer) cst) == 50) {
            super.visitLdcInsn(1000);
        } else {
            super.visitLdcInsn(cst);
        }
    }
}
