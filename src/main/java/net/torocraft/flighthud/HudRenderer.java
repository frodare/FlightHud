package net.torocraft.flighthud;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class HudRenderer extends DrawableHelper {

  private static int COLOR = Color.GREEN.getRGB();
  private static int COLOR_WARN = Color.RED.getRGB();

  private PitchIndicatorData pitchData = new PitchIndicatorData();
  private Dimensions dim = new Dimensions();
  private FlightComputer computer = new FlightComputer();

  public static class Dimensions {
    public int height;
    public int width;
    public int xMid;
    public int yMid;
    public double degreesPerPixel;

    public void update(MinecraftClient client) {
      height = client.getWindow().getScaledHeight();
      width = client.getWindow().getScaledWidth();
      degreesPerPixel = i(height / client.options.fov);
      xMid = i(width / 2);
      yMid = i(height / 2);
    }
  }

  private static int i(double d) {
    return (int) Math.round(d);
  }

  public void render(MatrixStack m, float partial, MinecraftClient client) {
    if (!client.player.isFallFlying()) {
      return;
    }
    computer.update(client, partial);
    dim.update(client);

    drawPitchIndicator(client, m);
    drawHeadingIndicator(client, m);
    drawAltitudeIndicator(client, m);
    drawSpeedIndicator(client, m);
    drawLocationIndicator(client, m);
    drawFlightPathIndicator(client, m);

    //printDebug(m, client, partial);
  }

  private double wrapHeading(double degree) {
    while (degree < 0) {
      degree += 360;
    }
    while (degree >= 360) {
      degree -= 360;
    }
    return degree;
  }

  private void drawPointer(MatrixStack m, int x, int y, float rot) {
    GlStateManager.pushMatrix();
    GlStateManager.translated((float) x, (float) y, 0);
    GlStateManager.rotatef(45f + rot, 0, 0, 1f);
    drawVerticalLine(m, 0, 0, 5, COLOR);
    drawHorizontalLine(m, 0, 5, 0, COLOR);
    GlStateManager.popMatrix();
  }

  private void drawFlightPathIndicator(MinecraftClient client, MatrixStack m) {
    double deltaPitch = computer.pitch - computer.flightPitch;
    double deltaHeading = wrapHeading(computer.flightHeading) - wrapHeading(computer.heading);
    
    if (deltaHeading < -180) {
      deltaHeading += 360;
    }

    int y = dim.yMid - 1;
    int x = dim.xMid - 1;

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

    drawVerticalLine(m, x, t - 6, t, COLOR);
    drawHorizontalLine(m, l - 4, l, y, COLOR);
    drawHorizontalLine(m, r, r + 4, y, COLOR);
  }

  private void drawLocationIndicator(MinecraftClient client, MatrixStack m) {
    TextRenderer fontRenderer = client.textRenderer;
   
    int x = pitchData.margin + 30;
    int y = dim.height - pitchData.margin;

    int xLoc = client.player.getBlockPos().getX();
    int zLoc = client.player.getBlockPos().getZ();

    fontRenderer.draw(m, String.format("%d / %d", xLoc, zLoc), x, y, COLOR);
  }

  private void drawHeadingIndicator(MinecraftClient client, MatrixStack m) {
    TextRenderer fontRenderer = client.textRenderer;
    int width = i(dim.width * 0.5d);
    int left = i((dim.width - width) / 2);
    int right = width + left;
    int top = i(dim.height * 0.1d);

    int northOffset = i(computer.heading * dim.degreesPerPixel);
    int xNorth = dim.xMid - northOffset;

    drawPointer(m, dim.xMid, top + 8, 0);

    for (int i = -540; i < 540; i = i + 5) {
      int x = i(i * dim.degreesPerPixel) + xNorth;
      if (x < left || x > right)
        continue;

      if (i % 15 == 0) {
        drawVerticalLine(m, x, top, top + 6, COLOR);
        fontRenderer.draw(m, String.format("%03d", i(wrapHeading(i))), x - 8, top - 7, COLOR);
      } else {
        drawVerticalLine(m, x, top + 3, top + 6, COLOR);
      }
    }
  }

  private void drawRightAlignedFont(MinecraftClient client, MatrixStack m, String s, int x, int y) {
    int w = client.textRenderer.getWidth(s);
    client.textRenderer.draw(m, s, x - w, y, COLOR);
  }

  private void drawSpeedIndicator(MinecraftClient client, MatrixStack m) {
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

  private void drawAltitudeIndicator(MinecraftClient client, MatrixStack m) {
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


  private void drawBox(MatrixStack m, int x, int y, int w, int h) {
    drawHorizontalLine(m, x, x + w, y, COLOR);
    drawHorizontalLine(m, x, x + w, y + h, COLOR);

    drawVerticalLine(m, x, y, y + h, COLOR);
    drawVerticalLine(m, x + w, y, y + h, COLOR);
  }

  private static class PitchIndicatorData {
    public int width;
    public int mid;
    public int margin;
    public int sideWidth;
    public int l1;
    public int l2;
    public int r1;
    public int r2;

    public void update(Dimensions dim) {
      width = i(dim.width / 3);
      int left = width;

      mid = i((width / 2) + left);
      margin = i(width * 0.3d);
      l1 = left + margin;
      l2 = mid - 7;
      sideWidth = l2 - l1;
      r1 = mid + 7;
      r2 = r1 + sideWidth;
    }
  }



  private void drawPitchIndicator(MinecraftClient client, MatrixStack matrixStack) {
    pitchData.update(dim);

    TextRenderer fontRenderer = client.textRenderer;
    int horizonOffset = i(computer.pitch * dim.degreesPerPixel);
    int yHorizon = dim.yMid + horizonOffset;



    for (int i = 20; i <= 90; i = i + 20) {
      int offset = i(dim.degreesPerPixel * i);
      drawDegreeBar(matrixStack, fontRenderer, -i, yHorizon + offset);
      drawDegreeBar(matrixStack, fontRenderer, i, yHorizon - offset);
    }

    pitchData.l1 -= pitchData.margin;
    pitchData.r2 += pitchData.margin;
    drawDegreeBar(matrixStack, fontRenderer, 0, yHorizon);

  }



  private void drawDegreeBar(MatrixStack matrixStack, TextRenderer fontRenderer, int degree,
      int y) {

    int dashes = degree < 0 ? 4 : 1;

    drawHorizontalLineDashed(matrixStack, pitchData.l1, pitchData.l2, y, COLOR, dashes);
    drawHorizontalLineDashed(matrixStack, pitchData.r1, pitchData.r2, y, COLOR, dashes);

    if (degree == 0) {
      int width = i((pitchData.l2 - pitchData.l1) * 0.25d);
      int l1 = pitchData.l2 - width;
      int r2 = pitchData.r1 + width;
      drawHorizontalLineDashed(matrixStack, l1, pitchData.l2, y + 3, COLOR, 3);
      drawHorizontalLineDashed(matrixStack, pitchData.r1, r2, y + 3, COLOR, 3);
      drawHorizontalLineDashed(matrixStack, l1, pitchData.l2, y + 6, COLOR, 3);
      drawHorizontalLineDashed(matrixStack, pitchData.r1, r2, y + 6, COLOR, 3);
      return;
    }

    int sideTickHeight = degree >= 0 ? 5 : -5;
    drawVerticalLine(matrixStack, pitchData.l1, y, y + sideTickHeight, COLOR);
    drawVerticalLine(matrixStack, pitchData.r2, y, y + sideTickHeight, COLOR);

    

    int fontVerticalOffset = degree >= 0 ? 0 : 6;

    fontRenderer.draw(matrixStack, String.format("%d", Math.abs(degree)), pitchData.r2 + 6,
        (float) y - fontVerticalOffset, COLOR);

    fontRenderer.draw(matrixStack, String.format("%d", Math.abs(degree)), pitchData.l1 - 17,
        (float) y - fontVerticalOffset, COLOR);
  }

  private void drawHorizontalLineDashed(MatrixStack m, int x1, int x2, int y, int c,
      int dashCount) {
    int width = x2 - x1;
    int segmentCount = dashCount * 2 - 1;
    double dashSize = width / segmentCount;
    for (int i = 0; i < segmentCount; i++) {
      if (i % 2 != 0) {
        continue;
      }
      int dx1 = i(i * dashSize) + x1;
      int dx2;
      if (i == segmentCount - 1) {
        dx2 = x2;
      } else {
        dx2 = i((i + 1) * dashSize) + x1;
      }
      drawHorizontalLine(m, dx1, dx2, y, c);

      /// draw last tick to end
    }
  }

  private void drawDouble(MatrixStack m, MinecraftClient c, String label, double d, int x, int y) {
    c.textRenderer.draw(m, String.format(label + ": %.2f", d), x, y, COLOR);
  }

  private void drawInt(MatrixStack m, MinecraftClient c, String label, int i, int x, int y) {
    c.textRenderer.draw(m, String.format(label + ": %d", i), x, y, COLOR);
  }

  private void printDebug(MatrixStack m, MinecraftClient c, float partial) {
    int x = 5;
    int y = 5;
    int i = 0;

    // drawDouble(m, c, "FPx", computer.velocity.x, x, y + (10 * i++));
    // drawDouble(m, c, "FPy", computer.velocity.y, x, y + (10 * i++));
    // drawDouble(m, c, "FPz", computer.velocity.z, x, y + (10 * i++));
    
    // Vec3d look = c.player.getRotationVec(partial);
    // drawDouble(m, c, "FPx", look.x, x, y + (10 * i++));
    // drawDouble(m, c, "FPy",look.y, x, y + (10 * i++));
    // drawDouble(m, c, "FPz",look.z, x, y + (10 * i++));

     drawDouble(m, c, " P", computer.pitch, x, y + (10 * i++));
    // drawDouble(m, c, " H", wrapHeading(computer.heading), x, y + (10 * i++));

    // i++;

    drawDouble(m, c, "FP", computer.flightPitch, x, y + (10 * i++));

    i++;
    drawDouble(m, c, "VV", computer.velocity.y, x, y + (10 * i++));
    //drawDouble(m, c, "FH", wrapHeading(computer.flightHeading), x, y + (10 * i++));


    // TextRenderer fontRenderer = client.textRenderer;
    // fontRenderer.draw(matrixStack, String.format("DPP: %.2f", dim.degreesPerPixel), (float) x + 10,
    //     (float) y - 10, COLOR);
    // fontRenderer.draw(matrixStack, String.format("Pitch: %.2f", computer.pitch), (float) x + 10,
    //     (float) y + 0, COLOR);
    // fontRenderer.draw(matrixStack, String.format("Speed: %.2f", computer.speed), (float) x + 10,
    //     (float) y + 10, Color.RED.getRGB());
    // fontRenderer.draw(
    //     matrixStack, String.format("Velocity: %.2f, %.2f, %.2f", computer.velocity.x,
    //         computer.velocity.y, computer.velocity.z),
    //     (float) x + 10, (float) y + 20, Color.RED.getRGB());
    // fontRenderer.draw(matrixStack, String.format("Heading: %.2f", computer.heading), (float) x + 10,
    //     (float) y + 30, Color.RED.getRGB());
    // fontRenderer.draw(matrixStack, String.format("Alt: %.2f", computer.altitude), (float) x + 10,
    //     (float) y + 40, Color.RED.getRGB());
    // if (computer.groundLevel != null) {
    //   fontRenderer.draw(matrixStack, String.format("Ground: %d", computer.groundLevel),
    //       (float) x + 10, (float) y + 50, COLOR);
    //   fontRenderer.draw(matrixStack,
    //       String.format("Dist Ground: %.2f", computer.distanceFromGround), (float) x + 10,
    //       (float) y + 60, COLOR);
    // }
  }


}
