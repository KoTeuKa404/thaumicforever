package com.example.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class IngredientInvalidateAllTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "net.minecraft.item.crafting.Ingredient";
    private static final String TARGET_DESC = "()V";
    private static final String SET_DESC = "Ljava/util/Set;";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !isTargetClass(name, transformedName)) {
            return basicClass;
        }

        try {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);

            FieldNode instancesField = findStaticSetField(cn);
            if (instancesField == null) {
                return basicClass;
            }

            boolean changed = false;
            for (Object rawMethod : cn.methods) {
                MethodNode mn = (MethodNode) rawMethod;
                if (isInvalidateAll(mn)) {
                    replaceInvalidateAll(cn.name, instancesField.name, mn);
                    changed = true;
                }
            }

            if (!changed) {
                return basicClass;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            System.out.println("[ThaumicForever-Coremod] Patched Ingredient.invalidateAll snapshot iteration.");
            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return basicClass;
        }
    }

    private static FieldNode findStaticSetField(ClassNode cn) {
        for (Object rawField : cn.fields) {
            FieldNode field = (FieldNode) rawField;
            if ((field.access & Opcodes.ACC_STATIC) != 0 && SET_DESC.equals(field.desc)) {
                return field;
            }
        }
        return null;
    }

    private static boolean isInvalidateAll(MethodNode mn) {
        return (mn.access & Opcodes.ACC_STATIC) != 0
                && TARGET_DESC.equals(mn.desc)
                && ("invalidateAll".equals(mn.name) || "func_193368_a".equals(mn.name));
    }

    private static void replaceInvalidateAll(String owner, String instancesField, MethodNode mn) {
        InsnList instructions = new InsnList();
        instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, owner, instancesField, SET_DESC));
        instructions.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "com/example/coremod/IngredientInvalidateAllPatch",
                "invalidateAllSnapshot",
                "(Ljava/util/Set;)V",
                false));
        instructions.add(new InsnNode(Opcodes.RETURN));

        mn.instructions.clear();
        mn.tryCatchBlocks.clear();
        mn.localVariables.clear();
        mn.instructions.add(instructions);
    }

    private static boolean isTargetClass(String name, String transformedName) {
        return TARGET_CLASS.equals(transformedName) || TARGET_CLASS.equals(name);
    }
}
