package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.voxelindustry.brokkcolor.Color;
import net.voxelindustry.brokkgui.internal.IRenderCommandReceiver;

import java.util.List;
import java.util.function.Consumer;

public interface ItemStackRenderCommandReceiver extends IRenderCommandReceiver
{
    void drawItemStack(float xStart,
                       float yStart,
                       float width,
                       float height,
                       float zLevel,
                       ItemStack itemStack,
                       String amountString,
                       Color color);

    void drawItemStackTooltip(int xStart,
                              int yStart,
                              float windowWidth,
                              float windowHeight,
                              ItemStack itemStack,
                              Consumer<List<Text>> stackTooltipModifier);
}
