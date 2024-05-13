package it.gaetanodev.ragefactions;

import it.gaetanodev.ragefactions.Commands.FactionCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class RageFactions extends JavaPlugin {
    public static RageFactions instance;
    public static Messages messages;
    public FileConfiguration factionsConfig;
    private File factionsFile;
    private FactionManager factionManager;


    @Override
    public void onEnable() {
        // Messaggi di Avvio
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "--------------------------");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Rage" + ChatColor.WHITE + "Factions");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Abilitato correttamente." + ChatColor.GRAY + " - @Gaethanos__");
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "--------------------------");

        // REGISTRA I COMANDI
        factionManager = new FactionManager();
        this.getCommand("f").setExecutor(new FactionCommands(factionManager));
        this.getCommand("f").setTabCompleter(new FactionCommands(factionManager));



        // Definisci instance in Main
        instance = this;
        // Carica i messaggi
        messages = new Messages(this);
        // Carica la configurazione
        saveDefaultConfig();
        // Carica i Factions
        createFactionsFile();
        // Carica le fazioni
        loadFactions();


    }

    // METODO FACTIONS CONFIG
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
        List<String> memberUUIDs = faction.getMembers()
                .stream().map(member -> member.getUniqueId().toString())
                .collect(Collectors.toList());
        RageFactions.instance.factionsConfig.set(path + "Members", memberUUIDs);
        try {
            RageFactions.instance.factionsConfig.save(factionsFile);
        } catch (Exception e) {
            getLogger().severe("Impossibile salvare factions.yml");
            e.printStackTrace();
        }
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
                if (leader.hasPlayedBefore()) {
                    List<String> memberUUIDStrings = factionsConfig.getStringList(path + "Members");
                    List<OfflinePlayer> members = memberUUIDStrings.stream()
                            .map(UUID::fromString)
                            .map(Bukkit::getOfflinePlayer)
                            .collect(Collectors.toList());
                    Faction faction = new Faction(factionName);
                    faction.setLeader(leader);
                    for (OfflinePlayer member : members) {
                        if (member.hasPlayedBefore()) {
                            faction.addMember(member);
                        }
                    }
                    factionManager.factions.put(factionName, faction);
                    factionManager.playerFactions.put(leaderUUID.toString(), factionName);
                    factionManager.playerFactions.put(leader.getName(), factionName);
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
