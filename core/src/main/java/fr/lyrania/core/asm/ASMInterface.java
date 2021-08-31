package fr.lyrania.core.asm;

import fr.lyrania.common.asm.ReflectionUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.util.UUID;

public class ASMInterface {

    private static final Class<?> BUNGEECORD_CLASS;
    private static final ProxyServer BUNGEECORD;

    private static final Plugin PLUGIN;
    private static final ClassLoader PLUGIN_CLASS_LOADER;

    private static final Class<?> NICK_SERVICE_CLASS;

    private static final Object NICK_SERVICE;

    private static final MethodHandle GET_NICK_NAME_METHOD;

    static {
        BUNGEECORD_CLASS = ReflectionUtils.getClass("net.md_5.bungee.BungeeCord", false, ASMInterface.class.getClassLoader());
        BUNGEECORD = (ProxyServer) ReflectionUtils.invokeMethod(BUNGEECORD_CLASS, "getInstance", null);

        PLUGIN = BUNGEECORD.getPluginManager().getPlugin("LyraCore");
        PLUGIN_CLASS_LOADER = PLUGIN.getClass().getClassLoader();

        NICK_SERVICE_CLASS = ReflectionUtils.getClass("fr.lyrania.core.services.nick.NickService", false, PLUGIN_CLASS_LOADER);

        NICK_SERVICE = ReflectionUtils.getFieldValue(NICK_SERVICE_CLASS, "INSTANCE", null);

        GET_NICK_NAME_METHOD = ReflectionUtils.getPrivateMethod(NICK_SERVICE_CLASS, "getNickName", UUID.class);
    }

    public static String getNickName(UUID uuid) {
        return (String) ReflectionUtils.invokeMethod(GET_NICK_NAME_METHOD, NICK_SERVICE, uuid);
    }
}
