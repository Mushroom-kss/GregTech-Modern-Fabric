package com.gregtechceu.gtceu.api.blockentity;

import appeng.api.networking.IInWorldGridNodeHost;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapability;
import com.gregtechceu.gtceu.api.capability.GTEnergyHelperImpl;
import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.ILaserContainer;
import com.gregtechceu.gtceu.api.capability.IPlatformEnergyStorage;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.misc.LaserContainerList;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.pipenet.longdistance.ILDEndpoint;
import com.gregtechceu.gtceu.common.pipelike.fluidpipe.longdistance.LDFluidEndpointMachine;
import com.gregtechceu.gtceu.common.pipelike.item.longdistance.LDItemEndpointMachine;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.side.fluid.fabric.FluidTransferHelperImpl;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.lowdragmc.lowdraglib.side.item.fabric.ItemTransferHelperImpl;
import com.lowdragmc.lowdraglib.syncdata.managed.MultiManagedStorage;

import lombok.Getter;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import team.reborn.energy.api.EnergyStorage;

import java.util.Set;

/**
 * @author KilaBash
 * @date 2023/2/17
 * @implNote MetaMachineBlockEntity
 */
public class MetaMachineBlockEntity extends BlockEntity implements IMachineBlockEntity {
    public final MultiManagedStorage managedStorage = new MultiManagedStorage();
    @Getter
    public final MetaMachine metaMachine;
    private final long offset = GTValues.RNG.nextInt(20);

    protected MetaMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.metaMachine = getDefinition().createMetaMachine(this);
    }

    public static void onBlockEntityRegister(BlockEntityType<BlockEntity> type) {
        GTCapability.CAPABILITY_COVERABLE.registerForBlockEntity((blockEntity, direction) -> ((IMachineBlockEntity)blockEntity).getMetaMachine().getCoverContainer(), type);
        GTCapability.CAPABILITY_TOOLABLE.registerForBlockEntity((blockEntity, direction) -> ((IMachineBlockEntity)blockEntity).getMetaMachine(), type);
        GTCapability.CAPABILITY_WORKABLE.registerForBlockEntity((blockEntity, direction) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof IWorkable workable) {
                return workable;
            }
            for (MachineTrait trait : ((IMachineBlockEntity)blockEntity).getMetaMachine().getTraits()) {
                if (trait instanceof IWorkable workable) {
                    return workable;
                }
            }
            return null;
        }, type);
        GTCapability.CAPABILITY_CONTROLLABLE.registerForBlockEntity((blockEntity, direction) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof IControllable controllable) {
                return controllable;
            }
            for (MachineTrait trait : ((IMachineBlockEntity)blockEntity).getMetaMachine().getTraits()) {
                if (trait instanceof IControllable controllable) {
                    return controllable;
                }
            }
            return null;
        }, type);
        GTCapability.CAPABILITY_RECIPE_LOGIC.registerForBlockEntity((blockEntity, direction) -> {
            for (MachineTrait trait : ((IMachineBlockEntity)blockEntity).getMetaMachine().getTraits()) {
                if (trait instanceof RecipeLogic recipeLogic) {
                    return recipeLogic;
                }
            }
            return null;
        }, type);
        GTCapability.CAPABILITY_ENERGY.registerForBlockEntity((blockEntity, side) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof IEnergyContainer energyContainer) {
                return  energyContainer;
            }
            var list = ((IMachineBlockEntity)blockEntity).getMetaMachine().getTraits().stream().filter(IEnergyContainer.class::isInstance).filter(t -> t.hasCapability(side)).map(IEnergyContainer.class::cast).toList();
            return list.isEmpty() ? null : list.size() == 1 ? list.get(0) : new EnergyContainerList(list);
        }, type);
        GTCapability.CAPABILITY_CLEANROOM_RECEIVER.registerForBlockEntity((blockEntity, direction) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof ICleanroomReceiver cleanroomReceiver) {
                return cleanroomReceiver;
            }
            return null;
        }, type);
        GTCapability.CAPABILITY_MAINTENANCE_MACHINE.registerForBlockEntity((blockEntity, direction) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof IMaintenanceMachine maintenanceMachine) {
                return maintenanceMachine;
            }
            return null;
        }, type);
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, side) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof LDItemEndpointMachine fluidEndpointMachine) {
                if (fluidEndpointMachine.getLevel().isClientSide) return null;
                ILDEndpoint endpoint = fluidEndpointMachine.getLink();
                if (endpoint == null) return null;
                Direction outputFacing = fluidEndpointMachine.getOutputFacing();
                IItemTransfer transfer = ItemTransferHelperImpl.getItemTransfer(blockEntity.getLevel(), endpoint.getPos().relative(outputFacing), outputFacing.getOpposite());
                if (transfer != null) {
                    return ItemTransferHelperImpl.toItemVariantStorage(new LDItemEndpointMachine.ItemHandlerWrapper(transfer));
                }
            }
            var transfer = ((IMachineBlockEntity)blockEntity).getMetaMachine().getItemTransferCap(side, true);
            return transfer == null ? null : ItemTransferHelperImpl.toItemVariantStorage(transfer);
        }, type);
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, side) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof LDFluidEndpointMachine fluidEndpointMachine) {
                if (fluidEndpointMachine.getLevel().isClientSide) return null;
                ILDEndpoint endpoint = fluidEndpointMachine.getLink();
                if (endpoint == null) return null;
                Direction outputFacing = fluidEndpointMachine.getOutputFacing();
                IFluidTransfer transfer = FluidTransferHelper.getFluidTransfer(blockEntity.getLevel(), endpoint.getPos().relative(outputFacing), outputFacing.getOpposite());
                if (transfer != null) {
                    return FluidTransferHelperImpl.toFluidVariantStorage(new LDFluidEndpointMachine.FluidHandlerWrapper(transfer));
                }
            }
            var transfer = ((IMachineBlockEntity)blockEntity).getMetaMachine().getFluidTransferCap(side, true);
            return transfer == null ? null : FluidTransferHelperImpl.toFluidVariantStorage(transfer);
        }, type);
        GTCapability.CAPABILITY_LASER.registerForBlockEntity((blockEntity, side) -> {
            if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof ILaserContainer energyContainer) {
                return energyContainer;
            }
            var list = ((IMachineBlockEntity)blockEntity).getMetaMachine().getTraits().stream().filter(ILaserContainer.class::isInstance).filter(t -> t.hasCapability(side)).map(ILaserContainer.class::cast).toList();
            return list.isEmpty() ? null : list.size() == 1 ? list.get(0) : new LaserContainerList(list);
        }, type);
        if (GTCEu.isRebornEnergyLoaded()) {
            EnergyStorage.SIDED.registerForBlockEntity((blockEntity, side) -> {
                if (((IMachineBlockEntity)blockEntity).getMetaMachine() instanceof IPlatformEnergyStorage platformEnergyStorage) {
                    return GTEnergyHelperImpl.toEnergyStorage(platformEnergyStorage);
                }
                var list = ((IMachineBlockEntity)blockEntity).getMetaMachine().getTraits().stream().filter(IPlatformEnergyStorage.class::isInstance).filter(t -> t.hasCapability(side)).map(IPlatformEnergyStorage.class::cast).toList();
                // TODO wrap list in the future
                return list.isEmpty() ? null : GTEnergyHelperImpl.toEnergyStorage(list.get(0));
            }, type);
        }
        if (GTCEu.isAE2Loaded()) {
            IInWorldGridNodeHost.LOOKUP.registerForBlockEntity((blockEntity, side) -> {
                MetaMachine machine = ((IMachineBlockEntity)blockEntity).getMetaMachine();
                if (machine instanceof IInWorldGridNodeHost gridNodeHost) {
                    return gridNodeHost;
                }

                var list = machine.getTraits().stream().filter(IInWorldGridNodeHost.class::isInstance).map(IInWorldGridNodeHost.class::cast).toList();
                // TODO wrap list in the future (or not.)
                return list.isEmpty() ? null : list.get(0);
            }, type);
        }
    }

    @Override
    public MultiManagedStorage getRootStorage() {
        return managedStorage;
    }

    @Override
    public boolean triggerEvent(int id, int para) {
        if (id == 1) { // chunk re render
            if (level != null && level.isClientSide) {
                scheduleRenderUpdate();
            }
            return true;
        }
        return false;
    }


    @Override
    public long getOffset() {
        return offset;
    }

    public static MetaMachineBlockEntity createBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new MetaMachineBlockEntity(type, pos, blockState);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        metaMachine.onUnload();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        metaMachine.onLoad();
    }

    @Override
    public boolean shouldRenderGrid(Player player, ItemStack held, Set<GTToolType> toolTypes) {
        return metaMachine.shouldRenderGrid(player, held, toolTypes);
    }

    @Override
    public ResourceTexture sideTips(Player player, Set<GTToolType> toolTypes, Direction side) {
        return metaMachine.sideTips(player, toolTypes, side);
    }
}
