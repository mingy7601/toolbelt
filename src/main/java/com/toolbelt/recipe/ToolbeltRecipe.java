package com.toolbelt.recipe;

import baubles.api.BaubleType;
import com.toolbelt.Toolbelt;
import com.toolbelt.item.ToolbeltItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Registers the crafting recipe for the Toolbelt item.
 * Pattern: 8 leather in a 3×3 grid with an empty center slot (suggests a belt with a buckle hole).
 */
public class ToolbeltRecipe {

    /**
     * Registers all mod recipes. Called during preInit.
     */
    @Mod.EventBusSubscriber(modid = Toolbelt.MODID)
    public static class RegistryHandler {
        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {
            IForgeRegistry<IRecipe> r = evt.getRegistry();

            // Register shaped recipe: leather in edge-only pattern, empty center
            r.register(new RecipeToolbelt()
                    .setRegistryName(Toolbelt.MODID, "toolbelt"));

            Toolbelt.LOGGER.info("Registered crafting recipe: 8 leather → Toolbelt");
        }
    }

    /**
     * Custom shaped recipe for the toolbelt.
     */
    public static class RecipeToolbelt extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

        @Override
        public boolean matches(InventoryCrafting inv, World world) {
            int leatherCount = 0;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() == Items.LEATHER) {
                        leatherCount++;
                    } else {
                        return false; // Only leather allowed
                    }
                }
            }
            // Exactly 8 leather items required
            return leatherCount == 8;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            return new ItemStack(getToolbeltItem());
        }

        @Override
        public boolean canFit(int width, int height) {
            return width >= 3 && height >= 3; // Must fit in 3×3 grid
        }

        @Override
        public ItemStack getRecipeOutput() {
            return new ItemStack(getToolbeltItem());
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
            NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
            for (int i = 0; i < remaining.size(); i++) {
                remaining.set(i, inv.getStackInSlot(i));
            }
            return remaining;
        }

        private static Item getToolbeltItem() {
            return ToolbeltItem.INSTANCE;
        }
    }
}
