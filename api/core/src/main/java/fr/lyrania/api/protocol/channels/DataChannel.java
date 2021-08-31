package fr.lyrania.api.protocol.channels;

import fr.lyrania.api.database.DataService;
import fr.lyrania.api.protocol.Channels;
import fr.lyrania.common.database.DataRegistry;
import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class DataChannel extends ChannelHandler {

    public DataChannel(Connection connection) {
        super(connection);
    }

    @Override
    public void onReceiveMessage(TcpInput in) throws IOException {
        byte actionId = in.readByte();
        Optional<Actions> action = Arrays.stream(Actions.values()).filter(action1 -> action1.getId() == actionId).findFirst();

        if (action.isPresent()) {
            switch (action.get()) {
                case RESULT:
                    byte rActionId = in.readByte();
                    String rCollection = in.readString();
                    String rId = in.readString();
                    String requestKey = rActionId + ":" + rCollection + ":" + rId;

                    if (DataService.INSTANCE.getPendingRequests().containsKey(requestKey)) {
                        DataService.INSTANCE.getPendingRequests().remove(requestKey)
                                .forEach(consumer -> {
                                    DataRegistry.fromCollectionName(rCollection).ifPresent(registry -> {
                                        DataService.INSTANCE.getDataParent(registry, rId, consumer);
                                    });
                                });
                    }
                    break;
            }
        }
    }

    @Override
    public IChannels getChannel() {
        return Channels.DATA;
    }

    public enum Actions {

        LOAD(0),
        DELETE(1),
        RESULT(2);

        private final byte id;

        Actions(int id) {
            this.id = (byte) id;
        }

        public byte getId() {
            return id;
        }
    }
}
