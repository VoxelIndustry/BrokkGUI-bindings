package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.data.RectSide;
import net.voxelindustry.brokkgui.element.input.Button;
import net.voxelindustry.brokkgui.element.shape.Rectangle;

public class ButtonDemo extends DemoCategory
{
    public ButtonDemo()
    {
        Button simpleButton = new Button("Simple");
        simpleButton.style().parseInlineCSS("background-color: #A1A1A1; border-color: #141414; border-width: 1;");

        this.addChild(simpleButton, 0, 0);

        this.setupIconButtons();
    }

    private void setupIconButtons()
    {
        int count = 0;
        for (RectSide side : RectSide.values())
        {
            Button iconButton = new Button("Icon " + side.name());

            Rectangle icon = new Rectangle();
            icon.size(8, 8);
            icon.style().styleClass().add("icon");

            iconButton.icon(icon);
            iconButton.iconSide(side);

            iconButton.style().parseInlineCSS("background-color: #A1A1A1; border-color: #141414; border-width: 1;");

            this.addChild(iconButton, count % 2 * 100, 30 + count / 2 * 30);
            count++;
        }
    }
}
