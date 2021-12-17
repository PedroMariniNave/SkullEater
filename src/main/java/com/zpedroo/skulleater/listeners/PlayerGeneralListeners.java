package com.zpedroo.skulleater.listeners;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.zpedroo.skulleater.managers.DataManager;
import com.zpedroo.skulleater.objects.PlayerData;
import com.zpedroo.skulleater.utils.config.Messages;
import com.zpedroo.skulleater.utils.config.Titles;
import com.zpedroo.skulleater.utils.formatter.NumberFormatter;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() == null) return;

        ItemStack item = DataManager.getInstance().getCache().getSkullItem();
        player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR)) return;

        ItemStack item = event.getItem().clone();
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("SkullEater")) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionQuery query = container.createQuery();
        if (query.testState(player.getLocation(), player, DefaultFlag.PVP)) {
            player.sendMessage(Messages.PVP_AREA);
            return;
        }

        PlayerData data = DataManager.getInstance().load(player);
        if (data == null) return;

        Integer amount = player.isSneaking() ? item.getAmount() : 1;

        item.setAmount(amount);
        player.getInventory().removeItem(item);

        data.addSkulls(amount);

        String[] titles = Titles.ITEM_ACTIVATED_TITLE;
        String title = StringUtils.replaceEach(titles[0], new String[] { "{amount}" }, new String[] { NumberFormatter.getInstance().formatDecimal(amount.doubleValue()) });
        String subtitle = StringUtils.replaceEach(titles[1], new String[] { "{amount}" }, new String[] { NumberFormatter.getInstance().formatDecimal(amount.doubleValue()) });

        player.sendTitle(title, subtitle);
        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 10f);
    }
}