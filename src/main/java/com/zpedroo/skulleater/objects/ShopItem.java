package com.zpedroo.skulleater.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopItem {

    private Integer price;
    private Integer defaultAmount;
    private Long requiredLevel;
    private ItemStack defaultDisplay;
    private ItemStack lockedDisplay;
    private ItemStack shopItem;
    private List<String> commands;

    public ShopItem(Integer price, Integer defaultAmount, Long requiredLevel, ItemStack defaultDisplay, ItemStack lockedDisplay, ItemStack shopItem, List<String> commands) {
        this.price = price;
        this.defaultAmount = defaultAmount;
        this.requiredLevel = requiredLevel;
        this.defaultDisplay = defaultDisplay;
        this.lockedDisplay = lockedDisplay;
        this.shopItem = shopItem;
        this.commands = commands;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getDefaultAmount() {
        return defaultAmount;
    }

    public Long getRequiredLevel() {
        return requiredLevel;
    }

    public ItemStack getDefaultDisplay() {
        return defaultDisplay.clone();
    }

    public ItemStack getLockedDisplay() {
        return lockedDisplay.clone();
    }

    public ItemStack getShopItem() {
        return shopItem;
    }

    public List<String> getCommands() {
        return commands;
    }
}