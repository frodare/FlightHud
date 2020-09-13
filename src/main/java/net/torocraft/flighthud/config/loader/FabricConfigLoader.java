package net.torocraft.flighthud.config.loader;

import java.io.File;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricConfigLoader<T extends IConfig> extends ConfigLoader<T> {

  public FabricConfigLoader(Class<T> configClass, String filename, Consumer<T> onLoad) {
    super(configClass, filename, onLoad);
  }

  @Override
  protected File getConfigFolder() {
    return FabricLoader.getInstance().getConfigDir().toFile();
  }

}
