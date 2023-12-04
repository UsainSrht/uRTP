package me.usainsrht.urtp.rtp;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class RTPLayout {

    private RTPMethod method;
    private int maxAttempts;
    private Range xRange;
    private Range yRange;
    private Range zRange;
    private BiomeFilterMode bfMode;
    private Set<Biome> bfList;

    public RTPLayout(ConfigurationSection config) {
        this.method = RTPMethod.valueOf(config.getString("method"));
        this.maxAttempts = config.getInt("max_attempts");
        this.xRange = new Range(config.getInt("range.x.min"), config.getInt("range.x.max"));
        this.yRange = new Range(config.getInt("range.y.min"), config.getInt("range.y.max"));
        this.zRange = new Range(config.getInt("range.z.min"), config.getInt("range.z.max"));
        this.bfMode = BiomeFilterMode.valueOf(config.getString("biome_filter.mode"));
        this.bfList = new HashSet<>();
        config.getStringList("biome_filter.list").forEach(key -> bfList.add(Biome.valueOf(key)));
    }

    public boolean isBiomeAcceptable(Biome biome) {
        if (bfList.isEmpty()) return true;
        return (bfMode == BiomeFilterMode.WHITELIST) == bfList.contains(biome);
    }

    public int getMaxAttempts() {
        return maxAttempts;
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

}
