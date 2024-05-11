package it.gaetanodev.ragefactions;

import it.gaetanodev.ragefactions.Commands.FactionCommands;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RageFactions extends JavaPlugin {
    public static RageFactions instance;
    public static Messages messages;
    private File factionsFile;
    private FileConfiguration factionsConfig;
    private FactionManager factionManager;
    private Map<String, Faction> factions;

    public RageFactions() {
        this.factions = new HashMap<>();

    }

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


        // Definisci instance in Main
        instance = this;
        // Carica i messaggi
        messages = new Messages(this);
        // Carica la configurazione
        saveDefaultConfig();
        // Carica i Factions
        createFactionsFile();

    }

    // METODO FACTIONS CONFIG
    private void createFactionsFile() {
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

    public void saveFactions() {
        Map<String, Faction> factions = factionManager.factions;
        if (factions != null) {
            for (Map.Entry<String, Faction> entry : factions.entrySet()) {
                Faction faction = entry.getValue();
                if (faction != null) {
                    String path = "Factions." + entry.getKey() + ".";
                    factionsConfig.set(path + "Name", entry.getValue().getName());
                    factionsConfig.set(path + "Leader", faction.getLeaderName());
                    List<String> memberNames = faction.getMembers()
                            .stream().map(Player::getName)
                            .collect(Collectors.toList());
                    factionsConfig.set(path + "Members", memberNames);
                }
                try {
                    factionsConfig.save(factionsFile);
                } catch (Exception e) {
                    getLogger().severe("Impossibile salvare factions.yml");
                    e.printStackTrace();
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
