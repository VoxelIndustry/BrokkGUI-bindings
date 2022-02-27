package net.voxelindustry.brokkgui.demo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.wrapper.impl.BrokkGuiManager;

public class BrokkGuiDemo implements ModInitializer
{
    public static final String MODID = "brokkguidemo";

    public static ScreenHandlerType<ContainerDemo> DEMO_CONTAINER;

    @Override
    public void onInitialize()
    {
        BrokkGuiPlatform.getInstance().enableRenderDebug(true);

        DEMO_CONTAINER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "dummybrokkguidemo"), (window, player) -> new ContainerDemo(DEMO_CONTAINER, window, player));
        ScreenRegistry.register(DEMO_CONTAINER, BrokkGuiDemo::demoScreenFactory);

        Registry.register(Registry.ITEM, new Identifier(MODID, "itembrokkguitest"), new Item(new Settings().group(ItemGroup.MISC))
        {
            @Override
            public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
            {
                {
                    user.setCurrentHand(hand);

                    if (world.isClient() && !user.isSneaking())
                        MinecraftClient.getInstance().setScreen(BrokkGuiManager.getBrokkGuiScreen(new LiteralText("demo"), new GuiDemo()));
                    else if (!world.isClient() && user.isSneaking())
                        user.openHandledScreen(new DummyDemoContainerProvider());
                    return TypedActionResult.fail(user.getStackInHand(hand));
                }
            }
        });
    }

    private static HandledScreen<ContainerDemo> demoScreenFactory(ContainerDemo container, PlayerInventory inventory, Text title)
    {
        return BrokkGuiManager.getBrokkGuiContainer(title, new GuiContainerDemo(container));
    }

    private static class DummyDemoContainerProvider implements NamedScreenHandlerFactory
    {
        @Override
        public Text getDisplayName()
        {
            return new LiteralText("demo");
        }

        @Override
        public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity)
        {
            return new ContainerDemo(DEMO_CONTAINER, i, playerInventory);
        }
    }
}