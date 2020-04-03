package net.voxelindustry.brokkgui.wrapper.overlay;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.GuiFocusManager;
import net.voxelindustry.brokkgui.gui.BrokkGuiScreen;
import net.voxelindustry.brokkgui.gui.IGuiWindow;
import net.voxelindustry.brokkgui.internal.IBrokkGuiImpl;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.paint.RenderTarget;
import net.voxelindustry.brokkgui.wrapper.GuiHelper;
import net.voxelindustry.brokkgui.wrapper.GuiRenderer;

import java.util.function.Predicate;

public class GuiOverlayImpl implements IBrokkGuiImpl
{
    private final String      modID;
    private final GuiRenderer renderer;
    private       IGuiWindow  brokkgui;

    private Predicate<Screen> overlayCondition;

    private int cachedMouseX;
    private int cachedMouseY;

    private int width;
    private int height;

    GuiOverlayImpl(String modID, BrokkGuiScreen brokkgui, Predicate<Screen> overlayCondition)
    {
        this.brokkgui = brokkgui;
        this.modID = modID;
        this.overlayCondition = overlayCondition;
        this.renderer = new GuiRenderer(Tessellator.getInstance());
        this.brokkgui.setWrapper(this);

        this.cachedMouseX = -1;
        this.cachedMouseY = -1;
    }

    public void init(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.brokkgui.setScreenWidth(this.width);
        this.brokkgui.setScreenHeight(this.height);

        this.brokkgui.initGui();
    }

    public void render(int mouseX, int mouseY, float partialTicks)
    {
        BrokkGuiPlatform.getInstance().getProfiler().beginRenderFrame();

        if (this.cachedMouseX != mouseX || this.cachedMouseY != mouseY)
        {
            this.brokkgui.onMouseMoved(mouseX, mouseY);
            this.cachedMouseX = mouseX;
            this.cachedMouseY = mouseY;
        }
        this.brokkgui.render(mouseX, mouseY, RenderTarget.MAIN,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN,
                GuiHelper.ITEM_HOVER);
        this.brokkgui.render(mouseX, mouseY, RenderTarget.WINDOW,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN,
                GuiHelper.ITEM_HOVER);
        this.brokkgui.render(mouseX, mouseY, RenderTarget.POPUP,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN,
                GuiHelper.ITEM_HOVER);
        this.brokkgui.renderLast(mouseX, mouseY);

        BrokkGuiPlatform.getInstance().getProfiler().endRenderFrame();

        brokkgui.tick();
    }

    public void mouseClicked(final double mouseX, final double mouseY, final int key)
    {
        this.brokkgui.onClick((int) mouseX, (int) mouseY, key);
    }

    public void mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dragX, double dragY)
    {
        this.brokkgui.onClickDrag((int) mouseX, (int) mouseY, clickedMouseButton, dragX, dragY);
    }

    public void mouseReleased(double mouseX, double mouseY, int state)
    {
        this.brokkgui.onClickStop((int) mouseX, (int) mouseY, state);
    }

    public void mouseScrolled(double mouseX, double mouseY, double scrolled)
    {
        this.brokkgui.handleMouseScroll(scrolled * 120);
    }

    public void keyReleased(int keyCode, int scanCode, int modsField)
    {
        this.brokkgui.onKeyReleased(keyCode);
    }

    public void keyPressed(int keyCode, int scanCode, int modsField)
    {
        this.brokkgui.onKeyPressed(keyCode);
    }

    public void charTyped(char typedChar, int modsField)
    {
        if (GuiFocusManager.getInstance().getFocusedNode() != null)
        {
            this.brokkgui.onKeyTyped(typedChar, -1);
        }
    }

    @Override
    public void askOpen()
    {

    }

    @Override
    public void askClose()
    {
        GuiOverlayManager.removeActiveOverlay(this);
        this.brokkgui.onClose();
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
        return brokkgui;
    }

    @Override
    public void setGuiWindow(IGuiWindow window)
    {
        this.brokkgui = window;

        this.brokkgui.setScreenWidth(this.width);
        this.brokkgui.setScreenHeight(this.height);
    }

    public Predicate<Screen> getOverlayCondition()
    {
        return overlayCondition;
    }
}
