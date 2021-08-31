package fr.lyrania.core.protocol.channels;

import fr.lyrania.common.database.DataRegistry;
import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.protocol.Channels;

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
                case LOAD:
                case DELETE:
                    Optional<DataRegistry> registry = DataRegistry.fromCollectionName(in.readString());

                    if (registry.isPresent()) {
                        String id = in.readString();

                        switch (action.get()) {
                            case LOAD:
                                DataService.INSTANCE.getDataParent(registry.get(), id);
                                break;
                            case DELETE:
                                DataService.INSTANCE.delete(registry.get(), id);
                                break;
                        }
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
