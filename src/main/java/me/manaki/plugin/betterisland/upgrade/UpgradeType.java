package me.manaki.plugin.betterisland.upgrade;

import org.bukkit.Material;

public enum UpgradeType {

    MEMBER("Thành viên", 2, Material.PLAYER_HEAD),
    BORDER("Kích thước đảo", 4, Material.WARPED_SIGN),
    ANIMAL("Giới hạn vật nuôi", 6, Material.CHICKEN_SPAWN_EGG);

    private String name;
    private int guiSlot;
    private Material icon;

    UpgradeType(String name, int guiSlot, Material icon) {
        this.name = name;
        this.guiSlot = guiSlot;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getGUISlot() {
        return guiSlot;
    }

    public Material getIcon() {
        return icon;
    }
}
