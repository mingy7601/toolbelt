package com.toolbelt;

import com.toolbelt.config.ToolbeltConfig;
import com.toolbelt.handler.ToolbeltSwapHandler;
import com.toolbelt.item.ToolbeltItem;
import com.toolbelt.recipe.ToolbeltRecipe;
import com.toolbelt.network.ToolbeltNetworkManager;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = Toolbelt.MODID,
    name = Toolbelt.NAME,
    version = Toolbelt.VERSION,
    dependencies = "required:baubles"
)
public class Toolbelt {

    public static final String MODID = "toolbelt";
    public static final String NAME = "Toolbelt";
    public static final String VERSION = "${VERSION}";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Toolbelt mod is initializing...");
        ToolbeltConfig.load();
        ToolbeltRecipe.onPreInit();

        // Register item registry event handler explicitly
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ItemRegistrationHandler());

        LOGGER.info("Toolbelt mod pre-initialization complete.");
    }

    public static class ItemRegistrationHandler {
        @SubscribeEvent
        public void registerItems(RegistryEvent.Register<Item> evt) {
            ToolbeltItem.INSTANCE = new ToolbeltItem();
            evt.getRegistry().register(ToolbeltItem.INSTANCE);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Toolbelt mod initialized.");

        // Register network channel (works on both client and server)
        ToolbeltNetworkManager.registerMessages();

        // Register the swap handler for key input events (client-side only)
        if (event.getSide() == Side.CLIENT) {
            ToolbeltSwapHandler.registerKeybind();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ToolbeltSwapHandler());
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Toolbelt mod post-initialization complete.");
    }
}
