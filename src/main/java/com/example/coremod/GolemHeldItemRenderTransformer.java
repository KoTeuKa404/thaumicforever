package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.InsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class GolemHeldItemRenderTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "thaumcraft.common.golems.client.RenderThaumcraftGolem";
    private static final String TARGET_DESC = "(Lthaumcraft/common/golems/EntityThaumcraftGolem;)V";

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
                if ("drawHeldItem".equals(mn.name) && TARGET_DESC.equals(mn.desc)) {
                    LabelNode vanillaPath = new LabelNode();
                    InsnList hook = new InsnList();
                    hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    hook.add(new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "com/example/coremod/GolemHeldItemRenderPatch",
                            "drawHeldCasterItem",
                            "(Lthaumcraft/common/golems/EntityThaumcraftGolem;)Z",
                            false));
                    hook.add(new JumpInsnNode(Opcodes.IFEQ, vanillaPath));
                    hook.add(new InsnNode(Opcodes.RETURN));
                    hook.add(vanillaPath);

                    mn.instructions.insert(hook);
                    changed = true;
                    break;
                }
            }

            if (!changed) {
                return basicClass;
            }

            System.out.println("[ThaumicForever-Coremod] Hooked golem held caster render.");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }
}
