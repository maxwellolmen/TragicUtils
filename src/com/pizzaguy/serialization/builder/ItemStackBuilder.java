
package com.pizzaguy.serialization.builder;

import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import net.minecraft.server.v1_9_R1.NBTTagCompound;

public class ItemStackBuilder {
    // standart values
    private ItemStack item;
    private Material material;
    private int amount;
    private short durability;
    private byte data;
    // standart meta
    private String name;
    private Map<Enchantment, Integer> enchants;
    private List<String> lore;
    private List<ItemFlag> flags;
    // banner meta
    private DyeColor base;
    private List<Pattern> patterns;
    // book meta
    private String author;
    private String title;
    private List<String> pages;
    // stored enchantment meta
    private Map<Enchantment, Integer> storedEnchants;
    // Fireworks meta
    private int power;
    private List<FireworkEffect> effects;
    // leather dye color meta
    private Color color;
    // map meta
    boolean scaling;
    // potion meta
    private List<PotionEffect> potions;
    private PotionData potiondata;
    // skull meta
    private NBTTagCompound tag;

    // variables for keeping track for what is set and what isnt
    private boolean materialSet;
    private boolean amountSet;
    private boolean durabilitySet;
    private boolean dataSet;
    private boolean nameSet;
    private boolean enchantsSet;
    private boolean loreSet;
    private boolean flagsSet;
    private boolean bannerSet;
    private boolean bookSet;
    private boolean storedEnchantsSet;
    private boolean fireworkSet;
    private boolean leatherSet;
    private boolean mapSet;
    private boolean potionSet;
    private boolean skullSet;
    private boolean potiondataSet;

    // default constructor
    public ItemStackBuilder() {
        // defualt material is air or as treated in MC as Null
        item = new ItemStack(Material.AIR);
    }

    // build the itemstack
    public ItemStack build() {
        // return null if material is null
        if (material == null)
            return null;
        // set material and amount and materialdata
        item = new ItemStack(material, amount, data);
        // set durability
        item.setDurability(durability);

        // set specific itemmeta

        // bannermeta
        if (item.getItemMeta() instanceof BannerMeta) {
            // cast the itemmeta
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            // set basecolor
            if (base != null)
                meta.setBaseColor(base);
            // set basecolors
            if (patterns != null)
                meta.setPatterns(patterns);
            // set itemmeta
            item.setItemMeta(meta);
        }
        // book
        else if (item.getItemMeta() instanceof BookMeta) {
            // cast the itemmeta
            BookMeta meta = (BookMeta) item.getItemMeta();
            // set author
            if (author != null)
                meta.setAuthor(author);
            // set title
            if (title != null)
                meta.setTitle(title);
            // set pages
            if (pages != null)
                meta.setPages(pages);
            // set itemmeta
            item.setItemMeta(meta);
        }
        // enchantmetnstorage meta
        else if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
            // cast the itemmeta
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            // check if null
            if (storedEnchants != null)
                // go through the map
                for (Enchantment ench : storedEnchants.keySet())
                    // add the ecnhantsments to the itemmeta
                    meta.addStoredEnchant(ench, storedEnchants.get(ench).intValue(), true);
            // set ietmmeta
            item.setItemMeta(meta);
        }
        // firework meta
        else if (item.getItemMeta() instanceof FireworkMeta) {
            // cast the itemmeta
            FireworkMeta meta = (FireworkMeta) item.getItemMeta();
            // check if null
            if (effects != null)
                // set effects
                meta.addEffects(effects);
            // set null
            meta.setPower(power);
            // set itemmeta
            item.setItemMeta(meta);
        }
        // leatherarmor meta
        else if (item.getItemMeta() instanceof LeatherArmorMeta) {
            // cast the itemmeta
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            // check if null
            if (color != null)
                // set color
                meta.setColor(color);
            // set itemmeta
            item.setItemMeta(meta);
        }
        // map meta
        else if (item.getItemMeta() instanceof MapMeta) {
            // cast the itemmeta
            MapMeta meta = (MapMeta) item.getItemMeta();
            // set scaling
            meta.setScaling(scaling);
            // set itemmeta
            item.setItemMeta(meta);
        }
        // potionmeta
        else if (item.getItemMeta() instanceof PotionMeta) {
            // cast the itemmeta
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            //check if null
            if(potiondata != null)
                //set potiondata
                meta.setBasePotionData(potiondata);
            // check if null
            if (potions != null)
                // go through the effects
                for (PotionEffect effect : potions)
                    // add the effects to the itemmeta
                    meta.addCustomEffect(effect, true);
            // set the itemmeta
            item.setItemMeta(meta);
        }
        // skullmeta
        else if (item.getItemMeta() instanceof SkullMeta) {
            // check if null
            if (tag != null) {
                // get the Craftitemstack
                net.minecraft.server.v1_9_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
                // add the tag
                stack.setTag(tag);
                // convert it back to a bukkit copy
                item = CraftItemStack.asBukkitCopy(stack);
            }
        }
        // get itemmeta
        ItemMeta meta = item.getItemMeta();
        // set itemmeta
        meta.setDisplayName(name);
        // add enchants
        if (enchants != null)
            for (Enchantment enchant : enchants.keySet())
                meta.addEnchant(enchant, enchants.get(enchant), true);
        // set lore
        meta.setLore(lore);
        // set itemflags
        if (flags != null)
            for (ItemFlag flag : flags)
                meta.addItemFlags(flag);
        // add itemmeta to item
        item.setItemMeta(meta);
        // return the final item
        return item;
    }

    // set material
    public ItemStackBuilder setType(Material material) {
        this.material = material;
        this.materialSet = true;
        return this;
    }

    // set amount
    public ItemStackBuilder setAmount(int amount) {
        this.amount = amount;
        this.amountSet = true;
        return this;
    }

    // set durabilty
    public ItemStackBuilder setDurability(short durability) {
        this.durability = durability;
        this.durabilitySet = true;
        return this;
    }

    // set data
    public ItemStackBuilder setData(byte data) {
        this.data = data;
        this.dataSet = true;
        return this;
    }

    // set display name
    public ItemStackBuilder setDisplayName(String name) {
        this.name = name;
        this.nameSet = true;
        return this;
    }

    // set enchantments
    public ItemStackBuilder addEnchantments(Map<Enchantment, Integer> enchants) {
        this.enchants = enchants;
        this.enchantsSet = true;
        return this;
    }

    // set lore
    public ItemStackBuilder setLore(List<String> lore) {
        this.lore = lore;
        this.loreSet = true;
        return this;
    }

    // set flags
    public ItemStackBuilder setFlags(List<ItemFlag> flags) {
        this.flags = flags;
        this.flagsSet = true;
        return this;
    }

    // set bannerdata
    public ItemStackBuilder setBannerData(DyeColor base, List<Pattern> patterns) {
        this.base = base;
        this.patterns = patterns;
        this.bannerSet = true;
        return this;
    }

    // set bookdata
    public ItemStackBuilder setBookData(String author, String title, List<String> pages) {
        this.author = author;
        this.title = title;
        this.pages = pages;
        this.bookSet = true;
        return this;
    }

    // set storedenchantmetns
    public ItemStackBuilder setStoredEnchantments(Map<Enchantment, Integer> storedEnchants) {
        this.storedEnchants = storedEnchants;
        this.storedEnchantsSet = true;
        return this;
    }

    // set fireworkdata
    public ItemStackBuilder setFireworkData(int power, List<FireworkEffect> effects) {
        this.power = power;
        this.effects = effects;
        this.fireworkSet = true;
        return this;
    }

    // set leatherarmor color
    public ItemStackBuilder setLeatherArmorColor(Color color) {
        this.color = color;
        this.leatherSet = true;
        return this;
    }

    // set map scaling
    public ItemStackBuilder setMapScaling(boolean scaling) {
        this.scaling = scaling;
        this.mapSet = true;
        return this;
    }

    // set custom potions
    public ItemStackBuilder setCustomPotions(List<PotionEffect> potions) {
        this.potions = potions;
        this.potionSet = true;
        return this;
    }

    // set skull data
    public ItemStackBuilder setSkullData(NBTTagCompound tag) {
        this.tag = tag;
        this.skullSet = true;
        return this;
    }
    
    //set potiondata
    public ItemStackBuilder setPotionData(PotionData potiondata){
        this.potiondata = potiondata;
        this.potiondataSet = true;
        return this;
    }

    //// getters to check if something is set//////
    public boolean isMaterialSet() {
        return materialSet;
    }

    public boolean isAmountSet() {
        return amountSet;
    }

    public boolean isDurabilitySet() {
        return durabilitySet;
    }

    public boolean isNameSet() {
        return nameSet;
    }

    public boolean isEnchantsSet() {
        return enchantsSet;
    }

    public boolean isLoreSet() {
        return loreSet;
    }

    public boolean isFlagsSet() {
        return flagsSet;
    }

    public boolean isBannerSet() {
        return bannerSet;
    }

    public boolean isBookSet() {
        return bookSet;
    }

    public boolean isStoredEnchantsSet() {
        return storedEnchantsSet;
    }

    public boolean isFireworkSet() {
        return fireworkSet;
    }

    public boolean isLeatherSet() {
        return leatherSet;
    }

    public boolean isMapSet() {
        return mapSet;
    }

    public boolean isPotionSet() {
        return potionSet;
    }

    public boolean isSkullSet() {
        return skullSet;
    }

    public boolean isDataSet() {
        return dataSet;
    }

    public boolean isPotiondataSet() {
        return potiondataSet;
    }
}
