package fr.lyrania.core.services.servers.controllers;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.servers.data.GlobalServerData;
import fr.lyrania.core.services.servers.data.ServerData;
import fr.lyrania.core.services.servers.enums.ServerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataServer {

    public static final DataServer INSTANCE = new DataServer();

    public void createServer(ServerType serverType) {
        Map<String, ServerData> servers = DataService.INSTANCE.getGlobalData(GlobalServerData.class).getServers();
        List<ServerData> filterServer = new ArrayList<>();

        for (String key : servers.keySet()) {
            if(servers.get(key).getServerType().equals(serverType)) {
                filterServer.add(servers.get(key));
            }
        }
        int start = 1;
        int i = start;
        if(!filterServer.isEmpty()) {
            for(; i <= 10; i++) {
                for (ServerData serverData : filterServer) {
                    if(serverData.getNumberOfServer() != i) {
                        start = i;
                        break;
                    }
                }
            }
        }

        String name = serverType.getName() + " #" + start;

        ApplicationServer applicationServer = ServerPtero.INSTANCE.createServer(serverType, name);

        ServerDB.INSTANCE.createServer(applicationServer, start, serverType);
    }

    public void deleteServer(Integer port) {
        ServerData serverData = DataService.INSTANCE.getGlobalData(GlobalServerData.class).getServers().get(port);
        ServerPtero.INSTANCE.deleteServerInPtero(serverData.getServerID());
        ServerDB.INSTANCE.deleteServer(port);
    }
}
