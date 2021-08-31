package fr.lyrania.api.services;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fr.lyrania.api.LyraAPI;
import fr.lyrania.api.data.PlayerPermissionsData;
import fr.lyrania.api.database.DataService;
import fr.lyrania.api.protocol.Channels;
import fr.lyrania.api.protocol.ProtocolService;
import fr.lyrania.api.protocol.channels.PermissionChannel;
import fr.lyrania.common.services.permissions.PermissionsGroup;
import fr.lyrania.common.services.permissions.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.UUID;

public class PermissionsService {

    public static final PermissionsService INSTANCE = new PermissionsService();

    private final Table<UUID, String, PermissionAttachment> permissionAttachments;

    private PermissionsService() {
        this.permissionAttachments = HashBasedTable.create();
    }

    public void updatePlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.toString(), data -> {
                data.getPermissionsGroups().keySet().forEach(key -> updatePermissionGroup(uuid, key));
                this.updatePermissionGroup(uuid, "rank");
            });
        }
    }

    public void updatePermissionGroup(UUID uuid, String key) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.toString(), data -> {
                if (key.equals("rank")) {
                    data.getPermissionsGroup(key).ifPresent(permissionsGroup -> {
                        this.updatePermissionGroup(uuid, key, permissionsGroup);
                    });
                } else {
                    this.updatePermissionGroup(uuid, "rank", data.getRank().getPermissionsGroup());
                }
            });
        }
    }

    private void updatePermissionGroup(UUID uuid, String key, PermissionsGroup permissionsGroup) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            PermissionAttachment attachment;
            if (this.permissionAttachments.contains(uuid, key)) {
                attachment = this.permissionAttachments.get(uuid, key);
            } else {
                attachment = this.permissionAttachments.put(uuid, key, player.addAttachment(LyraAPI.INSTANCE));
            }
            attachment.getPermissions().clear();

            permissionsGroup.getPermissions().forEach((permission, value) -> {
                attachment.getPermissions().put(permission.toLowerCase(), value);
            });

            attachment.getPermissible().recalculatePermissions();
        }
    }

    public void updateRank(Ranks rank) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            DataService.INSTANCE.getData(PlayerPermissionsData.class, player.getUniqueId().toString(), data -> {
                if (data.getRank() == rank) {
                    this.updatePermissionGroup(player.getUniqueId(), "rank", data.getRank().getPermissionsGroup());
                }
            });
        });
    }

    public void broadcastUpdate(UUID uuid, String key) {
        ProtocolService.INSTANCE.sendToServer(Channels.PERMISSION, out -> {
            out.writeByte(PermissionChannel.Actions.UPDATE_PERMS_GROUP.getId());

            out.writeUUID(uuid);
            out.writeString(key);
        });
    }
}
