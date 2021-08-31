package fr.lyrania.api.protocol.channels;

import fr.lyrania.api.protocol.Channels;
import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;

import java.io.IOException;

public class KeepAliveChannel extends ChannelHandler {

    public KeepAliveChannel(Connection connection) {
        super(connection);
    }

    @Override
    public void onReceiveMessage(TcpInput in) throws IOException {
        long keepAliveId = in.readLong();

        System.out.println("Received keep alive packet id of " + keepAliveId + " from server");

        this.sendMessage(out -> {
            out.writeLong(keepAliveId);
        });
    }

    @Override
    public IChannels getChannel() {
        return Channels.KEEP_ALIVE;
    }
}
