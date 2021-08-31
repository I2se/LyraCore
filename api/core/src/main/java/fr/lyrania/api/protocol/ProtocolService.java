package fr.lyrania.api.protocol;

import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.sender.PacketSender;
import fr.lyrania.common.protocol.tcp.TcpPacketEncryptor;
import fr.lyrania.common.protocol.tcp.TcpPacketSizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class ProtocolService {

    public static final ProtocolService INSTANCE = new ProtocolService();

    private static final String HOST = "178.170.41.154";
    private static final int PORT = 10000;

    private static final int CONNECTION_TIMEOUT = 30000;
    private static final long MAX_RECONNECT_DELAY = 5000;

    private Connection connection;
    private boolean shouldReconnect;
    private long reconnectDelay;

    private ProtocolService() {
        this.shouldReconnect = true;
        this.reconnectDelay = 1;
    }

    public void createClient() {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel channel) {
                        connection = new Connection(Channels.KEEP_ALIVE) {

                            @Override
                            public void whenReady() {
                                System.out.println("Client ready!");
                            }
                        };

                        channel.pipeline().addLast("encryptor", new TcpPacketEncryptor(connection));
                        channel.pipeline().addLast("sizer", new TcpPacketSizer());
                        channel.pipeline().addLast("manager", connection);
                    }
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT);

        InetSocketAddress address;
        try {
            address = new InetSocketAddress(InetAddress.getByName(HOST), PORT);
        } catch (UnknownHostException e) {
            address = InetSocketAddress.createUnresolved(HOST, PORT);
        }

        bootstrap.remoteAddress(address).localAddress("0.0.0.0", 0);

        this.startConnection(bootstrap);
    }

    private void startConnection(Bootstrap bootstrap) {
        try {
            bootstrap.connect().sync()
                    .addListener((ChannelFuture future) -> {
                        if (!future.isSuccess()) {
                            long delay = this.nextReconnectDelay();
                            System.out.println("Connection failed, we will re-attempt in " + delay + "ms");
                            future.channel().eventLoop().schedule(() -> {
                                this.startConnection(bootstrap);
                                future.channel().eventLoop().shutdownGracefully();
                            }, delay, TimeUnit.MILLISECONDS);
                        } else {
                            System.out.println("Successfully connected!");
                            this.reconnectDelay = 1;

                            future.channel().closeFuture()
                                    .addListener((ChannelFuture closeFuture) -> {
                                        if (this.shouldReconnect) {
                                            future.channel().eventLoop().schedule(() -> {
                                                this.startConnection(bootstrap);
                                            }, this.nextReconnectDelay(), TimeUnit.MILLISECONDS);
                                        }
                                    });
                        }
                    });
        } catch (InterruptedException e) {
            this.connection.exceptionCaught(null, e);
        }
    }

    private long nextReconnectDelay() {
        return this.reconnectDelay = Math.min(MAX_RECONNECT_DELAY, this.reconnectDelay * 2);
    }

    public void endConnection() {
        this.shouldReconnect = false;
        this.connection.getChannel().flush().close();
        this.connection.onStopped();
        this.connection = null;
    }

    public void sendToServer(Channels channel, PacketSender writeData) {
        this.connection.getChannelsHandlers().get(channel).sendMessage(writeData);
    }

    public Connection getConnection() {
        return connection;
    }
}
