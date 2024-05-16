////////////////////////////////
//                            //
//       CLASSE FAZIONE       //
//      SALVATAGGIO DATI      //
//                            //
////////////////////////////////


package it.gaetanodev.ragefactions;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Faction {
    private String name;
    private OfflinePlayer leader;
    private Set<OfflinePlayer> members;
    private Location home;
    private String tag;
    private boolean isPublic;
    private Set<UUID> invites;
    public Faction(String name, String tag, OfflinePlayer leader) {
        // Costruttore che inizializza una nuova Fazione con nome, tag e leader forniti
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
        this.tag = tag;
        this.invites = new HashSet<>();
    }

    public Faction(String name) {
        // Costruttore che inizializza una nuova Fazione con solo il nome fornito
        this.name = name;
        this.members = new HashSet<>();
    }

    // Aggiunge un membro alla fazione
    public void addMember(OfflinePlayer player) {
        members.add(player);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Restituisce il nome del leader della fazione
    public String getLeaderName() {
        return leader != null ? leader.getName() : null;
    }

    // Restituisce l'UUID del leader della fazione
    public UUID getLeaderUUID() {
        return leader != null ? leader.getUniqueId() : null;
    }

    // Restituisce una copia dell'insieme di membri per evitare modifiche dirette
    public Set<OfflinePlayer> getMembers() {
        return new HashSet<>(members);
    }

    public OfflinePlayer getLeader() {
        return leader;
    }

    // Imposta il leader della fazione e aggiunge il leader all'insieme dei membri se non presente
    public void setLeader(OfflinePlayer leader) {
        this.leader = leader;
        if (this.members == null) {
            this.members = new HashSet<>();
        }
        this.members.add(leader);
    }

    // Imposta la posizione della base della fazione
    public void setHome(Location home) {
        this.home = home;
    }

    // Restituisce la posizione della base della fazione
    public Location getHome() {
        return home;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    // Restituisce se la fazione è pubblica
    public boolean isPublic() {
        return isPublic;
    }

    // Imposta se la fazione è pubblica o meno
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    // Invita un giocatore alla fazione
    public void invitePlayer(OfflinePlayer player) {
        this.invites.add(player.getUniqueId());
    }

    // Revoca un invito a un giocatore
    public void revokeInvite(OfflinePlayer player) {
        this.invites.remove(player.getUniqueId());
    }

    // Controlla se un giocatore è stato invitato alla fazione
    public boolean isInvited(OfflinePlayer player) {
        return this.invites.contains(player.getUniqueId());
    }
}