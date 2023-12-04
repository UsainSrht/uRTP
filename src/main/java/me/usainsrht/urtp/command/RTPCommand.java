package me.usainsrht.urtp.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.usainsrht.urtp.URTP;
import me.usainsrht.urtp.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RTPCommand extends Command {

    public RTPCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public LiteralCommandNode<?> getCommodoreCommand() {
        return LiteralArgumentBuilder.literal(super.getName())
                .build();
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        if (!sender.hasPermission(MainConfig.getPermission("use"))) {
            //MessageUtil.send(sender, LayoutConfig.getMessage("no_permission"));
            //SoundUtil.play(sender, LayoutConfig.getSound("no_permission"));
            return false;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                URTP.getInstance().reload();
                //MessageUtil.send(sender, LayoutConfig.getMessage("reload"));
                //SoundUtil.play(sender, LayoutConfig.getSound("reload"));
            }
        } else {
            Player player = (Player) sender;
            long start = System.currentTimeMillis();
            Location location = URTP.getInstance().getRtpManager().generateRTP(player.getWorld());
            player.sendMessage("x: " + location.getX() + " y: " + location.getY() + " z: " + location.getZ());
            player.teleport(location);
            player.sendMessage("elapsed: " + (System.currentTimeMillis()-start) + "ms");
            //MessageUtil.send(sender, LayoutConfig.getMessage("help").replace("<command>", LayoutConfig.getCmdName()));
            //SoundUtil.play(sender, LayoutConfig.getSound("help"));
            return false;
        }
        return true;
    }

}
