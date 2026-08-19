package com.potatohive.client.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import static com.potatohive.client.utils.RandomDelay.*;

public class MineTask {

    private boolean running = false;

    public void start() {
        if (running) return;

        running = true;

        new Thread(() -> {
            while (running) {
                try {
                    Minecraft mc = Minecraft.getMinecraft();

                    if (mc.thePlayer == null) {
                        sleep(1000);
                        continue;
                    }

                    // Simple branch: mine 2 blocks ahead, then 2 left, repeat
                    for (int side = 0; side < 4; side++) {
                        for (int fwd = 0; fwd < 3; fwd++) {
                            mc.thePlayer.moveForward = 0.2f;

                            sleep(300 + gaussian(100));

                            // Mine block at head height
                            BlockPos target = mc.thePlayer
                                    .getPosition()
                                    .offset(mc.thePlayer.getHorizontalFacing(), 1)
                                    .up(1);

                            mc.playerController.clickBlock(target, EnumFacing.UP);

                            sleep(400 + gaussian(150));
                        }

                        mc.thePlayer.moveForward = 0;

                        // Turn left
                        mc.thePlayer.rotationYaw -= 90;

                        sleep(200);
                    }

                } catch (Exception ignored) {
                }
            }
        }).start();
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
