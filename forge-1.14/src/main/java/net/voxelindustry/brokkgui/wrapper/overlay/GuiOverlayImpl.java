package net.voxelindustry.brokkgui.wrapper.overlay;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.GuiFocusManager;
import net.voxelindustry.brokkgui.internal.IBrokkGuiImpl;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.paint.RenderTarget;
import net.voxelindustry.brokkgui.window.BrokkGuiScreen;
import net.voxelindustry.brokkgui.window.IGuiWindow;
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
        renderer = new GuiRenderer(Tessellator.getInstance());
        this.brokkgui.setWrapper(this);

        cachedMouseX = -1;
        cachedMouseY = -1;
    }

    public void init(int width, int height)
    {
        this.width = width;
        this.height = height;
        brokkgui.setScreenWidth(this.width);
        brokkgui.setScreenHeight(this.height);

        brokkgui.initGui();
    }

    public void render(int mouseX, int mouseY, float partialTicks)
    {
        BrokkGuiPlatform.getInstance().getProfiler().beginRenderFrame();

        if (cachedMouseX != mouseX || cachedMouseY != mouseY)
        {
            brokkgui.onMouseMoved(mouseX, mouseY);
            cachedMouseX = mouseX;
            cachedMouseY = mouseY;
        }
        brokkgui.render(mouseX, mouseY, RenderTarget.MAIN,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN,
                GuiHelper.ITEM_HOVER);
        brokkgui.render(mouseX, mouseY, RenderTarget.WINDOW,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN,
                GuiHelper.ITEM_HOVER);
        brokkgui.render(mouseX, mouseY, RenderTarget.POPUP,
                RenderPass.BACKGROUND, RenderPass.MAIN, RenderPass.FOREGROUND, RenderPass.HOVER, GuiHelper.ITEM_MAIN,
                GuiHelper.ITEM_HOVER);
        brokkgui.renderLast(mouseX, mouseY);

        BrokkGuiPlatform.getInstance().getProfiler().endRenderFrame();

        brokkgui.tick();
    }

    public void mouseClicked(double mouseX, double mouseY, int key)
    {
        brokkgui.onClick((int) mouseX, (int) mouseY, key);
    }

    public void mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dragX, double dragY)
    {
        brokkgui.onClickDrag((int) mouseX, (int) mouseY, clickedMouseButton, dragX, dragY);
    }

    public void mouseReleased(double mouseX, double mouseY, int state)
    {
        brokkgui.onClickStop((int) mouseX, (int) mouseY, state);
    }

    public void mouseScrolled(double mouseX, double mouseY, double scrolled)
    {
        brokkgui.handleMouseScroll(scrolled * 120);
    }

    public void keyReleased(int keyCode, int scanCode, int modsField)
    {
        brokkgui.onKeyReleased(keyCode);
    }

    public void keyPressed(int keyCode, int scanCode, int modsField)
    {
        brokkgui.onKeyPressed(keyCode);
    }

    public void charTyped(char typedChar, int modsField)
    {
        if (GuiFocusManager.instance().getFocusedNode() != null)
        {
            brokkgui.onKeyTyped(typedChar, -1);
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
        brokkgui.onClose();
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

        brokkgui.setScreenWidth(width);
        brokkgui.setScreenHeight(height);
    }

    public Predicate<Screen> getOverlayCondition()
    {
        return overlayCondition;
    }
}
