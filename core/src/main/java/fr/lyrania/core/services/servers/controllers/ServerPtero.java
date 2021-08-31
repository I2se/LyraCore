package fr.lyrania.core.services.servers.controllers;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.PteroAction;
import com.mattmalec.pterodactyl4j.ServerStatus;
import com.mattmalec.pterodactyl4j.application.entities.*;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import fr.lyrania.core.Core;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.servers.data.GlobalServerData;
import fr.lyrania.core.services.servers.data.ServerData;
import fr.lyrania.core.services.servers.enums.ServerType;
import net.md_5.bungee.api.ProxyServer;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServerPtero {

    public static final ServerPtero INSTANCE = new ServerPtero();

    private final Core Instance = Core.getInstance();
    public PteroApplication app = Instance.getApp();
    public PteroClient client = Instance.getApiclient();

    public ApplicationServer createServer(ServerType serverType, String name) {

        List<Integer> ports = new ArrayList<>();
        for (ApplicationServer applicationServer : app.retrieveServersByOwner(app.retrieveUserById("4").execute()).execute()) {
            int port = Integer.parseInt(applicationServer.getAllocations().get().get(0).getPort());
            if(port >= 25600 && port <= 25625)
                ports.add(port);
        }
        int port = 25600;
        int i = port;
        if(!ports.isEmpty()) {
            for (; i <= 25625; i++) {
                if (!ports.contains(i)) {
                    port = i;
                    break;
                }
            }
        }

        System.out.println(ports);
        System.out.println(port);

        Nest nest = app.retrieveNestById("6").execute();
        Location location =app.retrieveLocationById("1").execute();
        ApplicationEgg egg = app.retrieveEggById(nest, serverType.getEggnumber()).execute();

        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("SERVER_JARFILE", EnvironmentValue.ofString("server.jar"));
        map.put("VERSION", EnvironmentValue.ofString("latest"));

        PteroAction<ApplicationServer> action = app.createServer()
                .setName(name)
                .setDescription("")
                .setOwner(app.retrieveUserById("4").execute())
                .setEgg(egg)
                .setLocation(location)
                .setCPU(0L)
                .setDisk(5L, DataType.GB)
                .setMemory(4L, DataType.GB)
                .setPort(port)
                .setEnvironment(map)
                .startOnCompletion(true);

        ApplicationServer server = action.execute();


        ProxyServer.getInstance().getScheduler().runAsync(Instance, () -> {
            while (app.retrieveServerById(server.getId()).delay(1, TimeUnit.SECONDS).execute().getStatus().equals(ServerStatus.INSTALLING)) {

            }
            client.retrieveServerByIdentifier(server.getIdentifier()).flatMap(ClientServer::start).executeAsync();
        });

        return server;
    }

    public void deleteServerInPtero(String serverId) {

        ClientServer clientServer = client.retrieveServerByIdentifier(serverId).execute();
        String id = clientServer.getInternalId();
        app.retrieveServerById(id).flatMap(applicationServer -> applicationServer.getController().delete(false)).executeAsync();

    }
}
