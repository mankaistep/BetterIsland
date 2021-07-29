package me.manaki.plugin.betterisland.listener;

import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.border.Borders;
import me.manaki.plugin.betterisland.gui.IslandGUI;
import me.manaki.plugin.betterisland.util.BIUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import world.bentobox.bentobox.BentoBox;

public class BIListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/is") || e.getMessage().equalsIgnoreCase("/island")) {
            e.setCancelled(true);
            IslandGUI.open(e.getPlayer());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        IslandGUI.onClick(e);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(BetterIsland.get(), () -> {
            Borders.check(BetterIsland.get(), player);
        }, 10);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(BetterIsland.get(), () -> {
            Borders.check(BetterIsland.get(), player);
        }, 10);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var im = BentoBox.getInstance().getIslandsManager();

        var iso = im.getIslandAt(e.getBlock().getLocation());
        if (!iso.isPresent()) return;

        var is = iso.get();

        int dx = Math.abs(e.getBlock().getX() - is.getCenter().getBlockX());
        int dz = Math.abs(e.getBlock().getZ() - is.getCenter().getBlockZ());

        int r = BIUtils.getLevelConfig(is).getBorderSize();

        if (dx > (r / 2) || dz > (r / 2)) {
            e.setCancelled(true);
            p.sendMessage("§cNâng cấp đảo để đặt xa hơn §c§l(/is -> Nâng cấp)");
        }
    }


    @EventHandler
    public void onBucketBug(PlayerBucketFillEvent e) {
        if (e.getBlock().getType().name().contains("BUBBLE")) {
            e.getPlayer().sendMessage("§cNếu bạn muốn múc nước/lava hãy ra chỗ nào không có không khí nước");
            e.setCancelled(true);
        }
    }

}
