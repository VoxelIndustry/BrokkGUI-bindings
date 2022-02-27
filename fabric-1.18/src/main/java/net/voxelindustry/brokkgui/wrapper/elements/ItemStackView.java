package net.voxelindustry.brokkgui.wrapper.elements;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.voxelindustry.brokkcolor.Color;
import net.voxelindustry.brokkgui.component.GuiElement;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ourten 29 oct. 2016
 * <p>
 * A custom node used only for itemstack display. It cannot be used as a
 * replacement of a slot.
 */
public class ItemStackView extends GuiElement
{
    private ItemStackViewComponent itemStackViewComponent;

    public ItemStackView(ItemStack stack)
    {
        itemStackViewComponent().itemStack(stack);
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

    @Override
    public void postConstruct()
    {
        super.postConstruct();

        itemStackViewComponent = provide(ItemStackViewComponent.class);
    }

    public ItemStackViewComponent itemStackViewComponent()
    {
        return itemStackViewComponent;
    }

    public ItemStack itemStack()
    {
        return itemStackViewComponent.itemStack();
    }

    public void itemStack(ItemStack stack)
    {
        itemStackViewComponent.itemStack(stack);
    }

    public String alternateString()
    {
        return itemStackViewComponent.alternateString();
    }

    public void alternateString(String alternateString)
    {
        itemStackViewComponent.alternateString(alternateString);
    }

    public boolean itemTooltip()
    {
        return itemStackViewComponent.itemTooltip();
    }

    public void itemTooltip(boolean tooltip)
    {
        itemStackViewComponent.itemTooltip(tooltip);
    }

    public Color color()
    {
        return itemStackViewComponent.color();
    }

    public void color(Color color)
    {
        itemStackViewComponent.color(color);
    }

    public Consumer<List<Text>> stackTooltipModifier()
    {
        return itemStackViewComponent.stackTooltipModifier();
    }

    public void stackTooltipModifier(Consumer<List<Text>> stackTooltipModifier)
    {
        itemStackViewComponent.stackTooltipModifier(stackTooltipModifier);
    }
}