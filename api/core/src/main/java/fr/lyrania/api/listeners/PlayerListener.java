package fr.lyrania.api.listeners;

import fr.lyrania.api.services.PermissionsService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PermissionsService.INSTANCE.updatePlayer(player.getUniqueId());
    }
}
