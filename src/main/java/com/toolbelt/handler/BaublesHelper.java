package com.toolbelt.handler;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;

/**
 * Server-safe wrapper around Baubles API access.
 * Provides a consistent way to get the player's bauble handler from both client and server contexts.
 */
public class BaublesHelper {

    /**
     * Gets the IItemHandler for the player's baubles.
     * Works on both client and server sides.
     */
    public static IItemHandler getBaublesHandler(EntityPlayer player) {
        return BaublesApi.getBaublesHandler(player);
    }

}
