package net.voxelindustry.brokkgui.demo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;

public class ContainerDemo extends Container
{
    public ContainerDemo(ContainerType containerType, int windowId, final PlayerInventory player)
    {
        super(containerType, windowId);

        int l;
        for (l = 0; l < 3; ++l)
            for (int i1 = 0; i1 < 9; ++i1)
                this.addSlot(new Slot(player, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
        for (l = 0; l < 9; ++l)
            this.addSlot(new Slot(player, l, 8 + l * 18, 142));
    }

    @Override
    public boolean canInteractWith(final PlayerEntity player)
    {
        return true;
    }
}