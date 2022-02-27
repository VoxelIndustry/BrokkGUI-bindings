package net.voxelindustry.brokkgui.wrapper;

import net.minecraft.client.MinecraftClient;
import net.voxelindustry.brokkgui.animation.ITickSender;
import net.voxelindustry.brokkgui.animation.ITicking;

import java.util.ArrayList;
import java.util.List;

public class BrokkGuiTickSender implements ITickSender
{
    private final List<ITicking> tickings = new ArrayList<>();
    private final List<ITicking> toAdd    = new ArrayList<>();
    private final List<ITicking> toEvict  = new ArrayList<>();

    @Override
    public void addTicking(ITicking toTick)
    {
        this.toAdd.add(toTick);
    }

    @Override
    public void removeTicking(ITicking toTick)
    {
        this.toEvict.add(toTick);
    }

    public void clientTick(MinecraftClient minecraftClient)
    {
        long millis = System.currentTimeMillis();

        if (!toEvict.isEmpty())
        {
            tickings.removeAll(toEvict);
            toEvict.clear();
        }
        if (!toAdd.isEmpty())
        {
            tickings.addAll(toAdd);
            toAdd.clear();
        }
        for (ITicking ticking : tickings)
            ticking.tick(millis);
    }
}
