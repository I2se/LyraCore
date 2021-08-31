package fr.lyrania.core.asm;

import fr.lyrania.core.asm.transformers.UserConnectionTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent {

    public static void agentmain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new UserConnectionTransformer(), true);

        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            if (clazz != null && clazz.getPackage() != null && clazz.getSuperclass() != null) {
                if(!clazz.isPrimitive() && clazz.getPackage().getName().startsWith("net.md_5.bungee")){
                    if (clazz.getPackage().getName().startsWith("net.md_5.bungee")) {
                        try {
                            instrumentation.retransformClasses(clazz);
                        } catch (UnmodifiableClassException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
