package me.manaki.plugin.betterisland.config;

public class IslandConfig {

    private final int moneyRequired;
    private final int level;
    private final int borderSize;
    private final int maxHome;

    public IslandConfig(int level, int moneyRequired, int borderSize, int maxHome) {
        this.moneyRequired = moneyRequired;
        this.level = level;
        this.borderSize = borderSize;
        this.maxHome = maxHome;
    }

    public int getMoneyRequired() {
        return moneyRequired;
    }

    public int getLevel() {
        return level;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public int getMaxHome() {
        return maxHome;
    }
}
