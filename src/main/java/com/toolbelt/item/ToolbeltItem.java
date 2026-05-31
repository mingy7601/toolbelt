package com.toolbelt.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.toolbelt.Toolbelt;
import com.toolbelt.inventory.ToolbeltInventory;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The Toolbelt bauble item. Extends Item and implements IBauble with BELT type.
 * Delegates NBT storage to {@link ToolbeltInventory}.
 */
public class ToolbeltItem extends Item implements IBauble {

    private static final ToolbeltInventory INVENTORY = new ToolbeltInventory();

    public static final ToolbeltItem INSTANCE = new ToolbeltItem();

    private ToolbeltItem() {
        setTranslationKey("toolbelt");
        setRegistryName(new net.minecraft.util.ResourceLocation(Toolbelt.MODID, "toolbelt"));
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        // No equipped-layer texture — invisible on character model
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.BELT;
    }

    /**
     * Stores the given hotbar array into this item stack's NBT.
     */
    public void storeHotbar(ItemStack stack, ItemStack[] hotbar) {
        if (stack == null || hotbar == null) return;
        INVENTORY.writeToStackSafe(stack, hotbar);
    }

    /**
     * Stores the player's current hotbar into this item stack's NBT.
     */
    public void storeHotbar(ItemStack stack, EntityPlayer player) {
        if (stack == null || player == null) return;
        INVENTORY.writeToStackSafe(stack, getHotbar(player));
    }

    /**
     * Restores stored items from this item stack's NBT into a new array.
     */
    public ItemStack[] restoreHotbar(ItemStack stack) {
        if (stack == null) return new ItemStack[9];
        return INVENTORY.readFromStackSafe(stack);
    }

    /**
     * Checks whether this toolbelt has any stored items.
     */
    public boolean isEmpty(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return true;
        return INVENTORY.isEmpty(stack.getTagCompound());
    }

    @Mod.EventBusSubscriber(modid = Toolbelt.MODID)
    public static class RegistryHandler {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> evt) {
            evt.getRegistry().register(INSTANCE);
        }
    }

    /**
     * Registers the model for this item (client-side only).
     */
    @SideOnly(Side.CLIENT)
    public static void registerModel() {
        net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(
                INSTANCE, 0, new ModelResourceLocation(INSTANCE.getRegistryName(), "inventory"));
    }

    /**
     * Gets the player's hotbar (slots 0-8) as an array.
     */
    private static ItemStack[] getHotbar(EntityPlayer player) {
        ItemStack[] hotbar = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            hotbar[i] = player.inventory.mainInventory.get(i);
        }
        return hotbar;
    }

    /**
     * Sets the player's hotbar (slots 0-8) from an array.
     */
    private static void setHotbar(EntityPlayer player, ItemStack[] hotbar) {
        for (int i = 0; i < 9 && i < hotbar.length; i++) {
            if (hotbar[i] != null) {
                player.inventory.mainInventory.set(i, hotbar[i]);
            }
        }
    }
}
