package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.pane.GuiRelativePane;
import net.voxelindustry.brokkgui.element.pane.ScrollPane;
import net.voxelindustry.brokkgui.shape.Rectangle;

public class ScrollDemo extends GuiRelativePane implements IDemoCategory
{
    public ScrollDemo()
    {
        ScrollPane verticalScroll = new ScrollPane();
        verticalScroll.transform().sizeRatio(1, 0.5F);
        verticalScroll.setGripYWidth(12);
        verticalScroll.setGripYHeight(24);
        verticalScroll.setPannable(true);
        verticalScroll.setScrollable(false);
        verticalScroll.setPanSpeed(0.1F);

        Rectangle rectangle = new Rectangle();
        rectangle.width(64);
        rectangle.height(512);
        rectangle.id("vertscroll-rect");

        verticalScroll.setChild(rectangle);

        addChild(verticalScroll, 0.5f, 0.25f);

        ScrollPane horizontalScroll = new ScrollPane();
        horizontalScroll.transform().sizeRatio(1, 0.5F);
        horizontalScroll.setGripXWidth(24);
        horizontalScroll.setGripXHeight(12);

        Rectangle rectangle1 = new Rectangle();
        rectangle1.width(512);
        rectangle1.height(64);
        rectangle1.id("horiscroll-rect");

        horizontalScroll.setChild(rectangle1);

        addChild(horizontalScroll, 0.5f, 0.75f);
    }

    @Override
    public String getName()
    {
        return "Scroll";
    }
}
