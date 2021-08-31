package fr.lyrania.common.database.redis;

public class RedisCredentials {

    private String ip;
    private String password;
    private int port;
    private String clientName;

    public RedisCredentials(String ip, String password, int port) {
        this(ip, password, port, "Redis_HostSystem");
    }

    public RedisCredentials(String ip, String password, int port, String clientName) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.clientName = clientName;
    }

    public String getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getClientName() {
        return clientName;
    }

    public String toRedisURL() {
        return ip + ":" + port;
    }
}
