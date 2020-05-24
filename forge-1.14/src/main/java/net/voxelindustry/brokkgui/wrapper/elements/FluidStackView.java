package net.voxelindustry.brokkgui.wrapper.elements;

import fr.ourten.teabeans.value.BaseProperty;
import net.minecraftforge.fluids.FluidStack;
import net.voxelindustry.brokkgui.control.GuiSkinedElement;
import net.voxelindustry.brokkgui.skin.GuiSkinBase;

public class FluidStackView extends GuiSkinedElement
{
    private BaseProperty<FluidStack> fluidStackProperty;

    public FluidStackView(FluidStack stack)
    {
        fluidStackProperty = new BaseProperty<>(stack, "fluidStackProperty");

        style().registerProperty("flowing", false, Boolean.class);
    }

    @Override
    protected GuiSkinBase<?> makeDefaultSkin()
    {
        return new FluidStackViewSkin(this, new FluidStackViewBehavior(this));
    }

    public BaseProperty<FluidStack> getFluidStackProperty()
    {
        return fluidStackProperty;
    }

    public BaseProperty<Boolean> getFlowingProperty()
    {
        return style().getProperty("flowing", Boolean.class);
    }

    public FluidStack getFluidStack()
    {
        return getFluidStackProperty().getValue();
    }

    public void setFluidStack(FluidStack stack)
    {
        getFluidStackProperty().setValue(stack);
    }

    public boolean isFlowing()
    {
        return getFlowingProperty().getValue();
    }

    public void setFlowing(boolean isFlowing)
    {
        getFlowingProperty().setValue(isFlowing);
    }

    @Override
    public String type()
    {
        return "fluidstack";
    }
}
