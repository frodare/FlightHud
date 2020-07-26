package net.torocraft.flighthud.config;

import net.torocraft.flighthud.config.SwitchDisplayModeCommand.DisplayMode;

public class SettingsConfig implements IConfig {
  public boolean watchForConfigChanges = false;
  public String displayModeWhenFlying = DisplayMode.FULL.toString();
  public String displayModeWhenNotFlying = DisplayMode.NONE.toString();

  @Override
  public void update() {
  }
}
