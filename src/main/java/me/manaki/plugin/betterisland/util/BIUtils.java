package me.manaki.plugin.betterisland.util;

import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.config.IslandConfig;
import org.bukkit.Bukkit;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.database.objects.Island;

public class BIUtils {

    private static final String KEY = "betterisland-level";

    public static int getLevel(Island island) {
        if (island.getMetaData().isPresent()) {
            var data = island.getMetaData().get();
            if (data.containsKey(KEY)) return data.get(KEY).asInt();
            else {
                setLevel(island, 0);
                return 0;
            }
        }
        return 0;
    }

    public static IslandConfig getLevelConfig(Island is) {
        var lv = getLevel(is);
        return BetterIsland.get().getPluginConfig().getIsLevels().getOrDefault(lv, null);
    }

    /*
     #   VISITOR   = 0
     #   COOP      = 200
     #   TRUSTED   = 400
     #   MEMBER    = 500
     #   SUB-OWNER = 900
     #   OWNER     = 1000
     */

    public static void setLevel(Island is, int lv) {
        if (!is.getMetaData().isPresent()) return;

        // Save data
        var data = is.getMetaData().get();
        data.put(KEY, new MetaDataValue(lv));

        var ic = BetterIsland.get().getPluginConfig().getIsLevels().getOrDefault(lv, null);
        if (ic == null) return;

        setIsland(is, ic.getMaxHome());
    }

    public static void setIsland(Island is, int maxHomes) {
        // Set max home
        var im = BentoBox.getInstance().getIslandsManager();
        im.setMaxHomes(is, maxHomes);

        // Set MEMBER
        im.setMaxMembers(is, 500, 15);

        // OTHER RANKS
        im.setMaxMembers(is, 0, 15);
        im.setMaxMembers(is, 200, 15);
        im.setMaxMembers(is, 400, 15);
        im.setMaxMembers(is, 900, 15);
        im.setMaxMembers(is, 1000, 1);

        is.setMaxEverProtectionRange(180);
        is.setProtectionRange(180);

        im.save(is);
    }

    public static double getBankBalance(Island is) {
        if (!BentoBox.getInstance().getAddonsManager().getAddonByName("Bank").isPresent()) return 0;

        try {
            var bank = BentoBox.getInstance().getAddonsManager().getAddonByName("Bank").get();
            var getBankManager = bank.getClass().getMethod("getBankManager");

            var bankManager = getBankManager.invoke(bank);
            var getBalance = bankManager.getClass().getMethod("getBalance", Island.class);

            var balance = getBalance.invoke(bankManager, is);
            return (double) balance.getClass().getMethod("getValue").invoke(balance);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return 0;
    }

    public static boolean pay(Island is, double value) {
        double own = getBankBalance(is);
        if (own < value) return false;
        var name = Bukkit.getOfflinePlayer(is.getOwner()).getName();
        var cmd = "bsbadmin bank take " + name + " " + value;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

        return true;
    }

}
