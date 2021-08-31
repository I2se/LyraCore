package fr.lyrania.core.protocol.channels;

import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;
import fr.lyrania.core.protocol.Channels;
import fr.lyrania.core.protocol.ProtocolService;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class KeepAliveChannel extends ChannelHandler {

    private static final int PERIOD = 60000; // in ms
    private static final int TIMEOUT = 5000; // in ms

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final Random RANDOM = new Random();

    private final ScheduledFuture<?> fixedTask;
    private ScheduledFuture<?> timeoutTask;
    private long keepAliveId;

    public KeepAliveChannel(Connection connection) {
        super(connection);

        this.fixedTask = EXECUTOR_SERVICE.scheduleAtFixedRate(this::checkIfAlive, PERIOD, PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onReceiveMessage(TcpInput in) throws IOException {
        long keepAliveId = in.readLong();

        System.out.println("Received a keep alive packet of id " + keepAliveId + " from " + this.getConnection().getPort());

        if (keepAliveId == this.keepAliveId) {
            System.out.println("Keep alive packet valid!");
            this.timeoutTask.cancel(true);
        }
    }

    public void checkIfAlive() {
        this.keepAliveId = RANDOM.nextLong();

        System.out.println("Send to " + this.getConnection().getPort() + " a keep alive packet of id " + this.keepAliveId);

        this.sendMessage(out -> {
            out.writeLong(this.keepAliveId);
        });
        this.timeoutTask = EXECUTOR_SERVICE.schedule(() -> {
            this.timeoutTask = null;
            ProtocolService.INSTANCE.endConnectionFor(this.getConnection().getPort());
        }, TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onStopped() {
        this.fixedTask.cancel(true);

        if (this.timeoutTask != null) {
            this.timeoutTask.cancel(true);
        }
    }

    @Override
    public IChannels getChannel() {
        return Channels.KEEP_ALIVE;
    }
}
