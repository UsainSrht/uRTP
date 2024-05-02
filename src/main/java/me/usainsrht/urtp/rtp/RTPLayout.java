package me.usainsrht.urtp.rtp;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RTPLayout {

    private RTPMethod method;
    private int maxAttempts;
    private boolean limitToWorldborder;
    private Range xRange;
    private Range yRange;
    private Range zRange;
    private BiomeFilterMode bfMode;
    private Set<Biome> bfList;
    private HashMap<int[], HashMap<MaterialCondition, Boolean>> blockFilters;

    public RTPLayout(ConfigurationSection config) {
        this.method = RTPMethod.valueOf(config.getString("method"));
        this.maxAttempts = config.getInt("max_attempts");
        this.limitToWorldborder = config.getBoolean("range.limit_to_worldborder", true);
        this.xRange = new Range(config.getInt("range.x.min"), config.getInt("range.x.max"));
        this.yRange = new Range(config.getInt("range.y.min"), config.getInt("range.y.max"));
        this.zRange = new Range(config.getInt("range.z.min"), config.getInt("range.z.max"));
        this.bfMode = BiomeFilterMode.valueOf(config.getString("biome_filter.mode"));
        this.bfList = new HashSet<>();
        config.getStringList("biome_filter.list").forEach(key -> bfList.add(Biome.valueOf(key)));
        this.blockFilters = new HashMap<>();
        config.getConfigurationSection("block_filter").getKeys(false).forEach(offsets -> {
            String[] splitted = offsets.split(",");
            int[] offsetArray = new int[3];
            offsetArray[0] = Integer.parseInt(splitted[0]);
            offsetArray[1] = Integer.parseInt(splitted[1]);
            offsetArray[2] = Integer.parseInt(splitted[2]);
            HashMap<MaterialCondition, Boolean> conditions = new HashMap<>();
            config.getConfigurationSection("block_filter." + offsets).getKeys(false).forEach(condition -> {
                conditions.put(MaterialCondition.valueOf(condition), config.getBoolean("block_filter." + offsets + "." + condition));
            });
            blockFilters.put(offsetArray, conditions);
        });
    }

    public boolean isBlockValid(Block block) {
        return blockFilters.entrySet().stream().allMatch(entry -> {
            int[] offset = entry.getKey();
            HashMap<MaterialCondition, Boolean> conditions = entry.getValue();
            Block relative = block.getRelative(offset[0], offset[1], offset[2]);
            return conditions.entrySet().stream().allMatch(condEntry -> condEntry.getKey().getValue(relative) == condEntry.getValue());
        });
    }

    public boolean isBiomeValid(Biome biome) {
        if (bfList.isEmpty()) return true;
        return (bfMode == BiomeFilterMode.WHITELIST) == bfList.contains(biome);
    }

    public int getRandomX(World world) {
        if (isLimitToWorldborder()) {
            WorldBorder worldBorder = world.getWorldBorder();
            int borderMin = worldBorder.getCenter().getBlockX() - (int)worldBorder.getSize();
            int borderMax = worldBorder.getCenter().getBlockX() + (int)worldBorder.getSize();
            int min = Math.max(borderMin, getxRange().getMin());
            int max = Math.min(borderMax, getxRange().getMax());
            return new Range(min, max).next();
        }
        return getxRange().next();
    }

    public int getRandomZ(World world) {
        if (isLimitToWorldborder()) {
            WorldBorder worldBorder = world.getWorldBorder();
            int borderMin = worldBorder.getCenter().getBlockZ() - (int)worldBorder.getSize();
            int borderMax = worldBorder.getCenter().getBlockZ() + (int)worldBorder.getSize();
            int min = Math.max(borderMin, getzRange().getMin());
            int max = Math.min(borderMax, getzRange().getMax());
            return new Range(min, max).next();
        }
        return getzRange().next();
    }

    //getters

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public boolean isLimitToWorldborder() {
        return limitToWorldborder;
    }

    public Range getxRange() {
        return xRange;
    }

    public Range getyRange() {
        return yRange;
    }

    public Range getzRange() {
        return zRange;
    }

    public RTPMethod getMethod() {
        return method;
    }

    public BiomeFilterMode getBfMode() {
        return bfMode;
    }

    public Set<Biome> getBfList() {
        return bfList;
    }

    public HashMap<int[], HashMap<MaterialCondition, Boolean>> getBlockFilters() {
        return blockFilters;
    }
}
