package net.voxelindustry.brokkgui.wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.voxelindustry.brokkgui.internal.IMouseUtil;

public class MouseUtil implements IMouseUtil
{
    private MouseHelper mouse;

    public int getMouseX()
    {
        if (Minecraft.getInstance().currentScreen == null)
            return (int) getMouse().getMouseX();
        return (int) (getMouse().getMouseX() * Minecraft.getInstance().mainWindow.getScaledWidth() /
                Minecraft.getInstance().mainWindow.getWidth());
    }

    public int getMouseY()
    {
        if (Minecraft.getInstance().currentScreen == null)
            return (int) this.getMouse().getMouseY();

        return (int) (getMouse().getMouseY() * Minecraft.getInstance().mainWindow.getScaledHeight() /
                Minecraft.getInstance().mainWindow.getHeight());
    }

    public MouseHelper getMouse()
    {
        if (this.mouse == null)
            this.mouse = Minecraft.getInstance().mouseHelper;
        return this.mouse;
    }
}