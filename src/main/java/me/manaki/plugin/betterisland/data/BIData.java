package me.manaki.plugin.betterisland.data;

import me.manaki.plugin.betterisland.upgrade.UpgradeType;

import java.util.Map;

public class BIData {

    private String playerName;
    private Map<UpgradeType, String> upgrades;

    public BIData(String playerName, Map<UpgradeType, String> upgrades) {
        this.playerName = playerName;
        this.upgrades = upgrades;

        // Add default
        for (UpgradeType ut : UpgradeType.values()) {
            if (!this.upgrades.containsKey(ut)) this.upgrades.put(ut, "default");
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

    public void setUpgrade(UpgradeType type, String upgrade) {
        this.upgrades.put(type, upgrade);
    }

}
