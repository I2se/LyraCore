package fr.lyrania.common.database;

import java.util.Arrays;
import java.util.Optional;

public enum DataRegistry {

    PLAYER("player"),
    GLOBAL("global");

    private final String collectionName;

    DataRegistry(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public static Optional<DataRegistry> fromCollectionName(String collectionName) {
        return Arrays.stream(values())
                .filter(registry -> registry.getCollectionName().equals(collectionName))
                .findFirst();
    }
}
