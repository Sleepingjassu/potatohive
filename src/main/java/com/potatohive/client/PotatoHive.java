package com.potatohive.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = PotatoHive.MODID, version = PotatoHive.VERSION)
public class PotatoHive {
    public static final String MODID = "potatohive";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        AccountStore.getInstance().load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        ClientProxy.register();
    }
}
