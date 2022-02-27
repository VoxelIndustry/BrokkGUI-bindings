package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.pane.GuiPane;
import net.voxelindustry.brokkgui.element.pane.ScrollPane;
import net.voxelindustry.brokkgui.shape.Rectangle;

public class ScrollDemo extends GuiPane implements IDemoCategory
{
    public ScrollDemo()
    {
        var verticalScroll = new ScrollPane();
        verticalScroll.transform().widthRatio(1);
        verticalScroll.transform().heightRatio(0.5f);
        verticalScroll.scrollable().gripYWidth(12);
        verticalScroll.scrollable().gripYHeight(24);
        verticalScroll.scrollable().pannable(true);
        verticalScroll.scrollable().scrollable(false);
        verticalScroll.scrollable().panSpeed(0.1F);

        Rectangle rectangle = new Rectangle();
        rectangle.width(64);
        rectangle.height(512);
        rectangle.id("vertscroll-rect");

        verticalScroll.addChild(rectangle).centered();

        this.addChild(verticalScroll).relative(0.5F, 0.25F);

        ScrollPane horizontalScroll = new ScrollPane();
        horizontalScroll.transform().widthRatio(1);
        horizontalScroll.transform().heightRatio(0.5f);
        horizontalScroll.scrollable().gripXWidth(24);
        horizontalScroll.scrollable().gripXHeight(12);

        Rectangle rectangle1 = new Rectangle();
        rectangle1.width(512);
        rectangle1.height(64);
        rectangle1.id("horiscroll-rect");

        horizontalScroll.addChild(rectangle1).centered();

        this.addChild(horizontalScroll).relative(0.5F, 0.75F);
    }

    @Override
    public String getName()
    {
        return "Scroll";
    }
}
