package fr.lyrania.common.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface DataHolder<T extends JsonElement> {

    T serialize();

    void deserialize(T json);

    default void internDeserialize(JsonElement element) {
        this.deserialize((T) element);
    }

    default void ifHasJsonPrimitive(JsonObject json, String key, Consumer<JsonPrimitive> then) {
        if (json.has(key) && json.get(key).isJsonPrimitive()) {
            then.accept(json.get(key).getAsJsonPrimitive());
        }
    }

    default void ifHasString(JsonObject json, String key, Consumer<String> then) {
        this.ifHasJsonPrimitive(json, key, jsonPrimitive -> {
            if (jsonPrimitive.isString()) {
                then.accept(jsonPrimitive.getAsString());
            }
        });
    }

    default void ifHasNumber(JsonObject json, String key, Consumer<Number> then) {
        this.ifHasJsonPrimitive(json, key, jsonPrimitive -> {
            if (jsonPrimitive.isNumber()) {
                then.accept(jsonPrimitive.getAsNumber());
            }
        });
    }

    default void ifHasBoolean(JsonObject json, String key, Consumer<Boolean> then) {
        this.ifHasJsonPrimitive(json, key, jsonPrimitive -> {
            if (jsonPrimitive.isBoolean()) {
                then.accept(jsonPrimitive.getAsBoolean());
            }
        });
    }

    default void ifHasArray(JsonObject json, String key, Consumer<JsonArray> then) {
        if (json.has(key) && json.get(key).isJsonArray()) {
            then.accept(json.get(key).getAsJsonArray());
        }
    }

    default void ifHasObject(JsonObject json, String key, Consumer<JsonObject> then) {
        if (json.has(key) && json.get(key).isJsonObject()) {
            then.accept(json.get(key).getAsJsonObject());
        }
    }

    default <I> JsonArray mapArray(Iterable<I> data, Function<I, JsonElement> mapper) {
        if (data == null) {
            return new JsonArray();
        }
        JsonArray array = new JsonArray();
        for (I datum : data) {
            array.add(mapper.apply(datum));
        }
        return array;
    }

    default <K, V> JsonObject mapObject(Map<K, V> data, Function<K, String> keyMapper, Function<V, JsonElement> valueMapper) {
        if (data == null) {
            return new JsonObject();
        }
        JsonObject object = new JsonObject();
        data.keySet().forEach(key -> {
            object.add(keyMapper.apply(key), valueMapper.apply(data.get(key)));
        });
        return object;
    }

    default <I> List<I> mapJsonArray(JsonArray array, Function<JsonElement, I> mapper) {
        List<I> list = new ArrayList<>();
        for (JsonElement element : array) {
            list.add(mapper.apply(element));
        }
        return list;
    }

    default <K, V> Map<K, V> mapJsonObject(Map<K, V> container, JsonObject object, Function<String, K> keyMapper, Function<JsonElement, V> valueMapper, boolean removeNull) {
        object.keySet().forEach(key -> {
            K keyMapped = keyMapper.apply(key);
            if (keyMapped != null || !removeNull) {
                container.put(keyMapped, valueMapper.apply(object.get(key)));
            }

        });
        return container;
    }

    default <K, V> Map<K, V> mapJsonObject(Map<K, V> container, JsonObject object, Function<String, K> keyMapper, Function<JsonElement, V> valueMapper) {
        return this.mapJsonObject(container, object, keyMapper, valueMapper, false);
    }

    default <K, V> Map<K, V> mapJsonObject(JsonObject object, Function<String, K> keyMapper, Function<JsonElement, V> valueMapper, boolean removeNull) {
        return this.mapJsonObject(new HashMap<>(), object, keyMapper, valueMapper, removeNull);
    }

    default <K, V> Map<K, V> mapJsonObject(JsonObject object, Function<String, K> keyMapper, Function<JsonElement, V> valueMapper) {
        return this.mapJsonObject(object, keyMapper, valueMapper, false);
    }
}
