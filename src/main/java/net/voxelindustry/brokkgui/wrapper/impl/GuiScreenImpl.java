package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.client.gui.GuiScreen;
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
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiScreenImpl extends GuiScreen implements IBrokkGuiImpl
{
    private final String      modID;
    private final GuiRenderer renderer;

    private IGuiWindow brokkgui;

    GuiScreenImpl(String modID, IGuiWindow brokkgui)
    {
        this.brokkgui = brokkgui;
        this.modID = modID;
        this.renderer = new GuiRenderer(Tessellator.getInstance());
        this.brokkgui.setWrapper(this);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.brokkgui.screenWidth(this.width);
        this.brokkgui.screenHeight(this.height);

        Keyboard.enableRepeatEvents(true);
        this.brokkgui.initGui();
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);

        this.brokkgui.onClose();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

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

        brokkgui.tick();
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int key) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, key);

        this.brokkgui.onClick(mouseX, mouseY, key);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        this.brokkgui.onClickDrag(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        this.brokkgui.onClickStop(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.brokkgui.handleMouseScroll(Mouse.getEventDWheel());
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        super.handleKeyboardInput();

        char c0 = Keyboard.getEventCharacter();

        if (Keyboard.getEventKey() == 0 && c0 >= ' ' || !Keyboard.getEventKeyState())
            this.brokkgui.onKeyReleased(Keyboard.getEventKey());
    }

    @Override
    public void keyTyped(final char c, final int key) throws IOException
    {
        if (key == BrokkGuiPlatform.instance().keyboardUtil().getKeyCode("ESCAPE") ||
                GuiFocusManager.instance().focusedNode() == null)
            super.keyTyped(c, key);
        this.brokkgui.onKeyTyped(c, key);
        this.brokkgui.onKeyPressed(key);
    }

    @Override
    public void askClose()
    {
        this.mc.displayGuiScreen(null);
        this.mc.setIngameFocus();

        this.brokkgui.onClose();
    }

    @Override
    public void askOpen()
    {
        this.mc.displayGuiScreen(this);

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
        return brokkgui;
    }

    @Override
    public void setGuiWindow(IGuiWindow window)
    {
        this.brokkgui = window;

        this.brokkgui.screenWidth(this.width);
        this.brokkgui.screenHeight(this.height);
    }
}
