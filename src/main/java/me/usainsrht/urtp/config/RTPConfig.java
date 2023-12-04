package me.usainsrht.urtp.config;

import me.usainsrht.urtp.rtp.RTPLayout;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class RTPConfig {

    private static HashMap<World, RTPLayout> rtpWorlds;

    public static void create(ConfigurationSection config) {
        rtpWorlds = new HashMap<>();
        config.getKeys(false).forEach(worldName -> {
            World world = Bukkit.getWorld(worldName);
            if (world == null) return;
            RTPLayout rtpLayout = new RTPLayout(config.getConfigurationSection(worldName));
            rtpWorlds.put(world, rtpLayout);
        });
    }

    public static HashMap<World, RTPLayout> getRtpWorlds() {
        return rtpWorlds;
    }
}
