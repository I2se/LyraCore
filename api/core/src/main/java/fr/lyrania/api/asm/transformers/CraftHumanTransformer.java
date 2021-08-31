package fr.lyrania.api.asm.transformers;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class CraftHumanTransformer implements ClassFileTransformer, Opcodes {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.startsWith("org/bukkit/craftbukkit") && className.endsWith("entity/CraftHumanEntity")) {
            System.out.println("> Injecting " + className + "...");

            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classReader.accept(new CraftHumanEntityClassAdapter(classWriter), 0);

            return classWriter.toByteArray();
        } else {
            return classfileBuffer;
        }
    }

    public static class CraftHumanEntityClassAdapter extends ClassVisitor {

        public CraftHumanEntityClassAdapter(ClassVisitor classVisitor) {
            super(ASM4, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("getName") && desc.equals("()Ljava/lang/String;")) {
                System.out.println("    > Injecting getName()Ljava/lang/String;");
                return null;
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            MethodVisitor mv = this.cv.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);

            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(18, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/bukkit/craftbukkit/v1_8_R3/entity/CraftHumanEntity", "getHandle", "()Lnet/minecraft/server/v1_8_R3/EntityHuman;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/server/v1_8_R3/EntityHuman", "getUniqueID", "()Ljava/util/UUID;", false);
            mv.visitMethodInsn(INVOKESTATIC, "fr/lyrania/api/asm/ASMInterface", "getNickName", "(Ljava/util/UUID;)Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(19, l1);
            mv.visitVarInsn(ALOAD, 1);
            Label l2 = new Label();
            mv.visitJumpInsn(IFNULL, l2);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(20, l3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitLineNumber(22, l2);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/bukkit/craftbukkit/v1_8_R3/entity/CraftHumanEntity", "getHandle", "()Lnet/minecraft/server/v1_8_R3/EntityHuman;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/server/v1_8_R3/EntityHuman", "getName", "()Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLocalVariable("this", "Lorg/bukkit/craftbukkit/v1_8_R3/entity/CraftHumanEntity;", null, l0, l4, 0);
            mv.visitLocalVariable("nickname", "Ljava/lang/String;", null, l1, l4, 1);
            mv.visitMaxs(1, 2);
            mv.visitEnd();

            this.cv.visitEnd();
        }
    }
}
