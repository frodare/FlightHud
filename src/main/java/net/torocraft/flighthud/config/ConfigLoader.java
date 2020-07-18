package net.torocraft.flighthud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.torocraft.flighthud.FlightHud;
import net.torocraft.flighthud.HudComponent;

public class ConfigLoader {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String FILENAME = FlightHud.MODID + ".json";
  private static File file;
  private static Thread watcher;

  public static void load() {
    File file = getFile();

    if (!file.exists()) {
      FlightHud.CONFIG = new Config();
      save();
    } else {

      try (FileReader reader = new FileReader(file)) {
        FlightHud.CONFIG = GSON.fromJson(reader, Config.class);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    updateColor();
    updateThickness();
  }

  private static void updateThickness() {
    try {
      HudComponent.HALF_THICKNESS = FlightHud.CONFIG.thickness / 2;
    } catch (Exception e) {
      HudComponent.HALF_THICKNESS = 0.5f;
    }
  }

  private static void updateColor() {
    try {
      HudComponent.COLOR = new Color(FlightHud.CONFIG.color_red, FlightHud.CONFIG.color_green,
          FlightHud.CONFIG.color_blue).getRGB();
    } catch (Exception e) {
      HudComponent.COLOR = Color.GREEN.getRGB();
    }
  }

  private static void save() {
    File file = getFile();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(GSON.toJson(FlightHud.CONFIG));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void watch() {
    if (watcher != null) {
      return;
    }
    watcher = FileWatcher.watch(getFile(), ConfigLoader::load);
  }

  private static File getFile() {
    if (file == null) {
      file = new File(FabricLoader.getInstance().getConfigDirectory(), FILENAME);
    }
    return file;
  }

}
