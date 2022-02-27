package net.voxelindustry.brokkgui.demo.category;

import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.pane.GuiPane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.style.StyleComponent;

public class SpriteDemo extends GuiPane implements IDemoCategory
{
    public SpriteDemo()
    {
        GuiLabel noRepeatSpriteLabel = new GuiLabel("No repeat");
        this.addChild(noRepeatSpriteLabel).relative(0.25F, 0.2F);

        Rectangle noRepeatRect = new Rectangle();
        noRepeatRect.size(16, 16);
        noRepeatRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 16px, 16px)");
        noRepeatRect.yTranslate(12);
        this.addChild(noRepeatRect).relative(0.25F, 0.2F);

        GuiLabel repeatXSpriteLabel = new GuiLabel("Repeat X");
        this.addChild(repeatXSpriteLabel).relative(0.75F, 0.2F);

        Rectangle repeatXRect = new Rectangle();
        repeatXRect.size(16, 16);
        repeatXRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 8px, 16px); background-repeat: repeat-x;");
        repeatXRect.yTranslate(12);
        this.addChild(repeatXRect).relative(0.75F, 0.2F);

        GuiLabel repeatYSpriteLabel = new GuiLabel("Repeat Y");
        this.addChild(repeatYSpriteLabel).relative(0.25F, 0.4F);

        Rectangle repeatYRect = new Rectangle();
        repeatYRect.size(16, 16);
        repeatYRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 16px, 8px); background-repeat: repeat-y;");
        repeatYRect.yTranslate(12);
        this.addChild(repeatYRect).relative(0.25F, 0.4F);

        GuiLabel repeatBothSpriteLabel = new GuiLabel("Repeat Both");
        this.addChild(repeatBothSpriteLabel).relative(0.75F, 0.4F);

        Rectangle repeatBothRect = new Rectangle();
        repeatBothRect.size(16, 16);
        repeatBothRect.get(StyleComponent.class).parseInlineCSS("background-texture: assets(\"brokkguidemo:textures/gui/sprite_background.png, 8px, 8px); background-repeat: repeat;");
        repeatBothRect.yTranslate(12);
        this.addChild(repeatBothRect).relative(0.75F, 0.4F);

        GuiLabel animatedSpriteLabel = new GuiLabel("4 Frames");
        this.addChild(animatedSpriteLabel).relative(0.25F, 0.6F);

        Rectangle animatedRect = new Rectangle();
        animatedRect.size(16, 16);
        animatedRect.id("animated-rect-demo");
        animatedRect.yTranslate(24);
        this.addChild(animatedRect).relative(0.25F, 0.6F);

        GuiLabel animatedRepeatedSpriteLabel = new GuiLabel("4 Frames Repeat");
        this.addChild(animatedRepeatedSpriteLabel).relative(0.75F, 0.6F);

        Rectangle animatedRepeatedRect = new Rectangle();
        animatedRepeatedRect.size(16, 16);
        animatedRepeatedRect.id("animated-repeated-rect-demo");
        animatedRepeatedRect.yTranslate(24);
        this.addChild(animatedRepeatedRect).relative(0.75F, 0.6F);
    }

    @Override
    public String getName()
    {
        return "Sprites";
    }
}
