package fr.lyrania.core.commands;

import fr.lyrania.core.commands.nick.NickSubCommandHandler;
import fr.lyrania.core.commands.nick.OnNickSubCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class NickCommand extends Command implements TabExecutor {

    public NickCommand() {
        super("nick", null, "nickname", "nicknames", "nicks");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "staff":
                    break;
                case "help":
                default:
                    this.sendHelpMessage(sender);
                    break;
            }
        } else {
            this.sendHelpMessage(sender);
        }
    }

    private void handleCommand(CommandSender sender, ProxiedPlayer target, String[] args, boolean isStaffCommand) {

    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText("§6§l/nick help : §eAffiche la liste des commandes"));
        sender.sendMessage(TextComponent.fromLegacyText("§6§l/nick on <name> [skinUrl] : §eAffiche la liste des commandes"));
        sender.sendMessage(TextComponent.fromLegacyText("§6§l/nick off : §eAffiche la liste des commandes"));
        sender.sendMessage(TextComponent.fromLegacyText("§6§l/nick setname <name> : §eAffiche la liste des commandes"));
        sender.sendMessage(TextComponent.fromLegacyText("§6§l/nick setskin [skinUrl] : §eAffiche la liste des commandes"));
        sender.sendMessage(TextComponent.fromLegacyText("§6§l/nick reveal <name> : §eAffiche la liste des commandes"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public enum SubCommands {

        ON(new OnNickSubCommand()),
        ;

        private final NickSubCommandHandler handler;

        SubCommands(NickSubCommandHandler handler) {
            this.handler = handler;
        }

        public NickSubCommandHandler getHandler() {
            return handler;
        }
    }
}
