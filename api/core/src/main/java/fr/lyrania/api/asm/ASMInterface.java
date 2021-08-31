package fr.lyrania.api.asm;

import fr.lyrania.common.asm.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.util.UUID;

public class ASMInterface {

    private static final Plugin PLUGIN;
    private static final ClassLoader PLUGIN_CLASS_LOADER;

    private static final Class<?> NICK_SERVICE_CLASS;

    private static final Object NICK_SERVICE;

    private static final MethodHandle GET_NICK_NAME_METHOD;

    static {
        PLUGIN = Bukkit.getPluginManager().getPlugin("LyraNick");
        PLUGIN_CLASS_LOADER = PLUGIN.getClass().getClassLoader();

        NICK_SERVICE_CLASS = ReflectionUtils.getClass("fr.lyrania.api.services.NickService", false, PLUGIN_CLASS_LOADER);

        NICK_SERVICE = ReflectionUtils.getFieldValue(NICK_SERVICE_CLASS, "INSTANCE", null);

        GET_NICK_NAME_METHOD = ReflectionUtils.getPrivateMethod(NICK_SERVICE_CLASS, "getNickName", UUID.class);
    }

    public static String getNickName(UUID uuid) {
        return (String) ReflectionUtils.invokeMethod(GET_NICK_NAME_METHOD, NICK_SERVICE, uuid);
    }
}
