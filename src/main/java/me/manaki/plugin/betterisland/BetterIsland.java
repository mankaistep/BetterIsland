package me.manaki.plugin.betterisland;

import me.manaki.plugin.betterisland.command.BICommand;
import me.manaki.plugin.betterisland.config.PluginConfig;
import me.manaki.plugin.betterisland.listener.BIListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import world.bentobox.bentobox.BentoBox;

public final class BetterIsland extends JavaPlugin {

    private PluginConfig config;

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
    public void reloadConfig() {
        this.saveDefaultConfig();
        this.config = new PluginConfig(this);
    }

    public PluginConfig getPluginConfig() {
        return config;
    }

    public static BetterIsland get() {
        return JavaPlugin.getPlugin(BetterIsland.class);
    }
}
