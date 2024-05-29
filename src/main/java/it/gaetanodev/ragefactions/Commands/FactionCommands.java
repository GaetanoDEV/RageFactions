////////////////////////////////
//                            //
//   CLASSE GESTORE COMANDI   //
//                            //
////////////////////////////////


package it.gaetanodev.ragefactions.Commands;

import it.gaetanodev.ragefactions.Faction;
import it.gaetanodev.ragefactions.FactionManager;
import it.gaetanodev.ragefactions.RageFactions;
import it.gaetanodev.ragefactions.Rank;
import net.milkbowl.vault.economy.EconomyResponse;
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

import java.util.*;
import java.util.stream.Collectors;

public class FactionCommands implements CommandExecutor, TabCompleter {
    private final FactionManager factionManager;
    private final RageFactions plugin;

    public FactionCommands(FactionManager factionManager, RageFactions plugin) {
        this.factionManager = factionManager;

        this.plugin = plugin;
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

            String version = plugin.getPluginMeta().getVersion();

            player.sendMessage(" ");
            player.sendMessage(ChatColor.AQUA + "     " + "Rage" + ChatColor.WHITE + "Factions" + " " + ChatColor.GRAY + "(" + ChatColor.WHITE + "v" + version + ChatColor.GRAY + ")");
            player.sendMessage(ChatColor.AQUA + "  " + "developed by" + ChatColor.DARK_AQUA + " Gaethanos__");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("helpcommands")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-list")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-chat")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-sethome")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-info")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-ranks")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-join")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-leave")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-ally-add")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-ally-accept")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-ally-remove")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-ally-chat")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-invite")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-kick")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-uninvite")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-setrank")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-promote")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-demote")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-create")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-disband")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-rename")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-tag")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-open")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-admin")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-deposit")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-bank")));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("help-members")));
            player.sendMessage(" ");
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

                int factionMaxNameLegnght = RageFactions.instance.getConfig().getInt("factionMaxNameLegnght");
                String factionMaxNameLegnghtForReplace = RageFactions.instance.getConfig().getString("factionMaxNameLegnght");

                int factionMaxTagLenght = RageFactions.instance.getConfig().getInt("factionMaxTagLenght");
                String factionMaxTagLenghtForReplace = RageFactions.instance.getConfig().getString("factionMaxTagLenght");

                if (tag.length() > factionMaxTagLenght) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-create-taglenght").replace("%s", factionMaxTagLenghtForReplace)));
                    return true;
                }
                if (factionName.length() > factionMaxNameLegnght) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-createnameleght").replace("%s", factionMaxNameLegnghtForReplace)));
                    return true;
                }

                // Richiama il metodo per creare una nuova fazione
                factionManager.createFaction(factionName, tag, player);

                // Salva le modifiche nel file delle fazioni
                RageFactions.instance.saveFactions();
                break;
            // Comando join
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-namespecific")));
                    return true;
                }

                String factionNameJoin = args[1];

                // Richiama il metodo per unirsi a una fazione
                factionManager.joinFaction(player, factionNameJoin);

                // Salva le modifiche nel file delle fazioni
                RageFactions.instance.saveFactions();
                break;
            // Comando disband
            case "disband":
                String factionNameDisband = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameDisband != null) {
                    Faction faction = factionManager.factions.get(factionNameDisband);

                    if (faction != null && faction.getLeader().equals(player)) {
                        // Sciogli tutte le alleanze
                        for (String ally : faction.getAllies()) {
                            Faction allyFaction = factionManager.factions.get(ally);
                            if (allyFaction != null) {
                                allyFaction.removeAlly(factionNameDisband);
                                RageFactions.instance.saveFaction(allyFaction);
                            }
                        }

                        // Rimuovi la fazione e tutti i membri associati
                        factionManager.factions.remove(factionNameDisband);

                        // Rimuovi tutti i membri della fazione dalla mappa
                        for (OfflinePlayer member : faction.getMembers()) {
                            factionManager.playerFactions.remove(member.getUniqueId().toString());
                        }

                        // Rimuovi la fazione dal factions.yml
                        RageFactions.instance.factionsConfig.set("Factions." + factionNameDisband, null);
                        RageFactions.instance.saveFactions();
                        RageFactions.instance.reloadFactions();

                        // Comunica lo scioglimento della fazione a tutti i giocatori
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-broadcast-disband").replace("%s", factionNameDisband).replace("%leader%", faction.getLeaderName())));

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

                    // Stampare il nome e il tag di ogni fazione
                    for (String factionNameList : factionsSection.getKeys(false)) {
                        String factionTag = RageFactions.instance.factionsConfig.getString("Factions." + factionNameList + ".Tag");
                        player.sendMessage(ChatColor.AQUA + "- " + factionNameList + " (" + factionTag + ")");
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

                        // Verifica se il giocatore è un membro della fazione
                        List<String> memberUUIDs = RageFactions.instance.factionsConfig.getStringList("Factions." + factionNameKick + ".Members");
                        String memberUUIDToCheck = memberToKick.getUniqueId().toString() + ":";
                        boolean isMember = memberUUIDs.stream().anyMatch(uuid -> uuid.startsWith(memberUUIDToCheck));

                        // Rmuovi il membro dalla fazione e dalla mappa playerFactions
                        faction.getMembers().remove(memberToKick);
                        factionManager.playerFactions.remove(memberToKick.getUniqueId().toString());

                        if (isMember) {
                            // Rimuovi il membro dalla lista memberUUIDs e aggiorna il file factions.yml
                            memberUUIDs.removeIf(uuid -> uuid.startsWith(memberUUIDToCheck));
                            RageFactions.instance.factionsConfig.set("Factions." + factionNameKick + ".Members", memberUUIDs);
                            RageFactions.instance.saveFactions();
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
            // Comando leave
            case "leave":
                String factionNameLeave = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameLeave != null) {
                    Faction faction = factionManager.factions.get(factionNameLeave);

                    if (faction != null) {
                        if (faction.getLeader().equals(player)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-leadercannotleave")));
                        } else {
                            // Rimuovi il giocatore dalla lista dei membri, rimuovi il suo rank e aggiorna il file factions.yml
                            faction.getMembers().remove(player);
                            faction.ranks.remove(player.getUniqueId());
                            factionManager.playerFactions.remove(player.getUniqueId().toString());
                            List<String> memberUUIDs = RageFactions.instance.factionsConfig.getStringList("Factions." + factionNameLeave + ".Members");
                            String memberUUIDToCheck = player.getUniqueId().toString() + ":";
                            memberUUIDs.removeIf(uuid -> uuid.startsWith(memberUUIDToCheck));
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

                            // Programmazione del teletrasporto differito
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

                    // Invia il messaggio di chat a tutti i membri online della fazione
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

                // Verifica la lunghezza del nuovo tag
                if (newTag.length() > 4) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-create-taglenght")));
                    return true;
                }

                // Ottieni il nome della fazione del giocatore che sta cambiando il tag
                String factionNameTAG = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameTAG != null) {
                    Faction faction = factionManager.factions.get(factionNameTAG);

                    // Verifica se il giocatore che sta cambiando il tag è il leader della fazione
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

                // Ottieni l'oggetto OfflinePlayer per il nuovo leader
                OfflinePlayer newLeader = Bukkit.getOfflinePlayer(newLeaderName);

                // Verifica se il nuovo leader esiste e ha giocato in precedenza
                if (newLeader == null || !newLeader.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-notfound") + " " + newLeaderName));
                    return true;
                }

                // Ottieni il nome della fazione del giocatore che sta assegnando il nuovo leader
                String factionNameNewLeader = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameNewLeader != null) {
                    Faction faction = factionManager.factions.get(factionNameNewLeader);

                    // Verifica se il giocatore che sta assegnando il nuovo leader è il leader della fazione
                    if (faction != null && faction.getLeader().equals(player)) {
                        if (faction.getMembers().contains(newLeader)) { // Verifica se il nuovo leader è un membro della fazione
                            faction.setRank(faction.getLeader(), Rank.MEMBRO); // Imposta il rank del vecchio leader a "Membro"
                            faction.setLeader(newLeader); // Imposta il nuovo leader
                            faction.setRank(newLeader, Rank.LEADER); // Imposta il rank del nuovo leader a "Leader"
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

                    // Verifica se il giocatore che sta eseguendo l'azione è il leader della fazione
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

                // Ottieni l'oggetto OfflinePlayer per il giocatore da invitare
                OfflinePlayer invitee = Bukkit.getOfflinePlayer(inviteeName);

                // Verifica se il giocatore da invitare esiste e ha giocato in precedenza
                if (invitee == null || !invitee.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-notfound") + " " + inviteeName));
                    return true;
                }

                // Ottieni il nome della fazione del giocatore che sta invitando
                String factionNameInvite = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameInvite != null) {
                    Faction faction = factionManager.factions.get(factionNameInvite);

                    // Verifica se il giocatore che sta invitando è il leader della fazione
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
            // Comando uninvite
            case "uninvite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usa /f uninvite <giocatore>");
                    return true;
                }
                String uninviteeName = args[1];

                // Ottieni l'oggetto OfflinePlayer per il giocatore da revocare
                OfflinePlayer uninvitee = Bukkit.getOfflinePlayer(uninviteeName);

                // Verifica se il giocatore da revocare esiste e ha giocato in precedenza
                if (uninvitee == null || !uninvitee.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-notfound") + " " + uninviteeName));
                    return true;
                }

                // Ottieni il nome della fazione del giocatore che sta revocando l'invito
                String factionNameUninvite = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameUninvite != null) {
                    Faction faction = factionManager.factions.get(factionNameUninvite);

                    // Verifica se il giocatore che sta revocando l'invito è il leader della fazione
                    if (faction != null && faction.getLeader().equals(player)) {
                        faction.revokeInvite(uninvitee); // Rimuovi l'invito per il giocatore
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-player-uninvited").replace("%s", uninviteeName)));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
            // Comando rename
            case "rename":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usa /f rename <nuovoNome>");
                    return true;
                }
                String newName = args[1];

                // Ottieni il nome della fazione del giocatore che sta rinominando la fazione
                String factionNameRename = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameRename != null) {
                    Faction faction = factionManager.factions.get(factionNameRename);

                    // Verifica se il giocatore che sta rinominando la fazione è il leader della fazione
                    if (faction != null && faction.getLeader().equals(player)) {
                        // Salva il vecchio nome della fazione per l'aggiornamento del file yml
                        String oldName = faction.getName();
                        faction.setName(newName);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-renamed").replace("%s", newName)));
                        RageFactions.instance.factionsConfig.set("Factions." + oldName, null); // Rimuovi la vecchia fazione
                        RageFactions.instance.factionsConfig.set("Factions." + newName + ".Name", newName); // Aggiungi la nuova fazione
                        RageFactions.instance.saveFaction(faction);
                        RageFactions.instance.reloadFactions();
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
            // Comando promote
            case "promote":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("promote-nospecific")));
                    return true;
                }

                String memberName = args[1];

                OfflinePlayer member = Bukkit.getOfflinePlayer(memberName);

                Faction faction = factionManager.getFaction(player);
                if (faction.getLeader().equals(player)) {
                    faction.promoteMember(member);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("rank-promoted")));
                    RageFactions.instance.saveFaction(faction);
                    RageFactions.instance.reloadFactions();
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("rank-notleader")));
                }
                break;
            // Comando demote
            case "demote":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("promote-nospecific")));
                    return true;
                }

                String memberNameDemote = args[1];

                OfflinePlayer memberDemote = Bukkit.getOfflinePlayer(memberNameDemote);

                Faction factionDemote = factionManager.getFaction(player);
                if (factionDemote.getLeader().equals(player)) {
                    factionDemote.demoteMember(memberDemote);
                    RageFactions.instance.saveFaction(factionDemote);
                    RageFactions.instance.reloadFactions();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("rank-demoted")));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("rank-notleader")));
                }
                break;
            // Comando setrank
            case "setrank":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("rank-promote-nospecific")));
                    return true;
                }
                Faction factionRankNotMember = factionManager.getFaction(player);
                if (factionRankNotMember == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                    return true;
                }

                String memberNameRank = args[1];
                String rankName = args[2];

                OfflinePlayer memberRank = Bukkit.getOfflinePlayer(memberNameRank);
                Rank rank = Rank.valueOf(rankName.toUpperCase());

                Faction factionRank = factionManager.getFaction(player);
                if (factionRank.getLeader().equals(player)) {
                    factionRank.setRank(memberRank, rank);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("rank-promoted")));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("rank-notleader")));
                }
                break;
            // Comando ranks
            case "ranks":
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-ranks")));
                for (Rank ranklist : Rank.values()) {
                    player.sendMessage(ChatColor.AQUA + "- " + ranklist.name());
                }
                break;
            // Comando members
            case "members":
                // Ottieni il nome della fazione del giocatore
                String factionNameMembers = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameMembers != null) {
                    Faction factionMembers = factionManager.factions.get(factionNameMembers);

                    // Verifica se il giocatore è un membro della fazione
                    if (factionMembers != null && factionMembers.getMembers().contains(player)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-members")));
                        for (OfflinePlayer memberNameList : factionMembers.getMembers()) {
                            Rank rankList = factionMembers.getRank(memberNameList);
                            player.sendMessage(ChatColor.AQUA + "- " + memberNameList.getName() + " (" + rankList.name() + ")");
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
            // Comando ally
            case "ally":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allynospecific")));
                    return true;
                }

                String factionNameAlly = factionManager.playerFactions.get(player.getUniqueId().toString());

                if (factionNameAlly != null) {
                    Faction factionAlly = factionManager.factions.get(factionNameAlly);

                    if (factionAlly != null && factionAlly.getLeader().equals(player)) {
                        switch (args[1].toLowerCase()) {
                            case "add":
                                if (args.length < 3) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyaddnospecific")));
                                    return true;
                                }
                                String allyToAdd = args[2];
                                factionManager.inviteToAlliance(factionNameAlly, allyToAdd);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyinvitesent").replace("%s", allyToAdd)));
                                break;
                            case "accept":
                                if (args.length < 3) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyacceptnospecific")));
                                    return true;
                                }
                                String allyToAccept = args[2];
                                factionManager.acceptAlliance(allyToAccept, factionNameAlly);
                                break;
                            case "remove":
                                if (args.length < 3) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyremovenospecific")));
                                    return true;
                                }
                                String allyToRemove = args[2];
                                factionManager.removeAlliance(factionNameAlly, allyToRemove);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyremoved")));
                                break;
                            case "list":
                                Set<String> allies = factionAlly.getAllies();
                                String alliesList = allies.stream()
                                        .map(ally -> "- " + ally)
                                        .collect(Collectors.joining("\n"));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allylist") + "\n" + alliesList));
                                break;
                            default:
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-allyunknown")));
                                break;
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notleader")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                }
                break;
                // Comando deposit
            case "deposit":
                Faction factionDepositNotMember = factionManager.getFaction(player);
                if (factionDepositNotMember == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-depositnospecific")));
                    return true;
                }

                double amount;
                try {
                    amount = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-invalidamount")));
                    return true;
                }

                EconomyResponse response = RageFactions.getEconomy().withdrawPlayer(player, amount);
                if (response.transactionSuccess()) {
                    Faction factionDeposit = factionManager.getFaction(player);
                    factionDeposit.deposit(amount);
                    String message = RageFactions.messages.getMessage("faction-depositsuccess");
                    RageFactions.instance.saveFaction(factionDeposit);
                    RageFactions.instance.reloadFactions();
                    String formattedMessage = String.format(message, RageFactions.getEconomy().format(amount));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', formattedMessage));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-depositfail")));
                }
                break;
            // Comando bank
            case "bank":
                Faction factionBank = factionManager.getFaction(player);
                if (factionBank == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                    return true;
                }
                Faction factionEconomyBank = factionManager.getFaction(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-bankbalance") + " " + RageFactions.getEconomy().format(factionEconomyBank.getBank())));
                break;
                // Comando info
            case "info":
                if (args.length < 2) {
                    Faction factionInfo = factionManager.getFaction(player);
                    if (factionInfo == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-notmember")));
                        return true;
                    }
                    String name = factionInfo.getName();
                    String factionTag = factionInfo.getTag();
                    String leaderName = factionInfo.getLeaderName();
                    String factionBankInfo = String.valueOf(factionInfo.getBank());
                    String power = String.valueOf(factionInfo.getPower());

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info")));
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-name") + " " + name));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-tag") + " " + factionTag));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-leader") + " " + leaderName));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-bank") + " " + factionBankInfo));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-power") + " " + power));
                    player.sendMessage(" ");

                }else if (args[1] != null) {
                    String factionDefined = args[1];
                    Faction factionInfoOther = factionManager.factions.get(factionDefined);

                    if (factionInfoOther == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-dontexist")));
                        return true;
                    }

                    String nameOther = factionInfoOther.getName();
                    String factionTagOther = factionInfoOther.getTag();
                    String leaderNameOther = factionInfoOther.getLeaderName();
                    String factionBankOther = String.valueOf(factionInfoOther.getBank());
                    String powerOther = String.valueOf(factionInfoOther.getPower());

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info")));
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-name") + " " + nameOther));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-tag") + " " + factionTagOther));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-leader") + " " + leaderNameOther));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-bank") + " " + factionBankOther));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-info-power") + " " + powerOther));
                    player.sendMessage(" ");
                }
                break;
            // Comando leaderboard
            case "leaderboard":
                List<Faction> sortedFactions = new ArrayList<>(factionManager.getFactions().values());
                sortedFactions.sort((f1, f2) -> Integer.compare(f2.getPower(), f1.getPower()));

                String rankColor = RageFactions.instance.getConfig().getString("top-rankcolor");
                String nameColor = RageFactions.instance.getConfig().getString("top-namecolor");
                String afterNameColor = RageFactions.instance.getConfig().getString("top-aftername-color");
                String powerColor = RageFactions.instance.getConfig().getString("top-powercolor");
                String powerSymbol = RageFactions.instance.getConfig().getString("top-powersymbol");

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', RageFactions.messages.getMessage("faction-top")));
                for (int i = 0; i < Math.min(10, sortedFactions.size()); i++) {
                    Faction factionTop = sortedFactions.get(i);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            rankColor + (i + 1) + ". " +
                                    nameColor + factionTop.getName() + " " +
                                    afterNameColor + "- " + " " +
                                    powerColor + factionTop.getPower() + " " +
                                    powerSymbol));
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
            return Arrays.asList("create", "join", "disband", "list", "kick", "leave", "home", "sethome", "chat", "tag", "admin", "open", "invite", "uninvite", "rename", "ranks", "promote", "demote", "setrank", "members", "ally", "bank", "deposit", "info", "leaderboard")
                    .stream()
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("ally")) {
            return Arrays.asList("add", "accept", "remove", "list", "chat")
                    .stream()
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            return Arrays.asList("<Nome> <TAG>")
                    .stream()
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
