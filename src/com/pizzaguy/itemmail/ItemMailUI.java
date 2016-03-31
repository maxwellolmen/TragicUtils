package com.pizzaguy.itemmail;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.pizzaguy.serialization.builder.ItemStackBuilder;

import net.md_5.bungee.api.ChatColor;

public class ItemMailUI {

    public final static String INBOX = "Inbox";
    public final static String MAIL = "Mail";
    public final static String CONCEPT = "Concept";
    public final static String INVENTORY = "container.inventory";

    public final static ItemStack EXIT = new ItemStackBuilder().setType(Material.BARRIER).setAmount(1).setDisplayName(ChatColor.RED + "Exit").build();
    public final static ItemStack ACCEPT = new ItemStackBuilder().setType(Material.BOOK).setAmount(1).setDisplayName(ChatColor.DARK_GREEN + "Accept").build();
    public final static ItemStack SEND = new ItemStackBuilder().setType(Material.BOOK_AND_QUILL).setAmount(1).setDisplayName(ChatColor.DARK_GREEN + "Send").build();

    public final static ItemStack NEXT = new ItemStackBuilder().setType(Material.FEATHER).setAmount(1).setDisplayName(ChatColor.AQUA + "Next").build();
    public final static ItemStack BACK = new ItemStackBuilder().setType(Material.FEATHER).setAmount(1).setDisplayName(ChatColor.AQUA + "Back").build();

    public final static int SIZE = 5 * 9;

    public static Inventory getConcept(OfflinePlayer player) {
        Inventory inv = Bukkit.createInventory(null, SIZE, CONCEPT);
        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
        ItemMeta fillermeta = filler.getItemMeta();
        fillermeta.setDisplayName(" ");
        filler.setItemMeta(fillermeta);
        ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta skullmeta = (SkullMeta) playerhead.getItemMeta();
        skullmeta.setDisplayName(ChatColor.GREEN + "Mail to: " + ChatColor.BLUE + player.getName());
        skullmeta.setOwner(player.getName());
        playerhead.setItemMeta(skullmeta);

        int row = 9;
        for (int i = 0; i < row; i++)
            inv.setItem(i, filler);
        inv.setItem(0, EXIT);
        inv.setItem(4, SEND);
        inv.setItem(8, playerhead);
        return inv;
    }

    public static ItemStack[] getConceptItems(Inventory inv) {
        if (inv.getTitle().equals(CONCEPT)) {
            ItemStack[] items = new ItemStack[ItemMailUI.SIZE - 9];
            for (int i = 9; i < ItemMailUI.SIZE; i++)
                items[i - 9] = inv.getItem(i);
            return items;
        }
        return null;
    }

    public static Inventory getInbox(List<Transaction> transactions) {
        return getInbox(transactions, 1);
    }

    public static Inventory getInbox(List<Transaction> transactions, int id) {
        Inventory inv = Bukkit.createInventory(null, SIZE, INBOX);
        List<String> loreid = new ArrayList<String>();
        loreid.add(id + "");
        ItemStack page = new ItemStackBuilder().setType(Material.BOOK).setAmount(1).setDisplayName(ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Page").setLore(loreid).build();
        inv.setItem(0, EXIT);
        inv.setItem(3, BACK);
        inv.setItem(4, page);
        inv.setItem(5, NEXT);
        int row = 9;
        for (int i = row; i < SIZE; i++) {
            int current = i - row + (id - 1) * 4 * 9;
            if (!(current < transactions.size()))
                break;
            Transaction transaction = transactions.get(current);
            OfflinePlayer sender = Bukkit.getOfflinePlayer(transaction.getSender());
            ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta skullmeta = (SkullMeta) playerhead.getItemMeta();
            List<String> lore = new ArrayList<String>();
            lore.add(transaction.getId() + "");
            skullmeta.setLore(lore);
            skullmeta.setDisplayName(ChatColor.GREEN + "Mail from: " + ChatColor.BLUE + sender.getName());
            skullmeta.setOwner(sender.getName());
            playerhead.setItemMeta(skullmeta);
            inv.setItem(i, playerhead);
        }
        return inv;
    }

    public static Inventory getMail(Transaction transaction) {
        Inventory inv = Bukkit.createInventory(null, SIZE, MAIL);
        List<String> lore = new ArrayList<String>();
        lore.add("" + transaction.getId());
        inv.setItem(0, EXIT);
        inv.setItem(4, ACCEPT);
        inv.setItem(8, new ItemStackBuilder().setType(Material.PAPER).setAmount(1).setDisplayName(ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Transaction ID:").setLore(lore).build());
        int row = 9;
        for (int i = row; i < SIZE; i++) {
            inv.setItem(i, transaction.getItems()[i - row]);
        }
        return inv;
    }
}
