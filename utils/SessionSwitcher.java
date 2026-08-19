package com.potatohive.client.utils;

import com.mojang.authlib.GameProfile;
import com.potatohive.client.storage.AccountStore;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import java.lang.reflect.Field;
import java.util.UUID;

public class SessionSwitcher {
    public static void switchTo(AccountStore.Account acc) {
        Minecraft mc = Minecraft.getMinecraft();
        // Generate a fake session for offline mode
        Session newSession = new Session(
            acc.name,
            acc.uuid,
            acc.uuid,
            "mojang" // offline mode token
        );
        try {
            Field sessionField = Minecraft.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            sessionField.set(mc, newSession);
            
            // Also update the player's GameProfile if already spawned
            if (mc.thePlayer != null) {
                GameProfile gp = new GameProfile(UUID.fromString(acc.uuid), acc.name);
                Field profileField = mc.thePlayer.getClass().getDeclaredField("gameProfile");
                profileField.setAccessible(true);
                profileField.set(mc.thePlayer, gp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
