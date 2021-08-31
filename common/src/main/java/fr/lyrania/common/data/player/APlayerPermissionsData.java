package fr.lyrania.common.data.player;

import com.google.gson.JsonObject;
import fr.lyrania.common.data.global.GlobalRanksConfData;
import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.database.DataParentAccess;
import fr.lyrania.common.services.permissions.PermissionsGroup;
import fr.lyrania.common.services.permissions.Ranks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class APlayerPermissionsData implements DataHolder<JsonObject>, DataParentAccess {

    private UUID uuid;
    private Ranks rank;
    private Map<String, PermissionsGroup> permissionsGroups;
    
    public APlayerPermissionsData() {
        this.rank = Ranks.JOUEUR;
        this.permissionsGroups = new ConcurrentHashMap<>();
    }

    @Override
    public JsonObject serialize() {
        this.permissionsGroups.remove("rank"); // we don't want to serialize the rank permissions group

        JsonObject json = new JsonObject();
        json.addProperty("rank", this.rank.name());
        json.add("permissionsGroups", this.mapObject(this.permissionsGroups, key -> key, PermissionsGroup::serialize));
        return json;
    }

    @Override
    public void deserialize(JsonObject json) {
        this.ifHasString(json, "rank", value -> {
            try {
                this.setRank(Ranks.valueOf(value));
            } catch (Exception e) {
                this.setRank(Ranks.JOUEUR);
            }
        });
        this.ifHasObject(json, "permissionsGroups", value -> {
            this.permissionsGroups = this.mapJsonObject(new ConcurrentHashMap<>(), value, String::toLowerCase, child -> {
                PermissionsGroup permissionsGroup = new PermissionsGroup();
                permissionsGroup.internDeserialize(child);
                return permissionsGroup;
            });
        });
    }

    public void addPermissionsGroup(String key, PermissionsGroup permissionsGroup) {
        this.permissionsGroups.put(key.toLowerCase(), permissionsGroup);
    }

    public void removePermissionsGroup(String key) {
        if (!key.equalsIgnoreCase("rank")) {
            this.permissionsGroups.remove(key.toLowerCase());
        }
    }

    public void setRank(Ranks rank) {
        this.rank = rank;
    }

    public Optional<PermissionsGroup> getPermissionsGroup(String key) {
        if (this.permissionsGroups.containsKey(key.toLowerCase())) {
            return Optional.of(this.permissionsGroups.get(key.toLowerCase()));
        } else {
            return Optional.empty();
        }
    }

    public List<String> getAllPermissionsStartingWith(String str) {
        return this.permissionsGroups.values().stream()
                .map(permissionsGroup -> permissionsGroup.getAllPermissionsStartingWith(str))
                .flatMap(map -> map.entrySet().stream())
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean hasPermission(String permission) {
        return this.permissionsGroups.values().stream().anyMatch(permissionsGroup -> permissionsGroup.hasPermission(permission));
    }

    public Ranks getRank() {
        return rank;
    }

    public Map<String, PermissionsGroup> getPermissionsGroups() {
        return permissionsGroups;
    }

    @Override
    public void setId(String id) {
        this.uuid = UUID.fromString(id);
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
