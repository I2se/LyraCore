package fr.lyrania.core.database.exceptions;

public class ServerNotFoundException extends Exception {

    public ServerNotFoundException() {
        super("Server Not Found");
    }
}
