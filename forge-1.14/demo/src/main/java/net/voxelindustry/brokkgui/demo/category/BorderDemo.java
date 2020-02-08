package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.panel.GuiRelativePane;
import net.voxelindustry.brokkgui.shape.Rectangle;

public class BorderDemo extends GuiRelativePane implements IDemoCategory
{
    public BorderDemo()
    {
        Rectangle roundedBorder = new Rectangle();
        roundedBorder.setID("round-border");
        roundedBorder.setSize(20, 20);
        roundedBorder.setScale(2);
        roundedBorder.setStyle("border-width: 2 4 2 4; border-radius: 2; border-color: green; background-color: red;");

        this.addChild(roundedBorder, 0.25f, 0.5f);

        Rectangle imageBorder = new Rectangle();
        imageBorder.setID("image-border");
        imageBorder.setSize(20, 20);
        imageBorder.setScale(2);
        imageBorder.setStyle("border-image-source: url(\"brokkguidemo:textures/gui/image_border.png\"); " +
                "border-width: 4; border-image-slice: 40%; border-image-outset: 4; border-image-fill: true; background-color: red;");

        this.addChild(imageBorder, 0.75f, 0.5f);
    }

    @Override
    public String getName()
    {
        return "Border";
    }
}
