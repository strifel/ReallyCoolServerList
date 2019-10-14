package de.strifel.rcsl;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    private HashMap cachedUserNames;
    private ConfigFile config, cache;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        try {
            new File("plugins/ReallyCoolServerList/").mkdirs();
            cache = new ConfigFile(new File("plugins/ReallyCoolServerList/cache.json"), new HashMap(){{
                put("", "");
            }});
            config = new ConfigFile(new File("plugins/ReallyCoolServerList/config.json"), new HashMap<String, Object>(){{
                put("showMaxUser", 0);
                put("standardMessage", "Hi! Welcome to my Server. Feel free to join!");
                put("recognizedPlayerMessage", "Hi! Welcome back $username");
                put("bannedPlayerMessage", "You seem to be banned $username! You can appeal at forum.myserver.tld");
            }});
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        cachedUserNames = cache.getValues();
    }

    @Override
    public void onDisable() {
        try {
            cache.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onServerList(ServerListPingEvent event) {
        event.setMaxPlayers(0);
        if (cachedUserNames.containsKey(event.getAddress().getHostAddress())) {
            OfflinePlayer player = getServer().getOfflinePlayer((String) cachedUserNames.get(event.getAddress().getHostAddress()));
            if (player.isBanned()) {
                event.setMotd(ChatColor.DARK_RED + ((String)config.getValues().get("bannedPlayerMessage")).replace("$username", player.getName()));
            } else {
                event.setMotd(ChatColor.GREEN + ((String)config.getValues().get("recognizedPlayerMessage")).replace("$username", player.getName()));
            }
        } else {
            event.setMotd(ChatColor.GREEN + ((String) config.getValues().get("standardMessage")));
        }

    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        cachedUserNames.put(event.getAddress().getHostAddress(), event.getPlayer().getDisplayName());
    }
}
