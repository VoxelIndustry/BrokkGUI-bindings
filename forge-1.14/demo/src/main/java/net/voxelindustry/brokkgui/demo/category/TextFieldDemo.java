package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.input.GuiTextfield;
import net.voxelindustry.brokkgui.element.input.GuiTextfieldComplete;
import net.voxelindustry.brokkgui.element.pane.GuiAbsolutePane;
import net.voxelindustry.brokkgui.style.StyleComponent;

import java.util.Arrays;

public class TextFieldDemo extends GuiAbsolutePane implements IDemoCategory
{
    public TextFieldDemo()
    {
        GuiTextfield field = new GuiTextfield();
        field.transform().widthRatio(1);
        field.height(40);

        field.get(StyleComponent.class).parseInlineCSS("border-color: black; border-width: 1;");
        addChild(field, 0, 0);

        GuiTextfieldComplete autocomplete = new GuiTextfieldComplete();
        autocomplete.setPromptText("Enter a color");
        autocomplete.setCharBeforeCompletion(1);
        autocomplete.setSuggestions(Arrays.asList("Red", "Blue", "Purple", "Magenta", "Yellow", "Pink", "Gray"));
        autocomplete.transform().widthRatio(1);
        autocomplete.height(40);
        autocomplete.id("autocomplete");

        addChild(autocomplete, 0, 50);

        GuiTextfield expandField = new GuiTextfield();
        expandField.height(20);
        expandField.setExpandToText(true);

        expandField.get(StyleComponent.class).parseInlineCSS("border-color: black; border-width: 1;");
        addChild(expandField, 0, 120);
    }

    @Override
    public String getName()
    {
        return "TextField";
    }
}
