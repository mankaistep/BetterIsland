package me.manaki.plugin.betterisland;

import me.manaki.plugin.betterisland.command.BICommand;
import me.manaki.plugin.betterisland.data.BIDatas;
import me.manaki.plugin.betterisland.listener.BIListener;
import me.manaki.plugin.betterisland.upgrade.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class BetterIsland extends JavaPlugin {

    @Override
    public void onEnable() {
        // Config
        this.reloadConfig();

        // Listener
        Bukkit.getPluginManager().registerEvents(new BIListener(), this);

        // Command
        this.getCommand("islandupgrade").setExecutor(new BICommand());
    }

    @Override
    public void onDisable() {
        BIDatas.saveAll();
    }

    @Override
    public void reloadConfig() {
        this.saveDefaultConfig();
        var config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
        Upgrades.reload(config);
    }


    public static BetterIsland get() {
        return JavaPlugin.getPlugin(BetterIsland.class);
    }
}
