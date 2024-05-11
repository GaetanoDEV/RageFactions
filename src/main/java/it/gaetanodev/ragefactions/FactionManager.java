package it.gaetanodev.ragefactions;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FactionManager {
    public Map<String, Faction> factions = new HashMap<>();
    public Map<String, String> playerFactions = new HashMap<>();
    public void createFaction(String name, Player leader) {
        if (factions.containsKey(name)) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyexist")));
            return;
        }
        for (Faction faction : factions.values()) {
            if (faction.getLeader() != null && faction.getLeader().getName().equals(leader.getName())) {
                leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyleader")));
                return;
            }
        }
        if (playerFactions.containsKey(leader.getName())) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyin")));
            return;
        }
        Faction newFaction = new Faction(name, leader);
        newFaction.setLeader(leader);
        factions.put(name, newFaction);
        playerFactions.put(leader.getName(), name);
        leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-created")));
    }

    public void joinFaction(Player player, String factionName) {
        Faction faction = factions.get(factionName);
        if (faction == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
            return;
        }
        if (playerFactions.containsKey(player.getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyin")));
            return;
        }
        faction.addMember(player);
        playerFactions.put(player.getName(), factionName);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-joined")));
    }
    // ALTRI METODI

    // METODI GET
    public String getFactionName(String factionName) {
        if (factions.containsKey(factionName)) {
            return factions.get(factionName).getName();
        }
        return null;
    }
    public Set<OfflinePlayer> getFactionMembers(String factionName) {
        if (factions.containsKey(factionName)) {
            return factions.get(factionName).getMembers();
        }
        return null;
    }
    public UUID getFactionLeader(String factionName) {
        if (factions.containsKey(factionName)) {
            return factions.get(factionName).getLeaderUUID();
        }
        return null;
    }

}
