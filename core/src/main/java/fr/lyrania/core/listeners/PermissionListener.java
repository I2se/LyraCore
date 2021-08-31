package fr.lyrania.core.listeners;

import fr.lyrania.core.data.player.PlayerPermissionsData;
import fr.lyrania.core.database.DataService;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionListener implements Listener {

    @EventHandler
    public void onCheckPerm(PermissionCheckEvent event) {
        CommandSender sender = event.getSender();
        String permission = event.getPermission();

        if (sender instanceof ProxiedPlayer) {
            PlayerPermissionsData data = DataService.INSTANCE.getData(PlayerPermissionsData.class, ((ProxiedPlayer) sender).getUniqueId().toString());
            event.setHasPermission(data.hasPermission(permission));
        } else {
            event.setHasPermission(true);
        }
    }
}
