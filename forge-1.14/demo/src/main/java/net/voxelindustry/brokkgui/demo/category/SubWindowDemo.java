package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.demo.GuiDemo;
import net.voxelindustry.brokkgui.element.input.GuiButton;
import net.voxelindustry.brokkgui.element.pane.GuiRelativePane;
import net.voxelindustry.brokkgui.style.StyleComponent;
import net.voxelindustry.brokkgui.window.SubGuiScreen;

public class SubWindowDemo extends GuiRelativePane implements IDemoCategory
{
    public SubWindowDemo(GuiDemo parent)
    {
        GuiButton openWindow = new GuiButton("Open");
        openWindow.size(100, 20);

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

            size(150, 150);
            transform().zLevel(300);
            get(StyleComponent.class).parseInlineCSS("border-color: black; border-width: 1; background-color: #BDBDBD;");

            setCloseOnClick(false);

            GuiButton closeButton = new GuiButton("CLOSE");
            closeButton.setExpandToLabel(true);
            addChild(closeButton);
            RelativeBindingHelper.bindToPos(closeButton.transform(), transform());

            closeButton.setOnActionEvent(e -> close());
        }
    }
}
