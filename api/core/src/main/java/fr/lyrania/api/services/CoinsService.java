package fr.lyrania.api.services;

import fr.lyrania.api.database.DataService;
import fr.lyrania.common.data.player.PlayerCoinsData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CoinsService {

    public void setCoins(UUID uuid, double coins) {
        DataService.INSTANCE.writeData(PlayerCoinsData.class, uuid.toString(), playerCoinsData -> {
            playerCoinsData.setCoins(coins);
        });
    }

    public void addCoins(UUID uuid, double coins) {
        double coinsPlayer = getCoins(uuid);
        double coinsToAdd = coinsPlayer + coins;
        setCoins(uuid,coinsToAdd);
    }

    public void removeCoins(UUID uuid, double coins) {
        double coinsPlayer = getCoins(uuid);
        double coinsToAdd = coinsPlayer - coins;
        setCoins(uuid,coinsToAdd);
    }

    public double getCoins(UUID uuid) {
        AtomicReference<Double> coins = new AtomicReference<>(0.0);
        DataService.INSTANCE.getData(PlayerCoinsData.class, uuid.toString(), playerCoinsData -> {
            coins.set(playerCoinsData.getCoins());
        });
        return coins.get();
    }

    public void resetCoins(UUID uuid) {
        setCoins(uuid, 0.0);
    }
}
