package me.saehyeon.parrying.event;

import me.saehyeon.parrying.main.Parrying;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class onClick implements Listener {
    @EventHandler
    void onClick(PlayerInteractEvent e) {

        if(e.getAction() == Action.LEFT_CLICK_AIR)
            new Parrying().tryParrying(e.getPlayer());

    }
}
