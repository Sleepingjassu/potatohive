package com.potatohive.client.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemFood;
import java.util.List;

import static com.potatohive.client.utils.RandomDelay.*;

public class CombatTask {

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

                    // Find nearest mob
                    List<Entity> mobs = mc.theWorld.loadedEntityList;

                    Entity target = null;
                    double minDist = 6.0;

                    for (Entity e : mobs) {
                        if (e instanceof EntityMob
                                && e.getDistanceToEntity(mc.thePlayer) < minDist) {

                            target = e;
                            minDist = e.getDistanceToEntity(mc.thePlayer);
                        }
                    }

                    if (target != null) {
                        mc.thePlayer.rotationYaw = getAngleTo(target);

                        mc.playerController.attackEntity(
                                mc.thePlayer,
                                target
                        );

                        sleep(300 + gaussian(100));
                    }

                    // Eat if health < 5 and have food
                    if (mc.thePlayer.getHealth() < 5.0f) {

                        for (int i = 0; i < 9; i++) {

                            net.minecraft.item.ItemStack stack =
                                    mc.thePlayer.inventory.getStackInSlot(i);

                            if (stack != null
                                    && stack.getItem() instanceof ItemFood) {

                                mc.thePlayer.inventory.currentItem = i;

                                mc.playerController.rightClick(
                                        mc.thePlayer,
                                        mc.theWorld
                                );

                                sleep(1600 + gaussian(200));
                                break;
                            }
                        }
                    }

                    sleep(200);

                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    private float getAngleTo(Entity e) {
        double dx = e.posX
                - Minecraft.getMinecraft().thePlayer.posX;

        double dz = e.posZ
                - Minecraft.getMinecraft().thePlayer.posZ;

        return (float) (
                Math.atan2(dz, dx) * 180.0 / Math.PI
        ) - 90;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
        }
    }
}
