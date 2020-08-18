package net.voxelindustry.brokkgui.wrapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.voxelindustry.brokkgui.internal.IMouseUtil;

public class MouseUtil implements IMouseUtil
{
    private Mouse mouse;

    public int getMouseX()
    {
        if (MinecraftClient.getInstance().currentScreen == null)
            return (int) getMouse().getX();
        return (int) (getMouse().getX() * MinecraftClient.getInstance().getWindow().getScaledWidth() /
                MinecraftClient.getInstance().getWindow().getWidth());
    }

    public int getMouseY()
    {
        if (MinecraftClient.getInstance().currentScreen == null)
            return (int) this.getMouse().getY();

        return (int) (getMouse().getY() * MinecraftClient.getInstance().getWindow().getScaledHeight() /
                MinecraftClient.getInstance().getWindow().getHeight());
    }

    public Mouse getMouse()
    {
        if (this.mouse == null)
            this.mouse = MinecraftClient.getInstance().mouse;
        return this.mouse;
    }
}