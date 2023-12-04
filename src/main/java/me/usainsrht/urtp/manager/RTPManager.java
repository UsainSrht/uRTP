package me.usainsrht.urtp.manager;

import me.usainsrht.urtp.URTP;
import me.usainsrht.urtp.config.RTPConfig;
import me.usainsrht.urtp.rtp.RTPLayout;
import me.usainsrht.urtp.rtp.RTPMethod;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.Random;

public class RTPManager {

    private URTP plugin;

    public RTPManager(URTP plugin) {
        this.plugin = plugin;
    }

    public URTP getPlugin() {
        return plugin;
    }

    public Location generateRTP(World world) {
        RTPLayout rtpLayout = RTPConfig.getRtpWorlds().get(world);
        Location location = null;
        int attempts = 0;
        switch (rtpLayout.getMethod()) {
            case GET_HIGHEST_BLOCK -> {
                while (location == null) {
                    attempts++;
                    int x = rtpLayout.getxRange().next();
                    int z = rtpLayout.getzRange().next();
                    Block block = world.getHighestBlockAt(x, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                    }
                    attempts++;
                    if (attempts >= rtpLayout.getMaxAttempts()) {
                        break;
                    }
                }
            }
            case ITERATE_FROM_TOP -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMax()+1;
                int z = rtpLayout.getzRange().next();
                while (location == null) {
                    attempts++;
                    y--;
                    Block block = world.getBlockAt(x, y, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                    }
                    attempts++;
                    if (attempts >= rtpLayout.getMaxAttempts()) {
                        break;
                    }
                }
            }
            case ITERATE_FROM_BOTTOM -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMin()-1;
                int z = rtpLayout.getzRange().next();
                while (location == null) {
                    attempts++;
                    y++;
                    Block block = world.getBlockAt(x, y, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                    }
                    attempts++;
                    if (attempts >= rtpLayout.getMaxAttempts()) {
                        break;
                    }
                }
            }
        }
        return location;
    }

    public boolean isSafe(Block block) {
        if (!block.isSolid()) return false;
        Block above = block.getRelative(0, 1, 0);
        if (above.isSolid() && above.isLiquid()) return false;
        Block above2 = block.getRelative(0, 2, 0);
        if (above2.isSolid() && above2.isLiquid()) return false;
        return true;
    }

}
