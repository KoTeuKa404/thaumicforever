package com.example.coremod;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EmptyOnBlockHarvestedMethodVisitor extends MethodVisitor {
    public EmptyOnBlockHarvestedMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitInsn(Opcodes.RETURN); 
        super.visitMaxs(0, 0);
        super.visitEnd();
    }
}
