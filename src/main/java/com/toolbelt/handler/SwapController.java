package com.toolbelt.handler;

import net.minecraft.item.ItemStack;

/**
 * Core swap logic interface — testable without Minecraft runtime.
 */
public interface SwapController {

    /**
     * Checks whether a swap can proceed given current conditions.
     *
     * @param inGui whether the player is currently inside a GUI
     * @return true if swap should be allowed
     */
    boolean canSwap(boolean inGui);

    /**
     * Performs the hotbar swap: exchanges currentHotbar with storedHotbar.
     * Updates cooldown timer on success.
     *
     * @param currentHotbar  the player's current hotbar (will receive stored items)
     * @param storedHotbar   the toolbelt's stored hotbar (will be written to player slots)
     */
    void performSwap(ItemStack[] currentHotbar, ItemStack[] storedHotbar);

    /**
     * Returns the last swap timestamp in milliseconds since epoch.
     */
    long getLastSwapTime();

    /**
     * Sets the last swap timestamp (milliseconds).
     */
    void setLastSwapTime(long time);
}
