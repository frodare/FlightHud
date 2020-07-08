package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class FlightPathIndicator extends HudComponent {

  private final Dimensions dim;
  private final FlightComputer computer;

  public FlightPathIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient client) {
    double deltaPitch = computer.pitch - computer.flightPitch;
    double deltaHeading = wrapHeading(computer.flightHeading) - wrapHeading(computer.heading);
    
    if (deltaHeading < -180) {
      deltaHeading += 360;
    }

    int y = dim.yMid;
    int x = dim.xMid;

    y += i(deltaPitch * dim.degreesPerPixel);
    x += i(deltaHeading * dim.degreesPerPixel);

    int l = x - 3;
    int r = x + 3;
    int t = y - 3;
    int b = y + 3;

    drawVerticalLine(m, l, t, b, COLOR);
    drawVerticalLine(m, r, t, b, COLOR);

    drawHorizontalLine(m, l, r, t, COLOR);
    drawHorizontalLine(m, l, r, b, COLOR);

    drawVerticalLine(m, x, t - 5, t, COLOR);
    drawHorizontalLine(m, l - 4, l, y, COLOR);
    drawHorizontalLine(m, r, r + 4, y, COLOR);
  }
}