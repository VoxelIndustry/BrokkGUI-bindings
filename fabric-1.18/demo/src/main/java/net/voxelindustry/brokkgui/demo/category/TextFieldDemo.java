package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.input.GuiTextField;
import net.voxelindustry.brokkgui.element.pane.GuiPane;

public class TextFieldDemo extends GuiPane implements IDemoCategory
{
    public TextFieldDemo()
    {
        var field = new GuiTextField();
        field.transform().widthRatio(1);
        field.height(40);

        field.style().parseInlineCSS("border-color: black; border-width: 1;");
        this.addChild(field).leftTop();

        var expandField = new GuiTextField();
        expandField.height(20);
        expandField.expandToText(true);

        expandField.style().parseInlineCSS("border-color: black; border-width: 1;");
        this.addChild(expandField).absolute(0, 120);
    }

    @Override
    public String getName()
    {
        return "TextField";
    }
}
