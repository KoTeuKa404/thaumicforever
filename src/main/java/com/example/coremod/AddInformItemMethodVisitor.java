package com.example.coremod;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddInformItemMethodVisitor extends MethodVisitor {

    public AddInformItemMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }
    
    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
    
}
