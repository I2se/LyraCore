package fr.lyrania.core.protocol.channels;

import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.protocol.Channels;
import fr.lyrania.core.services.servers.data.GlobalServerData;
import fr.lyrania.core.services.servers.data.ServerData;
import fr.lyrania.common.services.servers.ServerStatus;

import java.io.IOException;

public class ServersChannel extends ChannelHandler {

    public ServersChannel(Connection connection) {
        super(connection);
    }

    @Override
    public void onReceiveMessage(TcpInput in) throws IOException {
        int port = in.readInt();
        ServerStatus serverStatus = ServerStatus.values()[in.readByte()];

        ServerData serverData = DataService.INSTANCE.getGlobalData(GlobalServerData.class).getServers().get(port);
        serverData.setServerStatus(serverStatus);
    }

    @Override
    public IChannels getChannel() {
        return Channels.SERVERSTATE;
    }
}
