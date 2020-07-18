package net.torocraft.flighthud;

import net.fabricmc.api.ModInitializer;
import net.torocraft.flighthud.config.Config;
import net.torocraft.flighthud.config.ConfigLoader;

public class FlightHud implements ModInitializer {
  public static final String MODID = "flighthud";
  public static Config CONFIG = new Config();

  @Override
  public void onInitialize() {
    ConfigLoader.load();
    if (CONFIG.watchForConfigChanges) {
      ConfigLoader.watch();
    }
  }
}
