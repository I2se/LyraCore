package fr.lyrania.core.commands.nick;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class OnNickSubCommand implements NickSubCommandHandler {

    @Override
    public void execute(CommandSender sender, ProxiedPlayer target, String[] args, boolean isStaffCommand) {

    }

    /*@Override
    public void execute(CommandSender sender, ProxiedPlayer target, String[] args, boolean isStaffCommand) {
        if (args.length > 0) {
            String name = args[0];
            if (!NickService.USERNAME_PATTERN.matcher(name).matches()) {
                sender.sendMessage(TextComponent.fromLegacyText("§cNom invalide ! (N'utilisez pas de charactères spéciaux et veillez à ne pas mettre un nom trop long)"));
                return;
            }

            if (args.length > 1) {
                String skinUrl = args[1];


                if (skinUrl.equalsIgnoreCase("r
                }
            } else {

            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /nick on <name> [skinUrl]"));
        }
    }*/
}
