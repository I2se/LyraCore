package fr.lyrania.common.services.permissions;

import com.google.gson.JsonArray;

import java.util.Arrays;
import java.util.Optional;

public enum Ranks {

    JOUEUR(0, "§7", "Joueur", 4),
    VIP(1, "§e", "VIP", 6),
    MAITRE(2, "§a", "Maître", 8),
    SUPREME(3, "§b", "Suprême", 10),
    ELITE(4, "§d", "Elite", 12),
    LEGEND(5, "§9", "Legend", 15),
    BUILDER(6, "§a", "Builder", 6),
    MINI_YT(7, "§a", "Mini-YT", 6),
    YOUTUBEUR(8, "§4", "Youtubeur", 8),
    FRIEND(9, "§a", "Friend", 6),
    HELPER(10, "§9", "Helper", 15),
    MODERATEUR(11, "§6", "Modérateur", 15),
    DEVELOPPEUR(12, "§e", "Développeur", 15),
    SUPER_MODERATEUR(13, "§6", "Super-Modérateur", 15),
    MANAGER_MODERATEUR(14, "§6", "Manager-Mod", 15),
    MANAGER_DEVELOPPEUR(15, "§e", "Manager-Dev", 15),
    ADMINISTRATEUR(16, "§c", "Administrateur", 999),
    OWNER(17, "§4", "Owner", 999);

    private final byte id;
    private final String color;
    private final String displayName;
    private final PermissionsGroup permissionsGroup;
    private final int maxSizeParty;

    Ranks(int id, String color, String displayName, int maxSizeParty) {
        this.id = (byte) id;
        this.color = color;
        this.displayName = displayName;
        this.permissionsGroup = new PermissionsGroup();
        this.permissionsGroup.addPosPermission("lyrania.rank." + this.getId());
        this.maxSizeParty = maxSizeParty;
    }

    public void read(JsonArray array) {
        array.forEach(element -> {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String permission = element.getAsString();

                if (permission.startsWith("-")) {
                    this.permissionsGroup.addNegPermission(permission);
                } else {
                    this.permissionsGroup.addPosPermission(permission);
                }
            }
        });
    }

    public static Optional<Ranks> getRankById(byte id) {
        return Arrays.stream(values()).filter(ranks -> ranks.getId() == id).findFirst();
    }

    public byte getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PermissionsGroup getPermissionsGroup() {
        return permissionsGroup;
    }

    public int getMaxSizeParty() {
        return maxSizeParty;
    }
}
