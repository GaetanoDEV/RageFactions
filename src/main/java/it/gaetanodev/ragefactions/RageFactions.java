////////////////////////////////
//                            //
//     CLASSE PRINCIPALE      //
//       RAGEWAR PLUGIN       //
//                            //
////////////////////////////////

package it.gaetanodev.ragefactions;

import it.gaetanodev.ragefactions.Commands.FactionCommands;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class RageFactions extends JavaPlugin {
    public static RageFactions instance;
    public static Messages messages;
    public FileConfiguration factionsConfig;
    private File factionsFile;
    private FactionManager factionManager;
    public List<Rank> ranks = new ArrayList<>();

////////////////////////////////
//                            //
//         METODO DI          //
//         OnEnable()         //
//                            //
////////////////////////////////

    @Override
    public void onEnable() {
        // Messaggi di Avvio
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "--------------------------");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Rage" + ChatColor.WHITE + "Factions");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Abilitato correttamente." + ChatColor.GRAY + " - @Gaethanos__");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "--------------------------");
        getServer().getConsoleSender().sendMessage("");

        // Crea un'istanza di FactionManager
        this.factionManager = new FactionManager();

        // REGISTRA I COMANDI & TAB COMPLETER
        this.getCommand("f").setExecutor(new FactionCommands(factionManager));
        this.getCommand("f").setTabCompleter(new FactionCommands(factionManager));


        // Definisci instance per onEnable()
        instance = this;
        // Carica i messaggi
        messages = new Messages(this);
        // Salva la configurazione se non esiste
        saveDefaultConfig();
        // Crea il file factions.yml se non esiste
        createFactionsFile();
        // Carica le fazioni
        loadFactions();

        // Carica i rank dal config.yml
        List<String> rankNames = getConfig().getStringList("Ranks");
        for (String rankName : rankNames) {
            try {
                Rank rank = Rank.valueOf(rankName.toUpperCase());
                ranks.add(rank);
            } catch (IllegalArgumentException e) {
                getLogger().severe("Rank non valido: " + rankName);
            }
        }
    }


////////////////////////////////
//                            //
//     METODI DI GESTIONE     //
//  E CREAZIONE DELLE CONFIG  //
//       DELLE FAZIONI        //
//                            //
////////////////////////////////

    private void createFactionsFile() {
        this.factionsConfig = factionsConfig;
        factionsFile = new File(getDataFolder(), "factions.yml");
        if (!factionsFile.exists()) {
            factionsFile.getParentFile().mkdirs();
            saveResource("factions.yml", false);
        }
        factionsConfig = new YamlConfiguration();
        try {
            factionsConfig.load(factionsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo saveFactions()
    public void saveFactions() {
        try {
            factionsConfig.save(factionsFile);
        } catch (Exception e) {
            getLogger().severe("Impossibile salvare factions.yml");
            e.printStackTrace();
        }
    }

    // Salva una Faction nel factionFile
    public void saveFaction(Faction faction) {
        String path = "Factions." + faction.getName() + ".";
        RageFactions.instance.factionsConfig.set(path + "Name", faction.getName());
        RageFactions.instance.factionsConfig.set(path + "LeaderUUID", faction.getLeaderUUID().toString());
        RageFactions.instance.factionsConfig.set(path + "LeaderName", faction.getLeader().getName());
        RageFactions.instance.factionsConfig.set(path + "Tag", faction.getTag());
        RageFactions.instance.factionsConfig.set(path + "isPublic", faction.isPublic());
        List<String> memberRanks = faction.getMembers().stream()
                .map(member -> member.getUniqueId().toString() + ":" + faction.getRank(member).name())
                .collect(Collectors.toList());
        RageFactions.instance.factionsConfig.set(path + "Members", memberRanks);
        // Salva le informazioni sulla home"della fazione
        Location homeLocation = faction.getHome();
        if (homeLocation != null) {
            RageFactions.instance.factionsConfig.set(path + "Home.World", homeLocation.getWorld().getName());
            RageFactions.instance.factionsConfig.set(path + "Home.X", homeLocation.getX());
            RageFactions.instance.factionsConfig.set(path + "Home.Y", homeLocation.getY());
            RageFactions.instance.factionsConfig.set(path + "Home.Z", homeLocation.getZ());
        }
        try {
            RageFactions.instance.factionsConfig.save(factionsFile);
        } catch (Exception e) {
            getLogger().severe("Impossibile salvare factions.yml");
            e.printStackTrace();
        }
    }


    // Metodo di reload delle fazioni
    public void reloadFactions() {
        factionManager.factions.clear();
        factionManager.playerFactions.clear();
        RageFactions.instance.loadFactions();
    }


    // Carica le factions.yml
    public void loadFactions() {
        ConfigurationSection factionsSection = factionsConfig.getConfigurationSection("Factions");
        if (factionsSection != null) {
            for (String factionName : factionsSection.getKeys(false)) {
                String path = "Factions." + factionName + ".";
                UUID leaderUUID = UUID.fromString(factionsConfig.getString(path + "LeaderUUID"));
                String leaderName = factionsConfig.getString(path + "LeaderName");
                OfflinePlayer leader = Bukkit.getOfflinePlayer(leaderUUID);
                String tag = factionsConfig.getString(path + "Tag");
                if (leader.hasPlayedBefore()) {
                    List<String> memberRankStrings = factionsConfig.getStringList(path + "Members");
                    Faction faction = new Faction(factionName, tag, leader);
                    faction.setLeader(leader);
                    for (String memberRankString : memberRankStrings) {
                        String[] parts = memberRankString.split(":");
                        UUID memberUUID = UUID.fromString(parts[0]);
                        Rank rank = Rank.valueOf(parts[1]);
                        OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
                        faction.addMember(member);
                        faction.setRank(member, rank);
                        // Aggiungi il membro alla mappa playerFactions
                        factionManager.playerFactions.put(member.getUniqueId().toString(), factionName);
                    }
                    // Carica le informazioni sulla "home" della fazione
                    String worldName = factionsConfig.getString(path + "Home.World");
                    if (worldName != null) {
                        double x = factionsConfig.getDouble(path + "Home.X");
                        double y = factionsConfig.getDouble(path + "Home.Y");
                        double z = factionsConfig.getDouble(path + "Home.Z");
                        World world = Bukkit.getWorld(worldName);
                        Location homeLocation = new Location(world, x, y, z);
                        faction.setHome(homeLocation);
                    }
                    factionManager.factions.put(factionName, faction);
                } else {
                    getLogger().info("Leader non valido");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        saveFactions();
        getLogger().info("Tutte le fazioni sono state salvate.");
    }
}
