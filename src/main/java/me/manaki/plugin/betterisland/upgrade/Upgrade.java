package me.manaki.plugin.betterisland.upgrade;

public class Upgrade {

    private String id;
    private UpgradeType type;
    private String next;
    private int amount;
    private double price;

    public Upgrade(String id, UpgradeType type, String next, int amount, double price) {
        this.id = id;
        this.type = type;
        this.next = next;
        this.amount = amount;
        this.price = price;
    }

    public String getID() {
        return id;
    }

    public UpgradeType getType() {
        return type;
    }

    public String getNext() {
        return next;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }
}
