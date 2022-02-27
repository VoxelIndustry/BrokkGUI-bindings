package net.voxelindustry.brokkgui.wrapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.voxelindustry.brokkgui.internal.IKeyboardUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyboardUtil implements IKeyboardUtil
{
    private final Map<String, Integer> keyMap;

    public KeyboardUtil()
    {
        this.keyMap = new HashMap<>();

        this.keyMap.put("UP", GLFW.GLFW_KEY_UP);
        this.keyMap.put("DOWN", GLFW.GLFW_KEY_DOWN);
        this.keyMap.put("LEFT", GLFW.GLFW_KEY_LEFT);
        this.keyMap.put("RIGHT", GLFW.GLFW_KEY_RIGHT);

        this.keyMap.put("PAGE_UP", GLFW.GLFW_KEY_PAGE_UP);
        this.keyMap.put("PAGE_DOWN", GLFW.GLFW_KEY_PAGE_DOWN);
        this.keyMap.put("HOME", GLFW.GLFW_KEY_HOME);
        this.keyMap.put("END", GLFW.GLFW_KEY_END);


        this.keyMap.put("DELETE", GLFW.GLFW_KEY_DELETE);
        this.keyMap.put("BACK", GLFW.GLFW_KEY_BACKSPACE);

        // Add all alphabet letters
        for (var letter = 'A'; letter <= 'Z'; letter++)
            this.keyMap.put(String.valueOf(letter), GLFW.GLFW_KEY_A + (letter - 'A'));

        this.keyMap.put("RETURN", GLFW.GLFW_KEY_ENTER);
        this.keyMap.put("NUMPADENTER", GLFW.GLFW_KEY_KP_ENTER);
    }

    @Override
    public boolean isCtrlKeyDown()
    {
        return Screen.hasControlDown();
    }

    @Override
    public boolean isShiftKeyDown()
    {
        return Screen.hasShiftDown();
    }

    @Override
    public String getClipboardString()
    {
        return MinecraftClient.getInstance().keyboard.getClipboard();
    }

    @Override
    public void setClipboardString(String text)
    {
        MinecraftClient.getInstance().keyboard.setClipboard(text);
    }

    @Override
    public int getKeyCode(String keyName)
    {
        if (this.keyMap.containsKey(keyName))
            return keyMap.get(keyName);
        throw new IllegalArgumentException("Key [" + keyName + "] is not mapped in the binding.");
    }

    @Override
    public String getKeyName(int keyCode)
    {
        throw new UnsupportedOperationException("GetKeyName is invalid with MC1.14");
    }
}