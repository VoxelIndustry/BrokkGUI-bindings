package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.data.RectSide;
import net.voxelindustry.brokkgui.data.Rotation;
import net.voxelindustry.brokkgui.element.input.GuiCheckbox;
import net.voxelindustry.brokkgui.element.input.GuiRadioButton;
import net.voxelindustry.brokkgui.element.input.GuiToggleGroup;
import net.voxelindustry.brokkgui.element.pane.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.style.StyleComponent;

public class RadioButtonDemo extends GuiAbsolutePane implements IDemoCategory
{
    public RadioButtonDemo()
    {
        Rectangle icon = new Rectangle(8, 8);
        icon.get(StyleComponent.class).parseInlineCSS("color: red;");

        GuiRadioButton radioButton = new GuiRadioButton("Right 1");
        radioButton.getLabel().setIcon(icon);
        radioButton.height(10);
        radioButton.transform().rotate(Rotation.build().fromCenter().angle(90).create());
        radioButton.transform().scale(2);

        GuiRadioButton radioButton2 = new GuiRadioButton("Nothing to see here 2");
        radioButton2.height(10);
        radioButton2.setButtonSide(RectSide.RIGHT);
        radioButton2.get(StyleComponent.class).parseInlineCSS("border-color: blue; border-width: 1;");

        GuiCheckbox checkbox = new GuiCheckbox("Left 3");
        checkbox.height(10);
        checkbox.setButtonSide(RectSide.LEFT);

        GuiToggleGroup toggleGroup = new GuiToggleGroup();
        toggleGroup.setAllowNothing(true);

        toggleGroup.addButtons(radioButton, radioButton2, checkbox);

        addChild(radioButton, 0, 0);
        addChild(radioButton2, 0, 15);
        addChild(checkbox, 0, 30);
    }

    @Override
    public String getName()
    {
        return "RadioButton";
    }
}
