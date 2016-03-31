
package com.pizzaguy.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.PotionData;

import com.pizzaguy.serialization.builder.ByteArrayBuilder;
import com.pizzaguy.serialization.builder.ItemStackBuilder;
import com.pizzaguy.serialization.model.Result;

import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;

public class ItemStackSerialization {
    // all headers for all the data a item could have

    // headers
    private static final byte[] HEADER = "[IS]".getBytes();
    private static final byte[] FOOTER = "[/IS]".getBytes();
    // basic values
    private static final byte[] MATERIAL = "MA".getBytes();
    private static final byte[] AMOUNT = "AM".getBytes();
    private static final byte[] DURABILITY = "DU".getBytes();
    private static final byte[] MATERIALDATA = "MD".getBytes();
    // item meta / nbt tag values
    private static final byte[] ENCHANTS = "EN".getBytes();
    private static final byte[] DISPLAYNAME = "DN".getBytes();
    private static final byte[] LORE = "LO".getBytes();
    private static final byte[] ITEMFLAGS = "IF".getBytes();
    private static final byte[] BANNERMETA = "BA".getBytes();
    private static final byte[] BOOKMETA = "BO".getBytes();
    private static final byte[] ENCHANTMENTSTORAGEMETA = "EM".getBytes();
    private static final byte[] FIREWORKMETA = "FW".getBytes();
    private static final byte[] LEATHERARMORMETA = "LA".getBytes();
    private static final byte[] MAPMETA = "MM".getBytes();
    private static final byte[] POTIONMETA = "PM".getBytes();
    private static final byte[] SKULLMETA = "HM".getBytes();

    // serialized itemstack
    @SuppressWarnings("deprecation")
    public static byte[] serialize(ItemStack item) {
        // checking if the item given isnt just null
        if (item == null)
            return new ByteArrayBuilder(HEADER).add(FOOTER).build();
        // create all arrays for different variables to be added later

        //// Visible values ////
        // material //
        final byte[] material = serializeMaterial(item.getType());
        // amount //
        final byte[] amount = serializeAmount(item.getAmount());
        // durability //
        final byte[] durability = serializeDurability(item.getDurability());
        // material data //
        final byte[] materialData = serializeMaterialData(item.getData().getData());
        // enchants //
        final byte[] enchants = serializeEnchantments(item.getEnchantments());
        //// base meta data ////
        ItemMeta meta = item.getItemMeta();
        // display name //
        final byte[] displayname = meta.hasDisplayName() ? serializeDisplayName(meta.getDisplayName().trim()) : null;
        // lore //
        final byte[] lore = meta.hasLore() ? serializeLore(meta.getLore()) : null;
        // item flags //
        final byte[] itemflags = serializeItemFlags(item.getItemMeta().getItemFlags());
        //// specified meta data ////
        byte[] metaData = null;
        // check which meta data it contains and write needed data for that
        // metadata
        if (meta instanceof BannerMeta)
            metaData = serializeBannerMeta((BannerMeta) meta);
        if (meta instanceof BookMeta)
            metaData = serializeBookMeta((BookMeta) meta);
        if (meta instanceof EnchantmentStorageMeta)
            metaData = serializeEnchantmentStorageMeta((EnchantmentStorageMeta) meta);
        if (meta instanceof FireworkMeta)
            metaData = serializeFireworkMeta((FireworkMeta) meta);
        if (meta instanceof LeatherArmorMeta)
            metaData = serializeLeatherArmorMeta((LeatherArmorMeta) meta);
        if (meta instanceof MapMeta)
            metaData = serializeMapMeta((MapMeta) meta);
        if (meta instanceof PotionMeta) 
            metaData = serializePotionMeta((PotionMeta) meta);
        if (meta instanceof SkullMeta)
            metaData = serializeSkullMeta(item, (SkullMeta) meta);

        // build final byte array with all the before created arrays
        final byte[] data = new ByteArrayBuilder(HEADER).add(material).add(amount).add(durability).add(materialData).add(enchants).add(displayname).add(lore).add(itemflags).add(metaData).add(FOOTER).build();
        return data;
    }
    // serialize itemstack array
    public static byte[] serializeArray(ItemStack[] items) {
        // creates array builder
        ByteArrayBuilder builder = new ByteArrayBuilder(new byte[0]);
        // adds serialized item to the byte array
        for (ItemStack item : items) {
            builder.add(serialize(item));
        }
        // builds the full byte array and returns it
        return builder.build();
    }

    public static ItemStack deserialize(byte[] src) {
        return (ItemStack) deserialize(src, 0).getResult();
    }

    // deserialized itemstack
    @SuppressWarnings("unchecked")
    public static Result deserialize(byte[] src, int index) {
        // creates an itemstackbuilder object for the final itemstack
        ItemStackBuilder imb = new ItemStackBuilder();
        // cycles through all the bytes in the array
        for (int i = index; i < src.length - 1; i++) {
            // takes this byte and the next to compare to the headers
            byte[] d = SerializationReader.readBytes(i, src, 2);
            // creates string from it
            String id = new String(d);

            // copares the current bytes to footer and stops reading when finds
            // footer
            if (i + FOOTER.length < src.length - 1) {
                String footer = new String(SerializationReader.readBytes(i, src, FOOTER.length));
                if (footer.equals(new String(FOOTER)))
                    break;
            }
            // compares to one of the before mentioned header
            // and if it has not been set yet in the itemstackbuilder read the
            // data and put it into the itemstackbuilder
            // also sets the current position to the last position it read

            // Material
            if (id.equals(new String(MATERIAL)) && !imb.isMaterialSet()) {
                Result r = deserializeMaterial(i, src);
                imb.setType((Material) r.getResult());
                i = r.getLength();
            }
            // Amount
            else if (id.equals(new String(AMOUNT)) && !imb.isAmountSet()) {
                Result r = deserializeAmount(i, src);
                imb.setAmount((short) r.getResult());
                i = r.getLength();
            }
            // Durability
            else if (id.equals(new String(DURABILITY)) && !imb.isDurabilitySet()) {
                Result r = deserializeDurability(i, src);
                imb.setDurability((short) r.getResult());
                i = r.getLength();
            }
            // MaterialData
            else if (id.equals(new String(MATERIALDATA)) && !imb.isDataSet()) {
                Result r = deserializeMaterialData(i, src);
                imb.setData((byte) r.getResult());
                i = r.getLength();
            }
            // Enchants
            else if (id.equals(new String(ENCHANTS)) && !imb.isEnchantsSet()) {
                Result r = deserializeEnchantments(i, src);
                imb.addEnchantments((Map<Enchantment, Integer>) r.getResult());
                i = r.getLength();
            }
            // Displayname
            else if (id.equals(new String(DISPLAYNAME)) && !imb.isNameSet()) {
                Result r = deserializeDisplayName(i, src);
                imb.setDisplayName((String) r.getResult());
                i = r.getLength();
            }
            // lore
            else if (id.equals(new String(LORE)) && !imb.isLoreSet()) {
                Result r = deserializeLore(i, src);
                imb.setLore((List<String>) r.getResult());
                i = r.getLength();
            }
            // ItemFlags
            else if (id.equals(new String(ITEMFLAGS)) && !imb.isFlagsSet()) {
                Result r = deserializeItemFlags(i, src);
                imb.setFlags((List<ItemFlag>) r.getResult());
                i = r.getLength();
            }
            // BannerMeta
            else if (id.equals(new String(BANNERMETA)) && !imb.isBannerSet()) {
                Result r = deserializeBaseColor(i, src);
                Result r2 = deserializePatterns(i + 2, src);
                imb.setBannerData((DyeColor) r.getResult(), (List<Pattern>) r2.getResult());
                i = r2.getLength();
            }
            // Bookmeta
            else if (id.equals(new String(BOOKMETA)) && !imb.isBookSet()) {
                Result author = deserializeAuthor(i, src);
                Result title = deserializeTitle(author.getLength(), src);
                Result pages = deserializePages(title.getLength(), src);
                imb.setBookData((String) author.getResult(), (String) title.getResult(), (List<String>) pages.getResult());
                i = pages.getLength();
            }
            // EnchantmetnStorage meta
            else if (id.equals(new String(ENCHANTMENTSTORAGEMETA)) && !imb.isStoredEnchantsSet()) {
                Result r = deserializeStoredEnchants(i, src);
                imb.setStoredEnchantments((Map<Enchantment, Integer>) r.getResult());
                i = r.getLength();
            }
            // FireworkMeta
            else if (id.equals(new String(FIREWORKMETA)) && !imb.isFireworkSet()) {
                Result r = deserializePower(i, src);
                Result r2 = deserializeFireworkEffects(r.getLength(), src);
                imb.setFireworkData((short) r.getResult(), (List<FireworkEffect>) r2.getResult());
                i = r2.getLength();
            }
            // LeatherArmor meta
            else if (id.equals(new String(LEATHERARMORMETA)) && !imb.isLeatherSet()) {
                Result r = deserializeLeatherArmorColor(i, src);
                imb.setLeatherArmorColor((Color) r.getResult());
                i = r.getLength();
            }
            // Map meta
            else if (id.equals(new String(MAPMETA)) && !imb.isMapSet()) {
                Result r = deserializeMapScaling(i, src);
                imb.setMapScaling((boolean) r.getResult());
                i = r.getLength();
            }
            // PotionMeta
            else if (id.equals(new String(POTIONMETA)) && !imb.isPotionSet()) {
                Result r1 = deserializePotionData(i, src);
                Result r2 = deserializeCustomPotions(r1.getLength(), src);
                imb.setPotionData((PotionData) r1.getResult());
                imb.setCustomPotions((List<PotionEffect>) r2.getResult());
                i = r2.getLength();
            }
            // Skull meta
            else if (id.equals(new String(SKULLMETA)) && !imb.isSkullSet()) {
                Result r = deserializeSkullData(i, src);
                if (r != null) {
                    imb.setSkullData((NBTTagCompound) r.getResult());
                    i = r.getLength();
                }
            }
            index = i;
        }
        // Finally build the itemstackbuilder and return it
        return new Result(imb.build(), index);
    }
    // deserialize itemstack array
    public static ItemStack[] deserializeArray(byte[] src) {
        // creates a new itemstack array
        List<ItemStack> items = new ArrayList<ItemStack>();
        // deserialize all the byte arrays and item to the itemstack list
        for (int i = 0; i < src.length - HEADER.length; i++) {
            String header = new String(SerializationReader.readBytes(i, src, HEADER.length));
            if (header.equals(new String(HEADER))) {
                ItemStack item = (ItemStack) deserialize(src, i).getResult();
                items.add(item);
            }
        }
        // return the itemstack list as a native array
        return (ItemStack[]) items.toArray(new ItemStack[items.size()]);
    }

    // writes the material data
    @SuppressWarnings("deprecation")
    private static final byte[] serializeMaterial(Material material) {
        // create the space required
        byte[] data = new byte[4];
        // write the header
        int pointer = SerializationWriter.writeBytes(0, data, MATERIAL);
        // write the materialdata itself
        pointer = SerializationWriter.writeBytes(pointer, data, (short) material.getId());
        // return the serialized data
        return data;
    }

    // writes amount data
    private static final byte[] serializeAmount(int amount) {
        // create the space required
        byte[] data = new byte[4];
        // write the header
        int pointer = SerializationWriter.writeBytes(0, data, AMOUNT);
        // write the amount
        pointer = SerializationWriter.writeBytes(pointer, data, (short) amount);
        // return the serialized data
        return data;
    }

    // write the durability data
    private static final byte[] serializeDurability(short durability) {
        // create space
        byte[] data = new byte[4];
        // write the header
        int pointer = SerializationWriter.writeBytes(0, data, DURABILITY);
        // write the durability
        pointer = SerializationWriter.writeBytes(pointer, data, (short) durability);
        // return the serialized data
        return data;
    }

    // write the MaterialData
    private static final byte[] serializeMaterialData(byte materialData) {
        // create space
        byte[] data = new byte[4];
        // write the header
        int pointer = SerializationWriter.writeBytes(0, data, MATERIALDATA);
        // write the MaterialData
        pointer = SerializationWriter.writeBytes(pointer, data, (short) materialData);
        // return the serialized data
        return data;
    }

    // write enchantments
    @SuppressWarnings("deprecation")
    private static final byte[] serializeEnchantments(Map<Enchantment, Integer> enchants) {
        // create space
        byte[] data = new byte[4 + (enchants.size() * 4)];
        // write the header
        int pointer = SerializationWriter.writeBytes(0, data, ENCHANTS);
        // write the amount of enchants
        pointer = SerializationWriter.writeBytes(pointer, data, (short) enchants.size());
        // write the enchant id + enchant strength
        for (Enchantment enchant : enchants.keySet()) {
            pointer = SerializationWriter.writeBytes(pointer, data, (short) enchant.getId());
            pointer = SerializationWriter.writeBytes(pointer, data, (short) enchants.get(enchant).shortValue());
        }
        // return serialized data
        return data;
    }

    // write displayname
    private static final byte[] serializeDisplayName(String name) {
        // create space
        byte[] data = new byte[4 + name.getBytes().length];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, DISPLAYNAME);
        // write string
        pointer = SerializationWriter.writeBytes(pointer, data, name);
        // return serialized data
        return data;
    }

    // write lore
    private static final byte[] serializeLore(List<String> lore) {
        // calculate size
        int size = LORE.length + Short.BYTES;
        // add length of the lines in the lore
        for (String line : lore)
            size += Short.BYTES + line.getBytes().length;
        // create data with size
        byte[] data = new byte[size];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, LORE);
        // write lore size
        pointer = SerializationWriter.writeBytes(pointer, data, (short) lore.size());
        // write all the lines
        for (String line : lore)
            pointer = SerializationWriter.writeBytes(pointer, data, line);
        // return serialized data
        return data;
    }

    // write itemflags
    private static final byte[] serializeItemFlags(Set<ItemFlag> set) {
        // create space
        byte[] data = new byte[4 + set.size() * 2];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, ITEMFLAGS);
        // write size of itemflags
        pointer = SerializationWriter.writeBytes(pointer, data, (short) set.size());
        // write the itemflags
        for (ItemFlag flag : set)
            pointer = SerializationWriter.writeBytes(pointer, data, (short) flag.ordinal());
        // return serialized data
        return data;
    }

    // write banner meta
    private static final byte[] serializeBannerMeta(BannerMeta bMeta) {
        // create space
        byte[] data = new byte[BANNERMETA.length + Short.BYTES * 2 + bMeta.getPatterns().size() * 4];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, BANNERMETA);
        // check if color is not null
        if (bMeta.getBaseColor() != null)
            // write color data as ordinal in the color ENUM
            pointer = SerializationWriter.writeBytes(pointer, data, (short) bMeta.getBaseColor().ordinal());
        else
            // write negitive value to indicate that it is null
            pointer = SerializationWriter.writeBytes(pointer, data, (short) -1);
        // write patern size
        pointer = SerializationWriter.writeBytes(pointer, data, (short) bMeta.getPatterns().size());
        // write patern color and paterntype
        for (Pattern pattern : bMeta.getPatterns()) {
            pointer = SerializationWriter.writeBytes(pointer, data, (short) pattern.getColor().ordinal());
            pointer = SerializationWriter.writeBytes(pointer, data, (short) pattern.getPattern().ordinal());
        }
        // return serialized data
        return data;
    }

    // write book meta
    private static final byte[] serializeBookMeta(BookMeta bMeta) {
        // calculate size
        int size = 2;
        if (bMeta.hasAuthor())
            size += bMeta.getAuthor().getBytes().length;
        size += 2;
        if (bMeta.hasTitle())
            size += bMeta.getTitle().getBytes().length;
        size += 2;
        size += 2;
        for (String page : bMeta.getPages())
            size += page.getBytes().length + 2;
        // create space
        byte[] data = new byte[size];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, BOOKMETA);
        // write authors name or "" if not present
        if (bMeta.hasAuthor())
            pointer = SerializationWriter.writeBytes(pointer, data, bMeta.getAuthor());
        else
            pointer = SerializationWriter.writeBytes(pointer, data, "");
        // writes title or "" if not present
        if (bMeta.hasTitle())
            pointer = SerializationWriter.writeBytes(pointer, data, bMeta.getTitle());
        else
            pointer = SerializationWriter.writeBytes(pointer, data, "");
        // writes all the pages or 0 if not present
        if (bMeta.hasPages()) {
            // writes pages size
            pointer = SerializationWriter.writeBytes(pointer, data, (short) bMeta.getPageCount());
            // write all the pages
            for (String page : bMeta.getPages())
                pointer = SerializationWriter.writeBytes(pointer, data, page);
        } else
            pointer = SerializationWriter.writeBytes(pointer, data, (short) 0);
        // return serialized data
        return data;
    }

    // write enchantmentstorage meta
    @SuppressWarnings("deprecation")
    private static byte[] serializeEnchantmentStorageMeta(EnchantmentStorageMeta meta) {
        // create space
        byte[] data = new byte[4 + (meta.getStoredEnchants().size() * 4)];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, ENCHANTMENTSTORAGEMETA);
        // write echantment size
        pointer = SerializationWriter.writeBytes(pointer, data, (short) meta.getStoredEnchants().size());
        // write enchantment id and level
        for (Enchantment ench : meta.getStoredEnchants().keySet()) {
            pointer = SerializationWriter.writeBytes(pointer, data, (short) ench.getId());
            pointer = SerializationWriter.writeBytes(pointer, data, (short) meta.getStoredEnchantLevel(ench));
        }
        // return serialized data
        return data;
    }

    // write firework meta
    @SuppressWarnings("unused")
    private static byte[] serializeFireworkMeta(FireworkMeta meta) {
        // calculate size
        int size = 6;
        for (FireworkEffect effect : meta.getEffects()) {
            size += 8;
            for (Color color : effect.getColors())
                size += Integer.BYTES;
            for (Color color : effect.getFadeColors())
                size += Integer.BYTES;
        }
        // create space
        byte[] data = new byte[size];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, FIREWORKMETA);
        // write power
        pointer = SerializationWriter.writeBytes(pointer, data, (short) meta.getPower());
        // write effect size
        pointer = SerializationWriter.writeBytes(pointer, data, (short) meta.getEffects().size());
        // write effects
        for (FireworkEffect effect : meta.getEffects()) {
            // write type
            pointer = SerializationWriter.writeBytes(pointer, data, (short) effect.getType().ordinal());
            // write trail as boolean
            pointer = SerializationWriter.writeBytes(pointer, data, effect.hasTrail());
            // write boolean as flicker
            pointer = SerializationWriter.writeBytes(pointer, data, effect.hasFlicker());
            // write color size
            pointer = SerializationWriter.writeBytes(pointer, data, (short) effect.getColors().size());
            // write colors
            for (Color color : effect.getColors())
                pointer = SerializationWriter.writeBytes(pointer, data, color.asRGB());
            // write fade colors size
            pointer = SerializationWriter.writeBytes(pointer, data, (short) effect.getFadeColors().size());
            // write fade colors
            for (Color color : effect.getFadeColors())
                pointer = SerializationWriter.writeBytes(pointer, data, color.asRGB());
        }
        // write serialized data
        return data;
    }

    // write leather armor meta
    private static byte[] serializeLeatherArmorMeta(LeatherArmorMeta meta) {
        // create space
        byte[] data = new byte[6];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, LEATHERARMORMETA);
        // write color as an int
        pointer = SerializationWriter.writeBytes(pointer, data, meta.getColor().asRGB());
        // return serialized data
        return data;
    }

    // serialize map data
    private static byte[] serializeMapMeta(MapMeta meta) {
        // create space
        byte[] data = new byte[3];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, MAPMETA);
        // write scaling boolean
        pointer = SerializationWriter.writeBytes(pointer, data, meta.isScaling());
        // return serialized data
        return data;
    }

    // write potion meta
    @SuppressWarnings("deprecation")
    private static byte[] serializePotionMeta(PotionMeta meta) {
        // create space
        byte[] data = new byte[POTIONMETA.length + Short.BYTES * 2 + 2 + (meta.getCustomEffects().size() * (Short.BYTES * 3 + 2))];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, POTIONMETA);
        // write main effect
        pointer = SerializationWriter.writeBytes(pointer, data, (short) meta.getBasePotionData().getType().ordinal());
        // write extended
        pointer = SerializationWriter.writeBytes(pointer, data, meta.getBasePotionData().isExtended());
        // write upgraded
        pointer = SerializationWriter.writeBytes(pointer, data, meta.getBasePotionData().isUpgraded());
        // write effect size
        pointer = SerializationWriter.writeBytes(pointer, data, (short) meta.getCustomEffects().size());
        // write effects
        for (PotionEffect effect : meta.getCustomEffects()) {
            // write type
            pointer = SerializationWriter.writeBytes(pointer, data, (short) effect.getType().getId());
            // write amplifier
            pointer = SerializationWriter.writeBytes(pointer, data, (short) effect.getAmplifier());
            // write duration
            pointer = SerializationWriter.writeBytes(pointer, data, (short) effect.getDuration());
            // write ambiant boolean
            pointer = SerializationWriter.writeBytes(pointer, data, effect.isAmbient());
            // write boolean particles
            pointer = SerializationWriter.writeBytes(pointer, data, effect.hasParticles());
        }
        // return sairialized data
        return data;
    }
    // serialized skull meta
    private static byte[] serializeSkullMeta(ItemStack item, SkullMeta meta) {
        // NBT tag, because skulls are badly supported
        net.minecraft.server.v1_9_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = stack.hasTag() ? stack.getTag() : new NBTTagCompound();

        // sets users id if avaliable
        String id = tag.getCompound("SkullOwner").getString("Id").equals("") ? null : tag.getCompound("SkullOwner").getString("Id");
        // sets users name if avaliable
        String name = tag.getCompound("SkullOwner").getString("Name").equals("") ? null : tag.getCompound("SkullOwner").getString("Name");
        // create space
        byte[] data = meta.hasOwner() && id != null && name != null ? new byte[SKULLMETA.length + 1 + Short.BYTES * 2 + id.getBytes().length + name.getBytes().length] : new byte[SKULLMETA.length + 1];
        // write header
        int pointer = SerializationWriter.writeBytes(0, data, SKULLMETA);
        // write boolean if player head
        pointer = SerializationWriter.writeBytes(pointer, data, (meta.hasOwner() && id != null && name != null));
        // check if playerhead
        if (meta.hasOwner() && id != null && name != null) {
            // write id
            pointer = SerializationWriter.writeBytes(pointer, data, id);
            // write name
            pointer = SerializationWriter.writeBytes(pointer, data, name);
        }
        // return serialized data
        return data;
    }

    // read matarial
    @SuppressWarnings("deprecation")
    private static Result deserializeMaterial(int i, byte[] src) {
        // read item id
        short itemId = SerializationReader.readShort(i += MATERIAL.length, src);
        // return result class with result and current read position
        return new Result(Material.getMaterial(itemId), i += 1);
    }

    // read amount
    private static Result deserializeAmount(int i, byte[] src) {
        // read amount
        short amount = SerializationReader.readShort(i += AMOUNT.length, src);
        // return result class with result and current read position
        return new Result(amount, i);
    }

    // read durability
    private static Result deserializeDurability(int i, byte[] src) {
        // read durabilty
        short durability = SerializationReader.readShort(i += DURABILITY.length, src);
        // return result class with result and current read position
        return new Result(durability, i);
    }

    // read materialdata
    private static Result deserializeMaterialData(int i, byte[] src) {
        // read materialdata
        byte data = (byte) SerializationReader.readShort(i += MATERIALDATA.length, src);
        // return result class with result and current read position
        return new Result(data, i);
    }

    // read enchants
    @SuppressWarnings("deprecation")
    private static Result deserializeEnchantments(int i, byte[] src) {
        // create variable
        Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
        // read enchantments length
        short length = SerializationReader.readShort(i += ENCHANTS.length, src);
        // read enchantments
        for (int x = 0; x < length; x++) {
            // read enchantment type
            int enchantId = SerializationReader.readShort(i += Short.BYTES, src);
            // read enchantment level
            short enchantLvl = SerializationReader.readShort(i += Short.BYTES, src);
            // add to the array
            enchants.put(Enchantment.getById(enchantId), new Integer(enchantLvl));
        }
        // return result class with result and current read position
        return new Result(enchants, i);
    }

    // read display name
    private static Result deserializeDisplayName(int i, byte[] src) {
        // read name
        String string = SerializationReader.readString(i += 2, src);
        // return result class with result and current read position
        return new Result(string, i + string.getBytes().length);
    }

    // read lore
    private static Result deserializeLore(int i, byte[] src) {
        // create list of lore
        List<String> lore = new ArrayList<String>();
        // read amount of lines
        short length = SerializationReader.readShort(i += LORE.length, src);
        // read lines
        for (int x = 0; x < length; x++) {
            // read line
            String line = SerializationReader.readString(i += Short.BYTES, src);
            // add to list
            lore.add(line);
            i += line.getBytes().length;
        }
        // return result class with result and current read position
        return new Result(lore, i);
    }

    // read itemflags
    private static Result deserializeItemFlags(int i, byte[] src) {
        // create list of itemflags
        List<ItemFlag> flags = new ArrayList<ItemFlag>();
        // read amount of flags
        short length = SerializationReader.readShort(i += ITEMFLAGS.length, src);
        // read flags
        for (int x = 0; x < length; x++) {
            // read flag
            short flagId = SerializationReader.readShort(i += Short.BYTES, src);
            // add a flag to list
            flags.add(ItemFlag.values()[flagId]);
        }
        // return result class with result and current read position
        return new Result(flags, i);
    }

    // read base color
    private static Result deserializeBaseColor(int i, byte[] src) {
        // create variable
        DyeColor baseColor = null;
        // read basecolor id
        short baseID = SerializationReader.readShort(i += BANNERMETA.length, src);
        // set variable if baseID is not -1
        if (baseID != -1)
            baseColor = DyeColor.values()[baseID];
        // return result class with result and current read position
        return new Result(baseColor, i);
    }

    // read banner patters
    private static Result deserializePatterns(int i, byte[] src) {
        // create list
        List<Pattern> patterns = new ArrayList<Pattern>();
        // read pattern length
        short length = SerializationReader.readShort(i += Short.BYTES, src);
        // read patterns
        for (int x = 0; x < length; x++) {
            // read color
            short color = SerializationReader.readShort(i += Short.BYTES, src);
            // read pattern id
            short pattern = SerializationReader.readShort(i += Short.BYTES, src);
            // add pattern to the list
            patterns.add(new Pattern(DyeColor.values()[color], PatternType.values()[pattern]));
        }
        // return result class with result and current read position
        return new Result(patterns, i);
    }

    // read author
    private static Result deserializeAuthor(int i, byte[] src) {
        // read author name
        String string = SerializationReader.readString(i += BOOKMETA.length, src);
        // return result class with result and current read position
        return new Result(string, i + string.getBytes().length + 2);
    }

    // return title
    private static Result deserializeTitle(int i, byte[] src) {
        // read title
        String string = SerializationReader.readString(i, src);
        // return result class with result and current read position
        return new Result(string, i + string.getBytes().length + 2);
    }

    // read pages
    private static Result deserializePages(int i, byte[] src) {
        // create list<string>
        List<String> pages = new ArrayList<String>();
        // read amount of pages
        short length = SerializationReader.readShort(i, src);
        // read pages
        for (int x = 0; x < length; x++) {
            // read page
            String current = SerializationReader.readString(i += Short.BYTES, src);
            // ad page
            pages.add(current);
            i += current.getBytes().length;
        }
        // return result class with result and current read position
        return new Result(pages, i);
    }

    // read stored enchants
    @SuppressWarnings("deprecation")
    private static Result deserializeStoredEnchants(int i, byte[] src) {
        // create enchant list
        Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
        // read length
        short length = SerializationReader.readShort(i += Short.BYTES, src);
        // read enchants
        for (int x = 0; x < length; x++) {
            // read enchant id
            short enchantID = SerializationReader.readShort(i += Short.BYTES, src);
            // read level
            short level = SerializationReader.readShort(i += 2, src);
            // add enchants
            enchants.put(Enchantment.getById(enchantID), (int) level);
        }
        // return result class with result and current read position
        return new Result(enchants, i);
    }

    // read power
    private static Result deserializePower(int i, byte[] src) {
        // read power
        short power = SerializationReader.readShort(i += 2, src);
        // return result class with result and current read position
        return new Result(power, i);
    }

    // read firework effects
    private static Result deserializeFireworkEffects(int i, byte[] src) {
        // creat list
        List<FireworkEffect> effects = new ArrayList<FireworkEffect>();
        // read firework effect length
        short length = SerializationReader.readShort(i += 2, src);
        i += 2;
        // read effects
        for (int x = 0; x < length; x++) {
            // read type
            Type effectID = Type.values()[SerializationReader.readShort(i, src)];
            // read trail
            boolean trail = SerializationReader.readBoolean(i += 2, src);
            // read flicker
            boolean flicker = SerializationReader.readBoolean(i += 1, src);
            // create color list
            List<Color> colors = new ArrayList<Color>();
            // read color length
            short lengthColor = SerializationReader.readShort(i += 1, src);
            i += 2;
            // read colors
            for (int z = 0; z < lengthColor; z++) {
                // add color
                colors.add(Color.fromRGB(SerializationReader.readInt(i, src)));
                i += 4;
            }
            // create fade color list
            List<Color> fadeColors = new ArrayList<Color>();
            // read face color length
            short lengthFadeColor = SerializationReader.readShort(i, src);
            i += 2;
            // read fade colors
            for (int z = 0; z < lengthFadeColor; z++) {
                fadeColors.add(Color.fromRGB(SerializationReader.readInt(i, src)));
                i += 4;
            }
            // add effect
            effects.add(FireworkEffect.builder().flicker(flicker).trail(trail).withColor(colors).withFade(fadeColors).with(effectID).build());
        }
        // return result class with result and current read position
        return new Result(effects, i);
    }

    // read leatherarmor color
    private static Result deserializeLeatherArmorColor(int i, byte[] src) {
        // read color as a RGB int
        Color c = Color.fromRGB(SerializationReader.readInt(i += LEATHERARMORMETA.length, src));
        // return result class with result and current read position
        return new Result(c, i);
    }

    // read map scaling
    private static Result deserializeMapScaling(int i, byte[] src) {
        // read scaling
        boolean b = SerializationReader.readBoolean(i += MAPMETA.length, src);
        // return result class with result and current read position
        return new Result(b, i);
    }

    // read custom potions
    @SuppressWarnings("deprecation")
    private static Result deserializeCustomPotions(int i, byte[] src) {
        // create list
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        // read amount of potions
        short length = SerializationReader.readShort(i, src);
        // read potions
        for (int x = 0; x < length; x++) {
            // read effectid
            short effectID = SerializationReader.readShort(i += Short.BYTES, src);
            // read amplifier
            short amplifier = SerializationReader.readShort(i += Short.BYTES, src);
            // read duration
            short duration = SerializationReader.readShort(i += Short.BYTES, src);
            // read ambiant boolean
            boolean ambiant = SerializationReader.readBoolean(i += Short.BYTES, src);
            // read particals boolean
            boolean particles = SerializationReader.readBoolean(++i, src);
            // add potion to list
            effects.add(new PotionEffect(PotionEffectType.getById(effectID), duration, amplifier, ambiant, particles));
        }
        // return result class with result and current read position
        return new Result(effects, i);
    }
    private static Result deserializePotionData(int i, byte[] src) {
        int ordinal = SerializationReader.readShort(i += 2, src);
        boolean extended = SerializationReader.readBoolean(i += 2, src);
        boolean upgraded = SerializationReader.readBoolean(i += 1, src);
        i+=1;
        PotionData data = new PotionData(PotionType.values()[ordinal], extended, upgraded);
        return new Result(data, i);
    }
    // read skulldata
    private static Result deserializeSkullData(int i, byte[] src) {
        // read if skull has data
        boolean hasData = SerializationReader.readBoolean(i += 2, src);
        // if there is no data then return null
        if (!hasData)
            return null;
        // read id
        String id = SerializationReader.readString(++i, src);
        // read name
        String name = SerializationReader.readString(i += id.getBytes().length + 2, src);
        // read texture

        // create tag
        NBTTagCompound tag = new NBTTagCompound();
        // create taglist
        NBTTagList textures = new NBTTagList();
        // add new nbttagcompound to taglist
        textures.add(new NBTTagCompound());
        // create a new properties tag
        NBTTagCompound properties = new NBTTagCompound();
        // add the taglist to properties tag
        properties.set("textures", textures);
        // create a new NBT compound for skullwoerns
        NBTTagCompound skullowner = new NBTTagCompound();
        // set the id
        skullowner.setString("Id", id);
        // set the name
        skullowner.setString("Name", name);
        // set the properties
        skullowner.set("Properties", properties);
        // add the skullowner tag to the original tag
        tag.set("SkullOwner", skullowner);
        // return result class with result and current read position
        return new Result(tag, i);
    }
}