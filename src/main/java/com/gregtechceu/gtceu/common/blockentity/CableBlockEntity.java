package com.gregtechceu.gtceu.common.blockentity;

import com.gregtechceu.gtceu.api.blockentity.PipeBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapability;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.block.CableBlock;
import com.gregtechceu.gtceu.common.pipelike.cable.CableData;
import com.gregtechceu.gtceu.common.pipelike.cable.EnergyNet;
import com.gregtechceu.gtceu.common.pipelike.cable.EnergyNetHandler;
import com.gregtechceu.gtceu.common.pipelike.cable.Insulation;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/3/1
 * @implNote CableBlockEntity
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CableBlockEntity extends PipeBlockEntity<Insulation, CableData> {
    protected WeakReference<EnergyNet> currentEnergyNet = new WeakReference<>(null);

    public CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static CableBlockEntity create(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new CableBlockEntity(type, pos, blockState);
    }

    @Override
    public boolean canAttachTo(Direction side) {
        if (level != null) {
            if (level.getBlockEntity(getBlockPos().relative(side)) instanceof CableBlockEntity) {
                return false;
            }
            return GTCapabilityHelper.getEnergyContainer(level, getBlockPos().relative(side), side.getOpposite()) != null;
        }
        return false;
    }

    @Nullable
    private EnergyNet getEnergyNet() {
        if (level instanceof ServerLevel serverLevel && getBlockState().getBlock() instanceof CableBlock cableBlock) {
            EnergyNet currentEnergyNet = this.currentEnergyNet.get();
            if (currentEnergyNet != null && currentEnergyNet.isValid() && currentEnergyNet.containsNode(getBlockPos()))
                return currentEnergyNet; //return current net if it is still valid
            // 添加空值检查以防止崩溃
            var worldPipeNet = cableBlock.getWorldPipeNet(serverLevel);
            if (worldPipeNet != null) {
                currentEnergyNet = worldPipeNet.getNetFromPos(getBlockPos());
                if (currentEnergyNet != null) {
                    this.currentEnergyNet = new WeakReference<>(currentEnergyNet);
                }
            }
        }
        return this.currentEnergyNet.get();
    }

    @Nullable
    public IEnergyContainer getEnergyContainer(@Nullable Direction side) {
        if (side != null && isBlocked(side)) return null;
        if (isRemote()) return IEnergyContainer.DEFAULT;
        var ENet = getEnergyNet();
        if (ENet != null) {
            return new EnergyNetHandler(ENet, this);
        }
        return IEnergyContainer.DEFAULT;
    }

    public static void onBlockEntityRegister(BlockEntityType<CableBlockEntity> type) {
        GTCapability.CAPABILITY_ENERGY.registerForBlockEntity(CableBlockEntity::getEnergyContainer, type);
        GTCapability.CAPABILITY_COVERABLE.registerForBlockEntity((blockEntity, direction) -> blockEntity.getCoverContainer(), type);
        GTCapability.CAPABILITY_TOOLABLE.registerForBlockEntity((blockEntity, direction) -> blockEntity, type);
    }

    //////////////////////////////////////
    //*******     Interaction    *******//
    //////////////////////////////////////

    @Override
    public ResourceTexture getPipeTexture(boolean isBlock) {
        return isBlock ? GuiTextures.TOOL_WIRE_CONNECT : GuiTextures.TOOL_WIRE_BLOCK;
    }

    @Override
    protected GTToolType getPipeTuneTool() {
        return GTToolType.WIRE_CUTTER;
    }
}