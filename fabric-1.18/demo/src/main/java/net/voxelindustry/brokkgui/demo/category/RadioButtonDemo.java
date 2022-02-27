package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.data.RectSide;
import net.voxelindustry.brokkgui.data.Rotation;
import net.voxelindustry.brokkgui.element.input.GuiCheckbox;
import net.voxelindustry.brokkgui.element.input.GuiRadioButton;
import net.voxelindustry.brokkgui.element.input.GuiToggleGroup;
import net.voxelindustry.brokkgui.element.pane.GuiPane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.style.StyleComponent;

public class RadioButtonDemo extends GuiPane implements IDemoCategory
{
    public RadioButtonDemo()
    {
        Rectangle icon = new Rectangle(8, 8);
        icon.get(StyleComponent.class).parseInlineCSS("color: red;");

        GuiRadioButton radioButton = new GuiRadioButton("Right 1");
        radioButton.icon(icon);
        radioButton.height(10);
        radioButton.transform().rotate(Rotation.build().fromCenter().angle(90).create());
        radioButton.transform().scale(2);

        GuiRadioButton radioButton2 = new GuiRadioButton("Nothing to see here 2");
        radioButton2.height(10);
        radioButton2.buttonSide(RectSide.RIGHT);
        radioButton2.style().parseInlineCSS("border-color: blue; border-width: 1;");

        GuiCheckbox checkbox = new GuiCheckbox("Left 3");
        checkbox.height(10);
        checkbox.buttonSide(RectSide.LEFT);

        final GuiToggleGroup toggleGroup = new GuiToggleGroup();
        toggleGroup.setAllowNothing(true);

        toggleGroup.addButtons(
                radioButton.toggleButtonComponent(),
                radioButton2.toggleButtonComponent(),
                checkbox.toggleButtonComponent()
        );

        this.addChild(radioButton).leftTop();
        this.addChild(radioButton2).absolute(0, 15);
        this.addChild(checkbox).absolute(0, 30);
    }

    @Override
    public String getName()
    {
        return "RadioButton";
    }
}
