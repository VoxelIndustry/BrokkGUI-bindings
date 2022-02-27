package net.voxelindustry.brokkgui.wrapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.voxelindustry.brokkgui.internal.IBrokkGuiImpl;
import net.voxelindustry.brokkgui.internal.IMouseUtil;
import net.voxelindustry.brokkgui.window.IGuiWindow;

public class MouseUtil implements IMouseUtil
{
    private Mouse mouse;

    @Override
    public float getMouseX(IGuiWindow window)
    {
        return (float) getMouseX();
    }

    @Override
    public float getMouseY(IGuiWindow window)
    {
        return (float) getMouseY();
    }

    @Override
    public float getMouseX(IBrokkGuiImpl wrapper)
    {
        return (float) getMouseX();
    }

    @Override
    public float getMouseY(IBrokkGuiImpl wrapper)
    {
        return (float) getMouseY();
    }

    public double getMouseX()
    {
        if (MinecraftClient.getInstance().currentScreen == null)
            return getMouse().getX();
        return (getMouse().getX() * MinecraftClient.getInstance().getWindow().getScaledWidth() /
                MinecraftClient.getInstance().getWindow().getWidth());
    }

    public double getMouseY()
    {
        if (MinecraftClient.getInstance().currentScreen == null)
            return  this.getMouse().getY();

        return  (getMouse().getY() * MinecraftClient.getInstance().getWindow().getScaledHeight() /
                MinecraftClient.getInstance().getWindow().getHeight());
    }

    public Mouse getMouse()
    {
        if (this.mouse == null)
            this.mouse = MinecraftClient.getInstance().mouse;
        return this.mouse;
    }
}