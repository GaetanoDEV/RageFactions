////////////////////////////////
//                            //
//  CLASSE GESTORE MESSAGGI   //
//                            //
////////////////////////////////

package it.gaetanodev.ragefactions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Messages {
    private final JavaPlugin plugin;
    private FileConfiguration messagesConfig;
    private File messageFile;

    public Messages(JavaPlugin plugin) {
        this.plugin = plugin;
        createMessagesFile();
    }

    // Crea il file messages.yml se non esiste
    private void createMessagesFile() {
        messageFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messageFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messageFile);
    }

    // Metodo per ottenere un messaggio dal file di configurazione
    public String getMessage(String path) {
        return messagesConfig.getString(path);
    }

    // Ricarica i messaggi dal file di configurazione
    public void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messageFile);
    }
}