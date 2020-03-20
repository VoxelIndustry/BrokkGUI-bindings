package net.voxelindustry.brokkgui.demo;

import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.demo.category.AnimationDemo;
import net.voxelindustry.brokkgui.demo.category.BorderDemo;
import net.voxelindustry.brokkgui.demo.category.IDemoCategory;
import net.voxelindustry.brokkgui.demo.category.LabelDemo;
import net.voxelindustry.brokkgui.demo.category.ListViewDemo;
import net.voxelindustry.brokkgui.demo.category.RadioButtonDemo;
import net.voxelindustry.brokkgui.demo.category.ScrollDemo;
import net.voxelindustry.brokkgui.demo.category.SpriteDemo;
import net.voxelindustry.brokkgui.demo.category.TextFieldDemo;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.ToastManager;
import net.voxelindustry.brokkgui.element.input.GuiButton;
import net.voxelindustry.brokkgui.gui.BrokkGuiScreen;
import net.voxelindustry.brokkgui.internal.profiler.GuiProfiler;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.panel.GuiRelativePane;
import net.voxelindustry.brokkgui.sprite.Texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiDemo extends BrokkGuiScreen
{
    public ToastManager toastManager;

    private List<IDemoCategory> demoPages = new ArrayList<>();
    private GuiNode             currentCategory;

    private final Random rand = new Random();

    public GuiDemo()
    {
        super(0.5f, 0.5f, 200, 200);

        this.addStylesheet("/assets/brokkguidemo/gui/css/demo.css");

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        mainPanel.setBackgroundTexture(new Texture("brokkguidemo:textures/gui/background.png"));

        demoPages.add(new TextFieldDemo());
        demoPages.add(new ListViewDemo(this));
        demoPages.add(new AnimationDemo());
        demoPages.add(new RadioButtonDemo());
        demoPages.add(new LabelDemo());
        demoPages.add(new ScrollDemo());
        demoPages.add(new BorderDemo());
        demoPages.add(new SpriteDemo());

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setID("body");
        body.setSizeRatio(1, 1);
        mainPanel.addChild(body);

        GuiRelativePane categoryHolder = new GuiRelativePane();
        categoryHolder.setSizeRatio(1, 1);
        mainPanel.addChild(categoryHolder);

        for (int index = 0; index < demoPages.size(); index++)
        {
            GuiButton button = new GuiButton(demoPages.get(index).getName());
            button.addStyleClass("demo-category-button");
            button.setSize(55, 20);
            int finalIndex = index;
            button.setOnActionEvent(e ->
            {
                currentCategory = (GuiNode) demoPages.get(finalIndex);
                currentCategory.setSize(200, 200);
                categoryHolder.addChild(currentCategory);
                body.setVisible(false);
            });
            body.addChild(button, 8 + index % 3 * 63, 10 + index / 3 * 25);
        }

        this.getMainPanel().setID("mainpane");

        this.toastManager = new ToastManager(this);
        toastManager.setRelativeXPos(0.5f);
        toastManager.setRelativeYPos(0.98f);
        toastManager.setToastAlignment(RectAlignment.MIDDLE_UP);

        GuiLabel label = new GuiLabel("Toast");
        label.addStyleClass("toast-label");
        label.setTextPadding(new RectBox(2, 0, 2, 0));
        label.setWidth(150);
        label.setHeight(20);
        toastManager.addToast(label, 5_000L);

        BrokkGuiPlatform.getInstance().setProfiler(new GuiProfiler());
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void tick()
    {
        super.tick();

        if (rand.nextInt(20) == 3)
            System.out.println(((GuiProfiler) BrokkGuiPlatform.getInstance().getProfiler()).getHumanReport());
    }
}
