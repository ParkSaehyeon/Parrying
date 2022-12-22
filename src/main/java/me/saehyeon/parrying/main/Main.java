package me.saehyeon.parrying.main;

import me.saehyeon.parrying.event.onClick;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new onClick(), this);
    }

    @Override
    public void onDisable() {

    }
}
