package fr.lyrania.common.protocol;

import fr.lyrania.common.protocol.io.TcpInput;
import fr.lyrania.common.protocol.sender.PacketSender;

import java.io.IOException;

public abstract class ChannelHandler {

    private final Connection connection;

    public ChannelHandler(Connection connection) {
        this.connection = connection;
    }

    public abstract void onReceiveMessage(TcpInput in) throws IOException;

    public abstract IChannels getChannel();

    public void onStopped() {

    }

    public void sendMessage(PacketSender writeData) {
        this.connection.sendMessage(this.getChannel(), writeData);
    }

    public Connection getConnection() {
        return connection;
    }
}
