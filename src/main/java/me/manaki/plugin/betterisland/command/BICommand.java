package me.manaki.plugin.betterisland.command;

import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.border.Borders;
import me.manaki.plugin.betterisland.gui.IslandGUI;
import me.manaki.plugin.betterisland.util.BIUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

public class BICommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length == 0) {
            Player player = (Player) sender;
            IslandGUI.open(player);
            return false;
        }

        if (!sender.hasPermission("betterisland.admin")) return false;

        if (args[0].equalsIgnoreCase("reload")) {
            BetterIsland.get().reloadConfig();
            sender.sendMessage("§aAll fucking done!");
        }

        else if (args[0].equalsIgnoreCase("bordertoggle")) {
            Player player = Bukkit.getPlayer(args[1]);
            Borders.toggle(player);
        }

        else if (args[0].equalsIgnoreCase("clearanimal")) {
            int sum = 0;
            var islist = BentoBox.getInstance().getIslandsManager().getIslands();
            for (Island is : islist) {
                var center = is.getCenter();
                var range = is.getProtectionRange();
                var isconfig = BIUtils.getLevelConfig(is);

                int c = 0;
                for (Entity entity : is.getWorld().getNearbyEntities(center, range, 200, range)) {
                    if (entity instanceof Animals) {
                        if (c > isconfig.getMaxAnimal()) {
                            Bukkit.getScheduler().runTask(BetterIsland.get(), entity::remove);
                            sum++;
                        } else c++;
                    }
                }
            }

            sender.sendMessage("§aCleared §c" + sum + " animals");
        }

        return false;
    }

}
