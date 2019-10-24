package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.voxelindustry.brokkgui.gui.BrokkGuiScreen;
import net.voxelindustry.brokkgui.style.StylesheetManager;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;

/**
 * @author Ourten 4 oct. 2016
 */
public class BrokkGuiManager
{
    public static Screen getBrokkGuiScreen(ITextComponent title, BrokkGuiScreen brokkGui)
    {
        return getBrokkGuiScreen(StylesheetManager.getInstance().DEFAULT_THEME, title, brokkGui);
    }

    public static Screen getBrokkGuiScreen(String modID, ITextComponent title, BrokkGuiScreen brokkGui)
    {
        return new GuiScreenImpl(modID, title, brokkGui);
    }

    public static <T extends Container> ContainerScreen<T> getBrokkGuiContainer(ITextComponent title, BrokkGuiContainer<T> brokkGui)
    {
        return getBrokkGuiContainer(StylesheetManager.getInstance().DEFAULT_THEME, title, brokkGui);
    }

    public static <T extends Container> ContainerScreen<T> getBrokkGuiContainer(String modID, ITextComponent title, BrokkGuiContainer<T> brokkGui)
    {
        return new GuiContainerImpl<T>(modID, title, brokkGui);
    }

    public static void openBrokkGuiScreen(ITextComponent title, BrokkGuiScreen brokkGui)
    {
        openBrokkGuiScreen(StylesheetManager.getInstance().DEFAULT_THEME, title, brokkGui);
    }

    public static void openBrokkGuiScreen(String modID, ITextComponent title, BrokkGuiScreen brokkGui)
    {
        Minecraft.getInstance().displayGuiScreen(BrokkGuiManager.getBrokkGuiScreen(modID, title, brokkGui));
    }
}