package com.gregtechceu.gtceu.client.renderer.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputFluid;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputItem;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.client.model.ItemBakedModel;
import com.gregtechceu.gtceu.client.renderer.block.TextureOverrideRenderer;
import com.gregtechceu.gtceu.client.renderer.cover.ICoverableRenderer;
import com.lowdragmc.lowdraglib.client.bakedpipeline.FaceQuad;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.lowdragmc.lowdraglib.client.model.custommodel.ICTMPredicate;
import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.utils.FacadeBlockAndTintGetter;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Mushroom
 * @date 2025/8/14
 * @implNote SiengleMachineRenderer
 */
public class MachineRenderer extends TextureOverrideRenderer implements ICoverableRenderer, IPartRenderer, ICTMPredicate {

    public static final ResourceLocation PIPE_OVERLAY = GTCEu.id("block/overlay/machine/overlay_pipe");
    public static final ResourceLocation FLUID_OUTPUT_OVERLAY = GTCEu.id("block/overlay/machine/overlay_fluid_output");
    public static final ResourceLocation ITEM_OUTPUT_OVERLAY = GTCEu.id("block/overlay/machine/overlay_item_output");

    // 渲染缓存：用于缓存静态部分的quad，key为位置+朝向的组合
    private static final Long2ObjectMap<List<BakedQuad>> STATIC_QUAD_CACHE = new Long2ObjectOpenHashMap<>();
    private static final Map<Long, Long> CACHE_TIMESTAMPS = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_TIME = 5000; // 缓存过期时间（毫秒）

    public MachineRenderer(ResourceLocation modelLocation) {
        super(modelLocation);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean useBlockLight(ItemStack stack) {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean useAO() {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model) {
        if (stack.getItem() instanceof MetaMachineItem machineItem) {
            IItemRendererProvider.disabled.set(true);
            Minecraft.getInstance().getItemRenderer().render(stack, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay,
                    new ItemBakedModel() {
                        @Override
                        @Environment(EnvType.CLIENT)
                        public List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction direction, RandomSource random) {
                            List<BakedQuad> quads = new LinkedList<>();
                            renderMachine(quads, machineItem.getDefinition(), null, Direction.NORTH, direction, random, direction, BlockModelRotation.X0_Y0);
                            return quads;
                        }
                    });
            IItemRendererProvider.disabled.set(false);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final List<BakedQuad> renderModel(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, @Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (state != null && state.getBlock() instanceof MetaMachineBlock machineBlock) {
            var frontFacing = machineBlock.getFrontFacing(state);
            var machine = (level == null || pos == null) ? null : machineBlock.getMachine(level, pos);
            if (machine != null) {
                // 生成缓存key：基于位置、朝向和side
                long cacheKey = generateCacheKey(pos, frontFacing, side);
                
                // 检查缓存是否有效
                Long lastUpdate = CACHE_TIMESTAMPS.get(cacheKey);
                long currentTime = System.currentTimeMillis();
                
                if (lastUpdate != null && (currentTime - lastUpdate) < CACHE_EXPIRY_TIME) {
                    List<BakedQuad> cachedQuads = STATIC_QUAD_CACHE.get(cacheKey);
                    if (cachedQuads != null) {
                        // 返回缓存的副本，避免外部修改
                        return new LinkedList<>(cachedQuads);
                    }
                }
                
                var definition = machine.getDefinition();
                var modelState = ModelFactory.getRotation(frontFacing);
                var modelFacing = side == null ? null : ModelFactory.modelFacing(side, frontFacing);
                var quads = new LinkedList<BakedQuad>();
                
                // render machine additional quads
                renderMachine(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState);
                
                // render auto IO
                if (machine instanceof IAutoOutputItem autoOutputItem) {
                    var itemFace = autoOutputItem.getOutputFacingItems();
                    if (itemFace != null && side == itemFace) {
                        //quads.add(FaceQuad.bakeFace(Overlay, modelFacing, ModelFactory.getBlockSprite(PIPE_OVERLAY), modelState, -1, 0, true, true));
                        quads.add(FaceQuad.bakeFace(modelFacing, ModelFactory.getBlockSprite(PIPE_OVERLAY), modelState));
                        // positive x axis emissive overlay renderer
                    }
                }
                if (machine instanceof IAutoOutputFluid autoOutputFluid) {
                    var fluidFace = autoOutputFluid.getOutputFacingFluids();
                    if (fluidFace != null && side == fluidFace) {
                        //quads.add(FaceQuad.bakeFace(Overlay, modelFacing, ModelFactory.getBlockSprite(PIPE_OVERLAY), modelState, -1, 0, true, true));
                        quads.add(FaceQuad.bakeFace(modelFacing, ModelFactory.getBlockSprite(PIPE_OVERLAY), modelState));
                    }
                }

                if (machine instanceof IAutoOutputItem autoOutputItem) {
                    var itemFace = autoOutputItem.getOutputFacingItems();
                    if (itemFace != null && side == itemFace) {
                        if (autoOutputItem.isAutoOutputItems()) {
                            //quads.add(FaceQuad.bakeFace(Overlay, modelFacing, ModelFactory.getBlockSprite(ITEM_OUTPUT_OVERLAY), modelState, -101, 15, true, true));
                            quads.add(FaceQuad.bakeFace(modelFacing, ModelFactory.getBlockSprite(ITEM_OUTPUT_OVERLAY), modelState, -101, 15));
                        }
                    }
                }

                if (machine instanceof IAutoOutputFluid autoOutputFluid) {
                    var fluidFace = autoOutputFluid.getOutputFacingFluids();
                    if (fluidFace != null && side == fluidFace) {
                        if (autoOutputFluid.isAutoOutputFluids()) {
                            //quads.add(FaceQuad.bakeFace(Overlay, modelFacing, ModelFactory.getBlockSprite(FLUID_OUTPUT_OVERLAY), modelState, -101, 15, true, true));
                            quads.add(FaceQuad.bakeFace(modelFacing, ModelFactory.getBlockSprite(FLUID_OUTPUT_OVERLAY), modelState, -101, 15));
                        }
                    }
                }

                // render covers
                ICoverableRenderer.super.renderCovers(quads, side, rand, machine.getCoverContainer(), modelFacing, modelState);
                
                // 更新缓存（只缓存静态部分，不包括覆盖板等动态内容）
                // 注意：这里我们缓存完整的quad列表，但需要在机器状态改变时清除缓存
                STATIC_QUAD_CACHE.put(cacheKey, new LinkedList<>(quads));
                CACHE_TIMESTAMPS.put(cacheKey, currentTime);
                
                return quads;
            }
        }
        return Collections.emptyList();
    }
    
    /**
     * 生成缓存key，基于位置、朝向和side
     */
    private long generateCacheKey(BlockPos pos, Direction frontFacing, Direction side) {
        // 使用位置、朝向和side组合生成唯一key
        long key = pos.asLong(); // BlockPos的long表示
        key = key * 31 + frontFacing.ordinal();
        key = key * 31 + (side == null ? 6 : side.ordinal()); // 6表示null
        return key;
    }
    
    /**
     * 清除指定位置的缓存
     */
    public static void clearCache(BlockPos pos) {
        // 清除该位置所有朝向的缓存
        for (Direction facing : Direction.values()) {
            for (int side = 0; side <= 6; side++) {
                long key = generateCacheKeyStatic(pos, facing, side == 6 ? null : Direction.from3DDataValue(side));
                STATIC_QUAD_CACHE.remove(key);
                CACHE_TIMESTAMPS.remove(key);
            }
        }
    }
    
    private static long generateCacheKeyStatic(BlockPos pos, Direction frontFacing, Direction side) {
        long key = pos.asLong();
        key = key * 31 + frontFacing.ordinal();
        key = key * 31 + (side == null ? 6 : side.ordinal());
        return key;
    }
    
    /**
     * 定期清理过期缓存
     */
    @Environment(EnvType.CLIENT)
    public static void cleanupExpiredCache() {
        long currentTime = System.currentTimeMillis();
        CACHE_TIMESTAMPS.entrySet().removeIf(entry -> {
            if ((currentTime - entry.getValue()) > CACHE_EXPIRY_TIME) {
                STATIC_QUAD_CACHE.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

    @Environment(EnvType.CLIENT)
    public void renderBaseModel(List<BakedQuad> quads, MachineDefinition definition, @Nullable MetaMachine machine, Direction frontFacing, @Nullable Direction side, RandomSource rand) {
        quads.addAll(getRotatedModel(frontFacing).getQuads(definition.defaultBlockState(), side, rand));
    }

    /**
     * Render machine block / item
     *
     * @param quads       quads pipeline
     * @param definition  machine definition
     * @param machine     if null, rendering item
     * @param frontFacing front facing
     * @param side        quad side
     * @param rand        random
     * @param modelFacing model facing before rotation
     * @param modelState  uvLocked rotation according to the front facing
     */
    @Environment(EnvType.CLIENT)
    public void renderMachine(List<BakedQuad> quads, MachineDefinition definition, @Nullable MetaMachine machine, Direction frontFacing, @Nullable Direction side, RandomSource rand, @Nullable Direction modelFacing, ModelState modelState) {
        if (!(machine instanceof IMultiPart part) || !part.replacePartModelWhenFormed() || !renderReplacedPartMachine(quads, part, frontFacing, side, rand, modelFacing, modelState)) {
            renderBaseModel(quads, definition, machine, frontFacing, side, rand);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        super.onPrepareTextureAtlas(atlasName, register);
        if (atlasName.equals(TextureAtlas.LOCATION_BLOCKS)) {
            register.accept(PIPE_OVERLAY);
            register.accept(FLUID_OUTPUT_OVERLAY);
            register.accept(ITEM_OUTPUT_OVERLAY);
        }
    }

    //////////////////////////////////////
    //**********     CTM     ***********//
    //////////////////////////////////////
    @Override
    public boolean isConnected(BlockAndTintGetter level, BlockState state, BlockPos pos, BlockState sourceState, BlockPos sourcePos, Direction side) {
        var stateAppearance = FacadeBlockAndTintGetter.getAppearance(state, level, pos, side, sourceState, sourcePos);
        var sourceStateAppearance = FacadeBlockAndTintGetter.getAppearance(sourceState, level, sourcePos, side, state, pos);
//        var machine = MetaMachine.getMachine(level, pos);
//        if (machine != null) {
//            if (machine instanceof IMultiController controller && !controller.isFormed()) {
//                return false;
//            }
//        }
        return stateAppearance == sourceStateAppearance;
    }
}
