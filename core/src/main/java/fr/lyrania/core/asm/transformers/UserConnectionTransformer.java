package fr.lyrania.core.asm.transformers;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class UserConnectionTransformer implements ClassFileTransformer, Opcodes {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("net/md_5/bungee/UserConnection")) {
            System.out.println("> Injecting " + className + "...");

            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
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

            Label elseLabel = new Label();
            Label endLabel = new Label();

            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "net/md_5/bungee/UserConnection", "getUniqueId", "()Ljava/util/UUID;", false);
            mv.visitMethodInsn(INVOKESTATIC, "fr/lyrania/core/asm/ASMInterface", "getNickName", "(Ljava/util/UUID;)Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);

            mv.visitJumpInsn(IFNULL, elseLabel);

            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ARETURN);

            mv.visitLabel(elseLabel);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "net/md_5/bungee/UserConnection", "name", "Ljava/lang/String;");
            mv.visitInsn(ARETURN);

            mv.visitLabel(endLabel);
            mv.visitMaxs(1, 2);
            mv.visitEnd();

            this.cv.visitEnd();
        }
    }
}
