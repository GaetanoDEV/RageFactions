package it.gaetanodev.ragefactions.Placeholders;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


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

////////////////////////////////
//                            //
//        PLACEHOLDERS        //
//         LEADERBOARD        //
//                            //
////////////////////////////////


        String rankColor = RageFactions.instance.getConfig().getString("top-rankcolor");
        String nameColor = RageFactions.instance.getConfig().getString("top-namecolor");
        String afterNameColor = RageFactions.instance.getConfig().getString("top-aftername-color");
        String powerColor = RageFactions.instance.getConfig().getString("top-powercolor");
        String powerSymbol = RageFactions.instance.getConfig().getString("top-powersymbol");

        // Gestione dei segnaposto per la top delle fazioni per power
        if (identifier.startsWith("powertop")) {
            List<Faction> sortedFactions = new ArrayList<>(factionManager.getFactions().values());
            sortedFactions.sort((f1, f2) -> Integer.compare(f2.getPower(), f1.getPower()));

            int rank;
            try {
                rank = Integer.parseInt(identifier.substring(8));
            } catch (NumberFormatException e) {
                return "-";
            }

            if (rank > 0 && rank <= sortedFactions.size()) {
                Faction faction = sortedFactions.get(rank - 1);
                return ChatColor.translateAlternateColorCodes('&', rankColor + rank + ". " +
                        nameColor + faction.getName() + " " +
                        afterNameColor + "- " + " " +
                        powerColor + faction.getPower() + " " +
                        powerSymbol);
            } else {
                return "-";
            }
        }


////////////////////////////////
//                            //
//        PLACEHOLDERS        //
//          GENERALI          //
//                            //
////////////////////////////////

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
