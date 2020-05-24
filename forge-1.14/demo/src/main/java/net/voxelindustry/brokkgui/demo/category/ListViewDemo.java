package net.voxelindustry.brokkgui.demo.category;

import net.minecraft.util.text.TextFormatting;
import net.voxelindustry.brokkgui.demo.GuiDemo;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.GuiListView;
import net.voxelindustry.brokkgui.element.GuiTooltip;
import net.voxelindustry.brokkgui.element.input.GuiButton;
import net.voxelindustry.brokkgui.element.pane.GuiRelativePane;
import net.voxelindustry.brokkgui.style.StyleComponent;
import net.voxelindustry.brokkgui.wrapper.elements.MCTooltip;

import java.util.Arrays;

public class ListViewDemo extends GuiRelativePane implements IDemoCategory
{
    public ListViewDemo(GuiDemo gui)
    {
        GuiListView<String> labelList = new GuiListView<>();

        labelList.width(75);
        labelList.height(30);

        labelList.setCellHeight(20);
        labelList.setCellWidth(75);
        labelList.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        labelList.setPlaceholder(new GuiLabel("I'm a placeholder"));

        labelList.setElements(Arrays.asList("One", "Two", "Three"));

        addChild(labelList, 0.25f, 0.5f);

        GuiListView<GuiButton> buttonList = new GuiListView<>();

        buttonList.size(75, 30);

        buttonList.setCellHeight(20);
        buttonList.setCellWidth(75);

        GuiButton button1 = new GuiButton("HEY 1");
        GuiButton button2 = new GuiButton("HELLO");
        GuiButton button3 = new GuiButton("LALALA");
        buttonList.setElements(Arrays.asList(button1, button2, button3));
        buttonList.setCellYPadding(1);

        button1.setTooltip(new GuiTooltip("This is a button"));
        button2.setTooltip(MCTooltip.build().line(TextFormatting.RED + "Another button").create());

        addChild(buttonList, 0.75f, 0.5f);

        GuiLabel toastLabel = new GuiLabel("You clicked on the button!");
        toastLabel.style().addStyleClass("toast-label");
        toastLabel.width(150);
        toastLabel.height(20);
        button1.setOnActionEvent(e -> gui.toastManager.addToast(toastLabel, 3000L));
    }

    @Override
    public String getName()
    {
        return "ListView";
    }
}
