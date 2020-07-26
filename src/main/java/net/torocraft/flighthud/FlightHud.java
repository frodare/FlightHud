package net.torocraft.flighthud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.command.CommandManager;
import net.torocraft.flighthud.config.ConfigLoader;
import net.torocraft.flighthud.config.ConfigLoader.ConfigType;
import net.torocraft.flighthud.config.HudConfig;
import net.torocraft.flighthud.config.SettingsConfig;
import net.torocraft.flighthud.config.SwitchDisplayModeCommand;
import org.lwjgl.glfw.GLFW;

public class FlightHud implements ModInitializer {
  public static final String MODID = "flighthud";

  public static SettingsConfig CONFIG_SETTINGS = new SettingsConfig();
  public static HudConfig CONFIG_MIN = new HudConfig();
  public static HudConfig CONFIG_FULL = new HudConfig();

  private static KeyBinding keyBinding;

  @Override
  public void onInitialize() {
    loadConfig();
    setupKeycCode();
    setupCommand();
  }

  private static void loadConfig() {
    ConfigLoader.load();
    if (CONFIG_FULL.watchForConfigChanges) {
      ConfigLoader.watch(ConfigType.FULL, HudConfig.class);
    }
    if (CONFIG_MIN.watchForConfigChanges) {
      ConfigLoader.watch(ConfigType.MIN, HudConfig.class);
    }
    if (CONFIG_SETTINGS.watchForConfigChanges) {
      ConfigLoader.watch(ConfigType.SETTINGS, SettingsConfig.class);
    }
  }

  private static void setupKeycCode() {
    keyBinding = new KeyBinding("key.flighthud.toggleDisplayMode", InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_GRAVE_ACCENT, "category.flighthud.toggleDisplayMode");

    KeyBindingHelper.registerKeyBinding(keyBinding);

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (keyBinding.wasPressed()) {
        SwitchDisplayModeCommand.toggle();
      } ;
    });
  }

  private static void setupCommand() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
      dispatcher.register(CommandManager.literal("flighthud")
          .then(CommandManager.literal("toggle").executes(new SwitchDisplayModeCommand())));
    });
  }
}
