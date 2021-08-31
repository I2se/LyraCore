package fr.lyrania.api.protocol.channels;

import fr.lyrania.api.services.NickService;
import fr.lyrania.api.protocol.Channels;
import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class NickChannel extends ChannelHandler {

    public NickChannel(Connection connection) {
        super(connection);
    }

    @Override
    public void onReceiveMessage(TcpInput in) throws IOException {
        byte actionId = in.readByte();
        Optional<Actions> action = Arrays.stream(Actions.values()).filter(action1 -> action1.getId() == actionId).findFirst();

        if (action.isPresent()) {
            switch (action.get()) {
                case UPDATE:
                    UUID uuid = in.readUUID();
                    Player player = Bukkit.getPlayer(uuid);

                    if (player != null) {
                        NickService.INSTANCE.updatePlayer(player);
                    }
                    break;
            }
        }
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
