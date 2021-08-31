package fr.lyrania.common.data.player;

import com.google.gson.JsonPrimitive;
import fr.lyrania.common.database.DataHolder;

public class PlayerCreditData implements DataHolder<JsonPrimitive> {

    private double credits = 0.0;

    @Override
    public JsonPrimitive serialize() {
        return new JsonPrimitive(credits);
    }

    @Override
    public void deserialize(JsonPrimitive json) {
        if (json.isString()) {
            this.credits = Double.valueOf(json.getAsString());
        }
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }
}
