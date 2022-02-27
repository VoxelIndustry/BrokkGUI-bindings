package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.animation.transition.ScaleTransition;
import net.voxelindustry.brokkgui.control.GuiButtonBase;
import net.voxelindustry.brokkgui.element.input.GuiRadioButton;
import net.voxelindustry.brokkgui.element.pane.GuiPane;

import java.util.concurrent.TimeUnit;

public class AnimationDemo extends GuiPane implements IDemoCategory
{
    public AnimationDemo()
    {
        GuiButtonBase movingButton = new GuiRadioButton("GO LEFT");
        movingButton.expandToText(true);
        movingButton.height(15);

        ScaleTransition leftTransition = new ScaleTransition(movingButton, 3, TimeUnit.SECONDS);
        leftTransition.maxCycles(2);
        leftTransition.reverse(true);

        leftTransition.setTranslateX(2);
        leftTransition.setTranslateY(2);

        movingButton.setOnActionEvent(e ->
        {
            if (leftTransition.isRunning())
                leftTransition.reset();
            else
                leftTransition.restart();
        });
        this.addChild(movingButton).relative(0.5F, 0.5F);
    }

    @Override
    public String getName()
    {
        return "Animation";
    }
}
