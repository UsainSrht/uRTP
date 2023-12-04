package me.usainsrht.urtp;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import me.usainsrht.urtp.command.CommandHandler;
import me.usainsrht.urtp.command.RTPCommand;
import me.usainsrht.urtp.config.MainConfig;
import me.usainsrht.urtp.config.RTPConfig;
import me.usainsrht.urtp.manager.RTPManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class URTP extends JavaPlugin {

    private static URTP instance;
    private RTPManager rtpManager;
    private Commodore commodore;
    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        rtpManager = new RTPManager(this);

        commodore = CommodoreProvider.getCommodore(this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        //todo shutdown datamanager
    }

    public void reload() {
        reloadConfig();
        loadConfig();
    }

    public void loadConfig() {
        saveDefaultConfig();
        MainConfig.create(getConfig());
        RTPConfig.create(getConfig().getConfigurationSection("worlds"));
    }

    public void registerCommands() {
        RTPCommand rtpCommand = new RTPCommand(MainConfig.getCmdName(), MainConfig.getCmdDesc(), MainConfig.getCmdUsage(), MainConfig.getCmdAliases());
        CommandHandler.register("urtp", rtpCommand);
        commodore.register(rtpCommand, rtpCommand.getCommodoreCommand());
    }

    public Commodore getCommodore() {
        return commodore;
    }

    public RTPManager getRtpManager() {
        return rtpManager;
    }

    public static URTP getInstance() {
        return instance;
    }

}
