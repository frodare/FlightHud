package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
  public void render(DrawContext context, float partial, MinecraftClient mc) {
    float top = dim.tFrame;
    float bottom = dim.bFrame;

    float left = dim.lFrame - 2;
    float right = dim.lFrame;
    float unitPerPixel = 30;

    float floorOffset = computer.speed * unitPerPixel;
    float yFloor = dim.yMid - floorOffset;

    float xSpeedText = left - 5;

    MatrixStack m = context.getMatrices();

    if (CONFIG.speed_showReadout) {
      drawRightAlignedFont(mc, m, String.format("%.2f", computer.speed), xSpeedText, dim.yMid - 3, context);
      drawBox(m, xSpeedText - 29.5f, dim.yMid - 4.5f, 30, 10);
    }


    if (CONFIG.speed_showScale) {
      for (float i = 0; i <= 100; i = i + 0.25f) {
        float y = dim.hScreen - i * unitPerPixel - yFloor;
        if (y < top || y > (bottom - 5))
          continue;

        if (i % 1 == 0) {
          drawHorizontalLine(m, left - 2, right, y);
          if (!CONFIG.speed_showReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
            drawRightAlignedFont(mc, m, String.format("%.0f", i), xSpeedText, y - 3, context);
          }
        }
        drawHorizontalLine(m, left, right, y);
      }
    }
  }
}