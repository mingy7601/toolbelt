package com.toolbelt.handler;

import baubles.api.BaublesApi;
import com.toolbelt.Toolbelt;
import com.toolbelt.config.ToolbeltConfig;
import com.toolbelt.item.ToolbeltItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Registers a Minecraft KeyBinding for the swap action (default key: F).
 * Listens for key press events and enforces configurable cooldown between swaps.
 * Triggers only when toolbelt is equipped in the correct slot.
 */
public class ToolbeltSwapHandler {

    private static final String KEY_CATEGORY = "key.categories.toolbelt";
    private static final String KEY_SWAP_NAME = "key.toolbelt.swap";
    private static KeyBinding keyBindSwap;

    /**
     * Registers the swap keybinding with Minecraft's standard KeyBinding system.
     */
    public static void registerKeybind() {
        if (keyBindSwap == null) {
            keyBindSwap = new KeyBinding(KEY_SWAP_NAME, Keyboard.KEY_F, KEY_CATEGORY);
            // Register via reflection into Minecraft's keybind array
            try {
                java.lang.reflect.Field field = Minecraft.class.getDeclaredField("field_7429_ao"); // keyBindingArray
                field.setAccessible(true);
                KeyBinding[] bindings = (KeyBinding[]) field.get(Minecraft.getMinecraft());
                KeyBinding[] newBindings = new KeyBinding[bindings.length + 1];
                System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
                newBindings[bindings.length] = keyBindSwap;
                field.set(Minecraft.getMinecraft(), newBindings);
            } catch (Exception e) {
                Toolbelt.LOGGER.warn("Failed to register keybinding via reflection.", e);
            }
        }
    }

    /**
     * Returns the registered swap keybinding, or null if not yet registered.
     */
    public static KeyBinding getKeyBindSwap() {
        return keyBindSwap;
    }

    /**
     * Handles the swap action when the keybind is pressed.
     * Checks cooldown, toolbelt equipped status, and performs the hotbar exchange.
     */
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (keyBindSwap == null || !keyBindSwap.isPressed()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        // Don't swap while in a GUI
        if (mc.currentScreen instanceof GuiContainer) return;

        EntityPlayer player = mc.player;
        ToolbeltItem toolbeltItem = getEquippedToolbelt(player);
        if (toolbeltItem == null) return;

        ItemStack toolbeltStack = getToolbeltStack(player);
        if (toolbeltStack == null) return;

        // Check cooldown on the entity
        if (!hasCooldownElapsed(player)) return;

        performSwap(toolbeltItem, toolbeltStack, player);
    }

    /**
     * Finds the Toolbelt item equipped in the bauble belt slot.
     */
    private static ToolbeltItem getEquippedToolbelt(EntityPlayer player) {
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return null;

        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ToolbeltItem) {
                return (ToolbeltItem) stack.getItem();
            }
        }
        return null;
    }

    /**
     * Gets the actual toolbelt ItemStack from the player's baubles.
     */
    private static ItemStack getToolbeltStack(EntityPlayer player) {
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return null;

        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ToolbeltItem) {
                return stack;
            }
        }
        return null;
    }

    /**
     * Checks whether the cooldown has elapsed for this player.
     */
    private boolean hasCooldownElapsed(EntityPlayer player) {
        int cooldown = ToolbeltConfig.getCooldown();
        if (cooldown <= 0) return true; // Cooldown disabled

        Integer lastSwapTime = getLastSwapTime(player);
        long now = System.currentTimeMillis() / 1000;

        if (lastSwapTime != null && (now - lastSwapTime) < cooldown) {
            return false;
        }

        setLastSwapTime(player, (int) now);
        return true;
    }

    private Integer getLastSwapTime(EntityPlayer player) {
        String key = "toolbelt_last_swap_time";
        if (player.getEntityData().hasKey(key)) {
            return player.getEntityData().getInteger(key);
        }
        return null;
    }

    private void setLastSwapTime(EntityPlayer player, int time) {
        player.getEntityData().setInteger("toolbelt_last_swap_time", time);
    }

    /**
     * Performs the actual hotbar swap: reads current hotbar → writes to inventory → restores stored set.
     */
    private void performSwap(ToolbeltItem toolbeltItem, ItemStack toolbeltStack, EntityPlayer player) {
        // Step 1: Read the player's current hotbar slots (0–8) into a temporary array
        ItemStack[] currentHotbar = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            currentHotbar[i] = player.inventory.mainInventory.get(i);
        }

        // Step 2: Store the current hotbar into the toolbelt's NBT
        toolbeltItem.storeHotbar(toolbeltStack, player);

        // Step 3: Restore stored items from the toolbelt into the player's hotbar slots
        ItemStack[] storedHotbar = toolbeltItem.restoreHotbar(toolbeltStack);
        for (int i = 0; i < 9 && i < storedHotbar.length; i++) {
            player.inventory.mainInventory.set(i, storedHotbar[i]);
        }

        // Mark inventory as dirty so changes are saved
        player.inventoryContainer.detectAndSendChanges();
    }
}
