package net.torocraft.flighthud.config.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.function.Consumer;

public abstract class ConfigLoader<T extends IConfig> {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private final Class<T> configClass;
  private final Consumer<T> onLoad;
  private final File file;
  private FileWatcher watcher;

  public ConfigLoader(Class<T> configClass, String filename, Consumer<T> onLoad) {
    this.configClass = configClass;
    this.onLoad = onLoad;
    this.file = new File(getConfigFolder(), filename);
  }

  public void load() {
    T config = createEmptyInstance();

    if (!file.exists()) {
      save(config);
    }

    config = read();
    config.update();
    onLoad.accept(config);

    if (config.shouldWatch()) {
      watch(file);
    }
  }

  private T createEmptyInstance() {
    try {
      return configClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract File getConfigFolder();

  public T read() {
    try (FileReader reader = new FileReader(file)) {
      return GSON.fromJson(reader, configClass);
    } catch (Exception e) {
      e.printStackTrace();
      return createEmptyInstance();
    }
  }

  public void save(T config) {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(GSON.toJson(config));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void watch(File file) {
    if (watcher != null) {
      return;
    }
    watcher = FileWatcher.watch(file, () -> load());
  }

}
