package fr.lyrania.core.commands.nick;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface NickSubCommandHandler {

    void execute(CommandSender sender, ProxiedPlayer target, String[] args, boolean isStaffCommand);
}
