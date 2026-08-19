package com.potatohive.client.gui;

import com.potatohive.client.automation.*;
import com.potatohive.client.storage.AccountStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class HiveOverlay {
    private boolean expanded = false;
    private int scrollOffset = 0;
    private int panelX, panelY, panelW, panelH;
    private final ConcurrentHashMap<String, Boolean> taskToggles = new ConcurrentHashMap<>();
    private FarmTask farmTask;
    private MineTask mineTask;
    private CombatTask combatTask;
    private SnipeTask snipeTask;

    public HiveOverlay() {
        taskToggles.put("Farm", false);
        taskToggles.put("Mine", false);
        taskToggles.put("Combat", false);
        taskToggles.put("Snipe", false);
        farmTask = new FarmTask();
        mineTask = new MineTask();
        combatTask = new CombatTask();
        snipeTask = new SnipeTask();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();

        panelX = width - 200;
        panelY = 60;
        panelW = 190;
        panelH = expanded ? 300 : 40;

        // Background
        drawRect(panelX, panelY, panelX + panelW, panelY + panelH, new Color(0, 0, 0, 200).getRGB());
        drawRectBorder(panelX, panelY, panelX + panelW, panelY + panelH, 2, new Color(0, 255, 200).getRGB());

        Minecraft mc = Minecraft.getMinecraft();
        // Title / toggle button
        mc.fontRendererObj.drawString("🍟 PotatoHive", panelX + 8, panelY + 4, 0x00FFCC);
        mc.fontRendererObj.drawString(expanded ? "▼" : "▲", panelX + panelW - 20, panelY + 4, 0xFFFFFF);

        if (expanded) {
            int y = panelY + 24;
            // Account list
            mc.fontRendererObj.drawString("§7[ Accounts ]", panelX + 8, y, 0xAAAAAA);
            y += 12;
            int idx = 0;
            for (AccountStore.Account acc : AccountStore.getInstance().getAccounts()) {
                String label = (idx == AccountStore.getInstance().getActiveIndex() ? "§a▶ " : "§7  ") + acc.name;
                mc.fontRendererObj.drawString(label, panelX + 8, y, 0xFFFFFF);
                y += 14;
                idx++;
            }
            // Add button
            mc.fontRendererObj.drawString("§a[ + Add Account ]", panelX + 8, y, 0x55FF55);
            y += 18;

            // Task toggles
            mc.fontRendererObj.drawString("§7[ Tasks ]", panelX + 8, y, 0xAAAAAA);
            y += 12;
            for (String task : new String[]{"Farm", "Mine", "Combat", "Snipe"}) {
                boolean on = taskToggles.getOrDefault(task, false);
                String color = on ? "§a" : "§c";
                mc.fontRendererObj.drawString(color + (on ? "✔" : "✖") + " " + task, panelX + 8, y, on ? 0x55FF55 : 0xFF5555);
                y += 14;
            }

            // Status
            mc.fontRendererObj.drawString("§7Active: " + (AccountStore.getInstance().getActive() != null ? AccountStore.getInstance().getActive().name : "none"), panelX + 8, y + 4, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        // Check for touch/mouse clicks on GUI
        if (Mouse.isButtonDown(0)) {
            int x = Mouse.getX() / 2; // approximate scaling – adjust for Pojav
            int y = Minecraft.getMinecraft().displayHeight - Mouse.getY() / 2;
            handleClick(x, y);
        }
    }

    private void handleClick(int x, int y) {
        if (x < panelX || x > panelX + panelW || y < panelY || y > panelY + panelH) return;
        int relX = x - panelX;
        int relY = y - panelY;

        // Title click toggles expand
        if (relY < 20) {
            expanded = !expanded;
            return;
        }
        if (!expanded) return;

        int yPos = 24 + 12; // start of account list
        int idx = 0;
        for (AccountStore.Account acc : AccountStore.getInstance().getAccounts()) {
            if (relY >= yPos && relY < yPos + 14) {
                AccountStore.getInstance().setActive(idx);
                return;
            }
            yPos += 14;
            idx++;
        }
        // Add button
        if (relY >= yPos && relY < yPos + 14) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiAddAccount());
            return;
        }
        yPos += 18;

        // Task toggles
        String[] tasks = {"Farm", "Mine", "Combat", "Snipe"};
        for (String task : tasks) {
            if (relY >= yPos && relY < yPos + 14) {
                boolean current = taskToggles.getOrDefault(task, false);
                taskToggles.put(task, !current);
                // Start/stop tasks
                if (!current) {
                    switch (task) {
                        case "Farm": farmTask.start(); break;
                        case "Mine": mineTask.start(); break;
                        case "Combat": combatTask.start(); break;
                        case "Snipe": snipeTask.start(); break;
                    }
                } else {
                    switch (task) {
                        case "Farm": farmTask.stop(); break;
                        case "Mine": mineTask.stop(); break;
                        case "Combat": combatTask.stop(); break;
                        case "Snipe": snipeTask.stop(); break;
                    }
                }
                return;
            }
            yPos += 14;
        }
    }

    private void drawRect(int l, int t, int r, int b, int color) {
        net.minecraft.client.gui.Gui.drawRect(l, t, r, b, color);
    }
    private void drawRectBorder(int l, int t, int r, int b, int thick, int color) {
        net.minecraft.client.gui.Gui.drawRect(l, t, r, t+thick, color);
        net.minecraft.client.gui.Gui.drawRect(l, b-thick, r, b, color);
        net.minecraft.client.gui.Gui.drawRect(l, t, l+thick, b, color);
        net.minecraft.client.gui.Gui.drawRect(r-thick, t, r, b, color);
    }
}
