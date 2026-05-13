package com.gregtechceu.gtceu;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.addon.AddonFinder;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.capability.compat.EUToREProvider;
import com.gregtechceu.gtceu.api.capability.GTCapability;
import com.gregtechceu.gtceu.api.machine.feature.IInteractedMachine;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.common.CommonProxy;
import com.gregtechceu.gtceu.common.ServerCommands;
import com.gregtechceu.gtceu.data.loader.FluidVeinLoader;
import com.gregtechceu.gtceu.data.loader.OreDataLoaderImpl;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.TaskHandler;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;

import lombok.Getter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;

import org.jetbrains.annotations.ApiStatus;
//import org.lwjgl.system.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.reborn.energy.api.EnergyStorage;

public class GTCEu implements ModInitializer {
	public static final String MOD_ID = "gtceu";
    public static final String NAME = "GregTechCEu";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	/* Just for pre-initialization stage */
	@Getter
    private static boolean highTier;
    private static boolean highTierInitialized;

	@Override
	public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Hello Fabric world!");
        LOGGER.info("{} is initializing on platform: {}", NAME, Platform.platformName());
        CommonProxy.init();
        CustomIngredientSerializer.register(SizedIngredient.Serializer.INSTANCE);
        AttackBlockCallback.EVENT.register(((player, world, hand, pos, direction) -> {
            var blockState = world.getBlockState(pos);
            if (blockState.hasBlockEntity() && blockState.getBlock() instanceof MetaMachineBlock block
                    && block.getMachine(world, pos) instanceof IInteractedMachine machine) {
                if (machine.onLeftClick(player, world, hand, pos, direction)) {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }));
        // register server commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ServerCommands.createServerCommands().forEach(dispatcher::register));

        ServerTickEvents.END_WORLD_TICK.register(TaskHandler::onTickUpdate);
        ServerWorldEvents.UNLOAD.register((server, world) -> TaskHandler.onWorldUnLoad(world));

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new OreDataLoaderImpl());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FluidVeinLoader());

        if (GTCEu.isRebornEnergyLoaded()) {
            GTCapability.CAPABILITY_ENERGY.registerFallback(new EUToREProvider(EnergyStorage.SIDED::find));
        }
	}

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, FormattingUtil.toLowerCaseUnder(path));
    }

    public static String appendIdString(String id) {
        return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
    }

    public static ResourceLocation appendId(String id) {
        String[] strings = new String[]{"gtceu", id};
        int i = id.indexOf(':');
        if (i >= 0) {
            strings[1] = id.substring(i + 1);
            if (i >= 1) {
                strings[0] = id.substring(0, i);
            }
        }
        return new ResourceLocation(strings[0], strings[1]);
    }

    public static boolean isKubeJSLoaded() {
        return LDLib.isModLoaded(GTValues.MODID_KUBEJS);
    }

    public static boolean isCreateLoaded() {
        return LDLib.isModLoaded(GTValues.MODID_CREATE);
    }

    public static boolean isIrisOculusLoaded() {
        return LDLib.isModLoaded(GTValues.MODID_IRIS) || LDLib.isModLoaded(GTValues.MODID_OCULUS);
    }

    public static boolean isSodiumRubidiumEmbeddiumLoaded() {
        return LDLib.isModLoaded(GTValues.MODID_SODIUM) || LDLib.isModLoaded(GTValues.MODID_RUBIDIUM) ||LDLib.isModLoaded(GTValues.MODID_EMBEDDIUM);
    }

    public static boolean isRebornEnergyLoaded() {
        return Platform.isForge() || LDLib.isModLoaded(GTValues.MODID_REBORN_ENERGY);
    }

    public static boolean isAE2Loaded() {
        return LDLib.isModLoaded(GTValues.MODID_APPENG);
    }

    public static boolean isAlmostUnifiedLoaded() {
        return LDLib.isModLoaded(GTValues.MODID_ALMOSTUNIFIED);
    }


    /**
     * Initializes High-Tier. Internal use only, do not attempt to call this.
     */
    @ApiStatus.Internal
    public static void initializeHighTier() {
        if (highTierInitialized) throw new IllegalStateException("High-Tier is already initialized.");
        highTier = ConfigHolder.INSTANCE.machines.highTierContent || AddonFinder.getAddons().stream().anyMatch(IGTAddon::requiresHighTier) || Platform.isDevEnv();
        highTierInitialized = true;

        if (isHighTier()) GTCEu.LOGGER.info("High-Tier is Enabled.");
        else GTCEu.LOGGER.info("High-Tier is Disabled.");
    }
}