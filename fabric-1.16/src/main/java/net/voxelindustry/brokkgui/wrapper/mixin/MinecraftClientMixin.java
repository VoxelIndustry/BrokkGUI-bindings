package net.voxelindustry.brokkgui.wrapper.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.voxelindustry.brokkgui.wrapper.accessible.IAccessibleItemColorMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IAccessibleItemColorMap
{
    @Shadow
    private ItemColors itemColors;

    @Override
    public ItemColors getItemColorMap()
    {
        return this.itemColors;
    }
}