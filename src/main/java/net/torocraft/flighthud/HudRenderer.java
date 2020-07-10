package net.torocraft.flighthud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.components.AltitudeIndicator;
import net.torocraft.flighthud.components.ElytraHealthIndicator;
import net.torocraft.flighthud.components.FlightPathIndicator;
import net.torocraft.flighthud.components.HeadingIndicator;
import net.torocraft.flighthud.components.LocationIndicator;
import net.torocraft.flighthud.components.PitchIndicator;
import net.torocraft.flighthud.components.SpeedIndicator;

public class HudRenderer extends HudComponent {

  private final Dimensions dim = new Dimensions();
  private final FlightComputer computer = new FlightComputer();

  private final HudComponent[] components = new HudComponent[] {
    new FlightPathIndicator(computer, dim), 
    new LocationIndicator(dim),
    new HeadingIndicator(computer, dim), 
    new SpeedIndicator(computer, dim),
    new AltitudeIndicator(computer, dim),
    new PitchIndicator(computer, dim),
    new ElytraHealthIndicator(computer, dim)
  };

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient client) {
    if (!client.player.isFallFlying()) {
      return;
    }

    computer.update(client, partial);
    dim.update(client);

    for (HudComponent component : components) {
      component.render(m, partial, client);
    }
  }
}
