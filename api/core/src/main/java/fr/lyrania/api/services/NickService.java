package fr.lyrania.api.services;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import fr.lyrania.api.events.UpdateNickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class NickService implements Listener {

    public static final NickService INSTANCE = new NickService();

    private NickService() {

    }

    /**
     * Update {@param player}'s display name, player list name and info.
     *
     * @param player The player
     */
    public void updatePlayer(Player player) {
        Bukkit.getPluginManager().callEvent(new UpdateNickEvent.Pre(player, player.getName()));

        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        this.updatePlayerInfo(player);

        Bukkit.getPluginManager().callEvent(new UpdateNickEvent.Post(player, player.getName()));
    }

    /**
     * Force the server to resend the player info data
     *
     * @param player The target
     */
    private void updatePlayerInfo(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other != player) {
                other.hidePlayer(player);
            }
        }

        this.removePlayerInfo(player);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other != player) {
                other.showPlayer(player);
            }
        }
    }

    /**
     * Send a packet to all online players except {@param target}
     * So that the player info data can be replaced
     *
     * @param target The target
     */
    private void removePlayerInfo(Player target) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getModifier().writeDefaults();
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        packet.getPlayerInfoDataLists().write(0, new ArrayList<PlayerInfoData>() {{
            this.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(target), 0, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(target.getDisplayName())));
        }});
        Bukkit.getOnlinePlayers().forEach(other -> {
            if (other != target) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(other, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.updatePlayer(event.getPlayer());
    }
}
