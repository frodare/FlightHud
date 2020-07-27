package net.torocraft.flighthud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.torocraft.flighthud.FlightHud;

public class ConfigLoader {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Map<ConfigType, File> files = new ConcurrentHashMap<>();
  private static final Map<ConfigType, FileWatcher> watchers = new ConcurrentHashMap<>();
  private static final Map<ConfigType, Consumer<IConfig>> updateHandlers = new HashMap<>();

  static {
    updateHandlers.put(ConfigType.FULL, config -> FlightHud.CONFIG_FULL = (HudConfig) config);
    updateHandlers.put(ConfigType.MIN, config -> FlightHud.CONFIG_MIN = (HudConfig) config);
    updateHandlers.put(ConfigType.SETTINGS, config -> FlightHud.CONFIG_SETTINGS = (SettingsConfig) config);
  }

  public enum ConfigType {
    MIN, FULL, SETTINGS
  }

  public static void load() {
    load(ConfigType.SETTINGS, SettingsConfig.class);
    load(ConfigType.FULL, HudConfig.class);
    load(ConfigType.MIN, HudConfig.class);
  }

  public static void save() {
    save(FlightHud.CONFIG_SETTINGS, ConfigType.SETTINGS);
    save(FlightHud.CONFIG_FULL, ConfigType.FULL);
    save(FlightHud.CONFIG_MIN, ConfigType.MIN);
  }

  private static <T extends IConfig> void load(ConfigType type, Class<T> configClass) {
    File file = getFile(type);

    T config;
    try {
      config = configClass.newInstance();
      if (ConfigType.MIN.equals(type)) {
        setDefaultMinSettings((HudConfig) config);
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
    updateHandlers.get(type).accept(config);
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
