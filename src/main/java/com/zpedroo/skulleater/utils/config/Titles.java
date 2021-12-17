package com.zpedroo.skulleater.utils.config;

import com.zpedroo.skulleater.utils.FileUtils;
import org.bukkit.ChatColor;

public class Titles {

    public static final String[] ITEM_ACTIVATED_TITLE = new String[] {
            getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.item-activated.title")),
            getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.item-activated.subtitle"))
    };

    private static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
