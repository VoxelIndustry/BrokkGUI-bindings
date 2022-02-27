package net.voxelindustry.brokkgui.demo;

import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.component.GuiElement;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.demo.category.AnimationDemo;
import net.voxelindustry.brokkgui.demo.category.BorderDemo;
import net.voxelindustry.brokkgui.demo.category.IDemoCategory;
import net.voxelindustry.brokkgui.demo.category.LabelDemo;
import net.voxelindustry.brokkgui.demo.category.RadioButtonDemo;
import net.voxelindustry.brokkgui.demo.category.ScrollDemo;
import net.voxelindustry.brokkgui.demo.category.SpriteDemo;
import net.voxelindustry.brokkgui.demo.category.SubWindowDemo;
import net.voxelindustry.brokkgui.demo.category.TextFieldDemo;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.ToastManager;
import net.voxelindustry.brokkgui.element.input.GuiButton;
import net.voxelindustry.brokkgui.element.pane.GuiPane;
import net.voxelindustry.brokkgui.profiler.GuiProfiler;
import net.voxelindustry.brokkgui.sprite.Texture;
import net.voxelindustry.brokkgui.window.BrokkGuiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiDemo extends BrokkGuiScreen
{
    public ToastManager toastManager;

    private final List<IDemoCategory> demoPages = new ArrayList<>();

    private GuiElement currentCategory;

    private final Random rand = new Random();

    public GuiDemo()
    {
        super(0.5f, 0.5f, 200, 200);

        this.addStylesheet("/assets/brokkguidemo/gui/css/demo.css");

        var mainPane = ((GuiPane) root());
        root().paint().backgroundTexture(new Texture("brokkguidemo:textures/gui/background.png"));
        root().id("mainpane");

        demoPages.add(new TextFieldDemo());
        demoPages.add(new AnimationDemo());
        demoPages.add(new RadioButtonDemo());
        demoPages.add(new LabelDemo());
        demoPages.add(new ScrollDemo());
        demoPages.add(new BorderDemo());
        demoPages.add(new SpriteDemo());
        demoPages.add(new SubWindowDemo(this));

        var body = new GuiPane();
        body.id("body");
        body.transform().sizeRatio(1, 1);
        mainPane.addChild(body).centered();

        var categoryHolder = new GuiPane();
        categoryHolder.transform().sizeRatio(1, 1);
        mainPane.addChild(categoryHolder).centered();

        for (int index = 0; index < demoPages.size(); index++)
        {
            GuiButton button = new GuiButton(demoPages.get(index).getName());
            button.style().addStyleClass("demo-category-button");
            button.size(55, 20);
            int finalIndex = index;
            button.setOnActionEvent(e ->
            {
                currentCategory = (GuiElement) demoPages.get(finalIndex);
                currentCategory.size(200, 200);
                categoryHolder.addChild(currentCategory).centered();
                body.setVisible(false);
            });
            body.addChild(button).absolute(8 + index % 3 * 63, 10 + index / 3 * 25);
        }


        this.toastManager = new ToastManager(this);
        toastManager.setRelativeXPos(0.5f);
        toastManager.setRelativeYPos(0.98f);
        toastManager.setToastAlignment(RectAlignment.MIDDLE_UP);

        var label = new GuiLabel("Toast");
        label.style().addStyleClass("toast-label");
        label.textPadding(new RectBox(2, 0, 2, 0));
        label.width(150);
        label.height(20);
        toastManager.addToast(label, 5_000L);

        BrokkGuiPlatform.getInstance().setProfiler(new GuiProfiler());
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }
}
