package fr.lyrania.core;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public enum Const {

    MUST_BE_PLAYER_MESSAGE("§cVous devez être un joueur pour effectuer cette action !"),
    NOT_ENOUGH_PERMISSION_MESSAGE("§cVous n'avez pas la permission d'exécuter cette commande !"),
    SPECIFY_SUBCOMMAND("§cMerci de spécifier la sous commande que vous souhaitez exécuter"),
    UNKNOWN_COMMAND("§cCommande Inconnue"),
    SPECIFY_PLAYER("§cMerci de spécifier le joueur"),
    UNKNOWN_PLAYER("§cCe joueur n'existe pas ou ne s'est jamais connecté sur le Lyrania");

    private String message;

    Const(String message) {
        this.message = message;
    }

    public BaseComponent[] getMessage() {
        return TextComponent.fromLegacyText(message);
    }
}
