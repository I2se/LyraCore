package fr.lyrania.core.commands;

import fr.lyrania.common.data.global.GlobalRanksConfData;
import fr.lyrania.common.services.permissions.PermissionsGroup;
import fr.lyrania.common.services.permissions.Ranks;
import fr.lyrania.core.Const;
import fr.lyrania.core.data.player.PlayerPermissionsData;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.perms.PermissionsService;
import fr.lyrania.core.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class PermCommand extends Command {

    public PermCommand() {
        super("permission", null, "perm", "perms", "permissions");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission")) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "setrank":
                        this.handleSetRank(sender, args);
                        break;
                    case "perm":
                        if (sender.hasPermission("lyracore.commands.permission.perm")) {
                            if (args.length > 1) {
                                switch (args[1]) {
                                    case "addgroup":
                                        this.handleAddPermGroup(sender, args);
                                        break;
                                    case "removegroup":
                                        this.handleRemovePermGroup(sender, args);
                                        break;
                                    case "setplayer":
                                        this.handleSetPlayerPerm(sender, args);
                                        break;
                                    case "setrank":
                                        this.handleSetRankPerm(sender, args);
                                        break;
                                    default:
                                        this.sendHelpMessage(sender);
                                        break;
                                }
                            } else {
                                this.sendHelpMessage(sender);
                            }
                        } else {
                            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
                        }
                        break;
                    case "showplayer":
                        this.handleShowPlayer(sender, args);
                        break;
                    case "showrank":
                        this.handleShowRank(sender, args);
                        break;
                    case "test":
                        this.handlePermTest(sender, args);
                        break;
                    case "help":
                    default:
                        this.sendHelpMessage(sender);
                        break;
                }
            } else {
                this.sendHelpMessage(sender);
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        if (sender.hasPermission("lyracore.commands.permission.help")) {
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission help"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission setrank <player> <rank>"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission perm addgroup <player> <key> [expireTime]"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission perm removegroup <player> <key>"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission perm setplayer <player> <key> <perm> [enabled]"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission perm setrank <rank> <perm> [enabled]"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission showplayer <player> [key] [page]"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission showrank <rank> [page]"));
            sender.sendMessage(TextComponent.fromLegacyText("§6/permission test <player> <perm>"));
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void handleSetRank(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.setrank")) {
            if (args.length > 2) {
                Optional<UUID> uuid = Utils.getUUIDFromName(args[1]);

                if (uuid.isPresent()) {
                    Ranks rank;
                    try {
                        rank = Ranks.valueOf(args[2].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cGrade inconnu !"));
                        return;
                    }

                    DataService.INSTANCE.writeData(PlayerPermissionsData.class, uuid.get().toString(), data -> {
                        data.setRank(rank);
                        sender.sendMessage(TextComponent.fromLegacyText("§bLe grade du Joueur " + args[1] + " a bien été modifié en " + rank.name() + " !"));
                    });
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cJoueur inconnue !"));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission setrank <player> <rank> !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void handleAddPermGroup(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.perm.addgroup")) {
            if (args.length > 3) {
                Optional<UUID> uuid = Utils.getUUIDFromName(args[2]);

                if (uuid.isPresent()) {
                    String key = args[3].toLowerCase();
                    PlayerPermissionsData readData = DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.get().toString());

                    if (readData.getPermissionsGroups().containsKey(key)) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cLe Joueur " + args[2] + " possède déjà un groupe de permissions avec comme clé \"" + key + "\" !"));
                        return;
                    }

                    AtomicLong expireTime = new AtomicLong(0L);
                    if (args.length > 4) {
                        try {
                             expireTime.set(System.currentTimeMillis() + Long.parseLong(args[4]) * 1000L);

                             if (expireTime.get() <= 0) {
                                 throw new NumberFormatException();
                             }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(TextComponent.fromLegacyText("§cTemps d'expiration invalide !"));
                            return;
                        }
                    }

                    DataService.INSTANCE.writeData(PlayerPermissionsData.class, uuid.get().toString(), writeData -> {
                        writeData.addPermissionsGroup(key, new PermissionsGroup(expireTime.get()));
                        sender.sendMessage(TextComponent.fromLegacyText("§bLe groupe de permission \"" + key + "\" a bien été ajouté dans le Joueur " + args[2] + " !"));
                    });
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cJoueur inconnue !"));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission addgroup <player> <key> [expireTime] !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void handleRemovePermGroup(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.perm.removegroup")) {
            if (args.length > 3) {
                Optional<UUID> uuid = Utils.getUUIDFromName(args[2]);

                if (uuid.isPresent()) {
                    String key = args[3].toLowerCase();
                    PlayerPermissionsData readData = DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.get().toString());

                    if (!key.equalsIgnoreCase("rank")) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cVous ne pouvez pas enlever ce groupe de permissions !"));
                        return;
                    }

                    if (!readData.getPermissionsGroups().containsKey(key)) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cLe Joueur " + args[2] + " ne possède pas un groupe de permissions avec comme clé \"" + key + "\" !"));
                        return;
                    }

                    DataService.INSTANCE.writeData(PlayerPermissionsData.class, uuid.get().toString(), writeData -> {
                        writeData.removePermissionsGroup(key);
                        sender.sendMessage(TextComponent.fromLegacyText("§bLe groupe de permission \"" + key + "\" a bien été enlevé du Joueur " + args[2] + " !"));
                    });
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cJoueur inconnue !"));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission removegroup <player> <key> !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void handleSetPlayerPerm(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.perm.setplayer")) {
            if (args.length > 4) {
                Optional<UUID> uuid = Utils.getUUIDFromName(args[2]);

                if (uuid.isPresent()) {
                    String key = args[3].toLowerCase();
                    PlayerPermissionsData readData = DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.get().toString());

                    if (!key.equalsIgnoreCase("rank")) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cVous ne pouvez pas modifier ce groupe de permissions !"));
                        return;
                    }

                    if (!readData.getPermissionsGroups().containsKey(key)) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cLe Joueur " + args[2] + " ne possède pas un groupe de permissions avec comme clé \"" + key + "\" !"));
                        return;
                    }

                    String permission = args[4];
                    AtomicBoolean enabled = new AtomicBoolean(true);
                    if (args.length > 5) {
                        enabled.set(Boolean.parseBoolean(args[5]));
                    }

                    DataService.INSTANCE.writeData(PlayerPermissionsData.class, uuid.get().toString(), writeData -> {
                        writeData.getPermissionsGroup(key).ifPresent(group -> {
                            group.addPermission(permission, enabled.get());
                            sender.sendMessage(TextComponent.fromLegacyText("§bLa permission \"" + permission + "\" a bien été " + (enabled.get() ? "activé" : "désactivé") + " dans le groupe de permission \"" + key + "\" du Joueur " + args[2] + " !"));
                        });
                    });
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cJoueur inconnue !"));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission perm setplayer <player> <key> <perm> [enabled] !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void handleSetRankPerm(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.perm.setrank")) {
            if (args.length > 3) {
                Ranks rank;
                try {
                    rank = Ranks.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cGrade inconnu !"));
                    return;
                }

                String permission = args[4];
                AtomicBoolean enabled = new AtomicBoolean(true);
                if (args.length > 5) {
                    enabled.set(Boolean.parseBoolean(args[5]));
                }

                DataService.INSTANCE.writeGlobalData(GlobalRanksConfData.class, writeData -> {
                    writeData.getRanksConf().get(rank).addPermission(permission, enabled.get());
                }, () -> {
                    PermissionsService.INSTANCE.broadcastUpdateRank(rank);
                    sender.sendMessage(TextComponent.fromLegacyText("§bLa permission \"" + permission + "\" a bien été " + (enabled.get() ? "activé" : "désactivé") + " dans le groupe de permission du Grade \"" + rank.name() + "\" !"));
                });
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission perm setplayer <player> <key> <perm> [enabled] !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void displayPermissionGroup(CommandSender sender, PermissionsGroup group, int page, int pageItemsAmount) {
        page = Math.min(Math.max(0, page), group.getPermissions().size() / pageItemsAmount);
        int currentIndex = page * pageItemsAmount;
        int nextIndex = Math.min(group.getPermissions().size(), (page + 1) * pageItemsAmount);

        String[] permissions = group.getPermissions().keySet().toArray(new String[0]);
        sender.sendMessage(TextComponent.fromLegacyText("§7 <------- Page " + (page + 1) + "/" + (permissions.length / pageItemsAmount + 1) + " ------->"));
        for (; currentIndex < nextIndex; currentIndex++) {
            String permission = permissions[currentIndex];
            boolean enabled = group.getPermissions().get(permission);
            sender.sendMessage(TextComponent.fromLegacyText("§8 " + (currentIndex + 1) + " - §7" + permission + " : " + (enabled ? "§a✔" : "§c✖")));
        }
    }

    private void handleShowPlayer(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.showplayer")) {
            if (args.length > 1) {
                Optional<UUID> uuid = Utils.getUUIDFromName(args[1]);

                if (uuid.isPresent()) {
                    if (args.length > 2) {
                        String key = args[2];

                        int page = 0;
                        if (args.length > 3) {
                            try {
                                page = Integer.parseInt(args[3]) - 1;
                            } catch (NumberFormatException e) {
                                sender.sendMessage(TextComponent.fromLegacyText("§cPage invalide !"));
                                return;
                            }
                        }

                        PlayerPermissionsData data = DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.get().toString());
                        if (!data.getPermissionsGroups().containsKey(key)) {
                            sender.sendMessage(TextComponent.fromLegacyText("§cGroupe de permissions inconnus !"));
                            return;
                        }

                        PermissionsGroup group = data.getPermissionsGroups().get(key);
                        this.displayPermissionGroup(sender, group, page, 5);
                    } else {
                        PlayerPermissionsData data = DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.get().toString());

                        sender.sendMessage(TextComponent.fromLegacyText("§8Groupes de permissions :"));
                        data.getPermissionsGroups().keySet().forEach(key -> {
                            sender.sendMessage(TextComponent.fromLegacyText("§7 - " + key + " §8(/permission showplayer " + args[1] + " " + key + ")"));
                        });
                    }
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cJoueur inconnue !"));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission showplayer <player> [key] [page] !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void handleShowRank(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.showrank")) {
            if (args.length > 1) {
                Ranks rank;
                try {
                    rank = Ranks.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cGrade inconnu !"));
                    return;
                }

                int page = 0;
                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2]) - 1;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cPage invalide !"));
                        return;
                    }
                }

                GlobalRanksConfData data = DataService.INSTANCE.getGlobalData(GlobalRanksConfData.class);
                if (data.getRanksConf().containsKey(rank)) { // allez vous faire voir ceux où cette condition renvoie faux
                    this.displayPermissionGroup(sender, data.getRanksConf().get(rank), page, 5);
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("ntm"));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission showrank <rank> [page] !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }

    private void handlePermTest(CommandSender sender, String[] args) {
        if (sender.hasPermission("lyracore.commands.permission.test")) {
            if (args.length > 2) {
                Optional<UUID> uuid = Utils.getUUIDFromName(args[1]);

                if (uuid.isPresent()) {
                    String perm = args[2];

                    PlayerPermissionsData data = DataService.INSTANCE.getData(PlayerPermissionsData.class, uuid.get().toString());
                    sender.sendMessage(TextComponent.fromLegacyText("§7Résultat du test : " + (data.hasPermission(perm) ? "§a✔" : "§c✖")));
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§cJoueur inconnue !"));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText("§cMauvais usage : /permission showplayer <player> [key] [page] !"));
            }
        } else {
            sender.sendMessage(Const.NOT_ENOUGH_PERMISSION_MESSAGE.getMessage());
        }
    }
}
