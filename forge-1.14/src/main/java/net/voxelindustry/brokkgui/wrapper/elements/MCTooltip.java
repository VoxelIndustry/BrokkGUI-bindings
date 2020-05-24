package net.voxelindustry.brokkgui.wrapper.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.voxelindustry.brokkgui.component.GuiElement;
import net.voxelindustry.brokkgui.element.GuiTooltip;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MCTooltip extends GuiElement
{
    public static Builder build()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private List<String>           lines;
        private Consumer<List<String>> linesFiller;

        public Builder()
        {
            lines = new ArrayList<>();
        }

        public Builder line(String line)
        {
            lines.add(line);
            return this;
        }

        public Builder dynamicLines(Consumer<List<String>> linesFiller)
        {
            this.linesFiller = linesFiller;
            return this;
        }

        public GuiTooltip create()
        {
            return new GuiTooltip(get());
        }

        public MCTooltip get()
        {
            return new MCTooltip(linesFiller, lines);
        }
    }

    private Consumer<List<String>> linesFiller;
    private List<String>           linesList;

    public MCTooltip(Consumer<List<String>> linesFiller, List<String> linesList)
    {
        this.linesList = linesList;
        this.linesFiller = linesFiller;
    }

    @Override
    protected void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
    {
        if (pass != RenderPass.HOVER)
            return;

        if (linesFiller != null)
        {
            linesList.clear();
            linesFiller.accept(linesList);
        }
        GlStateManager.pushLightingAttributes();

        GuiUtils.drawHoveringText(linesList, (int) transform().xPos(), (int) transform().yPos(),
                Minecraft.getInstance().mainWindow.getWidth(), Minecraft.getInstance().mainWindow.getHeight(),
                Minecraft.getInstance().mainWindow.getHeight(), Minecraft.getInstance().fontRenderer);

        GlStateManager.popAttributes();
    }

    @Override
    public String type()
    {
        return "mctooltip";
    }
}
