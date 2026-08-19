package com.potatohive.client.storage;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AccountStore {
    private static AccountStore instance;
    private List<Account> accounts = new ArrayList<>();
    private int activeIndex = 0;
    private final Path filePath = Paths.get("config/potatohive_accounts.json");

    public static AccountStore getInstance() {
        if (instance == null) instance = new AccountStore();
        return instance;
    }

    public void addAccount(String username) {
        accounts.add(new Account(username, UUID.randomUUID().toString()));
        save();
    }

    public void removeAccount(int index) {
        if (index >= 0 && index < accounts.size()) {
            accounts.remove(index);
            if (activeIndex >= accounts.size()) activeIndex = accounts.size() - 1;
            save();
        }
    }

    public List<Account> getAccounts() { return accounts; }
    public Account getActive() { return accounts.isEmpty() ? null : accounts.get(activeIndex); }
    public int getActiveIndex() { return activeIndex; }
    public void setActive(int index) { 
        if (index >= 0 && index < accounts.size()) {
            activeIndex = index;
            // Trigger session switch
            SessionSwitcher.switchTo(accounts.get(index));
        }
    }

    public void load() {
        if (!Files.exists(filePath)) return;
        try (Reader r = Files.newBufferedReader(filePath)) {
            JsonArray arr = new JsonParser().parse(r).getAsJsonArray();
            accounts.clear();
            arr.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                accounts.add(new Account(obj.get("name").getAsString(), obj.get("uuid").getAsString()));
            });
        } catch (Exception ignored) {}
    }

    public void save() {
        JsonArray arr = new JsonArray();
        accounts.forEach(acc -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", acc.name);
            obj.addProperty("uuid", acc.uuid);
            arr.add(obj);
        });
        try (Writer w = Files.newBufferedWriter(filePath)) {
            w.write(new GsonBuilder().setPrettyPrinting().create().toJson(arr));
        } catch (Exception ignored) {}
    }

    public static class Account {
        public String name, uuid;
        public Account(String name, String uuid) { this.name = name; this.uuid = uuid; }
        @Override public String toString() { return name; }
    }
}
