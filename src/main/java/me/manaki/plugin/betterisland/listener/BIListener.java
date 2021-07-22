package me.manaki.plugin.betterisland.listener;

import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.border.Borders;
import me.manaki.plugin.betterisland.data.BIData;
import me.manaki.plugin.betterisland.data.BIDatas;
import me.manaki.plugin.betterisland.gui.UpgradeGUI;
import me.manaki.plugin.betterisland.upgrade.Upgrade;
import me.manaki.plugin.betterisland.upgrade.UpgradeType;
import me.manaki.plugin.betterisland.upgrade.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.postgresql.core.Utils;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandCreatedEvent;
import world.bentobox.bentobox.api.events.island.IslandEnterEvent;
import world.bentobox.bentobox.api.events.island.IslandResettedEvent;
import world.bentobox.bentobox.api.events.team.TeamInviteEvent;
import world.bentobox.bentobox.api.events.team.TeamKickEvent;
import world.bentobox.bentobox.api.events.team.TeamLeaveEvent;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Objects;
import java.util.UUID;

public class BIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        UpgradeGUI.onClick(e);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        UpgradeGUI.onDrag(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        UpgradeGUI.onClose(e);
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
    public void onAnimalSpawn(EntitySpawnEvent e) {
        var entity = e.getEntity();
        if (!Upgrades.getAllowedEntities().contains(entity.getType().name())) return;

        // Check
        var l = entity.getLocation();
        var im = BentoBox.getInstance().getIslandsManager();
        var is = im.getIslandAt(l).isPresent() ? im.getIslandAt(l).get() : null;
        if (is == null) return;

        // Get max
        var op = Bukkit.getOfflinePlayer(is.getOwner());
        var name = op.getName();
        BIData data = BIDatas.get(name);
        int max = Upgrades.get(data.getUprade(UpgradeType.ANIMAL)).getAmount();

        // Count
        int c = 0;
        for (Entity nearE : is.getCenter().getNearbyEntities(200, 200, 200)) {
            if (Upgrades.getAllowedEntities().contains(nearE.getType().name())) c++;
            if (c >= max) {
                e.setCancelled(true);
                return;
            }
        }
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

        int r = Upgrades.get(BIDatas.get(p).getUprade(UpgradeType.BORDER)).getAmount();

        if (dx > (r / 2) || dz > (r / 2)) {
            e.setCancelled(true);
            p.sendMessage("§cNâng cấp đảo để đặt xa hơn");
        }
    }

    @EventHandler
    public void onIslandCreatedEvent(IslandCreatedEvent e) {
        var is = e.getIsland();
        var op = Bukkit.getOfflinePlayer(Objects.requireNonNull(is.getOwner()));
        var name = op.getName();

        Bukkit.getScheduler().runTaskLater(BetterIsland.get(), () -> {
            BIData data = BIDatas.get(name);
            data.save();
            BetterIsland.get().getLogger().info("Saved island data " + is.getOwner());
        }, 20);
    }

    @EventHandler
    public void onIslandEnterEvent(IslandEnterEvent e) {
        var is = e.getIsland();
        var op = Bukkit.getOfflinePlayer(Objects.requireNonNull(is.getOwner()));
        var name = op.getName();

        Bukkit.getScheduler().runTaskLater(BetterIsland.get(), () -> {
            BIData data = BIDatas.get(name);
            data.save();
            BetterIsland.get().getLogger().info("Saved island data " + is.getOwner());
        }, 20);
    }

    @EventHandler
    public void onIslandResetteddEvent(TeamInviteEvent e) {
        var is = e.getIsland();
        var op = Bukkit.getOfflinePlayer(Objects.requireNonNull(is.getOwner()));
        var name = op.getName();
        BIData data = BIDatas.get(name);

        int currentSize = is.getMemberSet().size();
        if (currentSize >= Upgrades.get(data.getUprade(UpgradeType.MEMBER)).getAmount()) {
            e.setCancelled(true);
            for (UUID uuid : is.getMemberSet()) {
                var mem = Bukkit.getOfflinePlayer(uuid);
                if (mem.isOnline()) {
                    mem.getPlayer().sendMessage("§cNâng cấp đảo để có thể mời thêm người mới");
                }
            }
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
