////////////////////////////////
//                            //
//   CLASSE GESTORE FAZIONI   //
//         METODI ETC         //
//                            //
////////////////////////////////

package it.gaetanodev.ragefactions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionManager {
    public Map<String, Faction> factions = new HashMap<>();
    public Map<String, String> playerFactions = new HashMap<>();
    public Map<String, Set<String>> allianceInvites = new HashMap<>();

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
        String broadcastMessage = (ChatColor.translateAlternateColorCodes('&', ChatColor.AQUA + leader.getName() + " " + RageFactions.messages.getMessage("faction-broadcast") + " " + name));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.getUniqueId().equals(leader.getUniqueId())) {
                onlinePlayer.sendMessage(broadcastMessage);
            }
        }
        leader.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.AQUA + leader.getName() + " " + RageFactions.messages.getMessage("faction-broadcast") + " " + name));
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

    // Metodo per accettare un'alleanza tra due fazioni
    public void acceptAlliance(String factionName1, String factionName2) {
        Faction faction1 = factions.get(factionName1);
        Faction faction2 = factions.get(factionName2);
        if (faction1 == null || faction2 == null) {
            return;
        }

        // Rimuovi l'invito dall'elenco degli inviti pendenti
        Set<String> invites = allianceInvites.get(factionName2);
        if (invites != null && invites.contains(factionName1)) {
            // Crea l'alleanza
            faction1.addAlly(factionName2);
            faction2.addAlly(factionName1);
            invites.remove(factionName1);

            // Salva le fazioni
            RageFactions.instance.saveFaction(faction1);
            RageFactions.instance.saveFaction(faction2);

            // Invia un messaggio di conferma ai leader di entrambe le fazioni
            Player leader1 = faction1.getLeader().getPlayer();
            if (leader1 != null && leader1.isOnline()) {
                leader1.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyacceptedally").replace("%s", factionName2)));
            }
            Player leader2 = faction2.getLeader().getPlayer();
            if (leader2 != null && leader2.isOnline()) {
                leader2.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyaccepted").replace("%s", factionName1)));
            }
        }
    }

    // Metodo per rimuovere un'alleanza tra due fazioni
    public void removeAlliance(String factionName1, String factionName2) {
        Faction faction1 = factions.get(factionName1);
        if (faction1 == null) {
            return;
        }
        Faction faction2 = factions.get(factionName2);
        if (faction1 == null) {
            return;
        }
        faction1.removeAlly(factionName2);
        faction2.removeAlly(factionName1);
        RageFactions.instance.saveFaction(faction1);
        RageFactions.instance.saveFaction(faction2);

        // Invia un messaggio al leader della fazione 2
        Player leader2 = faction2.getLeader().getPlayer();
        if (leader2 != null && leader2.isOnline()) {
            leader2.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyremovedleader").replace("%s", factionName1)));
        }
    }
    // Metodo per creare un invito per le alleanze
    public void inviteToAlliance(String factionName1, String factionName2) {
        Faction faction1 = factions.get(factionName1);
        Faction faction2 = factions.get(factionName2);
        if (faction1 == null || faction2 == null) {
            return;
        }

        // Aggiungi l'invito alla mappa
        allianceInvites.computeIfAbsent(factionName2, k -> new HashSet<>()).add(factionName1);

        // Invia un messaggio al leader della fazione invitata
        Player leader2 = faction2.getLeader().getPlayer();
        if (leader2 != null && leader2.isOnline()) {
            leader2.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyinvite").replace("%s", factionName1)));
        }
    }


    // Restituisce la fazione di un giocatore
    public Faction getFaction(Player player) {
        String factionName = playerFactions.get(player.getUniqueId().toString());
        return factions.get(factionName);
    }

    // Restiuisce la mappa delle fazioni
    public Map<String, Faction> getFactions() {
        return factions;
    }
}


