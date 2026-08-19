package com.potatohive.client.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import static com.potatohive.client.utils.RandomDelay.*;

public class FarmTask {

    private boolean running = false;
    private Thread worker;

    public void start() {
        if (running) return;

        running = true;

        worker = new Thread(() -> {
            while (running) {
                try {
                    Minecraft mc = Minecraft.getMinecraft();

                    if (mc.thePlayer == null) {
                        sleep(1000);
                        continue;
                    }

                    // Walk 3 blocks forward
                    for (int i = 0; i < 3; i++) {
                        mc.thePlayer.moveForward = 0.3f;
                        sleep(200 + gaussian(100));
                    }

                    mc.thePlayer.moveForward = 0;

                    // Break crop in front
                    BlockPos front = mc.thePlayer
                            .getPosition()
                            .offset(mc.thePlayer.getHorizontalFacing(), 1);

                    mc.playerController.clickBlock(front, EnumFacing.UP);

                    sleep(gaussian(300));

                    // Place seed
                    int oldSlot = mc.thePlayer.inventory.currentItem;

                    mc.thePlayer.inventory.currentItem = 0;

                    mc.playerController.rightClickBlock(
                            mc.thePlayer,
                            mc.theWorld,
                            front,
                            EnumFacing.UP,
                            new float[]{0.5f, 0.5f, 0.5f}
                    );

                    mc.thePlayer.inventory.currentItem = oldSlot;

                    sleep(500 + gaussian(200));

                } catch (Exception ignored) {
                }
            }
        });

        worker.setDaemon(true);
        worker.start();
    }

    public void stop() {
        running = false;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
        }
    }
}
