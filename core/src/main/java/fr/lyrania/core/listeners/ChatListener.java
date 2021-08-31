package fr.lyrania.core.listeners;

import fr.lyrania.common.data.global.PartyData;
import fr.lyrania.core.Core;
import fr.lyrania.core.services.chat.ChatService;
import fr.lyrania.core.services.party.PartyService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        UUID uuid = player.getUniqueId();
        switch (ChatService.INSTANCE.getStatusChat(uuid)) {
            case PARTY:
                event.setCancelled(true);
                PartyData partyData = PartyService.INSTANCE.getParty(player.getUniqueId());
                List<UUID> allplayers = partyData.getPlayerList();
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "[" + player.getDisplayName() + "]" + ChatColor.WHITE + event.getMessage()));
                for (UUID uuidreceiver : allplayers) {
                    ProxiedPlayer proxiedPlayer = Core.getInstance().getProxy().getPlayer(uuidreceiver);
                    if(proxiedPlayer != null) {
                        proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "[" + player.getDisplayName() + "]" + ChatColor.WHITE + event.getMessage()));
                    }
                }
                break;
            case GUILD:

                break;
        }
    }
}
