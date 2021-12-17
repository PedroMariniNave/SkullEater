package com.zpedroo.skulleater.listeners;

import com.zpedroo.skulleater.SkullEater;
import com.zpedroo.skulleater.managers.DataManager;
import com.zpedroo.skulleater.managers.InventoryManager;
import com.zpedroo.skulleater.objects.PlayerData;
import com.zpedroo.skulleater.objects.ShopItem;
import com.zpedroo.skulleater.utils.config.Messages;
import com.zpedroo.skulleater.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerChatListener implements Listener {

    private static HashMap<Player, PlayerChat> playerChat;

    static {
        playerChat = new HashMap<>(32);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!playerChat.containsKey(event.getPlayer())) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerChat playerChat = getPlayerChat().remove(player);
        Integer amount = null;

        try {
            amount = Integer.parseInt(event.getMessage());
        } catch (Exception ex) {
            // ignore
        }

        if (amount == null || amount <= 0) {
            player.sendMessage(Messages.INVALID_AMOUNT);
            return;
        }

        ShopItem item = playerChat.getItem();
        int limit = item.getDefaultDisplay().getMaxStackSize() == 1 ? 36 : 2304;
        if (amount > limit) amount = limit;

        Integer freeSpace = InventoryManager.getInstance().getFreeSpace(player, item.getDefaultDisplay());
        if (freeSpace < amount) {
            player.sendMessage(StringUtils.replaceEach(Messages.NEED_SPACE, new String[]{
                    "{has}",
                    "{need}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(freeSpace.doubleValue()),
                    NumberFormatter.getInstance().formatDecimal(amount.doubleValue())
            }));
            return;
        }

        PlayerData data = DataManager.getInstance().load(player);

        Integer skulls = data.getSkulls();
        Integer price = item.getPrice() * amount;

        if (skulls < price) {
            player.sendMessage(StringUtils.replaceEach(Messages.INSUFFICIENT_SKULLS, new String[]{
                    "{has}",
                    "{need}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(skulls.doubleValue()),
                    NumberFormatter.getInstance().formatDecimal(price.doubleValue())
            }));
            return;
        }

        data.removeSkulls(price);
        if (item.getShopItem() != null) {
            ItemStack toGive = item.getShopItem().clone();
            if (toGive.getMaxStackSize() == 64) {
                toGive.setAmount(amount);
                player.getInventory().addItem(toGive);
                return;
            }

            for (int i = 0; i < amount; ++i) {
                player.getInventory().addItem(toGive);
            }
        }

        for (String cmd : item.getCommands()) {
            if (cmd == null) continue;

            final Integer finalAmount = amount;
            SkullEater.get().getServer().getScheduler().runTaskLater(SkullEater.get(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(cmd, new String[]{
                    "{player}",
                    "{amount}"
            }, new String[]{
                    player.getName(),
                    String.valueOf(finalAmount * item.getDefaultAmount())
            })), 0L);
        }

        for (String msg : Messages.SUCCESSFUL_PURCHASED) {
            if (msg == null) continue;

            player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                    "{item}",
                    "{amount}",
                    "{price}"
            }, new String[]{
                    item.getDefaultDisplay().hasItemMeta() ? item.getDefaultDisplay().getItemMeta().hasDisplayName() ? item.getDefaultDisplay().getItemMeta().getDisplayName() : item.getDefaultDisplay().getType().toString() : item.getDefaultDisplay().getType().toString(),
                    NumberFormatter.getInstance().formatDecimal(amount.doubleValue()),
                    NumberFormatter.getInstance().formatDecimal(price.doubleValue())
            }));
        }

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 100f);
    }

    public static HashMap<Player, PlayerChat> getPlayerChat() {
        return playerChat;
    }

    public static class PlayerChat {

        private Player player;
        private ShopItem item;

        public PlayerChat(Player player, ShopItem item) {
            this.player = player;
            this.item = item;
        }

        public Player getPlayer() {
            return player;
        }

        public ShopItem getItem() {
            return item;
        }
    }
}