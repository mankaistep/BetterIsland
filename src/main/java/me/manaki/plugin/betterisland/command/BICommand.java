package me.manaki.plugin.betterisland.command;

import me.manaki.plugin.betterisland.BetterIsland;
import me.manaki.plugin.betterisland.border.Borders;
import me.manaki.plugin.betterisland.gui.IslandGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        return false;
    }

}
