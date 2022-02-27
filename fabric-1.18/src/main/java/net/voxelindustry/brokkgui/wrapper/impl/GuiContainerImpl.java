package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.component.GuiElement;
import net.voxelindustry.brokkgui.component.impl.Transform;
import net.voxelindustry.brokkgui.event.MouseInputCode;
import net.voxelindustry.brokkgui.internal.IBrokkGuiImpl;
import net.voxelindustry.brokkgui.internal.IRenderCommandReceiver;
import net.voxelindustry.brokkgui.window.IGuiWindow;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.brokkgui.wrapper.event.SlotEvent;
import org.lwjgl.glfw.GLFW;

public class GuiContainerImpl<T extends ScreenHandler> extends HandledScreen<T> implements IBrokkGuiImpl
{
    private IGuiWindow brokkgui;
    private String     modID;

    private final RenderCommandDispatcher renderer = new RenderCommandDispatcher(BrokkGuiPlatform.getInstance().getTextHelper());

    private int cachedMouseX;
    private int cachedMouseY;

    GuiContainerImpl(String modID, Text title, BrokkGuiContainer<T> brokkGui)
    {
        super(brokkGui.getContainer(), brokkGui.getPlayerInventory(), title);
        this.brokkgui = brokkGui;
        this.modID = modID;
        this.brokkgui.setWrapper(this);

        brokkGui.getWidthProperty().addChangeListener((obs, oldValue, newValue) ->
                refreshContainerWidth(newValue.intValue()));
        brokkGui.getHeightProperty().addChangeListener((obs, oldValue, newValue) ->
                refreshContainerHeight(newValue.intValue()));

        refreshContainerWidth((int) brokkGui.getWidth());
        refreshContainerHeight((int) brokkGui.getHeight());

        this.cachedMouseX = -1;
        this.cachedMouseY = -1;
    }

    private void refreshContainerWidth(int newWidth)
    {
        this.backgroundWidth = newWidth;
        this.x = (this.width - this.backgroundWidth) / 2;
    }

    private void refreshContainerHeight(int newHeight)
    {
        this.backgroundHeight = newHeight;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public void init()
    {
        super.init();

        this.brokkgui.screenWidth(this.width);
        this.brokkgui.screenHeight(this.height);

        this.client.keyboard.setRepeatEvents(true);
        this.brokkgui.initGui();
    }

    @Override
    public void onClose()
    {
        super.onClose();
        this.client.keyboard.setRepeatEvents(false);

        this.brokkgui.onClose();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks)
    {
        renderer.setMatrices(matrices);
        BrokkGuiPlatform.getInstance().getProfiler().beginRenderFrame();

        if (this.cachedMouseX != mouseX || this.cachedMouseY != mouseY)
        {
            this.brokkgui.onMouseMoved(mouseX, mouseY);
            this.cachedMouseX = mouseX;
            this.cachedMouseY = mouseY;
        }

        super.render(matrices, mouseX, mouseY, partialTicks);

        if (!brokkgui.doesOccludePoint(mouseX, mouseY))
            this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        BrokkGuiPlatform.getInstance().getProfiler().endRenderFrame();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        renderElementHierarchy(brokkgui.getRootElement());
    }

    private void renderElementHierarchy(GuiElement element)
    {
        element.renderNode(renderer, cachedMouseX, cachedMouseY);

        for (Transform child : element.transform().children())
            renderElementHierarchy(child.element());
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
        this.brokkgui.renderLast(mouseX, mouseY);
        brokkgui.tick();
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int key)
    {
        this.brokkgui.onClick((float) mouseX, (float) mouseY, MouseInputCode.fromGLFWCode(key));
        return super.mouseClicked(mouseX, mouseY, key);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dragX, double dragY)
    {
        this.brokkgui.onClickDrag((float) mouseX, (float) mouseY, MouseInputCode.fromGLFWCode(clickedMouseButton));

        return super.mouseDragged(mouseX, mouseY, clickedMouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        this.brokkgui.onClickStop((float) mouseX, (float) mouseY, MouseInputCode.fromGLFWCode(state));
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled)
    {
        this.brokkgui.onScroll((float) mouseX, (float) mouseY, 0, scrolled * 120);
        return super.mouseScrolled(mouseX, mouseY, scrolled);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modsField)
    {
        var consumed = this.brokkgui.onKeyReleased(keyCode);

        return consumed || super.keyReleased(keyCode, scanCode, modsField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modsField)
    {
        var consumed = this.brokkgui.onKeyPressed(keyCode);

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || !consumed)
            return super.keyPressed(keyCode, scanCode, modsField);
        return true;
    }

    @Override
    public boolean charTyped(char typedChar, int modsField)
    {
        var consumed = this.brokkgui.onTextTyped(String.valueOf(typedChar));

        return consumed || super.charTyped(typedChar, modsField);
    }

    @Override
    public void askClose()
    {
        if (this.client.player != null)
            this.client.player.closeScreen();

        this.brokkgui.onClose();
    }

    @Override
    public void askOpen()
    {
        // TODO : Container opening sync

        this.brokkgui.onOpen();
    }

    @Override
    public IRenderCommandReceiver getRenderer()
    {
        return this.renderer;
    }

    @Override
    public String getThemeID()
    {
        return this.modID;
    }

    @Override
    public float getGuiRelativePosX(float guiXRelativePos, float guiWidth)
    {
        return (int) (this.width / (1 / guiXRelativePos) - guiWidth / 2);
    }

    @Override
    public float getGuiRelativePosY(float guiYRelativePos, float guiHeight)
    {
        return (int) (this.height / (1 / guiYRelativePos) - guiHeight / 2);
    }

    @Override
    public IGuiWindow getGui()
    {
        return this.brokkgui;
    }

    @Override
    public void setGuiWindow(IGuiWindow window)
    {
        this.brokkgui = window;
    }

    @Override
    public float windowWidthRatio()
    {
        return 1;
    }

    @Override
    public float windowHeightRatio()
    {
        return 1;
    }

    @Override
    protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType)
    {
        super.onMouseClick(slot, invSlot, clickData, actionType);

        if (slot != null)
            this.brokkgui.dispatchEvent(SlotEvent.CLICK, new SlotEvent.Click(null, slot, clickData));
    }
}
