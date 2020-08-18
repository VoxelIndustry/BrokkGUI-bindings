package net.voxelindustry.brokkgui.wrapper.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
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

public class GuiContainerImpl<T extends ScreenHandler> extends HandledScreen<T> implements IBrokkGuiImpl
{
    private IGuiWindow brokkgui;
    private String     modID;

    private final GuiRenderer renderer;

    private int cachedMouseX;
    private int cachedMouseY;

    GuiContainerImpl(String modID, Text title, BrokkGuiContainer<T> brokkGui)
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

        this.brokkgui.getScreenWidthProperty().setValue(this.width);
        this.brokkgui.getScreenHeightProperty().setValue(this.height);

        this.client.keyboard.enableRepeatEvents(true);
        this.brokkgui.initGui();
    }

    @Override
    public void onClose()
    {
        super.onClose();
        this.client.keyboard.enableRepeatEvents(false);

        this.brokkgui.onClose();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks)
    {
        ((GuiHelper) renderer.getHelper()).setMatrices(matrices);
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
        this.brokkgui.render(mouseX, mouseY, RenderTarget.MAIN,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY)
    {
        GlStateManager.translatef(-this.x, -this.y, 0);
        this.brokkgui.render(mouseX, mouseY, RenderTarget.MAIN, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);

        this.brokkgui.render(mouseX, mouseY, RenderTarget.WINDOW, RenderPass.BACKGROUND, RenderPass.MAIN,
                RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);

        this.brokkgui.render(mouseX, mouseY, RenderTarget.POPUP, RenderPass.BACKGROUND, RenderPass.MAIN,
                RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN, GuiHelper.ITEM_HOVER);

        this.brokkgui.renderLast(mouseX, mouseY);
        GlStateManager.translatef(this.x, this.y, 0);

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
    protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType)
    {
        super.onMouseClick(slot, invSlot, clickData, actionType);

        if (slot != null)
            this.brokkgui.dispatchEvent(SlotEvent.CLICK, new SlotEvent.Click(null, slot, clickData));
    }
}
