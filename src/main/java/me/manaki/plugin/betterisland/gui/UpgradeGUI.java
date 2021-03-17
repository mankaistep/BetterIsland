package me.manaki.plugin.betterisland.gui;

import com.google.common.collect.Lists;
import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.border.Borders;
import me.manaki.plugin.betterisland.data.BIData;
import me.manaki.plugin.betterisland.data.BIDatas;
import me.manaki.plugin.betterisland.money.MoneyAPI;
import me.manaki.plugin.betterisland.upgrade.Upgrade;
import me.manaki.plugin.betterisland.upgrade.UpgradeType;
import me.manaki.plugin.betterisland.upgrade.Upgrades;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UpgradeGUI {

    public static void open(Player player) {
        BIData data = BIDatas.get(player);
        Inventory inv = Bukkit.createInventory(new UGUIHolder(data), 9, "§0§lNÂNG CẤP ĐẢO");
        player.openInventory(inv);

        Bukkit.getScheduler().runTaskAsynchronously(BetterIsland.get(), () -> {
            for (UpgradeType type : data.getUpgrades().keySet()) {
                String u = data.getUpgrades().get(type);
                var is = getIcon(u, type);
                inv.setItem(type.getGUISlot(), is);
            }
        });
    }

    public static void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof UGUIHolder) e.setCancelled(true);
        if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;

        int slot = e.getSlot();
        var player = (Player) e.getWhoClicked();
        for (UpgradeType type : UpgradeType.values()) {
            if (type.getGUISlot() == slot) {
                // Check max
                BIData data = BIDatas.get(player);
                String upgrade = data.getUprade(type);
                Upgrade next = Upgrades.get(Upgrades.get(upgrade).getNext());
                var max =  next == null;
                if (max) {
                    player.sendMessage("§cNâng cấp tối đa");
                    return;
                }

                // Price
                double price = next.getPrice();
                if (!MoneyAPI.moneyCost(player, price)) {
                    player.sendMessage("§cBạn cần có đủ " + price + "$ để nâng cấp!");
                    return;
                }

                // Upgrade
                data.setUpgrade(type, next.getID());

                // Reopen GUI
                open(player);
            }
        }
     }

    public static void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof UGUIHolder) e.setCancelled(true);
    }

    public static void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof UGUIHolder)) return;
        var holder = (UGUIHolder) e.getInventory().getHolder();
        var player = (Player) e.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(BetterIsland.get(), () -> {
            holder.getData().save();
        });
        Bukkit.getScheduler().runTask(BetterIsland.get(), () -> {
            Borders.check(BetterIsland.get(), player);
        });
    }

    public static ItemStack getIcon(String upgrade, UpgradeType type) {
        boolean max = Upgrades.get(Upgrades.get(upgrade).getNext()) == null;
        var is = new ItemStack(type.getIcon());
        Upgrade u =  Upgrades.get(upgrade);
        var meta = is.getItemMeta();
        var from = u.getAmount();

        meta.setDisplayName("§a§lNâng cấp " + type.getName());
        List<String> lore = Lists.newArrayList();
        if (max) {
            lore.add("§fCấp độ nâng cấp tối đa");
            lore.add("§f" + type.getName() + ": §7" + from);
        }
        else {
            var to = Upgrades.get(Upgrades.get(upgrade).getNext()).getAmount();
            lore.add("§f" + type.getName() + ": §7" + from + " >> " + to);
            lore.add("§fGiá: §c" + u.getPrice() + "$ §f§o(Click để nâng cấp)");
        }
        meta.setLore(lore);
        is.setItemMeta(meta);

        return is;
    }

}

class UGUIHolder implements InventoryHolder {

    private BIData data;
    private boolean changed;

    public UGUIHolder(BIData data) {
        this.data = data;
    }

    public BIData getData() {
        return data;
    }

    public void setChanged(boolean value) {
        this.changed = value;
    }

    public boolean isChanged() {
        return this.changed;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
