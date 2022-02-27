package net.voxelindustry.brokkgui.demo;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.voxelindustry.brokkcolor.Color;
import net.voxelindustry.brokkgui.element.pane.GuiPane;
import net.voxelindustry.brokkgui.sprite.Texture;
import net.voxelindustry.brokkgui.window.SubGuiScreen;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;

public class GuiContainerDemo extends BrokkGuiContainer<ContainerDemo>
{
    private static final int xSize = 176, ySize = 200;

    private static final Texture BACKGROUND = new Texture("brokkguidemo:textures/gui/container_background.png",
            0, 0,
            GuiContainerDemo.xSize / 256F, GuiContainerDemo.ySize / 256F);

    public GuiContainerDemo(final ContainerDemo container)
    {
        super(container);

        this.setWidth(GuiContainerDemo.xSize);
        this.setHeight(GuiContainerDemo.ySize);
        this.setxRelativePos(0.5F);
        this.setyRelativePos(0.5F);

        var mainPane = (GuiPane) root();

        var view = new ItemStackView(new ItemStack(Items.APPLE));
        view.itemTooltip(true);
        view.width(18);
        view.height(18);
        view.color(new Color(0.5F, 1, 0.5F, 0.5F));
        mainPane.addChild(view).absolute(getWidth() / 2 - 9, getHeight() / 2 - 18);

        var enchantedApple = new ItemStackView(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
        enchantedApple.itemTooltip(true);
        enchantedApple.width(18);
        enchantedApple.height(18);
        mainPane.addChild(enchantedApple).absolute(getWidth() / 2 - 9, getHeight() / 2);

        enchantedApple.setOnClickEvent(e -> this.addSubGui(new SubWindow()));

        mainPane.paint().backgroundTexture(GuiContainerDemo.BACKGROUND);
    }

    private static class SubWindow extends SubGuiScreen
    {
        public SubWindow()
        {
            super(0.5F, 0.5F);
            this.paint().backgroundColor(Color.GRAY);
            this.size(128, 128);

            ItemStackView view = new ItemStackView(new ItemStack(Items.APPLE));
            view.itemTooltip(true);
            view.width(18);
            view.height(18);

            for (int x = 0; x < 5; x++)
            {
                ItemStackView blockView = new ItemStackView(new ItemStack(Blocks.DIRT, 64));
                blockView.itemTooltip(true);
                blockView.width(18);
                blockView.height(18);
                blockView.transform().zTranslate(301);
                addChild(blockView).absolute(18 * x, 18);
            }
        }
    }
}