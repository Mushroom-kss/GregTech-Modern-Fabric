package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;
import com.gregtechceu.gtceu.api.misc.VoidFluidHandlerItemStack;
import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlungerBehavior implements IToolBehavior {

    public static final PlungerBehavior INSTANCE = PlungerBehavior.create();

    protected PlungerBehavior() {/**/}

    public static PlungerBehavior create() {
        return new PlungerBehavior();
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        IFluidTransfer fluidHandler = FluidTransferHelper.getFluidTransfer(context.getLevel(), context.getClickedPos(), context.getClickedFace());
        if (fluidHandler == null) {
            return InteractionResult.PASS;
        }

        IFluidTransfer handlerToRemoveFrom = fluidHandler;
//                player.isCrouching() ?
//                (fluidHandler instanceof IOFluidTransferList ? ((IOFluidTransferList) fluidHandler).input : null) :
//                (fluidHandler instanceof IOFluidTransferList ? ((IOFluidTransferList) fluidHandler).output : fluidHandler);

        if (handlerToRemoveFrom != null && handlerToRemoveFrom.drain(FluidHelper.getBucket(), true) != null) {
            ToolHelper.onActionDone(context.getPlayer(), context.getLevel(), context.getHand());
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gtceu.tool.behavior.plunger"));
    }

    @Override
    public void init(IGTTool toolItem) {
        FluidStorage.ITEM.registerForItems((itemStack, context) -> new VoidFluidHandlerItemStack(context, FluidHelper.getBucket()) {
            @Override
            public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                long result = super.insert(resource, maxAmount, transaction);
                if (result > 0) {
                    ToolHelper.damageItem(itemStack, null);
                }
                return result;
            }
        }, toolItem.asItem());
    }
}