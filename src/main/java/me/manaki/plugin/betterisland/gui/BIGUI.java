package me.manaki.plugin.betterisland.gui;

import com.google.common.collect.Lists;
import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.border.Borders;
import me.manaki.plugin.betterisland.util.BIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.BentoBox;

import java.util.List;
import java.util.Objects;

public class BIGUI {

    private final static int ICON_SLOT = 4;

    public static void open(Player p) {
        double bankValue = 0;
        var island = BentoBox.getInstance().getIslandsManager().getIsland(p.getWorld(), p.getUniqueId());
        if (island == null) bankValue = 0;
        else bankValue = BIUtils.getBankBalance(island);

        var inv = Bukkit.createInventory(new BIHolder(), 9, "§0§lNÂNG CẤP ĐẢO (Bank: §f§l" + bankValue + "$§0§l)");
        p.openInventory(inv);

        Bukkit.getScheduler().runTaskAsynchronously(BetterIsland.get(), () -> {
            for (int i = 0 ; i < 9 ; i++) inv.setItem(i, getBackIcon());
            inv.setItem(ICON_SLOT, getIslandIcon(p));
        });
    }

    public static void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof BIHolder)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;
        if (e.getSlot() != ICON_SLOT) return;

        var p = (Player) e.getWhoClicked();

        // Check island
        var island = BentoBox.getInstance().getIslandsManager().getIsland(p.getWorld(), p.getUniqueId());
        if (island == null) return;

        // Check if owner
        if (!Objects.equals(island.getOwner(), p.getUniqueId())) {
            p.sendMessage("§cChỉ chủ đảo mới có thể nâng cấp!");
            return;
        }

        var ic = BIUtils.getLevelConfig(island);
        var nextic = BetterIsland.get().getPluginConfig().getNextLevel(ic.getLevel());

        // Check bank money
        if (!BIUtils.pay(island, nextic.getMoneyRequired())) {
            p.sendMessage("§cKhông đủ, yêu cầu §f§l" + nextic.getMoneyRequired() + "$ §cđể nâng cấp!");
            return;
        }

        // Noti
        p.sendMessage("§aNâng cấp thành công!");
        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);

        // Set
        BIUtils.setLevel(island, nextic.getLevel());
        Bukkit.getScheduler().runTask(BetterIsland.get(), () -> {
            open(p);
        });
        Bukkit.getScheduler().runTaskAsynchronously(BetterIsland.get(), () -> {
            Borders.check(BetterIsland.get(), p);
        });
    }

    public static ItemStack getBackIcon() {
        var is = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        var im = is.getItemMeta();
        im.setDisplayName("§f");
        is.setItemMeta(im);
        return is;
    }

    private static ItemStack getIslandIcon(Player p) {
        var island = BentoBox.getInstance().getIslandsManager().getIsland(p.getWorld(), p.getUniqueId());
        if (island == null) return new ItemStack(Material.AIR);

        var is = new ItemStack(Material.ANVIL);
        var im = is.getItemMeta();
        var ic = BIUtils.getLevelConfig(island);

        im.setDisplayName("§a§lĐảo cấp §2§l#" + ic.getLevel());
        List<String> lore = Lists.newArrayList();
        lore.add("§6Diện tích: §f" + ic.getBorderSize());
        lore.add("§6Sethome tối đa: §f" + ic.getMaxHome());
        lore.add("");
        if (BetterIsland.get().getPluginConfig().isMaxLevel(ic.getLevel())) lore.add("§bCấp đảo tối đa!");
        else {
            var nextic = BetterIsland.get().getPluginConfig().getNextLevel(ic.getLevel());
            lore.add("§3§lNâng cấp §2§l#" + nextic.getLevel());
            lore.add("§bChi phí: §f§l" + nextic.getMoneyRequired() + "$ §f(trong /is bank)");
            lore.add("§bDiện tích mới: §f" + nextic.getBorderSize());
            lore.add("§bSethome tối đa mới: §f" + nextic.getMaxHome());
            lore.add("");
            lore.add("§eCLICK để nâng cấp");
        }
        im.setLore(lore);
        is.setItemMeta(im);

        return is;
    }

}

class BIHolder implements InventoryHolder {

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }

}
