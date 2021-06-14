package me.manaki.plugin.betterisland.border;

import com.google.common.collect.Sets;
import me.manaki.plugin.betterisland.data.BIDatas;
import me.manaki.plugin.betterisland.upgrade.UpgradeType;
import me.manaki.plugin.betterisland.upgrade.Upgrades;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.WorldBorder;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import world.bentobox.bentobox.BentoBox;

import java.util.Set;

public class Borders {

    public static Set<String> borderOn = Sets.newHashSet();

    public static void toggle(Player player) {
        if (borderOn.contains(player.getName())) {
            borderOn.remove(player.getName());
            player.sendMessage("§aĐã tắt hoạt ảnh giới hạn, thoát ra vào lại island để cập nhật thay đổi");
        }
        else {
            borderOn.add(player.getName());
            sendBorder(player);
            player.sendMessage("§aBật hoạt ảnh giới hạn!");
        }
    }

    public static void check(Plugin plugin, Player p) {
        sendBorder(p);
    }

    public static void sendBorder(Player p) {
        if (!borderOn.contains(p.getName())) return;

        var im = BentoBox.getInstance().getIslandsManager();
        var pm = BentoBox.getInstance().getPlayersManager();

        var is = im.getIsland(p.getWorld(), pm.getUser(p.getUniqueId()));
        if (is == null) return;
        if (is.getWorld() != p.getWorld()) return;

        int x = is.getCenter().getBlockX();
        int z = is.getCenter().getBlockZ();

        int radius = Upgrades.get(BIDatas.get(p).getUprade(UpgradeType.BORDER)).getAmount();

        WorldBorder wb = new WorldBorder();
        wb.world = ((CraftWorld) p.getWorld()).getHandle();
        wb.setCenter(x, z);
        wb.setSize(radius);
        wb.setWarningDistance(0);
        EntityPlayer player = ((CraftPlayer) p).getHandle();
        wb.world = (WorldServer) player.world;
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));

    }

}
