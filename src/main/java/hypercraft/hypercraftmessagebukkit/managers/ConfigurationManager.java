package hypercraft.hypercraftmessagebukkit.managers;

import hypercraft.hypercraftmessagebukkit.HypercraftMessageBukkit;
import java.io.File;
import hypercraft.hypercraftmessagebukkit.configurations.ChatConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationManager {
    private HypercraftMessageBukkit core;
    private ChatConfig chat = new ChatConfig();
    private File chatFile;
    private FileConfiguration chatConfig;

    public ConfigurationManager(HypercraftMessageBukkit core) {
        this.core = core;
        this.chatFile = new File(this.core.getDataFolder(), "config.yml");
        core.createDefaultConfiguration(this.chatFile, "config.yml");
        this.chatConfig = YamlConfiguration.loadConfiguration(this.chatFile);
        this.chat.load(this.chatConfig);
    }

    public ChatConfig getChat() {
        return this.chat;
    }
}
