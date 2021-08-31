package fr.lyrania.core.listeners;

import fr.lyrania.common.data.global.GlobalUUIDData;
import fr.lyrania.common.data.player.PlayerNameData;
import fr.lyrania.core.database.DataService;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        DataService.INSTANCE.writeGlobalData(GlobalUUIDData.class, uuidData -> {
            uuidData.getNames().put(player.getName(), player.getUniqueId());
        });

        DataService.INSTANCE.writeData(PlayerNameData.class, player.getUniqueId().toString(), data -> {
            data.setPseudo(player.getName());
        });
    }
}
