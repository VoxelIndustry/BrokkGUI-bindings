package net.voxelindustry.brokkgui.wrapper.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.voxelindustry.brokkgui.GuiFocusManager;
import net.voxelindustry.brokkgui.gui.IGuiWindow;
import net.voxelindustry.brokkgui.internal.IBrokkGuiImpl;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.paint.RenderTarget;
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

    GuiContainerImpl(String modID, ITextComponent title, BrokkGuiContainer<T> brokkGui)
    {
        super(brokkGui.getContainer(), brokkGui.getPlayerInventory(), title);
        this.brokkgui = brokkGui;
        this.modID = modID;
        this.renderer = new GuiRenderer(Tessellator.getInstance());
        this.brokkgui.setWrapper(this);

        brokkGui.getWidthProperty().addListener((obs, oldValue, newValue) ->
                refreshContainerWidth(newValue.intValue()));
        brokkGui.getHeightProperty().addListener((obs, oldValue, newValue) ->
                refreshContainerHeight(newValue.intValue()));

        refreshContainerWidth((int) brokkGui.getWidth());
        refreshContainerHeight((int) brokkGui.getHeight());
    }

    private void refreshContainerWidth(int newWidth)
    {
        this.xSize = newWidth;
        this.guiLeft = (this.width - this.xSize) / 2;
    }

    private void refreshContainerHeight(int newHeight)
    {
        this.ySize = newHeight;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void init()
    {
        super.init();

        this.brokkgui.getScreenWidthProperty().setValue(this.width);
        this.brokkgui.getScreenHeightProperty().setValue(this.height);

        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.brokkgui.initGui();
    }

    @Override
    public void onClose()
    {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);

        this.brokkgui.onClose();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);

        if (!brokkgui.doesOccludePoint(mouseX, mouseY))
            this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        this.brokkgui.render(mouseX, mouseY, RenderTarget.MAIN,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.translatef(-this.guiLeft, -this.guiTop, 0);
        this.brokkgui.render(mouseX, mouseY, RenderTarget.MAIN, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);
        RenderHelper.disableStandardItemLighting();
        this.brokkgui.render(mouseX, mouseY, RenderTarget.WINDOW, RenderPass.BACKGROUND, RenderPass.MAIN,
                RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);
        RenderHelper.disableStandardItemLighting();
        this.brokkgui.render(mouseX, mouseY, RenderTarget.POPUP, RenderPass.BACKGROUND, RenderPass.MAIN,
                RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);

        this.brokkgui.renderLast(mouseX, mouseY);
        GlStateManager.translatef(this.guiLeft, this.guiTop, 0);

        brokkgui.tick();
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int key)
    {
        this.brokkgui.onClick((int) mouseX, (int) mouseY, key);
        return super.mouseClicked(mouseX, mouseY, key);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dragX, double dragY)
    {
        this.brokkgui.onClickDrag((int) mouseX, (int) mouseY, clickedMouseButton, dragX, dragY);

        return super.mouseDragged(mouseX, mouseY, clickedMouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        this.brokkgui.onClickStop((int) mouseX, (int) mouseY, state);
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled)
    {
        this.brokkgui.handleMouseScroll(scrolled * 120);
        return super.mouseScrolled(mouseX, mouseY, scrolled);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modsField)
    {
        this.brokkgui.onKeyReleased(keyCode);

        return super.keyReleased(keyCode, scanCode, modsField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modsField)
    {
        this.brokkgui.onKeyPressed(keyCode);

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || GuiFocusManager.getInstance().getFocusedNode() == null)
            return super.keyPressed(keyCode, scanCode, modsField);
        return true;
    }

    @Override
    public boolean charTyped(char typedChar, int modsField)
    {
        if (GuiFocusManager.getInstance().getFocusedNode() != null)
        {
            this.brokkgui.onKeyTyped(typedChar, -1);
            return true;
        }
        return super.charTyped(typedChar, modsField);
    }

    @Override
    public void askClose()
    {
        if (this.minecraft.player != null)
            this.minecraft.player.closeScreen();

        this.brokkgui.onClose();
    }

    @Override
    public void askOpen()
    {
        // TODO : Container opening sync

        this.brokkgui.onOpen();
    }

    @Override
    public IGuiRenderer getRenderer()
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
    protected void handleMouseClick(final Slot slot, final int slotID, final int button, final ClickType flag)
    {
        super.handleMouseClick(slot, slotID, button, flag);

        if (slot != null)
            this.brokkgui.dispatchEvent(SlotEvent.CLICK, new SlotEvent.Click(null, slot, button));
    }
}
