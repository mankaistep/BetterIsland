package me.manaki.plugin.betterisland.config;

public class IslandConfig {

    private final int moneyRequired;
    private final int level;
    private final int borderSize;
    private final int maxHome;
    private final int maxAnimal;

    public IslandConfig(int level, int moneyRequired, int borderSize, int maxHome, int maxAnimal) {
        this.moneyRequired = moneyRequired;
        this.level = level;
        this.borderSize = borderSize;
        this.maxHome = maxHome;
        this.maxAnimal = maxAnimal;
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

    public int getMaxAnimal() {
        return maxAnimal;
    }
}
