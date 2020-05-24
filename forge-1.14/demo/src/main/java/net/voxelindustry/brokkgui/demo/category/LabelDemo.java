package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.data.RectSide;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.pane.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.style.StyleComponent;

public class LabelDemo extends GuiAbsolutePane implements IDemoCategory
{
    public LabelDemo()
    {
        setupLeftIcons();
        setupRightIcons();
        setupTopIcons();
        setupBottomIcons();
    }

    private void setupLeftIcons()
    {
        Rectangle icon = new Rectangle(8, 8);
        icon.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label = new GuiLabel("I Left T Middle", icon);
        label.width(100);
        label.setTextPadding(new RectBox(2, 0, 2, 0));
        label.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label, 5, 5);

        Rectangle icon2 = new Rectangle(8, 8);
        icon2.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label2 = new GuiLabel("I Left T Left", icon2);
        label2.width(100);
        label2.setTextAlignment(RectAlignment.LEFT_CENTER);
        label2.setTextPadding(new RectBox(2, 5, 2, 0));
        label2.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label2, 5, 20);

        Rectangle icon3 = new Rectangle(8, 8);
        icon3.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label3 = new GuiLabel("I Left T Right", icon3);
        label3.width(100);
        label3.setTextAlignment(RectAlignment.RIGHT_CENTER);
        label3.setTextPadding(new RectBox(2, 5, 2, 0));
        label3.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label3, 5, 35);
    }

    private void setupRightIcons()
    {
        Rectangle icon = new Rectangle(8, 8);
        icon.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label = new GuiLabel("I Right T Middle", icon);
        label.setIconSide(RectSide.RIGHT);
        label.width(100);
        label.setTextPadding(new RectBox(2, 0, 2, 0));
        label.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label, 5, 50);

        Rectangle icon2 = new Rectangle(8, 8);
        icon2.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label2 = new GuiLabel("I Right T Left", icon2);
        label2.setIconSide(RectSide.RIGHT);
        label2.width(100);
        label2.setTextAlignment(RectAlignment.LEFT_CENTER);
        label2.setTextPadding(new RectBox(2, 5, 2, 0));
        label2.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label2, 5, 65);

        Rectangle icon3 = new Rectangle(12, 12);
        icon3.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label3 = new GuiLabel("I Right T Right", icon3);
        label3.setIconSide(RectSide.RIGHT);
        label3.width(100);
        label3.setTextAlignment(RectAlignment.RIGHT_CENTER);
        label3.setTextPadding(new RectBox(2, 0, 2, 5));
        label3.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label3, 5, 80);
    }

    private void setupTopIcons()
    {
        Rectangle icon = new Rectangle(8, 8);
        icon.get(StyleComponent.class).parseInlineCSS("color: red;");

        GuiLabel label = new GuiLabel("I Up T Middle", icon);
        label.width(80);
        label.setIconSide(RectSide.UP);
        label.setTextPadding(new RectBox(2, 0, 2, 0));
        label.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label, 105, 5);

        Rectangle icon2 = new Rectangle(8, 8);
        icon2.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label2 = new GuiLabel("I Up T Left", icon2);
        label2.width(80);
        label2.setIconSide(RectSide.UP);
        label2.setTextAlignment(RectAlignment.LEFT_CENTER);
        label2.setTextPadding(new RectBox(2, 5, 2, 0));
        label2.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label2, 105, 30);

        Rectangle icon3 = new Rectangle(8, 8);
        icon3.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label3 = new GuiLabel("I Up T Right", icon3);
        label3.width(80);
        label3.setIconSide(RectSide.UP);
        label3.setTextAlignment(RectAlignment.RIGHT_CENTER);
        label3.setTextPadding(new RectBox(2, 5, 2, 0));
        label3.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label3, 105, 55);
    }

    private void setupBottomIcons()
    {
        Rectangle icon = new Rectangle(8, 8);
        icon.get(StyleComponent.class).parseInlineCSS("color: red;");

        GuiLabel label = new GuiLabel("I Down T Middle", icon);
        label.width(80);
        label.setIconSide(RectSide.DOWN);
        label.setTextPadding(new RectBox(2, 0, 2, 0));
        label.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label, 105, 80);

        Rectangle icon2 = new Rectangle(8, 8);
        icon2.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label2 = new GuiLabel("I Down T Left", icon2);
        label2.width(80);
        label2.setIconSide(RectSide.DOWN);
        label2.setTextAlignment(RectAlignment.LEFT_CENTER);
        label2.setTextPadding(new RectBox(2, 5, 2, 0));
        label2.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label2, 105, 105);

        Rectangle icon3 = new Rectangle(8, 8);
        icon3.get(StyleComponent.class).parseInlineCSS("background-color: red;");

        GuiLabel label3 = new GuiLabel("I Down T Right", icon3);
        label3.width(80);
        label3.setIconSide(RectSide.DOWN);
        label3.setTextAlignment(RectAlignment.RIGHT_CENTER);
        label3.setTextPadding(new RectBox(2, 5, 2, 0));
        label3.get(StyleComponent.class).parseInlineCSS("border-color: gray; border-width: 1;");

        addChild(label3, 105, 130);
    }

    @Override
    public String getName()
    {
        return "Label";
    }
}
