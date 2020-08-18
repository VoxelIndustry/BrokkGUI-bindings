package net.voxelindustry.brokkgui.wrapper.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.LiteralText;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.element.GuiTooltip;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.wrapper.GuiHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public class MCTooltip extends GuiNode
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
            this.lines.add(line);
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
        super("mctooltip");

        this.linesList = linesList;
        this.linesFiller = linesFiller;
    }

    @Override
    protected void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
    {
        GlStateManager.pushLightingAttributes();

        if (linesFiller != null)
        {
            linesList.clear();
            linesFiller.accept(linesList);
        }

        ((GuiHelper) renderer.getHelper()).drawHoveringTooltip(renderer,
                (int) getxPos(),
                (int) getyPos(),
                linesList.stream().map(LiteralText::new).collect(toList()));

        GlStateManager.popAttributes();
    }
}
