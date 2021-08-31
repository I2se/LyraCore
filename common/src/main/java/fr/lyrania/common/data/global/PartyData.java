package fr.lyrania.common.data.global;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.lyrania.common.database.DataHolder;

import java.util.*;

public class PartyData implements DataHolder<JsonObject> {

    private String partyID = "";
    private UUID owner = null;
    private List<UUID> playerList = new ArrayList<>();
    private Map<UUID, Boolean> follows = new HashMap<>();
    private List<UUID> inviteParty = new ArrayList<>();

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("partyID", this.partyID);
        json.addProperty("owner", this.owner.toString());
        json.add("playerList", this.mapArray(this.playerList, uuid -> new JsonPrimitive(uuid.toString())));
        json.add("follows", this.mapObject(this.follows, UUID::toString, JsonPrimitive::new));
        json.add("inviteParty", this.mapArray(this.playerList, uuid -> new JsonPrimitive(uuid.toString())));
        return json;
    }

    @Override
    public void deserialize(JsonObject json) {
        this.ifHasString(json, "partyID", value -> this.partyID = value);
        this.ifHasString(json, "owner", value -> this.owner = UUID.fromString(value));
        this.ifHasArray(json, "playerList", value -> {
            this.playerList = this.mapJsonArray(value, element -> UUID.fromString(element.getAsString()));
        });
        this.ifHasObject(json, "follows", value -> {
            this.follows = this.mapJsonObject(value, UUID::fromString, JsonElement::getAsBoolean);
        });
        this.ifHasArray(json , "inviteParty", value -> {
            this.inviteParty = this.mapJsonArray(value, element -> UUID.fromString(element.getAsString()));
        });
    }

    public String getPartyID() {
        return partyID;
    }

    public void setPartyID(String partyID) {
        this.partyID = partyID;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public List<UUID> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<UUID> playerList) {
        this.playerList = playerList;
    }

    public Map<UUID, Boolean> getFollows() {
        return follows;
    }

    public void setFollows(Map<UUID, Boolean> follows) {
        this.follows = follows;
    }

    public List<UUID> getInviteParty() {
        return inviteParty;
    }

    public void setInviteParty(List<UUID> inviteParty) {
        this.inviteParty = inviteParty;
    }

    public void addInviteParty(UUID uuid) {
        inviteParty.add(uuid);
    }

    public void addPlayerParty(UUID uuid) {
        playerList.add(uuid);
    }

    public void removeInviteParty(UUID uuid) {
        inviteParty.remove(uuid);
    }

    public void removePlayerParty(UUID uuid) {
        playerList.remove(uuid);
    }
}
