package net.voxelindustry.brokkgui.wrapper;

import net.fabricmc.api.ModInitializer;
import net.voxelindustry.brokkgui.BrokkGuiBootstrap;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;

import java.util.logging.Logger;

public class BrokkGuiWrapperMod implements ModInitializer
{
    public static final String MODID   = "brokkguiwrapper";

    @Override
    public void onInitialize()
    {
        BrokkGuiPlatform.getInstance().setPlatformName("Fabric1.18");
        BrokkGuiPlatform.getInstance().setLogger(Logger.getLogger("BrokkGUI:1.18"));

        BrokkGuiPlatform.getInstance().setKeyboardUtil(new KeyboardUtil());
        BrokkGuiPlatform.getInstance().setMouseUtil(new MouseUtil());
        BrokkGuiPlatform.getInstance().setResourceHandler(new ResourceHandler());
        BrokkGuiBootstrap.bootstrap();
    }
}