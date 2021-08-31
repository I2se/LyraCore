package fr.lyrania.api.protocol.channels;

import fr.lyrania.api.protocol.Channels;
import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;

public class ServersChannel extends ChannelHandler {

    public ServersChannel(Connection connection) {
        super(connection);
    }

    @Override
    public void onReceiveMessage(TcpInput in) {

    }

    @Override
    public IChannels getChannel() {
        return Channels.SERVERSTATE;
    }
}
