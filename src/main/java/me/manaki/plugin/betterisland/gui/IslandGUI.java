package me.manaki.plugin.betterisland.gui;

import com.google.common.collect.Lists;
import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.border.Borders;
import me.manaki.plugin.betterisland.money.MoneyAPI;
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

public class IslandGUI {

    private static final int DEL_REQUIRE = 10000;

    private static final int CREATE_BUTTON = 4;

    private static final int GO_BUTTON = 0;
    private static final int HELP_BUTTON = 2;
    private static final int UPGRADE_BUTTON = 4;
    private static final int DELETE_BUTTON = 8;
    private static final int MARKET_BUTTON = 6;

    public static void open(Player p) {
        var w = BetterIsland.get().getPluginConfig().getBukkitWorld();
        double bankValue = 0;
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        var island = BentoBox.getInstance().getIslandsManager().getIsland(w, p.getUniqueId());
        if (island == null) bankValue = 0;
        else bankValue = BIUtils.getBankBalance(island);

        // Has no island
        if (island == null) {
            var inv = Bukkit.createInventory(new IsGUIHolder(false), 9, "§0§lBẠN CHƯA CÓ ĐẢO, TẠO 1 CÁI CHỨ?");
            p.openInventory(inv);

            Bukkit.getScheduler().runTaskAsynchronously(BetterIsland.get(), () -> {
               for (int i = 0 ; i < 9 ; i++) inv.setItem(i, BIGUI.getBackIcon());
               inv.setItem(CREATE_BUTTON, getCreateButton());
            });
        }

        // Already have
        else {
            var inv = Bukkit.createInventory(new IsGUIHolder(true), 9, "§0§lISLAND MENU (Bank: §f§l" + bankValue + "$§0§l)");
            p.openInventory(inv);

            Bukkit.getScheduler().runTaskAsynchronously(BetterIsland.get(), () -> {
                for (int i = 0 ; i < 9 ; i++) inv.setItem(i, BIGUI.getBackIcon());
                inv.setItem(GO_BUTTON, getGoButton());
                inv.setItem(HELP_BUTTON, getHelpButton());
                inv.setItem(UPGRADE_BUTTON, getUpgradeButton(p));
                inv.setItem(DELETE_BUTTON, getDelButton());
                inv.setItem(MARKET_BUTTON, getMarketButton());
            });
        }
    }

    public static void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof IsGUIHolder)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;

        var p = (Player) e.getWhoClicked();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        var holder = (IsGUIHolder) e.getInventory().getHolder();
        int slot = e.getSlot();

        // Create
        if (!holder.hasIsland()) {
            if (slot == CREATE_BUTTON) {
                p.closeInventory();
                Bukkit.getScheduler().runTask(BetterIsland.get(), () -> {
                    Bukkit.dispatchCommand(p, "is create");
                });
            }
            return;
        }

        // Main menu
        if (slot == GO_BUTTON) {
            p.closeInventory();
            Bukkit.getScheduler().runTask(BetterIsland.get(), () -> {
                Bukkit.dispatchCommand(p, "is go");
            });
        }
        else if (slot == HELP_BUTTON) {
            p.closeInventory();
            Bukkit.getScheduler().runTask(BetterIsland.get(), () -> {
                Bukkit.dispatchCommand(p, "is help");
            });
        }
        else if (slot == UPGRADE_BUTTON) {
            upgradeClick(p);
        }
        else if (slot == DELETE_BUTTON) {
            if (!MoneyAPI.getEco().has(p, DEL_REQUIRE)) {
                p.sendMessage("§cKhông đủ, yêu cầu §f§l" + DEL_REQUIRE + "$ §cđể xóa đảo!");
            } else  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dm open xoadaoonconfirm " + p.getName());
        }
        else if (slot == MARKET_BUTTON) {
            Bukkit.dispatchCommand(p, "market");
        }

    }

    private static void upgradeClick(Player p) {
        // Check island
        var island = BentoBox.getInstance().getIslandsManager().getIsland(BetterIsland.get().getPluginConfig().getBukkitWorld(), p.getUniqueId());
        if (island == null) return;

        // Check if owner
        if (!Objects.equals(island.getOwner(), p.getUniqueId())) {
            p.sendMessage("§cChỉ chủ đảo mới có thể nâng cấp!");
            return;
        }

        var ic = BIUtils.getLevelConfig(island);
        var nextic = BetterIsland.get().getPluginConfig().getNextLevel(ic.getLevel());

        if (BetterIsland.get().getPluginConfig().isMaxLevel(ic.getLevel())) {
            return;
        }

        // Check bank money
        if (!BIUtils.pay(island, nextic.getMoneyRequired())) {
            p.sendMessage("§cKhông đủ, yêu cầu §f§l" + nextic.getMoneyRequired() + "$ §ctrong §f/island bank §cđể nâng cấp!");
            return;
        }

        // Noti
        p.sendMessage("§a§l§oNâng cấp thành công!");
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

    private static ItemStack getCreateButton() {
        var is = new ItemStack(Material.GRASS_BLOCK);
        var im = is.getItemMeta();
        im.setDisplayName("§e§lCLICK §a§lTạo một đảo mới");
        is.setItemMeta(im);

        return is;
    }

    private static ItemStack getGoButton() {
        var is = new ItemStack(Material.GRASS_BLOCK);
        var im = is.getItemMeta();
        im.setDisplayName("§e§lCLICK §a§lVề đảo của bạn");
        is.setItemMeta(im);
        is.setLore(Lists.newArrayList("§7§o/is go"));

        return is;
    }

    private static ItemStack getHelpButton() {
        var is = new ItemStack(Material.BOOK);
        var im = is.getItemMeta();
        im.setDisplayName("§e§lCLICK §a§lXem các lệnh");
        is.setItemMeta(im);
        is.setLore(Lists.newArrayList("§7§o/is help"));

        return is;
    }

    private static ItemStack getUpgradeButton(Player p) {
        var island = BentoBox.getInstance().getIslandsManager().getIsland(BetterIsland.get().getPluginConfig().getBukkitWorld(), p.getUniqueId());
        if (island == null) return new ItemStack(Material.AIR);

        var is = new ItemStack(Material.ANVIL);
        var im = is.getItemMeta();
        var ic = BIUtils.getLevelConfig(island);

        im.setDisplayName("§a§lĐảo cấp Lv." + ic.getLevel());
        List<String> lore = Lists.newArrayList();
        lore.add("§6Diện tích: §f" + ic.getBorderSize());
        lore.add("§6Sethome tối đa: §f" + ic.getMaxHome());
        lore.add("");
        if (BetterIsland.get().getPluginConfig().isMaxLevel(ic.getLevel())) lore.add("§bCấp đảo tối đa!");
        else {
            var nextic = BetterIsland.get().getPluginConfig().getNextLevel(ic.getLevel());
            lore.add("§3§lNâng cấp lên Lv." + nextic.getLevel());
            lore.add("§bChi phí: §f§l" + nextic.getMoneyRequired() + "$ §f(trong /is bank)");
            lore.add("§bDiện tích mới: §f" + nextic.getBorderSize());
            lore.add("§bSethome tối đa mới: §f" + nextic.getMaxHome());
            lore.add("");
            lore.add("§e§lCLICK để nâng cấp");
        }
        im.setLore(lore);
        is.setItemMeta(im);

        return is;
    }

    private static ItemStack getDelButton() {
        var is = new ItemStack(Material.BARRIER);
        var im = is.getItemMeta();
        im.setDisplayName("§e§lCLICK §c§lXóa đảo");
        is.setItemMeta(im);
        is.setLore(Lists.newArrayList("§fTiêu tốn §f§l" + DEL_REQUIRE + "$"));

        return is;
    }

    private static ItemStack getMarketButton() {
        var is = new ItemStack(Material.EMERALD);
        var im = is.getItemMeta();
        im.setDisplayName("§e§lCLICK §a§lThương lái");
        is.setItemMeta(im);
        is.setLore(Lists.newArrayList("§f§oLà nơi bán những mặt hàng bạn có để kiếm §f§l$", "§7§o/market"));

        return is;
    }


}

class IsGUIHolder implements InventoryHolder {

    private final boolean hasIsland;

    public IsGUIHolder(boolean hasIsland) {
        this.hasIsland = hasIsland;
    }

    public boolean hasIsland() {
        return hasIsland;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
