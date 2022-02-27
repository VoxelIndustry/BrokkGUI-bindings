package net.voxelindustry.brokkgui.wrapper.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.voxelindustry.brokkcolor.Color;
import net.voxelindustry.brokkgui.data.RectCorner;
import net.voxelindustry.brokkgui.internal.IRenderCommandReceiver;
import net.voxelindustry.brokkgui.internal.ITextHelper;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.sprite.SpriteRotation;
import net.voxelindustry.brokkgui.sprite.Texture;
import net.voxelindustry.brokkgui.text.TextSettings;
import net.voxelindustry.brokkgui.wrapper.GuiRenderItemHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static org.lwjgl.opengl.GL11.*;

public class RenderCommandDispatcher implements IRenderCommandReceiver, ItemStackRenderCommandReceiver, TooltipRenderCommandReceiver
{
    private final BufferBuilder       buffer;
    private final ITextHelper         textHelper;
    private final GuiRenderItemHelper itemHelper;

    private MatrixStack matrices;

    private final Deque<Box2D> masksStack = new ArrayDeque<>();

    public RenderCommandDispatcher(ITextHelper textHelper)
    {
        this.textHelper = textHelper;
        this.itemHelper = new GuiRenderItemHelper();
        buffer = Tessellator.getInstance().getBuffer();
    }

    public void setMatrices(MatrixStack matrices)
    {
        this.matrices = matrices;
    }

    @Override
    public void beginMatrix()
    {
        matrices.push();
    }

    @Override
    public void endMatrix()
    {
        matrices.pop();
    }

    @Override
    public void translateMatrix(float posX, float posY, float posZ)
    {
        matrices.translate(posX, posY, posZ);
    }

    @Override
    public void rotateMatrix(float angle, float x, float y, float z)
    {
        matrices.multiply(new Quaternion(new Vec3f(x, y, z), angle, true));
    }

    @Override
    public void scaleMatrix(float scaleX, float scaleY, float scaleZ)
    {
        matrices.scale(scaleX, scaleY, scaleZ);
    }

    @Override
    public void drawTexturedRect(float xStart, float yStart, float uMin, float vMin, float uMax, float vMax, float width, float height, float zLevel, SpriteRotation rotation, RenderPass pass)
    {
        var matrix = matrices.peek().getPositionMatrix();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, xStart, yStart + height, zLevel + pass.getPriority() * 0.25F)
                .texture(uMin, vMax)
                .next();
        buffer.vertex(matrix, xStart + width, yStart + height, zLevel + pass.getPriority() * 0.25F)
                .texture(uMax, vMax)
                .next();
        buffer.vertex(matrix, xStart + width, yStart, zLevel + pass.getPriority() * 0.25F)
                .texture(uMax, vMin)
                .next();
        buffer.vertex(matrix, xStart, yStart, zLevel + pass.getPriority() * 0.25F)
                .texture(uMin, vMin)
                .next();
        buffer.end();
        BufferRenderer.draw(buffer);
    }

    @Override
    public void drawTexturedRectWithColor(float xStart, float yStart, float uMin, float vMin, float uMax, float vMax, float width, float height, float zLevel, SpriteRotation rotation, RenderPass pass, Color color)
    {
        if (color.getAlpha() == 0)
        {
            drawTexturedRect(xStart, yStart, uMin, vMin, uMax, vMax, width, height, zLevel, rotation, pass);
            return;
        }

        var matrix = matrices.peek().getPositionMatrix();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, xStart, yStart + height, zLevel + pass.getPriority() * 0.25F)
                .texture(uMin, vMax)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, xStart + width, yStart + height, zLevel + pass.getPriority() * 0.25F)
                .texture(uMax, vMax)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, xStart + width, yStart, zLevel + pass.getPriority() * 0.25F)
                .texture(uMax, vMin)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, xStart, yStart, zLevel + pass.getPriority() * 0.25F)
                .texture(uMin, vMin)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();

        buffer.end();
        BufferRenderer.draw(buffer);
    }

    @Override
    public void drawColoredRect(float xStart, float yStart, float width, float height, float zLevel, Color color, RenderPass pass)
    {
        var matrix = matrices.peek().getPositionMatrix();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, xStart, yStart + height, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, xStart + width, yStart + height, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, xStart + width, yStart, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, xStart, yStart, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.end();
        BufferRenderer.draw(buffer);
    }

    @Override
    public void drawColoredQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float zLevel, Color color, RenderPass pass)
    {
        var matrix = matrices.peek().getPositionMatrix();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x1, y1, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, x2, y2, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, x3, y3, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.vertex(matrix, x4, y4, zLevel + pass.getPriority() * 0.25F)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .next();
        buffer.end();
        BufferRenderer.draw(buffer);
    }

    @Override
    public void drawColoredTriangles(float zLevel, Color color, RenderPass pass, float... vertices)
    {

    }

    @Override
    public void drawTexturedCircle(float xStart, float yStart, float uMin, float vMin, float uMax, float vMax, float radius, float zLevel, RenderPass pass)
    {
        // TODO : Shape drawing
    }

    @Override
    public void drawTexturedCircle(float xStart, float yStart, float uMin, float vMin, float radius, float zLevel, RenderPass pass)
    {
        // TODO : Shape drawing
    }

    @Override
    public void drawColoredEmptyCircle(float startX, float startY, float radius, float zLevel, Color color, float thin, RenderPass pass)
    {
        // TODO : Shape drawing
    }

    @Override
    public void drawColoredCircle(float startX, float startY, float radius, float zLevel, Color color, RenderPass pass)
    {
        // TODO : Shape drawing
    }

    @Override
    public void drawColoredLine(float startX, float startY, float endX, float endY, float lineWeight, float zLevel, Color color, RenderPass pass)
    {
        // TODO : Shape drawing
    }

    @Override
    public void drawColoredArc(float centerX, float centerY, float radius, float zLevel, Color color, RectCorner corner, RenderPass pass)
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
                case TOP_LEFT ->
                {
                    drawColoredRect(-x + centerX, -y + centerY, 1, 1, zLevel, color, pass);
                    if (x != y)
                        drawColoredRect(-y + centerX, -x + centerY, 1, 1, zLevel, color, pass);
                }
                case TOP_RIGHT ->
                {
                    drawColoredRect(x + centerX, -y + centerY, 1, 1, zLevel, color, pass);
                    if (x != y)
                        drawColoredRect(y + centerX, -x + centerY, 1, 1, zLevel, color, pass);
                }
                case BOTTOM_LEFT ->
                {
                    drawColoredRect(-x + centerX, y + centerY, 1, 1, zLevel, color, pass);
                    if (x != y)
                        drawColoredRect(-y + centerX, x + centerY, 1, 1, zLevel, color, pass);
                }
                case BOTTOM_RIGHT ->
                {
                    drawColoredRect(x + centerX, y + centerY, 1, 1, zLevel, color, pass);
                    if (x != y)
                        drawColoredRect(y + centerX, x + centerY, 1, 1, zLevel, color, pass);
                }
            }
        }
    }

    @Override
    public void startAlphaMask(double opacity)
    {
        // TODO : Opacity mask
    }

    @Override
    public void closeAlphaMask()
    {
        // TODO : Opacity mask
    }

    @Override
    public void beginScissor()
    {
        glEnable(GL_SCISSOR_TEST);
    }

    @Override
    public void scissorBox(float xStart, float yStart, float xEnd, float yEnd)
    {
        var minecraftWindow = MinecraftClient.getInstance().getWindow();
        double factor = minecraftWindow.getScaleFactor();

        // Needed to prevent pixel bleeding when scissor is active
        // This must be properly checked after a binding update
        // TODO : Ensure there are only 1 - 2 - 3 and 4 scaleFactor for MC GUI
        int topOffset = 0;
        int bottomOffset = 0;

        int heightRatio = (int) ((double) minecraftWindow.getFramebufferHeight() / factor);
        boolean doesHeightNeedOffset = (double) minecraftWindow.getFramebufferHeight() / factor > (double) heightRatio;

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
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen != null)
        {
            int bottomY = (int) (currentScreen.height - yEnd);
            GL11.glScissor((int) (xStart * factor), (int) (bottomY * factor) + bottomOffset, (int) (width * factor), (int) (height * factor) + topOffset);
        }
    }

    @Override
    public void endScissor()
    {
        glDisable(GL_SCISSOR_TEST);
    }

    @Override
    public void bindTexture(Texture texture)
    {
        RenderSystem.setShaderTexture(0, new Identifier(texture.getResource()));
    }

    @Override
    public void pushMask(float leftPos, float topPos, float rightPos, float bottomPos)
    {
        if (masksStack.isEmpty())
        {
            beginScissor();
            masksStack.addFirst(new Box2D((int) leftPos, (int) topPos, (int) rightPos, (int) bottomPos));
            scissorBox(leftPos, topPos, rightPos, bottomPos);
            return;
        }

        var previousMask = masksStack.peekFirst();

        int newLeft = max((int) leftPos, previousMask.left());
        int newTop = max((int) topPos, previousMask.top());
        int newRight = min((int) rightPos, previousMask.right());
        int newBottom = min((int) bottomPos, previousMask.bottom());

        masksStack.addFirst(new Box2D(newLeft, newTop, newRight, newBottom));
        scissorBox(newLeft, newTop, newRight, newBottom);
    }

    @Override
    public void popMask()
    {
        if (masksStack.isEmpty())
            return;

        masksStack.removeFirst();

        if (masksStack.isEmpty())
            endScissor();
    }

    @Override
    public void drawString(String text, float x, float y, float zLevel, RenderPass pass, TextSettings settings)
    {
        matrices.push();

        matrices.translate(0, 0, zLevel);
        if (settings.shadowColor() != null && settings.shadowColor().getAlpha() != 0)
            MinecraftClient.getInstance().textRenderer.draw(matrices, text, x + 1, y + 1, settings.shadowColor().toRGBAInt());

        MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, settings.textColor().toRGBAInt());

        matrices.pop();
    }

    @Override
    public void drawStringMultiline(String text, float x, float y, float zLevel, RenderPass pass, TextSettings settings)
    {
        var lines = text.split("\n");
        var lineHeight = textHelper.getStringHeight(settings);

        for (int index = 0; index < lines.length; index++)
            drawString(lines[index], x, y + (lineHeight * settings.lineSpacingMultiplier()) * index, zLevel, pass, settings);
    }

    /////////////////////////
    // ITEMSTACK RENDERING //
    /////////////////////////

    @Override
    public void drawItemStack(float startX,
                              float startY,
                              float width,
                              float height,
                              float zLevel,
                              ItemStack stack,
                              String displayString,
                              Color color)
    {
        if (stack.isEmpty())
            return;

        float scaleX = width / 18;
        float scaleY = height / 18;

        var matrixStack = RenderSystem.getModelViewStack();
        matrixStack.translate(0.0, 0.0, 32.0);
        matrixStack.translate(-(startX * (scaleX - 1)) - 8 * scaleX, -(startY * (scaleY - 1)) - 8 * scaleY, 0);
        matrixStack.scale(scaleX, scaleY, 1);

        matrixStack.translate(0.0F, 0.0F, 32.0F);
        RenderSystem.applyModelViewMatrix();

        this.itemHelper.renderItemStack(stack, (int) startX, (int) startY, zLevel, 0, color);
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, stack, (int) startX, (int) startY, displayString);
    }

    @Override
    public void drawItemStackTooltip(int mouseX,
                                     int mouseY,
                                     float windowWidth,
                                     float windowHeight,
                                     ItemStack stack,
                                     Consumer<List<Text>> tooltipModifier)
    {
        var lines = stack.getTooltip(MinecraftClient.getInstance().player, MinecraftClient.getInstance().options.advancedItemTooltips ?
                TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);

        if (tooltipModifier != null)
            tooltipModifier.accept(lines);

        drawHoveringTooltip(mouseX + 12,
                mouseY - 12,
                windowWidth,
                windowHeight,
                lines.stream().map(Text::asOrderedText).map(TooltipComponent::of).toList());
    }

    @Override
    public void drawHoveringTooltip(int posX,
                                    int posY,
                                    float windowWidth,
                                    float windowHeight,
                                    List<TooltipComponent> components)
    {
        int s;
        int k;
        if (components.isEmpty())
            return;

        int i = 0;
        int j = components.size() == 1 ? -2 : 0;
        for (var tooltipComponent : components)
        {
            k = tooltipComponent.getWidth(MinecraftClient.getInstance().textRenderer);
            if (k > i)
                i = k;
            j += tooltipComponent.getHeight();
        }
        int l = posX + 12;
        int tooltipComponent = posY - 12;
        k = i;
        int m = j;
        if (l + i > windowWidth)
        {
            l -= 28 + i;
        }
        if (tooltipComponent + m + 6 > windowHeight)
        {
            tooltipComponent = (int) (windowHeight - m - 6);
        }
        matrices.push();

        float previousZLevel = MinecraftClient.getInstance().getItemRenderer().zOffset;
        MinecraftClient.getInstance().getItemRenderer().zOffset = 400.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 4, l + k + 3, tooltipComponent - 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent + m + 3, l + k + 3, tooltipComponent + m + 4, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 3, l + k + 3, tooltipComponent + m + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, l - 4, tooltipComponent - 3, l - 3, tooltipComponent + m + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, l + k + 3, tooltipComponent - 3, l + k + 4, tooltipComponent + m + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 3 + 1, l - 3 + 1, tooltipComponent + m + 3 - 1, 400, 0x505000FF, 1344798847);
        Screen.fillGradient(matrix4f, bufferBuilder, l + k + 2, tooltipComponent - 3 + 1, l + k + 3, tooltipComponent + m + 3 - 1, 400, 0x505000FF, 1344798847);
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 3, l + k + 3, tooltipComponent - 3 + 1, 400, 0x505000FF, 0x505000FF);
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent + m + 2, l + k + 3, tooltipComponent + m + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);
        int r = tooltipComponent;
        for (s = 0; s < components.size(); ++s)
        {
            var currentComponent = components.get(s);
            currentComponent.drawText(MinecraftClient.getInstance().textRenderer, l, r, matrix4f, immediate);
            r += currentComponent.getHeight() + (s == 0 ? 2 : 0);
        }
        immediate.draw();
        matrices.pop();
        r = tooltipComponent;
        for (s = 0; s < components.size(); ++s)
        {
            var currentComponent = components.get(s);
            currentComponent.drawItems(MinecraftClient.getInstance().textRenderer, l, r, matrices, MinecraftClient.getInstance().getItemRenderer(), 400);
            r += currentComponent.getHeight() + (s == 0 ? 2 : 0);
        }
        MinecraftClient.getInstance().getItemRenderer().zOffset = previousZLevel;
    }
}
