package io.github.itzispyder.clickcrystals.events.listeners;

import io.github.itzispyder.clickcrystals.events.EventHandler;
import io.github.itzispyder.clickcrystals.events.Listener;
import io.github.itzispyder.clickcrystals.events.events.client.KeyPressEvent;
import io.github.itzispyder.clickcrystals.events.events.client.SetScreenEvent;
import io.github.itzispyder.clickcrystals.gui_beta.ClickType;
import io.github.itzispyder.clickcrystals.gui_beta.GuiScreen;
import io.github.itzispyder.clickcrystals.gui_beta.screens.*;
import io.github.itzispyder.clickcrystals.modules.keybinds.Keybind;
import io.github.itzispyder.clickcrystals.modules.modules.clickcrystals.DiscordRPC;
import io.github.itzispyder.clickcrystals.util.misc.ManualMap;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;

import java.util.Map;

import static io.github.itzispyder.clickcrystals.ClickCrystals.*;

public class UserInputListener implements Listener {

    private static Class<? extends GuiScreen> previousScreen = null;
    public static void openPreviousScreen() {
        Class<? extends GuiScreen> p = previousScreen;
        if (p == ModuleScreen.class || p == ModuleEditScreen.class) {
            mc.setScreen(new ModuleScreen());
        }
        else if (p == SearchScreen.class) {
            mc.setScreen(new SearchScreen());
        }
        else if (p == SettingScreen.class) {
            mc.setScreen(new SettingScreen());
        }
        else if (p == BulletinScreen.class) {
            mc.setScreen(new BulletinScreen());
        }
        else {
            mc.setScreen(new HomeScreen());
        }
    }

    public static final Map<Class<? extends Screen>, String> SCREEN_STATES = ManualMap.fromItems(
            TitleScreen.class, "Looking at the title screen",
            ModuleScreen.class, "Toggling modules",
            HomeScreen.class, "Scanning through ClickCrystals home",
            SearchScreen.class, "Searching modules",
            SettingScreen.class, "Changing ClickCrystals keybinds",
            SelectWorldScreen.class, "Selecting singleplayer",
            MultiplayerScreen.class, "Selecting server",
            GameMenuScreen.class, "Idling...",
            //CreditsScreen.class, "Checking out the goats",
            BulletinScreen.class, "Viewing CC Bulletin Board"
    );

    @EventHandler
    public void onKeyPress(KeyPressEvent e) {
        try {
            this.handleKeybindings(e);
        }
        catch (Exception ignore) {}
    }

    @EventHandler
    public void onScreenChange(SetScreenEvent e) {
        try {
            this.handleDiscordPresence(e);
            this.handleConfigSave(e);
            this.handleScreenManagement(e);
        }
        catch (Exception ignore) {}
    }

    private void handleScreenManagement(SetScreenEvent e) {
        if (e.getScreen() == null && e.getPreviousScreen() instanceof GuiScreen screen) {
            Class<? extends GuiScreen> p = screen.getClass();

            if (p == BulletinScreen.class ||
                    p == ModuleEditScreen.class ||
                    p == SearchScreen.class ||
                    p == SettingScreen.class ||
                    p == HomeScreen.class ||
                    p == ModuleScreen.class) {
                previousScreen = p;
            }
        }
    }

    private void handleConfigSave(SetScreenEvent e) {
        if (e.getScreen() instanceof GameMenuScreen) {
            config.save();
        }
    }

    private void handleDiscordPresence(SetScreenEvent e) {
        Screen s = e.getScreen();

        if (s == null) {
            if (mc.player != null) {
                discordPresence.setState(mc.isInSingleplayer() ? "In singleplayer world" : "In multiplayer server");
            }
            else {
                discordPresence.setState("Looking at the title screen");
            }
        }
        else if (s instanceof ModuleEditScreen mss) {
            discordPresence.setState("Editing module " + mss.getModule().getName() + "...");
        }
        else {
            discordPresence.setState(SCREEN_STATES.getOrDefault(s.getClass(), "Clicking Crystals..."));
        }

        DiscordRPC.syncRPC();
    }

    private void handleKeybindings(KeyPressEvent e) {
        if (e.getAction() == ClickType.CLICK) {
            for (Keybind bind : system.getBindsOf(e.getKeycode())) {
                if (bind.canPress(e.getKeycode(), e.getScancode())) {
                    bind.onPress();
                }
            }
        }
    }
}
