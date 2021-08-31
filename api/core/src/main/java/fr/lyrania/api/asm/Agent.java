package fr.lyrania.api.asm;

import fr.lyrania.api.asm.transformers.CraftHumanTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent {

    public static void agentmain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new CraftHumanTransformer(), true);

        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            if (clazz != null && clazz.getPackage() != null && clazz.getSuperclass() != null) {
                if(!clazz.isPrimitive() && clazz.getPackage().getName().startsWith("org.bukkit")){
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
