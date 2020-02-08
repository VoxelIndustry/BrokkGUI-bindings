package net.voxelindustry.brokkgui.demo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.wrapper.impl.BrokkGuiManager;

import javax.annotation.Nullable;

@Mod(BrokkGuiDemo.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BrokkGuiDemo
{
    public static final String MODID   = "brokkguidemo";
    public static final String MODNAME = "BrokkGui Demo";
    public static final String VERSION = "1.0";

    @ObjectHolder("dummybrokkguidemo")
    public static ContainerType<ContainerDemo> DEMO_CONTAINER;

    public BrokkGuiDemo()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private static ContainerScreen<ContainerDemo> create(ContainerDemo container, PlayerInventory inventory, ITextComponent title)
    {
        return BrokkGuiManager.getBrokkGuiContainer(title, new GuiContainerDemo(container));
    }

    private void commonSetup(FMLCommonSetupEvent e)
    {
        BrokkGuiPlatform.getInstance().enableRenderDebug(true);
        MinecraftForge.EVENT_BUS.register(this);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> this::screenInit);
    }

    private void screenInit()
    {
        ScreenManager.registerFactory(DEMO_CONTAINER, BrokkGuiDemo::create);
    }

    @SubscribeEvent
    public static void onContainerRegister(RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().register(new ContainerType<>((window, player) -> new ContainerDemo(DEMO_CONTAINER, window, player)).setRegistryName("dummybrokkguidemo"));
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new Item(new Item.Properties().group(ItemGroup.MISC))
        {
            @Override
            public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand)
            {
                player.setActiveHand(hand);

                if (world.isRemote && !player.isSneaking())
                    Minecraft.getInstance().displayGuiScreen(BrokkGuiManager.getBrokkGuiScreen(new StringTextComponent("demo"), new GuiDemo()));
                else if (!world.isRemote && player.isSneaking())
                  NetworkHooks.openGui((ServerPlayerEntity) player, new DummyDemoContainerProvider());
                return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
            }
        }.setRegistryName("itembrokkguitest"));
    }

    private static class DummyDemoContainerProvider implements INamedContainerProvider
    {
        @Override
        public ITextComponent getDisplayName()
        {
            return new StringTextComponent("demo");
        }

        @Nullable
        @Override
        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity)
        {
            return new ContainerDemo(DEMO_CONTAINER, i, playerInventory);
        }
    }
}