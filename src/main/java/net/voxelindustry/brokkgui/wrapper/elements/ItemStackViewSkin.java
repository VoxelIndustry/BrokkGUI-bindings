package net.voxelindustry.brokkgui.wrapper.elements;

import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.skin.GuiBehaviorSkinBase;
import net.voxelindustry.brokkgui.wrapper.GuiHelper;

/**
 * @author Ourten 29 oct. 2016
 */
public class ItemStackViewSkin extends GuiBehaviorSkinBase<ItemStackView, ItemStackViewBehavior>
{
    public ItemStackViewSkin(ItemStackView model, ItemStackViewBehavior behavior)
    {
        super(model, behavior);
    }

    @Override
    public void render(RenderPass pass, IGuiRenderer renderer, int mouseX, int mouseY)
    {
        super.render(pass, renderer, mouseX, mouseY);

        if (pass == GuiHelper.ITEM_MAIN)
        {
            ((GuiHelper) renderer.getHelper()).drawItemStack(renderer,
                    getModel().getLeftPos() + getModel().getWidth() / 2,
                    getModel().getTopPos() + getModel().getHeight() / 2,
                    getModel().getWidth(), getModel().getHeight(), getModel().getzLevel(),
                    getModel().getItemStack(), getModel().getAlternateString(), getModel().getColor());
        }
        else if (pass == GuiHelper.ITEM_HOVER)
        {
            if (getModel().isHovered() && getModel().hasItemTooltip() && !getModel().getItemStack().isEmpty())
                ((GuiHelper) renderer.getHelper()).drawItemStackTooltip(renderer, mouseX, mouseY,
                        getModel().getItemStack(), getModel().getStackTooltipModifier());
        }
    }
}