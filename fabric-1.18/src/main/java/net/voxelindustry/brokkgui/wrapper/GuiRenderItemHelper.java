package net.voxelindustry.brokkgui.wrapper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.voxelindustry.brokkcolor.Color;

public class GuiRenderItemHelper
{
    private static final Identifier RES_ITEM_GLINT = new Identifier(
            "textures/misc/enchanted_item_glint.png");

    private       ItemRenderer    itemRender;
    private final MinecraftClient mc;

    public GuiRenderItemHelper()
    {
        this.mc = MinecraftClient.getInstance();
        this.itemRender = this.mc.getItemRenderer();
    }

    public void renderItemStack(ItemStack stack, int x, int y, float zLevel, int depth, Color color)
    {
        var model = getRenderItem().getModel(stack, null, mc.player, 0);
        zLevel = model.hasDepth() ? zLevel + 50.0f + (float) depth : zLevel + 50.0f;

        this.mc.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 0, 0, 1.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0f + zLevel);
        matrixStack.translate(8.0, 8.0, 0.0);
        matrixStack.scale(1.0f, -1.0f, 1.0f);
        matrixStack.scale(16.0f, 16.0f, 16.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        if (!model.isSideLit())
            DiffuseLighting.disableGuiDepthLighting();

        getRenderItem().renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();

        if (!model.isSideLit())
            DiffuseLighting.enableGuiDepthLighting();

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    private ItemRenderer getRenderItem()
    {
        if (this.itemRender == null)
            this.itemRender = this.mc.getItemRenderer();
        return this.itemRender;
    }
}
