package com.pizzaguy.frameprotect;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;

import net.md_5.bungee.api.ChatColor;

public class FrameProtectListener implements Listener {

    private FrameProtectSql fpSql;

    public FrameProtectListener(FrameProtectSql fpSql) {
        this.fpSql = fpSql;
    }

    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent e) {
        if (e.getEntity().getType() != EntityType.ITEM_FRAME || !e.getPlayer().isSneaking())
            return;
        Location location = e.getEntity().getLocation();
        Frame frame = new Frame(location.getBlockX(), location.getBlockY(), location.getBlockZ(), e.getPlayer().getUniqueId());
        if (!fpSql.hasFrame(frame)) {
            fpSql.addFrame(frame);
            e.getPlayer().sendMessage(ChatColor.GOLD + "Protected frame placed.");
        }
    }

    @EventHandler
    public void onHangingBreakEvent(HangingBreakEvent e) {
        if (e.getEntity().getType() != EntityType.ITEM_FRAME)
            return;
        Location location = e.getEntity().getLocation();
        Frame frame = new Frame(location.getBlockX(), location.getBlockY(), location.getBlockZ(), null);
        if (fpSql.hasFrame(frame) && e.getCause() != RemoveCause.ENTITY)
            e.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreakEvent(HangingBreakByEntityEvent e) {
        Location location = e.getEntity().getLocation();
        if (e.getEntity().getType() != EntityType.ITEM_FRAME || !fpSql.hasFrame(new Frame(location.getBlockX(), location.getBlockY(), location.getBlockZ(), null)))
            return;
        Frame target = fpSql.getFrame(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (target.getOwner().equals(e.getRemover().getUniqueId()) || e.getRemover().hasPermission("frameprotect.admin")) {
            e.getRemover().sendMessage(ChatColor.GOLD + "Protected frame removed.");
            fpSql.removeFrame(target);
            return;
        } else {
            e.getRemover().sendMessage(ChatColor.RED + "You cannot do this, this ItemFrame is owned by "+ target.getPlayer().getName() );
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Location location = e.getEntity().getLocation();
        if (e.getEntity().getType() != EntityType.ITEM_FRAME || !fpSql.hasFrame(new Frame(location.getBlockX(), location.getBlockY(), location.getBlockZ(), null)))
            return;
        Frame target = fpSql.getFrame(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if(!target.getOwner().equals(e.getDamager().getUniqueId()) && !e.getDamager().hasPermission("frameprotect.admin")){
            e.getDamager().sendMessage(ChatColor.RED + "You cannot do this, this ItemFrame is owned by "+ target.getPlayer().getName() );
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
        Location location =  e.getRightClicked().getLocation();
        if (e.getRightClicked().getType() != EntityType.ITEM_FRAME || !fpSql.hasFrame(new Frame(location.getBlockX(), location.getBlockY(), location.getBlockZ(), null)))
            return;
        Frame target = fpSql.getFrame(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if(!target.getOwner().equals(e.getPlayer().getUniqueId()) && !e.getPlayer().hasPermission("frameprotect.admin")){
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot do this, this ItemFrame is owned by "+ target.getPlayer().getName() );
            e.setCancelled(true);
        }
    }
}
