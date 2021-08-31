package fr.lyrania.core;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import fr.lyrania.common.asm.AgentBuilder;
import fr.lyrania.common.asm.AgentUtils;
import fr.lyrania.core.asm.Agent;
import fr.lyrania.core.commands.ChatCommand;
import fr.lyrania.core.commands.NickCommand;
import fr.lyrania.core.commands.PartyCommand;
import fr.lyrania.core.commands.PermCommand;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.listeners.ChatListener;
import fr.lyrania.core.listeners.DataSubscribeListener;
import fr.lyrania.core.listeners.PlayerListener;
import fr.lyrania.core.protocol.ProtocolService;
import fr.lyrania.core.services.nick.NickService;
import fr.lyrania.core.services.servers.ServerService;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class Core extends Plugin {

    private static Core Instance;

    PteroApplication app = PteroBuilder.createApplication("https://panel.lyrania.fr", "KotPZrdsp66CcjafC0f6uxNv3uDfGULgEOGN3SXgZtrn29JH");
    PteroClient apiclient = PteroBuilder.createClient("https://panel.lyrania.fr", "GDEw1fjrJfRCtUDBydQcHWOHnsWwfdApsJBJqmOCsYppPuSZ");

    @Override
    public void onEnable() {
        getLogger().info("Core initialized");
        Instance = this;

        getLogger().info("Initializing the LyraCore TCP server...");
        try {
            ProtocolService.INSTANCE.createServer();
            getLogger().info("Initialized!");
        } catch (Exception e) {
            getLogger().severe("Error during the creation of the TCP server. " + e);
        }

        DataService.INSTANCE.load();
        registerCommands();
        registerListeners();

        DataService.INSTANCE.registerDataTypes();

        NickService.INSTANCE.load();
        ServerService.INSTANCE.onLoad();

        //loadAgent();

    }

    public void registerCommands() {
        PluginManager pm = getProxy().getPluginManager();

        pm.registerCommand(this, new NickCommand());
        pm.registerCommand(this, new PartyCommand());
        pm.registerCommand(this, new ChatCommand());
        pm.registerCommand(this, new PermCommand());
    }

    public void registerListeners() {
        PluginManager pm = getProxy().getPluginManager();

        pm.registerListener(this, new DataSubscribeListener());
        pm.registerListener(this, new PlayerListener());
        pm.registerListener(this, new ChatListener());
    }

    @Override
    public void onDisable() {
        getLogger().info("Core shutdown");

        DataService.INSTANCE.onDisable();

        ProtocolService.INSTANCE.stopServer();
    }

    private void loadAgent() {
        try {
            String pid = AgentUtils.getCurrentPid();
            AgentBuilder agentBuilder = new AgentBuilder()
                    .setManifestFrom(Core.class)
                    .setAgentClass(Agent.class)
                    .includeJarFor(Core.class)
                    .addDirtyPackage("fr/lyrania/core");

            AgentUtils.attach(pid, agentBuilder.createAgentFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // From Bukkit
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.getFile());
            } else {
                File outFile = new File(this.getDataFolder(), resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(this.getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        this.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    this.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

    // From Bukkit
    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        } else {
            try {
                URL url = this.getClass().getClassLoader().getResource(filename);
                if (url == null) {
                    return null;
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            } catch (IOException var4) {
                return null;
            }
        }
    }

    public static Core getInstance() {
        return Instance;
    }

    public PteroApplication getApp() {
        return app;
    }

    public PteroClient getApiclient() {
        return apiclient;
    }
}
