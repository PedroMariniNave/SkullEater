package com.zpedroo.skulleater.commands;

import com.zpedroo.skulleater.managers.DataManager;
import com.zpedroo.skulleater.objects.PlayerData;
import com.zpedroo.skulleater.utils.menus.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkullEaterCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;

        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[1]);
            int amount = 0;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (Exception ex) {
                // ignore
            }
            switch (args[0].toUpperCase()) {
                case "GIVE":
                    if (!sender.hasPermission("skulleater.admin")) break;
                    if (target == null || amount <= 0) break;

                    PlayerData targetData = DataManager.getInstance().load(target);
                    if (targetData == null) break;

                    targetData.addSkulls(amount);
                    return true;
                case "ITEM":
                    if (!sender.hasPermission("skulleater.admin")) break;
                    if (target == null || amount <= 0) break;

                    ItemStack item = DataManager.getInstance().getCache().getSkullItem();
                    item.setAmount(amount);

                    target.getInventory().addItem(item);
                    return true;
            }
        }

        if (player == null) return true;

        Menus.getInstance().openMainMenu(player);
        return false;
    }
}