package com.potatohive.client.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.potatohive.client.utils.RandomDelay.*;

public class SnipeTask {

    private boolean running = false;

    private final ConcurrentLinkedQueue<ItemStack> auctionItems =
            new ConcurrentLinkedQueue<>();

    public void start() {
        if (running) return;

        running = true;

        new Thread(() -> {
            while (running) {
                try {
                    Minecraft mc = Minecraft.getMinecraft();

                    // If an auction item exists, check its price
                    if (!auctionItems.isEmpty()) {

                        ItemStack item = auctionItems.poll();

                        if (item != null && getPrice(item) < 1000) {

                            if (mc.thePlayer != null
                                    && mc.thePlayer.openContainer != null) {

                                mc.playerController.windowClick(
                                        mc.thePlayer.openContainer.windowId,
                                        0,
                                        0,
                                        0,
                                        mc.thePlayer
                                );
                            }
                        }
                    }

                    sleep(5000 + gaussian(1000));

                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private int getPrice(ItemStack stack) {

        NBTTagCompound tag = stack.getTagCompound();

        if (tag != null && tag.hasKey("price")) {
            return tag.getInteger("price");
        }

        return 99999;
    }

    // Call this from a packet mixin
    public void onAuctionItem(ItemStack item) {
        if (item != null) {
            auctionItems.add(item);
        }
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
