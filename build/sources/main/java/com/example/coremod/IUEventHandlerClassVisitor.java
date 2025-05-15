package com.example.coremod;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IUEventHandlerClassVisitor extends ClassVisitor {

    public IUEventHandlerClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("addInformItem".equals(name) && desc.equals("(Lnet/minecraftforge/event/entity/player/ItemTooltipEvent;)V")) {
            return new AddInformItemMethodVisitor(mv);
        }
        return mv;
    }
}
    