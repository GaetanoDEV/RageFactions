////////////////////////////////
//                            //
//     CLASSE PRINCIPALE      //
//       RAGEWAR PLUGIN       //
//                            //
////////////////////////////////

package it.gaetanodev.ragefactions;

import it.gaetanodev.ragefactions.Commands.FactionCommands;
import it.gaetanodev.ragefactions.Events.FriendlyFire;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;

public final class RageFactions extends JavaPlugin {
    public static RageFactions instance;
    public static Messages messages;
    public FileConfiguration factionsConfig;
    public List<Rank> ranks = new ArrayList<>();
    private File factionsFile;
    private FactionManager factionManager;
    private static Economy econ = null;

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

        // Registra il Listerner di FriendlyFire
        FriendlyFire listener = new FriendlyFire(factionManager);
        getServer().getPluginManager().registerEvents(listener, this);

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

        // Crea un istanza per Vault
        if (!setupEconomy() ) {
            getLogger().severe("Disabilitato a causa della mancanza di Vault");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
        factionsConfig.set(path + "Name", faction.getName());
        factionsConfig.set(path + "LeaderUUID", faction.getLeaderUUID().toString());
        factionsConfig.set(path + "LeaderName", faction.getLeader().getName());
        factionsConfig.set(path + "Tag", faction.getTag());
        factionsConfig.set(path + "isPublic", faction.isPublic());
        factionsConfig.set(path + "Bank", faction.getBank());
        List<String> memberRanks = faction.getMembers().stream()
                .map(member -> member.getUniqueId().toString() + ":" + faction.getRank(member).name())
                .collect(Collectors.toList());
        RageFactions.instance.factionsConfig.set(path + "Members", memberRanks);
        // Salva le informazioni sulla home"della fazione
        Location homeLocation = faction.getHome();
        if (homeLocation != null) {
            factionsConfig.set(path + "Home.World", homeLocation.getWorld().getName());
            factionsConfig.set(path + "Home.X", homeLocation.getX());
            factionsConfig.set(path + "Home.Y", homeLocation.getY());
            factionsConfig.set(path + "Home.Z", homeLocation.getZ());
        }
        // Salva la lista degli alleati
        factionsConfig.set(path + "Allies", new ArrayList<>(faction.getAllies()));
        try {
            factionsConfig.save(factionsFile);
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
                    // Carica le alleanze
                    List<String> allies = factionsConfig.getStringList(path + "Allies");
                    if (allies != null) {
                        faction.getAllies().addAll(allies);
                    }
                    // Carica il saldo della banca della fazione
                    double bank = factionsConfig.getDouble(path + "Bank");
                    faction.setBank(bank); // Imposta il saldo della banca
                    factionManager.factions.put(factionName, faction);
                } else {
                    getLogger().info("Leader non valido");
                }
            }
        }
    }

////////////////////////////////
//                            //
//          GESTIONE          //
//          ECONOMIA          //
//                            //
////////////////////////////////


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEconomy() {
        return econ;
    }

    // Metodo di spegnimento del Server
    @Override
    public void onDisable() {
        saveFactions();
        getLogger().info("Tutte le fazioni sono state salvate.");
    }
}



