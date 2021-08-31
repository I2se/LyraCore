package fr.lyrania.core.protocol;

import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.sender.PacketSender;
import fr.lyrania.common.protocol.tcp.TcpPacketEncryptor;
import fr.lyrania.common.protocol.tcp.TcpPacketSizer;
import fr.lyrania.core.Core;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProtocolService {

    public static final ProtocolService INSTANCE = new ProtocolService();

    private static final String HOST = "127.0.0.1"; // in ip format
    private static final int PORT = 10000;

    private static final Random RANDOM = new Random();

    private final Map<Integer, Connection> connections;

    private KeyPair keyPair;
    private byte[] verifyToken;

    public ProtocolService() {
        this.connections = new HashMap<>();
    }

    public void createServer() throws Exception {
        this.initEncryption();

        Thread thread = new Thread(() -> {
            EventLoopGroup group = new NioEventLoopGroup();

            try {
                ServerBootstrap bootstrap = new ServerBootstrap()
                        .group(group)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                Connection connection = new Connection(Channels.KEEP_ALIVE) {

                                    @Override
                                    public void whenReady() {
                                        connections.put(this.getPort(), this);
                                    }
                                };

                                channel.pipeline().addLast("encryptor", new TcpPacketEncryptor(connection));
                                channel.pipeline().addLast("sizer", new TcpPacketSizer());
                                channel.pipeline().addLast("manager", connection);
                            }
                        })
                        .localAddress(HOST, PORT)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture future = bootstrap.bind(PORT).sync();

                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        });
        thread.setContextClassLoader(Core.getInstance().getClass().getClassLoader());
        thread.start();
    }

    private void initEncryption() throws IllegalStateException {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024);
            this.keyPair = gen.generateKeyPair();
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate server key pair.", e);
        }

        RANDOM.nextBytes(this.verifyToken = new byte[4]);
    }

    public void endConnectionFor(int port) {
        if (this.connections.containsKey(port)) {
            Connection connection = this.connections.remove(port);
            connection.getChannel().flush().close();
        }
    }

    public void stopServer() {
        this.connections.keySet().forEach(this::endConnectionFor);
    }

    public void broadcastMessage(Channels channel, PacketSender writeData) {
        this.connections.values().forEach(connection -> connection.getChannelsHandlers().get(channel).sendMessage(writeData));
    }

    public Map<Integer, Connection> getConnections() {
        return connections;
    }
}
