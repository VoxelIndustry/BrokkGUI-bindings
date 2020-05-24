package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.animation.transition.ScaleTransition;
import net.voxelindustry.brokkgui.control.GuiButtonBase;
import net.voxelindustry.brokkgui.element.input.GuiRadioButton;
import net.voxelindustry.brokkgui.element.pane.GuiRelativePane;

import java.util.concurrent.TimeUnit;

public class AnimationDemo extends GuiRelativePane implements IDemoCategory
{
    public AnimationDemo()
    {
        GuiButtonBase movingButton = new GuiRadioButton("GO LEFT");
        movingButton.setExpandToLabel(true);
        movingButton.transform().height(15);

        ScaleTransition leftTransition = new ScaleTransition(movingButton, 3, TimeUnit.SECONDS);
        leftTransition.setMaxCycles(2);
        leftTransition.setReverse(true);

        leftTransition.setTranslateX(2);
        leftTransition.setTranslateY(2);

        movingButton.setOnActionEvent(e ->
        {
            if (leftTransition.isRunning())
                leftTransition.reset();
            else
                leftTransition.restart();
        });
        addChild(movingButton, 0.5F, 0.5F);
    }

    @Override
    public String getName()
    {
        return "Animation";
    }
}
