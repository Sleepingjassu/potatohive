package com.potatohive.client;

import com.potatohive.client.gui.HiveOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy {
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new HiveOverlay());
    }
}
