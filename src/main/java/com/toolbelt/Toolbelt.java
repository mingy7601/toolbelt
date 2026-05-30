package com.toolbelt;

import com.toolbelt.config.ToolbeltConfig;
import com.toolbelt.handler.ToolbeltSwapHandler;
import com.toolbelt.item.ToolbeltItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

        // Load config from disk
        ToolbeltConfig.load();

        // Register the toolbelt item (handled by Mod.EventBusSubscriber in ToolbeltItem)
        // Register crafting recipe (handled by Mod.EventBusSubscriber in ToolbeltRecipe)

        // Register client-side model
        if (event.getSide() == Side.CLIENT) {
            ToolbeltItem.registerModel();
        }

        LOGGER.info("Toolbelt mod pre-initialization complete.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Toolbelt mod initialized.");

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
