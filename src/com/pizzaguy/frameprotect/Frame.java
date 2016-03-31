package com.pizzaguy.frameprotect;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Frame {
    private int x, y, z;
    private UUID owner;

    public Frame(int x, int y, int z, UUID owner) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }
    
    public OfflinePlayer getPlayer(){
        return Bukkit.getOfflinePlayer(owner);
    }

}
