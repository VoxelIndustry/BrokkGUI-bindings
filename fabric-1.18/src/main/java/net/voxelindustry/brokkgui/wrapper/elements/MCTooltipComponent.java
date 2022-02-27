package net.voxelindustry.brokkgui.wrapper.elements;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.voxelindustry.brokkgui.component.ExtendedRenderComponent;
import net.voxelindustry.brokkgui.component.GuiComponent;
import net.voxelindustry.brokkgui.wrapper.impl.TooltipRenderCommandReceiver;

import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public class MCTooltipComponent extends GuiComponent implements ExtendedRenderComponent<TooltipRenderCommandReceiver>
{
    private Consumer<List<Text>> linesFiller;
    private List<Text>           linesList;

    @Override
    public void renderExtendedContent(TooltipRenderCommandReceiver renderer, float mouseX, float mouseY)
    {
        if (linesFiller != null)
        {
            linesList.clear();
            linesFiller.accept(linesList);
        }

        renderer.drawHoveringTooltip(
                (int) transform().leftPos(),
                (int) transform().topPos(),
                element().window().getWidth(),
                element().window().getHeight(),
                linesList.stream()
                        .map(Text::asOrderedText)
                        .map(TooltipComponent::of)
                        .collect(toList())
        );
    }

    public Consumer<List<Text>> linesFiller()
    {
        return this.linesFiller;
    }

    public void linesFiller(Consumer<List<Text>> linesFiller)
    {
        this.linesFiller = linesFiller;
    }

    public List<Text> linesList()
    {
        return this.linesList;
    }

    public void linesList(List<Text> linesList)
    {
        this.linesList = linesList;
    }
}
