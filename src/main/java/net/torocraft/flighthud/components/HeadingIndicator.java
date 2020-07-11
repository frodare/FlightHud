package net.torocraft.flighthud.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class HeadingIndicator extends HudComponent {

  private final Dimensions dim;
  private final FlightComputer computer;

  public HeadingIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, Minecraft mc) {
    int left = dim.lFrame;
    int right = dim.rFrame;
    int top = dim.tFrame - 10;


    int yText = top - 7;

    int northOffset = i(computer.heading * dim.degreesPerPixel);
    int xNorth = dim.xMid - northOffset;

    drawPointer(m, dim.xMid, top + 10, 0);

    drawFont(mc, m, String.format("%03d", i(wrapHeading(computer.heading))), dim.xMid - 8, yText);

    drawBox(m, dim.xMid - 15, yText - 2, 30, 10);

    for (int i = -540; i < 540; i = i + 5) {
      int x = i(i * dim.degreesPerPixel) + xNorth;
      if (x < left || x > right)
        continue;

      if (i % 15 == 0) {
        if (i % 90 == 0) {
          drawFont(mc, m, headingToDirection(i), x - 2, yText + 10);
          drawFont(mc, m, headingToAxis(i), x - 8, yText + 20);
        } else {
          drawVerticalLine(m, x, top + 3, top + 10, COLOR);
        }

        if (x <= dim.xMid - 26 || x >= dim.xMid + 26) {
          drawFont(mc, m, String.format("%03d", i(wrapHeading(i))), x - 8, yText);
        }
      } else {
        drawVerticalLine(m, x, top + 6, top + 10, COLOR);
      }
    }
  }

  private String headingToDirection(int degrees) {
    degrees = i(wrapHeading(degrees));
    switch (degrees) {
      case 0:
        return "N";
      case 90:
        return "E";
      case 180:
        return "S";
      case 270:
        return "W";
      case 360:
        return "N";
      default:
        return "";
    }
  }

  private String headingToAxis(int degrees) {
    degrees = i(wrapHeading(degrees));
    switch (degrees) {
      case 0:
        return "-Z";
      case 90:
        return "+X";
      case 180:
        return "+Z";
      case 270:
        return "-X";
      case 360:
        return "-Z";
      default:
        return "";
    }
  }

}
