package fr.lyrania.core.services.servers.data;

import com.google.gson.JsonObject;
import fr.lyrania.common.database.DataHolder;

import java.util.HashMap;
import java.util.Map;

public class GlobalServerData implements DataHolder<JsonObject> {

    private Map<String, ServerData> servers = new HashMap<>();

    @Override
    public JsonObject serialize() {
        return this.mapObject(this.servers, str -> str, ServerData::serialize);
    }

    @Override
    public void deserialize(JsonObject json) {
        this.servers = this.mapJsonObject(
                json,
                str -> str,
                element -> {
                    ServerData serverData = new ServerData();
                    serverData.internDeserialize(element);
                    return serverData;
                }
        );
    }

    public Map<String, ServerData> getServers() {
        return servers;
    }
}
