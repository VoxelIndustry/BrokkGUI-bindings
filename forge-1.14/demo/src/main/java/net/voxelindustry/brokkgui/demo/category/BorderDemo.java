package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.pane.GuiRelativePane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.style.StyleComponent;

public class BorderDemo extends GuiRelativePane implements IDemoCategory
{
    public BorderDemo()
    {
        Rectangle roundedBorder = new Rectangle();
        roundedBorder.id("round-border");
        roundedBorder.size(20, 20);
        roundedBorder.transform().scale(2);
        roundedBorder.get(StyleComponent.class).parseInlineCSS("border-width: 2 4 2 4; border-radius: 2; border-color: green; background-color: red;");

        addChild(roundedBorder, 0.25f, 0.5f);

        Rectangle imageBorder = new Rectangle();
        imageBorder.id("image-border");
        imageBorder.size(20, 20);
        imageBorder.transform().scale(2);
        imageBorder.get(StyleComponent.class).parseInlineCSS("border-image-source: assets(\"brokkguidemo:textures/gui/image_border.png\"); " +
                "border-width: 4; border-image-slice: 40%; border-image-outset: 4; border-image-fill: true; background-color: red;");

        addChild(imageBorder, 0.75f, 0.5f);
    }

    @Override
    public String getName()
    {
        return "Border";
    }
}
