package net.voxelindustry.brokkgui.wrapper.elements;

import fr.ourten.teabeans.value.BaseProperty;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.voxelindustry.brokkgui.control.GuiSkinedElement;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.skin.GuiSkinBase;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ourten 29 oct. 2016
 * <p>
 * A custom node used only for itemstack display. It cannot be used as a
 * replacement of a slot.
 */
public class ItemStackView extends GuiSkinedElement
{
    private final BaseProperty<String>    alternateStringProperty;
    private final BaseProperty<ItemStack> stackProperty;

    private final BaseProperty<Boolean> itemTooltipProperty;

    private final BaseProperty<Color> colorProperty;

    private Consumer<List<ITextComponent>> stackTooltipModifier;

    public ItemStackView(ItemStack stack)
    {
        stackProperty = new BaseProperty<>(stack, "stackProperty");
        alternateStringProperty = new BaseProperty<>(null, "alternateStringProperty");
        itemTooltipProperty = new BaseProperty<>(false, "itemTooltipProperty");

        colorProperty = new BaseProperty<>(Color.WHITE, "colorProperty");
    }

    public ItemStackView()
    {
        this(ItemStack.EMPTY);
    }

    @Override
    public String type()
    {
        return "itemstack";
    }

    public BaseProperty<ItemStack> getStackProperty()
    {
        return stackProperty;
    }

    public BaseProperty<String> getAlternateStringProperty()
    {
        return alternateStringProperty;
    }

    public BaseProperty<Boolean> getItemTooltipProperty()
    {
        return itemTooltipProperty;
    }

    public BaseProperty<Color> getColorProperty()
    {
        return colorProperty;
    }

    public ItemStack getItemStack()
    {
        return stackProperty.getValue();
    }

    public void setItemStack(ItemStack stack)
    {
        stackProperty.setValue(stack);
    }

    public String getAlternateString()
    {
        return alternateStringProperty.getValue();
    }

    /**
     * @param alternateString a string to be displayed in place of the usual itemstack quantity
     *                        number at the down-right corner.
     */
    public void setAlternateString(String alternateString)
    {
        alternateStringProperty.setValue(alternateString);
    }

    public boolean hasItemTooltip()
    {
        return getItemTooltipProperty().getValue();
    }

    public void setItemTooltip(boolean tooltip)
    {
        getItemTooltipProperty().setValue(tooltip);
    }

    public Color getColor()
    {
        return getColorProperty().getValue();
    }

    public void setColor(Color color)
    {
        getColorProperty().setValue(color);
    }

    public Consumer<List<ITextComponent>> getStackTooltipModifier()
    {
        return stackTooltipModifier;
    }

    public void setStackTooltipModifier(Consumer<List<ITextComponent>> stackTooltipModifier)
    {
        this.stackTooltipModifier = stackTooltipModifier;
    }

    @Override
    protected GuiSkinBase<?> makeDefaultSkin()
    {
        return new ItemStackViewSkin(this, new ItemStackViewBehavior(this));
    }
}