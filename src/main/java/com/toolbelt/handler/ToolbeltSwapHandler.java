package com.toolbelt.handler;

import com.toolbelt.Toolbelt;
import com.toolbelt.config.ToolbeltConfig;
import com.toolbelt.network.ToolbeltNetworkManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Registers a Minecraft KeyBinding for the swap action (default key: F).
 * Listens for key press events and enforces configurable cooldown between swaps.
 * Triggers only when toolbelt is equipped in the correct slot.
 * Delegates core swap logic to {@link SwapController}.
 */
public class ToolbeltSwapHandler {

    private static final String KEY_CATEGORY = "key.categories.toolbelt";
    private static final String KEY_SWAP_NAME = "key.toolbelt.swap";
    private static KeyBinding keyBindSwap;

    private final SwapController swapController;

    public ToolbeltSwapHandler() {
        this.swapController = new DefaultSwapController();
    }

    /**
     * Package-private constructor for testing — allows injecting a mock controller.
     */
    ToolbeltSwapHandler(SwapController swapController) {
        this.swapController = swapController;
    }

    /**
     * Registers the swap keybinding with Minecraft's standard KeyBinding system.
     */
    public static void registerKeybind() {
        if (keyBindSwap == null) {
            keyBindSwap = new KeyBinding(KEY_SWAP_NAME, Keyboard.KEY_F, KEY_CATEGORY);
            ClientRegistry.registerKeyBinding(keyBindSwap);
        }
    }

    /**
     * Returns the registered swap keybinding, or null if not yet registered.
     */
    public static KeyBinding getKeyBindSwap() {
        return keyBindSwap;
    }

    /**
     * Client-side keybind handler. Checks conditions and sends a swap request
     * to the server. The actual swap logic runs on the server for multiplayer safety.
     */
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (keyBindSwap == null || !keyBindSwap.isPressed()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        // Don't swap while in a GUI
        boolean inGui = mc.currentScreen instanceof GuiContainer;
        if (!swapController.canSwap(inGui)) {
            Toolbelt.LOGGER.info("[Toolbelt] canSwap returned false, inGui={}", inGui);
            return;
        }

        // Send request to server — actual swap runs on server side
        ToolbeltNetworkManager.sendSwapRequestToServer();
    }


}
