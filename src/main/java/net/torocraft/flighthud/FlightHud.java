package net.torocraft.flighthud;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.ClientRegistry;
import net.torocraft.flighthud.config.HudConfig;
import net.torocraft.flighthud.config.SettingsConfig;
import net.torocraft.flighthud.config.loader.ConfigLoader;
import org.lwjgl.glfw.GLFW;

@Mod(FlightHud.MODID)
public class FlightHud {
  public static final String MODID = "flighthud";

  public static SettingsConfig CONFIG_SETTINGS = new SettingsConfig();
  public static HudConfig CONFIG_MIN = new HudConfig();
  public static HudConfig CONFIG_FULL = new HudConfig();
  
  public static ConfigLoader<SettingsConfig> CONFIG_LOADER_SETTINGS = new ConfigLoader<>(
    new SettingsConfig(), 
    FlightHud.MODID + ".settings.json", 
    config -> FlightHud.CONFIG_SETTINGS = config);
    

  public static ConfigLoader<HudConfig> CONFIG_LOADER_FULL = new ConfigLoader<>(
    new HudConfig(), 
    FlightHud.MODID + ".full.json", 
    config -> FlightHud.CONFIG_FULL = config);
  

  public static ConfigLoader<HudConfig> CONFIG_LOADER_MIN = new ConfigLoader<>(
    HudConfig.getDefaultMinSettings(), 
    FlightHud.MODID + ".min.json", 
    config -> FlightHud.CONFIG_MIN = config);

  private static KeyMapping keyBinding;

  public FlightHud() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.addListener(this::onKeyInput);
  }

  private void setup(final FMLCommonSetupEvent event) {
    keyBinding = new KeyMapping("key.flighthud.toggleDisplayModed", GLFW.GLFW_KEY_GRAVE_ACCENT, "category.flighthud.toggleDisplayMode");
    CONFIG_LOADER_SETTINGS.load();
    CONFIG_LOADER_FULL.load();
    CONFIG_LOADER_MIN.load();
    setupKeycCode();
    setupCommand();
  }

  private void onKeyInput(KeyInputEvent event) {
    if (keyBinding.consumeClick()) {
      CONFIG_SETTINGS.toggleDisplayMode();
    }
  }

  private static void setupKeycCode() {
    ClientRegistry.registerKeyBinding(keyBinding);
  }

  private static void setupCommand() {
    // CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
    //   dispatcher.register(CommandManager.literal("flighthud")
    //       .then(CommandManager.literal("toggle").executes(new SwitchDisplayModeCommand())));
    // });
  }
}
