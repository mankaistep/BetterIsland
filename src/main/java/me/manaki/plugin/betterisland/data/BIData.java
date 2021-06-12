package me.manaki.plugin.betterisland.data;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import me.manaki.plugin.betterisland.upgrade.UpgradeType;
import me.manaki.plugin.betterisland.upgrade.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Map;

public class BIData {

    private String playerName;
    private Map<UpgradeType, String> upgrades;

    public BIData(String playerName) {
        this.playerName = playerName;
        this.upgrades = Maps.newHashMap();
        for (UpgradeType ut : UpgradeType.values()) this.upgrades.put(ut, Upgrades.getDefault(ut));
    }

    public BIData(String playerName, Map<UpgradeType, String> upgrades) {
        this.playerName = playerName;
        this.upgrades = upgrades;

        // Add default
        for (UpgradeType ut : UpgradeType.values()) {
            if (!this.upgrades.containsKey(ut)) this.upgrades.put(ut, Upgrades.getDefault(ut));
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public Map<UpgradeType, String> getUpgrades() {
        return upgrades;
    }

    public String getUprade(UpgradeType type) {
        return upgrades.get(type);
    }

    public void setUpgrades(Map<UpgradeType, String> upgrades) {
        this.upgrades = upgrades;
    }

    /*
     #   VISITOR   = 0
     #   COOP      = 200
     #   TRUSTED   = 400
     #   MEMBER    = 500
     #   SUB-OWNER = 900
     #   OWNER     = 1000

     1 OWNER + (n - 1) MEMBER
     0 SUB-OWNER
     0 TRUSTED
     0 COOP
     10 VISITOR
     */
    public void setUpgrade(UpgradeType type, String upgrade) {
        this.upgrades.put(type, upgrade);
    }

    public void applyToIsland() {
        String u = getUprade(UpgradeType.MEMBER);
        Player player = Bukkit.getPlayer(this.playerName);
        var im = BentoBox.getInstance().getIslandsManager();
        var is = im.getIsland(player.getWorld(), player.getUniqueId());
        if (is == null) return;

        // Set MEMBER
        im.setMaxMembers(is, 500, Upgrades.get(u).getAmount() - 1);

        // OTHER RANKS
        im.setMaxMembers(is, 0, 10);
        im.setMaxMembers(is, 200, 0);
        im.setMaxMembers(is, 400, 0);
        im.setMaxMembers(is, 900, 0);
        im.setMaxMembers(is, 1000, 1);

        // Save
        im.save(is);
    }

    public void save() {
        this.applyToIsland();
        BIDatas.save(playerName, this);
    }

    @Override
    public String toString() {
        var gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static BIData parse(String s) {
        var gson = new GsonBuilder().create();
        return gson.fromJson(s, BIData.class);
    }

}
