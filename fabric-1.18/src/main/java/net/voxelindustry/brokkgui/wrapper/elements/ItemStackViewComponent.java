package net.voxelindustry.brokkgui.wrapper.elements;

import fr.ourten.teabeans.property.Property;
import fr.ourten.teabeans.property.specific.BooleanProperty;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.voxelindustry.brokkcolor.Color;
import net.voxelindustry.brokkgui.component.ExtendedRenderComponent;
import net.voxelindustry.brokkgui.component.GuiComponent;
import net.voxelindustry.brokkgui.wrapper.impl.ItemStackRenderCommandReceiver;

import java.util.List;
import java.util.function.Consumer;

public class ItemStackViewComponent extends GuiComponent implements ExtendedRenderComponent<ItemStackRenderCommandReceiver>
{
    private final Property<String>    alternateStringProperty = new Property<>(null);
    private final Property<ItemStack> stackProperty           = new Property<>(ItemStack.EMPTY);

    private final BooleanProperty itemTooltipProperty = new BooleanProperty();

    private final Property<Color> colorProperty = new Property<>(Color.WHITE);

    private Consumer<List<Text>> stackTooltipModifier;

    @Override
    public void renderExtendedContent(ItemStackRenderCommandReceiver renderer, float mouseX, float mouseY)
    {
        renderer.drawItemStack(
                transform().leftPos() + transform().width() / 2,
                transform().topPos() + transform().height() / 2,
                transform().width(),
                transform().height(),
                transform().zLevel(),
                itemStack(),
                alternateString(),
                color());

        if (element().isHovered() && itemTooltip() && !itemStack().isEmpty())
            renderer.drawItemStackTooltip(
                    (int) mouseX,
                    (int) mouseY,
                    element().window().getWidth(),
                    element().window().getHeight(),
                    itemStack(),
                    stackTooltipModifier());

    }

    public Property<ItemStack> stackProperty()
    {
        return this.stackProperty;
    }

    public Property<String> alternateStringProperty()
    {
        return this.alternateStringProperty;
    }

    public BooleanProperty itemTooltipProperty()
    {
        return this.itemTooltipProperty;
    }

    public Property<Color> colorProperty()
    {
        return this.colorProperty;
    }

    public ItemStack itemStack()
    {
        return this.stackProperty().getValue();
    }

    public void itemStack(ItemStack stack)
    {
        this.stackProperty().setValue(stack);
    }

    public String alternateString()
    {
        return this.alternateStringProperty().getValue();
    }

    /**
     * @param alternateString a string to be displayed in place of the usual itemstack quantity
     *                        number at the down-right corner.
     */
    public void alternateString(String alternateString)
    {
        this.alternateStringProperty().setValue(alternateString);
    }

    public boolean itemTooltip()
    {
        return this.itemTooltipProperty().get();
    }

    public void itemTooltip(boolean tooltip)
    {
        this.itemTooltipProperty().set(tooltip);
    }

    public Color color()
    {
        return this.colorProperty().getValue();
    }

    public void color(Color color)
    {
        this.colorProperty().setValue(color);
    }

    public Consumer<List<Text>> stackTooltipModifier()
    {
        return this.stackTooltipModifier;
    }

    public void stackTooltipModifier(Consumer<List<Text>> stackTooltipModifier)
    {
        this.stackTooltipModifier = stackTooltipModifier;
    }
}
