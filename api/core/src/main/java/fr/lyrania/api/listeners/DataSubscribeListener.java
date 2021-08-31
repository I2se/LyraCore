package fr.lyrania.api.listeners;

import fr.lyrania.api.data.PlayerPermissionsData;
import fr.lyrania.api.events.DataSubscribeEvent;
import fr.lyrania.common.data.global.GlobalRanksConfData;
import fr.lyrania.common.data.global.GlobalUUIDData;
import fr.lyrania.common.data.player.PlayerPartyData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DataSubscribeListener implements Listener {

    @EventHandler
    public void onSubscribe(DataSubscribeEvent event) {
        switch (event.getRegistry()) {
            case GLOBAL:
                event.subscribe("uuids", GlobalUUIDData.class);
                event.subscribe("ranks_config", GlobalRanksConfData.class);
                break;
            case PLAYER:
                event.subscribe("party", PlayerPartyData.class);
                event.subscribe("permissions", PlayerPermissionsData.class);
                break;
        }
    }
}
