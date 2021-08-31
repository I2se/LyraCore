package fr.lyrania.core.services.servers.enums;

public enum ServerType {

    CTRUSH("CTRush", 10, 17),
    FALLENKINGDOM("Fallen Kingdom", 11,17);

    String name;
    int filter;
    int eggnumber;

    ServerType(String name, int filter, int eggnumber) {
        this.name = name;
        this.filter = filter;
        this.eggnumber = eggnumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public int getEggnumber() {
        return eggnumber;
    }

    public void setEggnumber(int eggnumber) {
        this.eggnumber = eggnumber;
    }
}
