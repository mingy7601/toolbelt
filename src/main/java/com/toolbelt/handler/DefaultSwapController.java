package com.toolbelt.handler;

import com.toolbelt.config.ToolbeltConfig;
import net.minecraft.item.ItemStack;

/**
 * Default implementation of SwapController — contains the core swap logic.
 */
public class DefaultSwapController implements SwapController {

    private long lastSwapTime = 0;

    @Override
    public boolean canSwap(boolean inGui) {
        if (inGui) return false;

        double cooldown = ToolbeltConfig.getCooldown();
        if (cooldown <= 0) return true; // Cooldown disabled

        long now = System.currentTimeMillis();
        if ((now - lastSwapTime) < (cooldown * 1000)) {
            return false;
        }

        return true;
    }

    @Override
    public void performSwap(ItemStack[] currentHotbar, ItemStack[] storedHotbar) {
        int len = Math.min(currentHotbar.length, storedHotbar.length);

        // Save current hotbar before overwriting it
        ItemStack[] temp = new ItemStack[len];
        for (int i = 0; i < len; i++) {
            temp[i] = currentHotbar[i];
        }

        // Write stored items into the player's hotbar
        for (int i = 0; i < len; i++) {
            currentHotbar[i] = storedHotbar[i];
        }

        // Write the saved current items into stored
        for (int i = 0; i < len; i++) {
            storedHotbar[i] = temp[i];
        }

        lastSwapTime = System.currentTimeMillis();
    }

    @Override
    public long getLastSwapTime() {
        return lastSwapTime;
    }

    @Override
    public void setLastSwapTime(long time) {
        this.lastSwapTime = time;
    }
}
