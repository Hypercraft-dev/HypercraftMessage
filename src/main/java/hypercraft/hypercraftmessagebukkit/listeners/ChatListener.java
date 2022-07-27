package hypercraft.hypercraftmessagebukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.String;
import hypercraft.hypercraftmessagebukkit.HypercraftMessageBukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ChatListener implements Listener, PluginMessageListener {
    private HypercraftMessageBukkit plugin;

    public ChatListener(HypercraftMessageBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onChat(AsyncPlayerChatEvent e) {
        int nbr_maj = nbr_maj(e.getMessage());
        int nbr_min = nbr_min(e.getMessage());
        double result = (double)nbr_maj / (double)nbr_min;
        if (this.unflood(e.getMessage())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Votre message contient un flood. Il a été automatiquement supprimé.");
        }

        if (!e.isCancelled()) {
            if (e.getMessage().length() > 3 && result > 1.0D) {
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Votre message contient trop de majuscules. Il a été automatiquement mit en minuscule.");
                e.setMessage(e.getMessage().toLowerCase());
            }

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(e.getPlayer().getUniqueId().toString());
            String format = this.plugin.getConfigurationManager().getChat().getChatFormat();
            format = format.replace("{message}", e.getMessage());
            format = format.replace("{displayName}", e.getPlayer().getDisplayName());
            format = this.plugin.setPlaceHolders(e.getPlayer(), format);
            out.writeUTF(format);
            out.writeUTF(e.getMessage());
            e.getPlayer().sendPluginMessage(this.plugin, "hc:chatbungee", out.toByteArray());
            e.setCancelled(true);
        }

    }

    private static int nbr_min(String chaine) {
        int compteur = 0;

        for(int i = 0; i < chaine.length(); ++i) {
            char ch = chaine.charAt(i);
            if (ch != ' ' && ch != '!' && ch != '?' && Character.isLowerCase(ch)) {
                ++compteur;
            }
        }

        return compteur;
    }

    private static int nbr_maj(String chaine) {
        int compteur = 0;

        for(int i = 0; i < chaine.length(); ++i) {
            char ch = chaine.charAt(i);
            if (ch != ' ' && ch != '!' && ch != '?' && Character.isUpperCase(ch)) {
                ++compteur;
            }
        }

        return compteur;
    }

    private boolean unflood(String msg) {
        int tolerance = 7;
        char prev = msg.charAt(0);
        int occur = 1;

        for(int i = 1; i < msg.length(); ++i) {
            if (msg.charAt(i) == prev && prev != ' ') {
                ++occur;
            } else {
                occur = 1;
                prev = msg.charAt(i);
            }

            if (occur >= tolerance) {
                return true;
            }
        }

        return false;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equalsIgnoreCase("hc:chatbukkit")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            String messageRecieved;
            if (subchannel.equalsIgnoreCase("Message")) {
                messageRecieved = in.readUTF();
                messageRecieved = this.format(messageRecieved);
                String[] uuids = in.readUTF().split(";");
                List<UUID> ignores = new ArrayList();
                int var10 = uuids.length;

                for (String uuid : uuids) {
                    if (!uuid.equalsIgnoreCase("")) {
                        ignores.add(UUID.fromString(uuid));
                    }
                }

                this.plugin.getLogger().info(messageRecieved);

                for (Player player1 : this.plugin.getServer().getOnlinePlayers()) {
                    if (!ignores.contains(player1.getUniqueId())) {
                        player1.sendMessage(messageRecieved);
                    } else {
                        player1.sendMessage(this.format(this.plugin.getConfigurationManager().getChat().getIgnoreFormat()));
                    }
                }
            } else if (subchannel.equalsIgnoreCase("song")) {
                messageRecieved = in.readUTF();
                Player player1 = Bukkit.getPlayer(UUID.fromString(messageRecieved));
                if (player1 != null) {
                    player1.playSound(player1.getLocation(), org.bukkit.Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
                }
            }
        }

    }

    private String format(String msg) {
        Pattern pattern = Pattern.compile("[{]#[a-fA-F0-9]{6}[}]");
        for (Matcher match = pattern.matcher(msg); match.find(); match = pattern.matcher(msg)) {
            String color = msg.substring(match.start(), match.end());
            String replace = color;
            color = color.replace("{", "");
            color = color.replace("}", "");
            msg = msg.replace(replace, ChatColor.of(color).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
