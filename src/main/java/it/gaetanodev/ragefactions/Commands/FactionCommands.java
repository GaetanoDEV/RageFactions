package it.gaetanodev.ragefactions.Commands;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FactionCommands implements CommandExecutor {
    private FactionManager factionManager;
    public FactionCommands(FactionManager factionManager) {
        this.factionManager = factionManager;

    }
    public Map<String, Faction> factions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("player-command")));
            return true;
        }
        // Definisci player
        Player player = (Player) sender;

        if (args.length == 0) {
            // TODO: LISTA HELP
            return true;
        }
        switch (args[0].toLowerCase()) {
            // Comando create
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-createnospecific")));
                    return true;
                }
                String factionName = args[1];
                factionManager.createFaction(factionName, player);
                RageFactions.instance.saveFactions();
                break;


            // Comando join
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-namespecific")));
                    return true;
                }
                String factionNameJoin = args[1];
                factionManager.joinFaction(player, factionNameJoin);
                RageFactions.instance.saveFactions();
                break;
                // ALTRI COMANDI
        }
        return  true;
    }

}
