package fr.lyrania.api;

import com.comphenix.protocol.ProtocolLib;
import fr.lyrania.api.asm.Agent;
import fr.lyrania.api.database.DataService;
import fr.lyrania.api.listeners.DataSubscribeListener;
import fr.lyrania.api.listeners.PlayerListener;
import fr.lyrania.api.services.NickService;
import fr.lyrania.api.protocol.ProtocolService;
import fr.lyrania.common.asm.AgentBuilder;
import fr.lyrania.common.asm.AgentUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class LyraAPI extends JavaPlugin {

    public static LyraAPI INSTANCE; // kind of singleton

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.getLogger().info("Connecting to the LyraCore Protocol...");
        ProtocolService.INSTANCE.createClient();

        DataService.INSTANCE.load();

        this.getServer().getPluginManager().registerEvents(NickService.INSTANCE, this);
        this.getServer().getPluginManager().registerEvents(new DataSubscribeListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        DataService.INSTANCE.registerDataTypes();

        //this.loadAgent();
    }

    @Override
    public void onDisable() {
        DataService.INSTANCE.end();

        ProtocolService.INSTANCE.endConnection();
    }

    private void loadAgent() {
        try {
            String pid = AgentUtils.getCurrentPid();
            AgentBuilder agentBuilder = new AgentBuilder()
                    .setManifestFrom(LyraAPI.class)
                    .setAgentClass(Agent.class)
                    .includeJarFor(LyraAPI.class)
                    .includeJarFor(ProtocolLib.class)
                    .relocate("com/comphenix", "fr/lyrapia/api/libs/protocollib")
                    .addDirtyPackage("fr/lyrania/api")
                    .addDirtyPackage("com/comphenix");

            AgentUtils.attach(pid, agentBuilder.createAgentFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
