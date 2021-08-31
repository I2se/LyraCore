package fr.lyrania.core.listeners;

import fr.lyrania.common.data.global.GlobalPartyData;
import fr.lyrania.common.data.global.GlobalRanksConfData;
import fr.lyrania.common.data.global.GlobalUUIDData;
import fr.lyrania.common.data.player.*;
import fr.lyrania.core.data.player.PlayerPermissionsData;
import fr.lyrania.core.events.DataSubscribeEvent;
import fr.lyrania.core.services.servers.data.GlobalServerData;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DataSubscribeListener implements Listener {

    @EventHandler
    public void onSubscribe(DataSubscribeEvent event) {
        switch (event.getRegistry()) {
            case GLOBAL:
                event.subscribe("uuids", GlobalUUIDData.class);
                event.subscribe("party", GlobalPartyData.class);
                event.subscribe("server", GlobalServerData.class);
                event.subscribe("ranks_config", GlobalRanksConfData.class);
                break;
            case PLAYER:
                event.subscribe("party", PlayerPartyData.class);
                event.subscribe("permissions", PlayerPermissionsData.class);
                event.subscribe("chatstatus", PlayerChatData.class);
                event.subscribe("pseudo", PlayerNameData.class);
                event.subscribe("credits", PlayerCreditData.class);
                event.subscribe("coins", PlayerCoinsData.class);
                break;

        }
    }
}
