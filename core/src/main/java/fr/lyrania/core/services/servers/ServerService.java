package fr.lyrania.core.services.servers;

import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.servers.controllers.DataServer;
import fr.lyrania.core.services.servers.data.GlobalServerData;
import fr.lyrania.core.services.servers.data.ServerData;
import fr.lyrania.core.services.servers.enums.ServerType;

import java.util.Map;

public class ServerService {

    public static final ServerService INSTANCE = new ServerService();

    public void onLoad() {
        Map<String, ServerData> serverDataMap = DataService.INSTANCE.getGlobalData(GlobalServerData.class).getServers();
        int ctrush = 0;
        int fallenkingdom = 0;
        for (String key : serverDataMap.keySet()) {
            ServerData serverData = serverDataMap.get(key);
            switch (serverData.getServerType()) {
                case CTRUSH:
                    ctrush++;
                    break;
                case FALLENKINGDOM:
                    fallenkingdom++;
                    break;
            }
        }
        if (ctrush == 0) {
            DataServer.INSTANCE.createServer(ServerType.CTRUSH);
            System.out.println("ctrush");
        }
        if (fallenkingdom == 0) {
            DataServer.INSTANCE.createServer(ServerType.FALLENKINGDOM);
            System.out.println("fk");
        }
    }
}
