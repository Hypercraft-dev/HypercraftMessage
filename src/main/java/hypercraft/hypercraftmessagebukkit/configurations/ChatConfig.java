package hypercraft.hypercraftmessagebukkit.configurations;

import org.bukkit.configuration.Configuration;

public class ChatConfig {
    private String chatFormat;
    private String ignoreFormat;

    public ChatConfig() {
    }

    public void load(Configuration config) {
        this.chatFormat = config.getString("chatFormat");
        this.ignoreFormat = config.getString("ignoreFormat");
    }

    public String getChatFormat() {
        return this.chatFormat;
    }

    public String getIgnoreFormat() {
        return this.ignoreFormat;
    }
}
