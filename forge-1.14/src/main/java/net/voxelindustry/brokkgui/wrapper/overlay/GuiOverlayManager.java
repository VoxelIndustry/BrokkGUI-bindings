package net.voxelindustry.brokkgui.wrapper.overlay;

import net.minecraft.client.gui.screen.Screen;
import net.voxelindustry.brokkgui.gui.BrokkGuiScreen;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GuiOverlayManager
{
    private static List<Pair<Predicate<Screen>, Supplier<BrokkGuiScreen>>> guiOverlayByConditions = new ArrayList<>();

    private static List<GuiOverlayImpl> activeOverlays = new ArrayList<>();

    public static void addGuiOverlay(Supplier<BrokkGuiScreen> brokkGuiScreen, Predicate<Screen> overlayCondition)
    {
        guiOverlayByConditions.add(Pair.of(overlayCondition, brokkGuiScreen));
    }

    public static void addGuiOverlay(Supplier<BrokkGuiScreen> brokkGuiScreen, Class<? extends Screen> guiClassToOverlay)
    {
        addGuiOverlay(brokkGuiScreen, guiClassToOverlay::isInstance);
    }

    static void triggerOverlays(Screen openedScreen)
    {
        if (openedScreen == null)
        {
            activeOverlays.forEach(overlay -> overlay.getGui().onClose());
            activeOverlays.clear();
            return;
        }

        activeOverlays.removeIf(overlay -> !overlay.getOverlayCondition().test(openedScreen));

        guiOverlayByConditions.stream()
                .filter(overlayByCondition -> overlayByCondition.getLeft().test(openedScreen))
                .map(overlayByCondition -> new GuiOverlayImpl("", overlayByCondition.getValue().get(), overlayByCondition.getKey()))
                .forEach(activeOverlays::add);
    }

    static List<GuiOverlayImpl> getActiveOverlays()
    {
        return activeOverlays;
    }

    static void removeActiveOverlay(GuiOverlayImpl overlay)
    {
        activeOverlays.remove(overlay);
    }
}
