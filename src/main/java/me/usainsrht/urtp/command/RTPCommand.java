package me.usainsrht.urtp.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.usainsrht.urtp.URTP;
import me.usainsrht.urtp.config.MainConfig;
import me.usainsrht.urtp.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
            MessageUtil.send(sender, "no_perm_use");
            return false;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                URTP.getInstance().reload();
                MessageUtil.send(sender, "reload");
            } else if (args[0].equalsIgnoreCase("debug")) {
                URTP.getInstance().setDebug(!URTP.getInstance().isDebug());
                MessageUtil.send(sender, "debug " + URTP.getInstance().isDebug());
            } else if (args[0].equalsIgnoreCase("async")) {
                Player player = (Player) sender;
                long start = System.currentTimeMillis();
                CompletableFuture<Location> completableLocation = URTP.getInstance().getRtpManager().generateRTPAsync(player.getWorld());
                completableLocation.thenAcceptAsync(location -> {
                    player.sendMessage("x: " + location.getX() + " y: " + location.getY() + " z: " + location.getZ());
                    player.sendMessage("async elapsed: " + (System.currentTimeMillis()-start) + "ms");
                    location.add(0.5, 1, 0.5);
                    player.teleport(location);
                });
            }
        } else {
            Player player = (Player) sender;
            long start = System.currentTimeMillis();
            Location location = URTP.getInstance().getRtpManager().generateRTP(player.getWorld());
            player.sendMessage("x: " + location.getX() + " y: " + location.getY() + " z: " + location.getZ());
            player.sendMessage("elapsed: " + (System.currentTimeMillis()-start) + "ms");
            location.add(0.5, 1, 0.5);
            player.teleport(location);
            return false;
        }
        return true;
    }

}
