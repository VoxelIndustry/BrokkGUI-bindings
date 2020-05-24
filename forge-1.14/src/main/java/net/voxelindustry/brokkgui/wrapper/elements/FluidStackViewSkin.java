package net.voxelindustry.brokkgui.wrapper.elements;

import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.skin.GuiBehaviorSkinBase;
import net.voxelindustry.brokkgui.wrapper.GuiHelper;

public class FluidStackViewSkin extends GuiBehaviorSkinBase<FluidStackView, FluidStackViewBehavior>
{
    public FluidStackViewSkin(FluidStackView model, FluidStackViewBehavior behavior)
    {
        super(model, behavior);
    }

    @Override
    public void render(RenderPass pass, IGuiRenderer renderer, int mouseX, int mouseY)
    {
        super.render(pass, renderer, mouseX, mouseY);

        if (pass == RenderPass.MAIN)
        {
            ((GuiHelper) renderer.getHelper()).drawFluidStack(renderer,
                    getModel().getLeftPos(), getModel().getTopPos(),
                    getModel().width(), getModel().height(), getModel().transform().zLevel(),
                    getModel().getFluidStack(), getModel().isFlowing());
        }
    }
}
