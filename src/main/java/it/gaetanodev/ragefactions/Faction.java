////////////////////////////////
//                            //
//       CLASSE FAZIONE       //
//      SALVATAGGIO DATI      //
//                            //
////////////////////////////////


package it.gaetanodev.ragefactions;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Faction {
    public Map<UUID, Rank> ranks = new HashMap<>();
    private String name;
    private OfflinePlayer leader;
    private Set<OfflinePlayer> members;
    private Location home;
    private String tag;
    private boolean isPublic;
    private Set<UUID> invites;
    private Set<String> allies = new HashSet<>();
    private double bank;
    private int power;

    public Faction(String name, String tag, OfflinePlayer leader) {
        // Costruttore che inizializza una nuova Fazione con nome, tag e leader forniti
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
        this.tag = tag;
        this.invites = new HashSet<>();
        setRank(leader, Rank.LEADER);
    }

    // Aggiunge un membro alla fazione e imposta il suo rank a "Membro"
    public void addMember(OfflinePlayer player) {
        members.add(player);
        setRank(player, Rank.MEMBRO);
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

    // Restituisce la posizione della base della fazione
    public Location getHome() {
        return home;
    }

    // Imposta la posizione della base della fazione
    public void setHome(Location home) {
        this.home = home;
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

    // Imposta il rank ad un giocatore
    public void setRank(OfflinePlayer player, Rank rank) {
        ranks.put(player.getUniqueId(), rank);
    }

    // Restituisce il rank di un giocatore
    public Rank getRank(OfflinePlayer player) {
        return ranks.get(player.getUniqueId());
    }

    // Restituisce i fazioni alleati
    public Set<String> getAllies() {
        return allies;
    }

    // Aggiunge un alleato alla fazione
    public void addAlly(String ally) {
        this.allies.add(ally);
    }

    // Rimuove un alleato dalla fazione
    public void removeAlly(String ally) {
        this.allies.remove(ally);
    }

    public void promoteMember(OfflinePlayer member) {
        Rank currentRank = getRank(member);
        Rank[] ranks = Rank.values();
        int nextRankIndex = (currentRank.ordinal() + 1) % ranks.length;
        setRank(member, ranks[nextRankIndex]);
    }
    // Diminuisce il rank di un giocatore
    public void demoteMember(OfflinePlayer member) {
        Rank currentRank = getRank(member);
        if (currentRank == Rank.MEMBRO) {
            return;
        }
        Rank[] ranksDemote = Rank.values();
        int previousRankIndex = (currentRank.ordinal() - 1) % ranksDemote.length;
        setRank(member, ranksDemote[previousRankIndex]);
    }
    // Restituisce l'ammontare della banca
    public double getBank() {
        return bank;
    }
    // Imposta il valore della banca
    public void setBank(double bank) {
        this.bank = bank;
    }
    // Aggiunge l'ammontare al valore della banca
    public void deposit(double amount) {
        this.bank += amount;
    }
    // Restituisce il power della Fazione
    public int getPower() {
        return power;
    }
    // Imposta il power della fazione
    public void setPower(int power) {
        this.power = power;
    }
    // Aumenta il power della fazione di 1
    public void increasePower() {
        this.power++;
    }
    // Diminuisce il power della Fazione di 1
    public void decreasePower() {
        if (this.power > 0) {
            this.power--;
        }
    }
}
