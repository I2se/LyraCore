package fr.lyrania.core.services.servers.enums;

import java.util.Arrays;

public enum Servers {

    UNKNOWN(25565, "On network"),
    HUB1(25571, "Hub #1");

    private int port;
    private String name;

    Servers(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public static Servers getByPort(Integer port) {
        return Arrays.stream(values()).filter(r -> r.getPort() == port).findAny().orElse(Servers.UNKNOWN);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
