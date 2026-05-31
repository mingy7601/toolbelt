package com.toolbelt.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Forge @Config annotations for scalar values.
 * Config file location: config/toolbelt.cfg
 */
@Config(modid = "toolbelt", name = "toolbelt", type = Config.Type.INSTANCE)
public class ToolbeltConfig {

    /** Minimum time between swaps in seconds. Default: 0.5 */
    @Config.Comment("Minimum time between swaps in seconds")
    @Config.Name("Swap Cooldown Seconds")
    public static double swapCooldownSeconds = 0.5;

    /**
     * Loads the config from disk. Should be called during preInit.
     */
    public static void load() {
        ConfigManager.sync("toolbelt", Config.Type.INSTANCE);
    }

    /**
     * Reloads the config when a change event is fired (for in-game config GUI).
     */
    @Mod.EventBusSubscriber(modid = "toolbelt")
    public static class Handler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if ("toolbelt".equals(event.getModID())) {
                ConfigManager.sync("toolbelt", Config.Type.INSTANCE);
            }
        }
    }

    /**
     * Returns the current cooldown value in seconds.
     */
    public static double getCooldown() {
        return swapCooldownSeconds;
    }
}
