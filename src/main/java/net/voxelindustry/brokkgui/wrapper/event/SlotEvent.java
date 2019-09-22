package net.voxelindustry.brokkgui.wrapper.event;

import net.minecraft.inventory.container.Slot;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.hermod.EventType;
import net.voxelindustry.hermod.HermodEvent;
import net.voxelindustry.hermod.IEventEmitter;

/**
 * @author Ourten 31 oct. 2016
 */
public class SlotEvent extends HermodEvent
{
    public static final EventType<SlotEvent>       ANY    = new EventType<>("SLOT_EVENT");
    public static final EventType<SlotEvent.Click> CLICK  = new EventType<>(SlotEvent.ANY, "SLOT_CLICK_EVENT");
    public static final EventType<Change>          CHANGE = new EventType<>(SlotEvent.ANY, "SLOT_CHANGE_EVENT");

    private final Slot slot;

    public SlotEvent(BrokkGuiContainer<?> source, Slot slot)
    {
        super(source);

        this.slot = slot;
    }

    public Slot getSlot()
    {
        return this.slot;
    }

    @Override
    public SlotEvent copy(IEventEmitter source)
    {
        return new SlotEvent((BrokkGuiContainer<?>) source, getSlot());
    }

    public static final class Click extends SlotEvent
    {
        private final int key;

        public Click(BrokkGuiContainer<?> source, Slot slot, int key)
        {
            super(source, slot);

            this.key = key;
        }

        public int getKey()
        {
            return this.key;
        }

        @Override
        public Click copy(IEventEmitter source)
        {
            return new Click((BrokkGuiContainer<?>) source, getSlot(), getKey());
        }
    }

    public static final class Change extends SlotEvent
    {
        public Change(BrokkGuiContainer<?> source, Slot slot)
        {
            super(source, slot);
        }

        @Override
        public Change copy(IEventEmitter source)
        {
            return new Change((BrokkGuiContainer<?>) source, getSlot());
        }
    }
}