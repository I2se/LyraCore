package fr.lyrania.core.services.nick;

import fr.lyrania.common.asm.ReflectionUtils;
import fr.lyrania.core.protocol.Channels;
import fr.lyrania.core.protocol.ProtocolService;
import fr.lyrania.core.protocol.channels.NickChannel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class NickService {

    public static final NickService INSTANCE = new NickService();
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    private static final Class<?> BUNGEECORD_CLASS;
    private static final ProxyServer BUNGEECORD;

    private static final Class<?> USER_CONNECTION_CLASS;

    private static final MethodHandle ADD_CONNECTION;

    static {
        BUNGEECORD_CLASS = ReflectionUtils.getClass("net.md_5.bungee.BungeeCord", false, NickService.class.getClassLoader());
        BUNGEECORD = (ProxyServer) ReflectionUtils.invokeMethod(BUNGEECORD_CLASS, "getInstance", null);

        USER_CONNECTION_CLASS = ReflectionUtils.getClass("net.md_5.bungee.UserConnection", false, NickService.class.getClassLoader());

        ADD_CONNECTION = ReflectionUtils.getPrivateMethod(BUNGEECORD_CLASS, "addConnection", USER_CONNECTION_CLASS);
    }

    /**
     * The instance of the {@link NickInfoProvider}
     *
     * See {@link NickInfoProvider#createNickInfo(String, Consumer)}
     */
    private final NickInfoProvider nickInfoProvider;

    /**
     * The map containing all player and their {@link NickInfo}.
     *
     * Key : The {@link UUID} of the player in /nick
     * Value : The {@link NickInfo} of the player in /nick
     */
    private final Map<UUID, NickInfo> nickInfos;

    private NickService() {
        this.nickInfoProvider = new NickInfoProvider();
        this.nickInfos = new HashMap<>();
    }

    public void load() {
        this.nickInfoProvider.load();
    }


    /**
     * Add/Remove a nickname for a player with uuid {@param id}
     * If the {@param nickInfo} is null, the nickname of the player will be removed
     * Otherwise, it will be added.
     *
     * Note : This function need to be called before modifying anything related to the player name.
     * It ensures that {@link ProxiedPlayer#getName()} returns the real player name
     * When we remove the nickname from the player.
     *
     * In game, the content of the method {@link ProxiedPlayer#getName()} has been replaced with a new code
     * In which at each call, we are checking {@link NickService#nickInfos}
     *
     * These principles are also applied in the LyraAPI's implementation.
     *
     * @param uuid The uuid of the player with a new nickname
     * @param nickInfo The new nick infos applied to the player
     */
    public void setNickName(UUID uuid, NickInfo nickInfo) {
        if (nickInfo == null) {
            this.nickInfos.remove(uuid);
        } else {
            this.nickInfos.put(uuid, nickInfo);
        }

        this.updatePlayer(uuid);
    }

    /**
     * Broadcast an update message and update internally BungeeCord
     *
     * @param uuid The uuid of the player
     */
    public void updatePlayer(UUID uuid) {
        ReflectionUtils.invokeMethod(ADD_CONNECTION, BUNGEECORD.getPlayer(uuid));
        this.broadcastUpdateMessageFor(uuid);
    }

    /**
     * Broadcast an update message to all server
     *
     * @param uuid The uuid of the player
     */
    public void broadcastUpdateMessageFor(UUID uuid) {
        ProtocolService.INSTANCE.broadcastMessage(Channels.NICK, out -> {
            out.writeByte(NickChannel.Actions.UPDATE.getId());
            out.writeUUID(uuid);
        });
    }

    /**
     * Return the nickname of the {@param player}.
     * If he doesn't have a nickname, the returned value is null.
     *
     * @param player The uuid of the player
     * @return The nickname of the player
     */
    public String getNickName(UUID player) {
        return this.nickInfos.containsKey(player) ? this.nickInfos.get(player).getNickName() : null;
    }

    /**
     * Return the {@link NickInfoProvider} of the current {@link NickService}.
     * @return The {@link NickInfoProvider} instance
     */
    public NickInfoProvider getNickInfoProvider() {
        return nickInfoProvider;
    }
}
