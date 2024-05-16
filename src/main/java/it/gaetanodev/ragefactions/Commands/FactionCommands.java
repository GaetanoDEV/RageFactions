package it.gaetanodev.ragefactions.Commands;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FactionCommands implements CommandExecutor, TabCompleter {
    public Map<String, Faction> factions = new HashMap<>(); // Mappa per i dati dei Faction
    public Map<UUID, Boolean> factionChatMode = new HashMap<>(); // Mappa per la chat del Faction
    private final FactionManager factionManager;

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
                if (args.length < 3) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-createnospecific")));
                    return true;
                }
                String factionName = args[1];
                String tag = args[2];
                if (tag.length() > 4) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-create-taglenght")));
                    return true;
                }
                factionManager.createFaction(factionName, tag, player);
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
                        RageFactions.instance.saveFaction(faction);
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
                            RageFactions.instance.saveFaction(faction);
                            RageFactions.instance.reloadFactions();
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

            // Comando sethome
            case "sethome":
                String factionNameHome = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameHome != null) {
                    Faction faction = factionManager.factions.get(factionNameHome);
                    if (faction != null && faction.getLeader().equals(player)) {
                        // Ottieni la posizione attuale del giocatore
                        Location homeLocation = player.getLocation();
                        // Salva la posizione nel file factions.yml
                        RageFactions.instance.factionsConfig.set("Factions." + factionNameHome + ".Home.World", homeLocation.getWorld().getName());
                        RageFactions.instance.factionsConfig.set("Factions." + factionNameHome + ".Home.X", homeLocation.getX());
                        RageFactions.instance.factionsConfig.set("Factions." + factionNameHome + ".Home.Y", homeLocation.getY());
                        RageFactions.instance.factionsConfig.set("Factions." + factionNameHome + ".Home.Z", homeLocation.getZ());
                        RageFactions.instance.saveFactions();
                        RageFactions.instance.reloadFactions();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-home-set")));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                }
                break;
            // Comando home
            case "home":
                String factionNameHomeTP = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameHomeTP != null) {
                    Faction faction = factionManager.factions.get(factionNameHomeTP);
                    if (faction != null) {
                        Location homeLocation = faction.getHome();
                        if (homeLocation != null) {
                            // Ottieni il ritardo dal file di configurazione
                            int delayInSeconds = RageFactions.instance.getConfig().getInt("homeTeleportDelay");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-home-teleporting").replace("%s", String.valueOf(delayInSeconds))));
                            Bukkit.getScheduler().scheduleSyncDelayedTask(RageFactions.instance, new Runnable() {
                                @Override
                                public void run() {
                                    player.teleportAsync(homeLocation);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-home-teleport")));
                                }
                            }, delayInSeconds * 20L); // 20 ticks = 1 secondo

                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-home-notset")));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
                // Comando chat
            case "chat":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-chat-specific")));
                    return true;
                }
                UUID playerId = player.getUniqueId();
                String factionNameChat = factionManager.playerFactions.get(playerId.toString());
                if (factionNameChat != null) {
                    Faction faction = factionManager.factions.get(factionNameChat);
                    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    String prefix = ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-chat-prefix"));
                    for (OfflinePlayer member : faction.getMembers()) {
                        if (member.isOnline()) {
                            Player onlineMember = member.getPlayer();
                            if (onlineMember != null) {
                                onlineMember.sendMessage(prefix + " " + player.getName() + ": " + message);
                            }
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
                // Comando tag
            case "tag":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usa /f tag <nuovoTag>");
                    return true;
                }
                String newTag = args[1];
                if (newTag.length() > 4) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-create-taglenght")));
                    return true;
                }
                String factionNameTAG = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameTAG != null) {
                    Faction faction = factionManager.factions.get(factionNameTAG);
                    if (faction != null && faction.getLeader().equals(player)) {
                        faction.setTag(newTag);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-tag-changed").replace("%s", newTag)));
                        RageFactions.instance.saveFaction(faction);
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
                // Comando admin
            case "admin":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usa /f admin <giocatore>");
                    return true;
                }
                String newLeaderName = args[1];
                OfflinePlayer newLeader = Bukkit.getOfflinePlayer(newLeaderName);
                if (newLeader == null || !newLeader.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-notfound") + " " + newLeaderName));
                    return true;
                }
                String factionNameNewLeader = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameNewLeader != null) {
                    Faction faction = factionManager.factions.get(factionNameNewLeader);
                    if (faction != null && faction.getLeader().equals(player)) {
                        if (faction.getMembers().contains(newLeader)) { // Verifica se il nuovo leader è un membro della fazione
                            faction.setLeader(newLeader); // Imposta il nuovo leader
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-leader-changed").replace("%s", newLeaderName)));
                            RageFactions.instance.saveFaction(faction); // Salva le modifiche nel file factions.yml
                        } else {
                            player.sendMessage(ChatColor.RED + newLeaderName + " " + ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-notmember")));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
                // Comando open
            case "open":
                String factionNameOpen = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameOpen != null) {
                    Faction faction = factionManager.factions.get(factionNameOpen);
                    if (faction != null && faction.getLeader().equals(player)) {
                        faction.setPublic(!faction.isPublic()); // Alterna il valore di isPublic
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-open-status-changed").replace("%s", faction.isPublic() ? ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-open")) : ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-closed")))));
                        RageFactions.instance.saveFaction(faction); // Salva le modifiche nel file factions.yml
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
                // Comando invite
            case "invite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usa /f invite <giocatore>");
                    return true;
                }
                String inviteeName = args[1];
                OfflinePlayer invitee = Bukkit.getOfflinePlayer(inviteeName);
                if (invitee == null || !invitee.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-notfound") + " " + inviteeName));
                    return true;
                }
                String factionNameInvite = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameInvite != null) {
                    Faction faction = factionManager.factions.get(factionNameInvite);
                    if (faction != null && faction.getLeader().equals(player)) {
                        faction.invitePlayer(invitee); // Invita il giocatore
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-invited").replace("%s", inviteeName)));
                        if (invitee.isOnline()) { // Verifica se il giocatore invitato è online
                            Player inviteePlayer = invitee.getPlayer();
                            inviteePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-invited").replace("%s", faction.getName())));
                            inviteePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-invite-tip").replace("%s", faction.getName())));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
            case "uninvite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usa /f uninvite <giocatore>");
                    return true;
                }
                String uninviteeName = args[1];
                OfflinePlayer uninvitee = Bukkit.getOfflinePlayer(uninviteeName);
                if (uninvitee == null || !uninvitee.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-notfound") + " " + uninviteeName));
                    return true;
                }
                String factionNameUninvite = factionManager.playerFactions.get(player.getUniqueId().toString());
                if (factionNameUninvite != null) {
                    Faction faction = factionManager.factions.get(factionNameUninvite);
                    if (faction != null && faction.getLeader().equals(player)) {
                        faction.revokeInvite(uninvitee); // Rimuovi l'invito al giocatore
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-uninvited").replace("%s", uninviteeName)));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
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
            return Arrays.asList("create", "join", "disband", "list", "kick", "leave", "home", "sethome", "chat", "tag", "admin", "open", "invite", "uninvite", "reload")
                    .stream()
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
