package net.voxelindustry.brokkgui.wrapper;

import com.google.common.base.CharMatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.voxelindustry.brokkgui.internal.ITextHelper;
import net.voxelindustry.brokkgui.text.TextSettings;

public class GuiTextHelper implements ITextHelper
{
    private TextRenderer textRenderer;

    private TextRenderer textRenderer()
    {
        if (textRenderer == null)
            textRenderer = MinecraftClient.getInstance().textRenderer;
        return textRenderer;
    }

    @Override
    public String trimStringToWidth(String text, float width, TextSettings settings)
    {
        return textRenderer().trimToWidth(text, (int) width);
    }

    @Override
    public String trimStringToWidth(String text, float width, String ellipsis, TextSettings settings)
    {
        float textSize = getStringWidth(text, settings);

        if (textSize < width)
            return text;

        float targetSize = width - getStringWidth(ellipsis, settings);
        return trimStringToWidth(text, targetSize, settings) + ellipsis;
    }

    @Override
    public float getStringWidth(String text, TextSettings settings)
    {
        return textRenderer().getWidth(text);
    }

    @Override
    public float getStringWidthMultiLine(String text, TextSettings settings)
    {
        if (text == null)
            return 0;

        var lines = text.split("\n");

        var seen = false;
        var longestLine = 0F;
        for (String line : lines)
        {
            var lineWidth = getStringWidth(line, settings);
            if (!seen || Float.compare(lineWidth, longestLine) > 0)
            {
                seen = true;
                longestLine = lineWidth;
            }
        }
        return seen ? longestLine : 0;
    }

    @Override
    public float getStringHeight(TextSettings settings)
    {
        return textRenderer().fontHeight;
    }

    @Override
    public float getStringHeightMultiLine(String text, TextSettings settings)
    {
        if (text == null)
            return 0;

        var lines = CharMatcher.is('\n').countIn(text);

        if (!text.endsWith("\n"))
            lines++;

        return lines * getStringHeight(settings) * settings.lineSpacingMultiplier();
    }

    @Override
    public float getDefaultFontSize()
    {
        return textRenderer().fontHeight;
    }
}
