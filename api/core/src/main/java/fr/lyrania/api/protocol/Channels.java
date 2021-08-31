package fr.lyrania.api.protocol;

import fr.lyrania.api.protocol.channels.*;
import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public enum Channels implements IChannels {

    KEEP_ALIVE(0, KeepAliveChannel.class),
    SERVERSTATE(1, ServersChannel.class),
    NICK(2, NickChannel.class),
    DATA(3, DataChannel.class),
    PERMISSION(4, PermissionChannel.class);

    private final byte id;
    private final Class<? extends ChannelHandler> channelHandlerClass;

    Channels(int id, Class<? extends ChannelHandler> channelHandlerClass) {
        this.id = (byte) id;
        this.channelHandlerClass = channelHandlerClass;
    }

    public Optional<ChannelHandler> createChannelHandler(Connection connection) {
        try {
            return Optional.of(this.channelHandlerClass.getConstructor(Connection.class).newInstance(connection));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public IChannels[] getAll() {
        return values();
    }

    public byte getId() {
        return id;
    }

    public Class<? extends ChannelHandler> getChannelHandler() {
        return channelHandlerClass;
    }
}
