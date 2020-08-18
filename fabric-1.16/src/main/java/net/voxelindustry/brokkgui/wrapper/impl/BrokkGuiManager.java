package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.voxelindustry.brokkgui.gui.BrokkGuiScreen;
import net.voxelindustry.brokkgui.style.StylesheetManager;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;

/**
 * @author Ourten 4 oct. 2016
 */
public class BrokkGuiManager
{
    public static Screen getBrokkGuiScreen(Text title, BrokkGuiScreen brokkGui)
    {
        return getBrokkGuiScreen(StylesheetManager.getInstance().DEFAULT_THEME, title, brokkGui);
    }

    public static Screen getBrokkGuiScreen(String modID, Text title, BrokkGuiScreen brokkGui)
    {
        return new GuiScreenImpl(modID, title, brokkGui);
    }

    public static <T extends ScreenHandler> HandledScreen<T> getBrokkGuiContainer(Text title, BrokkGuiContainer<T> brokkGui)
    {
        return getBrokkGuiContainer(StylesheetManager.getInstance().DEFAULT_THEME, title, brokkGui);
    }

    public static <T extends ScreenHandler> HandledScreen<T> getBrokkGuiContainer(String modID, Text title, BrokkGuiContainer<T> brokkGui)
    {
        return new GuiContainerImpl<T>(modID, title, brokkGui);
    }

    public static void openBrokkGuiScreen(Text title, BrokkGuiScreen brokkGui)
    {
        openBrokkGuiScreen(StylesheetManager.getInstance().DEFAULT_THEME, title, brokkGui);
    }

    public static void openBrokkGuiScreen(String modID, Text title, BrokkGuiScreen brokkGui)
    {
        MinecraftClient.getInstance().openScreen(BrokkGuiManager.getBrokkGuiScreen(modID, title, brokkGui));
    }
}