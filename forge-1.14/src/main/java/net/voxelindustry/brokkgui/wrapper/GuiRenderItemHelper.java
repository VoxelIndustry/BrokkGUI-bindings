package net.voxelindustry.brokkgui.wrapper;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.voxelindustry.brokkgui.paint.Color;

import java.util.List;
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

        GlStateManager.pushMatrix();
        this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.setupGuiTransform(x, y, model.isGui3d(), (int) zLevel + 50);
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model,
                ItemCameraTransforms.TransformType.GUI, false);

        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer())
            {
                GlStateManager.color4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                GlStateManager.enableRescaleNormal();
                ItemStackTileEntityRenderer.instance.renderByItem(stack);
            }
            else
            {
                this.renderModel(model, color.toRGBAInt(), stack);

                if (stack.hasEffect())
                    this.renderEffect(model);
            }

            GlStateManager.popMatrix();
        }
        GlStateManager.disableAlphaTest();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d, int zLevel)
    {
        GlStateManager.translatef((float) xPosition, (float) yPosition, 100.0F + zLevel);
        GlStateManager.translatef(8.0F, 8.0F, 0.0F);
        GlStateManager.scalef(1.0F, -1.0F, 1.0F);
        GlStateManager.scalef(16.0F, 16.0F, 16.0F);

        if (isGui3d)
            GlStateManager.enableLighting();
        else
            GlStateManager.disableLighting();
    }

    private void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder = tessellator.getBuffer();
        BufferBuilder.begin(7, DefaultVertexFormats.ITEM);

        for (Direction facing : Direction.values())
            this.renderQuads(BufferBuilder, model.getQuads(null, facing, rand), color, stack);

        this.renderQuads(BufferBuilder, model.getQuads(null, null, rand), color, stack);
        tessellator.draw();
    }

    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack)
    {
        boolean flag = color == -1 && !stack.isEmpty();
        int i = 0;

        for (int j = quads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = quads.get(i);
            int k = color;

            if (flag && bakedquad.hasTintIndex())
            {
                k = this.mc.getItemColors().getColor(stack, bakedquad.getTintIndex());

                k = k | -16777216;
            }
            LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }

    private void renderEffect(IBakedModel model)
    {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        this.mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
        float f = (float) (Util.milliTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translatef(f, 0.0F, 0.0F);
        GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
        this.renderModel(model, -8372020, ItemStack.EMPTY);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
        float f1 = (float) (Util.milliTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translatef(-f1, 0.0F, 0.0F);
        GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
        this.renderModel(model, -8372020, ItemStack.EMPTY);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }

    private ItemRenderer getRenderItem()
    {
        if (this.itemRender == null)
            this.itemRender = this.mc.getItemRenderer();
        return this.itemRender;
    }
}
