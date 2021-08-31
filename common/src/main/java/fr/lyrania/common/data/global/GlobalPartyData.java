package fr.lyrania.common.data.global;

import com.google.gson.JsonObject;
import fr.lyrania.common.database.DataHolder;

import java.util.HashMap;
import java.util.Map;

public class GlobalPartyData implements DataHolder<JsonObject> {

    private Map<String, PartyData> parties = new HashMap<>();

    @Override
    public JsonObject serialize() {
        return this.mapObject(this.parties, str -> str, PartyData::serialize);
    }

    @Override
    public void deserialize(JsonObject json) {
        this.parties = this.mapJsonObject(
            json,
            str -> str,
            element -> {
                PartyData partyData = new PartyData();
                partyData.deserialize(element.getAsJsonObject());
                return partyData;
            }
        );
    }

    public Map<String, PartyData> getParties() {
        return parties;
    }
}
