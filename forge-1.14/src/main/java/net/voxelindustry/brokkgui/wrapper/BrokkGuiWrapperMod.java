package net.voxelindustry.brokkgui.wrapper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;

import java.util.logging.Logger;

@Mod(BrokkGuiWrapperMod.MODID)
public class BrokkGuiWrapperMod
{
    public static final String MODID   = "brokkguiwrapper";

    public BrokkGuiWrapperMod()
    {
        BrokkGuiPlatform.getInstance().setPlatformName("MC1.14.4");
        BrokkGuiPlatform.getInstance().setLogger(Logger.getLogger("BrokkGUI:1.14"));

        BrokkGuiPlatform.getInstance().setKeyboardUtil(new KeyboardUtil());
        BrokkGuiPlatform.getInstance().setMouseUtil(new MouseUtil());
        BrokkGuiPlatform.getInstance().setResourceHandler(new ResourceHandler());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent e)
    {
        if (EffectiveSide.get().isClient())
        {
            BrokkGuiPlatform.getInstance().setGuiHelper(new GuiHelper());

            BrokkGuiTickSender tickSender = new BrokkGuiTickSender();
            MinecraftForge.EVENT_BUS.register(tickSender);
            BrokkGuiPlatform.getInstance().setTickSender(tickSender);
        }
    }
}