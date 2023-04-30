package net.torocraft.flighthud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.torocraft.flighthud.components.AltitudeIndicator;
import net.torocraft.flighthud.components.ElytraHealthIndicator;
import net.torocraft.flighthud.components.FlightPathIndicator;
import net.torocraft.flighthud.components.HeadingIndicator;
import net.torocraft.flighthud.components.LocationIndicator;
import net.torocraft.flighthud.components.PitchIndicator;
import net.torocraft.flighthud.components.SpeedIndicator;
import net.torocraft.flighthud.config.SettingsConfig.DisplayMode;

class fps {
  public static int tps = 1;
}
public class HudRenderer extends HudComponent {
  private final Dimensions dim = new Dimensions();
  private final FlightComputer computer = new FlightComputer();
  private static final String FULL = DisplayMode.FULL.toString();
  private static final String MIN = DisplayMode.MIN.toString();

  private final HudComponent[] components =
      new HudComponent[] {new FlightPathIndicator(computer, dim), new LocationIndicator(dim),
          new HeadingIndicator(computer, dim), new SpeedIndicator(computer, dim),
          new AltitudeIndicator(computer, dim), new PitchIndicator(computer, dim),
          new ElytraHealthIndicator(computer, dim)};

  private void setupConfig(Minecraft client) {
    HudComponent.CONFIG = null;
    if (client.player.isFallFlying()) {
      if (FlightHud.CONFIG_SETTINGS.displayModeWhenFlying.equals(FULL)) {
        HudComponent.CONFIG = FlightHud.CONFIG_FULL;
      } else if (FlightHud.CONFIG_SETTINGS.displayModeWhenFlying.equals(MIN)) {
        HudComponent.CONFIG = FlightHud.CONFIG_MIN;
      }
    } else {
      if (FlightHud.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(FULL)) {
        HudComponent.CONFIG = FlightHud.CONFIG_FULL;
      } else if (FlightHud.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(MIN)) {
        HudComponent.CONFIG = FlightHud.CONFIG_MIN;
      }
    }
  }

  @Override
  public void render(PoseStack m, float partial, Minecraft client) {
    setupConfig(client);

    if (HudComponent.CONFIG == null) {
      return;
    }

    try {
      if (fps.tps == 1){
        m.pushPose();

        if (HudComponent.CONFIG.scale != 1d) {
          float scale = 1 / (float) HudComponent.CONFIG.scale;
          m.scale(scale, scale, scale);
        }

        computer.update(client, partial);
        dim.update(client);

        for (HudComponent component : components) {
          component.render(m, partial, client);
        }
        m.popPose();
        fps.tps = 2;
      }
    else if (fps.tps == 2){
      fps.tps = 3;
    }
    else if (fps.tps == 3){
      fps.tps = 4;
    }
    else if (fps.tps == 4){
      fps.tps = 5;
    }
    else if (fps.tps == 5){
      fps.tps = 6;
    }
    else if (fps.tps == 6){
      fps.tps = 1;
    }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
