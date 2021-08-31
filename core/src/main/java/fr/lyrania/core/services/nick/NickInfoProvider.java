package fr.lyrania.core.services.nick;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.lyrania.core.Core;

import java.util.Optional;
import java.util.function.Consumer;

public class NickInfoProvider {

    private final SkinProvider skinProvider;

    public NickInfoProvider() {
        this.skinProvider = new SkinProvider();
    }

    public void load() {
        this.skinProvider.load();
    }

    /**
     * Get Skin data
     * And putting those data in a {@link NickInfo} object.
     * This function execute the query in async.
     *
     * If the provided skinUrl is null, or he's not an approved url,
     * The skin will be chosen randomly.
     *
     * @param nickName The nickname
     * @param skinUrl The url of the skin
     * @param then The code executed after the query
     */
    public void createNickInfo(String nickName, String skinUrl, Consumer<NickInfo> then) {
        Core.getInstance().getProxy().getScheduler().runAsync(Core.getInstance(), () -> {
            if (skinUrl != null && this.skinProvider.getTexturesUrl().contains(skinUrl)) {
                this.skinProvider.getSkinData(skinUrl).ifPresent(skinData -> {
                    then.accept(new NickInfo(nickName, skinData[0], skinData[1]));
                });
            } else {
                this.skinProvider.chooseRandomSkin().ifPresent(skinData -> {
                    then.accept(new NickInfo(nickName, skinData[0], skinData[1]));
                });
            }
        });
    }

    /**
     *
     */

    // TODO : USE REDIS AND SQL

    /**
     * Load a {@link NickInfo} object from a {@link JsonObject}
     *
     * @param object The JsonObject which contains the data
     * @return The {@link NickInfo} object
     */
    public Optional<NickInfo> loadFromConfig(JsonObject object) {
        if (object.has("nickName") && object.has("skinValue") && object.has("skinSignature")) {
            return Optional.of(new NickInfo(
                    object.getAsJsonPrimitive("nickName").getAsString(),
                    object.getAsJsonPrimitive("skinValue").getAsString(),
                    object.getAsJsonPrimitive("skinSignature").getAsString()
            ));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Save a {@link NickInfo} object in a {@link JsonObject}
     *
     * @param object The JsonObject which will contain the data
     * @param nickInfo The {@link NickInfo} object
     */
    public void saveNickInfo(JsonObject object, NickInfo nickInfo) {
        if (object != null && nickInfo != null) {
            object.add("nickName", new JsonPrimitive(nickInfo.getNickName()));
            object.add("skinValue", new JsonPrimitive(nickInfo.getSkinValue()));
            object.add("skinSignature", new JsonPrimitive(nickInfo.getSkinSignature()));
        }
    }
}
