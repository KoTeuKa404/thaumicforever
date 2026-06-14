package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ThaumcraftLogisticsSearchLimitTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "thaumcraft.client.gui.GuiLogistics";
    private static final String INIT_GUI = "func_73866_w_";
    private static final String INIT_GUI_DEOBF = "initGui";
    private static final String GUI_TEXT_FIELD = "net/minecraft/client/gui/GuiTextField";
    private static final String SET_MAX_LENGTH = "func_146203_f";
    private static final String SET_MAX_LENGTH_DEOBF = "setMaxStringLength";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);
            boolean changed = false;

            for (Object rawMethod : cn.methods) {
                MethodNode mn = (MethodNode) rawMethod;
                if (!isInitGui(mn)) continue;

                for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                    if (!(insn instanceof MethodInsnNode)) continue;
                    MethodInsnNode method = (MethodInsnNode) insn;
                    if (!GUI_TEXT_FIELD.equals(method.owner) || !"(I)V".equals(method.desc) || !isSetMaxLength(method.name)) {
                        continue;
                    }

                    AbstractInsnNode previous = method.getPrevious();
                    if (previous instanceof IntInsnNode) {
                        IntInsnNode value = (IntInsnNode) previous;
                        if (value.operand == 10) {
                            value.operand = 100;
                            changed = true;
                        }
                    }
                }
            }

            if (!changed) {
                return basicClass;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static boolean isInitGui(MethodNode mn) {
        return (INIT_GUI.equals(mn.name) || INIT_GUI_DEOBF.equals(mn.name)) && "()V".equals(mn.desc);
    }

    private static boolean isSetMaxLength(String name) {
        return SET_MAX_LENGTH.equals(name) || SET_MAX_LENGTH_DEOBF.equals(name);
    }
}
