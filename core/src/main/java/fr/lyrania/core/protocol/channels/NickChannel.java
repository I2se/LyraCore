package fr.lyrania.core.protocol.channels;

import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;
import fr.lyrania.core.protocol.Channels;

public class NickChannel extends ChannelHandler {

    public NickChannel(Connection connection) {
        super(connection);
    }

    @Override
    public void onReceiveMessage(TcpInput in) {

    }

    @Override
    public IChannels getChannel() {
        return Channels.NICK;
    }

    public enum Actions {

        UPDATE(0);

        private final byte id;

        Actions(int id) {
            this.id = (byte) id;
        }

        public byte getId() {
            return id;
        }
    }
}
