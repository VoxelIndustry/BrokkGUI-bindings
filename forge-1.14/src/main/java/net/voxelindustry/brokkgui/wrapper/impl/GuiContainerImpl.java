package net.voxelindustry.brokkgui.wrapper.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.GuiFocusManager;
import net.voxelindustry.brokkgui.internal.IBrokkGuiImpl;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.paint.RenderTarget;
import net.voxelindustry.brokkgui.window.IGuiWindow;
import net.voxelindustry.brokkgui.wrapper.GuiHelper;
import net.voxelindustry.brokkgui.wrapper.GuiRenderer;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.brokkgui.wrapper.event.SlotEvent;
import org.lwjgl.glfw.GLFW;

public class GuiContainerImpl<T extends Container> extends ContainerScreen<T> implements IBrokkGuiImpl
{
    private IGuiWindow brokkgui;
    private String     modID;

    private final GuiRenderer renderer;

    private int cachedMouseX;
    private int cachedMouseY;

    GuiContainerImpl(String modID, ITextComponent title, BrokkGuiContainer<T> brokkGui)
    {
        super(brokkGui.getContainer(), brokkGui.getPlayerInventory(), title);
        brokkgui = brokkGui;
        this.modID = modID;
        renderer = new GuiRenderer(Tessellator.getInstance());
        brokkgui.setWrapper(this);

        brokkGui.getWidthProperty().addListener((obs, oldValue, newValue) ->
                refreshContainerWidth(newValue.intValue(), (int) brokkGui.getxOffset()));
        brokkGui.getHeightProperty().addListener((obs, oldValue, newValue) ->
                refreshContainerHeight(newValue.intValue(), (int) brokkGui.getyOffset()));

        brokkGui.getxOffsetProperty().addListener((obs, oldValue, newValue) ->
                refreshContainerWidth((int) brokkGui.getWidth(), newValue.intValue()));
        brokkGui.getyOffsetProperty().addListener((obs, oldValue, newValue) ->
                refreshContainerHeight((int) brokkGui.getHeight(), newValue.intValue()));

        refreshContainerWidth((int) brokkGui.getWidth(), (int) brokkGui.getxOffset());
        refreshContainerHeight((int) brokkGui.getHeight(), (int) brokkGui.getyOffset());

        cachedMouseX = -1;
        cachedMouseY = -1;
    }

    private void refreshContainerWidth(int width, int offsetX)
    {
        xSize = width;
        guiLeft = (this.width - xSize) / 2 + offsetX;
    }

    private void refreshContainerHeight(int height, int offsetY)
    {
        ySize = height;
        guiTop = (this.height - ySize) / 2 + offsetY;
    }

    @Override
    public void init()
    {
        super.init();

        brokkgui.getScreenWidthProperty().setValue(width);
        brokkgui.getScreenHeightProperty().setValue(height);

        minecraft.keyboardListener.enableRepeatEvents(true);
        brokkgui.initGui();
    }

    @Override
    public void onClose()
    {
        super.onClose();
        minecraft.keyboardListener.enableRepeatEvents(false);

        brokkgui.onClose();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        BrokkGuiPlatform.getInstance().getProfiler().beginRenderFrame();

        if (cachedMouseX != mouseX || cachedMouseY != mouseY)
        {
            brokkgui.onMouseMoved(mouseX, mouseY);
            cachedMouseX = mouseX;
            cachedMouseY = mouseY;
        }

        renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        if (!brokkgui.doesOccludePoint(mouseX, mouseY))
            renderHoveredToolTip(mouseX, mouseY);

        BrokkGuiPlatform.getInstance().getProfiler().endRenderFrame();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        brokkgui.render(mouseX, mouseY, RenderTarget.MAIN,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.translatef(-guiLeft, -guiTop, 0);
        brokkgui.render(mouseX, mouseY, RenderTarget.MAIN, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);
        RenderHelper.disableStandardItemLighting();
        brokkgui.render(mouseX, mouseY, RenderTarget.WINDOW, RenderPass.BACKGROUND, RenderPass.MAIN,
                RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);
        RenderHelper.disableStandardItemLighting();
        brokkgui.render(mouseX, mouseY, RenderTarget.POPUP, RenderPass.BACKGROUND, RenderPass.MAIN,
                RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);

        brokkgui.renderLast(mouseX, mouseY);
        GlStateManager.translatef(guiLeft, guiTop, 0);

        brokkgui.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key)
    {
        brokkgui.onClick((int) mouseX, (int) mouseY, key);
        return super.mouseClicked(mouseX, mouseY, key);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dragX, double dragY)
    {
        brokkgui.onClickDrag((int) mouseX, (int) mouseY, clickedMouseButton, dragX, dragY);

        return super.mouseDragged(mouseX, mouseY, clickedMouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        brokkgui.onClickStop((int) mouseX, (int) mouseY, state);
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled)
    {
        brokkgui.handleMouseScroll(scrolled * 120);
        return super.mouseScrolled(mouseX, mouseY, scrolled);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modsField)
    {
        brokkgui.onKeyReleased(keyCode);

        return super.keyReleased(keyCode, scanCode, modsField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modsField)
    {
        brokkgui.onKeyPressed(keyCode);

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || GuiFocusManager.instance().getFocusedNode() == null)
            return super.keyPressed(keyCode, scanCode, modsField);
        return true;
    }

    @Override
    public boolean charTyped(char typedChar, int modsField)
    {
        if (GuiFocusManager.instance().getFocusedNode() != null)
        {
            brokkgui.onKeyTyped(typedChar, -1);
            return true;
        }
        return super.charTyped(typedChar, modsField);
    }

    @Override
    public void askClose()
    {
        if (minecraft.player != null)
            minecraft.player.closeScreen();

        brokkgui.onClose();
    }

    @Override
    public void askOpen()
    {
        // TODO : Container opening sync

        brokkgui.onOpen();
    }

    @Override
    public IGuiRenderer getRenderer()
    {
        return renderer;
    }

    @Override
    public String getThemeID()
    {
        return modID;
    }

    @Override
    public float getGuiRelativePosX(float guiXRelativePos, float guiWidth)
    {
        return (int) (width / (1 / guiXRelativePos) - guiWidth / 2);
    }

    @Override
    public float getGuiRelativePosY(float guiYRelativePos, float guiHeight)
    {
        return (int) (height / (1 / guiYRelativePos) - guiHeight / 2);
    }

    @Override
    public IGuiWindow getGui()
    {
        return brokkgui;
    }

    @Override
    public void setGuiWindow(IGuiWindow window)
    {
        brokkgui = window;
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotID, int button, ClickType flag)
    {
        super.handleMouseClick(slot, slotID, button, flag);

        if (slot != null)
            brokkgui.dispatchEvent(SlotEvent.CLICK, new SlotEvent.Click(null, slot, button));
    }
}
