package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.panel.GuiRelativePane;
import net.voxelindustry.brokkgui.shape.Rectangle;

public class BorderDemo extends GuiRelativePane
{
    public BorderDemo()
    {
        Rectangle rounded1 = new Rectangle();
        rounded1.setID("rounded1");
        rounded1.setSize(20, 20);
        rounded1.setScale(3);
        rounded1.setStyle("border-width: 2 4 2 4; border-radius: 2; border-color: green; background-color: red;");

        this.addChild(rounded1);
    }
}
