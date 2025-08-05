// package com.example.coremod;

// import org.objectweb.asm.ClassVisitor;
// import org.objectweb.asm.Label;
// import org.objectweb.asm.MethodVisitor;
// import org.objectweb.asm.Opcodes;

// public class BaubleAttributeModifierHandlerPatcher extends ClassVisitor {
//     public BaubleAttributeModifierHandlerPatcher(ClassVisitor cv) {
//         super(Opcodes.ASM5, cv);
//     }

//     @Override
//     public MethodVisitor visitMethod(
//             int access, String name, String desc, String signature, String[] exceptions) {
//         MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

//         if (name.equals("removeAllModifiers") && desc.contains("EntityPlayer")) {
//             return new MethodVisitor(Opcodes.ASM5, mv) {
//                 private Label start = new Label();
//                 private Label end = new Label();
//                 private Label handler = new Label();

//                 @Override
//                 public void visitCode() {
//                     super.visitTryCatchBlock(start, end, handler, "java/lang/IndexOutOfBoundsException");
//                     super.visitLabel(start);
//                     super.visitCode();
//                 }

//                 @Override
//                 public void visitInsn(int opcode) {
//                     if (opcode == Opcodes.RETURN) {
//                         super.visitLabel(end);
//                     }
//                     super.visitInsn(opcode);
//                 }

//                 @Override
//                 public void visitMaxs(int maxStack, int maxLocals) {
//                     super.visitLabel(handler);
//                     super.visitInsn(Opcodes.RETURN);
//                     super.visitMaxs(maxStack, maxLocals);
//                 }
//             };
//         }
//         return mv;
//     }
// }
