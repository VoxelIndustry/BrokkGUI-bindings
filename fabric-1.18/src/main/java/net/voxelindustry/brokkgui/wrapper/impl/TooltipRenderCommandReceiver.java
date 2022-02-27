package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.voxelindustry.brokkgui.internal.IRenderCommandReceiver;

import java.util.List;

public interface TooltipRenderCommandReceiver extends IRenderCommandReceiver
{
    void drawHoveringTooltip(int xStart,
                              int yStart,
                              float windowWidth,
                              float windowHeight,
                             List<TooltipComponent> components);
}
