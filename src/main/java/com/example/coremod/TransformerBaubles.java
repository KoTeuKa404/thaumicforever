// package com.example.coremod;

// import org.objectweb.asm.ClassReader;
// import org.objectweb.asm.ClassVisitor;
// import org.objectweb.asm.ClassWriter;
// import org.objectweb.asm.FieldVisitor;
// import org.objectweb.asm.Label;
// import org.objectweb.asm.MethodVisitor;
// import org.objectweb.asm.Opcodes;

// import net.minecraft.launchwrapper.IClassTransformer;

// public class TransformerBaubles implements IClassTransformer, Opcodes {
//     @Override
//     public byte[] transform(String name, String transformedName, byte[] basicClass) {
//         if (transformedName.equals("baubles.api.cap.BaublesContainer")) {
//             ClassReader classReader = new ClassReader(basicClass);
//             ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//             classReader.accept(new CVBaublesContainer(classWriter), 0);
//             return classWriter.toByteArray();
//         }
//         if (transformedName.equals("baubles.common.container.ContainerPlayerExpanded"))  {
//             ClassReader classReader = new ClassReader(basicClass);
//             ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//             classReader.accept(new CVContainerPlayerExpanded(classWriter), 0);
//             return classWriter.toByteArray();
//         }
//         return basicClass;
//     }

//     public static class CVContainerPlayerExpanded extends ClassVisitor {

//         public CVContainerPlayerExpanded(ClassVisitor cv) {
//             super(ASM5, cv);
//         }

//         @Override
//         public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//             MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//             if (name.equals("transferStackInSlot") && desc.equals("(Lnet/minecraft/entity/player/EntityPlayer;I)Lnet/minecraft/item/ItemStack;")) {
//                 return new MethodVisitor(ASM5, mv) {
//                     private int i = 0;
//                     @Override
//                     public void visitVarInsn(int opcode, int var) {
//                         super.visitVarInsn(opcode, var);

//                         if (i == 0 && opcode == ASTORE && var == 6) {
//                             i++;
//                         }

//                         if (i == 1 && opcode == ALOAD && var == 0) {
//                             i++;
//                         }
//                     }

//                     @Override
//                     public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//                         super.visitFieldInsn(opcode, owner, name, desc);

//                         if (i == 2 && opcode == GETFIELD && owner.equals("baubles/common/container/ContainerPlayerExpanded") && name.equals("baubles")) {
//                             i++;
//                         }
//                     }

//                     @Override
//                     public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//                         super.visitMethodInsn(opcode, owner, name, desc, itf);

//                         if (owner.equals("baubles/api/cap/IBaublesItemHandler") && name.equals("getSlots") && desc.equals("()I") && i == 3) {
//                             this.visitIntInsn(BIPUSH, 7);
//                             this.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "min", "(II)I", false);
//                             this.i = -1;
//                         }
//                     }
//                 };
//             }

//             return mv;
//         }
//     }

//     public static class CVBaublesContainer extends ClassVisitor {

//         public CVBaublesContainer(ClassVisitor cv) {
//             super(ASM5, cv);
//         }

//         @Override
//         public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//             if (name.equals("BAUBLE_SLOTS") && desc.equals("I")) {
//                 return super.visitField(access, name, desc, signature, 32);
//             }
//             return super.visitField(access, name, desc, signature, value);
//         }

//         @Override
//         public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//             MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//             if (name.equals("<init>") && desc.equals("()V")) {
//                 return new MethodVisitor(ASM5, mv) {
//                     @Override
//                     public void visitIntInsn(int opcode, int operand) {
//                         if (opcode == BIPUSH && operand == 7) {
//                             operand = 32;
//                         }
//                         super.visitIntInsn(opcode, operand);
//                     }
//                 };
//             }
//             if (name.equals("isItemValidForSlot") && desc.equals("(ILnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)Z")) {
//                 return new MethodVisitor(ASM5, mv) {
//                     @Override
//                     public void visitCode() {
//                         super.visitCode();

//                         this.visitVarInsn(ILOAD, 1);
//                         this.visitIntInsn(BIPUSH, 7);

//                         Label label0 = new Label();
//                         this.visitJumpInsn(IF_ICMPLE, label0);
//                         this.visitInsn(ICONST_1);
//                         this.visitInsn(IRETURN);
//                         this.visitLabel(label0);
//                     }
//                 };
//             }
//             if (name.equals("setSize") && desc.equals("(I)V")) {
//                 return new MethodVisitor(ASM5, mv) {

//                     @Override
//                     public void visitIntInsn(int opcode, int operand) {
//                         if (opcode == BIPUSH && operand == 7) {
//                             operand = 32;
//                         }

//                         super.visitIntInsn(opcode, operand);
//                     }
//                 };
//             }
//             return mv;
//         }
//     }
// }
