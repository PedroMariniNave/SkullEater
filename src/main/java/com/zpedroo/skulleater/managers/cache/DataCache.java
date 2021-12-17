package com.zpedroo.skulleater.managers.cache;

import com.zpedroo.skulleater.objects.PlayerData;
import com.zpedroo.skulleater.objects.ShopItem;
import com.zpedroo.skulleater.utils.FileUtils;
import com.zpedroo.skulleater.utils.builder.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DataCache {

    private Map<Player, PlayerData> playerData;
    private List<ShopItem> shopItems;
    private ItemStack skullItem;

    public DataCache() {
        this.playerData = new HashMap<>(64);
        this.shopItems = new ArrayList<>(16);
        this.skullItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Item").build();
    }

    public Map<Player, PlayerData> getPlayerData() {
        return playerData;
    }

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    public ItemStack getSkullItem() {
        NBTItem nbt = new NBTItem(skullItem.clone());
        nbt.addCompound("SkullEater");

        return nbt.getItem();
    }
}