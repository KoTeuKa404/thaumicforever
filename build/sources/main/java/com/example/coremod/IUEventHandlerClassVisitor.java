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
        if ("onBlockHarvested".equals(name) && desc.equals("(Lnet/minecraftforge/event/world/BlockEvent$HarvestDropsEvent;)V")) {
            return new EmptyOnBlockHarvestedMethodVisitor(null);
        }
        if ("addInformItem".equals(name) && desc.equals("(Lnet/minecraftforge/event/entity/player/ItemTooltipEvent;)V")) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new AddInformItemMethodVisitor(mv);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
