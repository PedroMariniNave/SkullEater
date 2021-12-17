package com.zpedroo.skulleater;

import com.zpedroo.skulleater.commands.SkullEaterCmd;
import com.zpedroo.skulleater.listeners.PlayerChatListener;
import com.zpedroo.skulleater.listeners.PlayerGeneralListeners;
import com.zpedroo.skulleater.managers.DataManager;
import com.zpedroo.skulleater.managers.InventoryManager;
import com.zpedroo.skulleater.mysql.DBConnection;
import com.zpedroo.skulleater.utils.FileUtils;
import com.zpedroo.skulleater.utils.formatter.NumberFormatter;
import com.zpedroo.skulleater.utils.menus.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

import static com.zpedroo.skulleater.utils.config.Settings.*;

public class SkullEater extends JavaPlugin {

    private static SkullEater instance;
    public static SkullEater get() { return instance; }

    public void onEnable() {
        instance = this;
        new FileUtils(this);

        if (!isMySQLEnabled(getConfig())) {
            getLogger().log(Level.SEVERE, "MySQL are disabled! You need to enable it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new DBConnection(getConfig());
        new NumberFormatter(getConfig());
        new DataManager();
        new InventoryManager();
        new Menus();

        registerListeners();
        registerCommand(COMMAND, ALIASES, new SkullEaterCmd());
    }

    public void onDisable() {
        if (!isMySQLEnabled(getConfig())) return;

        try {
            DataManager.getInstance().saveAll();
            DBConnection.getInstance().closeConnection();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An error occurred while trying to save data!");
            ex.printStackTrace();
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerGeneralListeners(), this);
    }

    private void registerCommand(String command, List<String> aliases, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCmd = constructor.newInstance(command, this);
            pluginCmd.setAliases(aliases);
            pluginCmd.setExecutor(executor);

            Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            commandMap.register(getName().toLowerCase(), pluginCmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean isMySQLEnabled(FileConfiguration file) {
        if (!file.contains("MySQL.enabled")) return false;

        return file.getBoolean("MySQL.enabled");
    }
}