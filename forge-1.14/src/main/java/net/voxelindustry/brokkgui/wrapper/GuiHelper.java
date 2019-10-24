package net.voxelindustry.brokkgui.wrapper;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.voxelindustry.brokkgui.data.RectCorner;
import net.voxelindustry.brokkgui.data.Vector2i;
import net.voxelindustry.brokkgui.internal.EGuiRenderMode;
import net.voxelindustry.brokkgui.internal.IGuiHelper;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.paint.Texture;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public class GuiHelper implements IGuiHelper
{
    public static RenderPass ITEM_MAIN  = RenderPass.create("item_main", 1);
    public static RenderPass ITEM_HOVER = RenderPass.create("item_hover", 3);

    private ItemRenderer        itemRender;
    private Minecraft           mc;
    private GuiRenderItemHelper itemHelper;

    private double alphaMask;

    public GuiHelper()
    {
        this.mc = Minecraft.getInstance();
        this.itemRender = this.mc.getItemRenderer();

        this.itemHelper = new GuiRenderItemHelper();

        this.alphaMask = 1;
    }

    @Override
    public void bindTexture(Texture texture)
    {
        this.mc.textureManager.bindTexture(new ResourceLocation(texture.getResource()));
    }

    @Override
    public void scissorBox(float f, float g, float h, float i)
    {
        int width = (int) (h - f);
        int height = (int) (i - g);
        double factor = this.mc.mainWindow.getGuiScaleFactor();
        Screen currentScreen = this.mc.currentScreen;
        if (currentScreen != null)
        {
            int bottomY = (int) (currentScreen.height - i);
            GL11.glScissor((int) (f * factor), (int) (bottomY * factor), (int) (width * factor),
                    (int) (height * factor));
        }
    }

    @Override
    public void drawString(String string, float x, float y, float zLevel, Color textColor, Color shadowColor)
    {
        GlStateManager.enableBlend();
        GlStateManager.clearCurrentColor();
        if (zLevel != 0)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(0, 0, zLevel);
        }

        if (shadowColor.getAlpha() != 0)
            this.mc.fontRenderer.drawString(string, x + 1, y + 1, this.applyAlphaMask(shadowColor).toRGBAInt());
        this.mc.fontRenderer.drawString(string, x, y, this.applyAlphaMask(textColor).toRGBAInt());
        if (zLevel != 0)
            GL11.glPopMatrix();
        GlStateManager.clearCurrentColor();
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
        GlStateManager.color4f(1, 1, 1, (float) (1 * this.alphaMask));
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
        GlStateManager.disableTexture();
        GlStateManager.color4f(color.getRed(), color.getGreen(), color.getBlue(), (float) (color.getAlpha() * alphaMask));
        renderer.beginDrawingQuads(false);
        renderer.addVertex(startX, startY, zLevel);
        renderer.addVertex(startX, startY + height, zLevel);
        renderer.addVertex(startX + width, startY + height, zLevel);
        renderer.addVertex(startX + width, startY, zLevel);
        renderer.endDrawing();
        GlStateManager.clearCurrentColor();
        GlStateManager.enableTexture();
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
            GlStateManager.disableTexture();
            GlStateManager.color4f(color.getRed(), color.getGreen(), color.getBlue(), (float) (color.getAlpha() *
                    alphaMask));
            renderer.beginDrawing(EGuiRenderMode.POINTS, false);
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
            GlStateManager.clearCurrentColor();
            GlStateManager.enableTexture();
            this.disableAlpha();
        }
        if (thin > 1)
            this.drawColoredEmptyCircle(renderer, startX, startY, radius - 1, zLevel, color, thin - 1);
    }

    @Override
    public void drawColoredCircle(IGuiRenderer renderer, float startX, float startY,
                                  float radius, float zLevel, Color c)
    {
        GlStateManager.disableTexture();
        this.enableAlpha();
        GlStateManager.color4f(c.getRed(), c.getGreen(), c.getBlue(), (float) (c.getAlpha() * alphaMask));
        renderer.beginDrawing(EGuiRenderMode.POINTS, false);
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
        GlStateManager.clearCurrentColor();
        GlStateManager.enableTexture();
        this.disableAlpha();
    }

    @Override
    public void drawTexturedCircle(IGuiRenderer renderer, float xStart, float yStart,
                                   float uMin, float vMin, float uMax, float vMax,
                                   float radius, float zLevel)
    {
        this.enableAlpha();
        GlStateManager.color4f(1, 1, 1, (float) alphaMask);
        renderer.beginDrawing(EGuiRenderMode.POINTS, true);
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
        GlStateManager.disableTexture();
        this.enableAlpha();
        GlStateManager.color4f(c.getRed(), c.getGreen(), c.getBlue(), (float) (c.getAlpha() * alphaMask));

        renderer.beginDrawing(EGuiRenderMode.LINES, false);
        GL11.glLineWidth(lineWeight);

        renderer.addVertex(startX, startY, zLevel);
        renderer.addVertex(endX, endY, zLevel);

        renderer.endDrawing();
        GlStateManager.clearCurrentColor();
        GlStateManager.enableTexture();
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
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);

        if (!stack.isEmpty())
        {
            float scaleX = width / 18;
            float scaleY = height / 18;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-(startX * (scaleX - 1)) - 8 * scaleX, -(startY * (scaleY - 1)) - 8 * scaleY, 0);
            GlStateManager.scalef(scaleX, scaleY, 1);
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            if (font == null)
                font = this.mc.fontRenderer;

            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 0.0F, 32.0F);

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableLighting();
            short short1 = 240;
            short short2 = 240;
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, short1 / 1.0F, short2 / 1.0F);

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
                                     Consumer<List<ITextComponent>> tooltipModifier)
    {
        List<ITextComponent> list = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ?
                ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
                list.set(i, list.get(i).applyTextStyle(stack.getRarity().color));
            else
                list.set(i, list.get(i).applyTextStyle(TextFormatting.GRAY));
        }

        if (tooltipModifier != null)
            tooltipModifier.accept(list);

        GuiUtils.drawHoveringText(stack, list.stream().map(ITextComponent::getFormattedText).collect(toList()),
                mouseX, mouseY, this.mc.mainWindow.getWidth(), this.mc.mainWindow.getHeight(),
                this.mc.mainWindow.getWidth(), this.mc.fontRenderer);
    }

    public void drawFluidStack(IGuiRenderer renderer, float startX, float startY, float width, float height,
                               float zLevel, FluidStack stack, boolean flowing)
    {
        this.mc.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        // FIXME : Cannot render still and flowing fluid in gui
        TextureAtlasSprite still = this.mc.getModelManager().getBlockModelShapes().getModel(stack.getFluid().getDefaultState().getBlockState()).getParticleTexture();

        int iconHeight = still.getHeight();
        float offsetHeight = height;

        int iteration = 0;
        while (offsetHeight != 0)
        {
            float curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;
            this.drawTexturedRect(renderer, startX, startY + height - offsetHeight,
                    still.getMinU(), still.getMinV(), still.getMaxU(), still.getMaxV(), width, curHeight, zLevel);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50)
                break;
        }
    }

    @Override
    public void translateVecToScreenSpace(Vector2i vec)
    {
        Screen currentScreen = this.mc.currentScreen;
        if (currentScreen != null)
        {
            vec.setX((int) (vec.getX() / ((float) currentScreen.width / this.mc.mainWindow.getWidth())));
            vec.setY((int) ((currentScreen.height - vec.getY()) / ((float) currentScreen.height / this.mc.mainWindow.getHeight())
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
        GlStateManager.enableAlphaTest();
        GlStateManager.enableDepthTest();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
    }

    private void disableAlpha()
    {
        GlStateManager.disableDepthTest();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
    }

    private ItemRenderer getRenderItem()
    {
        if (this.itemRender == null)
            this.itemRender = this.mc.getItemRenderer();
        return this.itemRender;
    }
}