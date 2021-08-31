package fr.lyrania.common.data.player;

import com.google.gson.JsonPrimitive;
import fr.lyrania.common.database.DataHolder;

public class PlayerCoinsData implements DataHolder<JsonPrimitive> {

    private double coins = 0.0;

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(coins);
    }

    @Override
    public void deserialize(JsonPrimitive json) {
        if (json.isString()) {
            this.coins = Double.valueOf(json.getAsString());
        }
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }
}
