package it.gaetanodev.ragefactions.Commands;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FactionCommands implements CommandExecutor, TabCompleter {
    public Map<String, Faction> factions = new HashMap<>();
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

// Comando disband
            case "disband":
                String factionNameDisband = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameDisband != null) {
                    Faction faction = factionManager.factions.get(factionNameDisband);
                    if (faction != null && faction.getLeader().equals(player)) {
                        factionManager.factions.remove(factionNameDisband);
                        // Rimuovi tutti i membri della fazione dalla mappa
                        for (OfflinePlayer member : faction.getMembers()) {
                            factionManager.playerFactions.remove(member.getUniqueId().toString());
                        }
                        // Rimuovi la fazione dal factions.yml
                        RageFactions.instance.factionsConfig.set("Factions." + factionNameDisband, null);
                        RageFactions.instance.saveFactions();
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("the-faction") + " " + factionNameDisband + " " + RageFactions.messages.getMessage("faction-broadcast-disband") + " " + faction.getLeaderName()));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-disbanded")));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                }
                break;

            // Comando list
            case "list":
                ConfigurationSection factionsSection = RageFactions.instance.factionsConfig.getConfigurationSection("Factions");
                if (factionsSection == null || factionsSection.getKeys(false).isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-none")));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-list")));
                    for (String factionNameList : factionsSection.getKeys(false)) {
                        player.sendMessage(ChatColor.AQUA + "- " + factionNameList);
                    }
                }
                break;

// Comando kick
            case "kick":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-kicknospecific")));
                    return true;
                }
                String factionNameKick = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameKick != null) {
                    Faction faction = factionManager.factions.get(factionNameKick);
                    if (faction != null && faction.getLeader().equals(player)) {
                        String memberName = args[1];
                        OfflinePlayer memberToKick = Bukkit.getOfflinePlayer(memberName);
                        // Verifica se il giocatore è un membro di una fazione
                        List<String> memberUUIDs = RageFactions.instance.factionsConfig.getStringList("Factions." + factionNameKick + ".Members");
                        if (memberUUIDs.contains(memberToKick.getUniqueId().toString())) {
                            faction.getMembers().remove(memberToKick);
                            factionManager.playerFactions.remove(memberToKick.getUniqueId().toString());
                            // Rimuovi l'UUID del giocatore dal factions.yml
                            memberUUIDs.remove(memberToKick.getUniqueId().toString());
                            RageFactions.instance.factionsConfig.set("Factions." + factionNameKick + ".Members", memberUUIDs);
                            RageFactions.instance.saveFactions();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-kicked")));
                            if (memberToKick.isOnline()) {
                                ((Player) memberToKick).sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-kickedmember") + " " + factionNameKick));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                }
                break;

                // Comando reload
            case "reload":
                if (player.hasPermission("ragefactions.admin")) {
                    // Ricarica i messaggi
                    RageFactions.messages.reloadMessages();
                    // Ricarica il config.yml
                    RageFactions.instance.reloadConfig();
                    // Ricarica le fazioni
                    RageFactions.instance.reloadFactions();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-reloaded")));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("no-permission")));
                }
                break;

            // Comando leave
            case "leave":
                String factionNameLeave = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameLeave != null) {
                    Faction faction = factionManager.factions.get(factionNameLeave);
                    if (faction != null) {
                        if (faction.getLeader().equals(player)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-leadercannotleave")));
                        } else {
                            faction.getMembers().remove(player);
                            factionManager.playerFactions.remove(player.getUniqueId().toString());
                            // Rimuovi l'UUID del giocatore dal factions.yml
                            List<String> memberUUIDs = RageFactions.instance.factionsConfig.getStringList("Factions." + factionNameLeave + ".Members");
                            memberUUIDs.remove(player.getUniqueId().toString());
                            RageFactions.instance.factionsConfig.set("Factions." + factionNameLeave + ".Members", memberUUIDs);
                            RageFactions.instance.saveFactions();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-left")));
                            RageFactions.instance.reloadFactions();
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;


            // ALTRI COMANDI
}
        return true;
    }

    // TAB-COMPLETER
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "join", "disband", "list", "kick", "leave", "reload")
                    .stream()
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
