package fr.lyrania.core.services.nick;

public class NickInfo {

    private final String nickName;
    private final String skinValue;
    private final String skinSignature;

    public NickInfo(String nickName, String skinValue, String skinSignature) {
        this.nickName = nickName;
        this.skinValue = skinValue;
        this.skinSignature = skinSignature;
    }

    public String getNickName() {
        return nickName;
    }

    public String getSkinValue() {
        return skinValue;
    }

    public String getSkinSignature() {
        return skinSignature;
    }
}
