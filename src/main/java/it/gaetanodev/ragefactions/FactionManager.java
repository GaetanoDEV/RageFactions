package it.gaetanodev.ragefactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FactionManager {
    public Map<String, Faction> factions = new HashMap<>();
    public Map<String, String> playerFactions = new HashMap<>();
    public void createFaction(String name, Player leader) {
        if (factions.containsKey(name)) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyexist")));
            return;
        }
        for (Faction faction : factions.values()) {
            if (faction.getLeaderName().equals(leader.getName())) {
                leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyleader")));
                return;
            }
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
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-joined" + factionName + ".")));
    }
    // ALTRI METODI

    // METODI GET
    public String getFactionName(String factionName) {
        if (factions.containsKey(factionName)) {
            return factions.get(factionName).getName();
        }
        return null;
    }
    public Set<Player> getFactionMembers(String factionName) {
        if (factions.containsKey(factionName)) {
            return factions.get(factionName).getMembers();
        }
        return null;
    }
    public String getFactionLeader(String factionName) {
        if (factions.containsKey(factionName)) {
            return factions.get(factionName).getLeaderName();
        }
        return null;
    }

}
