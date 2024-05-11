package it.gaetanodev.ragefactions;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

public class Faction {
    private String name;
    private Player leader;
    private Set<Player> members;


    public Faction(String name, Player leader) {
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
    }

    public Faction(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public void addMember(Player player) {
        members.add(player);
    }
    public String getName() {
        return name;
    }
    public String getLeaderName() {
        return leader != null ? leader.getName() : null;
    }
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }
    public void setLeader(Player leader) {
        this.leader = leader;
        if (this.members == null) {
            this.members = new HashSet<>();
        }
        if (!this.members.contains(leader)) {
            this.members.add(leader);
        }
    }
    public Player getLeader() {
        return leader;
    }


}
