package fr.lyrania.core.services.party;

import java.util.HashMap;
import java.util.UUID;

public class TempParty {
    
    public static final TempParty INSTANCE = new TempParty();
    
    private HashMap<UUID, UUID> temporaryParty = new HashMap<>();

    public HashMap<UUID, UUID> getTemporaryParty() {
        return temporaryParty;
    }

    public void setTemporaryParty(HashMap<UUID, UUID> temporaryParty) {
        this.temporaryParty = temporaryParty;
    }

    public void addTemporaryParty(UUID uuid1, UUID uuid2) {
        this.temporaryParty.put(uuid1, uuid2);
    }

    public void removeTemporaryParty(UUID uuid1) {
        this.temporaryParty.remove(uuid1);
    }
}
