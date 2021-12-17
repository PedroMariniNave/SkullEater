package com.zpedroo.skulleater.managers;

import com.zpedroo.skulleater.managers.cache.DataCache;
import com.zpedroo.skulleater.mysql.DBConnection;
import com.zpedroo.skulleater.objects.PlayerData;
import com.zpedroo.skulleater.objects.ShopItem;
import com.zpedroo.skulleater.utils.FileUtils;
import com.zpedroo.skulleater.utils.builder.ItemBuilder;
import com.zpedroo.skulleater.utils.formatter.NumberFormatter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;

public class DataManager {

    private static DataManager instance;
    public static DataManager getInstance() { return instance; }

    private DataCache dataCache;

    public DataManager() {
        instance = this;
        this.dataCache = new DataCache();
        this.loadShopItems();
    }

    public PlayerData load(Player player) {
        PlayerData data = dataCache.getPlayerData().get(player);
        if (data == null) {
            data = DBConnection.getInstance().getDBManager().loadData(player);
            dataCache.getPlayerData().put(player, data);
        }

        return data;
    }

    public void save(Player player) {
        PlayerData data = dataCache.getPlayerData().get(player);
        if (data == null) return;
        if (!data.isQueueUpdate()) return;

        DBConnection.getInstance().getDBManager().saveData(data);
        data.setUpdate(false);
    }

    public void saveAll() {
        new HashSet<>(dataCache.getPlayerData().keySet()).forEach(this::save);
    }

    private void loadShopItems() {
        FileUtils.Files file = FileUtils.Files.SHOP;
        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            if (str == null) continue;

            Integer price = FileUtils.get().getInt(file, "Inventory.items." + str + ".price");
            Integer defaultAmount = FileUtils.get().getInt(file, "Inventory.items." + str + ".default-amount", 1);
            Long requiredLevel = FileUtils.get().getLong(file, "Inventory.items." + str + ".required-level");
            ItemStack lockedDisplay = null;
            if (FileUtils.get().getFile(file).get().contains("Inventory.items." + str + ".locked")) {
                lockedDisplay = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str + ".locked", new String[]{
                        "{price}",
                        "{required_level}"
                }, new String[]{
                        NumberFormatter.getInstance().formatDecimal(price.doubleValue()),
                        NumberFormatter.getInstance().formatDecimal(requiredLevel.doubleValue())
                }).build();
            }
            ItemStack defaultDisplay = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str + ".display", new String[]{
                    "{price}",
                    "{required_level}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(price.doubleValue()),
                    NumberFormatter.getInstance().formatDecimal(requiredLevel.doubleValue())
            }).build();
            ItemStack shopItem = null;
            if (FileUtils.get().getFile(file).get().contains("Inventory.items." + str + ".shop-item")) {
                shopItem = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str + ".shop-item").build();
            }
            List<String> commands = FileUtils.get().getStringList(file, "Inventory.items." + str + ".commands");

            dataCache.getShopItems().add(new ShopItem(price, defaultAmount, requiredLevel, defaultDisplay, lockedDisplay, shopItem, commands));
        }
    }

    public DataCache getCache() {
        return dataCache;
    }
}