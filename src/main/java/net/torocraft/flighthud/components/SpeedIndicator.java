package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class SpeedIndicator extends HudComponent {
  private final Dimensions dim;
  private final FlightComputer computer;

  public SpeedIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient client) {
    int vMargin = i(dim.height * 0.1d);
    int top = vMargin;
    int bottom = dim.height - vMargin;

    int left = 60;
    int right = left + 2;
    double unitPerPixel = 30;

    int floorOffset = i(computer.speed * unitPerPixel);
    int yFloor = dim.yMid - floorOffset;

    int xSpeedText = left - 5;
    drawRightAlignedFont(client, m, String.format("%.2f", computer.speed), xSpeedText, dim.yMid - 3);
    drawBox(m, xSpeedText - 30, dim.yMid - 5, 30, 10);

    for (double i = 0; i <= 14; i = i + 0.25) {

      int y = i(dim.height - i * unitPerPixel) - yFloor;
      if (y < top || y > (bottom - 5))
        continue;


      if (i % 1 == 0) {
        drawHorizontalLine(m, left - 2, right, y, COLOR);
        if (y > dim.yMid + 7 || y < dim.yMid - 7) {
          drawRightAlignedFont(client, m, String.format("%.0f", i), xSpeedText, y - 3);
        }
      }
      drawHorizontalLine(m, left, right, y, COLOR);
    }
  }
}