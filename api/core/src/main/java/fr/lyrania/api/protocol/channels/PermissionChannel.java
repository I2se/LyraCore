package fr.lyrania.api.protocol.channels;

import fr.lyrania.api.protocol.Channels;
import fr.lyrania.api.services.PermissionsService;
import fr.lyrania.common.protocol.ChannelHandler;
import fr.lyrania.common.protocol.Connection;
import fr.lyrania.common.protocol.IChannels;
import fr.lyrania.common.protocol.io.TcpInput;
import fr.lyrania.common.services.permissions.Ranks;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class PermissionChannel extends ChannelHandler {

    public PermissionChannel(Connection connection) {
        super(connection);
    }

    @Override
    public void onReceiveMessage(TcpInput in) throws IOException {
        byte actionId = in.readByte();
        Optional<Actions> action = Arrays.stream(Actions.values()).filter(action1 -> action1.getId() == actionId).findFirst();

        if (action.isPresent()) {
            switch (action.get()) {
                case SYNC_PERMS_GROUP:
                    UUID uuid = in.readUUID();
                    String key = in.readString();

                    PermissionsService.INSTANCE.updatePermissionGroup(uuid, key);
                    break;
                case SYNC_RANK:
                    byte rankId = in.readByte();

                    Ranks.getRankById(rankId).ifPresent(PermissionsService.INSTANCE::updateRank);
                    break;
            }
        }
    }

    @Override
    public IChannels getChannel() {
        return Channels.PERMISSION;
    }

    public enum Actions {

        // To Server
        UPDATE_PERMS_GROUP(0),
        // From Server
        SYNC_PERMS_GROUP(1),
        SYNC_RANK(2);

        private final byte id;

        Actions(int id) {
            this.id = (byte) id;
        }

        public byte getId() {
            return id;
        }
    }
}
