package net.voxelindustry.brokkgui.wrapper.impl;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.component.GuiElement;
import net.voxelindustry.brokkgui.component.impl.Transform;
import net.voxelindustry.brokkgui.event.MouseInputCode;
import net.voxelindustry.brokkgui.internal.IBrokkGuiImpl;
import net.voxelindustry.brokkgui.internal.IRenderCommandReceiver;
import net.voxelindustry.brokkgui.window.BrokkGuiScreen;
import net.voxelindustry.brokkgui.window.IGuiWindow;
import org.lwjgl.glfw.GLFW;

public class GuiScreenImpl extends Screen implements IBrokkGuiImpl
{
    private final String                  modID;
    private final RenderCommandDispatcher renderer = new RenderCommandDispatcher(BrokkGuiPlatform.getInstance().getTextHelper());

    private IGuiWindow brokkgui;

    private int cachedMouseX;
    private int cachedMouseY;

    GuiScreenImpl(String modID, Text title, BrokkGuiScreen brokkgui)
    {
        super(title);
        this.brokkgui = brokkgui;
        this.modID = modID;
        this.brokkgui.setWrapper(this);

        this.cachedMouseX = -1;
        this.cachedMouseY = -1;
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        renderer.setMatrices(matrices);
        BrokkGuiPlatform.getInstance().getProfiler().beginRenderFrame();

        super.render(matrices, mouseX, mouseY, delta);

        if (this.cachedMouseX != mouseX || this.cachedMouseY != mouseY)
        {
            this.brokkgui.onMouseMoved(mouseX, mouseY);
            this.cachedMouseX = mouseX;
            this.cachedMouseY = mouseY;
        }

        renderElementHierarchy(brokkgui.getRootElement());

        BrokkGuiPlatform.getInstance().getProfiler().endRenderFrame();
        this.brokkgui.renderLast(mouseX, mouseY);

        brokkgui.tick();
    }

    private void renderElementHierarchy(GuiElement element)
    {
        element.renderNode(renderer, cachedMouseX, cachedMouseY);

        for (Transform child : element.transform().children())
            renderElementHierarchy(child.element());
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
        this.client.setScreen(this);

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
        return brokkgui;
    }

    @Override
    public void setGuiWindow(IGuiWindow window)
    {
        this.brokkgui = window;

        this.brokkgui.screenWidth(this.width);
        this.brokkgui.screenHeight(this.height);
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
}
