package me.usainsrht.urtp.util;

import me.usainsrht.urtp.URTP;
import me.usainsrht.urtp.config.MainConfig;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

public class MessageUtil {

    private static URTP plugin;
    private static MiniMessage miniMessage;

    public static void initialize(URTP plugin) {
        MessageUtil.plugin = plugin;
        miniMessage = plugin.getMiniMessage();
    }

    public static void send(CommandSender sender, String message) {
        Collection<String> messages = MainConfig.getMessage(message);
        Collection<Component> components = messages.stream().map(string -> miniMessage.deserialize(string)).toList();
        Component component = Component.join(JoinConfiguration.newlines(), components);
        sender.sendMessage(component);

        MainConfig.getSound(message).forEach(sender::playSound);
    }
}
