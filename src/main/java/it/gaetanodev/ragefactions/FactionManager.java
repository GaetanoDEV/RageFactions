package it.gaetanodev.ragefactions;

import org.bukkit.Bukkit;
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
    public Map<UUID, Boolean> factionChatMode = new HashMap<>();

    public void createFaction(String name, Player leader) {
        if (factions.containsKey(name)) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyexist")));
            return;
        }
        // Verifica se il leader è già in una fazione
        if (playerFactions.containsKey(leader.getUniqueId().toString())) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyin")));
            return;
        }
        Faction newFaction = new Faction(name, leader);
        newFaction.setLeader(leader);
        factions.put(name, newFaction);
        playerFactions.put(leader.getUniqueId().toString(), name);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("the-faction") + " " + name + " " + RageFactions.messages.getMessage("faction-broadcast") + " " + leader.getName()));
        leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction") + " " + name + " " + RageFactions.messages.getMessage("faction-created")));
        RageFactions.instance.saveFaction(newFaction);
        RageFactions.instance.reloadFactions();

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
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-joined") + " " + factionName));
        // Salva la fazione nel factions.yml
        RageFactions.instance.saveFaction(faction);
        RageFactions.instance.reloadFactions();

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
