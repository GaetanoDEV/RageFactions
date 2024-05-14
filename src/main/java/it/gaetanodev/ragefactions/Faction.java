package it.gaetanodev.ragefactions;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Faction {
    private final String name;
    private OfflinePlayer leader;
    private Set<OfflinePlayer> members;
    private Location home;

    public Faction(String name, OfflinePlayer leader) {
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
    }

    public Faction(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public void addMember(OfflinePlayer player) {
        members.add(player);
    }

    public String getName() {
        return name;
    }

    public String getLeaderName() {
        return leader != null ? leader.getName() : null;
    }

    public UUID getLeaderUUID() {
        return leader != null ? leader.getUniqueId() : null;
    }

    public Set<OfflinePlayer> getMembers() {
        return new HashSet<>(members);
    }

    public OfflinePlayer getLeader() {
        return leader;
    }

    public void setLeader(OfflinePlayer leader) {
        this.leader = leader;
        if (this.members == null) {
            this.members = new HashSet<>();
        }
        this.members.add(leader);

    }
    public void setHome(Location home) {
        this.home = home;
    }

    public Location getHome() {
        return home;
    }

}