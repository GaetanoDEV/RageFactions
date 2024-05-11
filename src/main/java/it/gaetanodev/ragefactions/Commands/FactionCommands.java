package it.gaetanodev.ragefactions.Commands;

import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCommands implements CommandExecutor {
    private FactionManager factionManager;
    public FactionCommands(FactionManager factionManager) {
        this.factionManager = factionManager;

    }

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
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-created")));
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
                break;
                // ALTRI COMANDI
        }
        return  true;
    }

}
