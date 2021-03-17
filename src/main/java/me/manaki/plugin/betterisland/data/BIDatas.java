package me.manaki.plugin.betterisland.data;

import com.google.common.collect.Maps;
import mk.plugin.playerdata.storage.PlayerData;
import mk.plugin.playerdata.storage.PlayerDataAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class BIDatas {

    private final static Map<String, BIData> dataMap = Maps.newHashMap();

    private final static String KEY = "data";
    private final static String HOOK = "betterisland";

    public static BIData get(Player player) {
        return get(player.getName());
    }

    public static BIData get(String name) {
        if (dataMap.containsKey(name)) return dataMap.get(name);
        var pd = PlayerDataAPI.get(name, HOOK);
        if (!pd.hasData(KEY)) {
            BIData data = new BIData(name);
            dataMap.put(name, data);
            return data;
        }
        BIData data = BIData.parse(pd.getValue(KEY));
        dataMap.put(name, data);
        return data;
    }

    public static void save(String name, BIData data) {
        var pd = PlayerDataAPI.get(name, HOOK);
        pd.set(KEY, data.toString());
        pd.save();
    }

    public static void saveAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            save(player.getName(), get(player));
        }
    }

}
