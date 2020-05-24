package net.voxelindustry.brokkgui.demo;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.pane.GuiAbsolutePane;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.sprite.Texture;
import net.voxelindustry.brokkgui.window.SubGuiScreen;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.brokkgui.wrapper.elements.FluidStackView;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;
import net.voxelindustry.brokkgui.wrapper.elements.MCTooltip;

public class GuiContainerDemo extends BrokkGuiContainer<ContainerDemo>
{
    private static final int xSize = 176, ySize = 200;

    private static final Texture BACKGROUND = new Texture("brokkguidemo:textures/gui/container_background.png", 0, 0,
            GuiContainerDemo.xSize / 256.0f, GuiContainerDemo.ySize / 256.0f);

    public GuiContainerDemo(ContainerDemo container)
    {
        super(container);

        setWidth(GuiContainerDemo.xSize);
        setHeight(GuiContainerDemo.ySize);
        setxRelativePos(0.5f);
        setyRelativePos(0.5f);

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        setMainPanel(mainPanel);

        ItemStackView view = new ItemStackView(new ItemStack(Items.APPLE));
        view.setItemTooltip(true);
        view.width(18);
        view.height(18);
        view.setColor(new Color(1, 1, 1, 0.5f));
        mainPanel.addChild(view);

        ItemStackView enchantedApple = new ItemStackView(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
        enchantedApple.setItemTooltip(true);
        enchantedApple.width(18);
        enchantedApple.height(18);
        mainPanel.addChild(enchantedApple);

        FluidStackView fluidStackView = new FluidStackView(new FluidStack(Fluids.WATER, 1000));
        fluidStackView.width(18);
        fluidStackView.height(64);
        fluidStackView.setFlowing(true);
        fluidStackView.setOnClickEvent(e -> addSubGui(new SubWindow()));

        fluidStackView.setTooltip(MCTooltip.build().line("TEST TOOLTIP").create());

        mainPanel.addChild(fluidStackView, 4, 4);

        mainPanel.paint().backgroundTexture(GuiContainerDemo.BACKGROUND);
    }

    private static class SubWindow extends SubGuiScreen
    {
        public SubWindow()
        {
            super(0.5f, 0.5f);
            paint().backgroundColor(Color.GRAY);
            size(128, 128);

            GuiAbsolutePane mainPanel = new GuiAbsolutePane();
            mainPanel.transform().sizeRatio(1, 1);
            addChild(mainPanel);
            RelativeBindingHelper.bindToPos(mainPanel.transform(), transform());

            ItemStackView view = new ItemStackView(new ItemStack(Items.APPLE));
            view.setItemTooltip(true);
            view.width(18);
            view.height(18);

            for (int x = 0; x < 5; x++)
            {
                ItemStackView blockView = new ItemStackView(new ItemStack(Blocks.DIRT, 64));
                blockView.setItemTooltip(true);
                blockView.width(18);
                blockView.height(18);
                blockView.transform().zLevel(301);
                mainPanel.addChild(blockView, 18 * x, 18);
            }
        }
    }
}