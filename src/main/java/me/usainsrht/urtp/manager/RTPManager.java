package me.usainsrht.urtp.manager;

import me.usainsrht.urtp.URTP;
import me.usainsrht.urtp.config.RTPConfig;
import me.usainsrht.urtp.rtp.RTPLayout;
import org.bukkit.Bukkit;
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

    public CompletableFuture<Location> generateRTPAsync(World world) {
        RTPLayout rtpLayout = RTPConfig.getRtpWorlds().get(world);
        if (plugin.isDebug()) {
            plugin.getLogger().info("generating async rtp, mode " + rtpLayout.getMethod());
        }
        CompletableFuture<Location> future = new CompletableFuture<>();
        completeRTPFuture(future, world, rtpLayout);
        return future;
    }

    public void completeRTPFuture(CompletableFuture<Location> future, World world, RTPLayout rtpLayout) {
        switch (rtpLayout.getMethod()) {
            case GET_HIGHEST_BLOCK -> completeRTPFutureByGetHighestBlock(future, world, rtpLayout, 0);
            case ITERATE_FROM_TOP -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMax() + 1;
                int z = rtpLayout.getzRange().next();
                completeRTPFutureByIterateFromTop(future, world, rtpLayout, 0, x, y, z);
            }
            case ITERATE_FROM_BOTTOM -> {
                int x = rtpLayout.getxRange().next();
                int y = rtpLayout.getyRange().getMin() - 1;
                int z = rtpLayout.getzRange().next();
                completeRTPFutureByIterateFromBottom(future, world, rtpLayout, 0, x, y, z);
            }
        }
    }

    private void completeRTPFutureByGetHighestBlock(CompletableFuture<Location> future, World world, RTPLayout rtpLayout, int attempts) {
        int x = rtpLayout.getxRange().next();
        int z = rtpLayout.getzRange().next();
        if (plugin.isDebug()) plugin.getLogger().info("loading chunk at x: " + x + " z: " + z + " attempt: " + attempts);

        long start = System.currentTimeMillis();
        world.getChunkAtAsyncUrgently(x, z).thenRun(() -> {
            if (plugin.isDebug()) plugin.getLogger().info("loaded chunk at x: " + x + " z: " + z + " in " + (System.currentTimeMillis()-start) + "ms");

            Block block = world.getHighestBlockAt(x, z);
            if (isSafe(block)) {
                future.complete(block.getLocation());
                if (plugin.isDebug()) plugin.getLogger().info("block is safe, completed the future. attempt: " + attempts);
                return;
            }
            int attempt = attempts + 1;
            if (attempt >= rtpLayout.getMaxAttempts()) {
                if (plugin.isDebug()) plugin.getLogger().info("attemps maxed out " + attempt + " / " + rtpLayout.getMaxAttempts());
                return;
            }
            completeRTPFutureByGetHighestBlock(future, world, rtpLayout, attempt);
        });
    }

    private void completeRTPFutureByIterateFromTop(CompletableFuture<Location> future, World world, RTPLayout rtpLayout, int attempts, int x, int y, int z) {
        if (plugin.isDebug()) plugin.getLogger().info("loading chunk at x: " + x + " y: " + y + " z: " + z + " attempt: " + attempts);

        long start = System.currentTimeMillis();
        world.getChunkAtAsyncUrgently(x, z).thenRun(() -> {
            if (plugin.isDebug()) plugin.getLogger().info("loaded chunk at x: " + x + " z: " + z + " in " + (System.currentTimeMillis()-start) + "ms");

            Block block = world.getBlockAt(x, y, z);
            if (isSafe(block)) {
                future.complete(block.getLocation());
                if (plugin.isDebug()) plugin.getLogger().info("block is safe, completed the future. attempt: " + attempts);
                return;
            }
            if (y <= rtpLayout.getzRange().getMin()) {
                if (plugin.isDebug()) plugin.getLogger().info("all y levels from " + rtpLayout.getyRange() + " is not safe at x: " + x + " z: " + z);

                int attempt = attempts + 1;
                if (attempt >= rtpLayout.getMaxAttempts()) {
                    if (plugin.isDebug()) plugin.getLogger().info("attemps maxed out " + attempt + " / " + rtpLayout.getMaxAttempts());
                    return;
                }
                int newX = rtpLayout.getxRange().next();
                int newY = rtpLayout.getyRange().getMax() + 1;
                int newZ = rtpLayout.getzRange().next();
                completeRTPFutureByIterateFromTop(future, world, rtpLayout, attempt, newX, newY, newZ);
                return;
            }

            completeRTPFutureByIterateFromTop(future, world, rtpLayout, attempts, x, y-1, z);
        });
    }

    private void completeRTPFutureByIterateFromBottom(CompletableFuture<Location> future, World world, RTPLayout rtpLayout, int attempts, int x, int y, int z) {
        if (plugin.isDebug()) plugin.getLogger().info("loading chunk at x: " + x + " y: " + y + " z: " + z + " attempt: " + attempts);

        long start = System.currentTimeMillis();
        world.getChunkAtAsyncUrgently(x, z).thenRun(() -> {
            if (plugin.isDebug()) plugin.getLogger().info("loaded chunk at x: " + x + " z: " + z + " in " + (System.currentTimeMillis()-start) + "ms");

            Block block = world.getBlockAt(x, y, z);
            if (isSafe(block)) {
                future.complete(block.getLocation());
                if (plugin.isDebug()) plugin.getLogger().info("block is safe, completed the future. attempt: " + attempts);
                return;
            }
            if (y >= rtpLayout.getzRange().getMax()) {
                if (plugin.isDebug()) plugin.getLogger().info("all y levels from " + rtpLayout.getyRange() + " is not safe at x: " + x + " z: " + z);

                int attempt = attempts + 1;
                if (attempt >= rtpLayout.getMaxAttempts()) {
                    if (plugin.isDebug()) plugin.getLogger().info("attemps maxed out " + attempt + " / " + rtpLayout.getMaxAttempts());
                    return;
                }
                int newX = rtpLayout.getxRange().next();
                int newY = rtpLayout.getyRange().getMin() - 1;
                int newZ = rtpLayout.getzRange().next();
                completeRTPFutureByIterateFromBottom(future, world, rtpLayout, attempt, newX, newY, newZ);
                return;
            }

            completeRTPFutureByIterateFromBottom(future, world, rtpLayout, attempts, x, y+1, z);
        });
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
