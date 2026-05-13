package com.gregtechceu.gtceu.client.renderer.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.block.BoilerBurnerType;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.lowdragmc.lowdraglib.client.bakedpipeline.FaceQuad;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/3/16
 * @implNote LargeBoilerRenderer
 */
public class LargeBoilerRenderer extends WorkableCasingMachineRenderer implements IControllerRenderer {
    public static final ResourceLocation BLOOM_OVERLAY = GTCEu.id("block/casings/burner/machine_casing_burner_bloom");
    public final BoilerBurnerType burner;

    public LargeBoilerRenderer(ResourceLocation texture, BoilerBurnerType burner, ResourceLocation workableModel) {
        super(texture, workableModel, false);
        this.burner = burner;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderPartModel(List<BakedQuad> quads, IMultiController machine, IMultiPart part, Direction frontFacing, @Nullable Direction side, RandomSource rand, Direction modelFacing, ModelState modelState) {
        // We have to render it ourselves to avoid uv issues
        var PartOverlay = new AABB(0.0001 , 0.0001 , 0.0001 , 0.9999 , 0.9999, 0.9999);
        if (machine.self().getPos().below().getY() == part.self().getPos().getY()) {
            // converted burner renderer
            if (side != null && modelFacing != null) {
                if (side == Direction.UP) {
                    //quads.add(FaceQuad.bakeFace(modelFacing, ModelFactory.getBlockSprite(burner.top()), modelState)); converted burner top surface renderer,but no block will replaced at top.
                } else if (side == Direction.DOWN) {
                    quads.add(FaceQuad.bakeFace(PartOverlay, modelFacing, ModelFactory.getBlockSprite(burner.bottom()), modelState, -1, 0, true, true));
                    // converted burner buttom surface renderer
                } else {
                    quads.add(FaceQuad.bakeFace(PartOverlay, modelFacing, ModelFactory.getBlockSprite(burner.side()), modelState, -1, 0, true, true));
                    // converted burner side surface renderer
                    if (machine instanceof IRecipeLogicMachine recipeLogicMachine && recipeLogicMachine.getRecipeLogic().isWorking()) {
                        quads.add(FaceQuad.bakeFace(modelFacing, ModelFactory.getBlockSprite(BLOOM_OVERLAY), modelState, -101, 15));
                        // converted burner emissive overlay renderer
                    }
                }
            }
        } else {
            if (side != null && modelFacing != null) {
                quads.add(FaceQuad.bakeFace(PartOverlay, modelFacing, ModelFactory.getBlockSprite(baseCasing), modelState, -1, 0, true, true));
                // converted casing surface renderer
            }
        }
    }
}
