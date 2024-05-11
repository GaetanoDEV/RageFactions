package it.gaetanodev.ragefactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FactionManager {
    public Map<String, Faction> factions = new HashMap<>();
    public void createFaction(String name, Player leader) {
        if (factions.containsKey(name)) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyexist")));
            return;
        }
        Faction newFaction = new Faction(name, leader);
        factions.put(name, newFaction);
    }
    public void joinFaction(Player player, String factionName) {
        Faction faction = factions.get(factionName);
        if (faction == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
            return;
        }
        faction.addMember(player);
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
