package fr.lyrania.core.services.perms;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fr.lyrania.common.data.player.APlayerPermissionsData;
import fr.lyrania.common.services.permissions.PermissionsGroup;
import fr.lyrania.common.services.permissions.Ranks;
import fr.lyrania.core.Core;
import fr.lyrania.core.data.player.PlayerPermissionsData;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.protocol.Channels;
import fr.lyrania.core.protocol.ProtocolService;
import fr.lyrania.core.protocol.channels.PermissionChannel;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.apache.commons.lang3.tuple.Triple;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PermissionsService {

    public static final PermissionsService INSTANCE = new PermissionsService();

    private final Table<UUID, String, Triple<PermissionsGroup, ScheduledTask, Long>> expiringPermsGroups;

    private PermissionsService() {
        this.expiringPermsGroups = HashBasedTable.create();
    }

    public void updatePermissionGroup(UUID uuid, String key, PermissionsGroup permissionsGroup) {
        if (permissionsGroup != null && permissionsGroup.getExpireTime() > 0) {
            long currentTime = System.currentTimeMillis();
            long newExpireTime = permissionsGroup.getExpireTime();
            if (this.expiringPermsGroups.contains(uuid, key)) {
                Triple<PermissionsGroup, ScheduledTask, Long> container = this.expiringPermsGroups.get(uuid, key);

                long lastExpireTime = container.getRight();
                if (newExpireTime <= lastExpireTime) {
                    return;
                }

                container.getMiddle().cancel();
            }

            Runnable runnable = () -> {
                DataService.INSTANCE.writeData(APlayerPermissionsData.class, uuid.toString(), data -> {
                    this.expiringPermsGroups.remove(uuid, key);
                    data.removePermissionsGroup(key);
                });
            };

            if (newExpireTime - currentTime <= 0) {
                runnable.run();
            } else {
                ScheduledTask task = Core.getInstance().getProxy().getScheduler().schedule(Core.getInstance(), runnable,
                        newExpireTime - currentTime, TimeUnit.MILLISECONDS);

                this.expiringPermsGroups.put(uuid, key, Triple.of(permissionsGroup, task, newExpireTime));
            }
        } else if (this.expiringPermsGroups.contains(uuid, key)) {
            this.expiringPermsGroups.remove(uuid, key).getMiddle().cancel();
        }

        this.broadcastUpdatePlayer(uuid, key);
    }

    public void broadcastUpdatePlayer(UUID uuid, String key) {
        ProtocolService.INSTANCE.broadcastMessage(Channels.PERMISSION, out -> {
            out.writeByte(PermissionChannel.Actions.SYNC_PERMS_GROUP.getId());

            out.writeUUID(uuid);
            out.writeString(key);
        });
    }

    public void setPlayerRank(UUID uuid, Ranks rank) {
        DataService.INSTANCE.writeData(PlayerPermissionsData.class, uuid.toString(), data -> {
            data.setRank(rank);
            this.broadcastUpdatePlayer(uuid, "rank");
        });
    }

    public void broadcastUpdateRank(Ranks rank) {
        ProtocolService.INSTANCE.broadcastMessage(Channels.PERMISSION, out -> {
            out.writeByte(PermissionChannel.Actions.SYNC_RANK.getId());

            out.writeByte(rank.getId());
        });
    }
}
