package fr.lyrania.common.data.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.lyrania.common.database.DataHolder;

public class PlayerPartyData implements DataHolder<JsonPrimitive> {

    private String partyId = "";

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(partyId);
    }

    @Override
    public void deserialize(JsonPrimitive json) {
        if (json.isString()) {
            this.partyId = json.getAsString();
        }
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyId() {
        return partyId;
    }
}
