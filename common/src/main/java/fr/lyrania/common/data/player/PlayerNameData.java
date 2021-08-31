package fr.lyrania.common.data.player;

import com.google.gson.JsonPrimitive;
import fr.lyrania.common.data.global.GlobalUUIDData;
import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.database.DataParentAccess;

import java.util.UUID;

public class PlayerNameData implements DataHolder<JsonPrimitive> {

    private String pseudo = "";

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(pseudo);
    }

    @Override
    public void deserialize(JsonPrimitive json) {
        if (json.isString()) {
            this.pseudo = json.getAsString();
        }
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
