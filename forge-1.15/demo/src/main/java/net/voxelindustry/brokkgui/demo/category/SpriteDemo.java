package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.panel.GuiRelativePane;
import net.voxelindustry.brokkgui.shape.Rectangle;

public class SpriteDemo extends GuiRelativePane implements IDemoCategory
{
    public SpriteDemo()
    {
        GuiLabel noRepeatSpriteLabel = new GuiLabel("No repeat");
        this.addChild(noRepeatSpriteLabel, 0.25F, 0.2F);

        Rectangle noRepeatRect = new Rectangle();
        noRepeatRect.setSize(16, 16);
        noRepeatRect.setStyle("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 16px, 16px)");
        noRepeatRect.setyTranslate(12);
        this.addChild(noRepeatRect, 0.25F, 0.2F);


        GuiLabel repeatXSpriteLabel = new GuiLabel("Repeat X");
        this.addChild(repeatXSpriteLabel, 0.75F, 0.2F);

        Rectangle repeatXRect = new Rectangle();
        repeatXRect.setSize(16, 16);
        repeatXRect.setStyle("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 8px, 16px); background-repeat: repeat-x;");
        repeatXRect.setyTranslate(12);
        this.addChild(repeatXRect, 0.75F, 0.2F);

        GuiLabel repeatYSpriteLabel = new GuiLabel("Repeat Y");
        this.addChild(repeatYSpriteLabel, 0.25F, 0.4F);

        Rectangle repeatYRect = new Rectangle();
        repeatYRect.setSize(16, 16);
        repeatYRect.setStyle("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 16px, 8px); background-repeat: repeat-y;");
        repeatYRect.setyTranslate(12);
        this.addChild(repeatYRect, 0.25F, 0.4F);

        GuiLabel repeatBothSpriteLabel = new GuiLabel("Repeat Both");
        this.addChild(repeatBothSpriteLabel, 0.75F, 0.4F);

        Rectangle repeatBothRect = new Rectangle();
        repeatBothRect.setSize(16, 16);
        repeatBothRect.setStyle("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 8px, 8px); background-repeat: repeat;");
        repeatBothRect.setyTranslate(12);
        this.addChild(repeatBothRect, 0.75F, 0.4F);

        GuiLabel animatedSpriteLabel = new GuiLabel("4 Frames");
        this.addChild(animatedSpriteLabel, 0.25F, 0.6F);

        Rectangle animatedRect = new Rectangle();
        animatedRect.setSize(16, 16);
        animatedRect.setID("animated-rect-demo");
        animatedRect.setyTranslate(24);
        this.addChild(animatedRect, 0.25F, 0.6F);

        GuiLabel animatedRepeatedSpriteLabel = new GuiLabel("4 Frames Repeat");
        this.addChild(animatedRepeatedSpriteLabel, 0.75F, 0.6F);

        Rectangle animatedRepeatedRect = new Rectangle();
        animatedRepeatedRect.setSize(16, 16);
        animatedRepeatedRect.setID("animated-repeated-rect-demo");
        animatedRepeatedRect.setyTranslate(24);
        this.addChild(animatedRepeatedRect, 0.75F, 0.6F);
    }

    @Override
    public String getName()
    {
        return "Sprites";
    }
}
