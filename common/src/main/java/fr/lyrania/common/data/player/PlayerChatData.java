package fr.lyrania.common.data.player;

import com.google.gson.JsonPrimitive;
import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.services.chat.StatusChat;

public class PlayerChatData implements DataHolder<JsonPrimitive> {

    private StatusChat chatStatus = StatusChat.GENERAL;

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(chatStatus.name());
    }

    @Override
    public void deserialize(JsonPrimitive json) {
        if (json.isString()) {
            this.chatStatus = StatusChat.valueOf(json.getAsString());
        }
    }

    public StatusChat getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(StatusChat chatStatus) {
        this.chatStatus = chatStatus;
    }
}
