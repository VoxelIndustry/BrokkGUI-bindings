package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.demo.GuiDemo;
import net.voxelindustry.brokkgui.element.input.GuiButton;
import net.voxelindustry.brokkgui.gui.SubGuiScreen;
import net.voxelindustry.brokkgui.panel.GuiRelativePane;

public class SubWindowDemo extends GuiRelativePane implements IDemoCategory
{
    public SubWindowDemo(GuiDemo parent)
    {
        GuiButton openWindow = new GuiButton("Open");
        openWindow.setSize(100, 20);

        SubGuiScreen subWindow = new NestedWindow();

        openWindow.setOnActionEvent(e -> parent.addSubGui(subWindow));

        addChild(openWindow, 0.5F, 0.5F);
    }

    @Override
    public String getName()
    {
        return "SubWindow";
    }

    private static final class NestedWindow extends SubGuiScreen
    {
        public NestedWindow()
        {
            super(0.5F, 0.5F);

            setSize(150, 150);
            setzLevel(300);
            setStyle("border-color: black; border-width: 1; background-color: #BDBDBD;");

            setCloseOnClick(false);

            GuiButton closeButton = new GuiButton("CLOSE");
            closeButton.setExpandToLabel(true);
            this.addChild(closeButton);
            RelativeBindingHelper.bindToPos(closeButton, this);

            closeButton.setOnActionEvent(e -> this.close());
        }
    }
}
