package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ThaumcraftLogisticsQueueTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "thaumcraft.client.gui.GuiLogistics";
    private static final String DRAW_SCREEN = "func_73863_a";
    private static final String DRAW_SCREEN_DESC = "(IIF)V";
    private static final String ACTION_PERFORMED = "func_146284_a";
    private static final String ACTION_PERFORMED_DESC = "(Lnet/minecraft/client/gui/GuiButton;)V";
    private static final String MOUSE_CLICKED = "func_73864_a";
    private static final String MOUSE_CLICKED_DESC = "(III)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);
            boolean patched = false;

            for (MethodNode mn : cn.methods) {
                if ((DRAW_SCREEN.equals(mn.name) || "drawScreen".equals(mn.name)) && DRAW_SCREEN_DESC.equals(mn.desc)) {
                    patched |= patchDrawScreen(mn);
                } else if ((ACTION_PERFORMED.equals(mn.name) || "actionPerformed".equals(mn.name)) && ACTION_PERFORMED_DESC.equals(mn.desc)) {
                    patched |= patchActionPerformed(mn);
                } else if ((MOUSE_CLICKED.equals(mn.name) || "mouseClicked".equals(mn.name)) && MOUSE_CLICKED_DESC.equals(mn.desc)) {
                    patched |= patchMouseClicked(mn);
                }
            }

            if (!patched) {
                return basicClass;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            System.out.println("[ThaumicForever-Coremod] Patched logistics queue GUI.");
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static boolean patchActionPerformed(MethodNode mn) {
        InsnList hook = new InsnList();
        hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hook.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "com/example/coremod/ThaumcraftLogisticsQueuePatch",
            "onAction",
            "(Ljava/lang/Object;Lnet/minecraft/client/gui/GuiButton;)V",
            false
        ));
        mn.instructions.insert(hook);
        return true;
    }

    private static boolean patchMouseClicked(MethodNode mn) {
        LabelNode pass = new LabelNode();
        InsnList hook = new InsnList();
        hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
        hook.add(new VarInsnNode(Opcodes.ILOAD, 1));
        hook.add(new VarInsnNode(Opcodes.ILOAD, 2));
        hook.add(new VarInsnNode(Opcodes.ILOAD, 3));
        hook.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "com/example/coremod/ThaumcraftLogisticsQueuePatch",
            "onMouseClicked",
            "(Ljava/lang/Object;III)Z",
            false
        ));
        hook.add(new JumpInsnNode(Opcodes.IFEQ, pass));
        hook.add(new InsnNode(Opcodes.RETURN));
        hook.add(pass);
        mn.instructions.insert(hook);
        return true;
    }

    private static boolean patchDrawScreen(MethodNode mn) {
        boolean patched = false;
        for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getOpcode() != Opcodes.RETURN) {
                continue;
            }

            InsnList hook = new InsnList();
            hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
            hook.add(new VarInsnNode(Opcodes.ILOAD, 1));
            hook.add(new VarInsnNode(Opcodes.ILOAD, 2));
            hook.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "com/example/coremod/ThaumcraftLogisticsQueuePatch",
                "render",
                "(Ljava/lang/Object;II)V",
                false
            ));
            mn.instructions.insertBefore(insn, hook);
            patched = true;
        }
        return patched;
    }
}
