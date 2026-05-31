package com.toolbelt.recipe;

import com.toolbelt.Toolbelt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Crafting recipe registration for the Toolbelt item.
 *
 * <p>The recipe is defined as a JSON file at
 * {@code assets/toolbelt/recipes/toolbelt.json} and loaded automatically by
 * Forge's built-in JSON recipe loader. No Java-side registration is required.</p>
 *
 * <h3>Recipe layout</h3>
 * <pre>
 * [Leather][Leather][Leather]
 * [Leather][       ][Leather]
 * [Leather][Leather][Leather]
 * </pre>
 *
 * <p>The empty center slot represents the belt buckle hole — a classic
 * Minecraft crafting pattern (like TNT, trapdoors).</p>
 */
public class ToolbeltRecipe {

    private static final Logger LOGGER = LogManager.getLogger("ToolbeltRecipes");

    /**
     * Logs that recipes have been loaded. Called during preInit for visibility
     * in the log; actual recipe loading is handled by Forge's JSON loader.
     */
    public static void onPreInit() {
        LOGGER.info("Crafting recipes loaded from assets/{}/recipes/", Toolbelt.MODID);
    }
}
