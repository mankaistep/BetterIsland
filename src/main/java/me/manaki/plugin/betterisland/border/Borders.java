package me.manaki.plugin.betterisland.border;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import me.manaki.plugin.betterisland.data.BIDatas;
import me.manaki.plugin.betterisland.upgrade.UpgradeType;
import me.manaki.plugin.betterisland.upgrade.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import world.bentobox.bentobox.BentoBox;

public class Borders {

    public static void check(Plugin plugin, Player p, boolean async) {
        if (!async) sendBorder(p);
        else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                sendBorder(p);;
            });
        }
    }

    public static void sendBorder(Player p) {
        var im = BentoBox.getInstance().getIslandsManager();
        var pm = BentoBox.getInstance().getPlayersManager();

        var is = im.getIsland(p.getWorld(), pm.getUser(p.getUniqueId()));
        if (is == null) return;
        if (is.getWorld() != p.getWorld()) return;

        int x = is.getCenter().getBlockX();
        int z = is.getCenter().getBlockZ();

        int radius = Upgrades.get(BIDatas.get(p).getUprade(UpgradeType.BORDER)).getAmount();
        BorderAPI.getApi().setBorder(p, radius, new Location(p.getWorld(), x, 0, z));
    }

}
