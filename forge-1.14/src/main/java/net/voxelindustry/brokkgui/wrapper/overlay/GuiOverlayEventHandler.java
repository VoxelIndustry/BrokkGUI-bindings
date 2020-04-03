package net.voxelindustry.brokkgui.wrapper.overlay;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardCharTypedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyReleasedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseDragEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiOverlayEventHandler
{
    @SubscribeEvent
    public void OnGuiRender(DrawScreenEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.render(event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks()));
    }

    @SubscribeEvent
    public void onGuiInit(InitGuiEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.init(event.getGui().width, event.getGui().height));
    }

    @SubscribeEvent
    public void onGuiMouseClicked(MouseClickedEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton()));
    }

    @SubscribeEvent
    public void onGuiMouseDrag(MouseDragEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY()));
    }

    @SubscribeEvent
    public void onGuiMouseScroll(MouseScrollEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta()));
    }

    @SubscribeEvent
    public void onGuiKeyPressed(KeyboardKeyPressedEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers()));
    }

    @SubscribeEvent
    public void onGuiKeyReleased(KeyboardKeyReleasedEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.keyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers()));
    }

    @SubscribeEvent
    public void onGuiKeyReleased(KeyboardCharTypedEvent.Post event)
    {
        GuiOverlayManager.getActiveOverlays()
                .forEach(overlay -> overlay.charTyped(event.getCodePoint(), event.getModifiers()));
    }

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent event)
    {
        GuiOverlayManager.triggerOverlays(event.getGui());
    }
}
