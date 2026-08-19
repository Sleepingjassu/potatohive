package com.potatohive.client.gui;

import com.potatohive.client.storage.AccountStore;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiAddAccount extends GuiScreen {
    private GuiTextField nameField;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        nameField = new GuiTextField(0, fontRendererObj, width/2 - 75, height/2 - 20, 150, 20);
        nameField.setFocused(true);
        nameField.setMaxStringLength(16);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Enter Offline Username", width/2, height/2 - 50, 0xFFFFFF);
        nameField.drawTextBox();
        drawCenteredString(fontRendererObj, "Press ENTER to add, ESC to cancel", width/2, height/2 + 20, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC
            mc.displayGuiScreen(null);
            return;
        }
        if (keyCode == 28) { // ENTER
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                AccountStore.getInstance().addAccount(name);
                mc.displayGuiScreen(null);
            }
            return;
        }
        nameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        nameField.updateCursorCounter();
    }
}
