package hypercraft.hypercraftmessagebukkit;

import hypercraft.hypercraftmessagebukkit.listeners.ChatListener;
import hypercraft.hypercraftmessagebukkit.managers.ConfigurationManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class HypercraftMessageBukkit extends JavaPlugin {
    private ChatListener chatListener;
    private ConfigurationManager configurationManager;

    public HypercraftMessageBukkit() {
    }

    public void onEnable() {
        this.configurationManager = new ConfigurationManager(this);
        this.chatListener = new ChatListener(this);
        Bukkit.getPluginManager().registerEvents(this.chatListener, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "hc:chatbungee");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "hc:chatbukkit", this.chatListener);
    }

    public void createDefaultConfiguration(File actual, String defaultName) {
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (!actual.exists()) {
            InputStream input = null;

            try {
                JarFile file = new JarFile(this.getFile());
                ZipEntry copy = file.getEntry(defaultName);
                if (copy == null) {
                    throw new FileNotFoundException();
                }

                input = file.getInputStream(copy);
            } catch (IOException var21) {
                this.getLogger().severe("Unable to read default configuration: " + defaultName);
            }

            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];

                    int length;
                    while((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }

                    this.getLogger().info("Default configuration file written: " + actual.getAbsolutePath());
                } catch (IOException var22) {
                    var22.printStackTrace();
                } finally {
                    try {
                        input.close();
                    } catch (IOException var20) {
                    }

                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (IOException var19) {
                    }

                }
            }

        }
    }

    public ChatListener getChatListener() {
        return this.chatListener;
    }

    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    public String setPlaceHolders(OfflinePlayer player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
