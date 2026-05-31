package com.toolbelt.network.handler;

import com.toolbelt.Toolbelt;
import com.toolbelt.handler.BaublesHelper;
import com.toolbelt.handler.DefaultSwapController;
import com.toolbelt.handler.SwapController;
import com.toolbelt.item.ToolbeltItem;
import com.toolbelt.network.message.SwapMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Server-side handler for swap requests received from clients.
 * Performs the actual hotbar exchange on the server, ensuring proper
 * multiplayer synchronization and per-player toolbelt isolation.
 *
 * <p>Cooldown is enforced client-side before the packet is sent. The server
 * trusts this and performs the swap directly — no independent timer that could
 * desync between client and server.</p>
 */
public class SwapMessageHandler implements IMessageHandler<SwapMessage, IMessage> {

    private final SwapController swapController;

    public SwapMessageHandler() {
        this.swapController = new DefaultSwapController();
    }

    /**
     * Package-private constructor for testing — allows injecting a mock controller.
     */
    SwapMessageHandler(SwapController swapController) {
        this.swapController = swapController;
    }

    @Override
    public IMessage onMessage(SwapMessage message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        if (player == null) return null;

        IItemHandler baubles = BaublesHelper.getBaublesHandler(player);
        if (baubles == null) {
            Toolbelt.LOGGER.info("[Toolbelt] getBaublesHandler returned null");
            return null;
        }

        int slots = baubles.getSlots();
        ToolbeltItem foundItem = null;
        ItemStack foundStack = null;
        int foundSlot = 0;
        for (int i = 0; i < slots; i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            boolean empty = stack.isEmpty();
            if (!empty && stack.getItem() instanceof ToolbeltItem) {
                foundItem = (ToolbeltItem) stack.getItem();
                foundStack = stack;
                foundSlot = i;
                break;
            }
        }

        if (foundItem == null) {
            return null;
        }

        performSwap(foundItem, foundStack, player, baubles, foundSlot);
        return null;
    }

    /**
     * Performs the actual hotbar swap on the server side.
     */
    private void performSwap(ToolbeltItem toolbeltItem, ItemStack toolbeltStack,
                             EntityPlayer player, IItemHandler baubles, int baublesSlot) {

        // Get the live stack reference once — used for both read and write
        ItemStack liveStack = ((net.minecraftforge.items.IItemHandlerModifiable) baubles).getStackInSlot(baublesSlot);
        Toolbelt.LOGGER.info("[Toolbelt] liveStack stack identity: {}", System.identityHashCode(liveStack));
        Toolbelt.LOGGER.info("[Toolbelt] liveStack NBT before write: {}", liveStack.getTagCompound());
        // Step 1: Deep copy the current hotbar
        ItemStack[] currentHotbar = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            ItemStack s = player.inventory.mainInventory.get(i);
            currentHotbar[i] = s.isEmpty() ? ItemStack.EMPTY : s.copy();
        }

        // Step 2: Read stored items from the live stack's NBT
        ItemStack[] storedHotbar = toolbeltItem.restoreHotbar(liveStack);
        Toolbelt.LOGGER.info("[Toolbelt] storedHotbar read from NBT: slot0={}, slot1={}",
                storedHotbar[0], storedHotbar[1]);

        // Step 3: Swap arrays in memory
        swapController.performSwap(currentHotbar, storedHotbar);

        // Step 4: Write back to the same live stack reference
        toolbeltItem.storeHotbar(liveStack, storedHotbar);
        Toolbelt.LOGGER.info("[Toolbelt] NBT written to live stack: {}", liveStack.getTagCompound());

        // Step 5: Write the previously-stored items into the player's hotbar
        for (int i = 0; i < currentHotbar.length; i++) {
            player.inventory.mainInventory.set(i,
                    currentHotbar[i] != null ? currentHotbar[i] : ItemStack.EMPTY);
        }

        player.inventoryContainer.detectAndSendChanges();
    }
    private void writeToBaublesSlotDirectly(EntityPlayer player, int baublesSlot, ItemStack updatedStack) {
        NBTTagCompound playerNBT = player.getEntityData();

        if (!playerNBT.hasKey("BaublesItemsHandler")) {
            Toolbelt.LOGGER.warn("[Toolbelt] BaublesItemsHandler NBT key not found on player");
            return;
        }

        NBTTagCompound baublesNBT = playerNBT.getCompoundTag("BaublesItemsHandler");
        NBTTagList items = baublesNBT.getTagList("Items", 10); // 10 = TAG_COMPOUND

        // Find and replace the slot, or append if not found
        boolean found = false;
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound slotTag = items.getCompoundTagAt(i);
            if (slotTag.getByte("Slot") == (byte) baublesSlot) {
                updatedStack.writeToNBT(slotTag);
                slotTag.setByte("Slot", (byte) baublesSlot);
                found = true;
                break;
            }
        }
        if (!found) {
            NBTTagCompound slotTag = new NBTTagCompound();
            updatedStack.writeToNBT(slotTag);
            slotTag.setByte("Slot", (byte) baublesSlot);
            items.appendTag(slotTag);
        }

        baublesNBT.setTag("Items", items);
        playerNBT.setTag("BaublesItemsHandler", baublesNBT);
    }
}
