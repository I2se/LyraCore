package fr.lyrania.core.utils;

import fr.lyrania.common.data.global.GlobalUUIDData;
import fr.lyrania.core.Core;
import fr.lyrania.core.database.DataService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    public static Optional<UUID> getUUIDFromName(String name) {
        Map<String, UUID> uuids = DataService.INSTANCE.getGlobalData(GlobalUUIDData.class).getNames();
        if (uuids.containsKey(name)) {
            return Optional.of(uuids.get(name));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> getNameFromUUID(UUID uuid) {
        Map<String, UUID> uuids = DataService.INSTANCE.getGlobalData(GlobalUUIDData.class).getNames();
        for (String key : uuids.keySet()) {
            if (uuids.get(key) == uuid) {
                return Optional.of(key);
            }
        }
        return Optional.empty();
    }

    public static Optional<UUID> getPlayer(String name) {
        Optional<UUID> uuid;
        if(isOnline(name)) {
            uuid = Optional.ofNullable(Core.getInstance().getProxy().getPlayer(name).getUniqueId());
        } else {
            uuid = Utils.getUUIDFromName(name);
        }
        return uuid;
    }

    public static Optional<String> getPlayer(UUID uuid) {
        Optional<String> name;
        if(isOnline(uuid)) {
            name = Optional.ofNullable(Core.getInstance().getProxy().getPlayer(uuid).getName());
        } else {
            name = Utils.getNameFromUUID(uuid);
        }
        return name;
    }

    public static boolean isOnline(UUID uuid) {
        if(Core.getInstance().getProxy().getPlayer(uuid) != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOnline(String name) {
        if(Core.getInstance().getProxy().getPlayer(name) != null) {
            return true;
        } else {
            return false;
        }
    }
}
