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

  private final HudComponent[] components = new HudComponent[]{
    new FlightPathIndicator(computer, dim),
    new LocationIndicator(dim),
    new HeadingIndicator(computer, dim),
    new SpeedIndicator(computer, dim),
    new AltitudeIndicator(computer, dim),
    new PitchIndicator(computer, dim),
    new ElytraHealthIndicator(computer, dim)
  };

  protected void drawBoxx(MatrixStack m, int x, int y, int w, int h) {
    drawHorizontalLine(m, x, x + w, y, COLOR_WARN);
    drawHorizontalLine(m, x, x + w, y + h, COLOR_WARN);
    drawVerticalLine(m, x, y, y + h, COLOR_WARN);
    drawVerticalLine(m, x + w, y, y + h, COLOR_WARN);
  }

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


    //drawBoxx(m, dim.lFrame, dim.tFrame, dim.wFrame, dim.hFrame);
  }
}