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

import net.minecraft.launchwrapper.IClassTransformer;

public class GolemRenderScaleTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "thaumcraft.common.golems.client.RenderThaumcraftGolem";
    private static final String TARGET_DESC = "(Lthaumcraft/common/golems/EntityThaumcraftGolem;FFFFFFF)V";

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
                if ("renderModel".equals(mn.name) && TARGET_DESC.equals(mn.desc)) {
                    InsnList hook = new InsnList();
                    hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    hook.add(new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "com/example/coremod/GolemRenderScalePatch",
                            "applyScale",
                            "(Lthaumcraft/common/golems/EntityThaumcraftGolem;)V",
                            false));
                    mn.instructions.insert(hook);
                    insertPlugRenderHook(mn);
                    changed = true;
                    break;
                }
            }

            if (!changed) {
                return basicClass;
            }

            System.out.println("[ThaumicForever-Coremod] Hooked golem render scale.");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static void insertPlugRenderHook(MethodNode mn) {
        for (int i = 0; i < mn.instructions.size(); i++) {
            if (!(mn.instructions.get(i) instanceof MethodInsnNode)) continue;

            MethodInsnNode call = (MethodInsnNode) mn.instructions.get(i);
            if ("renderParts".equals(call.name)
                    && "(Lthaumcraft/common/golems/EntityThaumcraftGolem;FFFFFFF)V".equals(call.desc)) {
                LabelNode skipOverlay = new LabelNode();
                InsnList hook = new InsnList();

                hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                hook.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/coremod/GolemRenderScalePatch",
                        "beginIronSkinOverlay",
                        "(Lthaumcraft/common/golems/EntityThaumcraftGolem;)Z",
                        false));
                hook.add(new JumpInsnNode(Opcodes.IFEQ, skipOverlay));
                hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 2));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 3));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 4));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 5));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 6));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 7));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 8));
                hook.add(new MethodInsnNode(
                        call.getOpcode(),
                        call.owner,
                        call.name,
                        call.desc,
                        call.itf));
                hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                hook.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/coremod/GolemRenderScalePatch",
                        "renderIronSkinPlugOverlay",
                        "(Lthaumcraft/common/golems/EntityThaumcraftGolem;)V",
                        false));
                hook.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/coremod/GolemRenderScalePatch",
                        "endIronSkinOverlay",
                        "()V",
                        false));
                hook.add(skipOverlay);

                hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                hook.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/coremod/GolemRenderScalePatch",
                        "renderCorePlug",
                        "(Lthaumcraft/common/golems/EntityThaumcraftGolem;)V",
                        false));
                mn.instructions.insert(call, hook);
                return;
            }
        }
    }
}
