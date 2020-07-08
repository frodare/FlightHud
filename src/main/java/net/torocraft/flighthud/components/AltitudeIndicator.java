package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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
  public void render(MatrixStack m, float partial, MinecraftClient client) {
    TextRenderer fontRenderer = client.textRenderer;

    int vMargin = i(dim.height * 0.1d);
    int top = vMargin;
    int bottom = dim.height - vMargin;

    int right = dim.width - 60;
    int left = right - 2;
    int blocksPerPixel = 1;

    int floorOffset = i(computer.altitude * blocksPerPixel);
    int yFloor = dim.yMid - floorOffset;
    int xAltText = right + 5;


    drawHeightIndicator(client, m, left - 1, dim.yMid, bottom - dim.yMid);

    fontRenderer.draw(m, String.format("%.0f", computer.altitude), xAltText, dim.yMid - 3, COLOR);
    drawBox(m, xAltText - 3, dim.yMid - 5, 25, 10);

    fontRenderer.draw(m, "G", xAltText - 5, bottom + 2, COLOR);
    String heightText = computer.distanceFromGround == null ? "??"
        : String.format("%d", i(computer.distanceFromGround));
    fontRenderer.draw(m, heightText, xAltText + 5, bottom + 2, COLOR);
    drawBox(m, xAltText +2, bottom, 25, 10);

    for (int i = 0; i < 1000; i = i + 10) {

      int y = (dim.height - i * blocksPerPixel) - yFloor;
      if (y < top || y > (bottom - 5))
        continue;

      int color = COLOR;

      if (i % 50 == 0) {
        drawHorizontalLine(m, left, right + 2, y, color);
        if (y > dim.yMid + 7 || y < dim.yMid - 7) {
          fontRenderer.draw(m, String.format("%d", i), xAltText, y - 3, COLOR);
        }
      }
      drawHorizontalLine(m, left, right, y, color);

    }
  }


  private void drawHeightIndicator(MinecraftClient client, MatrixStack m, int x, int top, int h) {
    int bottom = top + h;
    double blocksPerPixel = (double) h / (double) (client.world.getHeight() + 64);
    int yAlt = bottom - i((computer.altitude + 64) * blocksPerPixel);
    int yFloor = bottom - i(64 * blocksPerPixel);
    
    drawVerticalLine(m, x, top - 1, bottom + 1, COLOR);

    if (computer.groundLevel != null) {
      int yGroundLevel = bottom - i((computer.groundLevel + 64) * blocksPerPixel);
      drawVerticalLine(m, x - 1, yGroundLevel, bottom, COLOR_WARN);
      drawHorizontalLine(m, x - 4, x - 2, yGroundLevel + 1, COLOR_WARN);
    }

    drawHorizontalLine(m, x - 4, x - 1, top, COLOR);
    drawHorizontalLine(m, x - 4, x - 1, yFloor, COLOR);
    drawHorizontalLine(m, x - 4, x - 1, bottom, COLOR_WARN);
    
    drawPointer(m, x, yAlt, 90);
  }

}