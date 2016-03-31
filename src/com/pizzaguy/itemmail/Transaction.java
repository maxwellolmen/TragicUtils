package com.pizzaguy.itemmail;

import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class Transaction {
    private int id;
    private UUID sender;
    private UUID receiver;
    private ItemStack[] items;

    public Transaction(int id, UUID sender, UUID receiver, ItemStack[] items) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.items = items;
    }

    public void giveItems(HumanEntity player) {
        giveItems(player, items);
    }

    public static void giveItems(HumanEntity player, ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null)
                if (player.getInventory().firstEmpty() < 0) {
                    player.getWorld().dropItemNaturally(player.getLocation(), items[i]);
                } else {
                    player.getInventory().addItem(items[i]);
                }
        }
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

}
