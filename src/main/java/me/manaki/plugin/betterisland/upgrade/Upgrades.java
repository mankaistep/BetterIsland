package me.manaki.plugin.betterisland.upgrade;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.Set;

public class Upgrades {

    private static String world;
    private static Set<String> allowedEntities;
    private static Map<String, Upgrade> upgrades = Maps.newHashMap();

    public static void reload(FileConfiguration config) {
        world = config.getString("world", "bskyblock_world");
        allowedEntities = Sets.newHashSet(config.getStringList("allowed-entities"));

        upgrades.clear();
        for (UpgradeType type : UpgradeType.values()) {
            String path = type.name().toLowerCase() + "-upgrade";
            for (String id : config.getConfigurationSection(path).getKeys(false)) {
                String next = config.getString(path + "." + id + ".next");
                int amount = config.getInt(path + "." + id + ".amount");
                double price = config.getDouble(path + "." + id + ".price");
                Upgrade u = new Upgrade(id, type, next, amount, price);
                upgrades.put(id, u);
            }
        }
    }

    public static String getDefault(UpgradeType type) {
        return "default-" + type.name().toLowerCase();
    }

    public static Upgrade get(String id) {
        return upgrades.getOrDefault(id, null);
    }

    public static Set<String> getAllowedEntities() {
        return allowedEntities;
    }

    public static World getWorld() {
        return Bukkit.getWorld(world);
    }

}
