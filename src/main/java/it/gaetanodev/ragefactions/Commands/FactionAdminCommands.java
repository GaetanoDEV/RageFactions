package it.gaetanodev.ragefactions.Commands;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FactionAdminCommands implements CommandExecutor, TabCompleter {
    private final FactionManager factionManager;

    public FactionAdminCommands(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Definisci player
        Player player = (Player) sender;

        if (player.hasPermission("ragefactions.admin")) {
            if (args.length == 0) {
                player.sendMessage(" ");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("adminhelp")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("adminhelp-reload")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("adminhelp-setpower")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("adminhelp-setbank")));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("adminhelp-gethome")));
                player.sendMessage(" ");

                return true;
            }
            switch (args[0].toLowerCase()) {
                // Comando delete
                case "delete":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-namespecific")));
                        return true;
                    }
                    String factionToDelete = args[1];
                    Faction faction = factionManager.factions.get(factionToDelete);

                    if (faction == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                        return true;
                    }

                    // Sciogli tutte le alleanze
                    for (String ally : faction.getAllies()) {
                        Faction allyFaction = factionManager.factions.get(ally);
                        if (allyFaction != null) {
                            allyFaction.removeAlly(factionToDelete);
                            RageFactions.instance.saveFaction(allyFaction);
                        }
                    }

                    // Rimuovi la fazione e tutti i membri associati
                    factionManager.factions.remove(factionToDelete);

                    // Rimuovi tutti i membri della fazione dalla mappa
                    for (OfflinePlayer member : faction.getMembers()) {
                        factionManager.playerFactions.remove(member.getUniqueId().toString());
                    }

                    // Rimuovi la fazione dal factions.yml
                    RageFactions.instance.factionsConfig.set("Factions." + factionToDelete, null);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-deleted").replace("%s", factionToDelete)));
                    RageFactions.instance.saveFactions();
                    RageFactions.instance.reloadFactions();
                    break;
                // Comando setpower
                case "setpower":
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-namespecific")));
                        return true;
                    }
                    String factionToEditPower = args[1];
                    String newPower = args[2];
                    Faction factionPower = factionManager.factions.get(factionToEditPower);

                    if (factionPower == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                        return true;
                    }

                    try {
                        int powerValue = Integer.parseInt(newPower);
                        factionPower.setPowerFaction(factionToEditPower, powerValue);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-newpower").replace("%s", factionToEditPower).replace("%p", newPower)));
                        RageFactions.instance.saveFaction(factionPower);
                        RageFactions.instance.reloadFactions();
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-number")));
                    }
                    break;
                // Comando setbank
                case "setbank":
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-namespecific")));
                        return true;
                    }
                    String factionToEditBank = args[1];
                    String newBank = args[2];
                    Faction factionBank = factionManager.factions.get(factionToEditBank);

                    if (factionBank == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                        return true;
                    }

                    try {
                        double bankValue = Double.valueOf(newBank);
                        factionBank.setBankFaction(factionToEditBank, bankValue);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-newbank").replace("%s", factionToEditBank).replace("%b", newBank)));
                        RageFactions.instance.saveFaction(factionBank);
                        RageFactions.instance.reloadFactions();
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-number")));
                    }
                    break;
                // Comando gethome
                case "gethome":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-namespecific")));
                        return true;
                    }
                    String factionToGetHome = args[1];
                    Faction getHome = factionManager.factions.get(factionToGetHome);

                    if (getHome == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                        return true;
                    }

                    if (getHome.getHomeFaction(factionToGetHome) == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-home-notset")));
                        return true;
                    }

                    String world = getHome.getHomeFaction(factionToGetHome).getWorld().getName();
                    double x = getHome.getHomeFaction(factionToGetHome).getX();
                    double y = getHome.getHomeFaction(factionToGetHome).getY();
                    double z = getHome.getHomeFaction(factionToGetHome).getZ();

                    String homeWorld = String.valueOf(world);
                    String homeX = String.valueOf(x);
                    String homeY = String.valueOf(y);
                    String homeZ = String.valueOf(z);

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-gethome").replace("%s", factionToGetHome)));
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-gethome-world") + " " + homeWorld));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-gethome-x") + " " + homeX));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-gethome-y") + " " + homeY));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-admin-gethome-z") + " " + homeZ));

                    break;
                // Comando reload
                case "reload":
                    RageFactions.instance.saveFactions();
                    RageFactions.instance.reloadFactions();
                    RageFactions.instance.reloadConfig();
                    RageFactions.messages.reloadMessages();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-reloaded")));
                    break;
                // ALTRI COMANDI
            }
        } else
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("no-permission")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("delete", "setpower", "setbank", "gethome", "reload")
                    .stream()
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
