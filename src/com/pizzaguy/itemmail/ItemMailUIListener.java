package com.pizzaguy.itemmail;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class ItemMailUIListener implements Listener {

    private ItemMailSql imSql;

    public ItemMailUIListener(ItemMailSql imSql) {
        this.imSql = imSql;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getSize() != ItemMailUI.SIZE)
            return;
        if (e.getCurrentItem() == null)
            return;
        if (e.getCurrentItem().equals(ItemMailUI.EXIT))
            e.getWhoClicked().closeInventory();

        if (e.getCurrentItem().equals(ItemMailUI.ACCEPT) && e.getClickedInventory().getTitle().equals(ItemMailUI.MAIL)) {
            int id = Integer.parseInt(e.getInventory().getItem(8).getItemMeta().getLore().get(0));
            imSql.getTransaction(id).giveItems(e.getWhoClicked());
            imSql.deleteTransaction(id);
            e.getWhoClicked().closeInventory();
        }
        if (e.getCurrentItem().equals(ItemMailUI.SEND) && e.getClickedInventory().getTitle().equals(ItemMailUI.CONCEPT)) {
            ItemStack[] items = ItemMailUI.getConceptItems(e.getClickedInventory());
            boolean contains = false;
            for (ItemStack item : items)
                if (item != null)
                    if (item.getType() != Material.AIR)
                        contains = true;
            if (contains) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(((SkullMeta) e.getClickedInventory().getItem(8).getItemMeta()).getOwner());
                if(target.getPlayer() != null)
                    target.getPlayer().sendMessage(ChatColor.GREEN + "" + e.getWhoClicked().getName() + ChatColor.GOLD + " has send you an ItemMail." );
                imSql.addTransaction(new Transaction(0, e.getWhoClicked().getUniqueId(), target.getUniqueId(), items));

                e.getClickedInventory().clear();
            } else {
                e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot send an empty ItemMail.");
            }
            e.getWhoClicked().closeInventory();
        }
        if (e.getCurrentItem().equals(ItemMailUI.NEXT) && e.getClickedInventory().getTitle().equals(ItemMailUI.INBOX)) {
            int id = Integer.parseInt(e.getInventory().getItem(4).getItemMeta().getLore().get(0)) + 1;
            e.getWhoClicked().openInventory(ItemMailUI.getInbox(imSql.getTransactions(e.getWhoClicked().getUniqueId()), id));
        }
        if (e.getCurrentItem().equals(ItemMailUI.BACK) && e.getClickedInventory().getTitle().equals(ItemMailUI.INBOX)) {
            int id = Integer.parseInt(e.getInventory().getItem(4).getItemMeta().getLore().get(0)) - 1;
            if (id < 1)
                id = 1;
            e.getWhoClicked().openInventory(ItemMailUI.getInbox(imSql.getTransactions(e.getWhoClicked().getUniqueId()), id));
        }

        if (e.getClickedInventory().getTitle().equals(ItemMailUI.CONCEPT))
            if (e.getSlot() < 9)
                e.setCancelled(true);

        if (e.getClickedInventory().getTitle().equals(ItemMailUI.INBOX))
            if (e.getCurrentItem() != null)
                if (e.getCurrentItem().getType() == Material.SKULL_ITEM)
                    e.getWhoClicked().openInventory(ItemMailUI.getMail(imSql.getTransaction(Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(0)))));

        if (e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equals(ItemMailUI.INBOX) || e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equals(ItemMailUI.MAIL)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMoveItemEvent(InventoryMoveItemEvent e) {
        if (e.getDestination().getTitle().equals(ItemMailUI.INBOX) || e.getDestination().getTitle().equals(ItemMailUI.MAIL))
            e.setCancelled(true);
    }

    @EventHandler
    public void onCloseEvent(InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equals(ItemMailUI.CONCEPT) && e.getInventory().getSize() == ItemMailUI.SIZE) {
            Transaction.giveItems(e.getPlayer(), ItemMailUI.getConceptItems(e.getInventory()));
        }
    }

}
