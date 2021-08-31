package fr.lyrania.api.services;

import fr.lyrania.api.database.DataService;
import fr.lyrania.common.data.player.PlayerCreditData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CreditsService {

    public void setCredits(UUID uuid, double credits) {
        DataService.INSTANCE.writeData(PlayerCreditData.class, uuid.toString(), playerCreditData -> {
            playerCreditData.setCredits(credits);
        });
    }

    public void addCredits(UUID uuid, double credits) {
        double creditsPlayer = getCredits(uuid);
        double creditsToAdd = creditsPlayer + credits;
        setCredits(uuid,creditsToAdd);
    }

    public void removeCredits(UUID uuid, double credits) {
        double creditsPlayer = getCredits(uuid);
        double creditsToAdd = creditsPlayer - credits;
        setCredits(uuid,creditsToAdd);
    }

    public double getCredits(UUID uuid) {
        AtomicReference<Double> credits = new AtomicReference<>(0.0);
        DataService.INSTANCE.getData(PlayerCreditData.class, uuid.toString(), playerCreditData -> {
            credits.set(playerCreditData.getCredits());
        });
        return credits.get();
    }

    public void resetCredits(UUID uuid) {
        setCredits(uuid, 0.0);
    }
}
