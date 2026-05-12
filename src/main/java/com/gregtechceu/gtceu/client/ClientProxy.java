package com.gregtechceu.gtceu.client;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.compass.GTCompassUIConfig;
import com.gregtechceu.gtceu.api.gui.compass.GTRecipeViewCreator;
import com.gregtechceu.gtceu.api.gui.compass.MultiblockAction;
import com.gregtechceu.gtceu.client.renderer.BlockHighLightRenderer;
import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.lowdragmc.lowdraglib.gui.compass.component.RecipeComponent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/7/30
 * @implNote ClientProxy
 */
@Environment(EnvType.CLIENT)
public class ClientProxy implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RecipeComponent.registerRecipeViewCreator(new GTRecipeViewCreator());
        CompassManager.INSTANCE.registerUIConfig(GTCEu.MOD_ID, new GTCompassUIConfig());
        CompassManager.INSTANCE.registerAction("multiblock", MultiblockAction::new);
        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            if (Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult) {
                BlockHighLightRenderer.renderBlockHighLight(worldRenderContext.matrixStack(), worldRenderContext.camera(), hitResult, worldRenderContext.consumers(), worldRenderContext.tickDelta());
            }
            return true;
        });
        ItemTooltipCallback.EVENT.register(TooltipsHandler::appendTooltips);
        // register client commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            List<LiteralArgumentBuilder<FabricClientCommandSource>> commands = ClientCommands.createClientCommands();
            commands.forEach(dispatcher::register);
        });

        ClientTickEvents.END_CLIENT_TICK.register(listener -> {
            TooltipHelper.onClientTick();
            GTValues.CLIENT_TIME++;
        });
    }
}
