package me.manaki.plugin.betterisland.config;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.LinkedHashMap;

public class PluginConfig {

    private final String world;
    private final LinkedHashMap<Integer, IslandConfig> isLevels;

    public PluginConfig(Plugin plugin) {
        var config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));

        this.world = config.getString("world");
        isLevels = Maps.newLinkedHashMap();
        int i = 0;
        do {
            var path = "island-level." + i;
            int money = config.getInt(path + ".money-required");
            int border = config.getInt(path + ".data.border");
            int home = config.getInt(path + ".data.home");
            int animal = config.getInt(path  + ".data.animal");

            isLevels.put(i, new IslandConfig(i, money, border, home, animal));

            i++;
        }
        while (config.contains("island-level." + i));
    }

    public String getWorld() {
        return world;
    }

    public LinkedHashMap<Integer, IslandConfig> getIsLevels() {
        return isLevels;
    }

    public boolean isMaxLevel(int lv) {
        return lv >= this.isLevels.size() - 1;
    }

    public IslandConfig getNextLevel(int lv) {
        if (isMaxLevel(lv)) return null;
        return isLevels.get(lv + 1);
    }

    public World getBukkitWorld() {
        return Bukkit.getWorld(this.world);
    }
}
