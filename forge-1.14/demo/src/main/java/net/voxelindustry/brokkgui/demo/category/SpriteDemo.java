package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.pane.GuiRelativePane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.style.StyleComponent;

public class SpriteDemo extends GuiRelativePane implements IDemoCategory
{
    public SpriteDemo()
    {
        GuiLabel noRepeatSpriteLabel = new GuiLabel("No repeat");
        addChild(noRepeatSpriteLabel, 0.25F, 0.2F);

        Rectangle noRepeatRect = new Rectangle();
        noRepeatRect.size(16, 16);
        noRepeatRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 16px, 16px)");
        noRepeatRect.yTranslate(12);
        addChild(noRepeatRect, 0.25F, 0.2F);

        GuiLabel repeatXSpriteLabel = new GuiLabel("Repeat X");
        addChild(repeatXSpriteLabel, 0.75F, 0.2F);

        Rectangle repeatXRect = new Rectangle();
        repeatXRect.size(16, 16);
        repeatXRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 8px, 16px); background-repeat: repeat-x;");
        repeatXRect.yTranslate(12);
        addChild(repeatXRect, 0.75F, 0.2F);

        GuiLabel repeatYSpriteLabel = new GuiLabel("Repeat Y");
        addChild(repeatYSpriteLabel, 0.25F, 0.4F);

        Rectangle repeatYRect = new Rectangle();
        repeatYRect.size(16, 16);
        repeatYRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 16px, 8px); background-repeat: repeat-y;");
        repeatYRect.yTranslate(12);
        addChild(repeatYRect, 0.25F, 0.4F);

        GuiLabel repeatBothSpriteLabel = new GuiLabel("Repeat Both");
        addChild(repeatBothSpriteLabel, 0.75F, 0.4F);

        Rectangle repeatBothRect = new Rectangle();
        repeatBothRect.size(16, 16);
        repeatBothRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 8px, 8px); background-repeat: repeat;");
        repeatBothRect.yTranslate(12);
        addChild(repeatBothRect, 0.75F, 0.4F);

        GuiLabel animatedSpriteLabel = new GuiLabel("4 Frames");
        addChild(animatedSpriteLabel, 0.25F, 0.6F);

        Rectangle animatedRect = new Rectangle();
        animatedRect.size(16, 16);
        animatedRect.id("animated-rect-demo");
        animatedRect.yTranslate(24);
        addChild(animatedRect, 0.25F, 0.6F);

        GuiLabel animatedRepeatedSpriteLabel = new GuiLabel("4 Frames Repeat");
        addChild(animatedRepeatedSpriteLabel, 0.75F, 0.6F);

        Rectangle animatedRepeatedRect = new Rectangle();
        animatedRepeatedRect.size(16, 16);
        animatedRepeatedRect.id("animated-repeated-rect-demo");
        animatedRepeatedRect.yTranslate(24);
        addChild(animatedRepeatedRect, 0.75F, 0.6F);
    }

    @Override
    public String getName()
    {
        return "Sprites";
    }
}
