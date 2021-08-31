package fr.lyrania.common.services.permissions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.lyrania.common.database.DataHolder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PermissionsGroup implements DataHolder<JsonObject> {

    private final Map<String, Boolean> permissions;
    private long expireTime;

    public PermissionsGroup(long expireTime) {
        this.permissions = new LinkedHashMap<>();
        this.expireTime = expireTime;
    }

    public PermissionsGroup() {
        this(0L);
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.add("permissions", this.mapObject(this.permissions, key -> key, JsonPrimitive::new));
        json.addProperty("expireTime", this.expireTime);
        return json;
    }

    @Override
    public void deserialize(JsonObject json) {
        this.ifHasObject(json, "permissions", value -> this.mapJsonObject(new LinkedHashMap<>(), value, key -> key, JsonElement::getAsBoolean));
        this.ifHasNumber(json, "expireTime", value -> this.expireTime = value.longValue());
    }

    public boolean hasPermission(String permission) {
        return this.permissions.entrySet().stream()
                .anyMatch(entry -> {
                    return entry.getValue()
                            // check for star permission
                            && (entry.getKey().endsWith("*")
                            && permission.startsWith(entry.getKey().substring(0, entry.getKey().length() - (entry.getKey().length() == 1 ? 1 : 2)))
                            || !entry.getKey().endsWith("*") && entry.getKey().equals(permission));
                });
    }

    public Map<String, Boolean> getAllPermissionsStartingWith(String str) {
        return this.permissions.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(str))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void addPermission(String permission, boolean value) {
        this.permissions.put(permission, value);
    }

    public void addPosPermission(String permission) {
        this.addPermission(permission, true);
    }

    public void addAllPosPermissions(Iterable<String> permissions) {
        permissions.forEach(this::addPosPermission);
    }

    public void addNegPermission(String permission) {
        this.addPermission(permission, false);
    }

    public void addAllNegPermissions(Iterable<String> permissions) {
        permissions.forEach(this::addNegPermission);
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
