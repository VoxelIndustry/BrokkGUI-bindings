package net.voxelindustry.brokkgui.wrapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.voxelindustry.brokkgui.paint.Color;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GuiRenderItemHelper
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(
            "textures/misc/enchanted_item_glint.png");

    private       ItemRenderer itemRender;
    private final Minecraft    mc;
    private final Random       rand;

    GuiRenderItemHelper()
    {
        this.mc = Minecraft.getInstance();
        this.itemRender = this.mc.getItemRenderer();
        this.rand = new Random();
    }

    public void renderItemStack(ItemStack stack, int x, int y, float zLevel, Color color)
    {

        GlStateManager.translatef(0.0F, 0.0F, 32.0F);

        IBakedModel model = this.getRenderItem().getItemModelMesher().getItemModel(stack);
        model = model.getOverrides().getModelWithOverrides(model, stack, null, Minecraft.getInstance().player);

        RenderSystem.pushMatrix();
        this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float) x, (float) y, 100.0F + zLevel);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);


        MatrixStack matrixStack = new MatrixStack();

        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(
                matrixStack,
                model,
                ItemCameraTransforms.TransformType.GUI,
                false);

        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        boolean flag = !model.func_230044_c_();
        if (flag)
            RenderHelper.setupGuiFlatDiffuseLighting();

        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer())
            {
                GlStateManager.color4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                GlStateManager.enableRescaleNormal();
                ItemStackTileEntityRenderer.instance.render(
                        stack,
                        matrixStack,
                        Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(),
                        15728880,
                        OverlayTexture.DEFAULT_LIGHT);
            }
            else
            {
                RenderType rendertype = RenderTypeLookup.getRenderType(stack);
                if (Objects.equals(rendertype, Atlases.getTranslucentBlockType()))
                    rendertype = Atlases.getTranslucentCullBlockType();

                this.renderModel(model,
                        15728880,
                        OverlayTexture.DEFAULT_LIGHT,
                        matrixStack,
                        ItemRenderer.getBuffer(bufferSource, rendertype, true, stack.hasEffect()),
                        color);
            }

            GlStateManager.popMatrix();
        }
        bufferSource.finish();
        RenderSystem.enableDepthTest();
        if (flag)
            RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    private void renderModel(IBakedModel model, int combinedLight, int combinedOverlay, MatrixStack matrixStack, IVertexBuilder buffer, Color color)
    {
        Random random = new Random();

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            this.renderQuads(matrixStack, buffer, model.getQuads(null, direction, random), combinedLight, combinedOverlay, color);
        }

        random.setSeed(42L);
        this.renderQuads(matrixStack, buffer, model.getQuads(null, null, random), combinedLight, combinedOverlay, color);
    }

    public void renderQuads(MatrixStack matrixStack, IVertexBuilder buffer, List<BakedQuad> quads, int combinedLight, int combinedOverlay, Color color)
    {
        MatrixStack.Entry lastMatrixStack = matrixStack.getLast();

        for (BakedQuad bakedquad : quads)
            buffer.addVertexData(lastMatrixStack, bakedquad, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), combinedLight, combinedOverlay, true);

    }

    private ItemRenderer getRenderItem()
    {
        if (this.itemRender == null)
            this.itemRender = this.mc.getItemRenderer();
        return this.itemRender;
    }
}
