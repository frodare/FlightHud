package net.torocraft.flighthud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.torocraft.flighthud.FlightHud;

public class ConfigLoader {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Map<ConfigType, File> files = new ConcurrentHashMap<>();
  private static final Map<ConfigType, FileWatcher> watchers = new ConcurrentHashMap<>();

  public enum ConfigType { MIN, FULL, SETTINGS }

  public static void load() {
    FlightHud.CONFIG_SETTINGS = load(ConfigType.SETTINGS, SettingsConfig.class);
    FlightHud.CONFIG_FULL = load(ConfigType.FULL, HudConfig.class);
    FlightHud.CONFIG_MIN = load(ConfigType.MIN, HudConfig.class);
  }

  public static void save() {
    save(FlightHud.CONFIG_SETTINGS, ConfigType.SETTINGS);
    save(FlightHud.CONFIG_FULL, ConfigType.FULL);
    save(FlightHud.CONFIG_MIN, ConfigType.MIN);
  }

  private static <T extends IConfig> T load(ConfigType type, Class<T> configClass) {
    File file = getFile(type);

    T config;
    try {
      config = configClass.newInstance();
      if (ConfigType.MIN.equals(type)) {
        setDefaultMinSettings((HudConfig)config);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (!file.exists()) {
      save(config, type);
    } else {
      try (FileReader reader = new FileReader(file)) {
        config = GSON.fromJson(reader, configClass);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    config.update();
    return config;
  }

  public static <T extends IConfig> void save(T config, ConfigType type) {
    File file = getFile(type);
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(GSON.toJson(config));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static <T extends IConfig> void watch(ConfigType type, Class<T> configClass) {
    FileWatcher watcher = watchers.get(type);
    if (watcher != null) {
      return;
    }
    watcher = FileWatcher.watch(getFile(type), () -> load(type, configClass));
  }

  private static File getFile(ConfigType type) {
    File file = files.get(type);
    return getFile(file, FlightHud.MODID + "." + type.toString().toLowerCase() + ".json");
  }

  private static File getFile(File file, String filename) {
    if (file == null) {
      file = new File(FabricLoader.getInstance().getConfigDirectory(), filename);
    }
    return file;
  }

  private static void setDefaultMinSettings(HudConfig config) {
    config.altitude_showScale = false;
    config.speed_showScale = false;
    config.heading_showScale = false;
    config.altitude_showGroundInfo = false;
    config.pitchLadder_showLadder = false;
    config.pitchLadder_optimumClimbAngle = 0;
    config.pitchLadder_optimumGlideAngle = 0;
    config.elytra_showHealth = false;
    config.flightPath_show = false;
    config.altitude_showHeight = false;
  }
}
