package net.voxelindustry.brokkgui.wrapper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.voxelindustry.brokkgui.data.RectCorner;
import net.voxelindustry.brokkgui.data.Vector2i;
import net.voxelindustry.brokkgui.internal.EGuiRenderMode;
import net.voxelindustry.brokkgui.internal.IGuiHelper;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.sprite.SpriteRotation;
import net.voxelindustry.brokkgui.sprite.Texture;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class GuiHelper implements IGuiHelper
{
    public static final RenderPass ITEM_MAIN  = RenderPass.create("item_main", 1);
    public static final RenderPass ITEM_HOVER = RenderPass.create("item_hover", 3);

    private       ItemRenderer        itemRender;
    private final MinecraftClient     mc;
    private final GuiRenderItemHelper itemHelper;

    private double alphaMask;

    private MatrixStack matrices;

    public GuiHelper()
    {
        this.mc = MinecraftClient.getInstance();
        this.itemRender = this.mc.getItemRenderer();

        this.itemHelper = new GuiRenderItemHelper();

        this.alphaMask = 1;
    }

    public void setMatrices(MatrixStack matrices)
    {
        this.matrices = matrices;
    }

    @Override
    public void bindTexture(Texture texture)
    {
        this.mc.getTextureManager().bindTexture(new Identifier(texture.getResource()));
    }

    @Override
    public void scissorBox(float xStart, float yStart, float xEnd, float yEnd)
    {
        double factor = this.mc.getWindow().getScaleFactor();

        // Needed to prevent pixel bleeding when scissor is active
        // This must be properly checked after a binding update
        // TODO : Ensure there are only 1 - 2 - 3 and 4 scaleFactor for MC GUI
        int topOffset = 0;
        int bottomOffset = 0;

        int heightRatio = (int) ((double) mc.getWindow().getFramebufferHeight() / factor);
        boolean doesHeightNeedOffset = (double) mc.getWindow().getFramebufferHeight() / factor > (double) heightRatio;

        if (factor == 4)
        {
            topOffset = -4;
            bottomOffset = 1;
        }
        else if (factor == 3)
        {
            if (doesHeightNeedOffset)
            {
                topOffset = -2;
                bottomOffset = 1;
            }
            else
            {
                topOffset = -3;
                bottomOffset = 3;
            }
        }
        else if (factor == 2)
        {
            if (doesHeightNeedOffset)
            {
                topOffset = -2;
                bottomOffset = 1;
            }
            else
            {
                topOffset = -2;
                bottomOffset = 2;
            }
        }
        else if (factor == 1)
        {
            topOffset = -1;
            bottomOffset = 1;
        }

        int width = (int) (xEnd - xStart);
        int height = (int) (yEnd - yStart);
        Screen currentScreen = this.mc.currentScreen;
        if (currentScreen != null)
        {
            int bottomY = (int) (currentScreen.height - yEnd);
            GL11.glScissor((int) (xStart * factor), (int) (bottomY * factor) + bottomOffset, (int) (width * factor), (int) (height * factor) + topOffset);
        }
    }

    @Override
    public void drawString(String string, float x, float y, float zLevel, Color textColor, Color shadowColor)
    {
        GlStateManager.enableBlend();
        GlStateManager.clearCurrentColor();
        if (zLevel != 0)
        {
            matrices.push();
            matrices.translate(0,0,zLevel);
        }

        if (shadowColor.getAlpha() != 0)
            this.mc.textRenderer.draw(matrices, string, x + 1, y + 1, this.applyAlphaMask(shadowColor).toRGBAInt());
        this.mc.textRenderer.draw(matrices, string, x, y, this.applyAlphaMask(textColor).toRGBAInt());
        if (zLevel != 0)
            matrices.pop();
        GlStateManager.clearCurrentColor();
        GlStateManager.disableBlend();
    }

    @Override
    public void drawString(String string, float x, float y, float zLevel, Color textColor)
    {
        this.drawString(string, x, y, zLevel, textColor, Color.ALPHA);
    }

    @Override
    public void drawStringMultiline(String string, float x, float y, float zLevel, Color textColor, Color shadowColor, float lineSpacing)
    {
        String[] lines = StringUtils.splitPreserveAllTokens(string, '\n');
        float lineHeight = getStringHeight();

        for (int index = 0; index < lines.length; index++)
            drawString(lines[index], x, y + (lineHeight + lineSpacing) * index, zLevel, textColor, shadowColor);
    }

    @Override
    public void drawTexturedRect(IGuiRenderer renderer, float xStart, float yStart, float uMin, float vMin,
                                 float uMax, float vMax, float width, float height, float zLevel, SpriteRotation rotation)
    {
        this.enableAlpha();
        GlStateManager.color4f(1, 1, 1, (float) (1 * this.alphaMask));
        renderer.beginDrawingQuads(true);

        switch (rotation)
        {
            case NONE:
                renderer.addVertexWithUV(xStart, yStart + height, zLevel, uMin, vMax);
                renderer.addVertexWithUV(xStart + width, yStart + height, zLevel, uMax, vMax);
                renderer.addVertexWithUV(xStart + width, yStart, zLevel, uMax, vMin);
                renderer.addVertexWithUV(xStart, yStart, zLevel, uMin, vMin);
                break;
            case CLOCKWISE:
                renderer.addVertexWithUV(xStart, yStart + height, zLevel, uMax, vMax);
                renderer.addVertexWithUV(xStart + width, yStart + height, zLevel, uMax, vMin);
                renderer.addVertexWithUV(xStart + width, yStart, zLevel, uMin, vMin);
                renderer.addVertexWithUV(xStart, yStart, zLevel, uMin, vMax);
                break;
            case UPSIDE:
                renderer.addVertexWithUV(xStart, yStart + height, zLevel, uMax, vMin);
                renderer.addVertexWithUV(xStart + width, yStart + height, zLevel, uMin, vMin);
                renderer.addVertexWithUV(xStart + width, yStart, zLevel, uMin, vMax);
                renderer.addVertexWithUV(xStart, yStart, zLevel, uMax, vMax);
                break;
            case COUNTERCLOCKWISE:
                renderer.addVertexWithUV(xStart, yStart + height, zLevel, uMin, vMin);
                renderer.addVertexWithUV(xStart + width, yStart + height, zLevel, uMin, vMax);
                renderer.addVertexWithUV(xStart + width, yStart, zLevel, uMax, vMax);
                renderer.addVertexWithUV(xStart, yStart, zLevel, uMax, vMin);
                break;
        }

        renderer.endDrawing();
        this.disableAlpha();
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
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (!stack.isEmpty())
        {
            float scaleX = width / 18;
            float scaleY = height / 18;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-(startX * (scaleX - 1)) - 8 * scaleX, -(startY * (scaleY - 1)) - 8 * scaleY, 0);
            GlStateManager.scalef(scaleX, scaleY, 1);
            TextRenderer font = this.mc.textRenderer;

            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 0.0F, 32.0F);

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableLighting();
            short short1 = 240;
            short short2 = 240;
            GlStateManager.multiTexCoords2f(GL13.GL_TEXTURE1, short1 / 1.0F, short2 / 1.0F);

            this.itemHelper.renderItemStack(stack, (int) startX, (int) startY, zLevel, applyAlphaMask(color));
            this.getRenderItem().renderGuiItemOverlay(font, stack, (int) startX, (int) startY, displayString);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }

    public void drawItemStackTooltip(IGuiRenderer renderer, int mouseX, int mouseY, ItemStack stack)
    {
        this.drawItemStackTooltip(renderer, mouseX, mouseY, stack, null);
    }

    public void drawItemStackTooltip(IGuiRenderer renderer, int mouseX, int mouseY, ItemStack stack,
                                     Consumer<List<? extends StringRenderable>> tooltipModifier)
    {
        List<Text> lines = stack.getTooltip(this.mc.player, this.mc.options.advancedItemTooltips ?
                TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);

        if (tooltipModifier != null)
            tooltipModifier.accept(lines);

        drawHoveringTooltip(renderer, mouseX + 12, mouseY - 12, lines);
    }

    public void drawHoveringTooltip(IGuiRenderer renderer, int posX, int posY, List<Text> lines)
    {
        if (lines.isEmpty())
            return;

        int i = 0;

        for (Text line : lines)
        {
            int lineWidth = mc.textRenderer.getWidth(line);
            if (lineWidth > i)
            {
                i = lineWidth;
            }
        }

        int linePadding = 8;
        if (lines.size() > 1)
            linePadding += 2 + (lines.size() - 1) * 10;

        if (posX + i > this.mc.getWindow().getWidth())
            posX -= 28 + i;

        if (posY + linePadding + 6 > this.mc.getWindow().getHeight())
            posY = this.mc.getWindow().getHeight() - linePadding - 6;

        matrices.push();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getModel();
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 4, posX + i + 3, posY - 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY + linePadding + 3, posX + i + 3, posY + linePadding + 4, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 3, posX + i + 3, posY + linePadding + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, posX - 4, posY - 3, posX - 3, posY + linePadding + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, posX + i + 3, posY - 3, posX + i + 4, posY + linePadding + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 3 + 1, posX - 3 + 1, posY + linePadding + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, posX + i + 2, posY - 3 + 1, posX + i + 3, posY + linePadding + 3 - 1, 400, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 3, posX + i + 3, posY - 3 + 1, 400, 1347420415, 1347420415);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY + linePadding + 2, posX + i + 3, posY + linePadding + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0D, 0.0D, 400.0D);

        for (int s = 0; s < lines.size(); ++s)
        {
            StringRenderable stringRenderable = lines.get(s);
            if (stringRenderable != null)
                mc.textRenderer.draw(stringRenderable, (float) posX, (float) posY, -1, true, matrix4f, immediate, false, 0, 15728880);

            if (s == 0)
                posY += 2;
            posY += 10;
        }

        immediate.draw();
        matrices.pop();
    }

    private void fillGradient(Matrix4f matrix4f, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd, int yEnd, int i, int j, int k)
    {
        float f = (float) (j >> 24 & 255) / 255.0F;
        float g = (float) (j >> 16 & 255) / 255.0F;
        float h = (float) (j >> 8 & 255) / 255.0F;
        float l = (float) (j & 255) / 255.0F;
        float m = (float) (k >> 24 & 255) / 255.0F;
        float n = (float) (k >> 16 & 255) / 255.0F;
        float o = (float) (k >> 8 & 255) / 255.0F;
        float p = (float) (k & 255) / 255.0F;
        bufferBuilder.vertex(matrix4f, (float) xEnd, (float) yStart, (float) i).color(g, h, l, f).next();
        bufferBuilder.vertex(matrix4f, (float) xStart, (float) yStart, (float) i).color(g, h, l, f).next();
        bufferBuilder.vertex(matrix4f, (float) xStart, (float) yEnd, (float) i).color(n, o, p, m).next();
        bufferBuilder.vertex(matrix4f, (float) xEnd, (float) yEnd, (float) i).color(n, o, p, m).next();
    }

    @Override
    public void translateVecToScreenSpace(Vector2i vec)
    {
        Screen currentScreen = this.mc.currentScreen;
        if (currentScreen != null)
        {
            vec.setX((int) (vec.getX() / ((float) currentScreen.width / this.mc.getWindow().getWidth())));
            vec.setY((int) ((currentScreen.height - vec.getY()) / ((float) currentScreen.height / this.mc.getWindow().getHeight())
                    - 1));
        }
    }

    @Override
    public String trimStringToPixelWidth(String str, int pixelWidth)
    {
        return this.mc.textRenderer.trimToWidth(str, pixelWidth);
    }

    @Override
    public float getStringWidth(String str)
    {
        return this.mc.textRenderer.getWidth(str);
    }

    @Override
    public float getStringHeight()
    {
        return this.mc.textRenderer.fontHeight;
    }

    @Override
    public float getStringWidthMultiLine(String str)
    {
        String[] lines = StringUtils.splitPreserveAllTokens(str, '\n');
        return (float) Arrays.stream(lines).mapToDouble(this::getStringWidth).max().orElse(0);
    }

    @Override
    public float getStringHeightMultiLine(String str, float lineSpacing)
    {
        String[] lines = StringUtils.splitPreserveAllTokens(str, '\n');
        return lines.length * getStringHeight() + (lines.length - 1) * lineSpacing;
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
        GlStateManager.blendFuncSeparate(
                SrcFactor.SRC_ALPHA.field_22545,
                DstFactor.ONE_MINUS_SRC_ALPHA.field_22528,
                SrcFactor.ONE.field_22545,
                DstFactor.ZERO.field_22528);
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