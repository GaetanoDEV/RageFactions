////////////////////////////////
//                            //
//   CLASSE GESTORE COMANDI   //
//                            //
////////////////////////////////

package it.gaetanodev.ragefactions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class FactionManager {
    public Map<String, Faction> factions = new HashMap<>();
    public Map<String, String> playerFactions = new HashMap<>();

    // Metodo per creare una nuova fazione
    public void createFaction(String name, String tag, Player leader) {
        if (factions.containsKey(name)) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyexist")));
            return;
        }

        // Verifica se il leader è già in una fazione
        if (playerFactions.containsKey(leader.getUniqueId().toString())) {
            leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-alreadyin")));
            return;
        }

        Faction newFaction = new Faction(name, tag, leader);
        newFaction.setLeader(leader);
        factions.put(name, newFaction);
        playerFactions.put(leader.getUniqueId().toString(), name);

        // Invia il messaggio di broadcast a tutti i giocatori online tranne il leader
        String broadcastMessage = ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("the-faction") + " " + name + " " + RageFactions.messages.getMessage("faction-broadcast") + " " + leader.getName());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.getUniqueId().equals(leader.getUniqueId())) {
                onlinePlayer.sendMessage(broadcastMessage);
            }
        }

        leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction") + " " + name + " " + RageFactions.messages.getMessage("faction-created")));
        RageFactions.instance.saveFaction(newFaction);
        RageFactions.instance.reloadFactions();
    }

    // Metodo per far unire un giocatore a una fazione esistente
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

        // Verifica se la fazione è pubblica o se il giocatore è stato invitato
        if (faction.isPublic() || faction.isInvited(player)) {
            faction.addMember(player);
            playerFactions.put(player.getName(), factionName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-joined") + " " + factionName));
            // Salva la fazione nel factions.yml
            RageFactions.instance.saveFaction(faction);
            RageFactions.instance.reloadFactions();

            // Invia un messaggio al leader della fazione
            Player leader = faction.getLeader().getPlayer();
            if (leader != null && leader.isOnline()) {
                leader.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-newmember").replace("%s", player.getName())));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-isclosed")));
        }
    }
    // Restituisce la fazione di un giocatore
    public Faction getFaction(Player player) {
        String factionName = playerFactions.get(player.getUniqueId().toString());
        return factions.get(factionName);
    }
}

