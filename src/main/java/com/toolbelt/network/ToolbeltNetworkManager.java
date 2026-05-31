package com.toolbelt.network;

import com.toolbelt.network.handler.SwapMessageHandler;
import com.toolbelt.network.message.SwapMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network channel for Toolbelt mod. Handles client-server communication
 * for swap requests in multiplayer environments.
 */
public class ToolbeltNetworkManager {

    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("toolbelt");
    private static int messageID = 0;

    /**
     * Must be called during mod init to register all message handlers.
     */
    public static void registerMessages() {
        // Messages sent from client to server (Side.SERVER processes them)
        INSTANCE.registerMessage(SwapMessageHandler.class, SwapMessage.class, messageID++, Side.SERVER);
    }

    /**
     * Sends a swap request to the server. Called from the client when keybind is pressed.
     */
    public static void sendSwapRequestToServer() {
        INSTANCE.sendToServer(new SwapMessage());
    }
}
