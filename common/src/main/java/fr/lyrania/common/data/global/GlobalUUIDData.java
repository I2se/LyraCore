package fr.lyrania.common.data.global;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.lyrania.common.database.DataHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalUUIDData implements DataHolder<JsonObject> {

    private Map<String, UUID> names = new HashMap<>();

    @Override
    public JsonObject serialize() {
        return this.mapObject(this.names, key -> key, value -> new JsonPrimitive(value.toString()));
    }

    @Override
    public void deserialize(JsonObject json) {
        this.names = this.mapJsonObject(json, key -> key, value -> UUID.fromString(value.getAsString()));
    }

    public Map<String, UUID> getNames() {
        return names;
    }
}
