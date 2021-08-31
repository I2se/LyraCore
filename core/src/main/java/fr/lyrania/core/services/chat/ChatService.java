package fr.lyrania.core.services.chat;

import fr.lyrania.common.data.player.PlayerChatData;
import fr.lyrania.common.services.chat.StatusChat;
import fr.lyrania.core.database.DataService;

import java.util.UUID;

public class ChatService {

    public static final ChatService INSTANCE = new ChatService();

    public void setStatusChat(StatusChat statusChat, UUID uuid) {
        DataService.INSTANCE.writeData(PlayerChatData.class, uuid.toString(), playerChatData -> {
            playerChatData.setChatStatus(statusChat);
        });
    }

    public StatusChat getStatusChat(UUID uuid) {
        return DataService.INSTANCE.getData(PlayerChatData.class, uuid.toString()).getChatStatus();
    }
}
