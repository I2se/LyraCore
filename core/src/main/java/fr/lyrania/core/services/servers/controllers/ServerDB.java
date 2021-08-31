package fr.lyrania.core.services.servers.controllers;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.servers.data.GlobalServerData;
import fr.lyrania.core.services.servers.data.ServerData;
import fr.lyrania.common.services.servers.ServerStatus;
import fr.lyrania.core.services.servers.enums.ServerType;

public class ServerDB {

    public static final ServerDB INSTANCE = new ServerDB();

    public void createServer(ApplicationServer applicationServer, int number, ServerType serverType) {
        DataService.INSTANCE.writeGlobalData(GlobalServerData.class, globalServerData -> {

            ServerData serverData = new ServerData();
            serverData.setServerID(applicationServer.getIdentifier());
            serverData.setPort(applicationServer.getAllocations().get().get(0).getPortInt());
            serverData.setNumberOfServer(number);
            serverData.setName(applicationServer.getName());
            serverData.setServerStatus(ServerStatus.GAMENOTSTARTED);
            serverData.setServerType(serverType);

            globalServerData.getServers().put(applicationServer.getAllocations().get().get(0).getPort(), serverData);
        });
    }

    public void deleteServer(Integer port) {
        DataService.INSTANCE.writeGlobalData(GlobalServerData.class, globalServerData ->  {
            globalServerData.getServers().remove(port);
        });
    }
}
