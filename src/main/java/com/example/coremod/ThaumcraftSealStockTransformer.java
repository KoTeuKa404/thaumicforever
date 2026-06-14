package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ThaumcraftSealStockTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "thaumcraft.common.golems.seals.SealStock";
    private static final String TARGET_METHOD = "tickSeal";
    private static final String TARGET_DESC = "(Lnet/minecraft/world/World;Lthaumcraft/api/golems/seals/ISealEntity;)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !isTargetClass(transformedName, name)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);

            boolean patched = false;
            for (MethodNode mn : cn.methods) {
                if (TARGET_METHOD.equals(mn.name) && TARGET_DESC.equals(mn.desc)) {
                    patched |= patchCountMethod(mn);
                }
            }

            if (!patched) {
                return basicClass;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            System.out.println("[ThaumicForever-Coremod] Patched SealStock in-transit item counting.");
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static boolean patchCountMethod(MethodNode mn) {
        boolean patched = false;
        for (AbstractInsnNode insn = mn.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (!(insn instanceof MethodInsnNode)) {
                continue;
            }

            MethodInsnNode mi = (MethodInsnNode) insn;
            if (!"thaumcraft/api/ThaumcraftInvHelper".equals(mi.owner)
                    || !"countTotalItemsIn".equals(mi.name)
                    || !"(Lnet/minecraftforge/items/IItemHandler;Lnet/minecraft/item/ItemStack;Lthaumcraft/api/ThaumcraftInvHelper$InvFilter;)I".equals(mi.desc)) {
                continue;
            }

            InsnList extraArgs = new InsnList();
            extraArgs.add(new VarInsnNode(Opcodes.ALOAD, 1));
            extraArgs.add(new VarInsnNode(Opcodes.ALOAD, 2));
            mn.instructions.insertBefore(mi, extraArgs);

            mi.owner = "com/example/coremod/ThaumcraftSealStockPatch";
            mi.name = "countStockedAndRequested";
            mi.desc = "(Lnet/minecraftforge/items/IItemHandler;Lnet/minecraft/item/ItemStack;Lthaumcraft/api/ThaumcraftInvHelper$InvFilter;Lnet/minecraft/world/World;Lthaumcraft/api/golems/seals/ISealEntity;)I";
            patched = true;
        }
        return patched;
    }

    private static boolean isTargetClass(String transformedName, String name) {
        String tn = transformedName == null ? "" : transformedName;
        String n = name == null ? "" : name;
        return TARGET_CLASS.equals(tn) || TARGET_CLASS.equals(n) || tn.endsWith(".SealStock") || n.endsWith(".SealStock");
    }
}
