package net.voxelindustry.brokkgui.demo;

import net.voxelindustry.brokkgui.window.BrokkGuiScreen;

public class GuiOverlayDemo extends BrokkGuiScreen
{
    public GuiOverlayDemo()
    {
        super(0.5F, 0.5F, 0, 0);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        setWidth(getScreenWidth());
        setHeight(getScreenHeight());
    }
}
