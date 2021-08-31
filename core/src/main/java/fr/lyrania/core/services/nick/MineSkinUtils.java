package fr.lyrania.core.services.nick;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;

public class MineSkinUtils {

    public static Optional<String[]> generateSkin(String textureUrl){
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
    private static String queryURL(String query) throws IOException {
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
}
