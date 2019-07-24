package net.voxelindustry.brokkgui.demo;

import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.demo.category.ButtonDemo;
import net.voxelindustry.brokkgui.demo.category.DemoCategory;
import net.voxelindustry.brokkgui.element.Label;
import net.voxelindustry.brokkgui.element.input.Button;
import net.voxelindustry.brokkgui.element.pane.AbsolutePane;
import net.voxelindustry.brokkgui.gui.BrokkGuiScreen;

import java.util.LinkedHashMap;
import java.util.Map;

public class GuiDemo extends BrokkGuiScreen
{
    private final Map<String, DemoCategory> categoryMap = new LinkedHashMap<>();
    private       String                    currentCategory;

    public GuiDemo()
    {
        super(0.5f, 0.5f, 200, 200);

        categoryMap.put("Buttons", new ButtonDemo());

        final AbsolutePane mainPane = new AbsolutePane();
        mainPane.setId("mainpane");
        this.setMainPanel(mainPane);

        AbsolutePane header = new AbsolutePane();
        header.size(200, 10);

        AbsolutePane categoryGrid = new AbsolutePane();
        categoryGrid.transform().sizeRatio(1, 1);
        AbsolutePane categoryPane = new AbsolutePane();
        categoryPane.transform().sizeRatio(1, 1);
        Label title = new Label();

        Button backButton = new Button("BACK");
        backButton.style().parseInlineCSS("border-color: #373737; border-width: 1; border-radius: 2; background-color: #505050;");
        backButton.textPadding(RectBox.ONE);
        backButton.setVisible(false);

        backButton.onActionEvent(e ->
        {
            categoryPane.removeChild(categoryMap.get(currentCategory));
            categoryPane.setVisible(false);
            categoryGrid.setVisible(true);
            backButton.setVisible(false);
        });
        header.addChild(backButton, 1, 1);
        header.addChild(title, backButton.width() + 5, 1);
        categoryPane.setVisible(false);

        int count = 0;
        for (Map.Entry<String, DemoCategory> categoryEntry : categoryMap.entrySet())
        {
            Button categoryButton = new Button(categoryEntry.getKey());
            categoryButton.style().styleClass().add("category-button");
            categoryButton.size(100, 30);
            categoryButton.onActionEvent(e ->
            {
                currentCategory = categoryEntry.getKey();
                title.text(categoryEntry.getKey() + " Demo");
                categoryPane.setVisible(true);
                categoryGrid.setVisible(false);
                backButton.setVisible(true);
                categoryPane.addChild(categoryEntry.getValue(), 0, 10);
            });

            categoryGrid.addChild(categoryButton, 100 * (count % 2), 20 + count / 2);
            count++;
        }

        mainPane.addChild(header, 0, 0);
        mainPane.addChild(categoryGrid, 0, 10);
        mainPane.addChild(categoryPane, 0, 10);

        this.addStylesheet("/assets/brokkguidemo/gui/css/demo.css");
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }
}
