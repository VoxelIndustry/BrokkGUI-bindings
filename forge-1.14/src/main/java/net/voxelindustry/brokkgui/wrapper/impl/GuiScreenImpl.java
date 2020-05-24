package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.ITextComponent;
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
import org.lwjgl.glfw.GLFW;

public class GuiScreenImpl extends Screen implements IBrokkGuiImpl
{
    private final String      modID;
    private final GuiRenderer renderer;

    private IGuiWindow brokkgui;

    private int cachedMouseX;
    private int cachedMouseY;

    GuiScreenImpl(String modID, ITextComponent title, BrokkGuiScreen brokkgui)
    {
        super(title);
        this.brokkgui = brokkgui;
        this.modID = modID;
        renderer = new GuiRenderer(Tessellator.getInstance());
        this.brokkgui.setWrapper(this);

        cachedMouseX = -1;
        cachedMouseY = -1;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void init()
    {
        super.init();

        brokkgui.setScreenWidth(width);
        brokkgui.setScreenHeight(height);

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
        super.render(mouseX, mouseY, partialTicks);

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
        minecraft.displayGuiScreen(this);

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

        brokkgui.setScreenWidth(width);
        brokkgui.setScreenHeight(height);
    }
}
