package com.gregtechceu.gtceu.common.blockentity.forge;

import com.gregtechceu.gtceu.api.blockentity.forge.MetaMachineBlockEntityImpl;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.blockentity.KineticMachineBlockEntity;
import com.lowdragmc.lowdraglib.LDLib;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
//import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
//import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * @author KilaBash
 * @date 2023/3/31
 * @implNote KineticMachineBlockEntityImpl
 */
public class KineticMachineBlockEntityImpl extends KineticMachineBlockEntity {
    
    protected KineticMachineBlockEntityImpl(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static KineticMachineBlockEntity create(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        return new KineticMachineBlockEntityImpl(typeIn, pos, state);
    }

    /*@Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        var result = MetaMachineBlockEntityImpl.getCapability(getMetaMachine(), cap, side);
        return result == null ? super.getCapability(cap, side) : result;
    }*/

    public static void onBlockEntityRegister(BlockEntityType blockEntityType, NonNullSupplier<BiFunction<VisualizationContext, KineticMachineBlockEntity, BlockEntityVisual<? super KineticMachineBlockEntity>>> instanceFactory, boolean renderNormally) {
        // 空实现，因为Flywheel集成需要更新到新版本API
    }
}