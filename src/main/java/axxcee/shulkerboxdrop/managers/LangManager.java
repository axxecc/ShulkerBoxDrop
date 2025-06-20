package axxcee.shulkerboxdrop.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LangManager {
    private final JavaPlugin plugin;
    private FileConfiguration messages;
    
    public LangManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadMessages() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }
    
    public String getMessage(String path) {
        return messages.getString(path, "&cMissing message: " + path)
                .replace('&', '§');
    }
}