package net.voxelindustry.brokkgui.wrapper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3i;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.wrapper.accessible.IAccessibleItemColorMap;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

public class GuiRenderItemHelper
{
    private static final Identifier RES_ITEM_GLINT = new Identifier(
            "textures/misc/enchanted_item_glint.png");

    private       ItemRenderer    itemRender;
    private final MinecraftClient mc;
    private final Random          rand;

    GuiRenderItemHelper()
    {
        this.mc = MinecraftClient.getInstance();
        this.itemRender = this.mc.getItemRenderer();
        this.rand = new Random();
    }

    public void renderItemStack(ItemStack stack, int x, int y, float zLevel, Color color)
    {
        GlStateManager.translatef(0.0F, 0.0F, 32.0F);

        BakedModel model = this.getRenderItem().getModels().getModel(stack);
        model = model.getOverrides().apply(model, stack, null, mc.player);

        RenderSystem.pushMatrix();
        this.mc.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.mc.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float) x, (float) y, 100.0F + zLevel);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);


        MatrixStack matrixStack = new MatrixStack();

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean flag = !model.isSideLit();
        if (flag)
            DiffuseLighting.disableGuiDepthLighting();

        if (!stack.isEmpty())
        {
            matrixStack.push();
            matrixStack.translate(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltin())
            {
                GlStateManager.color4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                GlStateManager.enableRescaleNormal();
                BuiltinModelItemRenderer.INSTANCE.render(stack,
                        ModelTransformation.Mode.GUI,
                        matrixStack,
                        immediate,
                        15728880,
                        OverlayTexture.DEFAULT_UV);
            }
            else
            {
                RenderLayer renderLayer = RenderLayers.getItemLayer(stack, true);
                VertexConsumer vertexBuffer;
                if (stack.getItem() == Items.COMPASS && stack.hasGlint())
                {
                    matrixStack.push();
                    MatrixStack.Entry entry = matrixStack.peek();
                    entry.getModel().multiply(0.5F);

                    vertexBuffer = ItemRenderer.method_30115(immediate, renderLayer, entry);

                    matrixStack.pop();
                }
                else
                    vertexBuffer = ItemRenderer.method_29711(immediate, renderLayer, true, stack.hasGlint());

                this.renderModel(
                        stack,
                        model,
                        15728880,
                        OverlayTexture.DEFAULT_UV,
                        matrixStack,
                        vertexBuffer,
                        color);
            }

            matrixStack.pop();
        }
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (flag)
            DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    private void renderModel(ItemStack stack, BakedModel model, int combinedLight, int combinedOverlay, MatrixStack matrixStack, VertexConsumer buffer, Color color)
    {
        Random random = new Random();

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            this.renderQuads(stack, matrixStack, buffer, model.getQuads(null, direction, random), combinedLight, combinedOverlay, color);
        }

        random.setSeed(42L);
        this.renderQuads(stack, matrixStack, buffer, model.getQuads(null, null, random), combinedLight, combinedOverlay, color);
    }

    public void renderQuads(ItemStack stack, MatrixStack matrixStack, VertexConsumer buffer, List<BakedQuad> quads, int combinedLight, int combinedOverlay, Color color)
    {
        MatrixStack.Entry entry = matrixStack.peek();

        for (BakedQuad bakedQuad : quads)
        {
            float r;
            float g;
            float b;
            float a;

            int i = -1;
            if (!stack.isEmpty() && bakedQuad.hasColor())
            {
                i = ((IAccessibleItemColorMap) mc).getItemColorMap().getColorMultiplier(stack, bakedQuad.getColorIndex());
            }

            r = (float) (i >> 16 & 255) / 255.0F;
            g = (float) (i >> 8 & 255) / 255.0F;
            b = (float) (i & 255) / 255.0F;
            a = 1;

            if (color != Color.WHITE)
            {
                r = Color.mixMultiply(r, color.getRed());
                g = Color.mixMultiply(g, color.getGreen());
                b = Color.mixMultiply(b, color.getBlue());
                a = Color.mixMultiply(a, color.getAlpha());
            }

            quad(buffer, entry, bakedQuad, r, g, b, a, combinedLight, combinedOverlay);
        }
    }

    void quad(VertexConsumer buffer, Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float alpha, int light, int overlay)
    {
        this.quad(buffer, matrixEntry, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{light, light, light, light}, overlay);
    }

    void quad(VertexConsumer buffer, Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay)
    {
        int[] is = quad.getVertexData();
        Vec3i vec3i = quad.getFace().getVector();
        Vector3f vector3f = new Vector3f((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
        Matrix4f matrix4f = matrixEntry.getModel();
        vector3f.transform(matrixEntry.getNormal());
        int j = is.length / 8;
        MemoryStack memoryStack = MemoryStack.stackPush();
        Throwable var17 = null;

        try
        {
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            for (int k = 0; k < j; ++k)
            {
                intBuffer.clear();
                intBuffer.put(is, k * 8, 8);
                float f = byteBuffer.getFloat(0);
                float g = byteBuffer.getFloat(4);
                float h = byteBuffer.getFloat(8);
                float r = brightnesses[k] * red;
                float s = brightnesses[k] * green;
                float t = brightnesses[k] * blue;

                int u = lights[k];
                float v = byteBuffer.getFloat(16);
                float w = byteBuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
                vector4f.transform(matrix4f);
                buffer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), r, s, t, alpha, v, w, overlay, u, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
        } catch (Throwable var38)
        {
            var17 = var38;
            throw var38;
        } finally
        {
            if (memoryStack != null)
            {
                if (var17 != null)
                {
                    try
                    {
                        memoryStack.close();
                    } catch (Throwable var37)
                    {
                        var17.addSuppressed(var37);
                    }
                }
                else
                    memoryStack.close();
            }

        }
    }

    private ItemRenderer getRenderItem()
    {
        if (this.itemRender == null)
            this.itemRender = this.mc.getItemRenderer();
        return this.itemRender;
    }
}
