package com.toolbelt.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Client-to-server message requesting a toolbelt swap.
 * Contains no payload — the server determines which player sent it and performs the swap for that player's toolbelt.
 */
public class SwapMessage implements IMessage {

    public SwapMessage() {}

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }
}
