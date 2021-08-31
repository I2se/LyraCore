package fr.lyrania.core.services.party;

import fr.lyrania.common.data.global.GlobalPartyData;
import fr.lyrania.common.data.global.PartyData;
import fr.lyrania.common.data.player.PlayerPartyData;
import fr.lyrania.core.Core;
import fr.lyrania.core.database.DataService;
import fr.lyrania.core.services.nick.NickService;
import fr.lyrania.core.services.servers.data.GlobalServerData;
import fr.lyrania.core.services.servers.data.ServerData;
import fr.lyrania.core.services.servers.enums.Servers;
import fr.lyrania.core.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PartyService implements Listener {

    public static final PartyService INSTANCE = new PartyService();

    public void createParty(UUID owner, UUID player) {
        String serverID = RandomStringUtils.randomAlphanumeric(4);
        DataService.INSTANCE.writeGlobalData(GlobalPartyData.class, globalPartyData -> {
            PartyData party = new PartyData();
            List<UUID> playerList = new ArrayList<>();
            playerList.add(player);
            HashMap<UUID, Boolean> followList = new HashMap<>();
            followList.put(player, false);
            List<UUID> inviteList = new ArrayList<>();

            party.setPartyID(serverID);
            party.setOwner(owner);
            party.setPlayerList(playerList);
            party.setFollows(followList);
            party.setInviteParty(inviteList);

            setParty(owner, serverID);
            setParty(player, serverID);

            globalPartyData.getParties().put(serverID, party);
        });
    }

    public void deleteParty(String partyID) {
        DataService.INSTANCE.writeGlobalData(GlobalPartyData.class, globalPartyData -> {
            globalPartyData.getParties().remove(partyID);
        });
    }

    public PartyData getParty(UUID uuid) {
        PlayerPartyData playerPartyData = DataService.INSTANCE.getData(PlayerPartyData.class, uuid.toString());
        String partyID = playerPartyData.getPartyId();
        GlobalPartyData globalData = DataService.INSTANCE.getGlobalData(GlobalPartyData.class);
        return globalData.getParties().get(partyID);
    }

    public void setParty(UUID uuid, String partyID) {
        DataService.INSTANCE.writeData(PlayerPartyData.class, uuid.toString(), playerPartyData -> {
            playerPartyData.setPartyId(partyID);
        });
    }

    public void leaveParty(UUID uuid) {
        DataService.INSTANCE.writeData(PlayerPartyData.class, uuid.toString(), playerPartyData -> {
            playerPartyData.setPartyId(" ");
        });
    }

    public void addPlayerToParty(UUID uuid, String partyID) {
        DataService.INSTANCE.writeGlobalData(GlobalPartyData.class, globalPartyData -> {
            globalPartyData.getParties().get(partyID).addPlayerParty(uuid);
        });
    }

    public void removePlayerToParty(UUID uuid, String partyID) {
        DataService.INSTANCE.writeGlobalData(GlobalPartyData.class, globalPartyData -> {
            globalPartyData.getParties().get(partyID).removePlayerParty(uuid);
        });
    }

    public void addInvite(UUID uuid, String partyID) {
        DataService.INSTANCE.writeGlobalData(GlobalPartyData.class, globalPartyData -> {
            globalPartyData.getParties().get(partyID).addInviteParty(uuid);
        });
    }

    public void deleteInvite(UUID uuid, String partyID) {
        DataService.INSTANCE.writeGlobalData(GlobalPartyData.class, globalPartyData -> {
            globalPartyData.getParties().get(partyID).removeInviteParty(uuid);
        });
    }

    public String showPlayerList(UUID uuid) {
        ProxiedPlayer player = Core.getInstance().getProxy().getPlayer(uuid);
        String name = "";
        String follow = "";
        String status = "";
        String more = "";
        if(player != null) {
            PartyData partyDataPlayer = PartyService.INSTANCE.getParty(player.getUniqueId());
            name = player.getDisplayName();
            if(partyDataPlayer.getFollows().get(player.getUniqueId())) {
                follow = ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + "Suivis" + ChatColor.DARK_GRAY + ") ";
            }
            int port = player.getServer().getAddress().getPort();
            if(port >= 25600) {
                ServerData serverData = DataService.INSTANCE.getGlobalData(GlobalServerData.class).getServers().get(port);
                more = serverData.getFullNameServer();
            } else {
                more = Servers.getByPort(port).getName();
            }
            status = ChatColor.GREEN + "• (" + more + ")";
        } else {
            name = Utils.getNameFromUUID(uuid).get();
            status = ChatColor.RED + "• (Hors-Ligne)";
        }

        return ChatColor.GOLD + name + ChatColor.DARK_GRAY + " | " + follow + status;
    }
}
