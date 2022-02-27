package net.voxelindustry.brokkgui.wrapper.elements;

import net.minecraft.text.Text;
import net.voxelindustry.brokkgui.component.GuiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MCTooltip extends GuiElement
{
    private MCTooltipComponent tooltipComponent;

    public MCTooltip(Consumer<List<Text>> linesFiller, List<Text> linesList)
    {
        tooltipComponent.linesFiller(linesFiller);
        tooltipComponent.linesList(linesList);
    }

    @Override
    public void postConstruct()
    {
        super.postConstruct();

        tooltipComponent = provide(MCTooltipComponent.class);
    }

    @Override
    public String type()
    {
        return "mc-tooltip";
    }

    public static Builder build()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<Text>           lines;
        private       Consumer<List<Text>> linesFiller;

        public Builder()
        {
            lines = new ArrayList<>();
        }

        public Builder line(Text line)
        {
            this.lines.add(line);
            return this;
        }

        public Builder dynamicLines(Consumer<List<Text>> linesFiller)
        {
            this.linesFiller = linesFiller;
            return this;
        }

        public MCTooltip create()
        {
            return new MCTooltip(linesFiller, lines);
        }
    }
}
