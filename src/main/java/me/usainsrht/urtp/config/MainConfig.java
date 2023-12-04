package me.usainsrht.urtp.config;

import me.usainsrht.urtp.command.RTPCommand;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class MainConfig {

    private static String prefix;

    private static HashMap<String, Collection<String>> messages;
    private static HashMap<String, Collection<Sound>> sounds;

    private static String cmdName;
    private static String cmdDesc;
    private static String cmdUsage;
    private static HashMap<String, String> cmdPerms;
    private static List<String> cmdAliases;

    public static void create(ConfigurationSection config) {
        prefix = config.getString("prefix");

        messages = new HashMap<>();
        config.getConfigurationSection("messages").getKeys(false).forEach(key -> {
            List<String> msgCollection = new ArrayList<>();
            if (config.isString("messages." + key)) msgCollection.add(config.getString("messages." + key));
            else if (config.isList("messages." + key)) msgCollection.addAll(config.getStringList("messages." + key));
            messages.put(key, msgCollection);
        });

        sounds = new HashMap<>();
        config.getConfigurationSection("sounds").getKeys(false).forEach(key -> {
            Sound.Builder sound = Sound.sound();
            String[] splitted = config.getString("sounds." + key).split(",");
            Key name = Key.key(splitted[0]);
            sound.type(name);
            if (splitted.length > 1) {
                float volume = Float.parseFloat(splitted[1]);
                sound.volume(volume);
                if (splitted.length > 2) {
                    float pitch = Float.parseFloat(splitted[2]);
                    sound.pitch(pitch);
                    if (splitted.length > 3) {
                        Sound.Source source = Sound.Source.valueOf(splitted[3]);
                        sound.source(source);
                        if (splitted.length > 4) {
                            long seed = Long.parseLong(splitted[4]);
                            sound.seed(seed);
                        }
                    }
                }
            }
        });

        cmdName = config.getString("command.name");
        cmdDesc = config.getString("command.description");
        cmdUsage = config.getString("command.usage");
        cmdAliases = config.getStringList("command.aliases");
        cmdPerms = new HashMap<>();
        config.getConfigurationSection("command.permissions").getKeys(false).forEach(key -> {
            cmdPerms.put(key, config.getString("command.permissions." + key));
        });

    }

    public static String getPrefix() {
        return prefix;
    }

    public static Collection<String> getMessage(String message) {
        return messages.getOrDefault(message, List.of(message));
    }

    public static Collection<Sound> getSound(String sound) {
        return sounds.getOrDefault(sound, Collections.emptyList());
    }

    public static String getCmdName() {
        return cmdName;
    }

    public static String getCmdDesc() {
        return cmdDesc;
    }

    public static String getCmdUsage() {
        return cmdUsage;
    }

    public static List<String> getCmdAliases() {
        return cmdAliases;
    }

    public static String getPermission(String permission) {
        return cmdPerms.get(permission);
    }
}
