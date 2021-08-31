package fr.lyrania.core.commands;

import fr.lyrania.common.data.global.PartyData;
import fr.lyrania.core.Const;
import fr.lyrania.core.Core;
import fr.lyrania.core.data.player.PlayerPermissionsData;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.party.PartyService;
import fr.lyrania.core.services.party.TempParty;
import fr.lyrania.core.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class PartyCommand extends Command implements TabExecutor {

    public PartyCommand() {
        super("party", null, "p");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            PartyData partyDataPlayer = PartyService.INSTANCE.getParty(player.getUniqueId());
            if(this.hasPermission(sender)) {
                if(args.length == 0) {
                    sendHelp(player);
                }
                if(args.length >= 1) {
                    switch (args[0]) {
                        case "invite":
                            if(args.length == 2) {
                                Optional<UUID> targetOpti = Utils.getPlayer(args[1]);
                                if(targetOpti.isPresent()) {
                                    UUID target = targetOpti.get();
                                    PlayerPermissionsData permissionsData = DataService.INSTANCE.getData(PlayerPermissionsData.class, player.getUniqueId().toString());
                                    int limit = permissionsData.getAllPermissionsStartingWith("lyracore.party.limit.").stream().map(str -> {
                                                String[] array = str.split("\\.");
                                                if (array.length < 4) {
                                                    return -1;
                                                }
                                                try {
                                                    return Integer.parseInt(array[3]);
                                                } catch (NumberFormatException e) {
                                                    return -1;
                                                }
                                            }).filter(value -> value >= 0).max(Comparator.comparingInt(value -> value)).orElse(0);
                                    if(partyDataPlayer != null) {
                                        int numberPlayerInParty;
                                        PartyData partyDataTarget = PartyService.INSTANCE.getParty(target);
                                        if(partyDataPlayer.getOwner().equals(player.getUniqueId())) {
                                            numberPlayerInParty = partyDataPlayer.getPlayerList().size();
                                            invitePlayer(numberPlayerInParty, limit, player, target, partyDataPlayer.getPartyID());
                                        } else {
                                            numberPlayerInParty = PartyService.INSTANCE.getParty(partyDataPlayer.getOwner()).getPlayerList().size();
                                            invitePlayer(numberPlayerInParty, limit, player, target, partyDataPlayer.getPartyID());
                                        }
                                    } else {
                                        if(TempParty.INSTANCE.getTemporaryParty().containsKey(player.getUniqueId()) && TempParty.INSTANCE.getTemporaryParty().containsValue(target)) {
                                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous avez deja envoyé une invitation a ce joueur"));
                                        } else {
                                            TempParty.INSTANCE.addTemporaryParty(player.getUniqueId(), target);
                                        }
                                    }
                                } else {
                                    player.sendMessage(Const.UNKNOWN_PLAYER.getMessage());
                                }
                            } else {
                                sendHelp(player);
                            }
                            break;
                        case "kick":
                            if(args.length == 2) {
                                Optional<UUID> targetOpti = Utils.getPlayer(args[1]);
                                if(targetOpti.isPresent()) {
                                    UUID target = targetOpti.get();
                                    if(partyDataPlayer != null) {
                                        if(partyDataPlayer.getPlayerList().size() > 2) {
                                            if(partyDataPlayer.getOwner().equals(player.getUniqueId())) {
                                                PartyService.INSTANCE.leaveParty(target);
                                                Optional<String> pseudoTarget = Utils.getNameFromUUID(target);
                                                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Vous avez exclu " + pseudoTarget.get() + " du groupe"));
                                                if(Utils.isOnline(target)) {
                                                    ProxiedPlayer playerTarget = Core.getInstance().getProxy().getPlayer(target);
                                                    playerTarget.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous avez été exclu du groupe"));
                                                }
                                            } else {
                                                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&cVous devez être chef pour kick une personne")));
                                            }
                                        } else {
                                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Action impossible"));
                                        }
                                    } else {
                                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&cVous devez être dans un groupe")));
                                    }
                                } else {
                                    player.sendMessage(Const.UNKNOWN_PLAYER.getMessage());
                                }
                            } else {
                                sendHelp(player);
                            }
                            break;
                        case "join":
                            if(args.length == 2) {
                                Optional<UUID> targetOpti = Utils.getPlayer(args[1]);
                                if(targetOpti.isPresent()) {
                                    UUID target = targetOpti.get();
                                    PartyData partyDataTarget = PartyService.INSTANCE.getParty(target);
                                    if(partyDataTarget != null) {
                                        if(partyDataPlayer != null) {
                                            PartyService.INSTANCE.leaveParty(player.getUniqueId());
                                        }
                                        PartyService.INSTANCE.setParty(player.getUniqueId(), partyDataTarget.getPartyID());
                                        PartyService.INSTANCE.addPlayerToParty(player.getUniqueId(), partyDataPlayer.getPartyID());
                                        PartyService.INSTANCE.deleteInvite(player.getUniqueId(), partyDataPlayer.getPartyID());
                                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Vous avez rejoint le groupe de " + Utils.getNameFromUUID(target).get()));
                                        if(Utils.isOnline(target)) {
                                            ProxiedPlayer proxiedPlayer = Core.getInstance().getProxy().getPlayer(target);
                                            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + player.getDisplayName() + " a rejoint votre groupe"));
                                        }
                                    } else {
                                        if(TempParty.INSTANCE.getTemporaryParty().containsKey(target) && TempParty.INSTANCE.getTemporaryParty().containsValue(player.getUniqueId())) {
                                            if(partyDataPlayer != null) {
                                                PartyService.INSTANCE.leaveParty(player.getUniqueId());
                                            }
                                            PartyService.INSTANCE.createParty(target, player.getUniqueId());
                                            TempParty.INSTANCE.removeTemporaryParty(player.getUniqueId());
                                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Vous avez rejoint le groupe"));
                                            if(Utils.isOnline(target)) {
                                                ProxiedPlayer proxiedPlayer = Core.getInstance().getProxy().getPlayer(target);
                                                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Nouveau groupe creer avec " + player.getDisplayName()));
                                            }
                                        } else {
                                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous n'avez pas d'invite de ce joueur"));
                                        }
                                    }
                                } else {
                                    player.sendMessage(Const.UNKNOWN_PLAYER.getMessage());
                                }
                            } else {
                                sendHelp(player);
                            }
                            break;
                        case "leave":
                            if(args.length == 1) {
                                if(partyDataPlayer != null) {
                                    if(partyDataPlayer.getPlayerList().size() == 2) {
                                        PartyService.INSTANCE.deleteParty(partyDataPlayer.getPartyID());
                                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Vous avez dissous le groupe"));
                                    } else if(partyDataPlayer.getOwner().equals(player.getUniqueId())) {
                                        PartyService.INSTANCE.deleteParty(partyDataPlayer.getPartyID());
                                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Vous avez dissous le groupe"));
                                    } else {
                                        PartyService.INSTANCE.leaveParty(player.getUniqueId());
                                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&cVous avez quitté le groupe")));
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&cVous devez être dans un groupe pour le quitter")));
                                }
                            } else {
                                sendHelp(player);
                            }
                            break;
                        case "on":
                            if(args.length == 1) {
                                if (partyDataPlayer != null) {
                                    if(partyDataPlayer.getFollows().get(player.getUniqueId())) {
                                        partyDataPlayer.getFollows().remove(player.getUniqueId());
                                        partyDataPlayer.getFollows().put(player.getUniqueId(), false);
                                    } else {
                                        partyDataPlayer.getFollows().remove(player.getUniqueId());
                                        partyDataPlayer.getFollows().put(player.getUniqueId(), true);
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&cVous devez être dans un groupe pour le quitter")));
                                }
                            }
                            break;
                        case "warp":
                            if(args.length == 1) {
                                if(partyDataPlayer != null) {
                                    if(partyDataPlayer.getOwner().equals(player.getUniqueId())) {
                                        Server server = player.getServer();
                                        List<UUID> playersInParty = partyDataPlayer.getPlayerList();
                                        for (UUID uuid : playersInParty) {
                                            // Waiting telepoortation socket
                                        }
                                    } else {
                                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous devez être chef"));
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous devez avoir un groupe"));
                                }
                            } else {
                                sendHelp(player);
                            }
                            break;
                        case "list":
                            if(args.length == 1 || args.length == 2) {
                                if(partyDataPlayer != null) {
                                    int page = 0;
                                    final int maxStrings = 5;
                                    if(args.length == 2) {
                                        try {
                                            page = Math.max(0, Math.min(Integer.parseInt(args[1]) - 1, partyDataPlayer.getPlayerList().size() / maxStrings));
                                        } catch (NumberFormatException e) {
                                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Veuillez entrer un numero de page valide"));
                                        }
                                    }
                                    player.sendMessage(TextComponent.fromLegacyText("" + ChatColor.GOLD + ChatColor.BOLD + "Partie : " + ChatColor.GRAY + "(" + partyDataPlayer.getPlayerList().size() + "/NTM)"));
                                    int currentIndex = page * maxStrings;
                                    int maxIndex = (page + 1) * maxStrings;
                                    for (int i = currentIndex; i < maxIndex; i++) {
                                        player.sendMessage(TextComponent.fromLegacyText(PartyService.INSTANCE.showPlayerList(partyDataPlayer.getPlayerList().get(i))));
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous devez faire parti d'un groupe"));
                                }
                            } else {
                                sendHelp(player);
                            }
                            break;
                        case "lead":
                            if(args.length == 2) {
                                if(partyDataPlayer != null) {
                                    if(partyDataPlayer.getOwner().equals(player.getUniqueId())) {
                                        Optional<UUID> uuidTarget = Utils.getUUIDFromName(args[1]);
                                        if (uuidTarget.isPresent()) {
                                            if(partyDataPlayer.getPlayerList().contains(uuidTarget)) {
                                                partyDataPlayer.setOwner(uuidTarget.get());
                                            } else {
                                                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Ce joueur n'est pas dans le groupe"));
                                            }
                                        } else {
                                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Le joueur n'existe pas"));
                                        }
                                    } else {
                                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous devez être chef"));
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous devez faire parti d'un groupe"));
                                }
                            } else {
                                sendHelp(player);
                            }
                            break;

                        default:
                            sendHelp(player);
                            break;
                    }
                }
            } else {
                player.sendMessage(player.getUniqueId(), Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
            }
        } else {
            sender.sendMessage(Const.MUST_BE_PLAYER_MESSAGE.getMessage());
        }
    }

    public void invitePlayer(int numberPlayerInParty, int limit, ProxiedPlayer player, UUID target, String partyID) {
        if(numberPlayerInParty < limit) {
            PartyService.INSTANCE.addInvite(target, partyID);
            String nameTarget = Utils.getNameFromUUID(target).get();
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Vous avez invité " + nameTarget + " a rejoindre votre groupe"));
            if(Utils.isOnline(target)) {
                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.GREEN + player.getDisplayName() + " vous invite à rejoindre son groupe"));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + player.getDisplayName()));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Cliquez pour rejoindre le groupe").create()));
                ProxiedPlayer playerTarget = Core.getInstance().getProxy().getPlayer(target);
                playerTarget.sendMessage(textComponent);
            }
        } else {
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Vous avez atteint la limite du groupe"));
        }
    }

    public void sendHelp(ProxiedPlayer player) {
        player.sendMessage(TextComponent.fromLegacyText("§m-----------------------------------"));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p invite <player> §8- §7Inviter un joueur."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p kick <player> §8- §7Expulser un joueur."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p join <player> §8- §7Rejoindre une partie."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p leave <player> §8- §7Quitter une partie."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p on §8❘ §coff §8- §7Activer / Désactiver le suivi de partie."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p warp §8- §7Téléporter les membres sur le serveur du chef de groupe."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p list §8- §7Afficher les membres du groupes."));
        player.sendMessage(TextComponent.fromLegacyText("§8• §a/p lead §8- §7Définir le chef du groupe."));
        player.sendMessage(TextComponent.fromLegacyText("§m-----------------------------------"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> completionarg1 = new ArrayList<>();
            completionarg1.add("invite");
            completionarg1.add("kick");
            completionarg1.add("join");
            completionarg1.add("leave");
            completionarg1.add("on");
            completionarg1.add("warp");
            completionarg1.add("list");
            completionarg1.add("lead");

            return completionarg1;
        }
        if(args[0].equalsIgnoreCase("invite")) {
            if(args.length == 2) {
                List<String> completioninvite = new ArrayList<>();
                Collection<ProxiedPlayer> allplayers = Core.getInstance().getProxy().getPlayers();
                for (ProxiedPlayer player : allplayers) {
                    completioninvite.add(player.getDisplayName());
                }
                return completioninvite;
            }
        } else if(args[0].equalsIgnoreCase("kick")) {
            if(args.length == 2) {
                List<String> completionkick = new ArrayList<>();
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
                List<UUID> allplayersparty = PartyService.INSTANCE.getParty(proxiedPlayer.getUniqueId()).getPlayerList();
                for (UUID uuid : allplayersparty) {
                    ProxiedPlayer player = Core.getInstance().getProxy().getPlayer(uuid);
                    if(player != null) {
                        completionkick.add(player.getDisplayName());
                    }
                }
                return completionkick;
            }
        } else if(args[0].equalsIgnoreCase("join")) {
            if(args.length == 2) {
                List<String> completionjoin = new ArrayList<>();
                Collection<ProxiedPlayer> allplayers = Core.getInstance().getProxy().getPlayers();
                for (ProxiedPlayer player : allplayers) {
                    completionjoin.add(player.getDisplayName());
                }
                return completionjoin;
            }
        } else if(args[1].equalsIgnoreCase("lead")) {
            if(args.length == 2) {
                List<String> completionlead = new ArrayList<>();
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
                List<UUID> allplayersparty = PartyService.INSTANCE.getParty(proxiedPlayer.getUniqueId()).getPlayerList();
                for (UUID uuid : allplayersparty) {
                    ProxiedPlayer player = Core.getInstance().getProxy().getPlayer(uuid);
                    if(player != null) {
                        completionlead.add(player.getDisplayName());
                    }
                }
                return completionlead;
            }
        }
        return new ArrayList<>();
    }
}
