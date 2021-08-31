package fr.lyrania.api.nms;

import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.logging.Level;

public class NMSProvider {

    private static final Optional<INMS> NMS;

    static {
        Optional<INMS> nmsImplementation;
        String versionName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            String packageName = NMSProvider.class.getPackage().getName();
            nmsImplementation = Optional.of(Class.forName(packageName + "." + versionName + ".NMS").asSubclass(INMS.class).newInstance());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "LyraAPI couldn't find a NMS implementation for the server version " + versionName + ".");
            e.printStackTrace();
            nmsImplementation = Optional.empty();
        }
        NMS = nmsImplementation;
    }

    public static Optional<INMS> getNMS() {
        return NMS;
    }
}
