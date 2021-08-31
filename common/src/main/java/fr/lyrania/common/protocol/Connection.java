package fr.lyrania.common.protocol;

import fr.lyrania.common.protocol.encryption.AESEncryption;
import fr.lyrania.common.protocol.encryption.ProtocolEncryption;
import fr.lyrania.common.protocol.io.bytebuf.ByteBufInput;
import fr.lyrania.common.protocol.io.bytebuf.ByteBufOutput;
import fr.lyrania.common.protocol.sender.PacketSender;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Connection extends SimpleChannelInboundHandler<ByteBuf> {

    private ChannelHandlerContext context;
    private Channel channel;
    private ProtocolEncryption encryption;

    private IChannels lyraChannels;
    private final Map<IChannels, ChannelHandler> channelsHandlers;

    public Connection(IChannels channels) {
        this.lyraChannels = channels;
        this.channelsHandlers = new HashMap<>();
    }

    public abstract void whenReady() throws IOException;

    public void createChannelsHandlers() {
        for (IChannels channel : this.lyraChannels.getAll()) {
            channel.createChannelHandler(this).ifPresent(channelHandler -> {
                this.channelsHandlers.put(channel, channelHandler);
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.channel != null) {
            ctx.channel().close();
            return;
        }

        this.context = ctx;
        this.channel = ctx.channel();
        this.createChannelsHandlers();
        this.whenReady();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel() == this.channel) {
            this.channel = null;
            this.onStopped();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        this.channel = null;
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        this.readMessage(buf);
    }

    public void readMessage(ByteBuf buf) throws IOException {
        ByteBufInput in = new ByteBufInput(buf);

        byte channelId = in.readByte();

        Arrays.stream(this.lyraChannels.getAll()).filter(channel -> channel.getId() == channelId).findFirst().ifPresent(channel -> {
            try {
                this.channelsHandlers.get(channel).onReceiveMessage(in);
            } catch (IOException e) {
                exceptionCaught(this.context, e);
            }
        });
    }

    public void sendMessage(IChannels lyraChannel, PacketSender writeData) {
        ByteBufOutput out = new ByteBufOutput(Unpooled.buffer());


        try {
            out.writeByte(lyraChannel.getId());
            writeData.writeData(out);
        } catch (IOException e) {
            exceptionCaught(this.context, e);
        }

        if (!this.channel.isOpen()) {
            return;
        }

        this.channel.writeAndFlush(out.getBuf()).addListener(future -> {
            if (!future.isSuccess()) {
                exceptionCaught(this.context, future.cause());
            }
        });
    }

    public void enableAESEncryption(Key key) {
        try {
            this.encryption = new AESEncryption(key);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return ((InetSocketAddress) this.getChannel().remoteAddress()).getPort();
    }

    public void stop() {
        this.context.close();
    }

    public void onStopped() {
        this.channelsHandlers.values().forEach(ChannelHandler::onStopped);
    }

    public Channel getChannel() {
        return channel;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public ProtocolEncryption getEncryption() {
        return encryption;
    }

    public Map<IChannels, ChannelHandler> getChannelsHandlers() {
        return channelsHandlers;
    }
}
