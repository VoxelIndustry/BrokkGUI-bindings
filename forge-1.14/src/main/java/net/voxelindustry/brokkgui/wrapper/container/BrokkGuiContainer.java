package net.voxelindustry.brokkgui.wrapper.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.voxelindustry.brokkgui.window.BrokkGuiScreen;
import net.voxelindustry.brokkgui.wrapper.event.SlotEvent;
import net.voxelindustry.hermod.EventHandler;

public class BrokkGuiContainer<T extends Container> extends BrokkGuiScreen
{
    private final T                       container;

    private EventHandler<SlotEvent.Click> onSlotClickEvent;

    public BrokkGuiContainer(final T container)
    {
        super();
        this.container = container;
    }

    public T getContainer()
    {
        return this.container;
    }

    public void slotClick(final Slot slot, final int key)
    {
        this.getEventDispatcher().dispatchEvent(SlotEvent.CLICK, new SlotEvent.Click(this, slot, key));
    }

    public PlayerInventory getPlayerInventory()
    {
        return null;
    }

    /////////////////////
    // EVENTS HANDLING //
    /////////////////////

    public void setOnSlotClickEvent(final EventHandler<SlotEvent.Click> onSlotClickEvent)
    {
        this.getEventDispatcher().removeHandler(SlotEvent.CLICK, this.onSlotClickEvent);
        this.onSlotClickEvent = onSlotClickEvent;
        this.getEventDispatcher().addHandler(SlotEvent.CLICK, this.onSlotClickEvent);
    }
}