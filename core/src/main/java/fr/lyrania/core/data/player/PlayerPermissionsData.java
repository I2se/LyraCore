package fr.lyrania.core.data.player;

import fr.lyrania.common.data.global.GlobalRanksConfData;
import fr.lyrania.common.data.player.APlayerPermissionsData;
import fr.lyrania.common.services.permissions.PermissionsGroup;
import fr.lyrania.common.services.permissions.Ranks;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.perms.PermissionsService;

public class PlayerPermissionsData extends APlayerPermissionsData {

    @Override
    public void addPermissionsGroup(String key, PermissionsGroup permissionsGroup) {
        super.addPermissionsGroup(key, permissionsGroup);

        PermissionsService.INSTANCE.updatePermissionGroup(this.getUUID(), key, permissionsGroup);
    }

    @Override
    public void removePermissionsGroup(String key) {
        super.removePermissionsGroup(key);

        PermissionsService.INSTANCE.updatePermissionGroup(this.getUUID(), key, null);
    }

    @Override
    public void setRank(Ranks rank) {
        super.setRank(rank);

        this.addPermissionsGroup("rank", DataService.INSTANCE.getGlobalData(GlobalRanksConfData.class).getRanksConf().get(rank));
    }
}
