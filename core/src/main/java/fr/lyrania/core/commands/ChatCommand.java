package fr.lyrania.core.commands;

import fr.lyrania.common.data.global.PartyData;
import fr.lyrania.common.data.player.PlayerChatData;
import fr.lyrania.common.services.chat.StatusChat;
import fr.lyrania.core.Const;
import fr.lyrania.core.services.chat.ChatService;
import fr.lyrania.core.services.party.PartyService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatCommand extends Command implements TabExecutor {

    public ChatCommand() {
        super("chat", null, "c");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            PartyData partyData = PartyService.INSTANCE.getParty(player.getUniqueId());
            if(this.hasPermission(sender)) {
                if (args.length == 0) {
                    sendHelp(player);
                }
                if (args.length >= 1) {
                    switch (args[0]) {
                        case "general":
                            ChatService.INSTANCE.setStatusChat(StatusChat.GENERAL, player.getUniqueId());
                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Passage au chat général"));
                            break;
                        case "party":
                            if(partyData != null) {
                                ChatService.INSTANCE.setStatusChat(StatusChat.PARTY, player.getUniqueId());
                                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Passage au chat party"));
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText("Vous n'etes pas dans une partie"));
                            }
                            break;
                        case "guild":
                            ChatService.INSTANCE.setStatusChat(StatusChat.GUILD, player.getUniqueId());
                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Passage au chat guild"));
                            break;

                        default:
                            sendHelp(player);
                            break;
                    }
                }
            }
        } else {
            sender.sendMessage(Const.MUST_BE_PLAYER_MESSAGE.getMessage());
        }
    }

    public void sendHelp(ProxiedPlayer player) {
        player.sendMessage(TextComponent.fromLegacyText("§m-----------------------------------"));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/c general §8- §7Passer au chat général."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/c party §8- §7Passer au chat party."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/c guild §8- §7Passer au chat guild."));
        player.sendMessage(TextComponent.fromLegacyText("§m-----------------------------------"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> tab = new ArrayList<>();
        tab.add("general");
        tab.add("party");
        tab.add("guild");
        return tab;
    }
}
