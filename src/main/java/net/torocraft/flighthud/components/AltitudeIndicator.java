package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class AltitudeIndicator extends HudComponent {
  private final Dimensions dim;
  private final FlightComputer computer;

  public AltitudeIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(DrawContext context, float partial, MinecraftClient mc) {
    float top = dim.tFrame;
    float bottom = dim.bFrame;

    float right = dim.rFrame + 2;
    float left = dim.rFrame;

    float blocksPerPixel = 1;

    float floorOffset = i(computer.altitude * blocksPerPixel);
    float yFloor = dim.yMid - floorOffset;
    float xAltText = right + 5;

    MatrixStack m = context.getMatrices();

    if (CONFIG.altitude_showGroundInfo) {
      drawHeightIndicator(mc, m, left - 1, dim.yMid, bottom - dim.yMid);
    }

    if (CONFIG.altitude_showReadout) {
      drawFont(mc, m, String.format("%.0f", computer.altitude), xAltText, dim.yMid - 3, context);
      drawBox(m, xAltText - 2, dim.yMid - 4.5f, 28, 10);
    }

    if (CONFIG.altitude_showHeight) {
      drawFont(mc, m, "G", xAltText - 10, bottom + 3, context);
      String heightText = computer.distanceFromGround == null ? "??"
          : String.format("%d", i(computer.distanceFromGround));
      drawFont(mc, m, heightText, xAltText, bottom + 3, context);
      drawBox(m, xAltText - 2, bottom + 1.5f, 28, 10);
    }

    if (CONFIG.altitude_showScale) {
      for (int i = 0; i < 1000; i = i + 10) {

        float y = (dim.hScreen - i * blocksPerPixel) - yFloor;
        if (y < top || y > (bottom - 5))
          continue;

        if (i % 50 == 0) {
          drawHorizontalLine(m, left, right + 2, y);
          if (!CONFIG.altitude_showReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
            drawFont(mc, m, String.format("%d", i), xAltText, y - 3, context);
          }
        }
        drawHorizontalLine(m, left, right, y);
      }
    }
  }

  private void drawHeightIndicator(MinecraftClient client, MatrixStack m, float x, float top, float h) {
    float bottom = top + h;
    float blocksPerPixel =  h / (client.world.getHeight() + 64f);
    float yAlt = bottom - i((computer.altitude + 64) * blocksPerPixel);
    float yFloor = bottom - i(64 * blocksPerPixel);

    drawVerticalLine(m, x, top - 1, bottom + 1);

    if (computer.groundLevel != null) {
      float yGroundLevel = bottom - (computer.groundLevel + 64f) * blocksPerPixel;
      fill(m, x - 3, yGroundLevel + 2, x, yFloor);
    }

    drawHorizontalLine(m, x - 6, x - 1, top);
    drawHorizontalLine(m, x - 6, x - 1, yFloor);
    drawHorizontalLine(m, x - 6, x - 1, bottom);

    drawPointer(m, x, yAlt, 90);
  }

}
