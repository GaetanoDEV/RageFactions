package it.gaetanodev.ragefactions.Placeholders;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholdersManager extends PlaceholderExpansion {
    private final FactionManager factionManager;

    public PlaceholdersManager(RageFactions plugin, FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ragefactions";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Gaethanos__";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        // Ottieni la fazione del giocatore
        Faction faction = factionManager.getFaction(player);
        if (faction == null) {
            return "";
        }

        switch (identifier) {
            case "name":
                return faction.getName();
            case "leader":
                return faction.getLeaderName();
            case "bank":
                return String.valueOf(faction.getBank());
            case "isopen":
                return faction.isPublic() ? "Aperto" : "Solo su invito";
            case "tag":
                return faction.getTag();
            case "power":
                return String.valueOf(faction.getPower());
            default:
                return null;
        }
    }
}
