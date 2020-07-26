package net.torocraft.flighthud.config;

import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.torocraft.flighthud.config.ConfigLoader.ConfigType;

public class SwitchDisplayModeCommand implements Command<ServerCommandSource> {

  public enum DisplayMode {
    NONE, MIN, FULL
  }

  @Override
  public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    toggle();
    return 0;
  }

  public static DisplayMode parseDisplayMode(String s) {
    try {
      return DisplayMode.valueOf(s);
    } catch (Exception e) {
      return DisplayMode.NONE;
    }
  }

  private static String toggle(String curr) {
    DisplayMode m = parseDisplayMode(curr);
    int i = (m.ordinal() + 1) % DisplayMode.values().length;
    return DisplayMode.values()[i].toString();
  }

  public static void toggle() {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player.isFallFlying()) {
      CONFIG_SETTINGS.displayModeWhenFlying = toggle(CONFIG_SETTINGS.displayModeWhenFlying);
    } else {
      CONFIG_SETTINGS.displayModeWhenNotFlying = toggle(CONFIG_SETTINGS.displayModeWhenNotFlying);
    }
    ConfigLoader.save(CONFIG_SETTINGS, ConfigType.SETTINGS);
  }

  public static void switchTo(DisplayMode mode) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player.isFallFlying()) {
      CONFIG_SETTINGS.displayModeWhenFlying = mode.toString();
    } else {
      CONFIG_SETTINGS.displayModeWhenNotFlying = mode.toString();
    }
    ConfigLoader.save(CONFIG_SETTINGS, ConfigType.SETTINGS);
  }

}
