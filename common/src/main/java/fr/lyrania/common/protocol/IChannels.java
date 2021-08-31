package fr.lyrania.common.protocol;

import java.util.Optional;

public interface IChannels {

    byte getId();

    Optional<ChannelHandler> createChannelHandler(Connection connection);

    IChannels[] getAll();
}
