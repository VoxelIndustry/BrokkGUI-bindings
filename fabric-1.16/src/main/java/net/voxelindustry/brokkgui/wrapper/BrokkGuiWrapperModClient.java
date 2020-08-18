package net.voxelindustry.brokkgui.wrapper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;

public class BrokkGuiWrapperModClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BrokkGuiPlatform.getInstance().setGuiHelper(new GuiHelper());

        BrokkGuiTickSender tickSender = new BrokkGuiTickSender();
        ClientTickEvents.END_CLIENT_TICK.register(tickSender::clientTick);
        BrokkGuiPlatform.getInstance().setTickSender(tickSender);
    }
}
