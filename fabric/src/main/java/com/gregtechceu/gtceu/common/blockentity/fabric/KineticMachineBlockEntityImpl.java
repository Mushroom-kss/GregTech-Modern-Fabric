package com.gregtechceu.gtceu.common.blockentity.fabric;

import com.gregtechceu.gtceu.api.blockentity.fabric.MetaMachineBlockEntityImpl;
import com.gregtechceu.gtceu.common.blockentity.KineticMachineBlockEntity;
import com.lowdragmc.lowdraglib.LDLib;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
//import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * @author KilaBash
 * @date 2023/3/31
 * @implNote KineticMachineBlockEntityImpl
 */
public class KineticMachineBlockEntityImpl extends KineticMachineBlockEntity{

    protected KineticMachineBlockEntityImpl(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static KineticMachineBlockEntity create(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        return new KineticMachineBlockEntityImpl(typeIn, pos, state);
    }

    public static void onBlockEntityRegister(BlockEntityType blockEntityType, NonNullSupplier<BiFunction<VisualizationContext, KineticMachineBlockEntity, BlockEntityVisual<? super KineticMachineBlockEntity>>> instanceFactory, boolean renderNormally) {
        MetaMachineBlockEntityImpl.onBlockEntityRegister(blockEntityType);
        /*if (instanceFactory != null && LDLib.isClient()) {
            InstancedRenderRegistry.configure(blockEntityType)
                    .factory(instanceFactory.get())
                    .skipRender(be -> !renderNormally)
                    .apply();
        }*/
    }
}
