package fr.lyrania.core.services.servers.data;

import com.google.gson.JsonObject;
import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.services.servers.ServerStatus;
import fr.lyrania.core.services.servers.enums.ServerType;

public class ServerData implements DataHolder<JsonObject> {

    private String serverID;
    private int port;
    private int numberOfServer;
    private String name;
    private ServerType serverType;
    private ServerStatus serverStatus;

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("serverID", this.serverID);
        json.addProperty("port", this.port);
        json.addProperty("number", this.numberOfServer);
        json.addProperty("name", this.name);
        json.addProperty("type", this.serverType.name());
        json.addProperty("status", this.serverStatus.name());
        return json;
    }

    @Override
    public void deserialize(JsonObject json) {
        this.ifHasString(json , "serverId", value -> this.serverID = value);
        this.ifHasNumber(json , "port", value -> this.port = value.intValue());
        this.ifHasNumber(json , "number", value -> this.numberOfServer = value.intValue());
        this.ifHasString(json , "name", value -> this.name = value);
        this.ifHasString(json , "type", value -> this.serverType = ServerType.valueOf(value));
        this.ifHasString(json , "status", value -> this.serverStatus = ServerStatus.valueOf(value));
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getFullNameServer() {
        return name + " #" + numberOfServer;
    }

    public int getNumberOfServer() {
        return numberOfServer;
    }

    public void setNumberOfServer(int numberOfServer) {
        this.numberOfServer = numberOfServer;
    }
}
