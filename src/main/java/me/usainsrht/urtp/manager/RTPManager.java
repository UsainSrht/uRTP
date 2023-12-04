package me.usainsrht.urtp.manager;

import me.usainsrht.urtp.URTP;
import me.usainsrht.urtp.config.RTPConfig;
import me.usainsrht.urtp.rtp.RTPLayout;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.concurrent.CompletableFuture;

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
        if (plugin.isDebug()) {
            plugin.getLogger().info("generating rtp, mode " + rtpLayout.getMethod());
        }
        switch (rtpLayout.getMethod()) {
            case GET_HIGHEST_BLOCK -> {
                while (location == null) {
                    attempts++;
                    int x = rtpLayout.getxRange().next();
                    int z = rtpLayout.getzRange().next();
                    Block block = world.getHighestBlockAt(x, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                        break;
                    }
                    attempts++;
                    if (attempts >= rtpLayout.getMaxAttempts()) {
                        if (plugin.isDebug()) {
                            plugin.getLogger().info("attemps maxed out " + attempts + " / " + rtpLayout.getMaxAttempts() );
                        }
                        break;
                    }
                }
            }
            case ITERATE_FROM_TOP -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMax()+1;
                int z = rtpLayout.getzRange().next();
                while (location == null) {
                    y--;
                    Block block = world.getBlockAt(x, y, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                        break;
                    }
                    if (y <= rtpLayout.getzRange().getMin()) {
                        attempts++;
                        if (attempts >= rtpLayout.getMaxAttempts()) {
                            if (plugin.isDebug()) {
                                plugin.getLogger().info("attemps maxed out " + attempts + " / " + rtpLayout.getMaxAttempts() );
                            }
                            break;
                        }
                        x = rtpLayout.getxRange().next();
                        y = rtpLayout.getyRange().getMax()+1;
                        z = rtpLayout.getzRange().next();
                    }
                }
            }
            case ITERATE_FROM_BOTTOM -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMin()-1;
                int z = rtpLayout.getzRange().next();
                while (location == null) {
                    y++;
                    Block block = world.getBlockAt(x, y, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                        break;
                    }
                    if (y >= rtpLayout.getzRange().getMax()) {
                        attempts++;
                        if (attempts >= rtpLayout.getMaxAttempts()) {
                            if (plugin.isDebug()) {
                                plugin.getLogger().info("attemps maxed out " + attempts + " / " + rtpLayout.getMaxAttempts() );
                            }
                            break;
                        }
                        x = rtpLayout.getxRange().next();
                        y = rtpLayout.getyRange().getMin()-1;
                        z = rtpLayout.getzRange().next();
                    }

                }
            }
        }
        return location;
    }

    public CompletableFuture<Location> generateRTP(World world) {
        RTPLayout rtpLayout = RTPConfig.getRtpWorlds().get(world);
        CompletableFuture<Location> future = null;
        int attempts = 0;
        if (plugin.isDebug()) {
            plugin.getLogger().info("generating async rtp, mode " + rtpLayout.getMethod());
        }
        switch (rtpLayout.getMethod()) {
            case GET_HIGHEST_BLOCK -> {
                while (!future.isDone()) {
                    attempts++;
                    int x = rtpLayout.getxRange().next();
                    int z = rtpLayout.getzRange().next();
                    world.getChunkAtAsyncUrgently(x, z).thenRun(() -> {
                        Block block = world.getHighestBlockAt(x, z);
                        if (isSafe(block)) {
                            future.complete(block.getLocation());
                            break;
                        }
                    });

                    attempts++;
                    if (attempts >= rtpLayout.getMaxAttempts()) {
                        if (plugin.isDebug()) {
                            plugin.getLogger().info("attemps maxed out " + attempts + " / " + rtpLayout.getMaxAttempts() );
                        }
                        break;
                    }
                }
            }
            case ITERATE_FROM_TOP -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMax()+1;
                int z = rtpLayout.getzRange().next();
                while (location == null) {
                    y--;
                    Block block = world.getBlockAt(x, y, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                        break;
                    }
                    if (y <= rtpLayout.getzRange().getMin()) {
                        attempts++;
                        if (attempts >= rtpLayout.getMaxAttempts()) {
                            if (plugin.isDebug()) {
                                plugin.getLogger().info("attemps maxed out " + attempts + " / " + rtpLayout.getMaxAttempts() );
                            }
                            break;
                        }
                        x = rtpLayout.getxRange().next();
                        y = rtpLayout.getyRange().getMax()+1;
                        z = rtpLayout.getzRange().next();
                    }
                }
            }
            case ITERATE_FROM_BOTTOM -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMin()-1;
                int z = rtpLayout.getzRange().next();
                while (location == null) {
                    y++;
                    Block block = world.getBlockAt(x, y, z);
                    if (isSafe(block)) {
                        location = block.getLocation();
                        break;
                    }
                    if (y >= rtpLayout.getzRange().getMax()) {
                        attempts++;
                        if (attempts >= rtpLayout.getMaxAttempts()) {
                            if (plugin.isDebug()) {
                                plugin.getLogger().info("attemps maxed out " + attempts + " / " + rtpLayout.getMaxAttempts() );
                            }
                            break;
                        }
                        x = rtpLayout.getxRange().next();
                        y = rtpLayout.getyRange().getMin()-1;
                        z = rtpLayout.getzRange().next();
                    }

                }
            }
        }
        return future;
    }

    public boolean isSafe(Block block) {
        if (!block.isSolid()) return false;
        Block above = block.getRelative(0, 1, 0);
        if (above.isSolid() || above.isLiquid()) return false;
        Block above2 = block.getRelative(0, 2, 0);
        if (above2.isSolid() || above2.isLiquid()) return false;
        return true;
    }

}
