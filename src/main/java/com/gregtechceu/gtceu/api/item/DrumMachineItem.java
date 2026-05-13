package com.gregtechceu.gtceu.api.item;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.api.misc.FluidHandlerItemStack;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.world.item.Item;

/**
 * @author KilaBash
 * @date 2023/3/28
 * @implNote DrumMachineItem
 */
public class DrumMachineItem extends MetaMachineItem {
    protected DrumMachineItem(IMachineBlock block, Properties properties) {
        super(block, properties);
        FluidStorage.ITEM.registerForItems((itemStack, context) -> new FluidHandlerItemStack(context, GTMachines.DRUM_CAPACITY.get(getDefinition())), this);
    }

    public static DrumMachineItem create(IMachineBlock block, Item.Properties properties) {
        return new DrumMachineItem(block, properties);
    }

}
