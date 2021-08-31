package fr.lyrania.core.services.nick;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.lyrania.core.Core;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class SkinProvider {

    private static final Random RANDOM = new Random();

    private final Map<String, String[]> skinsData;
    private List<String> texturesUrl;

    public SkinProvider() {
        this.skinsData = new HashMap<>();
    }

    public void load() {
        this.texturesUrl = this.loadTexturesUrl();
    }

    public List<String> loadTexturesUrl() {
        List<String> texturesUrl = new ArrayList<>();
        File skinFile = new File(Core.getInstance().getDataFolder(), "configs/skin.txt");

        if (!skinFile.exists()) {
            Core.getInstance().saveResource("configs/skin.txt", false);
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(skinFile));
            reader.lines().forEach(texturesUrl::add);
            reader.close();
        } catch (FileNotFoundException e) {
            Core.getInstance().getLogger().severe("It seems that the skin.txt file doesn't exist :");
            e.printStackTrace();
        } catch (IOException e) {
            Core.getInstance().getLogger().severe("The data.yml file couldn't be loaded :");
            e.printStackTrace();
        }

        return texturesUrl;
    }

    public Optional<String[]> chooseRandomSkin() {
        return this.getSkinData(this.texturesUrl.get(RANDOM.nextInt(this.texturesUrl.size())));
    }

    public Optional<String[]> getSkinData(String textureUrl) {
        if (this.skinsData.containsKey(textureUrl)) {
            return Optional.of(this.skinsData.get(textureUrl));
        } else {
            return this.generateSkinData(textureUrl);
        }
    }

    public Optional<String[]> generateSkinData(String textureUrl){
        try {
            String output = queryURL("url=" + URLEncoder.encode(textureUrl, "UTF-8"));
            if (output.isEmpty())
                throw new IllegalStateException("Weird output from mineskin api");

            JsonObject json = new JsonParser().parse(output).getAsJsonObject();
            if (json.has("data")) {
                JsonObject dataJson = json.getAsJsonObject("data");
                if (dataJson.has("texture")) {
                    JsonObject textureJson = dataJson.getAsJsonObject("texture");
                    if (textureJson.has("value") && textureJson.has("signature")) {
                       return Optional.of(new String[]{
                               textureJson.get("value").getAsString(),
                               textureJson.get("signature").getAsString()
                       });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // From SkinRestorer (of course we didn't ask)
    private String queryURL(String query) throws IOException {
        for (int i = 0; i < 3; i++) { // try 3 times, if server not responding
            try {
                HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.mineskin.org/generate/url/").openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("Content-length", String.valueOf(query.length()));
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("User-Agent", "Lyrania");
                con.setConnectTimeout(90000);
                con.setReadTimeout(90000);
                con.setDoOutput(true);
                con.setDoInput(true);

                DataOutputStream output = new DataOutputStream(con.getOutputStream());
                output.writeBytes(query);
                output.close();
                StringBuilder outStr = new StringBuilder();
                InputStream is;

                try {
                    is = con.getInputStream();
                } catch (Exception e) {
                    is = con.getErrorStream();
                }

                DataInputStream input = new DataInputStream(is);
                for (int c = input.read(); c != -1; c = input.read())
                    outStr.append((char) c);

                input.close();
                return outStr.toString();
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    public List<String> getTexturesUrl() {
        return texturesUrl;
    }
}
