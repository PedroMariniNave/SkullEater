package com.zpedroo.skulleater.utils.menus;

import com.zpedroo.onlinetime.api.OnlineTimeAPI;
import com.zpedroo.skulleater.listeners.PlayerChatListener;
import com.zpedroo.skulleater.managers.DataManager;
import com.zpedroo.skulleater.objects.PlayerData;
import com.zpedroo.skulleater.objects.ShopItem;
import com.zpedroo.skulleater.utils.FileUtils;
import com.zpedroo.skulleater.utils.builder.InventoryBuilder;
import com.zpedroo.skulleater.utils.builder.InventoryUtils;
import com.zpedroo.skulleater.utils.builder.ItemBuilder;
import com.zpedroo.skulleater.utils.config.Messages;
import com.zpedroo.skulleater.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Menus extends InventoryUtils {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    private ItemStack nextPageItem;
    private ItemStack previousPageItem;

    public Menus() {
        instance = this;
        this.nextPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Next-Page").build();
        this.previousPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Previous-Page").build();
    }

    public void openMainMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.MAIN;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        PlayerData data = DataManager.getInstance().load(player);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{player}",
                    "{skulls}",
                    "{level}"
            }, new String[]{
                    player.getName(),
                    NumberFormatter.getInstance().formatDecimal(data.getSkulls().doubleValue()),
                    NumberFormatter.getInstance().formatDecimal(OnlineTimeAPI.getLevel(player).doubleValue()),
            }).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");
            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            inventory.addItem(item, slot, () -> {
                switch (action.toUpperCase()) {
                    case "SHOP":
                        openShopMenu(player);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openShopMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.SHOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        int nextPageSlot = FileUtils.get().getInt(file, "Inventory.next-page-slot");
        int previousPageSlot = FileUtils.get().getInt(file, "Inventory.previous-page-slot");

        InventoryBuilder inventory = new InventoryBuilder(title, size, previousPageItem, previousPageSlot, nextPageItem, nextPageSlot);

        List<ShopItem> shopItems = DataManager.getInstance().getCache().getShopItems();

        int i = -1;
        String[] slots = FileUtils.get().getString(file, "Inventory.item-slots").replace(" ", "").split(",");
        for (ShopItem shopItem : shopItems) {
            if (shopItem == null) continue;
            if (++i >= slots.length) i = 0;

            ItemStack display = OnlineTimeAPI.getLevel(player) >= shopItem.getRequiredLevel() ? shopItem.getDefaultDisplay() : shopItem.getLockedDisplay();

            int slot = Integer.parseInt(slots[i]);

            inventory.addItem(display, slot, () -> {
                if (OnlineTimeAPI.getLevel(player) < shopItem.getRequiredLevel()) return;

                player.closeInventory();
                PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(player, shopItem));
                for (String msg : Messages.CHOOSE_AMOUNT) {
                    if (msg == null) continue;

                    player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                            "{item}",
                            "{price}"
                    }, new String[]{
                            display.hasItemMeta() ? display.getItemMeta().hasDisplayName() ? display.getItemMeta().getDisplayName() : display.getType().toString() : display.getType().toString(),
                            NumberFormatter.getInstance().formatDecimal(shopItem.getPrice().doubleValue())
                    }));
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }
}