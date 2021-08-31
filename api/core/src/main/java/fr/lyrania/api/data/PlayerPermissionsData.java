package fr.lyrania.api.data;

import fr.lyrania.api.database.DataService;
import fr.lyrania.api.services.PermissionsService;
import fr.lyrania.common.data.global.GlobalRanksConfData;
import fr.lyrania.common.data.player.APlayerPermissionsData;
import fr.lyrania.common.services.permissions.PermissionsGroup;
import fr.lyrania.common.services.permissions.Ranks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerPermissionsData extends APlayerPermissionsData {

    // because if global data is not loaded, we need to do request to load data
    // and due to the fact that this is done async,
    private final List<Runnable> syncTasks;

    public PlayerPermissionsData() {
        this.syncTasks = new ArrayList<>();
    }


    @Override
    public void addPermissionsGroup(String key, PermissionsGroup permissionsGroup) {
        super.addPermissionsGroup(key, permissionsGroup);

        PermissionsService.INSTANCE.broadcastUpdate(this.getUUID(), key);
    }

    @Override
    public void removePermissionsGroup(String key) {
        super.removePermissionsGroup(key);

        PermissionsService.INSTANCE.broadcastUpdate(this.getUUID(), key);
    }

    @Override
    public void setRank(Ranks rank) {
        super.setRank(rank);

        DataService.INSTANCE.getGlobalData(GlobalRanksConfData.class, data -> {
            this.addPermissionsGroup("rank", data.getRanksConf().get(rank));

            this.syncTasks.removeIf(runnable -> { // kind of hacky
                runnable.run();
                return true;
            });
        });
    }

    public void modifyPermissionsGroup(String key, Consumer<PermissionsGroup> modifyGroup) {
        this.getPermissionsGroup(key).ifPresent(group -> {
            modifyGroup.accept(group);

            PermissionsService.INSTANCE.broadcastUpdate(this.getUUID(), key);
        });
    }

    public void queueSyncTask(Runnable runnable) {
        if (this.getPermissionsGroup("rank").isPresent()) {
            runnable.run();
        } else {
            this.syncTasks.add(runnable);
        }
    }
}
