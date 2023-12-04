package me.usainsrht.urtp;

import me.usainsrht.urtp.config.MainConfig;
import me.usainsrht.urtp.config.RTPConfig;
import me.usainsrht.urtp.manager.RTPManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class URTP extends JavaPlugin {

    private static URTP instance;
    private RTPManager rtpManager;
    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        rtpManager = new RTPManager(this);
    }

    @Override
    public void onDisable() {

    }

    public void reload() {
        reloadConfig();
        loadConfig();
    }

    public void loadConfig() {
        MainConfig.create(getConfig());
        RTPConfig.create(getConfig());
    }

    public RTPManager getRtpManager() {
        return rtpManager;
    }

    public static URTP getInstance() {
        return instance;
    }

}
