package me.usainsrht.urtp;

import org.bukkit.plugin.java.JavaPlugin;

public final class URTP extends JavaPlugin {

    private static URTP instance;
    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

    public static URTP getInstance() {
        return instance;
    }

}
