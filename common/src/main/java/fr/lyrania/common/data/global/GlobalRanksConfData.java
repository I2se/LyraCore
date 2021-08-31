package fr.lyrania.common.data.global;

import com.google.gson.JsonObject;
import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.services.permissions.PermissionsGroup;
import fr.lyrania.common.services.permissions.Ranks;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalRanksConfData implements DataHolder<JsonObject> {

    private Map<Ranks, PermissionsGroup> ranksConf;

    public GlobalRanksConfData() {
        this.ranksConf = Arrays.stream(Ranks.values()).collect(Collectors.toMap(rank -> rank, rank -> new PermissionsGroup()));
    }

    @Override
    public JsonObject serialize() {
        return this.mapObject(this.ranksConf, rank -> String.valueOf(rank.getId()), PermissionsGroup::serialize);
    }

    @Override
    public void deserialize(JsonObject json) {
        this.ranksConf = this.mapJsonObject(
                json,
                key -> Ranks.getRankById(Byte.parseByte(key)).orElse(null),
                value -> {
                    PermissionsGroup group = new PermissionsGroup();
                    group.internDeserialize(value);
                    return group;
                },
                true
        );
    }

    public Map<Ranks, PermissionsGroup> getRanksConf() {
        return ranksConf;
    }
}
