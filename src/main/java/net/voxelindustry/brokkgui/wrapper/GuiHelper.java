package net.voxelindustry.brokkgui.wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.voxelindustry.brokkgui.data.RectCorner;
import net.voxelindustry.brokkgui.data.Vector2i;
import net.voxelindustry.brokkgui.internal.GuiRenderMode;
import net.voxelindustry.brokkgui.internal.IGuiHelper;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.paint.Texture;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.function.Consumer;

public class GuiHelper implements IGuiHelper
{
    public static RenderPass ITEM_MAIN  = RenderPass.create("item_main", 1);
    public static RenderPass ITEM_HOVER = RenderPass.create("item_hover", 3);

    private RenderItem          itemRender;
    private Minecraft           mc;
    private GuiRenderItemHelper itemHelper;

    private double alphaMask;

    public GuiHelper()
    {
        this.mc = Minecraft.getMinecraft();
        this.itemRender = this.mc.getRenderItem();

        this.itemHelper = new GuiRenderItemHelper();

        this.alphaMask = 1;
    }

    @Override
    public void bindTexture(Texture texture)
    {
        this.mc.renderEngine.bindTexture(new ResourceLocation(texture.getResource()));
    }

    @Override
    public void scissorBox(float f, float g, float h, float i)
    {
        int xStart = (int) Math.floor(f);
        int yStart = (int) Math.floor(g);
        int xEnd = (int) Math.ceil(h);
        int yEnd = (int) Math.ceil(i);

        int width = xEnd - xStart;
        int height = yEnd - yStart;

        ScaledResolution sr = new ScaledResolution(this.mc);
        int factor = sr.getScaleFactor();
        GuiScreen currentScreen = this.mc.currentScreen;

        if (currentScreen != null)
        {
            int bottomY = (currentScreen.height - yEnd);
            GL11.glScissor((xStart * factor), bottomY * factor, width * factor, height * factor);
        }
    }

    @Override
    public void drawString(String string, float x, float y, float zLevel, Color textColor, Color shadowColor)
    {
        GlStateManager.enableBlend();
        GlStateManager.resetColor();
        if (zLevel != 0)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(0, 0, zLevel);
        }

        if (shadowColor.getAlpha() != 0)
            this.mc.fontRenderer.drawString(string, x + 1, y + 1, this.applyAlphaMask(shadowColor).toRGBAInt(), false);
        this.mc.fontRenderer.drawString(string, x, y, this.applyAlphaMask(textColor).toRGBAInt(), false);
        if (zLevel != 0)
            GL11.glPopMatrix();
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    @Override
    public void drawString(String string, float x, float y, float zLevel, Color textColor)
    {
        this.drawString(string, x, y, zLevel, textColor, Color.ALPHA);
    }

    @Override
    public void drawTexturedRect(IGuiRenderer renderer, float xStart, float yStart, float uMin, float vMin,
                                 float uMax, float vMax, float width, float height, float zLevel)
    {
        this.enableAlpha();
        GlStateManager.color(1, 1, 1, (float) (1 * this.alphaMask));
        renderer.beginDrawingQuads(true);
        renderer.addVertexWithUV(xStart, yStart + height, zLevel, uMin, vMax);
        renderer.addVertexWithUV(xStart + width, yStart + height, zLevel, uMax, vMax);
        renderer.addVertexWithUV(xStart + width, yStart, zLevel, uMax, vMin);
        renderer.addVertexWithUV(xStart, yStart, zLevel, uMin, vMin);
        renderer.endDrawing();
        this.disableAlpha();
    }

    @Override
    public void drawTexturedRect(IGuiRenderer renderer, float xStart, float yStart, float uMin, float vMin,
                                 float width, float height, float zLevel)
    {
        this.drawTexturedRect(renderer, xStart, yStart, uMin, vMin, 1, 1, width, height, zLevel);
    }

    @Override
    public void drawColoredEmptyRect(IGuiRenderer renderer, float startX, float startY, float width, float height,
                                     float zLevel, Color c, float thin)
    {
        this.drawColoredRect(renderer, startX, startY, width - thin, thin, zLevel, c);
        this.drawColoredRect(renderer, startX + width - thin, startY, thin, height - thin, zLevel, c);
        this.drawColoredRect(renderer, startX + thin, startY + height - thin, width - thin, thin, zLevel, c);
        this.drawColoredRect(renderer, startX, startY + thin, thin, height - thin, zLevel, c);
    }

    @Override
    public void drawColoredRect(IGuiRenderer renderer, float startX, float startY,
                                float width, float height, float zLevel, Color color)
    {
        this.enableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), (float) (color.getAlpha() * alphaMask));
        renderer.beginDrawingQuads(false);
        renderer.addVertex(startX, startY, zLevel);
        renderer.addVertex(startX, startY + height, zLevel);
        renderer.addVertex(startX + width, startY + height, zLevel);
        renderer.addVertex(startX + width, startY, zLevel);
        renderer.endDrawing();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        this.disableAlpha();
    }

    @Override
    public void drawColoredEmptyCircle(IGuiRenderer renderer, float startX, float startY, float radius, float zLevel,
                                       Color color, float thin)
    {
        if (thin > 0)
        {
            float x = radius;
            float y = 0;
            float err = 0;

            this.enableAlpha();
            GlStateManager.disableTexture2D();
            GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), (float) (color.getAlpha() *
                    alphaMask));
            renderer.beginDrawing(GuiRenderMode.POINTS, false);
            while (x >= y)
            {
                renderer.addVertex(startX + x, startY + y, zLevel);
                renderer.addVertex(startX + y, startY + x, zLevel);

                renderer.addVertex(startX - y, startY + x, zLevel);
                renderer.addVertex(startX - x, startY + y, zLevel);

                renderer.addVertex(startX - x, startY - y, zLevel);
                renderer.addVertex(startX - y, startY - x, zLevel);

                renderer.addVertex(startX + y, startY - x, zLevel);
                renderer.addVertex(startX + x, startY - y, zLevel);

                y += 1;
                err += 1 + 2 * y;
                if (2 * (err - x) + 1 > 0)
                {
                    x -= 1;
                    err += 1 - 2 * x;
                }
            }
            renderer.endDrawing();
            GlStateManager.resetColor();
            GlStateManager.enableTexture2D();
            this.disableAlpha();
        }
        if (thin > 1)
            this.drawColoredEmptyCircle(renderer, startX, startY, radius - 1, zLevel, color, thin - 1);
    }

    @Override
    public void drawColoredCircle(IGuiRenderer renderer, float startX, float startY,
                                  float radius, float zLevel, Color c)
    {
        GlStateManager.disableTexture2D();
        this.enableAlpha();
        GlStateManager.color(c.getRed(), c.getGreen(), c.getBlue(), (float) (c.getAlpha() * alphaMask));
        renderer.beginDrawing(GuiRenderMode.POINTS, false);
        float r2 = radius * radius;
        float area = r2 * 4;
        float rr = radius * 2;

        for (int i = 0; i < area; i++)
        {
            float tx = i % rr - radius;
            float ty = i / rr - radius;

            if (tx * tx + ty * ty <= r2)
                renderer.addVertex(startX + tx, startY + ty, zLevel);
        }
        renderer.endDrawing();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        this.disableAlpha();
    }

    @Override
    public void drawTexturedCircle(IGuiRenderer renderer, float xStart, float yStart,
                                   float uMin, float vMin, float uMax, float vMax,
                                   float radius, float zLevel)
    {
        this.enableAlpha();
        GlStateManager.color(1, 1, 1, (float) alphaMask);
        renderer.beginDrawing(GuiRenderMode.POINTS, true);
        float r2 = radius * radius;
        float area = r2 * 4;
        float rr = radius * 2;

        for (int i = 0; i < area; i++)
        {
            float tx = i % rr - radius;
            float ty = i / rr - radius;

            if (tx * tx + ty * ty <= r2)
                renderer.addVertexWithUV(xStart + tx, yStart + ty, zLevel,
                        uMin + tx / rr * (uMax - uMin), vMin + ty / rr * (vMax - vMin));
        }
        renderer.endDrawing();
        this.disableAlpha();
    }

    @Override
    public void drawTexturedCircle(IGuiRenderer renderer, float xStart, float yStart,
                                   float uMin, float vMin, float radius, float zLevel)
    {
        this.drawTexturedCircle(renderer, xStart, yStart, uMin, vMin, 1, 1, radius, zLevel);
    }

    @Override
    public void drawColoredLine(IGuiRenderer renderer, float startX, float startY,
                                float endX, float endY, float lineWeight, float zLevel,
                                Color c)
    {
        GlStateManager.disableTexture2D();
        this.enableAlpha();
        GlStateManager.color(c.getRed(), c.getGreen(), c.getBlue(), (float) (c.getAlpha() * alphaMask));

        renderer.beginDrawing(GuiRenderMode.LINES, false);
        GL11.glLineWidth(lineWeight);

        renderer.addVertex(startX, startY, zLevel);
        renderer.addVertex(endX, endY, zLevel);

        renderer.endDrawing();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        this.disableAlpha();
    }

    @Override
    public void drawColoredArc(IGuiRenderer renderer, float centerX, float centerY, float radius, float zLevel,
                               Color color, RectCorner corner)
    {
        float x = radius, y = 0;
        float P = 1 - radius;

        while (x > y)
        {
            y++;
            if (P <= 0)
                P = P + 2 * y + 1;
            else
            {
                x--;
                P = P + 2 * y - 2 * x + 1;
            }

            if (x < y)
                break;

            switch (corner)
            {
                case TOP_LEFT:
                    this.drawColoredRect(renderer, -x + centerX, -y + centerY, 1, 1, zLevel, color);
                    if (x != y)
                        this.drawColoredRect(renderer, -y + centerX, -x + centerY, 1, 1, zLevel, color);
                    break;
                case TOP_RIGHT:
                    this.drawColoredRect(renderer, x + centerX, -y + centerY, 1, 1, zLevel, color);
                    if (x != y)
                        this.drawColoredRect(renderer, y + centerX, -x + centerY, 1, 1, zLevel, color);
                    break;
                case BOTTOM_LEFT:
                    this.drawColoredRect(renderer, -x + centerX, y + centerY, 1, 1, zLevel, color);
                    if (x != y)
                        this.drawColoredRect(renderer, -y + centerX, x + centerY, 1, 1, zLevel, color);
                    break;
                case BOTTOM_RIGHT:
                    this.drawColoredRect(renderer, x + centerX, y + centerY, 1, 1, zLevel, color);
                    if (x != y)
                        this.drawColoredRect(renderer, y + centerX, x + centerY, 1, 1, zLevel, color);
                    break;
            }
        }
    }

    @Override
    public void drawColoredCross(IGuiRenderer renderer, float centerX, float centerY, float radius, float width, float zLevel, Color color)
    {
        // Left Arm
        this.drawColoredRect(renderer, centerX - radius, centerY - width / 2, radius - width / 2, width, zLevel, color);

        // Right Arm
        this.drawColoredRect(renderer, centerX + width / 2, centerY - width / 2, radius - width / 2, width, zLevel, color);

        // Upper Arm
        this.drawColoredRect(renderer, centerX - width / 2, centerY - radius, width, radius - width / 2, zLevel, color);

        // Bottom Arm
        this.drawColoredRect(renderer, centerX - width / 2, centerY + width / 2, width, radius - width / 2, zLevel, color);

        // Center
        this.drawColoredRect(renderer, centerX - width / 2, centerY - width / 2, width, width, zLevel, color);
    }

    public void drawItemStack(IGuiRenderer renderer, float startX, float startY,
                              float width, float height, float zLevel, ItemStack stack,
                              Color color)
    {
        this.drawItemStack(renderer, startX, startY, width, height, zLevel, stack, null, color);
    }

    public void drawItemStack(IGuiRenderer renderer, float startX, float startY,
                              float width, float height, float zLevel, ItemStack stack,
                              String displayString, Color color)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F);

        if (!stack.isEmpty())
        {
            float scaleX = width / 18;
            float scaleY = height / 18;
            GlStateManager.pushMatrix();
            GlStateManager.translate(-(startX * (scaleX - 1)) - 8 * scaleX, -(startY * (scaleY - 1)) - 8 * scaleY, 0);
            GlStateManager.scale(scaleX, scaleY, 1);
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            if (font == null)
                font = this.mc.fontRenderer;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 32.0F);

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableLighting();
            short short1 = 240;
            short short2 = 240;
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);

            this.itemHelper.renderItemStack(stack, (int) startX, (int) startY, zLevel, applyAlphaMask(color));
            this.getRenderItem().renderItemOverlayIntoGUI(font, stack, (int) startX, (int) startY, displayString);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    public void drawItemStackTooltip(IGuiRenderer renderer, int mouseX, int mouseY, ItemStack stack)
    {
        this.drawItemStackTooltip(renderer, mouseX, mouseY, stack, null);
    }

    public void drawItemStackTooltip(IGuiRenderer renderer, int mouseX, int mouseY, ItemStack stack,
                                     Consumer<List<String>> tooltipModifier)
    {
        List<String> list = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ?
                ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
                list.set(i, stack.getRarity().color + list.get(i));
            else
                list.set(i, TextFormatting.GRAY + list.get(i));
        }

        if (tooltipModifier != null)
            tooltipModifier.accept(list);

        GuiUtils.drawHoveringText(stack, list, mouseX, mouseY, this.mc.displayWidth, this.mc.displayHeight,
                this.mc.displayWidth, this.mc.fontRenderer);
    }

    public void drawFluidStack(IGuiRenderer renderer, float startX, float startY, float width, float height,
                               float zLevel, FluidStack stack, boolean flowing)
    {
        this.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ResourceLocation still = flowing ? stack.getFluid().getFlowing(stack) : stack.getFluid().getStill(stack);
        TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(still.toString());

        int iconHeight = sprite.getIconHeight();
        float offsetHeight = height;

        int iteration = 0;
        while (offsetHeight != 0)
        {
            float curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;
            this.drawTexturedRect(renderer, startX, startY + height - offsetHeight,
                    sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), width, curHeight, zLevel);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50)
                break;
        }
    }

    @Override
    public void translateVecToScreenSpace(Vector2i vec)
    {
        GuiScreen currentScreen = this.mc.currentScreen;
        if (currentScreen != null)
        {
            vec.setX((int) (vec.getX() / ((float) currentScreen.width / this.mc.displayWidth)));
            vec.setY((int) ((currentScreen.height - vec.getY()) / ((float) currentScreen.height / this.mc.displayHeight)
                    - 1));
        }
    }

    @Override
    public String trimStringToPixelWidth(String str, int pixelWidth)
    {
        return this.mc.fontRenderer.trimStringToWidth(str, pixelWidth);
    }

    @Override
    public float getStringWidth(String str)
    {
        return this.mc.fontRenderer.getStringWidth(str);
    }

    @Override
    public float getStringHeight()
    {
        return this.mc.fontRenderer.FONT_HEIGHT;
    }

    @Override
    public void beginScissor()
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void endScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void startAlphaMask(double opacity)
    {
        this.alphaMask = opacity;
        this.enableAlpha();
    }

    @Override
    public void closeAlphaMask()
    {
        this.alphaMask = 1;
        this.disableAlpha();
    }

    private Color applyAlphaMask(Color src)
    {
        if (this.alphaMask == 1)
            return src;
        Color result = Color.from(src);

        result.setAlpha((float) (src.getAlpha() * this.alphaMask));
        return result;
    }

    private void enableAlpha()
    {
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
    }

    private void disableAlpha()
    {
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    private RenderItem getRenderItem()
    {
        if (this.itemRender == null)
            this.itemRender = this.mc.getRenderItem();
        return this.itemRender;
    }
}